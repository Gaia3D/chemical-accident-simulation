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
    const position = Cesium.Cartesian3.fromDegrees(126.68418943890646, 36.902156101663145);

    const size : 32 = 32;
    const center = viewer.entities.add({
        name: "Dome",
        position: position,
        ellipsoid: {
            radii: new Cesium.Cartesian3(1, 1, 1),
            maximumCone: Cesium.Math.PI_OVER_TWO,
            material: Cesium.Color.RED.withAlpha(innerFactor),
            outline: false,
            outlineColor: Cesium.Color.RED.withAlpha(outerFactor),
        },
        billboard: {
            //image: url,
            heightReference: Cesium.HeightReference.CLAMP_TO_GROUND,
            //image: "/src/assets/images/icons/marker.png",
            image: "/data/icons/marker.png",
            verticalOrigin: Cesium.VerticalOrigin.center,
            pixelOffset : new Cesium.Cartesian2(0.0, -(size/2)),
            width: size,
            height: size,
            disableDepthTestDistance: Number.POSITIVE_INFINITY,
        },
        /*label: {
            text: "사고지점",
            showBackground: true,
            backgroundColor: Cesium.Color.WHITE,
            fillColor : Cesium.Color.BLACK,
            font: "11px sans-serif",
            horizontalOrigin: Cesium.HorizontalOrigin.CENTER,
            pixelOffset: new Cesium.Cartesian2(0, 20),
            disableDepthTestDistance: Number.POSITIVE_INFINITY,
            heightReference: Cesium.HeightReference.CLAMP_TO_GROUND,
        }*/
    });

    /*const computedCircle = viewer.entities.add({
        polylineVolume: {
            position: Cesium.Cartesian3.fromDegrees(126.65403123232736, 36.90329299539047),
            shape: computeCircle(3000.0),
            material: Cesium.Color.WHITE,
        },
    });*/

    const near = viewer.entities.add({
        name: "Dome",
        position: position,
        /*ellipsoid: {
            radii: new Cesium.Cartesian3(range, range, 10),
            maximumCone: Cesium.Math.PI_OVER_TWO,
            material: Cesium.Color.RED.withAlpha(innerFactor),
            outline: false,
            outlineColor: Cesium.Color.RED.withAlpha(outerFactor),
            heightReference: Cesium.HeightReference.CLAMP_TO_GROUND,
        },*/
        cylinder: {
            length: 1,
            topRadius: range,
            bottomRadius: range,
            heightReference: Cesium.HeightReference.CLAMP_TO_GROUND,
            outline: true,
            outlineColor: Cesium.Color.GREY.withAlpha(outerFactor),
            fill: false,
        },
    });

    const middle = viewer.entities.add({
        name: "Dome",
        position: position,
        /*ellipsoid: {
            radii: new Cesium.Cartesian3(range * 2, range * 2, 10),
            maximumCone: Cesium.Math.PI_OVER_TWO,
            material: Cesium.Color.DARKORANGE.withAlpha(innerFactor),
            outline: false,
            outlineColor: Cesium.Color.DARKORANGE.withAlpha(outerFactor),
        },*/
        cylinder: {
            length: 1,
            topRadius: range * 2,
            bottomRadius: range * 2,
            heightReference: Cesium.HeightReference.CLAMP_TO_GROUND,
            outline: true,
            outlineColor: Cesium.Color.GREY.withAlpha(outerFactor),
            fill: false,
        },
    });

    const far = viewer.entities.add({
        name: "Dome",
        position: position,
        /*ellipsoid: {
            radii: new Cesium.Cartesian3(range * 3, range * 3, 10),
            maximumCone: Cesium.Math.PI_OVER_TWO,
            material: Cesium.Color.GREEN.withAlpha(innerFactor),
            outline: false,
            outlineColor: Cesium.Color.GREEN.withAlpha(outerFactor),
        },*/
        cylinder: {
            length: 1,
            topRadius: range * 3,
            bottomRadius: range * 3,
            heightReference: Cesium.HeightReference.CLAMP_TO_GROUND,
            outline: true,
            outlineColor: Cesium.Color.GREY.withAlpha(outerFactor),
            fill: false,
        },
    });

    radiusEntities = {
        center, near, middle, far
    };
}