<script setup lang="ts">
import "../map-custom.css";

/* @ts-ignore */
const Cesium = window.Cesium;
/* @ts-ignore */
const Mago3D = window.Mago3D;

const props = defineProps<{
  transferViewer: any;
}>();

const changeGlobeColor = (cssColor: string = '#000000') => {
  const viewer = getViewer()
  const scene = viewer.scene
  scene.globe.baseColor = Cesium.Color.fromCssColorString(cssColor)
}

const changeImageryLayer = (url: string) => {
  const viewer = getViewer()
  const scene = viewer.scene
  const layers = scene.imageryLayers

  const duplicateLayer = layers._layers.find((layer: any) => {
    if (layer.imageryProvider.url !== undefined) {
      return layer.imageryProvider.url.startsWith(url)
    }
  })

  if (duplicateLayer === undefined) {
    const osm = new Cesium.OpenStreetMapImageryProvider({url: url})
    layers.addImageryProvider(osm)
  } else {
    hideOtherImageryLayers(url)
    duplicateLayer.show = true
  }
}

const changeBingImageryLayer = async (mapStyle: any) => {
  const viewer = getViewer()
  const scene = viewer.scene
  const layers = scene.imageryLayers
  const mapStyleText = mapStyle === Cesium.IonWorldImageryStyle.AERIAL ? 'Aerial' : 'RoadOnDemand'

  const duplicateLayer = layers._layers.find((layer: any) => {
    const imageryProvider = layer.imageryProvider._imageryProvider;
    return imageryProvider instanceof Cesium.BingMapsImageryProvider && imageryProvider.mapStyle === mapStyleText
  })

  hideAllImageryLayers();
  if (duplicateLayer === undefined) {
    const bing = await Cesium.createWorldImageryAsync({style : mapStyle});
    layers.addImageryProvider(bing)
  } else {
    duplicateLayer.show = true
  }
}

const changeVWorldImageryLayer = (type: string, extension: string) => {
  const viewer = getViewer()
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
    if (layer.imageryProvider.url !== undefined) {
      return layer.imageryProvider.url.startsWith(options.url)
    }
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
  const viewer = getViewer()
  const scene = viewer.scene
  const layers = scene.imageryLayers
  layers._layers.forEach((layer: any) => {
    if (layer.imageryProvider.url !== url) {
      layer.show = false
    }
  })
}

const hideAllImageryLayers = () => {
  const viewer = getViewer()
  const scene = viewer.scene
  const layers = scene.imageryLayers
  layers._layers.forEach((layer: any) => {
    layer.show = false
  })
}

const removeAllImageryLayers = () => {
  const viewer = getViewer()
  const scene = viewer.scene
  const layers = scene.imageryLayers
  layers.removeAll()
}

const toggleOsmLayer = () => {
  changeGlobeColor('#edebe5')
  const protocol = location.protocol === 'https:' ? 'https' : 'http'
  changeImageryLayer(`${protocol}://a.tile.openstreetmap.org`);
}
const toggleBingLayer = async () => {
  changeGlobeColor('#f0ffff')
  await changeBingImageryLayer(Cesium.IonWorldImageryStyle.ROAD);
}
const toggleBingSatelliteLayer = async () => {
  changeGlobeColor('#9f9785')
  await changeBingImageryLayer(Cesium.IonWorldImageryStyle.AERIAL);
}
const toggleCartoMapLightLayer = () => {
  changeGlobeColor('#d2d8dd')
  const protocol = location.protocol === 'https:' ? 'https' : 'http'
  changeImageryLayer(`${protocol}://a.basemaps.cartocdn.com/light_all/`);
}
const toggleCartoMapDarkLayer = () => {
  changeGlobeColor('#090909')
  const protocol = location.protocol === 'https:' ? 'https' : 'http'
  changeImageryLayer(`${protocol}://a.basemaps.cartocdn.com/dark_all/`);
}
const toggleVWorldSatelliteLayer = () => {
  changeGlobeColor('#686b61');
  changeVWorldImageryLayer('Satellite', 'jpeg');
}
const toggleVWorldBaseLayer = () => {
  changeGlobeColor('#686b61');
  changeVWorldImageryLayer('Base', 'png');
}

const getViewer = () => {
  return props.transferViewer.viewer;
}

defineExpose({
  toggleOsmLayer,
  toggleBingLayer,
  toggleBingSatelliteLayer,
  toggleCartoMapLightLayer,
  toggleCartoMapDarkLayer,
  toggleVWorldSatelliteLayer,
  toggleVWorldBaseLayer,
  hideAllImageryLayers,
  removeAllImageryLayers
})

</script>

<template>
  <div class="map-controller layer vertical">
    <button @click="toggleOsmLayer" title="OpenStreetMap Layer">
      <img src="../assets/images/maps/osm.png" alt="OpenStreetMap Layer">
    </button>
    <button @click="toggleCartoMapLightLayer" title="CartoMap Light Layer">
      <img src="../assets/images/maps/carto-light.png" alt="CartoMap Light Layer">
    </button>
    <button @click="toggleCartoMapDarkLayer" title="CartoMap Dark Layer">
      <img src="../assets/images/maps/carto-dark.png" alt="CartoMap Dark Layer">
    </button>
    <button @click="toggleVWorldBaseLayer" title="VWorld Base Layer">
      <img src="../assets/images/maps/vworld-base.png" alt="VWorld Base Layer">
    </button>
    <button @click="toggleVWorldSatelliteLayer" title="VWorld Satellite Layer">
      <img src="../assets/images/maps/vworld-satellite.png" alt="VWorld Satellite Layer">
    </button>
    <button @click="toggleBingLayer" title="Bing Maps Layer">
      <img src="../assets/images/maps/bing.png" alt="Bing Maps Layer">
    </button>
    <button @click="toggleBingSatelliteLayer" title="Bing Satellite Layer">
      <img src="../assets/images/maps/bing-satellite.png" alt="Bing Satellite Layer">
    </button>
  </div>
</template>

<style scoped>
div.map-controller {
  padding: 6px;
  display: inline-block;
}
div.map-controller > button {
  width: 36px;
  height: 36px;
  padding: 0;
  border: none;
  background-color: transparent;
  cursor: pointer;
  overflow: hidden;
  vertical-align: bottom;
  margin-right: 5px;
}
div.map-controller > button:last-child {
  margin-right: 0;
}
div.map-controller > button img {
  width: 100%;
  height: 100%;
}
</style>
