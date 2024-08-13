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
  labels: ["0", "1", "2", "3", "4", "5", "0", "1", "2", "3", "4", "5", "0", "1", "2", "3", "4", "5", "0", "1", "2", "3", "4", "5"],
  datasets: [
    {
      label: "호흡 발암 노출량",
      data: [12, 19, 3, 5, 2, 3, 12, 19, 3, 5, 2, 3, 12, 19, 3, 5, 2, 3, 12, 19, 3, 5, 2, 3],
    },
    {
      label: "호흡 비발암 노출량",
      data: [12, 19, 3, 5, 2, 3, 12, 19, 3, 5, 2, 3, 12, 19, 3, 5, 2, 3, 12, 19, 3, 5, 2, 3],
    },
    {
      label: "섭취/토양 발암 노출량",
      data: [12, 19, 3, 5, 2, 3, 12, 19, 3, 5, 2, 3, 12, 19, 3, 5, 2, 3, 12, 19, 3, 5, 2, 3],
    },
    {
      label: "섭취/토양 비발암 노출량",
      data: [12, 19, 3, 5, 2, 3, 12, 19, 3, 5, 2, 3, 12, 19, 3, 5, 2, 3, 12, 19, 3, 5, 2, 3],
    },
  ],
};

data.datasets[1].data = data.datasets[0].data.map((value) => value * 1.1);
data.datasets[1].data = data.datasets[0].data.map((value) => value * 2.4);
data.datasets[2].data = data.datasets[0].data.map((value) => value * 1.7);
data.datasets[2].data = data.datasets[0].data.map((value) => value * 2.8);

// resize ratio

export default {
  name: 'DosageLineChart',
  components: { Line },
  data() {
    return {
      chartData: data,
      chartOptions: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          annotation: {
            annotations: {
              line1: {
                type: 'line',
                yMin: 10,
                yMax: 10,
                borderColor: 'rgb(255, 99, 132)',
                borderWidth: 2,
              }
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