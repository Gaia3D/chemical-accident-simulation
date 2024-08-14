<script setup lang="ts">
import Map from "./Map.vue";
import {onMounted, ref} from "vue";
import "../map-custom.css";
import MapSelector from "./MapSelector.vue";
import MapController from "./MapController.vue";
import TimeSlider from "./TimeSlider.vue";
import SimulationController from "./SimulationController.vue";
import ChartLayer from "./ChartLayer.vue";
import { store } from "../store/store";

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
const chartLayer = ref();

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
  shouldAnimate: true,
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


onMounted(async () => {
  console.log('[MainComponent] Mounted Main Component');

  viewer.value = mapComponent.value.getViewer();
  magoInstance.value = mapComponent.value.getMagoInstance();
  transferViewer.value.viewer = viewer.value;
  transferViewer.value.magoInstance = magoInstance.value;

  viewer.value.scene.globe.depthTestAgainstTerrain = false;

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

  // esc key event
  let debugCount = 1;
  window.addEventListener('keydown', (event) => {
    if (event.key === "Escape") {
      console.log("[DEBUG] Pressed ESC Key", debugCount);
      if (debugCount == 5) {
        store.startRender();
      } else {
        debugCount++;
      }
    }
  });

  window.addEventListener('message', (event) => {
    console.log("[Message Received]", event);
    const data = event.data;
    if (data) {
      if (data.action === "startRender") {
        store.startRender();
      } else if (data.action === "showChart") {
        store.showChartWindow();
      } else if (data.action === "hideChart") {
        store.hideChartWindow();
      } else if (data.action === "loadChart") {
        if (data.detail) {
          chartLayer.value.setAccidentInfo(data.detail.accidentId, data.detail.userId);
          chartLayer.value.loadPersonalData();
          store.showChartWindow();
        } else {
          console.error("[ERROR] No Detail Data");
        }
      }  else {
        console.error("[ERROR] Unknown Action");
      }
    } else {
      console.error("[ERROR] No Data");
    }
  });
});

</script>

<template>
  <div class="loading" v-show="store.isShowLoading" v-if="store.isReady">
    <span>시뮬레이션 데이터 로드 중</span>
  </div>

  <div class="float-layer left top horizontal" v-if="store.isReady">
    <div id="info" class="layer">
      <h2>충청남도 당진시 시곡동 77-5 | 페놀(3,600L) 누출</h2>
      <h4>분석데이터 : 2024/02/06 11:00 기준 (2일 0시간 예측)</h4>
<!--      <h4>사고원인 : 시설결함</h4>-->
    </div>
    <div class="left vertical">
      <SimulationController :transfer-viewer="transferViewer" ref="simulationController"/>
    </div>
  </div>
  <div class="float-layer right top horizontal" v-show="store.isReady">
    <MapSelector :transferViewer="transferViewer" ref="mapSelector"/>
    <MapController :transferViewer="transferViewer" ref="mapController"/>
  </div>
  <div class="float-layer right bottom vertical" v-if="store.isReady">
  </div>
  <div class="float-layer left bottom horizontal" v-if="store.isReady">
    <ChartLayer ref="chartLayer"/>
    <TimeSlider :transfer-viewer="transferViewer" ref="timeSlider"/>
  </div>
  <Map :init-options="options" ref="mapComponent"/>
</template>

<style scoped>
div.loading {
  position: fixed;
  width: 100%;
  height: 100%;
  left: 0;
  top: 0;
  z-index: 100;
  background-color: #000000b5;
}
div.loading span {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 20px;
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
</style>
