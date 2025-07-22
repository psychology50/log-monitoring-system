<template>
  <div class="chart-container">
    <h4>시간대별 로그 추이 (최근 {{ period }})</h4>
    <div class="chart-wrapper">
      <canvas ref="chartCanvas"></canvas>
    </div>
    <div v-if="loading" class="loading">로딩 중...</div>
    <div v-if="error" class="error">{{ error }}</div>
  </div>
</template>

<script>
import {onMounted, ref, watch} from 'vue'
import {
  CategoryScale,
  Chart as ChartJS,
  Filler,
  Legend,
  LinearScale,
  LineElement,
  PointElement,
  Title,
  Tooltip
} from 'chart.js'

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend,
    Filler
)

export default {
  name: 'TimeSeriesChart',
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
    const period = ref('6시간')

    const createChart = () => {
      if (!chartCanvas.value || !props.data.timeline) return

      // 기존 차트 삭제
      if (chartInstance.value) {
        chartInstance.value.destroy()
      }

      const ctx = chartCanvas.value.getContext('2d')
      const timeline = props.data.timeline || []

      const labels = timeline.map(point => {
        const date = new Date(point.time)
        return date.toLocaleTimeString('ko-KR', {
          hour: '2-digit',
          minute: '2-digit'
        })
      })

      const values = timeline.map(point => point.count)

      chartInstance.value = new ChartJS(ctx, {
        type: 'line',
        data: {
          labels: labels,
          datasets: [{
            label: '로그 수',
            data: values,
            borderColor: '#007bff',
            backgroundColor: 'rgba(0, 123, 255, 0.1)',
            borderWidth: 2,
            fill: true,
            tension: 0.3,
            pointBackgroundColor: '#007bff',
            pointBorderColor: '#fff',
            pointBorderWidth: 2,
            pointRadius: 4
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: false
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              grid: {
                color: 'rgba(0,0,0,0.1)'
              }
            },
            x: {
              grid: {
                color: 'rgba(0,0,0,0.1)'
              }
            }
          },
          elements: {
            point: {
              hoverRadius: 8
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
  height: 250px;
}

.loading, .error {
  text-align: center;
  padding: 20px;
}

.error {
  color: #dc3545;
}
</style>