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

const isLoading = ref(true);


onMounted(async () => {
  console.log('[SimulationController] Mounted Slider Component');
  setTimeout(() => {
    toggleRadius(getViewer());
    loadSimulations();
  }, 2000);
});

/*const toggleShadow = () => {
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
  }
}*/

const trackEntities : any[] = [];

const layerState = ref({
  layer: true,
  legend: true
})

const toggleLayer = () => {
  layerState.value.layer = !layerState.value.layer;
}

const toggleLegend = () => {
  layerState.value.legend = !layerState.value.legend;
}

const paddingZero = (num: number) => {
  let padding = "000";
  return (padding + num).slice(-padding.length);
}

const loadItinerary = () => {
  const viewer = getViewer();
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
  let thickness = 1.5;
  for (let loop = 0; loop < 100; loop++) {
    let padded = paddingZero(loop + 1);
    let filePath = itineraryPath + "USER" + padded + ".json";
    let imagePath = "/data/navigations/navigation.png";
    let layerOptions = {
      filePath: filePath,
      lineThickness: thickness,
      thickLineColor: {
        r: 0.3, g: 0.3, b: 0.3, a: 0.5
      },
      animatedIconFilePath : imagePath,
    };

    fetch(filePath, {method: "GET"}).then((response) => {
      //console.log("response", response);
      return response.json()
    }).then((json) => {
      //console.log("json", json);

      const centerGeographicCoord = json.centerGeographicCoord;
      const localPositions = json.localPositions;

      const worldCoord = Cesium.Cartesian3.fromDegrees(centerGeographicCoord.longitude, centerGeographicCoord.latitude, centerGeographicCoord.altitude);
      //const cartographic = Cesium.Cartographic.fromCartesian(worldCoord);
      const modelMatrix = Cesium.Transforms.eastNorthUpToFixedFrame(worldCoord);

      //console.log("worldCoord", worldCoord);
      //console.log("cartographic", cartographic);

      const polylinePositions : any = [];

      for (let index = 0; index < localPositions.length; index+=3) {
        let x = localPositions[index];
        let y = localPositions[index + 1];
        //let z = localPositions[index + 2];
        let z = 5;

        const localCartesian = new Cesium.Cartesian3(x, y, z);

        const translateMatrix = Cesium.Matrix4.fromTranslation(localCartesian, new Cesium.Matrix4());

        const translatedMatrix = Cesium.Matrix4.multiply(modelMatrix, translateMatrix, new Cesium.Matrix4());
        const worldPosition = Cesium.Matrix4.getTranslation(translatedMatrix, new Cesium.Cartesian3());

        polylinePositions.push(worldPosition);
      }


      const polylineEntitiy = viewer.entities.add({
        name: "Blue dashed line",
        polyline: {
          positions: polylinePositions,
          width: 1,
          material: Cesium.Color.LIGHTGREY,
          clampToGround : true,
        },
        show: false
      });

      trackEntities.push(polylineEntitiy);
    });

    itineraryManager.newItineraryLayer(layerOptions);
    itineraryManager.hide();
  }
}

const loadSimulations = () => {
  getAnimationTimeController();
  load3dSimulation();
  load2dSimulation();
  loadItinerary();
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();

  isLoading.value = true;
  const interval = setInterval(() => {
    if (magoManager.chemicalAccidentManager.isReady() && magoManager.chemicalAccident2dManager.isReady()) {
      clearInterval(interval);
      isLoading.value = false;
    } else {
      console.log("로드 중...");
    }
  }, 1000);
}

const load3dSimulation = () => {
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
      renderingColorType : 2,
      url : jsonPath,
      //geoJsonIndexFileFolderPath : path
    };
    magoManager.chemicalAccidentManager = new Mago3D.ChemicalAccidentManager(options);
    magoManager.chemicalAccidentManager.load_chemicalAccidentIndexFile(jsonPath);
    magoManager.chemicalAccidentManager._animationState = Mago3D.CODE.processState.STARTED;

    magoManager.chemicalAccidentManager.setLegendColors(get3DLegendColors());
    magoManager.chemicalAccidentManager.setLegendValuesScale(1e10);
    magoManager.chemicalAccidentManager.hide();
  }
}


const load2dSimulation = () => {
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
  const path = "/data/chemicalAccident/output_chemicalAccident_2d";
  const jsonPath = path + "/JsonIndex2D.json";
  if (!magoManager.chemicalAccident2dManager) {
    let options = {
      magoManager : magoManager,
      url : jsonPath,
      renderingColorType : 2,
      renderBorder : true,
      textureFilterType : 1, // 0 = NEAREST, 1 = LINEAR
      //geoJsonIndexFileFolderPath : path,
      //geoJsonIndexFilePath : jsonPath,
    };
    magoManager.chemicalAccident2dManager = new Mago3D.ChemicalAccident2DManager(options);
    magoManager.chemicalAccident2dManager._animationState = Mago3D.CODE.processState.STARTED;

    magoManager.chemicalAccident2dManager.setLegendColors(getLegendColors());
    magoManager.chemicalAccident2dManager.hide();
  }
}

const getLogDivisions = (minValue : number, maxValue : number, numberOfColors : number, accentuationFactor : number) => {
  const logMin = Math.log(minValue + 1); // Evitar log(0)
  const logMax = Math.log(maxValue + 1);
  const logRange = logMax - logMin;
  const divisions = [];

  for (let i = 0; i <= numberOfColors; i++) {
    const normalizedValue = i / numberOfColors;
    const accentuatedValue = Math.pow(normalizedValue, accentuationFactor);
    const logValue = logMin + accentuatedValue * logRange;
    const value = Math.exp(logValue) - 1;
    divisions.push(value);
  }

  return divisions;
}

const get3DLegendColors = () => {
  const legendValuesScale = 1e10;
  const minValue = 1e-17 * legendValuesScale;
  const maxValue = 0.648 * legendValuesScale;
  const numColors = 12;
  const accentuationFactor = 1.0;
  const legendValues = getLogDivisions(minValue, maxValue, numColors, accentuationFactor);
  const accentuationFactorAlpha = 2.5;
  const alphaValues = getLogDivisions(0.0, 0.99, numColors, accentuationFactorAlpha);

  const legendColors = [];
  let LegendColor = new Mago3D.ColorLegend(0/255, 0/255, 143/255, 0.0, legendValues[0]);  // 0
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 15/255, 255/255, alphaValues[1], legendValues[1]);  // 1
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 95/255, 255/255, alphaValues[2], legendValues[2]);  // 2
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 175/255, 255/255, alphaValues[3], legendValues[3]);  // 3
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 255/255, 255/255, alphaValues[4], legendValues[4]);  // 4
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(79/255, 255/255, 175/255, alphaValues[5], legendValues[5]);  // 5
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(159/255, 255/255, 95/255, alphaValues[6], legendValues[6]);  // 6
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(239/255, 255/255, 15/255, alphaValues[7], legendValues[7]);  // 7
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 191/255, 0/255, alphaValues[8], legendValues[8]);  // 8
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 111/255, 0/255, alphaValues[9], legendValues[9]);  // 9
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 31/255, 0/255, alphaValues[10], legendValues[10]);  // 10
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(207/255, 0/255, 0/255, alphaValues[11], legendValues[11]);  // 11
  legendColors.push(LegendColor);

  return legendColors;
}

const getLegendColors = () => {
  const legendValuesScale = 1e10;
  const minValue = 1e-17 * legendValuesScale;
  const maxValue = 0.648 * legendValuesScale;
  const numColors = 12;
  const accentuationFactor = 1.0;
  const legendValues = getLogDivisions(minValue, maxValue, numColors, accentuationFactor);

  // create legend colors.***
  const legendColors = [];

  let LegendColor = new Mago3D.ColorLegend(0/255, 0/255, 143/255, 128/255, legendValues[0]);  // 0
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 15/255, 255/255, 128/255, legendValues[1]);  // 1
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 95/255, 255/255, 128/255, legendValues[2]);  // 2
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 175/255, 255/255, 128/255, legendValues[3]);  // 3
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 255/255, 255/255, 128/255, legendValues[4]);  // 4
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(79/255, 255/255, 175/255, 128/255, legendValues[5]);  // 5
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(159/255, 255/255, 95/255, 128/255, legendValues[6]);  // 6
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(239/255, 255/255, 15/255, 128/255, legendValues[7]);  // 7
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 191/255, 0/255, 128/255, legendValues[8]);  // 8
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 111/255, 0/255, 128/255, legendValues[9]);  // 9
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 31/255, 0/255, 128/255, legendValues[10]);  // 10
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(207/255, 0/255, 0/255, 128/255, legendValues[11]);  // 11
  legendColors.push(LegendColor);

  return legendColors;
}

const getLegendColorsB = () => {
  const legendValuesScale = 1e10;
  const minValue = 1e-17 * legendValuesScale;
  const maxValue = 0.648 * legendValuesScale;
  const numColors = 10;
  const accentuationFactor = 1.0;
  const legendValues = getLogDivisions(minValue, maxValue, numColors, accentuationFactor);

  // create legend colors.***
  const legendColors = [];

  //let alpha = 128/255;
  let LegendColor = new Mago3D.ColorLegend(128/255, 128/255, 128/255, 0.1, legendValues[0]);  // 0
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 0/255, 0/255, 0.1, legendValues[1]);  // 1
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 0/255, 0/255, 0.2, legendValues[2]);  // 2
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 0/255, 0/255, 0.3, legendValues[3]);  // 3
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 0/255, 0/255, 0.4, legendValues[4]);  // 4
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 0/255, 0/255, 0.5, legendValues[5]);  // 5
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 0/255, 0/255, 0.6, legendValues[6]);  // 6
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 0/255, 0/255, 0.7, legendValues[7]);  // 7
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 0/255, 0/255, 0.8, legendValues[8]);  // 8
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 0/255, 0/255, 0.9, legendValues[9]);  // 9
  legendColors.push(LegendColor);

  return legendColors;
}

const getLegendColorsC = () => {
  const legendValuesScale = 1e10;
  const minValue = 1e-17 * legendValuesScale;
  const maxValue = 0.648 * legendValuesScale;
  const numColors = 10;
  const accentuationFactor = 1.0;
  const legendValues = getLogDivisions(minValue, maxValue, numColors, accentuationFactor);

  // create legend colors.***
  const legendColors = [];

  //let alpha = 128/255;
  let LegendColor = new Mago3D.ColorLegend(128/255, 128/255, 128/255, 0.1, legendValues[0]);  // 0
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 255/255, 255/255, 0.1, legendValues[1]);  // 1
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 255/255, 255/255, 0.2, legendValues[2]);  // 2
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 255/255, 255/255, 0.3, legendValues[3]);  // 3
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 255/255, 255/255, 0.4, legendValues[4]);  // 4
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 255/255, 255/255, 0.5, legendValues[5]);  // 5
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 255/255, 255/255, 0.6, legendValues[6]);  // 6
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 255/255, 255/255, 0.7, legendValues[7]);  // 7
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 255/255, 255/255, 0.8, legendValues[8]);  // 8
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 255/255, 255/255, 0.9, legendValues[9]);  // 9
  legendColors.push(LegendColor);
  return legendColors;
}

const startChemicalAccidentConcentration = () => {
  // 농도
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
  stopAllChemicalAccident();
  if (magoManager.chemicalAccidentManager) {
    magoManager.chemicalAccidentManager.show();
    magoManager.chemicalAccidentManager.setLegendValuesScale(1e10);

    let chemicalAccidentLayer = magoManager.chemicalAccidentManager.getChemicalAccidentLayer(0);
    if (!chemicalAccidentLayer) {
      alert("레이어가 준비되지 않았습니다.");
      return;
    }
    chemicalAccidentLayer.setUseMinMaxValuesToRender(0)
  }
}

const startChemicalAccidentExposure = () => {
  // 노출량
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
  stopAllChemicalAccident();
  if (magoManager.chemicalAccident2dManager) {
    magoManager.chemicalAccident2dManager.setLegendColors(getLegendColors());
    magoManager.chemicalAccident2dManager.setLegendValuesScale(1e10);
    magoManager.chemicalAccident2dManager.show();

    const firstLayer = magoManager.chemicalAccident2dManager.getChemicalAccident2DLayer(0);
    firstLayer.setLegendColors(getLegendColors());
    firstLayer.setLegendValuesScale(1e10);

  }
}

const startChemicalAccidentAcuteHazard = () => {
  // 급성 위해도
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
  stopAllChemicalAccident();
  if (magoManager.chemicalAccident2dManager) {
    magoManager.chemicalAccident2dManager.setLegendColors(getLegendColorsB());
    magoManager.chemicalAccident2dManager.setLegendValuesScale(1e10);
    magoManager.chemicalAccident2dManager.show();
  }
}

const stopAllChemicalAccident = () => {
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
  if (magoManager.chemicalAccident2dManager) {
    magoManager.chemicalAccident2dManager.hide();
  }
  if (magoManager.chemicalAccidentManager) {
    magoManager.chemicalAccidentManager.hide();
  }
}


const startVictimDistribution = () => {
  // 피해자 분포
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
  stopAllVictim();
  if (magoManager.chemicalAccident2dManager) {
    magoManager.chemicalAccident2dManager.show();
    magoManager.chemicalAccident2dManager.setLegendColors(getLegendColorsC());
    magoManager.chemicalAccident2dManager.setLegendValuesScale(1e10);
  }
}

const startVictimMovement = () => {
  // 피해자 이동
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
  stopAllVictim();
  if (magoManager.itineraryManager) {
    magoManager.itineraryManager.show();
    trackEntities.forEach((entity) => {
      entity.show = true;
    });
  }
}

const stopAllVictim = () => {
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
  if (magoManager.itineraryManager) {
    magoManager.itineraryManager.hide();
    trackEntities.forEach((entity) => {
      entity.show = false;
    });
  }
  /*if (magoManager.chemicalAccident2dManager) {
    magoManager.chemicalAccident2dManager.hide();
  }*/
}


const getAnimationTimeController = () => {
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
  if (magoManager.animationTimeController === undefined) {
    let timeOptions = {
      timeScale : 600000,
      year : 2024,
      month : 2,
      day : 6,
      hour : 11,
      minute : 0,
      second : 0
    };
    magoManager.animationTimeController = new Mago3D.AnimationTimeController(timeOptions);
    magoManager.animationTimeController.pauseAnimation();
  }
  return magoManager.animationTimeController;
}

const getViewer = () => {
  return props.transferViewer.viewer;
}

</script>

<template>
  <div id="simulation-layer" class="layer left top horizontal">
    <h1>
      레이어
      <button class="close" @click="toggleLayer()"><img class="icon" src="/src/assets/images/icons/minus.png"></button>
    </h1>
    <div class="layer-contents horizontal" v-show="layerState.layer">
      <div class="loading" v-show="isLoading">
        <span>데이터 로드 중</span>
      </div>
      <h3>사고물질 레이어</h3>
      <div class="switch-wrapper">
        <h4>사고물질 농도 (3D)</h4>
        <label>
          <input type="radio" name="accident-group" @change="startChemicalAccidentConcentration()">
          <span></span>
        </label>
      </div>
      <div class="switch-wrapper">
        <h4>사고물질 농도 (지면)</h4>
        <label>
          <input type="radio" name="accident-group" @change="startChemicalAccidentConcentration()">
          <span></span>
        </label>
      </div>
      <div class="switch-wrapper">
        <h4>사고물질 급성위해도</h4>
        <h4>(위치정보 미확인 피해등급)</h4>
        <label>
          <input type="radio" name="accident-group" @change="startChemicalAccidentAcuteHazard()">
          <span></span>
        </label>
      </div>
      <div class="line"></div>
      <h3>예상피해자 레이어</h3>
      <div class="switch-wrapper">
        <h4>예상 피해자 분포</h4>
        <label>
          <input type="radio" name="victim-group" @change="startVictimDistribution()">
          <span></span>
        </label>
      </div>
      <div class="switch-wrapper">
        <h4>예상 피해자 이동동선</h4>
        <label>
          <input type="radio" name="victim-group" @change="startVictimMovement()">
          <span></span>
        </label>
      </div>
      <div class="switch-wrapper">
        <h4>개인별 상세 동선</h4>
        <label>
          <input type="radio" name="victim-group" @change="startVictimMovement()">
          <span></span>
        </label>
      </div>
      <div class="line"></div>
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
          <input type="checkbox" name="victim-group" @change="toggleRadius(getViewer())" checked>
          <span></span>
        </label>
      </div>
<!--      <div class="switch-wrapper">
        <h4>그림자 효과</h4>
        <label>
          <input type="checkbox" name="victim-group" @change="">
          <span></span>
        </label>
      </div>-->
    </div>
  </div>
  <div id="legend-layer" class="layer left top horizontal">
    <h1>
      범례
      <button class="close" @click="toggleLegend()"><img class="icon" src="/src/assets/images/icons/minus.png"></button>
    </h1>
    <div class="layer-contents horizontal" v-show="layerState.legend">
      <h3>페놀 농도</h3>
      <div class="legend-wrap">
        <div class="legend"></div>
        <div class="datalist-wrap">
          <datalist>
            <option value="0">0.03 ~ </option>
            <option value="1">0.006 ~ 0.001</option>
            <option value="2">0.001 ~ 0.005</option>
            <option value="3">~ 0.001</option>
          </datalist>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

h1 {
  background-color: #043960;
  font-size: 13px;
  padding: 10px 10px;
  margin: -8px;
  color: white;
}

h1 button {
  float: right;
  background: none;
  border: none;
  cursor: pointer;
  position: absolute;
  right: 10px;
  padding: 0;
  width: 16px;
  height: 16px;
}

h1 button img {
  width: 100%;
  height: 100%;
  filter: brightness(0) saturate(100%) invert(100%) sepia(0%) saturate(0%) hue-rotate(74deg) brightness(104%) contrast(103%);
}

div.loading {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  font-size: 1.5em;
  text-align: center;
  line-height: 100px;
  z-index: 10;
}
div.loading span {
  position: absolute;
  left: 50%;
  top: 50%;
  font-size: 12px;
  translate: -50% -50%;
  line-height: 14px;
}

div#simulation-layer {
  width: 225px;
  display: inline-block;
  position: relative;
  overflow: hidden;
  left: 0;
  top: 0;
}
div#simulation-layer h3 {
  font-size: 1.0em;
  margin: 8px 5px;
  padding: 0;
}

.switch-wrapper {
  display: inline-block;
  width: 100%
}
.switch-wrapper > h4 {
  display: inline-block;
  text-align: left;
  vertical-align: middle;
  font-size: 0.9em;
  font-weight: normal;
  margin: 0 5px;
  width: 160px;
  line-height: normal;
  word-break: keep-all;

}

div#legend-layer {
  min-width: 140px;
  display: inline-block;
  overflow: hidden;
}
div#legend-layer h3 {
  font-size: 1.0em;
  margin: 5px;
  padding: 0;
}

div.legend-wrap {
  display: inline-block;
  vertical-align: middle;
  padding: 10px 10px;
}

div.datalist-wrap {
  display: inline-block;
  vertical-align: middle;
}
div.datalist-wrap datalist {
  height: 100px;
  display: grid;
  grid-auto-flow: dense;
  position: relative;
  margin-left: 5px;
}

div.datalist-wrap datalist > option {
  margin-top: -0.5em;
  font-size: 0.8em;
  margin-left: 5px;
}

div.datalist-wrap datalist > option:last-child {
  position: absolute;
  bottom: 0;
  margin-bottom: -0.5em;
}



div.legend {
  height: 100px;
  width: 20px;
  background: linear-gradient(red, yellow, green, blue);
  border: 1px solid #464646;
  display: inline-block;
  margin: 0px;
  vertical-align: middle;
}
</style>
