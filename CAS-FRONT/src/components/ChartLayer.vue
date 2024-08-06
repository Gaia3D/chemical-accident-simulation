<script setup lang="ts">
import {onMounted} from "vue";
import "../map-custom.css";

/* @ts-ignore */
const Cesium = window.Cesium;
/* @ts-ignore */
const Mago3D = window.Mago3D;

const props = defineProps<{
  transferViewer: any;
}>();

onMounted(async () => {
  console.log('[MainComponent] Mounted Chart Layer Component');
});

const getViewer = () => {
  return props.transferViewer.viewer;
}

</script>

<template>
  <div id="chart-layer" class="layer left top">
    chart-layer
  </div>
</template>

<style scoped>
#chart-layer {
  width: calc(100vw - 40px);
}
</style>
