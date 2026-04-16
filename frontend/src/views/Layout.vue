<template>
    <el-container class="layout-container">
        <el-aside width="200px">
            <div class="logo">个人记账本</div>
            <el-menu
                :default-active="activeMenu"
                router
                background-color="#304156"
                text-color="#bfcbd9"
                active-text-color="#409EFF"
            >
                <el-menu-item index="/dashboard">
                    <el-icon><Odometer /></el-icon>
                    <span>首页</span>
                </el-menu-item>
                <el-menu-item index="/transaction">
                    <el-icon><Document /></el-icon>
                    <span>账目管理</span>
                </el-menu-item>
                <el-menu-item index="/category">
                    <el-icon><Folder /></el-icon>
                    <span>分类管理</span>
                </el-menu-item>
                <el-menu-item index="/statistics">
                    <el-icon><DataAnalysis /></el-icon>
                    <span>统计报表</span>
                </el-menu-item>
                <el-menu-item index="/budget">
                    <el-icon><Wallet /></el-icon>
                    <span>预算管理</span>
                </el-menu-item>
                <el-menu-item index="/profile">
                    <el-icon><User /></el-icon>
                    <span>个人中心</span>
                </el-menu-item>
            </el-menu>
        </el-aside>
        <el-container>
            <el-header>
                <div class="header-right">
                    <el-dropdown @command="handleCommand">
                        <span class="user-info">
                            <el-icon><User /></el-icon>
                            {{ userStore.userInfo?.nickname || userStore.userInfo?.username }}
                            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                        </span>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </div>
            </el-header>
            <el-main>
                <router-view />
            </el-main>
        </el-container>
    </el-container>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

onMounted(async () => {
    if (!userStore.userInfo) {
        await userStore.getUserInfo()
    }
})

const handleCommand = (command) => {
    if (command === 'profile') {
        router.push('/profile')
    } else if (command === 'logout') {
        ElMessageBox.confirm('确定要退出登录吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            userStore.logout()
            router.push('/login')
        }).catch(() => {})
    }
}
</script>

<style scoped>
.layout-container {
    height: 100vh;
}
.el-aside {
    background-color: #304156;
    color: white;
}
.logo {
    height: 60px;
    line-height: 60px;
    text-align: center;
    font-size: 18px;
    font-weight: bold;
    color: white;
    border-bottom: 1px solid #1f2d3d;
}
.el-header {
    background-color: white;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}
.header-right {
    display: flex;
    align-items: center;
}
.user-info {
    display: flex;
    align-items: center;
    cursor: pointer;
    font-size: 14px;
}
.el-main {
    background-color: #f0f2f5;
    overflow-y: auto;
}
</style>
