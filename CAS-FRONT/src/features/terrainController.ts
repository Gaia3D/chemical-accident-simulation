/* @ts-ignore */
const Cesium = window.Cesium;

let cesiumWorldTerrain : any;
export const getCesiumWorldTerrain = async () => {
  if (cesiumWorldTerrain) {
    return cesiumWorldTerrain;
  }
  const worldTerrain = await Cesium.createWorldTerrainAsync({
    requestVertexNormals: true
  });
  cesiumWorldTerrain = worldTerrain;
  return worldTerrain;
}

let dangjinTerrain : any
export const getDangjinTerrain = async () => {
    if (dangjinTerrain) {
        return dangjinTerrain;
    }
    const url = "/data/terrains/dang-jin-terrain-14";
    const terrain = await Cesium.CesiumTerrainProvider.fromUrl(url, {
        requestVertexNormals: true,
    });
    dangjinTerrain = terrain;
    return terrain;

}

export const toggleWorldTerrain = async (viewer : any) => {
  if (!(viewer.terrainProvider instanceof Cesium.CesiumTerrainProvider)) {
    viewer.terrainProvider = await getCesiumWorldTerrain();
    return true;
  } else {
    viewer.terrainProvider = new Cesium.EllipsoidTerrainProvider();
    return false;
  }
}

export const toggleDangjinTerrain = async (viewer : any) => {
  if (!(viewer.terrainProvider instanceof Cesium.CesiumTerrainProvider)) {
    viewer.terrainProvider = await getDangjinTerrain();
    return true;
  } else {
    viewer.terrainProvider = new Cesium.EllipsoidTerrainProvider();
    return false;
  }
}