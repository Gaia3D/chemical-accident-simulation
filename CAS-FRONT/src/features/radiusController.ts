/* @ts-ignore */
const Cesium = window.Cesium;

let radiusEntities : any ;
const innerFactor = 0.3;
const outerFactor = 0.5;

export const toggleRadius = async (viewer : any) => {
    if (radiusEntities) {
        radiusEntities.center.show = !radiusEntities.center.show;
        radiusEntities.near.show = !radiusEntities.near.show;
        radiusEntities.middle.show = !radiusEntities.middle.show;
        radiusEntities.far.show = !radiusEntities.far.show;
        return;
    }

    const range = 1000

    const center = viewer.entities.add({
        name: "Dome",
        position: Cesium.Cartesian3.fromDegrees(126.65403123232736, 36.90329299539047),
        ellipsoid: {
            radii: new Cesium.Cartesian3(10, 10, 10),
            maximumCone: Cesium.Math.PI_OVER_TWO,
            material: Cesium.Color.RED.withAlpha(innerFactor),
            outline: true,
            outlineColor: Cesium.Color.RED.withAlpha(outerFactor),
        },
        label: {
            text: "사고지점",
            showBackground: true,
            backgroundColor: Cesium.Color.WHITE,
            fillColor : Cesium.Color.BLACK,
            font: "11px sans-serif",
            horizontalOrigin: Cesium.HorizontalOrigin.CENTER,
            pixelOffset: new Cesium.Cartesian2(0, 20),
            disableDepthTestDistance: Number.POSITIVE_INFINITY,
        }
    });

    const near = viewer.entities.add({
        name: "Dome",
        position: Cesium.Cartesian3.fromDegrees(126.65403123232736, 36.90329299539047),
        ellipsoid: {
            radii: new Cesium.Cartesian3(range, range, range),
            maximumCone: Cesium.Math.PI_OVER_TWO,
            material: Cesium.Color.RED.withAlpha(innerFactor),
            outline: true,
            outlineColor: Cesium.Color.RED.withAlpha(outerFactor),
        },
    });

    const middle = viewer.entities.add({
        name: "Dome",
        position: Cesium.Cartesian3.fromDegrees(126.65403123232736, 36.90329299539047),
        ellipsoid: {
            radii: new Cesium.Cartesian3(range * 2, range * 2, range * 2),
            maximumCone: Cesium.Math.PI_OVER_TWO,
            material: Cesium.Color.DARKORANGE.withAlpha(innerFactor),
            outline: true,
            outlineColor: Cesium.Color.DARKORANGE.withAlpha(outerFactor),
        },
    });

    const far = viewer.entities.add({
        name: "Dome",
        position: Cesium.Cartesian3.fromDegrees(126.65403123232736, 36.90329299539047),
        ellipsoid: {
            radii: new Cesium.Cartesian3(range * 3, range * 3, range * 3),
            maximumCone: Cesium.Math.PI_OVER_TWO,
            material: Cesium.Color.GREEN.withAlpha(innerFactor),
            outline: true,
            outlineColor: Cesium.Color.GREEN.withAlpha(outerFactor),
        },
    });

    radiusEntities = {
        center, near, middle, far
    };
}