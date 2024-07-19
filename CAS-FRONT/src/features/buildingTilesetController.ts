/* @ts-ignore */
const Cesium = window.Cesium;

let omsBuildingsTileset : any;
export const getOmsBuildingsTileset = async () => {
    if (omsBuildingsTileset) {
        return omsBuildingsTileset;
    }

    const osmBuildingsTileset = await Cesium.createOsmBuildingsAsync({
        enableShowOutline: false,
        showOutline: false,
    });

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

export const toggleOsmBuildings = async (viewer : any) => {
    const osmBuildingsTileset = await getOmsBuildingsTileset();

    const duplicatedTileset = viewer.scene.primitives._primitives.find((e : any) => {
        return e._url === osmBuildingsTileset._url
    })
    if (duplicatedTileset !== undefined) {
        duplicatedTileset.show = !duplicatedTileset.show;
        return duplicatedTileset.show;
    } else {
        viewer.scene.primitives.add(osmBuildingsTileset);
        return true;
    }
}
export const toggleDangjinBuildings = async (viewer : any) => {
    const url = "/data/tilesets/dang-jin-building-terrain/tileset.json";
    const buildingsTileset = await Cesium.Cesium3DTileset.fromUrl(url, {});

    const duplicatedTileset = viewer.scene.primitives._primitives.find((e : any) => {
        return e._url === buildingsTileset._url
    })
    if (duplicatedTileset !== undefined) {
        duplicatedTileset.show = !duplicatedTileset.show;
        return duplicatedTileset.show;
    } else {
        viewer.scene.primitives.add(buildingsTileset);
        return true;
    }
}