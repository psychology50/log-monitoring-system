import {createRouter, createWebHistory} from 'vue-router'
import LogSearchView from '@/views/LogSearchView.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            redirect: '/logs'
        },
        {
            path: '/logs',
            name: 'logs',
            component: LogSearchView
        }
    ]
})

export default router
