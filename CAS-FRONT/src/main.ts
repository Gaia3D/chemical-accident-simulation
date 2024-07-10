import {createApp} from 'vue'
import LoadScript, {loadScript} from 'vue-plugin-load-script'
import './base.css'
import "./externals/cesium/Widgets/widgets.css";
import "./cesium-custom.css";

/* @ts-ignore */
window.CESIUM_BASE_URL = import.meta.env.VITE_CESIUM_BASE_URL

import App from './App.vue'
import router from './router'

await loadScript("/src/externals/cesium/cesium.js").then(e => {
    /* @ts-ignore */
    console.log(`[Main] Loadded CesiumJS ${Cesium.VERSION}`);
    console.debug(e);
}).catch(e => {
    console.error("[Main] Could not load CesiumJS.");
    console.error(e);
})
await loadScript("/src/externals/mago3d/mago3d.js").then(e => {
    /* @ts-ignore */
    console.log(`[Main] Loadded MagoJS ${Mago3D.VERSION}`);
    console.debug(e);
}).catch(e => {
    console.error("[Main] Could not load Mago3D.");
    console.error(e);
})

createApp(App)
    .use(router)
    .use(LoadScript)
    .mount('#app')
