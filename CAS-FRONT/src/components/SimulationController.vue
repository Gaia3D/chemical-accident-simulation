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


onMounted(async () => {
  console.log('[SimulationController] Mounted Slider Component');

});

const toggleShadow = () => {
  const viewer = getViewer();
  if (viewer.scene.shadowMap.enabled) {
    viewer.scene.shadowMap.enabled = false
    viewer.scene.globe.enableLighting = false;
  } else {
    viewer.scene.shadowMap.enabled = true
    viewer.scene.globe.enableLighting = true;
    viewer.scene.shadowMap.darkness = 0.5
  }
}

const initDome = () => {
  const viewer = getViewer();
  const range = 1000
  const innerFactor = 0.3;
  const outterFactor = 0.5;
  viewer.entities.add({
    name: "Dome",
    position: Cesium.Cartesian3.fromDegrees(126.65403123232736, 36.90329299539047),
    ellipsoid: {
      radii: new Cesium.Cartesian3(range, range, range),
      maximumCone: Cesium.Math.PI_OVER_TWO,
      material: Cesium.Color.RED.withAlpha(innerFactor),
      outline: true,
      outlineColor: Cesium.Color.RED.withAlpha(outterFactor),
    },
  });

  viewer.entities.add({
    name: "Dome",
    position: Cesium.Cartesian3.fromDegrees(126.65403123232736, 36.90329299539047),
    ellipsoid: {
      radii: new Cesium.Cartesian3(range * 2, range * 2, range * 2),
      maximumCone: Cesium.Math.PI_OVER_TWO,
      material: Cesium.Color.DARKORANGE.withAlpha(innerFactor),
      outline: true,
      outlineColor: Cesium.Color.DARKORANGE.withAlpha(outterFactor),
    },
  });

  viewer.entities.add({
    name: "Dome",
    position: Cesium.Cartesian3.fromDegrees(126.65403123232736, 36.90329299539047),
    ellipsoid: {
      radii: new Cesium.Cartesian3(range * 3, range * 3, range * 3),
      maximumCone: Cesium.Math.PI_OVER_TWO,
      material: Cesium.Color.GREEN.withAlpha(innerFactor),
      outline: true,
      outlineColor: Cesium.Color.GREEN.withAlpha(outterFactor),
    },
  });
}

const getViewer = () => {
  return props.transferViewer.viewer;
}

</script>

<template>
  <div id="simulation-layer" class="layer left top horizontal">
    <h3>사고물질 레이어</h3>
    <div class="switch-wrapper">
      <h4>사고물질 농도</h4>
      <label>
        <input type="radio" name="accident-group" checked>
        <span></span>
      </label>
    </div>
    <div class="switch-wrapper">
      <h4>사고물질 노출량</h4>
      <label>
        <input type="radio" name="accident-group">
        <span></span>
      </label>
    </div>
    <div class="switch-wrapper">
      <h4>사고물질 급성위해도</h4>
      <label>
        <input type="radio" name="accident-group">
        <span></span>
      </label>
    </div>
    <h3>예상피해자 레이어</h3>
    <div class="switch-wrapper">
      <h4>예상 피해자 분포</h4>
      <label>
        <input type="radio" name="victim-group" checked>
        <span></span>
      </label>
    </div>
    <div class="switch-wrapper">
      <h4>예상 피해자 이동동선</h4>
      <label>
        <input type="radio" name="victim-group">
        <span></span>
      </label>
    </div>

    <h3>기타 레이어</h3>
    <div class="switch-wrapper">
      <h4>건물 레이어</h4>
      <label>
        <input type="checkbox" name="victim-group" checked>
        <span></span>
      </label>
    </div>
    <div class="switch-wrapper">
      <h4>지형 레이어</h4>
      <label>
        <input type="checkbox" name="victim-group">
        <span></span>
      </label>
    </div>
    <div class="switch-wrapper">
      <h4>반경 레이어(동심원)</h4>
      <label>
        <input type="checkbox" name="victim-group" @click="initDome()">
        <span></span>
      </label>
    </div>
    <div class="switch-wrapper">
      <h4>그림자 효과</h4>
      <label>
        <input type="checkbox" name="victim-group" @click="toggleShadow()">
        <span></span>
      </label>
    </div>
  </div>
  <div id="legend-layer" class="layer left top horizontal">
    <h3>범례</h3>
    <div class="legend">

    </div>
  </div>
</template>

<style scoped>

div#simulation-layer {
  width: 200px;
  display: inline-block;
  padding-bottom: 12px;
}
div#simulation-layer h3 {
  font-size: 1.0em;
  margin: 8px 5px;
  padding: 0;
}

.switch-wrapper {
  display: inline-block;
  width: 185px;
}
.switch-wrapper > h4 {
  min-width: 135px;
  display: inline-block;
  text-align: left;
  vertical-align: middle;
  font-size: 0.9em;
  font-weight: normal;
  margin: 0 5px;
}

div#legend-layer {
  width: 100px;
  display: inline-block;
}
div#legend-layer h3 {
  font-size: 1.0em;
  margin: 5px;
  padding: 0;
}

div.legend {
  height: 100px;
  width: 20px;
  background: linear-gradient(red, yellow, green, blue);
  border: 1px solid #464646;
  display: inline-block;
  margin: 5px;
}
</style>
