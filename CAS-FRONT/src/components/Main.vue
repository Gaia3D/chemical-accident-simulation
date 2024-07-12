<script setup lang="ts">
import Map from "./Map.vue";
import {onMounted, ref} from "vue";
import "../map-custom.css";
import MapSelector from "./MapSelector.vue";
import MapController from "./MapController.vue";
import TerrainSelector from "./TerrainSelector.vue";
import TimeSlider from "./TimeSlider.vue";

/* @ts-ignore */
const Cesium = window.Cesium;
/* @ts-ignore */
const Mago3D = window.Mago3D;

const transferViewer = ref({
  viewer: undefined,
  initPosition: {
    //lon: 126.978388,
    //lat: 37.566610,
    lon: 126.65403123232736,
    lat: 36.90329299539047,
    height: 20000
  },
  magoInstance: undefined
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

const startSimulation = () => {
  const magoManager = magoInstance.value.getMagoManager();
  magoManager.interactionCollection.array[0].setActive(true);
  magoManager.interactionCollection.array[0].setTargetType(Mago3D.DataType.NATIVE);
  magoManager.interactionCollection.array[1].setActive(true);
  magoManager.interactionCollection.array[1].setTargetType(Mago3D.DataType.NATIVE);

  const path = "/data/chemicalAccident/output_chemicalAccident";
  const jsonPath = path + "/JsonIndex.json";

  if (!magoManager.chemicalAccidentManager) {
    let options = {
      magoManager : magoManager,
      geoJsonIndexFileFolderPath : path
    };
    magoManager.chemicalAccidentManager = new Mago3D.ChemicalAccidentManager(options);
    magoManager.chemicalAccidentManager.load_chemicalAccidentIndexFile(jsonPath);
    magoManager.chemicalAccidentManager._animationState = Mago3D.CODE.processState.STARTED;

    let timeOptions = {
      timeScale : 100,
      year : 2024,
      month : 2,
      day : 6,
      hour : 11,
      minute : 0,
      second : 0
    };
    if (magoManager.animationTimeController === undefined) {
      magoManager.animationTimeController = new Mago3D.AnimationTimeController(timeOptions);
    }
    magoManager.animationTimeController.reset(timeOptions);
    magoManager.animationTimeController.pauseAnimation();
    console.log('[MainComponent] Start Simulation');
  }
}

onMounted(async () => {
  console.log('[MainComponent] Mounted Main Component');

  viewer.value = mapComponent.value.getViewer();
  magoInstance.value = mapComponent.value.getMagoInstance();
  transferViewer.value.viewer = viewer.value;
  transferViewer.value.magoInstance = magoInstance.value;

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
    <div class="vertical right">
      <button @click="startSimulation">Function 1</button>
      <button>Function 2</button>
      <button>Function 3</button>
    </div>
  </div>
  <div class="float-layer right bottom vertical">

  </div>
  <TimeSlider :transfer-viewer="transferViewer" ref="timeSlider"/>
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
