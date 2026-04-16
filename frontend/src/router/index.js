import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/Login.vue'),
        meta: { requiresAuth: false }
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/Register.vue'),
        meta: { requiresAuth: false }
    },
    {
        path: '/',
        component: () => import('@/views/Layout.vue'),
        meta: { requiresAuth: true },
        children: [
            {
                path: '',
                redirect: '/dashboard'
            },
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: () => import('@/views/Dashboard.vue')
            },
            {
                path: 'transaction',
                name: 'Transaction',
                component: () => import('@/views/Transaction.vue')
            },
            {
                path: 'category',
                name: 'Category',
                component: () => import('@/views/Category.vue')
            },
            {
                path: 'statistics',
                name: 'Statistics',
                component: () => import('@/views/Statistics.vue')
            },
            {
                path: 'budget',
                name: 'Budget',
                component: () => import('@/views/Budget.vue')
            },
            {
                path: 'profile',
                name: 'Profile',
                component: () => import('@/views/Profile.vue')
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    if (to.meta.requiresAuth && !token) {
        next('/login')
    } else if ((to.path === '/login' || to.path === '/register') && token) {
        next('/dashboard')
    } else {
        next()
    }
})

export default router
