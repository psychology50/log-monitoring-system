<template>
  <div class="realtime-logs">
    <div class="realtime-header">
      <h3>실시간 로그 스트림</h3>
      <div class="controls">
        <button
            v-if="!logStore.isRealtime"
            class="start-button"
            @click="startRealtime"
        >
          🔴 실시간 시작
        </button>
        <button
            v-else
            class="stop-button"
            @click="stopRealtime"
        >
          ⏹️ 중지
        </button>
        <span
            :class="['status', logStore.wsConnected ? 'connected' : 'disconnected']"
        >
          {{ logStore.wsConnected ? '연결됨' : '연결안됨' }}
        </span>
      </div>
    </div>

    <div ref="logStream" class="log-stream">
      <div v-if="!logStore.isRealtime" class="not-active">
        실시간 모드가 비활성화되었습니다. "실시간 시작" 버튼을 클릭하세요.
      </div>

      <div v-else-if="logStore.realtimeLogs.length === 0" class="waiting">
        실시간 로그를 기다리고 있습니다...
      </div>

      <div
          v-for="log in logStore.realtimeLogs"
          v-else
          :key="log.id"
          :class="'level-' + log.level"
          class="log-item"
      >
        <div class="log-time">{{ formatTimestamp(log.timestamp) }}</div>
        <div class="log-content">
          <span :class="'level-' + log.level" class="log-level">{{ log.level }}</span>
          <span class="log-service">[{{ log.service }}]</span>
          <span class="log-message">{{ log.message }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {useLogStore} from '@/stores/logStore'

export default {
  name: 'RealtimeLogs',
  setup() {
    const logStore = useLogStore()

    const startRealtime = async () => {
      await logStore.startRealtime()
    }

    const stopRealtime = () => {
      logStore.stopRealtime()
    }

    const formatTimestamp = (timestamp) => {
      if (!timestamp) return '-'
      const date = new Date(timestamp)
      return date.toLocaleTimeString('ko-KR')
    }

    return {
      logStore,
      startRealtime,
      stopRealtime,
      formatTimestamp
    }
  }
}
</script>

<style scoped>
.realtime-logs {
  background: #1a1a1a;
  color: #00ff00;
  border-radius: 8px;
  height: 400px;
  display: flex;
  flex-direction: column;
  font-family: 'Courier New', monospace;
}

.realtime-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: #333;
  border-radius: 8px 8px 0 0;
}

.realtime-header h3 {
  margin: 0;
  color: #fff;
}

.controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.start-button, .stop-button {
  padding: 5px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
}

.start-button {
  background: #28a745;
  color: white;
}

.stop-button {
  background: #dc3545;
  color: white;
}

.status {
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
}

.status.connected {
  background: #28a745;
  color: white;
}

.status.disconnected {
  background: #6c757d;
  color: white;
}

.log-stream {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  background: #000;
}

.not-active, .waiting {
  text-align: center;
  padding: 20px;
  color: #888;
}

.log-item {
  margin-bottom: 5px;
  padding: 5px;
  border-left: 3px solid #00ff00;
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.log-item.level-ERROR {
  border-left-color: #ff6b6b;
  color: #ff6b6b;
}

.log-item.level-WARN {
  border-left-color: #feca57;
  color: #feca57;
}

.log-item.level-INFO {
  border-left-color: #48cae4;
  color: #48cae4;
}

.log-time {
  font-size: 12px;
  color: #888;
  margin-bottom: 2px;
}

.log-content {
  display: flex;
  gap: 10px;
  align-items: center;
}

.log-level {
  font-size: 12px;
  font-weight: bold;
  padding: 2px 6px;
  border-radius: 3px;
  background: rgba(255, 255, 255, 0.1);
}

.log-service {
  color: #888;
  font-size: 14px;
}

.log-message {
  font-size: 14px;
}
</style>