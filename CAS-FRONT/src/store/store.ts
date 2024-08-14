import { reactive } from 'vue'

export const store = reactive({
    isReady: false,
    startRender() {
        this.isReady = true;
    },
    isShowChartWindow: false,
    showChartWindow() {
        this.isShowChartWindow = true;
    },
    hideChartWindow() {
        this.isShowChartWindow = false;
    },
    toggleChartWindow() {
        this.isShowChartWindow = !this.isShowChartWindow;
    },
    isShowLoading: false,
    showLoading() {
        this.isShowLoading = true;
    },
    hideLoading() {
        this.isShowLoading = false;
    },
    toggleLoading() {
        this.isShowLoading = !this.isShowLoading;
    },
})