<template>
    <el-container class="layout-container">
        <el-aside width="200px" class="aside">
            <div class="logo">
                <h3>📊 记账本</h3>
            </div>
            <el-menu :default-active="activeMenu" router>
                <el-menu-item index="/account">
                    <el-icon><Notebook /></el-icon>
                    <span>账目管理</span>
                </el-menu-item>
                <el-menu-item index="/category">
                    <el-icon><Folder /></el-icon>
                    <span>分类管理</span>
                </el-menu-item>
                <el-menu-item index="/budget">
                    <el-icon><Wallet /></el-icon>
                    <span>预算管理</span>
                </el-menu-item>
                <el-menu-item index="/statistics">
                    <el-icon><DataAnalysis /></el-icon>
                    <span>统计报表</span>
                </el-menu-item>
                <el-menu-item index="/profile">
                    <el-icon><User /></el-icon>
                    <span>个人中心</span>
                </el-menu-item>
            </el-menu>
        </el-aside>
        <el-container>
            <el-header class="header">
                <div class="user-info">
                    <span>{{ store.user?.nickname || store.user?.username }}</span>
                    <el-button type="text" @click="handleLogout">退出</el-button>
                </div>
            </el-header>
            <el-main class="main">
                <router-view />
            </el-main>
        </el-container>
    </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { store } from '@/store'

const router = useRouter()
const route = useRoute()

const activeMenu = computed(() => route.path)

const handleLogout = () => {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        store.logout()
        ElMessage.success('已退出登录')
        router.push('/login')
    }).catch(() => {})
}
</script>

<style scoped>
.layout-container {
    height: 100vh;
}
.aside {
    background-color: #304156;
}
.logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
}
.logo h3 {
    margin: 0;
}
.el-menu {
    border-right: none;
    background-color: #304156;
}
.el-menu-item {
    color: #bfcbd9 !important;
}
.el-menu-item:hover {
    background-color: #263445 !important;
    color: #fff !important;
}
.el-menu-item.is-active {
    background-color: #409eff !important;
    color: #fff !important;
}
.header {
    background-color: #fff;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    padding: 0 20px;
    border-bottom: 1px solid #e6e6e6;
}
.user-info {
    display: flex;
    align-items: center;
    gap: 20px;
}
.main {
    background-color: #f5f7fa;
    padding: 20px;
    overflow-y: auto;
}
</style>
