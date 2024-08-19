import "externals/cesium/Widget/Widget.css";

<script setup lang="ts">
  import {onMounted, ref} from "vue";

  const props = defineProps<{
    initOptions: any;
  }>();

  /* @ts-ignore */
  const Cesium = window.Cesium;
  /* @ts-ignore */
  const Mago3D = window.Mago3D;

  const magoInstance = ref<any>();
  const cesiumViewer = ref<any>();

  onMounted(() => {
    console.log('[MapComponent] Mounted');
    Cesium.Ion.defaultAccessToken = import.meta.env.VITE_CESIUM_ION_TOKEN;
    const newMagoInstance = new Mago3D.Mago3d("mago3dContainer", {}, {loadend: () => {}}, props.initOptions);
    magoInstance.value = newMagoInstance;
    cesiumViewer.value = newMagoInstance.getViewer()
    cesiumViewer.value.scene.globe.depthTestAgainstTerrain = false;
    cesiumViewer.value.scene.globe.enableLighting = false;
    console.log('[MapComponent] mago3dInstance', magoInstance);
    console.log('[MapComponent] viewer', cesiumViewer);
  });

  const getViewer = () => {
    return cesiumViewer.value;
  }

  const getMagoInstance = () => {
    return magoInstance.value;
  }

  defineExpose({
    getViewer,
    getMagoInstance,
  });
</script>

<template>
  <div id="mago3dContainer"></div>
</template>

<style scoped>
div#mago3dContainer {
  width: 100vw;
  height: 100vh;
  position: absolute;
  left: 0;
  top: 0;
}
</style>