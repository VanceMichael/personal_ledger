import { reactive } from 'vue'

const state = reactive({
    user: JSON.parse(localStorage.getItem('user') || 'null')
})

export const store = {
    get user() {
        return state.user
    },
    setUser(user) {
        state.user = user
        if (user) {
            localStorage.setItem('user', JSON.stringify(user))
        } else {
            localStorage.removeItem('user')
        }
    },
    logout() {
        state.user = null
        localStorage.removeItem('user')
    }
}
