import "externals/cesium/Widget/Widget.css";

<script setup lang="ts">
  import {onMounted, ref} from "vue";

  const props = defineProps<{
    initOptions: any;
  }>();

  /* @ts-ignore */
  const Cesium = window.Cesium;
  const magoInstance = ref<any>();
  const cesiumViewer = ref<any>();

  const omsBuildingsTileset = ref<any>();
  const cesiumWorldTerrain = ref<any>();

  onMounted(() => {
    console.log('[MapComponent] Mounted');
    /* @ts-ignore */
    const newMagoInstance = new Mago3D.Mago3d("mago3dContainer", {}, {loadend: () => {}}, props.initOptions);
    magoInstance.value = newMagoInstance;
    cesiumViewer.value = newMagoInstance.getViewer()

    Cesium.Ion.defaultAccessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiI5OTEzYmExNS1lNjI1LTQxNWUtOGFkNS0zM2U0Y2FkMjA4OGMiLCJpZCI6MTY3ODk5LCJpYXQiOjE2OTUzNTYxMTl9.7Alxx-4hzrnSBkbz1DvClO8eQX38sBVzTKXYI-lI8_g"

    console.log('[MapComponent] mago3dInstance', magoInstance);
    console.log('[MapComponent] viewer', cesiumViewer);
  });

  const changeImageryLayer = (url: string) => {
    const viewer = cesiumViewer.value
    const scene = viewer.scene
    const layers = scene.imageryLayers
    layers.removeAll()

    /* @ts-ignore */
    const osm = new Cesium.OpenStreetMapImageryProvider({url: url})
    layers.addImageryProvider(osm)
    return osm
  }

  const changeGlobeColor = (cssColor: string = '#000000') => {
    const viewer = cesiumViewer.value
    const scene = viewer.scene
    scene.globe.baseColor = Cesium.Color.fromCssColorString(cssColor)
  }

  const removeAllImageryLayers = () => {
    const viewer = cesiumViewer.value
    const scene = viewer.scene
    const layers = scene.imageryLayers
    layers.removeAll()
  }

  const changeVWorldImageryLayer = (type: string, extension: string) => {
    const viewer = cesiumViewer.value
    const scene = viewer.scene
    const layers = scene.imageryLayers

    const VWORD_KEY = props.initOptions.vworldToken
    const minLevel = 5
    const maxLevel = 19

    const protocol = location.protocol === 'https:' ? 'https' : 'http'
    const options = {
      url: `${protocol}://api.vworld.kr/req/wmts/1.0.0/${VWORD_KEY}/${type}/{TileMatrix}/{TileRow}/{TileCol}.${extension}`,
      layer: 'Base',
      style: 'default',
      maximumLevel: maxLevel,
      tileMatrixSetID: 'default028mm'
    }
    const imageryProvider = new Cesium.WebMapTileServiceImageryProvider(options)
    const imageryLayer = new Cesium.ImageryLayer(imageryProvider, {
      show: true,
      minimumTerrainLevel: minLevel
    });
    layers.add(imageryLayer)
  }

  const changeWorldTerrain = async () => {
    if (cesiumWorldTerrain.value) {
      return cesiumWorldTerrain.value;
    }
    const worldTerrain = await Cesium.createWorldTerrainAsync();
    cesiumWorldTerrain.value = worldTerrain;
    return worldTerrain;
  }

  const getOmsBuildingsTileset = async () => {
    if (omsBuildingsTileset.value) {
      return omsBuildingsTileset.value;
    }

    const osmBuildingsTileset = await Cesium.createOsmBuildingsAsync({
      enableShowOutline: false,
      showOutline: false,
    });
    osmBuildingsTileset.colorBlendMode = Cesium.Cesium3DTileColorBlendMode.REPLACE;
    osmBuildingsTileset.style = new Cesium.Cesium3DTileStyle({
      color: "color('#ffffff')",
    });
    omsBuildingsTileset.value = osmBuildingsTileset;
    return osmBuildingsTileset;
  }

  const getViewer = () => {
    return cesiumViewer.value;
  }

  const getMagoInstance = () => {
    return magoInstance.value;
  }

  defineExpose({
    getViewer,
    getMagoInstance,
    changeGlobeColor,
    changeImageryLayer,
    changeVWorldImageryLayer,
    changeWorldTerrain,
    getOmsBuildingsTileset,
    removeAllImageryLayers
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