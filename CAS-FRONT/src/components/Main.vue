<script setup lang="ts">
import Map from "./Map.vue";
import {onMounted, ref} from "vue";
import "../map-custom.css";
import MapSelector from "./MapSelector.vue";
import MapController from "./MapController.vue";
import TerrainSelector from "./TerrainSelector.vue";
import TimeSlider from "./TimeSlider.vue";
import SimulationController from "./SimulationController.vue";

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

const paddingZero = (num: number) => {
  let padding = "000";
  return (padding + num).slice(-padding.length);
}

const startItinerary = () => {
  const legendSample = [
    ["1.0", "0.0", "0.0", "1.0", "red"], // red
    ["1.0", "0.5", "0.0", "1.0", "orange"], // orange
    ["1.0", "1.0", "0.0", "1.0", "yellow"], // yellow
    ["0.5", "1.0", "0.0", "1.0", "lime"], // lime
    ["0.0", "1.0", "0.0", "1.0", "green"], // green
    ["0.0", "0.5", "0.5", "1.0", "cyan"], // cyan
    ["0.0", "0.0", "1.0", "1.0", "blue"], // blue
    ["0.5", "0.0", "1.0", "1.0", "purple"], // purple
    ["1.0", "0.0", "1.0", "1.0", "magenta"], // magenta
    ["1.0", "0.0", "0.5", "1.0", "pink"], // pink
  ];

  const magoManager = magoInstance.value.getMagoManager();

  const itineraryPath = "/data/itinerary/";
  const walkingManMosaicTexPath = itineraryPath + "navigation.png";
  const options = {
    magoManager: magoManager,
    walkingManMosaicTexPath: walkingManMosaicTexPath,
    walkingManMosaicColumnsCount: 1,
    walkingManMosaicRowsCount: 1,
    renderThickLine : false,
    samplePointsSize : 0.0
  };

  magoManager.itineraryManager = new Mago3D.ItineraryManager(options);
  const itineraryManager = magoManager.itineraryManager;

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

  let thickness = 2.0;
  //let itineraryPaths = [];

  for (let loop = 0; loop < 10; loop++) {
    let padded = paddingZero(loop+1);

    let filePath = itineraryPath + "USER" + padded + ".json";
    console.log(filePath);
    //itineraryPaths.push(filePath);



    let imagePath = "/data/navigations/navigation.png";
    let layerOptions = {
      filePath: filePath,
      lineThickness: thickness,
      thickLineColor: {
        r: 0.5, g: 0.5, b: 0.5, a: 0.5
      },
      animatedIconFilePath : imagePath,
    };
    itineraryManager.newItineraryLayer(layerOptions);
  }
}

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

const paddedDate = (date: Date) => {
  let dd = String(date.getDate()).padStart(2, '0')
  let mm = String(date.getMonth() + 1).padStart(2, '0')
  let yyyy = String(date.getFullYear()).padStart(4, '0')
  return `${yyyy}-${mm}-${dd}`
}

const paddedTime = (date: Date) => {
  let hh = String(date.getHours()).padStart(2, '0')
  let mm = String(date.getMinutes()).padStart(2, '0')
  let ss = String(date.getSeconds()).padStart(2, '0')
  return `${hh}:${mm}:${ss}`
}

onMounted(async () => {
  console.log('[MainComponent] Mounted Main Component');

  viewer.value = mapComponent.value.getViewer();
  magoInstance.value = mapComponent.value.getMagoInstance();
  transferViewer.value.viewer = viewer.value;
  transferViewer.value.magoInstance = magoInstance.value;

  viewer.value.scene.globe.depthTestAgainstTerrain = true;

  const dateObject = new Date()
  const options = {
    date: '',
    time: '',
    dateObject: dateObject,
    speed: 1000
  };
  options.date = paddedDate(dateObject)
  options.time = paddedTime(dateObject)
  options.dateObject = dateObject

  let today = dateObject
  let tomorrow = new Date(today)
  tomorrow.setDate(tomorrow.getDate() + 1)

  const initPosition = transferViewer.value.initPosition;
  mapController.value.flyTo(initPosition.lon, initPosition.lat, initPosition.height, 0);
  mapSelector.value.toggleOsmLayer();
});

</script>

<template>
  <div class="float-layer left top horizontal">
    <div id="info" class="layer">
      <h2>충청남도 당진시 시곡동 77-5 | 페놀(3,600) 누출</h2>
      <h4>분석데이터 : 2023/10/22 11:00 기준 (1일 0시간 예측)</h4>
    </div>
    <div class="left vertical">
      <SimulationController :transfer-viewer="transferViewer" ref="simulationController"/>
    </div>
  </div>
  <div class="float-layer right top horizontal">

    <MapSelector :transferViewer="transferViewer" ref="mapSelector"/>
    <TerrainSelector :transfer-viewer="transferViewer" ref="terrainSelector"/>
    <div class="vertical right">
      <button @click="startSimulation">3차원 화학사고 데이터 로드</button>
      <button @click="startItinerary">이동동선 데이터 로드</button>
      <button>Function 3</button>
    </div>
    <MapController :transferViewer="transferViewer" ref="mapController"/>
  </div>
  <div class="float-layer right bottom vertical">

  </div>
  <TimeSlider :transfer-viewer="transferViewer" ref="timeSlider"/>
  <Map :init-options="options" ref="mapComponent"/>
</template>

<style scoped>
div#info {

}
div#info h2 {
  font-size: 1.2em;

  margin: 5px;
  padding: 0;
}
div#info h4 {
  font-size: 0.8em;
  margin: 5px;
  padding: 0;
}

div#logo {
  width: 105px;
  padding: 10px;
}
div#logo img {
  width: 75px;
  height: 22px;
  margin: 5px 5px 0 5px;
}
</style>
