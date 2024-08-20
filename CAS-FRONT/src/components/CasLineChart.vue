<template>
  <div id="line-chart-wrapper">
    <Line
          :data="computedChartData"
          :options="computedChartOption"
    />
  </div>
</template>

<script >
import { Line } from 'vue-chartjs';
import { Chart, registerables } from 'chart.js';
import annotationPlugin from 'chartjs-plugin-annotation';
Chart.register(...registerables);
Chart.register(annotationPlugin);

const data = {
  labels: [],
  datasets: [
    {
      label: "사고물질 농도",
      data: [],
    },
  ],
};

const options = {
  responsive: true,
  maintainAspectRatio: false,
  animation: {
    duration: 0
  },
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
        //stepSize: .2

      },
      min: 0.0,
      max: 24 * 2 * 6,
    },
    y: {
      ticks: {
        callback: function(value, index, ticks) {
          return (Math.round(value * 1000 * 10) / 10) + 'mg';
        },
        font : {
          size: 9,
        },
        stepSize: 30.0 * 0.001
      },
      min: 0,
      max: 150.0 * 0.001,
    }
  },
  plugins: {
    legend: {
      labels: {
        // This more specific font property overrides the global property
        font: {
          size: 11
        }
      }
    },
    annotation: {
      annotations: {
        line1: {
          type: 'line',
          yMin: 1.1 * 0.001,
          yMax: 1.1 * 0.001,
          borderColor: 'rgb(255,211,99)',
          borderWidth: 2,
        },
        label1: {
          type: 'label',
          xValue: 288,
          yValue: 1.1 * 0.001,
          yAdjust: -5,
          position: { x: 'end', y: 'center' },
          backgroundColor: 'rgba(245, 245, 245, 0)',
          content: ['저위험 수준 (AEGL-1 10min 1.1 mg/m3) '],
          textAlign: 'end',
          font: {
            size: 11,
            family: 'Noto Sans KR'
          }
        },

        line2: {
          type: 'line',
          yMin: 17 * 0.001,
          yMax: 17 * 0.001,
          borderColor: 'rgb(255,164,99)',
          borderWidth: 2,
        },
        label2: {
          type: 'label',
          xValue: 288,
          yValue: 17 * 0.001,
          yAdjust: -5,
          position: { x: 'end', y: 'center' },
          backgroundColor: 'rgba(245, 245, 245, 0)',
          content: ['중위험 수준 (AEGL-2 10min 17 mg/m3)'],
          textAlign: 'end',
          font: {
            size: 11,
            family: 'Noto Sans KR'
          }
        },
        line3: {
          type: 'line',
          yMin: 123 * 0.001,
          yMax: 123 * 0.001,
          borderColor: 'rgb(255,99,99)',
          borderWidth: 2,
        },
        label3: {
          type: 'label',
          xValue: 288,
          yValue: 123 * 0.001,
          yAdjust: -5,
          position: { x: 'end', y: 'center' },
          backgroundColor: 'rgba(245, 245, 245, 0)',
          content: ['고위험 수준 (AEGL-3 10min 123 mg/m3)'],
          textAlign: 'end',
          font: {
            size: 11,
            family: 'Noto Sans KR'
          }
        },

      }
    }
  }
};

// resize ratio
export default {
  name: 'CasLineChart',
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
      //this.chartData = data;
      this.chartData = {
        labels: [],
        datasets: [
          {
            label: "사고물질 농도",
            data: [],
          },
        ],
      };
    },
    initOptionsData() {
      this.chartOptions = options;
    },
    updateData() {
      if (this.personalData.json) {
        this.initChartData();

        this.chartData.labels.length = [];
        this.chartData.datasets[0].data.length = [];

        let json = this.personalData.json;
        let length = json.length;
        for (let i = 0; i < length; i++) {
          this.chartData.labels.push(i);

          let temp = json[i];
          let time = i;
          let value = parseFloat(temp.concentration);
          if (isNaN(value)) {
            value = 0;
          }

          this.chartData.datasets[0].data[time] = value;
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