<template>
  <div class="log-table">
    <div v-if="logStore.loading" class="loading">
      로딩 중...
    </div>

    <div v-else-if="logStore.error" class="error">
      오류: {{ logStore.error }}
    </div>

    <div v-else>
      <div class="table-info">
        총 {{ logStore.totalLogs }}개의 로그
      </div>

      <table class="table">
        <thead>
        <tr>
          <th>시간</th>
          <th>레벨</th>
          <th>서비스</th>
          <th>메시지</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="log in logStore.logs" :key="log.id" :class="'level-' + log.level">
          <td class="timestamp">{{ formatTimestamp(log.timestamp) }}</td>
          <td class="level">
            <span :class="'badge level-' + log.level">{{ log.level }}</span>
          </td>
          <td class="service">{{ log.service }}</td>
          <td class="message">{{ log.message }}</td>
        </tr>
        </tbody>
      </table>

      <div v-if="logStore.logs.length === 0" class="no-data">
        로그가 없습니다.
      </div>
    </div>
  </div>
</template>

<script>
import {useLogStore} from '@/stores/logStore'

export default {
  name: 'LogTable',
  setup() {
    const logStore = useLogStore()

    const formatTimestamp = (timestamp) => {
      if (!timestamp) return '-'
      const date = new Date(timestamp)
      return date.toLocaleString('ko-KR')
    }

    return {
      logStore,
      formatTimestamp
    }
  }
}
</script>

<style scoped>
.log-table {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.loading, .error, .no-data {
  text-align: center;
  padding: 40px;
  color: #666;
}

.error {
  color: #dc3545;
}

.table-info {
  padding: 15px;
  background: #f8f9fa;
  border-bottom: 1px solid #dee2e6;
  font-weight: bold;
}

.table {
  width: 100%;
  border-collapse: collapse;
}

.table th {
  background: #f8f9fa;
  padding: 12px;
  text-align: left;
  border-bottom: 2px solid #dee2e6;
  font-weight: 600;
}

.table td {
  padding: 12px;
  border-bottom: 1px solid #dee2e6;
}

.timestamp {
  width: 200px;
  font-family: monospace;
  font-size: 14px;
}

.level {
  width: 80px;
}

.service {
  width: 120px;
}

.message {
  max-width: 400px;
  word-break: break-word;
}

.badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.level-INFO {
  background-color: #d1ecf1;
  color: #0c5460;
}

.level-WARN {
  background-color: #fff3cd;
  color: #856404;
}

.level-ERROR {
  background-color: #f8d7da;
  color: #721c24;
}

.level-DEBUG {
  background-color: #e2e3e5;
  color: #383d41;
}
</style>