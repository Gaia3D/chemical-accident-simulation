<script setup lang="ts">
import {onMounted, ref} from "vue";
import "../map-custom.css";

/* @ts-ignore */
const Cesium = window.Cesium;
/* @ts-ignore */
const Mago3D = window.Mago3D;

const props = defineProps<{
  transferViewer: any;
}>();

const timeTable = ref<any>({
  startTime : {
    year : 2024,
    month : 2,
    day : 6,
    hour : 11,
    minute : 0,
    second : 0
  },
  endTime : {
    year : 2024,
    month : 2,
    day : 7,
    hour : 11,
    minute : 0,
    second : 0
  },
  timeScale : 1000,
  nowUnixTimeMilisec : 10000000,
  minUnixTimeMilisec : 0,
  maxUnixTimeMilisec : 86400000,

  startUnixTimeMilisec : 0,
});

const setTimeScale = (timeScale: number) => {
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
  //magoManager.animationTimeController.setTimeScale(timeScale);
  magoManager.animationTimeController._timeScale = timeScale;
}

const resetTime = () => {
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
  let timeOptions = {
    timeScale : 1000,
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
}

let intervalEvent: any = undefined;

/*const setCesiumClock = (startMilisec, currentMilisec) => {

}*/

const setTime = () => {
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
  const startMilisec = magoManager.animationTimeController._animationStartUnixTimeMilisec;
  const currentTime = parseInt(timeTable.value.nowUnixTimeMilisec);
  magoManager.animationTimeController._currentUnixTimeMilisec = startMilisec + currentTime;


  let startDateTime = new Date(startMilisec);
  let currentDateTime = new Date( magoManager.animationTimeController._currentUnixTimeMilisec);
  let endDateTime = new Date(startMilisec + 86400000);

  const timeInfo = toTimeFormat(currentDateTime);
  const endTimeInfo = toTimeFormat(endDateTime);


  let startJulianDate = Cesium.JulianDate.fromIso8601(startDateTime.toISOString())
  let currentJulianDate = Cesium.JulianDate.fromIso8601(currentDateTime.toISOString())
  let endJulianDate = Cesium.JulianDate.fromIso8601(endDateTime.toISOString())

  /* setCesiumDate */
  const viewer = getViewer();
  const clock = viewer.clock
  clock.startTime = startJulianDate
  clock.stopTime = endJulianDate
  clock.currentTime = currentJulianDate
  clock.multiplier = 1

  const timeInfoElement = document.getElementById('time-info');
  if (timeInfoElement) {
    timeInfoElement.innerText = `${timeInfo} / ${endTimeInfo}`;
  }

}

const playTime = () => {
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
  magoManager.animationTimeController.startAnimation();


  const viewer = getViewer();

  if (intervalEvent === undefined) {
    intervalEvent = setInterval(() => {
      timeTable.value.startUnixTimeMilisec = magoManager.animationTimeController._animationStartUnixTimeMilisec;

      const startUnixTimeMilisec = magoManager.animationTimeController._animationStartUnixTimeMilisec;
      const currentUnixTimeMilisec = magoManager.animationTimeController._currentUnixTimeMilisec;
      const offset = currentUnixTimeMilisec - startUnixTimeMilisec;
      timeTable.value.nowUnixTimeMilisec = offset;

      const date = new Date(magoManager.animationTimeController._currentUnixTimeMilisec);
      const timeInfo = toTimeFormat(date);

      const endDateTime = new Date(startUnixTimeMilisec + 86400000);
      const endTimeInfo = toTimeFormat(endDateTime);

      let startDateTime = new Date(startUnixTimeMilisec);


      let startJulianDate = Cesium.JulianDate.fromIso8601(startDateTime.toISOString())
      let endJulianDate = Cesium.JulianDate.fromIso8601(endDateTime.toISOString())
      let currentTime = Cesium.JulianDate.fromIso8601(date.toISOString())


      //let startDateTime = new Date(startUnixTimeMilisec);
      //let endDateTime = new Date(startUnixTimeMilisec + 86400000);

      /* setCesiumDate */
      let clock = viewer.clock
      clock.startTime = startJulianDate
      clock.stopTime = endJulianDate
      clock.currentTime = currentTime
      clock.multiplier = 1



      const timeInfoElement = document.getElementById('time-info');
      if (timeInfoElement) {
        timeInfoElement.innerText = `${timeInfo} / ${endTimeInfo}`;
      }
    }, 10);
  }
}

const toTimeFormat = (date: Date) => {
  return `${date.getFullYear()}-${paddingZero(date.getMonth() + 1)}-${paddingZero(date.getDate())} ${paddingZero(date.getHours())}:${paddingZero(date.getMinutes())}:${paddingZero(date.getSeconds())}`;
}

const paddingZero = (num: number) => {
  return num < 10 ? `0${num}` : `${num}`;
}

const pauseTime = () => {
  const magoInstance = props.transferViewer.magoInstance;
  const magoManager = magoInstance.getMagoManager();
  magoManager.animationTimeController.pauseAnimation();

  if (intervalEvent !== undefined) {
    clearInterval(intervalEvent);
    intervalEvent = undefined;
  }
}

onMounted(async () => {
  console.log('[MainComponent] Mounted Slider Component');
});

const getViewer = () => {
  return props.transferViewer.viewer;
}

</script>

<template>
  <div class="float-layer right bottom horizontal">
    <div id="speed-controller" class="layer">
      <div class="vertical">
        <label>
          <input id="speed-1" type="radio" name="input-format" value="10" @click="setTimeScale(600)" checked/>
          <span>10분</span>
        </label>
        <label>
          <input id="speed-2" type="radio" name="input-format" value="30"  @click="setTimeScale(1800)"/>
          <span>30분</span>
        </label>
        <label>
          <input id="speed-3" type="radio" name="input-format" value="60"  @click="setTimeScale(3600)"/>
          <span>1시간</span>
        </label>
        <label>
          <input id="speed-4" type="radio" name="input-format" value="180"  @click="setTimeScale(10800)"/>
          <span>3시간</span>
        </label>
        <label>
          <input id="speed-5" type="radio" name="input-format" value="360"  @click="setTimeScale(21600)"/>
          <span>6시간</span>
        </label>
      </div>
    </div>
    <div id="time-slider" class="layer">
      <input type="range" step="0.01" min="0" max="86400000" value="1000" v-model="timeTable.nowUnixTimeMilisec" id="myRange" list="tick-marks" @change="setTime" @input="setTime">
      <datalist id="tick-marks">
        <option value="0"        label="00H"></option>
        <option value="7200000"  label="02H">2</option>
        <option value="14400000" label="04H"></option>
        <option value="21600000" label="06H"></option>
        <option value="28800000" label="08H"></option>
        <option value="36000000" label="10H"></option>
        <option value="43200000" label="12H"></option>
        <option value="50400000" label="14H"></option>
        <option value="57600000" label="16H"></option>
        <option value="64800000" label="18H"></option>
        <option value="72000000" label="20H"></option>
        <option value="79200000" label="22H"></option>
        <option value="86400000" label="24H"></option>
        <!--<option value="0" label="00h"></option>
        <option value="6" label="06h">2</option>
        <option value="12" label="12h"></option>
        <option value="18" label="18h"></option>
        <option value="24" label="24h"></option>
        <option value="30" label="30h"></option>
        <option value="36" label="36h"></option>
        <option value="42" label="42h"></option>
        <option value="48" label="48h"></option>
        <option value="54" label="54h"></option>
        <option value="60" label="60h"></option>-->
      </datalist>
      <div id="time-controller" class="vertical center">
        <button @click="playTime">Play</button>
        <button @click="pauseTime">Pause</button>
<!--        <button>Stop</button>-->
        <button @click="resetTime">Reset</button>
      </div>
      <div id="time-info">
        2021-09-01 12:00 / 2021-09-02 12:00
      </div>
    </div>
  </div>
</template>

<style scoped>

input[type=radio] {
  display: none;
  opacity: 0.5;
}
span {
  display: inline-block;
  padding: 6px 12px;
  color: #a4a4a4;
  /*border: 1px solid #c4c4c4;*/
  background-color: #e0e0e0;
  border-radius: 20px;
  cursor: pointer;
  font-size: 9px;
  font-weight: bold;
}
label > input[type="radio"]:checked+span {
  /*border: 1px solid #444444;*/
  color: #444444;
  background-color: #c9c9c9;
  /*font-weight: bold;*/
}
label > input[type="radio"]:hover+span {

}


#time-slider {
  width: calc(100vw - 40px);
  height: auto;
  bottom: 30px;
  padding: 15px;
}
#time-slider input[type=range] {
  width: 100%;
  appearance: none;
  -webkit-appearance: none;
  border: 1px solid #c4c4c4;
  border-radius: 50px;
  height: 12px;
  vertical-align: middle;
  background-color: #e5e5e5;
}

#time-slider input[type=range]::-webkit-slider-thumb {
  appearance: none;
  -webkit-appearance: none;
  border: 1px solid #7c7c7c;
  border-radius: 50%;
  width: 16px; height: 16px;
  transform: translate(0, -6px);
  vertical-align: middle;
  background: #b6b6b6;
}
#time-slider input[type=range]::-ms-track {
  background: transparent;
  border-color: transparent;
  color: transparent;
}

datalist#tick-marks {
  display: grid;
  grid-auto-flow: column;
  text-align: center;
  font-size: 9px;
  margin-top: 3px;
}
datalist#tick-marks > option {
  text-align: left;
}
datalist#tick-marks > option:last-child {
  position: absolute;
  right: 0;
  transform: translate(-50%, 0);
}

#speed-controller {
  height: auto;
  display: inline-block;
}

#time-controller {
  margin-top: 10px;
  position: relative;
  text-align: center;
}
#time-info {
  position: absolute;
  left: 15px;
  bottom: 15px;
  border-radius: 8px;
  background-color: #f1f1f1;
  padding: 8px 20px;
  font-size: 11px;
  color: #777777;
}
</style>
