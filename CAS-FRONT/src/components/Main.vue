<script setup lang="ts">
import Map from "./Map.vue";
import {onMounted, ref} from "vue";
import "../map-custom.css";
import MapSelector from "./Map-Selector.vue";
import MapController from "./Map-Controller.vue";
import TerrainSelector from "./Terrain-Selector.vue";

/* @ts-ignore */
const Cesium = window.Cesium;
/* @ts-ignore */
const Mago3D = window.Mago3D;

const transferViewer = ref({
  viewer: undefined,
  initPosition: {
    lon: 126.978388,
    lat: 37.566610,
    height: 50000
  }
});
const mapComponent = ref();
const mapSelector = ref();
const terrainSelector = ref();
const mapController = ref();

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
});

onMounted(async () => {
  console.log('[MainComponent] Mounted Main Component');

  viewer.value = mapComponent.value.getViewer();
  magoInstance.value = mapComponent.value.getMagoInstance();
  transferViewer.value.viewer = viewer.value;

  const initPosition = transferViewer.value.initPosition;
  mapController.value.flyTo(initPosition.lon, initPosition.lat, initPosition.height, 0);
  mapSelector.value.toggleOsmLayer();
});

</script>

<template>
  <div class="float-layer left top horizontal">
    <div class="vertical">
      <div id="logo">
        <img src="../assets/images/mago.png" alt="Mago" title="mago"/>
      </div>
    </div>
    <MapController :transferViewer="transferViewer" ref="mapController"/>
  </div>
  <div class="float-layer right top horizontal">
    <MapSelector :transferViewer="transferViewer" ref="mapSelector"/>
    <TerrainSelector :transfer-viewer="transferViewer" ref="terrainSelector"/>
  </div>
  <div class="float-layer right bottom vertical">
    <div class="horizontal right">
      <button>Function 1</button>
      <button>Function 2</button>
      <button>Function 3</button>
    </div>
  </div>
  <Map :init-options="options" ref="mapComponent"/>
</template>

<style scoped>
div#logo {
  width: 110px;
  background-color: #ffffff;
  padding: 10px;
  border-radius: 8px;
  box-shadow: 0 0 3px 3px rgba(0, 0, 0, 0.1);
}
div#logo img {
  width: 75px;
  height: 22px;
  margin: 5px 5px 0 5px;
}
</style>
