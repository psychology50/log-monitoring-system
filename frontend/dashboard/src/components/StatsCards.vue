<template>
  <div class="stats-grid">
    <div class="stat-card total">
      <div class="stat-icon">📊</div>
      <div class="stat-content">
        <div class="stat-label">총 로그 수 (24시간)</div>
        <div class="stat-value">{{ formatNumber(stats.recent24h?.total || 0) }}</div>
      </div>
    </div>

    <div class="stat-card recent">
      <div class="stat-icon">⏰</div>
      <div class="stat-content">
        <div class="stat-label">최근 1시간</div>
        <div class="stat-value">{{ formatNumber(stats.recent1h?.total || 0) }}</div>
      </div>
    </div>

    <div class="stat-card errors">
      <div class="stat-icon">🚨</div>
      <div class="stat-content">
        <div class="stat-label">에러 로그 (24시간)</div>
        <div class="stat-value error-count">
          {{ formatNumber(getErrorCount(stats.recent24h)) }}
        </div>
      </div>
    </div>

    <div class="stat-card services">
      <div class="stat-icon">🔧</div>
      <div class="stat-content">
        <div class="stat-label">활성 서비스</div>
        <div class="stat-value">{{ getServiceCount(stats.services) }}</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'StatsCards',
  props: {
    stats: {
      type: Object,
      default: () => ({})
    }
  },
  setup(props) {
    const formatNumber = (num) => {
      if (num >= 1000000) return (num / 1000000).toFixed(1) + 'M'
      if (num >= 1000) return (num / 1000).toFixed(1) + 'K'
      return num.toString()
    }

    const getErrorCount = (levelStats) => {
      if (!levelStats?.levels) return 0
      return (levelStats.levels.ERROR || 0) + (levelStats.levels.FATAL || 0)
    }

    const getServiceCount = (serviceStats) => {
      if (!serviceStats?.services) return 0
      return Object.keys(serviceStats.services).length
    }

    return {
      formatNumber,
      getErrorCount,
      getServiceCount
    }
  }
}
</script>

<style scoped>
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  font-size: 2.5rem;
  margin-right: 15px;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #6c757d;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 2rem;
  font-weight: bold;
  color: #333;
}

.total .stat-value {
  color: #007bff;
}

.recent .stat-value {
  color: #28a745;
}

.error-count {
  color: #dc3545 !important;
}

.services .stat-value {
  color: #6f42c1;
}
</style>