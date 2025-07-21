import axios from 'axios'

const API_BASE_URL = 'http://localhost:8084/api'

const logService = {
    // 로그 검색
    async searchLogs(query = '', page = 0, size = 20) {
        try {
            const response = await axios.get(`${API_BASE_URL}/logs/search`, {
                params: {query, page, size}
            })
            return response.data
        } catch (error) {
            console.error('로그 검색 실패:', error)
            throw error
        }
    },

    // 최근 로그 조회
    async getRecentLogs(limit = 10) {
        try {
            const response = await axios.get(`${API_BASE_URL}/logs/recent`, {
                params: {limit}
            })
            return response.data
        } catch (error) {
            console.error('최근 로그 조회 실패:', error)
            throw error
        }
    },

    // 통계 조회
    async getStats() {
        try {
            const response = await axios.get(`${API_BASE_URL}/logs/stats`)
            return response.data
        } catch (error) {
            console.error('통계 조회 실패:', error)
            throw error
        }
    }
}

export default logService