<script setup lang="ts">
import {onMounted, ref} from "vue";
import "../map-custom.css";
import {store} from "../store/store.ts";

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
    day : 8,
    hour : 11,
    minute : 0,
    second : 0
  },
  timeScale : 600000,
  nowUnixTimeMilisec : 0,
  minUnixTimeMilisec : 0,
  maxUnixTimeMilisec : 172800000,
  startUnixTimeMilisec : 0,
  isPlaying : false
});

const percentage = ref({'width' : '0%'});

const startTimeAnimation = (timeScale: number) => {
  const animationTimeController = getAnimationTimeController();
  timeTable.value.nowUnixTimeMilisec = 0;
  animationTimeController._currentUnixTimeMilisec = animationTimeController._animationStartUnixTimeMilisec;
  setTimeScale(timeScale);
  setTime(true);
  play();
}

const setTimeScale = (timeScale: number) => {
  const animationTimeController = getAnimationTimeController();
  animationTimeController._timeScale = timeScale;
  timeTable.value.timeScale = timeScale;
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
  }
  return magoManager.animationTimeController;
}

let playIntervalEvent: any = undefined;

const prev = () => {
  const nowUnixTimeMilisec = parseInt(timeTable.value.nowUnixTimeMilisec);
  //let timeScale = parseInt(timeTable.value.timeScale); // timeScale is not used
  let timeScale = 600000;
  if (nowUnixTimeMilisec <= 3600000) {
    timeScale = 60000;
  }
  let remainValue = nowUnixTimeMilisec % timeScale;
  if (remainValue === 0) {
    timeTable.value.nowUnixTimeMilisec = parseInt(timeTable.value.nowUnixTimeMilisec) - timeScale;
  } else {
    timeTable.value.nowUnixTimeMilisec = parseInt(timeTable.value.nowUnixTimeMilisec) - (remainValue);
  }
  setTime();
}

const next = () => {
  const nowUnixTimeMilisec = parseInt(timeTable.value.nowUnixTimeMilisec);
  //let timeScale = parseInt(timeTable.value.timeScale); // timeScale is not used
  let timeScale = 600000;
  if (nowUnixTimeMilisec < 3600000) {
    timeScale = 60000;
  }
  let remainValue = nowUnixTimeMilisec % timeScale;
  if (remainValue === 0) {
    timeTable.value.nowUnixTimeMilisec = parseInt(timeTable.value.nowUnixTimeMilisec) + timeScale;
  } else {
    timeTable.value.nowUnixTimeMilisec = parseInt(timeTable.value.nowUnixTimeMilisec) + (timeScale - remainValue);
  }
  setTime();
}

const play = () => {
  const animationTimeController = getAnimationTimeController();

  if (playIntervalEvent !== undefined) {
    clearInterval(playIntervalEvent);
    playIntervalEvent = undefined;
  }

  playIntervalEvent = setInterval(() => {
    let nowUnixTimeMilisec = parseInt(timeTable.value.nowUnixTimeMilisec);
    let timeScale = parseInt(timeTable.value.timeScale);
    let remainValue = nowUnixTimeMilisec % timeScale;
    if (remainValue === 0) {
      timeTable.value.nowUnixTimeMilisec = parseInt(timeTable.value.nowUnixTimeMilisec) + timeScale;
    } else {
      timeTable.value.nowUnixTimeMilisec = parseInt(timeTable.value.nowUnixTimeMilisec) + (timeScale - remainValue);
    }
    setTime(false);

    if (timeTable.value.nowUnixTimeMilisec > timeTable.value.maxUnixTimeMilisec) {
      clearInterval(playIntervalEvent);
      playIntervalEvent = undefined;
      timeTable.value.isPlaying = false;
      timeTable.value.nowUnixTimeMilisec = 0;
      animationTimeController._currentUnixTimeMilisec = animationTimeController._animationStartUnixTimeMilisec;
    }
  }, 1000)

  timeTable.value.isPlaying = true;
}

const pause = () => {
  if (playIntervalEvent !== undefined) {
    clearInterval(playIntervalEvent);
    playIntervalEvent = undefined;
  }
  timeTable.value.isPlaying = false;
}

const stop = () => {
  pause();
  const animationTimeController = getAnimationTimeController();
  timeTable.value.nowUnixTimeMilisec = 0;
  animationTimeController._currentUnixTimeMilisec = animationTimeController._animationStartUnixTimeMilisec;
  setTime();
}

const setSliderTime = () => {
  const fakeTime = store.getChemicalAccidentInfo().fakeTime;
  const fakeDate = new Date();
  fakeDate.setFullYear(fakeTime.year);
  fakeDate.setMonth(fakeTime.month - 1);
  fakeDate.setDate(fakeTime.day);
  fakeDate.setHours(fakeTime.hour);
  fakeDate.setMinutes(fakeTime.minute);
  fakeDate.setSeconds(fakeTime.second);
  let fakeStartUnixTimeMilisec = fakeDate.getTime();
  let fakeStartDateTime = new Date(fakeStartUnixTimeMilisec + parseInt(timeTable.value.nowUnixTimeMilisec));
  let fakeEndDateTime = new Date(fakeStartUnixTimeMilisec + timeTable.value.maxUnixTimeMilisec);
  const timeInfo = toTimeFormat(fakeStartDateTime);
  const endTimeInfo = toTimeFormat(fakeEndDateTime);
  const timeInfoElement = document.getElementById('time-info');
  if (timeInfoElement) {
    timeInfoElement.innerText = `${timeInfo} / ${endTimeInfo}`;
  }
}

const setTime = (snap = false) => {
  const animationTimeController = getAnimationTimeController();
  let nowUnixTimeMilisec = parseInt(timeTable.value.nowUnixTimeMilisec);
  let scale = parseInt(timeTable.value.timeScale);
  if (nowUnixTimeMilisec <= 3600000) {
    scale = 60000;
  }

  if (snap) {
    let remainValue = nowUnixTimeMilisec % scale;
    if (remainValue === 0) {
      timeTable.value.nowUnixTimeMilisec = parseInt(timeTable.value.nowUnixTimeMilisec);
    } else {
      if (remainValue > scale / 2) {
        timeTable.value.nowUnixTimeMilisec = parseInt(timeTable.value.nowUnixTimeMilisec) + (scale - remainValue);
      } else {
        timeTable.value.nowUnixTimeMilisec = parseInt(timeTable.value.nowUnixTimeMilisec) - remainValue;
      }
    }
  }

  percentage.value.width = `${(parseInt(timeTable.value.nowUnixTimeMilisec) / timeTable.value.maxUnixTimeMilisec) * 100}%`;

  const startMilisec = animationTimeController._animationStartUnixTimeMilisec;
  animationTimeController._currentUnixTimeMilisec = startMilisec + parseInt(timeTable.value.nowUnixTimeMilisec);
  let startDateTime = new Date(startMilisec);
  let currentDateTime = new Date(animationTimeController._currentUnixTimeMilisec);
  let endDateTime = new Date(startMilisec + timeTable.value.maxUnixTimeMilisec);

  //const timeInfo = toTimeFormat(currentDateTime);
  //const endTimeInfo = toTimeFormat(endDateTime);
  let startJulianDate = Cesium.JulianDate.fromIso8601(startDateTime.toISOString())
  let currentJulianDate = Cesium.JulianDate.fromIso8601(currentDateTime.toISOString())
  let endJulianDate = Cesium.JulianDate.fromIso8601(endDateTime.toISOString())

  const viewer = getViewer();
  const clock = viewer.clock
  clock.startTime = startJulianDate
  clock.stopTime = endJulianDate
  clock.currentTime = currentJulianDate
  clock.multiplier = 1

  setSliderTime();
}

const toTimeFormat = (date: Date) => {
  return `${date.getFullYear()}-${paddingZero(date.getMonth() + 1)}-${paddingZero(date.getDate())} ${paddingZero(date.getHours())}:${paddingZero(date.getMinutes())}:${paddingZero(date.getSeconds())}`;
}

const paddingZero = (num: number) => {
  return num < 10 ? `0${num}` : `${num}`;
}

onMounted(async () => {
  //console.log('[MainComponent] Mounted Slider Component');
  setSliderTime();
});

const getViewer = () => {
  return props.transferViewer.viewer;
}

</script>

<template>
  <div id="speed-controller" class="">
    <div class="vertical">
      <div class="speed-label">애니메이션 보기 : </div>
      <label>
        <input id="speed-1" type="radio" name="input-format" value="10" @click="startTimeAnimation(600000)" checked/>
        <span>10분</span>
      </label>
      <label>
        <input id="speed-2" type="radio" name="input-format" value="30"  @click="startTimeAnimation(1800000)"/>
        <span>30분</span>
      </label>
      <label>
        <input id="speed-3" type="radio" name="input-format" value="60"  @click="startTimeAnimation(3600000)"/>
        <span>1시간</span>
      </label>
      <label>
        <input id="speed-4" type="radio" name="input-format" value="180"  @click="startTimeAnimation(10800000)"/>
        <span>3시간</span>
      </label>
      <label>
        <input id="speed-5" type="radio" name="input-format" value="360"  @click="startTimeAnimation(21600000)"/>
        <span>6시간</span>
      </label>
      <label v-show="!timeTable.isPlaying">
        <span @click="play">시작</span>
      </label>
      <label v-show="timeTable.isPlaying">
        <span @click="pause">멈춤</span>
      </label>
      <label>
        <span @click="stop">정지</span>
      </label>
    </div>
  </div>
  <div id="time-slider" class="layer">
    <input type="range" step="60000" min="0" max="172800000" value="1000" v-model="timeTable.nowUnixTimeMilisec" id="myRange" list="tick-marks" @change="setTime(true)" @input="setTime(true)">
    <div class="bar-wrap">
      <div class="bar" :style="percentage"></div>
    </div>
    <datalist id="tick-marks">
      <option value="0"        label="00H"></option>
      <option value="3600000"  label="01H">1</option>
      <option value="7200000"  label="02H">2</option>
      <option value="10800000" label="03H"></option>
      <option value="14400000" label="04H"></option>
      <option value="18000000" label="05H"></option>
      <option value="21600000" label="06H"></option>
      <option value="25200000" label="07H"></option>
      <option value="28800000" label="08H"></option>
      <option value="32400000" label="09H"></option>
      <option value="36000000" label="10H"></option>
      <option value="39600000" label="11H"></option>
      <option value="43200000" label="12H"></option>
      <option value="46800000" label="13H"></option>
      <option value="50400000" label="14H"></option>
      <option value="54000000" label="15H"></option>
      <option value="57600000" label="16H"></option>
      <option value="61200000" label="17H"></option>
      <option value="64800000" label="18H"></option>
      <option value="68400000" label="19H"></option>
      <option value="72000000" label="20H"></option>
      <option value="75600000" label="21H"></option>
      <option value="79200000" label="22H"></option>
      <option value="82800000" label="23H"></option>
      <option value="86400000" label="24H"></option>
      <option value="90000000" label="25H"></option>
      <option value="93600000" label="26H"></option>
      <option value="97200000" label="27H"></option>
      <option value="100800000" label="28H"></option>
      <option value="104400000" label="29H"></option>
      <option value="108000000" label="30H"></option>
      <option value="111600000" label="31H"></option>
      <option value="115200000" label="32H"></option>
      <option value="118800000" label="33H"></option>
      <option value="122400000" label="34H"></option>
      <option value="126000000" label="35H"></option>
      <option value="129600000" label="36H"></option>
      <option value="133200000" label="37H"></option>
      <option value="136800000" label="38H"></option>
      <option value="140400000" label="39H"></option>
      <option value="144000000" label="40H"></option>
      <option value="147600000" label="41H"></option>
      <option value="151200000" label="42H"></option>
      <option value="154800000" label="43H"></option>
      <option value="158400000" label="44H"></option>
      <option value="162000000" label="45H"></option>
      <option value="165600000" label="46H"></option>
      <option value="169200000" label="47H"></option>
      <option value="172800000" label="48H"></option>
    </datalist>
    <div id="time-controller" class="vertical center">
      <button @click="prev">
        <img src="/src/assets/images/icons/back.png" alt="play button"/>
      </button>
      <button @click="play" v-show="!timeTable.isPlaying">
        <img src="/src/assets/images/icons/play.png" alt="play button"/>
      </button>
      <button @click="pause" v-show="timeTable.isPlaying">
        <img src="/src/assets/images/icons/pause.png" alt="pause button"/>
      </button>
      <button @click="stop">
        <img src="/src/assets/images/icons/stop.png" alt="reset button"/>
      </button>
      <button @click="next">
        <img src="/src/assets/images/icons/forward.png" alt="play button"/>
      </button>
      <!--        <button @click="playTime" v-show="timeTable.isPlaying">Play</button>
              <button @click="pauseTime" v-show="!timeTable.isPlaying">Pause</button>
      &lt;!&ndash;        <button>Stop</button>&ndash;&gt;
              <button @click="resetTime">Reset</button>-->
    </div>
    <div id="time-info">
      2021-09-01 12:00 / 2021-09-02 12:00
    </div>
  </div>
</template>

<style scoped>

.speed-label {
  vertical-align: middle;
  font-size: 11px;
  font-weight: 400;
}
input[type=range] {
  cursor: pointer;
}

input[type=radio] {
  display: none;
  opacity: 0.5;
}
input + span {
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
span {
  display: inline-block;
  padding: 6px 12px;
  color: #444444;
  /*border: 1px solid #c4c4c4;*/
  background-color: #c9c9c9;
  border-radius: 20px;
  cursor: pointer;
  font-size: 9px;
  font-weight: bold;
}
span:hover {
  background-color: #eeeeee;
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
  width: calc(100vw);
  height: auto;
  bottom: 30px;
  padding: 15px;
  margin: 0 -20px -20px -20px;
  border-radius: 0;
}
#time-slider input[type=range] {
  width: 100%;
  appearance: none;
  -webkit-appearance: none;
  border: 1px solid #e2e2e2;
  border-radius: 50px;
  height: 10px;
  vertical-align: middle;
  background: rgb(210 210 210 / 25%);
  margin: 0;
}
#time-slider div.bar-wrap {
  border-radius: 50px;
  overflow: hidden;
  margin: -10px 2px 0;
}

#time-slider div.bar-wrap > div.bar {
  height: 10px;
  background-color: #007fff;
  border-radius: 50px 0 0 50px;
}

#time-slider input[type=range]::-webkit-slider-thumb {
  appearance: none;
  -webkit-appearance: none;
  border: 4px solid #007fff;
  border-radius: 50%;
  width: 16px; height: 16px;
  transform: translate(0, -6px);
  vertical-align: middle;
  background: #ffffff;
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
  padding: 0px 8px;
}
datalist#tick-marks > option {
  /*text-align: left;*/
  transform: translate(-50%, 0);
}
datalist#tick-marks > option:last-child {
  position: absolute;
  right: 0;
  transform: translate(35%, 0);
}

#speed-controller {
  height: auto;
  display: inline-block;
  position: absolute;
  right: 0;
  bottom: 0;
  margin-bottom: 0;
  z-index: 15;
}

#time-controller {
  margin-top: 10px;
  position: relative;
  text-align: center;
}
#time-controller button {
  width: 32px;
  height: 32px;
  padding: 2px;
}
#time-controller button > img {
  width: 100%;
  height: 100%;
  filter: brightness(0) saturate(100%) invert(43%) sepia(0%) saturate(80%) hue-rotate(162deg) brightness(86%) contrast(91%);
}

#time-info {
  position: absolute;
  left: 0px;
  bottom: 0px;
  border-radius: 8px;
  background-color: #f2f2f2;
  padding: 6px 20px;
  font-size: 11px;
  color: #777777;
  font-family: monospace;
}
</style>
