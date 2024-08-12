<template>
  <div id="line-chart-wrapper">
    <Line
          :options="chartOptions"
          :data="chartData"
    />
  </div>
</template>

<script>
import { Line } from 'vue-chartjs'
import { Chart, registerables } from 'chart.js';
Chart.register(...registerables);

const data = {
  labels: ["0", "1", "2", "3", "4", "5", "0", "1", "2", "3", "4", "5", "0", "1", "2", "3", "4", "5", "0", "1", "2", "3", "4", "5"],
  datasets: [
    {
      label: "사고물질 농도",
      data: [12, 19, 3, 5, 2, 3, 12, 19, 3, 5, 2, 3, 12, 19, 3, 5, 2, 3, 12, 19, 3, 5, 2, 3],
    },
  ],
};

// resize ratio

export default {
  name: 'LineChart',
  components: { Line },
  data() {
    return {
      chartData: data,
      chartOptions: {
        responsive: true,
        maintainAspectRatio: false,
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