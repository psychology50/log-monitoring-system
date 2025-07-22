import axios from 'axios'

const API_BASE_URL = 'http://localhost:8084/api'

const analyticsService = {
    // 대시보드 요약 통계
    async getDashboardSummary() {
        try {
            const response = await axios.get(`${API_BASE_URL}/analytics/dashboard`)
            return response.data
        } catch (error) {
            console.error('대시보드 요약 조회 실패:', error)
            throw error
        }
    },

    // 로그 레벨별 통계
    async getLogLevelStats(hours = 24) {
        try {
            const response = await axios.get(`${API_BASE_URL}/analytics/levels`, {
                params: {hours}
            })
            return response.data
        } catch (error) {
            console.error('로그 레벨 통계 조회 실패:', error)
            throw error
        }
    },

    // 시계열 통계
    async getTimeSeriesStats(hours = 6) {
        try {
            const response = await axios.get(`${API_BASE_URL}/analytics/timeline`, {
                params: {hours}
            })
            return response.data
        } catch (error) {
            console.error('시계열 통계 조회 실패:', error)
            throw error
        }
    },

    // 서비스별 통계
    async getServiceStats(hours = 24) {
        try {
            const response = await axios.get(`${API_BASE_URL}/analytics/services`, {
                params: {hours}
            })
            return response.data
        } catch (error) {
            console.error('서비스 통계 조회 실패:', error)
            throw error
        }
    }
}

export default analyticsService