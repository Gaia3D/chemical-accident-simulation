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
  timeScale : 600000,
  nowUnixTimeMilisec : 0,
  minUnixTimeMilisec : 0,
  maxUnixTimeMilisec : 86400000,
  startUnixTimeMilisec : 0,
  isPlaying : false
});

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

const play = () => {
  const animationTimeController = getAnimationTimeController();

  if (playIntervalEvent !== undefined) {
    clearInterval(playIntervalEvent);
    playIntervalEvent = undefined;
  }

  playIntervalEvent = setInterval(() => {
    let nowUnixTimeMilisec = parseInt(timeTable.value.nowUnixTimeMilisec);
    let remainValue = nowUnixTimeMilisec % timeTable.value.timeScale;
    if (remainValue === 0) {
      timeTable.value.nowUnixTimeMilisec = parseInt(timeTable.value.nowUnixTimeMilisec) + timeTable.value.timeScale;
    } else {
      timeTable.value.nowUnixTimeMilisec = parseInt(timeTable.value.nowUnixTimeMilisec) + (timeTable.value.timeScale - remainValue);
    }
    setTime();

    if (timeTable.value.nowUnixTimeMilisec > 86400000) {
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
}


const setTime = () => {
  const animationTimeController = getAnimationTimeController();
  const startMilisec = animationTimeController._animationStartUnixTimeMilisec;
  animationTimeController._currentUnixTimeMilisec = startMilisec + parseInt(timeTable.value.nowUnixTimeMilisec);

  let startDateTime = new Date(startMilisec);
  let currentDateTime = new Date(animationTimeController._currentUnixTimeMilisec);
  let endDateTime = new Date(startMilisec + 86400000);

  const timeInfo = toTimeFormat(currentDateTime);
  const endTimeInfo = toTimeFormat(endDateTime);
  let startJulianDate = Cesium.JulianDate.fromIso8601(startDateTime.toISOString())
  let currentJulianDate = Cesium.JulianDate.fromIso8601(currentDateTime.toISOString())
  let endJulianDate = Cesium.JulianDate.fromIso8601(endDateTime.toISOString())

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

const toTimeFormat = (date: Date) => {
  return `${date.getFullYear()}-${paddingZero(date.getMonth() + 1)}-${paddingZero(date.getDate())} ${paddingZero(date.getHours())}:${paddingZero(date.getMinutes())}:${paddingZero(date.getSeconds())}`;
}

const paddingZero = (num: number) => {
  return num < 10 ? `0${num}` : `${num}`;
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
          <input id="speed-1" type="radio" name="input-format" value="10" @click="setTimeScale(600000)" checked/>
          <span>10분</span>
        </label>
        <label>
          <input id="speed-2" type="radio" name="input-format" value="30"  @click="setTimeScale(1800000)"/>
          <span>30분</span>
        </label>
        <label>
          <input id="speed-3" type="radio" name="input-format" value="60"  @click="setTimeScale(3600000)"/>
          <span>1시간</span>
        </label>
        <label>
          <input id="speed-4" type="radio" name="input-format" value="180"  @click="setTimeScale(10800000)"/>
          <span>3시간</span>
        </label>
        <label>
          <input id="speed-5" type="radio" name="input-format" value="360"  @click="setTimeScale(21600000)"/>
          <span>6시간</span>
        </label>
      </div>
    </div>
    <div id="time-slider" class="layer">
      <input type="range" step="600000" min="0" max="86400000" value="1000" v-model="timeTable.nowUnixTimeMilisec" id="myRange" list="tick-marks" @change="setTime" @input="setTime">
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
      </datalist>
      <div id="time-controller" class="vertical center">
<!--        <button @click="playTime">
          <img src="/src/assets/images/icons/back.png" alt="play button"/>
        </button>-->
        <button @click="play" v-show="!timeTable.isPlaying">
          <img src="/src/assets/images/icons/play.png" alt="play button"/>
        </button>
        <button @click="pause" v-show="timeTable.isPlaying">
          <img src="/src/assets/images/icons/pause.png" alt="pause button"/>
        </button>
        <button @click="stop">
          <img src="/src/assets/images/icons/stop.png" alt="reset button"/>
        </button>
<!--        <button @click="playTime">
          <img src="/src/assets/images/icons/forward.png" alt="play button"/>
        </button>-->
        <!--        <button @click="playTime" v-show="timeTable.isPlaying">Play</button>
                <button @click="pauseTime" v-show="!timeTable.isPlaying">Pause</button>
        &lt;!&ndash;        <button>Stop</button>&ndash;&gt;
                <button @click="resetTime">Reset</button>-->

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
  padding: 0px 8px;
}
datalist#tick-marks > option {
  /*text-align: left;*/
  transform: translate(-50%, 0);
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
#time-controller button {
  width: 36px;
  height: 36px;
  padding: 0;
}
#time-controller button > img {
  width: 100%;
  height: 100%;
  filter: brightness(0) saturate(100%) invert(43%) sepia(0%) saturate(80%) hue-rotate(162deg) brightness(86%) contrast(91%);
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
  font-family: monospace;
}
</style>
