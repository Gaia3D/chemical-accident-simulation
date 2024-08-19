<script setup lang="ts">
import {onMounted, ref} from "vue";
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

const toggleChronic = () => {
  layerState.value.chronicInfo = !layerState.value.chronicInfo;
}

const toggleAcuteHarm = () => {
  layerState.value.acuteHarmInfo = !layerState.value.acuteHarmInfo;
}

/*const props = defineProps<{
  transferViewer: any;
}>();*/

const accidentInfo = ref({
  accidentId: 'CA201905001',
  personalId: 'USER001'
});

onMounted(async () => {
  console.log('[MainComponent] Mounted Chart Layer Component');
  //loadPersonalData();
  layerState.value.isCas = true;
});

const personalData = ref({
  json : undefined
});

const setAccidentInfo = (accidentId : string, personalId : string) => {
  accidentInfo.value.accidentId = accidentId;
  accidentInfo.value.personalId = personalId;
}

const loadPersonalData = () => {
  const url = `${import.meta.env.VITE_API_SERVER}/accident/${accidentInfo.value.accidentId}/personal/${accidentInfo.value.personalId}`;
  fetch(url, {
    method:'GET',
    headers: {
      'Access-Control-Allow-Origin': '*'
    }
  }).then((response) => {
    return response.json()
  }).then((json) => {
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

defineExpose({
  setAccidentInfo,
  loadPersonalData
});

/*const getViewer = () => {
  return props.transferViewer.viewer;
}*/

</script>

<template>
  <div class="modal-dim" v-show="layerState.chronicInfo || layerState.acuteHarmInfo" ></div>
  <div id="acute-harm" class="non-modal layer" v-show="layerState.acuteHarmInfo">
    <h1>
      개인별 피해등급(급성위해도) 산정기준
      <button @click="toggleAcuteHarm()"><img class="icon" src="/src/assets/images/icons/minus.png"></button>
    </h1>
    <div class="layer-contents">
      <table>
        <thead>
        <tr>
          <th>등급</th>
          <th>피해정도</th>
          <th>평가 기준치</th>
        </tr>
        </thead>
        <tbody>
        <tr>
          <td>고위험</td>
          <td>생명의 위협 또는 사망할 수 있는 피해 정도</td>
          <td>급성 독성 참고치(Level3) 초과 <br/> (AEGL-3 10min : 200mg/m3)</td>
        </tr>
        <tr>
          <td>중위험</td>
          <td>생명의 위협 또는 사망할 수 있는 피해 정도</td>
          <td>급성 독성 참고치(Level2) 초과 <br/> (AEGL-2 10min : 110mg/m3)</td>
        </tr>
        <tr>
          <td>저위험</td>
          <td>생명의 위협 또는 사망할 수 있는 피해 정도</td>
          <td>급성 독성 참고치(Level1) 초과 <br/> (AEGL-1 10min : 73mg/m3)
          </td>
        </tr>
        <tr>
          <td>해당없음</td>
          <td>피해 없음</td>
          <td>급성 독성 참고치(Level1) 미만 <br/> (AEGL-1 10min : 73mg/m3)
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
  <div id="determining-chronic-layer" class="non-modal layer"  v-show="layerState.chronicInfo">
    <h1>
      개인별 만성피해 유무 산정기준
      <button class="close" @click="toggleChronic()"><img class="icon" src="/src/assets/images/icons/minus.png"></button>
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
          <td rowspan="2">국립환경과학원 화학물질 위해성평가의 구체적 방법 등에 관한 규정, US EPA 에서는 만성위해도값이 10^-6 값 이하인 경우 위해도가 없다고 판단함
          </td>
        </tr>
        <tr>
          <td>섭취-토양 발암</td>
        </tr>
        <tr>
          <td>호흡비발암</td>
          <td rowspan="2">국립환경과학원 화학물질 위해성평가의 구체적 방법 등에 관한 규정, US EPA 에서는 만성위해도값이 1 미만인 경우 위해도가 없다고 판단함</td>
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
        <img class="icon" src="/src/assets/images/icons/minus.png">
      </button>
    </h1>
    <div class="layer-contents">
      <div class="chart-info">
        <div>
          예상 피해자 ID : <span>{{ accidentInfo.personalId }}</span>
        </div>
        <div class="vertical-line"></div>
        <div>
          개인피해등급 : <span>고위험</span>
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
        <div class="vertical-line"></div>
        <div>
          호흡-발암 위해유무 : O (2.13E-06)
        </div>
        <div>
          호흡-비발암 위해유무 : X (0.23E-05)
        </div>
        <div>
          섭취/토양-발암 위해유무 : O (2.13E-06)
        </div>
        <div>
          섭취/토양-비발암 위해유무 : X (0.23E-05)
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
          만성노출량
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
#chart-layer {
  width: calc(100vw - 40px);
  /*height: 200px;*/
  overflow: hidden;
  background-color: #ffffffd1;
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
}

</style>
