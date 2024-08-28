import { reactive } from 'vue'

export const store = reactive({
    chemicalAccidentInfo: {
        chemicalInfo: {} as any,
        accidentInfo: {
            accidentId: undefined,
            accidentNo: undefined
        } as any,
        personInfo: {

        } as any,
        personRiskInfo: {} as any,
        fakeTime: {
            year: 1900,
            month: 1,
            day: 1,
            hour: 12,
            minute: 0,
            second: 0
        } as any
    },
    getChemicalAccidentInfo() {
        return this.chemicalAccidentInfo;
    },
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
    toStandardExponential(num: number, digits: number) {
        const exponential = num.toExponential(digits);
        const [coefficient, exponent] = exponential.split('e');

        const isNegative = exponent[0] === '-';
        const absExponent = exponent.slice(1);
        const paddedExponent = absExponent.padStart(2, '0');
        return `${coefficient}E${isNegative ? '-' : '+'}${paddedExponent}`;
    }
})