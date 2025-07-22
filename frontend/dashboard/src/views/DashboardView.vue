<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1>📊 로그 모니터링 대시보드</h1>
      <div class="refresh-controls">
        <button :disabled="loading" class="refresh-btn" @click="refreshData">
          🔄 새로고침
        </button>
        <span class="last-updated">
          마지막 업데이트: {{ formatLastUpdated }}
        </span>
      </div>
    </div>

    <!-- 통계 카드들 -->
    <StatsCards :stats="dashboardStats"/>

    <!-- 차트 영역 -->
    <div class="charts-grid">
      <div class="chart-section">
        <LogLevelChart
            :data="dashboardStats.recent24h || {}"
            :error="error"
            :loading="loading"
        />
      </div>

      <div class="chart-section">
        <TimeSeriesChart
            :data="dashboardStats.timeSeries || {}"
            :error="error"
            :loading="loading"
        />
      </div>
    </div>

    <!-- 실시간 로그 & 검색 영역 -->
    <div class="dashboard-grid">
      <div class="realtime-section">
        <RealtimeLogs/>
      </div>

      <div class="search-section">
        <h3>로그 검색</h3>
        <SearchForm/>
        <LogTable/>
      </div>
    </div>
  </div>
</template>

<script>
import {computed, onMounted, onUnmounted, ref} from 'vue'
import StatsCards from '@/components/StatsCards.vue'
import LogLevelChart from '@/components/charts/LogLevelChart.vue'
import TimeSeriesChart from '@/components/charts/TimeSeriesChart.vue'
import RealtimeLogs from '@/components/RealtimeLogs.vue'
import SearchForm from '@/components/SearchForm.vue'
import LogTable from '@/components/LogTable.vue'
import {useLogStore} from '@/stores/logStore'
import analyticsService from '@/services/analyticsService'

export default {
  name: 'DashboardView',
  components: {
    StatsCards,
    LogLevelChart,
    TimeSeriesChart,
    RealtimeLogs,
    SearchForm,
    LogTable
  },
  setup() {
    const logStore = useLogStore()
    const dashboardStats = ref({})
    const loading = ref(false)
    const error = ref(null)
    const lastUpdated = ref(null)
    const refreshInterval = ref(null)

    const formatLastUpdated = computed(() => {
      if (!lastUpdated.value) return '없음'
      return new Date(lastUpdated.value).toLocaleTimeString('ko-KR')
    })

    const loadDashboardData = async () => {
      loading.value = true
      error.value = null

      try {
        const stats = await analyticsService.getDashboardSummary()
        dashboardStats.value = stats
        lastUpdated.value = stats.lastUpdated || Date.now()
      } catch (err) {
        error.value = err.message
        console.error('대시보드 데이터 로드 실패:', err)
      } finally {
        loading.value = false
      }
    }

    const refreshData = () => {
      loadDashboardData()
      logStore.loadRecentLogs()
    }

    const startAutoRefresh = () => {
      // 30초마다 자동 새로고침
      refreshInterval.value = setInterval(() => {
        loadDashboardData()
      }, 30000)
    }

    const stopAutoRefresh = () => {
      if (refreshInterval.value) {
        clearInterval(refreshInterval.value)
        refreshInterval.value = null
      }
    }

    onMounted(() => {
      loadDashboardData()
      logStore.loadRecentLogs()
      startAutoRefresh()
    })

    onUnmounted(() => {
      stopAutoRefresh()
      if (logStore.isRealtime) {
        logStore.stopRealtime()
      }
    })

    return {
      logStore,
      dashboardStats,
      loading,
      error,
      formatLastUpdated,
      refreshData
    }
  }
}
</script>

<style scoped>
.dashboard {
  max-width: 1600px;
  margin: 0 auto;
  padding: 20px;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #e9ecef;
}

.dashboard-header h1 {
  color: #333;
  margin: 0;
}

.refresh-controls {
  display: flex;
  align-items: center;
  gap: 15px;
}

.refresh-btn {
  padding: 8px 16px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
}

.refresh-btn:hover {
  background: #0056b3;
}

.refresh-btn:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

.last-updated {
  font-size: 14px;
  color: #6c757d;
}

.charts-grid {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 20px;
  margin-bottom: 30px;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  min-height: 500px;
}

.realtime-section, .search-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-section h3 {
  margin-top: 0;
  color: #333;
}

@media (max-width: 1200px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }

  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    gap: 15px;
  }
}
</style>