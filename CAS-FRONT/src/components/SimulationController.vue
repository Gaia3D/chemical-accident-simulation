<script setup lang="ts">
import {onMounted, ref} from "vue";
import "../map-custom.css";
import {toggleDangjinTerrain} from "../features/terrainController.ts";
import {toggleDangjinBuildings} from "../features/buildingTilesetController.ts";
import {toggleRadius} from "../features/radiusController.ts";

/* @ts-ignore */
const Cesium = window.Cesium;
/* @ts-ignore */
const Mago3D = window.Mago3D;

const props = defineProps<{
  transferViewer: any;
}>();


onMounted(async () => {
  console.log('[SimulationController] Mounted Slider Component');
});

const toggleShadow = () => {
  const viewer = getViewer();
  const scene = viewer.scene;
  const shadowMap = scene.shadowMap;
  if (shadowMap.enabled) {
    viewer.scene.globe.enableLighting = false;
    shadowMap.enabled = false
  } else {
    viewer.scene.globe.enableLighting = true;
    shadowMap.enabled = true
    shadowMap.darkness = 0.5
    //shadowMap.softShadows = true
    //shadowMap.normalOffset = false
    //shadowMap.fadingEnabled = false
    //shadowMap.size = 4096
  }
}

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

  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();

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

  for (let loop = 0; loop < 1; loop++) {
    let padded = paddingZero(loop+1);
    let filePath = itineraryPath + "USER" + padded + ".json";
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
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
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

const getViewer = () => {
  return props.transferViewer.viewer;
}

</script>

<template>
  <div id="simulation-layer" class="layer left top horizontal">
    <h3>사고물질 레이어</h3>
    <div class="switch-wrapper">
      <h4>사고물질 농도</h4>
      <label>
        <input type="radio" name="accident-group" @change="startSimulation()">
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
    <h3>예상피해자 레이어</h3>
    <div class="switch-wrapper">
      <h4>예상 피해자 분포</h4>
      <label>
        <input type="radio" name="victim-group">
        <span></span>
      </label>
    </div>
    <div class="switch-wrapper">
      <h4>예상 피해자 이동동선</h4>
      <label>
        <input type="radio" name="victim-group" @change="startItinerary()">
        <span></span>
      </label>
    </div>

    <h3>기타 레이어</h3>
    <div class="switch-wrapper">
      <h4>건물 레이어</h4>
      <label>
        <input type="checkbox" name="victim-group" @change="toggleDangjinBuildings(getViewer())">
        <span></span>
      </label>
    </div>
    <div class="switch-wrapper">
      <h4>지형 레이어</h4>
      <label>
        <input type="checkbox" name="victim-group" @change="toggleDangjinTerrain(getViewer())">
        <span></span>
      </label>
    </div>
    <div class="switch-wrapper">
      <h4>반경 레이어(동심원)</h4>
      <label>
        <input type="checkbox" name="victim-group" @change="toggleRadius(getViewer())">
        <span></span>
      </label>
    </div>
    <div class="switch-wrapper">
      <h4>그림자 효과</h4>
      <label>
        <input type="checkbox" name="victim-group" @change="toggleShadow()">
        <span></span>
      </label>
    </div>
  </div>
  <div id="legend-layer" class="layer left top horizontal">
    <h3>범례</h3>
    <div class="legend">

    </div>
  </div>
</template>

<style scoped>

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

div#legend-layer {
  width: 100px;
  display: inline-block;
}
div#legend-layer h3 {
  font-size: 1.0em;
  margin: 5px;
  padding: 0;
}

div.legend {
  height: 100px;
  width: 20px;
  background: linear-gradient(red, yellow, green, blue);
  border: 1px solid #464646;
  display: inline-block;
  margin: 5px;
}
</style>
