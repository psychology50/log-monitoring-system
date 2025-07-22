<template>
  <div class="chart-container">
    <h4>로그 레벨별 분포 (최근 {{ period }})</h4>
    <div class="chart-wrapper">
      <canvas ref="chartCanvas"></canvas>
    </div>
    <div v-if="loading" class="loading">로딩 중...</div>
    <div v-if="error" class="error">{{ error }}</div>
  </div>
</template>

<script>
import {onMounted, ref, watch} from 'vue'
import {ArcElement, Chart as ChartJS, Legend, Title, Tooltip} from "chart.js/auto"

ChartJS.register(ArcElement, Tooltip, Legend, Title)

export default {
  name: 'LogLevelChart',
  props: {
    data: {
      type: Object,
      default: () => ({})
    },
    loading: {
      type: Boolean,
      default: false
    },
    error: {
      type: String,
      default: null
    }
  },
  setup(props) {
    const chartCanvas = ref(null)
    const chartInstance = ref(null)
    const period = ref('24시간')

    const levelColors = {
      'INFO': '#17a2b8',
      'WARN': '#ffc107',
      'ERROR': '#dc3545',
      'DEBUG': '#6c757d',
      'FATAL': '#6f42c1'
    }

    const createChart = () => {
      if (!chartCanvas.value || !props.data.levels) return

      // 기존 차트 삭제
      if (chartInstance.value) {
        chartInstance.value.destroy()
      }

      const ctx = chartCanvas.value.getContext('2d')
      const levels = props.data.levels || {}

      const labels = Object.keys(levels)
      const values = Object.values(levels)
      const colors = labels.map(level => levelColors[level] || '#6c757d')

      chartInstance.value = new ChartJS(ctx, {
        type: 'pie',
        data: {
          labels: labels,
          datasets: [{
            data: values,
            backgroundColor: colors,
            borderWidth: 2,
            borderColor: '#fff'
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              position: 'bottom'
            },
            tooltip: {
              callbacks: {
                label: function (context) {
                  const total = context.dataset.data.reduce((a, b) => a + b, 0)
                  const percentage = ((context.parsed / total) * 100).toFixed(1)
                  return `${context.label}: ${context.parsed} (${percentage}%)`
                }
              }
            }
          }
        }
      })
    }

    watch(() => props.data, () => {
      if (props.data.period) {
        period.value = props.data.period
      }
      createChart()
    }, {deep: true})

    onMounted(() => {
      createChart()
    })

    return {
      chartCanvas,
      period
    }
  }
}
</script>

<style scoped>
.chart-container {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.chart-container h4 {
  margin: 0 0 15px 0;
  color: #333;
  text-align: center;
}

.chart-wrapper {
  position: relative;
  height: 300px;
}

.loading, .error {
  text-align: center;
  padding: 20px;
}

.error {
  color: #dc3545;
}
</style>