import { defineStore } from 'pinia'
import request from '@/utils/request'

export const useUserStore = defineStore('user', {
    state: () => ({
        userInfo: null,
        token: localStorage.getItem('token') || ''
    }),
    actions: {
        async login(formData) {
            const res = await request.post('/user/login', formData)
            this.token = res.data.token
            this.userInfo = res.data.userInfo
            localStorage.setItem('token', res.data.token)
            return res
        },
        async register(formData) {
            return await request.post('/user/register', formData)
        },
        async getUserInfo() {
            const res = await request.get('/user/info')
            this.userInfo = res.data
            return res
        },
        async updateUserInfo(formData) {
            return await request.put('/user/update', formData)
        },
        logout() {
            this.token = ''
            this.userInfo = null
            localStorage.removeItem('token')
        }
    }
})
