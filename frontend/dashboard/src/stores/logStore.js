import {defineStore} from 'pinia'
import logService from '@/services/logService'

export const useLogStore = defineStore('logs', {
    state: () => ({
        logs: [],
        totalLogs: 0,
        currentPage: 0,
        pageSize: 20,
        searchQuery: '',
        loading: false,
        error: null
    }),

    actions: {
        async searchLogs(query = '', page = 0) {
            this.loading = true
            this.error = null

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
            try {
                const logs = await logService.getRecentLogs(20)
                this.logs = logs
                this.totalLogs = logs.length
            } catch (error) {
                this.error = error.message
            } finally {
                this.loading = false
            }
        }
    }
})