<script setup lang="ts">
import Map from "./Map.vue";
import {onMounted, ref} from "vue";

/* @ts-ignore */
const Cesium = window.Cesium;
/* @ts-ignore */
const Mago3D = window.Mago3D;

const mapComponent = ref();

const viewer = ref();
const magoInstance = ref();
const initPosition = {
  lon: 126.978388,
  lat: 37.566610,
  height: 50000,
};

const options = ref({
  infoBox: false,
  navigationHelpButton: false,
  selectionIndicator: false,
  homeButton: false,
  fullscreenButton: false,
  geocoder: false,
  baseLayerPicker: false,
});

const flyTo = (lon: number, lat: number, height: number, duration: number = 2) => {
  viewer.value.camera.flyTo({
    destination: Cesium.Cartesian3.fromDegrees(lon, lat, height),
    duration: duration
  });
}
const toggleOsmLayer = () => {
  mapComponent.value.changeGlobeColor('#edebe5')
  mapComponent.value.changeImageryLayer('https://a.tile.openstreetmap.org');
}
const toggleCartoMapLightLayer = () => {
  mapComponent.value.changeGlobeColor('#d2d8dd')
  mapComponent.value.changeImageryLayer('https://a.basemaps.cartocdn.com/light_all/');
}
const toggleCartoMapDarkLayer = () => {
  mapComponent.value.changeGlobeColor('#090909')
  mapComponent.value.changeImageryLayer('https://a.basemaps.cartocdn.com/dark_all/');
}
const toggleVWorldSatelliteLayer = () => {
  mapComponent.value.changeGlobeColor('#686b61');
  mapComponent.value.changeVWorldImageryLayer('Satellite', 'jpeg');
}
const toggleVWorldBaseLayer = () => {
  mapComponent.value.changeGlobeColor('#686b61');
  mapComponent.value.changeVWorldImageryLayer('Base', 'png');
}
const toggleWorldTerrain = async () => {
  if (!(viewer.value.terrainProvider instanceof Cesium.CesiumTerrainProvider)) {
    viewer.value.terrainProvider = await mapComponent.value.changeWorldTerrain();
  } else {
    viewer.value.terrainProvider = new Cesium.EllipsoidTerrainProvider();
  }
}
const toggleOsmBuildings = async () => {
  const osmBuildingsTileset = await mapComponent.value.getOmsBuildingsTileset();

  const duplicatedTileset = viewer.value.scene.primitives._primitives.find((e) => {
    return e._url === osmBuildingsTileset._url
  })
  if (duplicatedTileset !== undefined) {
    duplicatedTileset.show = !duplicatedTileset.show;
  } else {
    viewer.value.scene.primitives.add(osmBuildingsTileset);
  }
}

onMounted(async () => {
  console.log('[MainComponent] Mounted Main Component');
  viewer.value = mapComponent.value.getViewer();
  magoInstance.value = mapComponent.value.getMagoInstance();

  flyTo(initPosition.lon, initPosition.lat, initPosition.height, 0);

  toggleOsmLayer();
  await toggleOsmBuildings();
  await toggleWorldTerrain();
});

</script>

<template>
  <div class="layer-group left top horizontal">
    <div id="logo">
      <h2>Mago</h2>
    </div>
    <button @click="flyTo(initPosition.lon, initPosition.lat, initPosition.height)">Initial position</button>
  </div>
  <div class="layer-group right top horizontal">
    <button @click="toggleOsmLayer" title="OpenStreetMap Layer">OSM</button>
    <button @click="toggleCartoMapLightLayer" title="CartoMap Light Layer">CartoMap Light</button>
    <button @click="toggleCartoMapDarkLayer" title="CartoMap Dark Layer">CartoMap Dark</button>
    <button @click="toggleVWorldSatelliteLayer" title="VWorld Satellite Layer">VWorld Satellite</button>
    <button @click="toggleVWorldBaseLayer" title="VWorld Base Layer">VWorld Base</button>
  </div>
  <div class="layer-group right bottom vertical">
    <button @click="toggleWorldTerrain" title="Cesium World Terrain">World Terrain</button>
    <button @click="toggleOsmBuildings" title="Cesium OSM Buildings">OSM Buildings</button>
  </div>
  <Map :init-options="options" ref="mapComponent"/>
</template>

<style scoped>
div#logo {
  background-color: #ffffff;
  padding: 10px;
  border-radius: 8px;
  box-shadow: 0 0 3px 3px rgba(0, 0, 0, 0.1);
}

button {
  color: #000000;
  font-size: 11px;
  padding: 5px 15px;
  border-radius: 8px;
  background-color: #ffffff;
  box-shadow: 0 0 3px 3px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.left {
  left: 10px;
}
.left > * {
  margin-right: auto;
}

.right {
  right: 10px;
}
.right > * {
  margin-left: auto;
}

.top {
  top: 10px;
}
.bottom {
  bottom: 10px;
}

.layer-group {
  font-size: 0.8em;
  position: absolute;
  z-index: 10;
  opacity: 0.9;
}

.vertical > * {
  display: inline-block;
  margin-right: 5px;
}
.vertical > *:last-child {
  margin-right: 0;
}

.horizontal > * {
  display: block;
  margin-bottom: 5px;
}
.horizontal > *:last-child {
  margin-bottom: 0;
}
</style>
