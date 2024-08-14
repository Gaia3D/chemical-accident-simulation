<template>
  <div id="line-chart-wrapper">
    <Line
          :options="computedChartOption"
          :data="computedChartData"
    />
  </div>
</template>

<script>
import { Line } from 'vue-chartjs';
import { Chart, registerables } from 'chart.js';
import annotationPlugin from 'chartjs-plugin-annotation';
Chart.register(...registerables);
Chart.register(annotationPlugin);

const options = {
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    x: {
      ticks: {
        callback: function(value, index, ticks) {
          return (value / 6) + 'H';
        },
        count: 48,
        font : {
          size: 9,
        },
        stepSize: 1,

        autoSkip: true,
        maxTicksLimit: 49,
      },
      min: 0.0,
      max: 24 * 2 * 6,
    },
    y: {
      ticks: {
        callback: function(value, index, ticks) {
          return value + '㎍';
        },
        font : {
          size: 9,
        },
        stepSize: 0.3
      },
      min: 0.0,
      //max: 1.0,
    }
  },
  plugins: {
    annotation: {
      annotations: {
        line1: {
          type: 'line',
          yMin: 0.5,
          yMax: 0.5,
          borderColor: 'rgb(255, 99, 132)',
          borderWidth: 2,
        }
      }
    }
  }
}

const data = {
  labels: [],
  datasets: [
    {
      label: "호흡 발암 노출량",
      data: [],
    },
    {
      label: "호흡 비발암 노출량",
      data: [],
    },
    {
      label: "섭취/토양 발암 노출량",
      data: [],
    },
    {
      label: "섭취/토양 비발암 노출량",
      data: [],
    },
  ],
};

data.datasets[0].data = data.datasets[0].data.map((value) => value * 1.1);
data.datasets[1].data = data.datasets[1].data.map((value) => value * 2.4);
data.datasets[2].data = data.datasets[2].data.map((value) => value * 1.7);
data.datasets[3].data = data.datasets[3].data.map((value) => value * 2.8);

// resize ratio

export default {
  name: 'DosageLineChart',
  components: { Line },
  props: {
    personalData: Object
  },
  setup(props) {

  },
  mounted() {

  },
  computed: {
    computedChartData() {
      return this.chartData;
    },
    computedChartOption() {
      return this.chartOptions;
    }
  },
  methods: {
    initChartData() {
      this.chartData = {
        labels: [],
        datasets: [
          {
            label: "호흡 발암 노출량",
            data: [],
          },
          {
            label: "호흡 비발암 노출량",
            data: [],
          },
          {
            label: "섭취/토양 발암 노출량",
            data: [],
          },
          {
            label: "섭취/토양 비발암 노출량",
            data: [],
          },
        ],
      };
    },
    updateData() {
      if (this.personalData.json) {
        this.initChartData();
        /*this.chartData.labels.length = [];
        this.chartData.datasets[0].data.length = [];
        this.chartData.datasets[1].data.length = [];
        this.chartData.datasets[2].data.length = [];
        this.chartData.datasets[3].data.length = [];*/

        let json = this.personalData.json;
        let length = json.length;
        for (let i = 0; i < length; i++) {
          this.chartData.labels.push(i);

          let temp = json[i];
          let time = i;
          let value1 = parseFloat(temp.exposureRespCarc); // 노출량 공기 - 발암성
          let value2 = parseFloat(temp.exposure); // 노출량 공기 - 비 발암성
          let value3 = parseFloat(temp.exposureSoilIngestCarc); // 노출량 토양 - 발암성
          let value4 = parseFloat(temp.exposureSoilIngestNoncarc); // 노출량 토양 - 비 발암성

          if (isNaN(value1)) {
            value1 = 0;
          }

          if (isNaN(value2)) {
            value2 = 0;
          }

          if (isNaN(value3)) {
            value3 = 0;
          }

          if (isNaN(value4)) {
            value4 = 0;
          }

          this.chartData.datasets[0].data[time] = value1;
          this.chartData.datasets[1].data[time] = value2;
          this.chartData.datasets[2].data[time] = value3;
          this.chartData.datasets[3].data[time] = value4;
        }
      }
    }
  },
  data() {
    return {
      chartData: data,
      chartOptions: options
    }
  }
}
</script>

<style scoped>
#line-chart-wrapper {
  width: 100%;
  height: 100%;
}
</style>