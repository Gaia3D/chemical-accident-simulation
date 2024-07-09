<script setup lang="ts">
import Map from "./Map.vue";
import {onMounted, ref} from "vue";

const mapComponent = ref();

const viewer = ref();
const magoInstance = ref();

const options = ref({
  infoBox: false,
  navigationHelpButton: false,
  selectionIndicator: false,
  homeButton: false,
  fullscreenButton: false,
  geocoder: false,
  baseLayerPicker: false,
  vworldToken: 'BB89CEE2-0CBC-3378-A40B-468C4897B788',
});

/* @ts-ignore */
const Cesium = window.Cesium;
/* @ts-ignore */
const Mago3D = window.Mago3D;

const printViewer = () => {
  console.log(viewer.value);
}

const printMagoInstance = () => {
  console.log(magoInstance.value);
}

const flyTo = (lon: number, lat: number, height: number, duration: number = 2) => {
  viewer.value.camera.flyTo({
    destination: Cesium.Cartesian3.fromDegrees(lon, lat, height),
    duration: duration
  });
}

const toggleOsmLayer = () => {
  mapComponent.value.changeGlobeColor('#edebe5')
  mapComponent.value.removeAllImageryLayers();
  mapComponent.value.changeImageryLayer('https://a.tile.openstreetmap.org');
}
const toggleVWorldLayer = () => {
  mapComponent.value.changeGlobeColor('#686b61');
  mapComponent.value.removeAllImageryLayers();
  mapComponent.value.changeVWorldImageryLayer('Satellite', 'jpeg');
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

  //mapComponent.value.changeGlobeColor('#686b61');
  //mapComponent.value.changeImageryLayer('https://a.tile.openstreetmap.org');
  // mapComponent.value.removeAllImageryLayers();
  // mapComponent.value.changeVWorldImageryLayer('Satellite', 'jpeg');


  toggleOsmLayer();
  toggleVWorldLayer();

  flyTo(126.978388, 37.566610, 50000, 0);

  await toggleOsmBuildings();
  await toggleWorldTerrain();

  /*const osmBuildingsTileset = await mapComponent.value.getOmsBuildingsTileset();
  viewer.value.scene.primitives.add(osmBuildingsTileset);

  const cesiumWorldTerrain = await mapComponent.value.changeWorldTerrain();
  viewer.value.terrainProvider = cesiumWorldTerrain;*/
});

</script>

<template>
  <div class="button-group">
    <button @click="printViewer">Print Viewer</button>
    <button @click="printMagoInstance">Print MagoInstance</button>
    <button @click="toggleOsmLayer">Toggle OSM Layer</button>
    <button @click="toggleVWorldLayer">Toggle VWorld Layer</button>
    <button @click="toggleWorldTerrain">Toggle World Terrain</button>
    <button @click="toggleOsmBuildings">Toggle OSM Buildings</button>
  </div>
  <Map :init-options="options" ref="mapComponent"/>
</template>

<style scoped>
.button-group {
  font-size: 0.8em;
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 10;
  opacity: 0.9;
}
.button-group button {
  display: block;
  margin-bottom: 5px;
}
.button-group button:last-child {
  margin-bottom: 0;
}
</style>
