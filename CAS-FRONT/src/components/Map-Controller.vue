<script setup lang="ts">
import "../map-custom.css";
import {ref} from "vue";
import {onCompassInformation} from "../features/magoCameraInformation.ts";

/* @ts-ignore */
const Cesium = window.Cesium;
/* @ts-ignore */
const Mago3D = window.Mago3D;

const props = defineProps<{
  transferViewer: any;
}>();

const interval = setInterval(() => {
  if (getViewer() !== undefined) {
    clearInterval(interval)
    setCompass()
  }
}, 1000)

const cameraToolInfo = ref({
  longitude: 0,
  latitude: 0,
  height: 0,
  heading: 360,
  compassDirection: 360,
  compass: 'N',
});

const resetDirection = () => {
  const viewer = getViewer()
  viewer.camera.flyTo({
    destination: viewer.camera.positionWC,
    duration: 1.0,
    orientation: {
      heading: 0,
      pitch: viewer.camera.pitch,
      roll: viewer.camera.roll
    }
  })
}

const flyTo = (lon: number, lat: number, height: number, duration: number = 1) => {
  const viewer = getViewer()
  viewer.camera.flyTo({
    destination: Cesium.Cartesian3.fromDegrees(lon, lat, height),
    duration: duration
  });
}

const setCompass = () => {
  const viewer = getViewer();
  onCompassInformation(viewer, (heading : number) => {
    const cameraInfo = cameraToolInfo.value;
    cameraInfo.heading = parseFloat(heading.toFixed(2));
    cameraInfo.compass = getCardinalDirection(heading);
    cameraInfo.compassDirection = -heading;
  });
}

const getCardinalDirection = (angle : number) => {
  const directions = ['N', 'NE', 'E', 'SE', 'S', 'SW', 'W', 'NW'];
  return directions[Math.round(angle / 45) % 8];
}

const zoomInCamera = () => {
  const viewer = getViewer();
  const ellipsoid = viewer.scene.globe.ellipsoid
  const camera = viewer.camera
  const position = camera.position
  const height = ellipsoid.cartesianToCartographic(position).height
  const factor = height / 2
  camera.zoomIn(factor)
}

const zoomOutCamera = () => {
  const viewer = getViewer();
  const ellipsoid = viewer.scene.globe.ellipsoid
  const camera = viewer.camera
  const position = camera.position
  const height = ellipsoid.cartesianToCartographic(position).height
  const factor = height / 2
  camera.zoomOut(factor)
}

const getInitialPosition = () => {
  return props.transferViewer.initPosition;
}

const getViewer = () => {
  return props.transferViewer.viewer;
}

defineExpose({
  flyTo,
})

</script>

<template>
  <div class="direction-controller layer vertical left">
    <div class="compass" title="Compass" @click="resetDirection" @dblclick="flyTo(getInitialPosition().lon, getInitialPosition().lat, getInitialPosition().height)">
      <img src="../assets/images/icons/compass.png" class="icon-48 margin-4" alt="compass" title="compass" :style="{ transform: 'rotate('+ cameraToolInfo.compassDirection + 'deg)'}">
    </div>
  </div>
  <div>
    <div class="zoom-controller layer horizontal left">
      <button title="Zoom In" @click="zoomInCamera">
        <img src="../assets/images/icons/plus.png" alt="">
      </button>
      <button title="Zoom Out" @click="zoomOutCamera">
        <img src="../assets/images/icons/minus.png" alt="">
      </button>
    </div>
  </div>
</template>

<style scoped>
/* Zoom */
div.zoom-controller {
  padding: 6px;
  display: inline-block;
}
div.zoom-controller > button {
  width: 28px;
  height: 28px;
  padding: 5px;
  border: none;
  margin-bottom: 5px;
  cursor: pointer;
  overflow: hidden;
  vertical-align: bottom;
}
div.zoom-controller > button:last-child {
  margin-bottom: 0;
}
div.zoom-controller > button img {
  width: 100%;
  height: 100%;
}

/* Compass */
div.direction-controller {
  padding: 8px;
  display: inline-block;
}
div.compass {
  width: 40px;
  height: 40px;
  padding: 0;
  background-color: transparent;
  cursor: pointer;
  overflow: hidden;
  vertical-align: bottom;
}
div.compass img {
  width: 100%;
  height: 100%;
  opacity: 0.8;
}
</style>
