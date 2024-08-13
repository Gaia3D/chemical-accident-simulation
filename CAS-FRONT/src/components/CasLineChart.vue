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
          return value + '㎍';
        },
        font : {
          size: 9,
        },
        stepSize: 0.3
      },
      min: 0.0,
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
          yMin: 368 / 1000,
          yMax: 368 / 1000,
          borderColor: 'rgb(255,211,99)',
          borderWidth: 2,
        },
        label1: {
          type: 'label',
          xValue: 288,
          yValue: 368 / 1000,
          yAdjust: -5,
          position: { x: 'end', y: 'center' },
          backgroundColor: 'rgba(245, 245, 245, 0)',
          content: ['저위험 수준 (AEGL-1 10min 368 mg/m3) '],
          textAlign: 'end',
          font: {
            size: 11,
            family: 'Noto Sans KR'
          }
        },

        line2: {
          type: 'line',
          yMin: 868 / 1000,
          yMax: 868 / 1000,
          borderColor: 'rgb(255,164,99)',
          borderWidth: 2,
        },
        label2: {
          type: 'label',
          xValue: 288,
          yValue: 868 / 1000,
          yAdjust: -5,
          position: { x: 'end', y: 'center' },
          backgroundColor: 'rgba(245, 245, 245, 0)',
          content: ['중위험 수준 (AEGL-2 10min 868 mg/m3)'],
          textAlign: 'end',
          font: {
            size: 11,
            family: 'Noto Sans KR'
          }
        },
        line3: {
          type: 'line',
          yMin: 1568 / 1000,
          yMax: 1568 / 1000,
          borderColor: 'rgb(255,99,99)',
          borderWidth: 2,
        },
        label3: {
          type: 'label',
          xValue: 288,
          yValue: 1568 / 1000,
          yAdjust: -5,
          position: { x: 'end', y: 'center' },
          backgroundColor: 'rgba(245, 245, 245, 0)',
          content: ['고위험 수준 (AEGL-3 10min 1568 mg/m3)'],
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
    updateData() {
      if (this.personalData.json) {
        console.log(this.personalData.json);

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