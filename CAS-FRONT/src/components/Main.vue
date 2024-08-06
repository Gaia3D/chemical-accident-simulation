<script setup lang="ts">
import Map from "./Map.vue";
import {onMounted, ref} from "vue";
import "../map-custom.css";
import MapSelector from "./MapSelector.vue";
import MapController from "./MapController.vue";
import TimeSlider from "./TimeSlider.vue";
import SimulationController from "./SimulationController.vue";
import ChartLayer from "./ChartLayer.vue";

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

const layerState = ref({
  chronic : false,
  acuteHarm : false
});

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

const toggleChronic = () => {
  layerState.value.chronic = !layerState.value.chronic;
}

const toggleAcuteHarm = () => {
  layerState.value.acuteHarm = !layerState.value.acuteHarm;
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
  mapSelector.value.toggleVWorldBaseLayer();
});

</script>

<template>
  <div class="modal-dim" v-show="layerState.chronic || layerState.acuteHarm" ></div>
  <div id="acute-harm" class="non-modal layer" v-show="layerState.acuteHarm">
    <h1>
      개인별 피해등급(급성위해도) 산정기준
      <button class="close" @click="toggleAcuteHarm()"><img class="icon" src="/src/assets/images/icons/minus.png"></button>
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
  <div id="determining-chronic-layer" class="non-modal layer"  v-show="layerState.chronic">
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
  <div class="float-layer left top horizontal">
    <div id="info" class="layer">
      <h2>충청남도 당진시 시곡동 77-5 | 페놀(3,600L) 누출</h2>
      <h4>분석데이터 : 2024/02/06 11:00 기준 (1일 0시간 예측)</h4>
<!--      <h4>사고원인 : 시설결함</h4>-->
    </div>
    <div class="left vertical">
      <SimulationController :transfer-viewer="transferViewer" ref="simulationController"/>
    </div>
  </div>
  <div class="float-layer right top horizontal">
    <MapSelector :transferViewer="transferViewer" ref="mapSelector"/>
    <MapController :transferViewer="transferViewer" ref="mapController"/>
  </div>
  <div class="float-layer right bottom vertical">

  </div>
  <div class="float-layer right bottom horizontal">
    <ChartLayer :transferViewer="transferViewer" ref="chartLayer"/>
    <TimeSlider :transfer-viewer="transferViewer" ref="timeSlider"/>
  </div>
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
