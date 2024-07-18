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
  viewer.value.scene.globe.enableLighting = true;


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

  /*let start = Cesium.JulianDate.fromIso8601(today.toISOString())
  let stop = Cesium.JulianDate.fromIso8601(tomorrow.toISOString())
  let clock = viewer.value.clock
  clock.startTime = start
  clock.stopTime = stop
  clock.currentTime = start
  clock.clockRange = Cesium.ClockRange.UNBOUNDED
  clock.multiplier = options.speed
*/
  const range = 1000
  const innerFactor = 0.3;
  const outterFactor = 0.5;
  viewer.value.entities.add({
    name: "Dome",
    position: Cesium.Cartesian3.fromDegrees(126.65403123232736, 36.90329299539047),
    ellipsoid: {
      radii: new Cesium.Cartesian3(range, range, range),
      maximumCone: Cesium.Math.PI_OVER_TWO,
      material: Cesium.Color.RED.withAlpha(innerFactor),
      outline: true,
      outlineColor: Cesium.Color.RED.withAlpha(outterFactor),
    },
  });

  viewer.value.entities.add({
    name: "Dome",
    position: Cesium.Cartesian3.fromDegrees(126.65403123232736, 36.90329299539047),
    ellipsoid: {
      radii: new Cesium.Cartesian3(range * 2, range * 2, range * 2),
      maximumCone: Cesium.Math.PI_OVER_TWO,
      material: Cesium.Color.DARKORANGE.withAlpha(innerFactor),
      outline: true,
      outlineColor: Cesium.Color.DARKORANGE.withAlpha(outterFactor),
    },
  });

  viewer.value.entities.add({
    name: "Dome",
    position: Cesium.Cartesian3.fromDegrees(126.65403123232736, 36.90329299539047),
    ellipsoid: {
      radii: new Cesium.Cartesian3(range * 3, range * 3, range * 3),
      maximumCone: Cesium.Math.PI_OVER_TWO,
      material: Cesium.Color.GREEN.withAlpha(innerFactor),
      outline: true,
      outlineColor: Cesium.Color.GREEN.withAlpha(outterFactor),
    },
  });

  /*const scene = viewer.scene
  const layers = scene.imageryLayers
  const cesiumLogo = Cesium.ImageryLayer.fromProviderAsync(
      Cesium.SingleTileImageryProvider.fromUrl(
          "/src/assets/images/mago.png",
          {
            rectangle: Cesium.Rectangle.fromDegrees(
                -75.0,
                28.0,
                -67.0,
                29.75
            ),
          }
      )
  );
  layers.add(cesiumLogo);*/

  viewer.value.scene.shadowMap.enabled = true
  viewer.value.scene.shadowMap.darkness = 0.5

  const initPosition = transferViewer.value.initPosition;
  mapController.value.flyTo(initPosition.lon, initPosition.lat, initPosition.height, 0);
  mapSelector.value.toggleOsmLayer();
});

</script>

<template>
  <div class="float-layer left top horizontal">
    <!--    <div id="logo" class="layer">
      <img src="../assets/images/mago.png" alt="Mago" title="mago"/>
    </div>-->
    <div id="info" class="layer">
      <h2>충청남도 당진시 시곡동 77-5 | 페놀(3,600) 누출</h2>
      <h4>분석데이터 : 2023/10/22 11:00 기준 (1일 0시간 예측)</h4>
    </div>
    <div class="left vertical">
      <div id="simulation-layer" class="layer left top horizontal">
        <h3>사고물질 레이어</h3>

        <div class="switch-wrapper">
          <h4>사고물질 농도</h4>
          <label>
            <input type="radio" name="accident-group" checked>
            <span></span>
          </label>
        </div>
        <div class="switch-wrapper">
          <h4>사고물질 노출량</h4>
          <label>
            <input type="radio" name="accident-group">
            <span></span>
          </label>
        </div>
        <div class="switch-wrapper">
          <h4>사고물질 급성위해도</h4>
          <label>
            <input type="radio" name="accident-group">
            <span></span>
          </label>
        </div>
<!--        <div class="switch-wraper">
          <label for="switch" class="switch_label">
            <input type="radio" class="switch" name="group">
            <span class="onf_btn"></span>
          </label>
        </div>-->
<!--        <button @click="startSimulation">사고물질 농도</button>
        <button @click="startSimulation">사고물질 노출량</button>
        <button @click="startSimulation">사고물질 급성위해도</button>-->
        <h3>예상피해자 레이어</h3>
        <div class="switch-wrapper">
          <h4>예상 피해자 분포</h4>
          <label>
            <input type="radio" name="victim-group" checked>
            <span></span>
          </label>
        </div>
        <div class="switch-wrapper">
          <h4>예상 피해자 이동동선</h4>
          <label>
            <input type="radio" name="victim-group">
            <span></span>
          </label>
        </div>

        <h3>기타 레이어</h3>
        <div class="switch-wrapper">
          <h4>건물 레이어</h4>
          <label>
            <input type="checkbox" name="victim-group" checked>
            <span></span>
          </label>
        </div>
        <div class="switch-wrapper">
          <h4>지형 레이어</h4>
          <label>
            <input type="checkbox" name="victim-group">
            <span></span>
          </label>
        </div>
        <div class="switch-wrapper">
          <h4>반경 레이어(동심원)</h4>
          <label>
            <input type="checkbox" name="victim-group">
            <span></span>
          </label>
        </div>
<!--        <button @click="startSimulation">예상 피해자 분포</button>
        <button @click="startSimulation">예상 피해자 이동동선</button>-->
      </div>
      <div id="legend-layer" class="layer left top horizontal">
        <h3>범례</h3>
      </div>
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
.switch-wrapper {
  display: inline-block;
  width: 185px;
}
.switch-wrapper > h4 {
  min-width: 135px;
  display: inline-block;
  text-align: left;
  vertical-align: middle;
  font-size: 0.9em;
  font-weight: normal;
  margin: 0 5px;
}

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

div#simulation-layer {
  width: 200px;
  display: inline-block;
  padding-bottom: 12px;
}
div#simulation-layer h3 {
  font-size: 1.0em;
  margin: 8px 5px;
  padding: 0;
}

div#legend-layer {
  width: 130px;
  display: inline-block;
}
div#legend-layer h3 {
  font-size: 1.0em;
  margin: 5px;
  padding: 0;
}
</style>
