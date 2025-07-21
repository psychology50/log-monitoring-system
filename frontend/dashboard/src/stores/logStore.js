import {defineStore} from 'pinia'
import logService from '@/services/logService'
import websocketService from '@/services/websocketService'

export const useLogStore = defineStore('logs', {
    state: () => ({
        logs: [],
        realtimeLogs: [],  // 실시간 로그 전용
        totalLogs: 0,
        currentPage: 0,
        pageSize: 20,
        searchQuery: '',
        loading: false,
        error: null,
        isRealtime: false,  // 실시간 모드 여부
        wsConnected: false  // WebSocket 연결 상태
    }),

    actions: {
        async searchLogs(query = '', page = 0) {
            this.loading = true
            this.error = null
            this.isRealtime = false  // 검색 모드로 전환

            try {
                const result = await logService.searchLogs(query, page, this.pageSize)
                this.logs = result.logs || []
                this.totalLogs = result.total || 0
                this.currentPage = page
                this.searchQuery = query
            } catch (error) {
                this.error = error.message
                console.error('검색 실패:', error)
            } finally {
                this.loading = false
            }
        },

        async loadRecentLogs() {
            this.loading = true
            this.isRealtime = false
            try {
                const logs = await logService.getRecentLogs(20)
                this.logs = logs
                this.totalLogs = logs.length
            } catch (error) {
                this.error = error.message
            } finally {
                this.loading = false
            }
        },

        // 🚀 실시간 모드 시작
        async startRealtime() {
            try {
                await websocketService.connect()
                this.wsConnected = true

                // 실시간 로그 구독
                websocketService.subscribe('/topic/logs', (logData) => {
                    this.addRealtimeLog(logData)
                })

                this.isRealtime = true
                this.realtimeLogs = []  // 실시간 로그 초기화
                console.log('🔴 실시간 모드 시작')

            } catch (error) {
                this.error = '실시간 연결 실패: ' + error.message
                console.error('WebSocket 연결 실패:', error)
            }
        },

        // 실시간 모드 중지
        stopRealtime() {
            websocketService.unsubscribe('/topic/logs')
            websocketService.disconnect()
            this.wsConnected = false
            this.isRealtime = false
            console.log('⏹️ 실시간 모드 중지')
        },

        // 새로운 실시간 로그 추가
        addRealtimeLog(logData) {
            // 맨 앞에 추가 (최신 로그가 위에)
            this.realtimeLogs.unshift(logData)

            // 최대 50개까지만 유지 (성능 고려)
            if (this.realtimeLogs.length > 50) {
                this.realtimeLogs = this.realtimeLogs.slice(0, 50)
            }

            console.log('📨 새 실시간 로그:', logData.message)
        }
    }
})