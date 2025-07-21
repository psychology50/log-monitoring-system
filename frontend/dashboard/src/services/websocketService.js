import {Client} from '@stomp/stompjs'
import SockJS from 'sockjs-client'

class WebSocketService {
    constructor() {
        this.client = null
        this.connected = false
        this.subscribers = new Map()
    }

    connect() {
        if (this.connected) {
            console.log('WebSocket already connected')
            return Promise.resolve()
        }

        return new Promise((resolve, reject) => {
            this.client = new Client({
                // WebSocket 연결 설정
                webSocketFactory: () => new SockJS('http://localhost:8084/ws'),

                // 연결 성공 시
                onConnect: (frame) => {
                    console.log('✅ WebSocket Connected:', frame)
                    this.connected = true
                    resolve()
                },

                // 연결 실패 시
                onStompError: (frame) => {
                    console.error('❌ WebSocket Error:', frame)
                    this.connected = false
                    reject(frame)
                },

                // 연결 해제 시
                onDisconnect: () => {
                    console.log('🔌 WebSocket Disconnected')
                    this.connected = false
                },

                // 디버그 로그 (개발용)
                debug: (str) => {
                    console.log('WebSocket Debug:', str)
                }
            })

            this.client.activate()
        })
    }

    disconnect() {
        if (this.client) {
            this.client.deactivate()
            this.connected = false
        }
    }

    // 특정 토픽 구독
    subscribe(topic, callback) {
        if (!this.connected) {
            console.warn('WebSocket not connected. Call connect() first.')
            return null
        }

        const subscription = this.client.subscribe(topic, (message) => {
            try {
                const data = JSON.parse(message.body)
                callback(data)
            } catch (error) {
                console.error('Error parsing WebSocket message:', error)
            }
        })

        this.subscribers.set(topic, subscription)
        return subscription
    }

    // 구독 해제
    unsubscribe(topic) {
        const subscription = this.subscribers.get(topic)
        if (subscription) {
            subscription.unsubscribe()
            this.subscribers.delete(topic)
        }
    }

    // 메시지 전송
    send(destination, message) {
        if (this.connected && this.client) {
            this.client.publish({
                destination,
                body: JSON.stringify(message)
            })
        }
    }
}

const websocketService = new WebSocketService()
export default websocketService