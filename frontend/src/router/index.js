import { createRouter, createWebHistory } from 'vue-router'
import { store } from '@/store'

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
        name: 'Layout',
        component: () => import('@/views/Layout.vue'),
        meta: { requiresAuth: true },
        redirect: '/account',
        children: [
            {
                path: 'account',
                name: 'Account',
                component: () => import('@/views/AccountList.vue')
            },
            {
                path: 'category',
                name: 'Category',
                component: () => import('@/views/CategoryList.vue')
            },
            {
                path: 'budget',
                name: 'Budget',
                component: () => import('@/views/BudgetManage.vue')
            },
            {
                path: 'statistics',
                name: 'Statistics',
                component: () => import('@/views/Statistics.vue')
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
    if (to.meta.requiresAuth && !store.user) {
        next('/login')
    } else if ((to.path === '/login' || to.path === '/register') && store.user) {
        next('/')
    } else {
        next()
    }
})

export default router
