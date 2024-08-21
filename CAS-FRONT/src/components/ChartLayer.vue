<script setup lang="ts">
import {onMounted, ref, watch} from "vue";
import "../map-custom.css";
import CasLineChart from "./CasLineChart.vue";
import DosageLineChart from "./DosageLineChart.vue";
import { store } from "../store/store";

const lineChart = ref();
const dosageLineChart = ref();

/* @ts-ignore */
const Cesium = window.Cesium;
/* @ts-ignore */
const Mago3D = window.Mago3D;

const layerState = ref({
  chronicInfo : false,
  acuteHarmInfo : false,
  isCas: false,
  isDosage: false,
});

const personalTrackEntity : any = ref(undefined);

const props = defineProps<{
  transferViewer: any;
}>();

const toggleChronic = () => {
  layerState.value.chronicInfo = !layerState.value.chronicInfo;
}

const toggleAcuteHarm = () => {
  layerState.value.acuteHarmInfo = !layerState.value.acuteHarmInfo;
}

watch(() => store.isShowChartWindow, (isShow) => {
  if (!personalTrackEntity.value) {
    return;
  }
  personalTrackEntity.value.show = isShow;
});

onMounted(async () => {
  //console.log('[MainComponent] Mounted Chart Layer Component');
  layerState.value.isCas = true;
});

const personalData = ref({
  json : undefined
});

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

const loadPersonalRiskData = (personalId : string) => {
  const chemicalAccidentInfo = store.getChemicalAccidentInfo();
  const accidentId = chemicalAccidentInfo.accidentInfo.accidentNo;
  const url = `${import.meta.env.VITE_API_SERVER}/accident/${accidentId}/personal/${personalId}/risk`;
  fetch(url, {
    method:'GET',
    headers: {
      'Access-Control-Allow-Origin': '*'
    }
  }).then((response) => {
    return response.json()
  }).then((json) => {
    json.indiDamageGrade = getDamageGrade(json.indiDamageGrade);
    store.getChemicalAccidentInfo().personRiskInfo = json;
  }).catch((error) => {
    console.error(error);
  })
}

const drawTrackEntity = async (list : any[]) => {
  const viewer = getViewer();
  let cartographicDegrees : any[] = [];
  let timeUnit = (60 * 10); // 10분
  let increement = -timeUnit;
  list.forEach((item : any) => {
    let lon = item.utmkX;
    let lat = item.utmkY;

    cartographicDegrees.push(increement += timeUnit);
    cartographicDegrees.push(lon);
    cartographicDegrees.push(lat);
    cartographicDegrees.push(0);
  });

  const czml = [
    {
      id: "document",
      name: "CZML Path",
      version: "1.0",
    },
    {
      id: store.getChemicalAccidentInfo().personRiskInfo.personalId,
      availability: "2024-02-06T02:00:00Z/2024-02-08T02:00:00Z",
      path: {
        material: {
          polylineDash: {
            color: {
              rgba: [128, 128, 128, 255],
            },
          },
          width: 2,
        },
        width: 4,
        leadTime: 10,
        trailTime: 10000000,
        resolution: 5,
      },
      billboard: {
        //image: "/src/assets/images/icons/man.png",
        image: "/data/icons/man.png",
        width: 32,
        height: 32,
        pixelOffset : new Cesium.Cartesian2(0.0, -16),
      },
      position: {
        epoch: "2024-02-06T02:00:00Z",
        cartographicDegrees: cartographicDegrees,
      },
    },
  ];

  viewer.dataSources.removeAll();
  const dataSource = await viewer.dataSources.add(
      Cesium.CzmlDataSource.load(czml)
  );

  personalTrackEntity.value = dataSource.entities.getById(store.getChemicalAccidentInfo().personRiskInfo.personalId);
  viewer.trackedEntity = dataSource.entities.getById("path");
}


const loadPersonalData = (personalId : string) => {
  const chemicalAccidentInfo = store.getChemicalAccidentInfo();
  const accidentId = chemicalAccidentInfo.accidentInfo.accidentNo;
  const url = `${import.meta.env.VITE_API_SERVER}/accident/${accidentId}/personal/${personalId}`;
  //const url = `${import.meta.env.VITE_API_SERVER}/accident/${accidentInfo.value.accidentId}/personal/${accidentInfo.value.personalId}`;
  fetch(url, {
    method:'GET',
    headers: {
      'Access-Control-Allow-Origin': '*'
    }
  }).then((response) => {
    return response.json()
  }).then((json) => {
    drawTrackEntity(json);
    store.getChemicalAccidentInfo().personInfo = json;
    if (!json || json.length === 0) {
      console.error("[ERROR] No Data");
      return;
    }
    personalData.value.json = json;
    lineChart.value.updateData(json);
    dosageLineChart.value.updateData(json);
  }).catch((error) => {
    console.error(error);
  })
}

const selectCas = () => {
  layerState.value.isCas = true;
  layerState.value.isDosage = false;
}

const selectDosage = () => {
  layerState.value.isCas = false;
  layerState.value.isDosage = true;
}

const toggleLayer = () => {
  store.toggleChartWindow();
}

const getViewer = () => {
  return props.transferViewer.viewer;
}

defineExpose({
  loadPersonalData,
  loadPersonalRiskData
});

</script>

<template>
  <div class="modal-dim" v-show="layerState.chronicInfo || layerState.acuteHarmInfo" ></div>
  <div id="acute-harm" class="non-modal layer" v-show="layerState.acuteHarmInfo">
    <h1>
      개인별 피해등급(급성위해도) 산정기준
      <button class="close" @click="toggleAcuteHarm()"><img class="icon" src="/src/assets/images/icons/close.png"></button>
    </h1>
    <div class="layer-contents">
      <table>
        <thead>
        <tr>
          <th>등급</th>
          <th>피해정도</th>
          <th>평가 기준치 <br>({{store.getChemicalAccidentInfo().chemicalInfo.chemicalNm}} - {{store.getChemicalAccidentInfo().chemicalInfo.chemicalEngNm}})</th>
        </tr>
        </thead>
        <tbody>
        <tr>
          <td>고위험</td>
          <td>생명의 위협 또는 사망할 수 있는 피해 정도</td>
          <td>급성 독성 참고치(Level3) 초과 <br/> (AEGL-3 10min : {{store.getChemicalAccidentInfo().chemicalInfo.aegl3Conc}}mg/m3)</td>
        </tr>
        <tr>
          <td>중위험</td>
          <td>비가역적 또는 심각하고, 지속가능한 장애 효과를 가져올 수 있는 피해 정도</td>
          <td>급성 독성 참고치(Level2) 초과 <br/> (AEGL-2 10min : {{store.getChemicalAccidentInfo().chemicalInfo.aegl2Conc}}mg/m3)</td>
        </tr>
        <tr>
          <td>저위험</td>
          <td>상당한 불쾌감, 자극, 어던 증상을 동반하지 않는 피해 정도</td>
          <td>급성 독성 참고치(Level1) 초과 <br/> (AEGL-1 10min : {{store.getChemicalAccidentInfo().chemicalInfo.aegl1Conc}}mg/m3)
          </td>
        </tr>
        <tr>
          <td>해당없음</td>
          <td>피해 없음</td>
          <td>급성 독성 참고치(Level1) 미만 <br/> (AEGL-1 10min : {{store.getChemicalAccidentInfo().chemicalInfo.aegl1Conc}}mg/m3)
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
  <div id="determining-chronic-layer" class="non-modal layer"  v-show="layerState.chronicInfo">
    <h1>
      개인별 만성피해 유무 산정기준
      <button class="close" @click="toggleChronic()"><img class="icon" src="/src/assets/images/icons/close.png"></button>
    </h1>
    <div class="layer-contents">
      <table>
        <thead>
        <tr>
          <th>구분</th>
          <th>평가기준</th>
        </tr>
        </thead>
        <tbody>
        <tr>
          <td>호흡 발암</td>
          <td rowspan="2">국립환경과학원 화학물질 위해성평가의 구체적 방법 등에 관한 규정, <br> US EPA 에서는 만성위해도값이 10^-6 값 이하인 경우 위해도가 없다고 판단함
          </td>
        </tr>
        <tr>
          <td>섭취-토양 발암</td>
        </tr>
        <tr>
          <td>호흡비발암</td>
          <td rowspan="2">국립환경과학원 화학물질 위해성평가의 구체적 방법 등에 관한 규정, <br> US EPA 에서는 만성위해도값이 1 미만인 경우 위해도가 없다고 판단함</td>
        </tr>
        <tr>
          <td>섭취-토양 비발암</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
  <div id="chart-layer" class="layer left top" v-show="store.isShowChartWindow">
    <h1>
      개인별 상세 피해규모 예측 차트
      <button class="close" @click="toggleLayer()">
        <img class="icon" src="/src/assets/images/icons/close.png">
      </button>
    </h1>
    <div class="layer-contents">
      <div class="chart-info">
        <div>
          예상 피해자 ID : <span>{{store.getChemicalAccidentInfo().personRiskInfo.personalId}}</span>
        </div>
        <div class="vertical-line"></div>
        <div>
          개인피해등급 : <span>{{store.getChemicalAccidentInfo().personRiskInfo.indiDamageGrade}}</span>
        </div>
        <div>
          <button class="info" @click="toggleAcuteHarm()">
            <img class="icon" src="/src/assets/images/icons/info.png">
          </button>
        </div>
        <div class="vertical-line"></div>
        <div>
          만성 위해도 :
        </div>
        <div>
          호흡-발암 위해유무 : {{store.getChemicalAccidentInfo().personRiskInfo.chronicRespCarcDamage}} ({{store.getChemicalAccidentInfo().personRiskInfo.chronicExpSoilIngnoncarc}}) <br> 호흡-비발암 위해유무 : {{store.getChemicalAccidentInfo().personRiskInfo.chronicRespNoncarcDamage}} ({{store.getChemicalAccidentInfo().personRiskInfo.chronicExposureAmt}})
        </div>
        <div>
          섭취/토양-발암 위해유무 : {{store.getChemicalAccidentInfo().personRiskInfo.chronicSoilCarcDamage}} ({{ store.getChemicalAccidentInfo().personRiskInfo.chronicExpSoilIngcarc }}) <br> 섭취/토양-비발암 위해유무 : {{store.getChemicalAccidentInfo().personRiskInfo.chronicSoilNoncarcRisk}} ({{store.getChemicalAccidentInfo().personRiskInfo.chronicExpSoilIngnoncarc}})
        </div>
        <button class="info" @click="toggleChronic()">
          <img class="icon" src="/src/assets/images/icons/info.png">
        </button>
      </div>
      <div class="layer-tab">
        <div @click="selectCas()" v-bind:class="layerState.isCas ? 'selected' : ''">
          개인피해등급(급성위해도)
        </div>
        <div @click="selectDosage()" v-bind:class="layerState.isDosage ? 'selected' : ''">
          노출량
        </div>
        <div class="chemical-info">
          {{store.getChemicalAccidentInfo().chemicalInfo.chemicalNm}}({{store.getChemicalAccidentInfo().chemicalInfo.chemicalEngNm}})
        </div>
      </div>
      <div class="layer-body">
        <div class="layer-wrap" v-show="layerState.isCas">
          <CasLineChart ref="lineChart" :personalData="personalData"/>
        </div>
        <div class="layer-wrap" v-show="layerState.isDosage">
          <DosageLineChart ref="dosageLineChart" :personalData="personalData"/>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped>
.chemical-info {
  position: relative;
  right: 0;
  float: right;
  padding: 8px !important;
  background: unset !important;
}

#chart-layer {
  width: calc(100vw - 40px);
  /*height: 200px;*/
  overflow: hidden;
  /*background-color: #ffffffd1;*/
}
.chart-info {
  min-height: 30px;
  font-size: 12px;
  margin: 0 10px;
}
.chart-info > div {
  display: inline-block;
  margin-right: 10px;
  vertical-align: middle;
  line-height: 18px;
}
.chart-info > div > span {
  font-weight: bold;
}

.vertical-line {
  border-left: 1px solid #d2d2d2;
  height: 12px;
  width: 0;
}

.layer-body {
  background-color: rgba(220, 220, 220, 0.86);
  margin: 0 5px 5px;
  padding: 5px;
  border-radius: 0 10px 10px 10px;
}
.layer-wrap {
  height: 150px;
}

.layer-tab {
  margin: 5px 5px 0;
}
.layer-tab div {
  display: inline-block;
  background-color: #ededed;
  font-size: 11px;
  padding: 8px 25px;
  border-radius: 10px 10px 0 0;
  cursor: pointer;
}
.layer-tab div.selected {
  background-color: rgba(220, 220, 220, 0.86) !important;
}

button.info {
  width: 18px;
  height: 18px;
  padding: 0;
  vertical-align: middle;
  background-color: unset;
}
button > img {
  width: 100%;
  height: 100%;
  filter: brightness(0) saturate(100%) invert(53%) sepia(0%) saturate(1352%) hue-rotate(179deg) brightness(65%) contrast(77%);
}

</style>
