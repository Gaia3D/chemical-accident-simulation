<script setup lang="ts">
import Map from "./Map.vue";
import {onMounted, ref} from "vue";

const mapComponent = ref();
const viewer = ref();
const magoInstance = ref();

const printViewer = () => {
  console.log(viewer.value);
}

const printMagoInstance = () => {
  console.log(magoInstance.value);
}

onMounted(() => {
  console.log('[MainComponent] Mounted Main Component');
  viewer.value = mapComponent.value.getViewer();
  magoInstance.value = mapComponent.value.getMagoInstance();
});

</script>

<template>
  <div class="button-group">
    <button @click="printViewer">Print Viewer</button>
    <button @click="printMagoInstance">Print MagoInstance</button>
  </div>
  <Map ref="mapComponent"/>
</template>

<style scoped>
.button-group {
  font-size: 0.8em;
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 1000;
}
.button-group button {
  display: block;
  margin-bottom: 5px;
}
.button-group button:last-child {
  margin-bottom: 0;
}
</style>
