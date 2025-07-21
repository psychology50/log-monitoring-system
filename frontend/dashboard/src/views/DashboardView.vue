<template>
  <div class="dashboard">
    <h1>로그 모니터링 대시보드</h1>

    <div class="dashboard-grid">
      <!-- 실시간 로그 스트림 -->
      <div class="realtime-section">
        <RealtimeLogs/>
      </div>

      <!-- 로그 검색 섹션 -->
      <div class="search-section">
        <h3>로그 검색</h3>
        <SearchForm/>
        <LogTable/>
      </div>
    </div>
  </div>
</template>

<script>
import {onMounted, onUnmounted} from 'vue'
import RealtimeLogs from '@/components/RealtimeLogs.vue'
import SearchForm from '@/components/SearchForm.vue'
import LogTable from '@/components/LogTable.vue'
import {useLogStore} from '@/stores/logStore'

export default {
  name: 'DashboardView',
  components: {
    RealtimeLogs,
    SearchForm,
    LogTable
  },
  setup() {
    const logStore = useLogStore()

    onMounted(() => {
      // 페이지 로드 시 최근 로그 자동 로드
      logStore.loadRecentLogs()
    })

    onUnmounted(() => {
      // 페이지 종료 시 WebSocket 연결 정리
      if (logStore.isRealtime) {
        logStore.stopRealtime()
      }
    })

    return {}
  }
}
</script>

<style scoped>
.dashboard {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.dashboard h1 {
  color: #333;
  margin-bottom: 30px;
  text-align: center;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  min-height: 600px;
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

@media (max-width: 1024px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}
</style>