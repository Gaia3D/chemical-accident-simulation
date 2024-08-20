<script setup lang="ts">
import {onMounted, ref} from "vue";
import "../map-custom.css";
import {toggleDangjinTerrain} from "../features/terrainController.ts";
import {toggleDangjinBuildings} from "../features/buildingTilesetController.ts";
import {toggleRadius} from "../features/radiusController.ts";
import { store } from "../store/store";

/* @ts-ignore */
const Cesium = window.Cesium;
/* @ts-ignore */
const Mago3D = window.Mago3D;

const props = defineProps<{
  transferViewer: any;
}>();

onMounted(async () => {
  console.log('[SimulationController] Mounted Slider Component');
  store.showLoading();
  setTimeout(() => {
    toggleRadius(getViewer());
    loadSimulations();
  }, 2000);
});

const legendList : any = ref({
  title : "농도 범례",
  list : [],
  cssLinearGradient : "linear-gradient(red, yellow, green)"
})

const singleTrackEntity : any = ref(undefined);
const trackEntities : any[] = [];

const layerState : any = ref({
  layer: true,
  legend: true,
  chemicalAccident3d: false,
  chemicalAccident2d: false,
  accidentAcuteHazard: false,
  victimDistribution: false,
  victimMovement: false,
  personalMovement: false,
  selectedAccident: undefined,
  selectedVictim: undefined,
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

const loadItinerary = async () => {
  const viewer = getViewer();
  const itineraryPath = "/data/itinerary/";

  for (let loop = 0; loop < 100; loop++) {
    let padded = paddingZero(loop + 1);
    let userId = "USER" + padded;
    let filePath = itineraryPath + userId + ".json";
    await fetch(filePath, {method: "GET"}).then((response) => {
      return response.json()
    }).then((json) => {
      const centerGeographicCoord = json.centerGeographicCoord;
      const localPositions = json.localPositions;

      const worldCoord = Cesium.Cartesian3.fromDegrees(centerGeographicCoord.longitude, centerGeographicCoord.latitude, centerGeographicCoord.altitude);
      const modelMatrix = Cesium.Transforms.eastNorthUpToFixedFrame(worldCoord);

      const positions = [];
      const cartographicDegrees:any = [];

      let increement = 0;
      for (let index = 0; index < localPositions.length; index+=3) {
        let x = localPositions[index];
        let y = localPositions[index + 1];
        let z = 5;

        const localCartesian = new Cesium.Cartesian3(x, y, z);
        const translateMatrix = Cesium.Matrix4.fromTranslation(localCartesian, new Cesium.Matrix4());
        const translatedMatrix = Cesium.Matrix4.multiply(modelMatrix, translateMatrix, new Cesium.Matrix4());
        const worldPosition = Cesium.Matrix4.getTranslation(translatedMatrix, new Cesium.Cartesian3());

        positions.push(worldPosition);

        const cartographic = Cesium.Cartographic.fromCartesian(worldPosition);

        const lon = Cesium.Math.toDegrees(cartographic.longitude);
        const lat = Cesium.Math.toDegrees(cartographic.latitude);

        cartographicDegrees.push(increement += (60 * 10));
        cartographicDegrees.push(lon);
        cartographicDegrees.push(lat);
        cartographicDegrees.push(0);
      }

      const lineEntity = viewer.entities.add({
        id: userId,
        polyline: {
          positions: positions,
          width: 1,
          material: Cesium.Color.GREY,
        },
        show: false,
      });

      trackEntities.push(lineEntity);
    });
  }
}

const loadSimulations = () => {
  getAnimationTimeController();
  load3dSimulation();
  load2dSimulation();
  loadItinerary();
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();

  const interval = setInterval(() => {
    if (magoManager.chemicalAccidentManager.isReady() && magoManager.chemicalAccident2dManager.isReady()) {
      clearInterval(interval);
      store.hideLoading();
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

  //output_chemAcc_20240805
  //const path = "/data/chemicalAccident/output_chemicalAccident";
  const path = "/data/chemicalAccident/output_chemAcc_20240805";
  const jsonPath = path + "/JsonIndex.json";

  if (!magoManager.chemicalAccidentManager) {
    let options = {
      magoManager : magoManager,
      renderingColorType : 2,
      url : jsonPath,
      textureFlipYAxis : true
      //geoJsonIndexFileFolderPath : path
    };
    magoManager.chemicalAccidentManager = new Mago3D.ChemicalAccidentManager(options);
    magoManager.chemicalAccidentManager.load_chemicalAccidentIndexFile(jsonPath);
    magoManager.chemicalAccidentManager._animationState = Mago3D.CODE.processState.STARTED;

    magoManager.chemicalAccidentManager.setLegendColors(get3DLegendColors(1e8));
    magoManager.chemicalAccidentManager.setLegendValuesScale(1e8);
    magoManager.chemicalAccidentManager.hide();
  }
}

const load2dSimulation = () => {
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();

  let managerOptions = {
    magoManager : magoManager,
  }
  magoManager.chemicalAccident2dManager = new Mago3D.ChemicalAccident2DManager(managerOptions);
  magoManager.chemicalAccident2dManager._animationState = Mago3D.CODE.processState.STARTED;


  let optionsA = {
    url : "/data/chemicalAccident/output_chemAcc2D_20240805/JsonIndex2D.json",
    renderingColorType : 2,  // 0= rainbow, 1= monotone, 2= legendColors.***
    renderBorder : true, // true, false.***
    textureFilterType : 0,  // 0= nearest, 1= linear.***
    interpolationBetweenSlices : 0,  // 0= nearest, 1= linear.***
    textureFlipYAxis : true
  };
  let accidentLayerA = new Mago3D.ChemicalAccident2DLayer(optionsA);
  accidentLayerA.setLegendColors(get2DLegendColors(1e8));
  accidentLayerA.hide();

  magoManager.chemicalAccident2dManager.addChemAccidentLayer2D(accidentLayerA);

  let optionsB = {
    url : "/data/chemicalAccident/output_chemAcc2D_acute_criticality/JsonIndex2D.json",
    renderingColorType : 2,  // 0= rainbow, 1= monotone, 2= legendColors.***
    renderBorder : true, // true, false.***
    textureFilterType : 0,  // 0= nearest, 1= linear.***
    interpolationBetweenSlices : 0,  // 0= nearest, 1= linear.***
    textureFlipYAxis : true
  };
  let accidentLayerB = new Mago3D.ChemicalAccident2DLayer(optionsB);
  accidentLayerB.setLegendColors(getAcuteCriticalityLegendColors());
  accidentLayerB.hide();

  magoManager.chemicalAccident2dManager.addChemAccidentLayer2D(accidentLayerB);

  let optionsC = {
    url : "/data/chemicalAccident/output_chemAcc2D_victim_distribution/JsonIndex2D.json",
    renderingColorType : 2,  // 0= rainbow, 1= monotone, 2= legendColors.***
    renderBorder : true, // true, false.***
    textureFilterType : 1,  // 0= nearest, 1= linear.***
    interpolationBetweenSlices : 0,  // 0= nearest, 1= linear.***
    textureFlipYAxis : true
  };
  let accidentLayerC = new Mago3D.ChemicalAccident2DLayer(optionsC);
  accidentLayerC.setLegendColors(getVictimDistributionLegendColors());
  accidentLayerC.hide();

  magoManager.chemicalAccident2dManager.addChemAccidentLayer2D(accidentLayerC);
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

const setLegendTable = (legendTitle : string, legendColors : any[], scale : number = 1.0, unit : string) => {
  legendList.value.list.length = 0;
  let cssLinearGradient = "linear-gradient(";
  for (let i = 0; i < legendColors.length; i++) {
    let legend = legendColors[i];

    let contextValue;
    if (scale === 1.0) {
      contextValue = legend.value;
    } else {
      contextValue = (parseFloat(legend.value) / scale).toFixed(10);
    }

    let context = "";
    context = contextValue;
    let alpha = legend.alpha < 0.3 ? 0.3 : legend.alpha;
    let color = "rgba(" + (legend.red * 255) + ", " + (legend.green * 255) + ", " + (legend.blue * 255) + ", " + alpha + ")";
    legendList.value.list.push({
      index : i,
      context : context,
      color : color
    });
    cssLinearGradient += color + ", ";
  }
  cssLinearGradient = cssLinearGradient.slice(0, -2) + ")";
  legendList.value.title = legendTitle;
  legendList.value.unit = unit;
  legendList.value.cssLinearGradient = cssLinearGradient;
}

const getDamageGrade = (grade : string) => {
  switch (grade) {
    case '1':
      return '저위험';
    case '2':
      return '중위험';
    case '3':
      return '고위험';
    default:
      return '해당없음';
  }
}

const setLegendTableForAcuteCriticality = (legendTitle : string, legendColors : any[], unit : string) => {
  legendList.value.list.length = 0;
  let cssLinearGradient = "linear-gradient(";
  let percent = 100 / legendColors.length;
  for (let i = 0; i < legendColors.length; i++) {
    let legend = legendColors[i];
    let contextValue = getDamageGrade(`${legend.value}`);
    let alpha = legend.alpha < 0.3 ? 0.3 : legend.alpha;
    let color = "rgba(" + (legend.red * 255) + ", " + (legend.green * 255) + ", " + (legend.blue * 255) + ", " + alpha + ")" + " " + (percent * i) + "% " + percent * (i + 1) + "%";
    legendList.value.list.push({
      index : i,
      context : contextValue,
      color : color
    });
    cssLinearGradient += color + ", ";
  }
  cssLinearGradient = cssLinearGradient.slice(0, -2) + ")";
  legendList.value.title = legendTitle;
  legendList.value.unit = unit;
  legendList.value.cssLinearGradient = cssLinearGradient;
}

const get3DLegendColors = (scale : number) => {
  const legendValuesScale = scale;
  const minValue = 0;
  const maxValue = 112000000.0 * legendValuesScale;
  const numColors = 12;
  const accentuationFactor = 1.0;
  const legendValues = getLogDivisions(minValue, maxValue, numColors, accentuationFactor);
  const accentuationFactorAlpha = 2.0;
  const alphaValues = getLogDivisions(0.0, 1.0, numColors, accentuationFactorAlpha);

  const legendColors = [];
  let LegendColor = new Mago3D.ColorLegend(0/255, 0/255, 143/255, alphaValues[0], legendValues[0]);  // 0
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

const get2DLegendColors = (scale: number) => {
  const legendValuesScale = scale;
  const minValue = 0;
  const maxValue = 1620000.0 * legendValuesScale;
  const numColors = 12;
  const accentuationFactor = 2.0;
  const legendValues = getLogDivisions(minValue, maxValue, numColors, accentuationFactor);

  // create legend colors.***
  const legendColors = [];
  let alphaColor = 0.5;

  let LegendColor = new Mago3D.ColorLegend(0/255, 0/255, 143/255, 0.1, legendValues[0]);  // 0
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 15/255, 255/255, alphaColor, legendValues[1]);  // 1
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 95/255, 255/255, alphaColor, legendValues[2]);  // 2
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 175/255, 255/255, alphaColor, legendValues[3]);  // 3
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 255/255, 255/255, alphaColor, legendValues[4]);  // 4
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(79/255, 255/255, 175/255, alphaColor, legendValues[5]);  // 5
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(159/255, 255/255, 95/255, alphaColor, legendValues[6]);  // 6
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(239/255, 255/255, 15/255, alphaColor, legendValues[7]);  // 7
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 191/255, 0/255, alphaColor, legendValues[8]);  // 8
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 111/255, 0/255, alphaColor, legendValues[9]);  // 9
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 31/255, 0/255, alphaColor, legendValues[10]);  // 10
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(207/255, 0/255, 0/255, alphaColor, legendValues[11]);  // 11
  legendColors.push(LegendColor);

  return legendColors;
}

const getAcuteCriticalityLegendColors = () => {
  const legendColors = [];

  let alphaColor = 0.5;
  let LegendColor = new Mago3D.ColorLegend(255/255, 255/255, 255/255, 0.1, 0.0);
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 255/255, 0/255, alphaColor, 1.0); // green
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 255/255, 0/255, alphaColor, 2.0); // yellow
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 0/255, 0/255, alphaColor, 3.0); // red
  legendColors.push(LegendColor);

  return legendColors;
}

const getVictimDistributionLegendColors = () => {
  const legendColors = [];

  let alphaColor = 0.5;
  let LegendColor = new Mago3D.ColorLegend(255/255, 255/255, 255/255, 0.1, 0.0);
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 0/255, 255/255, alphaColor, 1.0);
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 127/255, 255/255, alphaColor, 2.0);
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(0/255, 255/255, 127/255, alphaColor, 3.0);
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 255/255, 0/255, alphaColor, 6.0);
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 127/255, 0/255, alphaColor, 9.0);
  legendColors.push(LegendColor);
  LegendColor = new Mago3D.ColorLegend(255/255, 0/255, 0/255, alphaColor, 12.0);
  legendColors.push(LegendColor);

  return legendColors;
}

const startChemicalAccident3d = () => {
  if (layerState.value.chemicalAccident3d) {
    stopAllChemicalAccident();
    layerState.value.selectedAccident = undefined;
    return;
  }
  stopAllChemicalAccident();
  layerState.value.chemicalAccident3d = true;
  layerState.value.selectedAccident = "chemicalAccident3d";

  // 농도
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
  if (magoManager.chemicalAccidentManager) {
    magoManager.chemicalAccidentManager.show();
    magoManager.chemicalAccidentManager.setLegendValuesScale(1e10);

    let chemicalAccidentLayer = magoManager.chemicalAccidentManager.getChemicalAccidentLayer(0);
    if (!chemicalAccidentLayer) {
      alert("레이어가 준비되지 않았습니다.");
      return;
    }
    chemicalAccidentLayer.setUseMinMaxValuesToRender(0)

    setLegendTable("농도", get3DLegendColors(1e8), 1e8, "(μg/㎥)")
  }
}

const startChemicalAccident2d = () => {
  if (layerState.value.chemicalAccident2d) {
    stopAllChemicalAccident();
    layerState.value.selectedAccident = undefined;
    return;
  }
  stopAllChemicalAccident();
  layerState.value.chemicalAccident2d = true;
  layerState.value.selectedAccident = "chemicalAccident2d";

  // 노출량
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
  if (magoManager.chemicalAccident2dManager) {
    const layer = magoManager.chemicalAccident2dManager.getChemicalAccident2DLayer(0);
    layer.show();

    setLegendTable("농도", get2DLegendColors(1e8), 1e8, "(μg/㎥)")

  }
}

const startChemicalAccidentAcuteHazard = () => {
  if (layerState.value.accidentAcuteHazard) {
    stopAllChemicalAccident();
    layerState.value.selectedAccident = undefined;
    return;
  }
  stopAllChemicalAccident();
  layerState.value.accidentAcuteHazard = true;
  layerState.value.selectedAccident = "chemicalAccidentAcuteHazard";

  // 급성 위해도
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
  if (magoManager.chemicalAccident2dManager) {
    const layer = magoManager.chemicalAccident2dManager.getChemicalAccident2DLayer(1)
    layer.show();
    setLegendTableForAcuteCriticality("위해도", getAcuteCriticalityLegendColors(), "")
  }
}

const stopAllChemicalAccident = () => {
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
  if (magoManager.chemicalAccident2dManager) {
    magoManager.chemicalAccident2dManager.getChemicalAccident2DLayer(0).hide();
    magoManager.chemicalAccident2dManager.getChemicalAccident2DLayer(1).hide()
  }
  if (magoManager.chemicalAccidentManager) {
    magoManager.chemicalAccidentManager.hide();
  }
  layerState.value.chemicalAccident3d = false;
  layerState.value.chemicalAccident2d = false;
  layerState.value.accidentAcuteHazard = false;
}


const startVictimDistribution = () => {
  if (layerState.value.victimDistribution) {
    stopAllVictim();
    layerState.value.selectedVictim = undefined;
    return;
  }
  stopAllVictim();
  layerState.value.victimDistribution = true;
  layerState.value.selectedVictim = "victimDistribution";

  // 피해자 분포
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
  if (magoManager.chemicalAccident2dManager) {
    const layer = magoManager.chemicalAccident2dManager.getChemicalAccident2DLayer(2);
    layer.show();
    setLegendTable("피해자 분포", getVictimDistributionLegendColors(), 1, "(명/㎡)")
  }
}

const startVictimMovement = () => {
  if (layerState.value.victimMovement) {
    stopAllVictim();
    layerState.value.selectedVictim = undefined;
    return;
  }
  stopAllVictim();
  layerState.value.victimMovement = true;
  layerState.value.selectedVictim = "victimMovement";

  setLegendTable("", [], 1, "")
  trackEntities.forEach((entity) => {
    entity.show = true;
  });

  // 피해자 이동
  /*const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
  if (magoManager.itineraryManager) {
    const layers = magoManager.itineraryManager._itineraryLayersArray;
    magoManager.itineraryManager.show();
    layers.forEach((itinerarylayer : any) => {
      itinerarylayer.setLayerShow(true);
    });


  }*/
}

const startPersonalMovement = () => {
  stopAllVictim();
  layerState.value.selectedVictim = "personalMovement";
  setLegendTable("", [], 1, "")
}

/*const togglePersonalMovement = () => {
  if (layerState.value.personalMovement) {
    stopAllVictim();
    layerState.value.selectedVictim = undefined;
    store.hideChartWindow();
    return;
  }
  stopAllVictim();
  layerState.value.personalMovement = true;
  layerState.value.selectedVictim = "personalMovement";

  /!*const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
  if (magoManager.itineraryManager) {
    magoManager.itineraryManager.show();
    const layers = magoManager.itineraryManager._itineraryLayersArray;
    layers.forEach((itinerarylayer : any) => {
      //console.log(itinerarylayer)
      if (itinerarylayer._filePath.indexOf(userId) >= 0) {
        itinerarylayer.setLayerShow(true);
      } else {
        itinerarylayer.setLayerShow(false);
      }
    });
  }*!/
  setLegendTable("", [], 1, "")
  store.showChartWindow();
}*/


const stopAllVictim = () => {
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
  /*if (magoManager.itineraryManager) {
    magoManager.itineraryManager.hide();
    const layers = magoManager.itineraryManager._itineraryLayersArray;
    layers.forEach((itinerarylayer : any) => {
      itinerarylayer.setLayerShow(false);
    });

    trackEntities.forEach((entity) => {
      entity.show = false;
    });
    if (singleTrackEntity.value) {
      singleTrackEntity.value.show = false;
    }
  }*/

  trackEntities.forEach((entity) => {
    entity.show = false;
  });
  if (singleTrackEntity.value) {
    singleTrackEntity.value.show = false;
  }

  //store.hideChartWindow();
  layerState.value.victimDistribution = false;
  layerState.value.victimMovement = false;
  layerState.value.personalMovement = false;
  if (magoManager.chemicalAccident2dManager) {
    magoManager.chemicalAccident2dManager.getChemicalAccident2DLayer(2).hide();
  }
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

defineExpose({
  startPersonalMovement,
});

</script>

<template>
  <div id="simulation-layer" class="layer left top horizontal">
    <h1>
      레이어
      <button class="close" @click="toggleLayer()"><img class="icon" src="/src/assets/images/icons/minus.png"></button>
    </h1>
    <div class="layer-contents horizontal" v-show="layerState.layer">
      <h3>사고물질 레이어</h3>
      <div class="switch-wrapper">
        <h4>사고물질 농도 (3D)</h4>
        <label>
          <input type="radio" name="accident-group" v-model="layerState.selectedAccident" value="chemicalAccident3d" @click="startChemicalAccident3d()">
          <span></span>
        </label>
      </div>
      <div class="switch-wrapper">
        <h4>사고물질 농도 (2D)</h4>
        <label>
          <input type="radio" name="accident-group" v-model="layerState.selectedAccident" value="chemicalAccident2d" @click="startChemicalAccident2d()">
          <span></span>
        </label>
      </div>
      <div class="switch-wrapper">
        <h4>사고물질 급성위해도</h4>
        <h4>(위치정보 미확인 피해등급)</h4>
        <label>
          <input type="radio" name="accident-group" v-model="layerState.selectedAccident" value="chemicalAccidentAcuteHazard" @click="startChemicalAccidentAcuteHazard()">
          <span></span>
        </label>
      </div>
      <div class="line"></div>
      <h3>예상피해자 레이어</h3>
      <div class="switch-wrapper">
        <h4>예상 피해자 분포</h4>
        <label>
          <input type="radio" name="victim-group" v-model="layerState.selectedVictim" value="victimDistribution" @click="startVictimDistribution()">
          <span></span>
        </label>
      </div>
      <div class="switch-wrapper">
        <h4>예상 피해자 이동동선</h4>
        <label>
          <input type="radio" name="victim-group" v-model="layerState.selectedVictim" value="victimMovement" @click="startVictimMovement()">
          <span></span>
        </label>
      </div>
<!--      <div class="switch-wrapper">
        <h4>개인별 상세 동선</h4>
        <label>
          <input type="radio" name="victim-group" v-model="layerState.selectedVictim" value="personalMovement"  @click="togglePersonalMovement()">
          <span></span>
        </label>
      </div>-->
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
        <h4>동심원 보기</h4>
        <label>
          <input type="checkbox" name="victim-group" @change="toggleRadius(getViewer())" checked>
          <span></span>
        </label>
      </div>
    </div>
  </div>
  <div id="legend-layer" class="layer left top horizontal">
    <h1>
      범례
      <button class="close" @click="toggleLegend()"><img class="icon" src="/src/assets/images/icons/minus.png"></button>
    </h1>
    <div class="layer-contents horizontal" v-show="layerState.legend">
      <span class="legend-unit" v-if="legendList.list.length > 0">{{legendList.unit}}</span>
      <h3 v-if="legendList.list.length > 0">{{legendList.title}}</h3>
      <div class="legend-wrap" v-show="legendList.list.length > 0">
        <div class="legend" :style="{ background: legendList.cssLinearGradient }"></div>
        <div class="datalist-wrap">
          <datalist>
            <option v-for= "legend in legendList.list" :key="legend.index">{{legend.context}}</option>
          </datalist>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

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
span.legend-unit {
  position: absolute;
  right: 2px;
  text-align: right;
  font-size: 10px;
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
  min-width: 170px;
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
  height: 200px;
  display: grid;
  grid-auto-flow: dense;
  position: relative;
  margin-left: 5px;
}

div.datalist-wrap datalist > option {
  /*margin-top: -0.5em;
  font-size: 0.8em;
  margin-left: 5px;
*/
  font-size: 0.8em;
  margin-left: 5px;
  vertical-align: middle;
  /* text-align: center; */
  align-content: center;
}

/*div.datalist-wrap datalist > option:last-child {
  position: absolute;
  bottom: 0;
  margin-bottom: -0.5em;
}*/

div.legend {
  height: 200px;
  width: 20px;
  /*background: linear-gradient(red, yellow, green, blue);*/
  border: 1px solid #464646;
  display: inline-block;
  margin: 0;
  vertical-align: middle;
}
</style>
