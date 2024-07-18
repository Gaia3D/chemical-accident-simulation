<script setup lang="ts">
import "../map-custom.css";
import {ref} from "vue";

/* @ts-ignore */
const Cesium = window.Cesium;
/* @ts-ignore */
const Mago3D = window.Mago3D;

const omsBuildingsTileset = ref<any>();
const cesiumWorldTerrain = ref<any>();
const toggleState = ref<any>({
  worldTerrain: false,
  osmBuildings: false,
  dangjinTerrain: false,
  dangjinBuildings: false,
});

const props = defineProps<{
  transferViewer: any;
}>();

const changeWorldTerrain = async () => {
  if (cesiumWorldTerrain.value) {
    return cesiumWorldTerrain.value;
  }
  const worldTerrain = await Cesium.createWorldTerrainAsync({
    requestVertexNormals: true
  });
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

const toggleWorldTerrain = async () => {
  const viewer = getViewer()
  if (!(viewer.terrainProvider instanceof Cesium.CesiumTerrainProvider)) {
    viewer.terrainProvider = await changeWorldTerrain();
    toggleState.value.worldTerrain = true;
  } else {
    viewer.terrainProvider = new Cesium.EllipsoidTerrainProvider();
    toggleState.value.worldTerrain = false;
  }
}
const toggleDangjinTerrain = async () => {
  const viewer = getViewer()
  if (!(viewer.terrainProvider instanceof Cesium.CesiumTerrainProvider)) {
    const url = "/data/terrains/dang-jin-terrain-14";
    viewer.terrainProvider = await Cesium.CesiumTerrainProvider.fromUrl(url, {
      requestVertexNormals: true,
    });
    toggleState.value.dangjinTerrain = true;
  } else {
    viewer.terrainProvider = new Cesium.EllipsoidTerrainProvider();
    toggleState.value.dangjinTerrain = false;
  }
}

const toggleOsmBuildings = async () => {
  const viewer = getViewer()
  const osmBuildingsTileset = await getOmsBuildingsTileset();

  const duplicatedTileset = viewer.scene.primitives._primitives.find((e : any) => {
    return e._url === osmBuildingsTileset._url
  })
  if (duplicatedTileset !== undefined) {
    duplicatedTileset.show = !duplicatedTileset.show;
    toggleState.value.osmBuildings = duplicatedTileset.show;
  } else {
    viewer.scene.primitives.add(osmBuildingsTileset);
    toggleState.value.osmBuildings = true;
  }
}
const toggleDangjinBuildings = async () => {
  const viewer = getViewer()
  const url = "/data/tilesets/dang-jin-building-terrain/tileset.json";
  const buildingsTileset = await Cesium.Cesium3DTileset.fromUrl(url, {

  });

  const duplicatedTileset = viewer.scene.primitives._primitives.find((e : any) => {
    return e._url === buildingsTileset._url
  })
  if (duplicatedTileset !== undefined) {
    duplicatedTileset.show = !duplicatedTileset.show;
    toggleState.value.dangjinBuildings = duplicatedTileset.show;
  } else {
    viewer.scene.primitives.add(buildingsTileset);
    toggleState.value.dangjinBuildings = true;
  }
}

const getViewer = () => {
  return props.transferViewer.viewer;
}

defineExpose({
  toggleOsmBuildings,
  toggleDangjinBuildings,
  toggleWorldTerrain,
  toggleDangjinTerrain,
  changeWorldTerrain,
  getOmsBuildingsTileset
})

</script>

<template>
  <div class="vertical">
    <div class="terrain-selector layer vertical">
      <button @click="toggleWorldTerrain" title="Cesium World Terrain" v-bind:class="toggleState.worldTerrain ? 'toggle' : ''">World Terrain</button>
      <button @click="toggleOsmBuildings" title="Cesium OSM Buildings" v-bind:class="toggleState.osmBuildings ? 'toggle' : ''">OSM Buildings</button>
      <button @click="toggleDangjinTerrain" title="Dangjin Terrain" v-bind:class="toggleState.dangjinTerrain ? 'toggle' : ''">Dangjin Terrain</button>
      <button @click="toggleDangjinBuildings" title="Dangjin Buildings" v-bind:class="toggleState.dangjinBuildings ? 'toggle' : ''">Dangjin Buildings</button>
    </div>
  </div>
</template>
<style scoped>
  .terrain-selector {
    padding: 6px;
    position: relative;
  }
  .terrain-selector > button {
    font-size: 9px;
  }
</style>
