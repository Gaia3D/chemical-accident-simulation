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

  const omsBuildingsTileset = ref<any>();
  const cesiumWorldTerrain = ref<any>();

  onMounted(() => {
    console.log('[MapComponent] Mounted');
    Cesium.Ion.defaultAccessToken = import.meta.env.VITE_CESIUM_ION_TOKEN;
    const newMagoInstance = new Mago3D.Mago3d("mago3dContainer", {}, {loadend: () => {}}, props.initOptions);
    magoInstance.value = newMagoInstance;
    cesiumViewer.value = newMagoInstance.getViewer()
    console.log('[MapComponent] mago3dInstance', magoInstance);
    console.log('[MapComponent] viewer', cesiumViewer);
  });

  const changeGlobeColor = (cssColor: string = '#000000') => {
    const viewer = cesiumViewer.value
    const scene = viewer.scene
    scene.globe.baseColor = Cesium.Color.fromCssColorString(cssColor)
  }

  const changeImageryLayer = (url: string) => {
    const viewer = cesiumViewer.value
    const scene = viewer.scene
    const layers = scene.imageryLayers

    const duplicateLayer = layers._layers.find((layer: any) => {
      return layer.imageryProvider.url.startsWith(url)
    })

    if (duplicateLayer === undefined) {
      const osm = new Cesium.OpenStreetMapImageryProvider({url: url})
      layers.addImageryProvider(osm)
    } else {
      hideOtherImageryLayers(url)
      duplicateLayer.show = true
    }
  }

  const changeVWorldImageryLayer = (type: string, extension: string) => {
    const viewer = cesiumViewer.value
    const scene = viewer.scene
    const layers = scene.imageryLayers

    const VWORLD_KEY = import.meta.env.VITE_VWORLD_ACCESS_TOKEN;
    const minLevel = 5
    const maxLevel = 19

    const protocol = location.protocol === 'https:' ? 'https' : 'http'
    const options = {
      url: `${protocol}://api.vworld.kr/req/wmts/1.0.0/${VWORLD_KEY}/${type}/{TileMatrix}/{TileRow}/{TileCol}.${extension}`,
      layer: 'Base',
      style: 'default',
      maximumLevel: maxLevel,
      tileMatrixSetID: 'default028mm'
    }

    const duplicateLayer = layers._layers.find((layer: any) => {
      return layer.imageryProvider.url.startsWith(options.url)
    })

    if (duplicateLayer === undefined) {
      const imageryProvider = new Cesium.WebMapTileServiceImageryProvider(options)
      const imageryLayer = new Cesium.ImageryLayer(imageryProvider, {
        show: true,
        minimumTerrainLevel: minLevel
      });
      layers.add(imageryLayer)
    } else {
      hideOtherImageryLayers(options.url)
      duplicateLayer.show = true
    }
  }

  const hideOtherImageryLayers = (url: string) => {
    const viewer = cesiumViewer.value
    const scene = viewer.scene
    const layers = scene.imageryLayers
    layers._layers.forEach((layer: any) => {
      if (layer.imageryProvider.url !== url) {
        layer.show = false
      }
    })
  }

  const hideAllImageryLayers = () => {
    const viewer = cesiumViewer.value
    const scene = viewer.scene
    const layers = scene.imageryLayers
    layers._layers.forEach((layer: any) => {
      layer.show = false
    })
  }

  const removeAllImageryLayers = () => {
    const viewer = cesiumViewer.value
    const scene = viewer.scene
    const layers = scene.imageryLayers
    layers.removeAll()
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

    // 0.85 0.82 0.79
    osmBuildingsTileset.colorBlendMode = Cesium.Cesium3DTileColorBlendMode.HIGHLIGHT;
    osmBuildingsTileset.style = new Cesium.Cesium3DTileStyle({
      color: "color('#d9d0c9')",
      backgroundColor: "color('#ffffff')",
    });
    omsBuildingsTileset.value = osmBuildingsTileset;

    omsBuildingsTileset.value.customShader = new Cesium.CustomShader({
      lightingModel: Cesium.LightingModel.PBR,
      fragmentShaderText: `
        void fragmentMain(FragmentInput fsInput, inout czm_modelMaterial material)
        {
            material.diffuse = vec3(0.8509, 0.82, 0.79);
        }
        `,
    });

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
    hideAllImageryLayers,
    hideOtherImageryLayers,
    removeAllImageryLayers,
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