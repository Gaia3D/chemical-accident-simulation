<template>
  <div id="line-chart-wrapper">
    <Line
          :options="chartOptions"
          :data="chartData"
    />
  </div>
</template>

<script>
import { Line } from 'vue-chartjs';
import { Chart, registerables } from 'chart.js';
import annotationPlugin from 'chartjs-plugin-annotation';
Chart.register(...registerables);
Chart.register(annotationPlugin);

const data = {
  labels: ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"],
  datasets: [
    {
      label: "사고물질 농도",
      data: [12, 25, 10, 5, 2, 3, 12, 19, 3, 5, 2, 3, 12, 15, 3, 5, 2, 3, 12, 19, 3, 5, 2, 3],
    },
  ],
};

// resize ratio

export default {
  name: 'CasLineChart',
  components: { Line },
  data() {
    return {
      chartData: data,
      chartOptions: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          x: {
            ticks: {
              callback: function(value, index, ticks) {
                return '$' + value;
              },
              font : {
                size: 9,
                stepSize: 1
              }
            }
          },
          y: {
            ticks: {
              callback: function(value, index, ticks) {
                return '%' + value;
              },
              font : {
                size: 9,
                stepSize: 1
              }
            }
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
                yMin: 5,
                yMax: 5,
                borderColor: 'rgb(255,211,99)',
                borderWidth: 2,
              },
              label1: {
                type: 'label',
                xValue: 23,
                yValue: 5,
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
                yMin: 10,
                yMax: 10,
                borderColor: 'rgb(255,164,99)',
                borderWidth: 2,
              },
              label2: {
                type: 'label',
                xValue: 23,
                yValue: 10,
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
                yMin: 20,
                yMax: 20,
                borderColor: 'rgb(255,99,99)',
                borderWidth: 2,
              },
              label3: {
                type: 'label',
                xValue: 23,
                yValue: 20,
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
      }
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