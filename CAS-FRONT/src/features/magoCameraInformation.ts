/* @ts-ignore */
const Cesium = window.Cesium;

let compassInformationEvent : any = undefined;
let cameraInformationEvent : any = undefined;

export const onCompassInformation = (viewer : any, callback : Function) => {
  if (!compassInformationEvent) {
    compassInformationEvent = () => {
      const camera = viewer.camera;
      const heading = Cesium.Math.toDegrees(camera.heading);
      if (callback) {
        callback(heading);
      }
    };
  }
  viewer.clock.onTick.addEventListener(compassInformationEvent);
}

export const onCameraInformation = (viewer : any, callback : Function) => {
  if (!cameraInformationEvent) {
    cameraInformationEvent = () => {
      const camera = viewer.camera;
      const cartographic = Cesium.Cartographic.fromCartesian(camera.positionWC);
      const longitude = Cesium.Math.toDegrees(cartographic.longitude);
      const latitude = Cesium.Math.toDegrees(cartographic.latitude);
      const height = cartographic.height;
      const heading = Cesium.Math.toDegrees(camera.heading);
      if (callback) {
        callback(longitude, latitude, height, heading);
      }
    };
  }
  viewer.clock.onTick.addEventListener(cameraInformationEvent);
}

export const offCameraInformation = (viewer : any) => {
  viewer.clock.onTick.removeEventListener(cameraInformationEvent);
}
