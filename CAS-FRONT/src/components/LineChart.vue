<template>
  <Line id="line-chart"
      :options="chartOptions"
      :data="chartData"
  />
</template>

<script>
import { Line } from 'vue-chartjs'
import { Chart, registerables } from 'chart.js';
Chart.register(...registerables);

const data = {
  labels: ["Red", "Blue", "Yellow", "Green", "Purple", "Orange"],
  datasets: [
    {
      label: "# of Votes",
      data: [12, 19, 3, 5, 2, 3],
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
#line-chart {
  width: 100%;
  height: 100%;
}
</style>