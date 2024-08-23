<template>
  <div id="legend-margin"></div>
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
import { store } from "../store/store";
Chart.register(...registerables);
Chart.register(annotationPlugin);
/*
const getOrCreateLegendList = (chart, id) => {
  const legendContainer = document.getElementById(id);

  if (!legendContainer) {
    return document.createElement('ul');
  }

  let listContainer = legendContainer.querySelector('ul');

  if (!listContainer) {
    listContainer = document.createElement('ul');
    /!*listContainer.style.display = 'flex';
    listContainer.style.flexDirection = 'row';
    listContainer.style.margin = 0;
    listContainer.style.padding = 0;*!/

    legendContainer.appendChild(listContainer);
  }

  return listContainer;
};

const htmlLegendPlugin = {
  id: 'htmlLegend',
  afterUpdate(chart, args, options) {
    const ul = getOrCreateLegendList(chart, options.containerID);

    // Remove old legend items
    while (ul.firstChild) {
      ul.firstChild.remove();
    }
    // Reuse the built-in legendItems generator
    const items = chart.options.plugins.legend.labels.generateLabels(chart);

    items.forEach(item => {
      const li = document.createElement('li');
      /!*li.style.alignItems = 'center';
      li.style.cursor = 'pointer';
      li.style.display = 'flex';
      li.style.flexDirection = 'row';
      li.style.marginLeft = '10px';*!/

      li.onclick = () => {
        const {type} = chart.config;
        if (type === 'pie' || type === 'doughnut') {
          // Pie and doughnut charts only have a single dataset and visibility is per item
          chart.toggleDataVisibility(item.index);
        } else {
          chart.setDatasetVisibility(item.datasetIndex, !chart.isDatasetVisible(item.datasetIndex));
        }
        chart.update();
      };

      // Color box
      const boxSpan = document.createElement('span');
      boxSpan.style.background = item.fillStyle;
      boxSpan.style.borderColor = item.strokeStyle;
      /!*boxSpan.style.borderWidth = item.lineWidth + 'px';
      boxSpan.style.display = 'inline-block';
      boxSpan.style.flexShrink = 0;
      boxSpan.style.height = '20px';
      boxSpan.style.marginRight = '10px';
      boxSpan.style.width = '20px';*!/
      // Text
      const textContainer = document.createElement('p');
      /!*textContainer.style.color = item.fontColor;
      textContainer.style.margin = 0;
      textContainer.style.padding = 0;*!/
      textContainer.style.textDecoration = item.hidden ? 'line-through' : '';

      const text = document.createTextNode(item.text);
      textContainer.appendChild(text);

      li.appendChild(boxSpan);
      li.appendChild(textContainer);
      ul.appendChild(li);
    });
  }
};

Chart.register(htmlLegendPlugin);*/



const data = {
  labels: [],
  datasets: [
    {
      label: "사고물질 농도",
      data: [],
    },
  ],
};

const scale = 1;

const options = {
  responsive: true,
  maintainAspectRatio: false,
  animation: {
    duration: 0
  },
  layout: {
    padding: {
      left: 5,
      right: 5,
    }
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
        precision: 1,
      },
      min: 0.0,
      max: 24 * 2 * 6,
    },
    y: {
      ticks: {
        callback: function(value, index, ticks) {
          let scaledValue = (Math.round(value * scale * 10) / 10);
          scaledValue = scaledValue.toExponential(2);
          return scaledValue + ' mg/㎥';
        },
        font : {
          size: 9,
        },
        stepSize: 30.0 / scale,
        precision: 2,
      },
      min: 0,
      //max: 150.0 / scale,
    }
  },
  plugins: {
    tooltip: {
      callbacks: {
        label: function(context) {
          console.log(context);
          let label = context.dataset.label || '';
          let value = context.dataset.data[context.label];

          // microgram
          //let text = `${label} : ${value}㎍/㎥`;
          // milligram
          let text = `${label} : ${parseFloat(value)} mg/㎥`;
          return text;
        }
      }
    },
    legend: {
      display: false,
      labels: {
        // This more specific font property overrides the global property
        font: {
          size: 11
        },
      },
    },
    annotation: {
      annotations: {
        line1: {
          type: 'line',
          yMin: 0,
          yMax: 0,
          borderColor: 'rgb(255,211,99)',
          borderWidth: 2,
        },
        label1: {
          type: 'label',
          xValue: 288,
          yValue: 0,
          yAdjust: -7,
          position: { x: 'end', y: 'center' },
          backgroundColor: 'rgba(245, 245, 245, 0)',
          content: ['저위험 수준 (AEGL-1 10min 0 mg/㎡) '],
          textAlign: 'end',
          font: {
            size: 10,
            family: 'Noto Sans KR'
          }
        },

        line2: {
          type: 'line',
          yMin: 0,
          yMax: 0,
          borderColor: 'rgb(255,164,99)',
          borderWidth: 2,
        },
        label2: {
          type: 'label',
          xValue: 288,
          yValue: 0,
          yAdjust: -7,
          position: { x: 'end', y: 'center' },
          backgroundColor: 'rgba(245, 245, 245, 0)',
          content: ['중위험 수준 (AEGL-2 10min 0 mg/㎡)'],
          textAlign: 'end',
          font: {
            size: 10,
            family: 'Noto Sans KR'
          }
        },
        line3: {
          type: 'line',
          yMin: 0,
          yMax: 0,
          borderColor: 'rgb(255,99,99)',
          borderWidth: 2,
        },
        label3: {
          type: 'label',
          xValue: 288,
          yValue: 0,
          yAdjust: -7,
          position: { x: 'end', y: 'center' },
          backgroundColor: 'rgba(245, 245, 245, 0)',
          content: ['고위험 수준 (AEGL-3 10min 0 mg/㎡)'],
          textAlign: 'end',
          font: {
            size: 10,
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
      this.chartOptions = undefined;
      this.chartOptions = options;
    },
    updateData() {
      if (this.personalData.json) {
        this.initChartData();
        this.initOptionsData();

        const scale = 1;

        const aegl1 = parseFloat(store.getChemicalAccidentInfo().chemicalInfo.aegl1Conc);
        const aegl2 = parseFloat(store.getChemicalAccidentInfo().chemicalInfo.aegl2Conc);
        const aegl3 = parseFloat(store.getChemicalAccidentInfo().chemicalInfo.aegl3Conc);

        this.chartOptions.plugins.annotation.annotations.line1.yMin = aegl1 / scale;
        this.chartOptions.plugins.annotation.annotations.line1.yMax = aegl1 / scale;
        this.chartOptions.plugins.annotation.annotations.label1.yValue = aegl1 / scale;
        this.chartOptions.plugins.annotation.annotations.label1.content = [`저위험 수준 (AEGL-1 10min ${aegl1} mg/㎡) `];

        this.chartOptions.plugins.annotation.annotations.line2.yMin = aegl2 / scale;
        this.chartOptions.plugins.annotation.annotations.line2.yMax = aegl2 / scale;
        this.chartOptions.plugins.annotation.annotations.label2.yValue = aegl2 / scale;
        this.chartOptions.plugins.annotation.annotations.label2.content = [`중위험 수준 (AEGL-2 10min ${aegl2} mg/㎡)`];

        this.chartOptions.plugins.annotation.annotations.line3.yMin = aegl3 / scale;
        this.chartOptions.plugins.annotation.annotations.line3.yMax = aegl3 / scale;
        this.chartOptions.plugins.annotation.annotations.label3.yValue = aegl3 / scale;
        this.chartOptions.plugins.annotation.annotations.label3.content = [`고위험 수준 (AEGL-3 10min ${aegl3} mg/㎡)`];


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

          if (value < 0.00000001) {
            value = 0;
          }
          this.chartData.datasets[0].data[time] = value / 1000;
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
  height: calc(100% - 20px);
  width: 100%;
}
#legend-margin {
  height: 20px;
  padding: 4px;
}
</style>