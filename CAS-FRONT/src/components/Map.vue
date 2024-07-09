import "externals/cesium/Widget/Widget.css";

<script setup lang="ts">
  import {onMounted, ref} from "vue";
  const magoInstance = ref<any>();
  const viewer = ref<any>();

  /*defineProps<{
    msg: string
  }>()*/

  onMounted(() => {
    console.log('[MapComponent] Mounted');
    const options : any = {};
    options.infoBox = false;
    options.navigationHelpButton = false;
    options.selectionIndicator = false;
    options.homeButton = false;
    options.fullscreenButton = false;
    options.geocoder = false;
    options.baseLayerPicker = false;

    /* @ts-ignore */

    const newMagoInstance = new Mago3D.Mago3d("mago3dContainer", {}, {loadend: () => {}}, options);
    magoInstance.value = newMagoInstance;
    viewer.value = newMagoInstance.getViewer()

    console.log('[MapComponent] mago3dInstance', magoInstance);
    console.log('[MapComponent] viewer', viewer);
  });

  const getViewer = () => {
    return viewer.value;
  }

  const getMagoInstance = () => {
    return magoInstance.value;
  }

  defineExpose({
    getViewer,
    getMagoInstance
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