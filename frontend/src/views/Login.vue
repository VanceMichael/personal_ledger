<template>
    <div class="login-container">
        <el-card class="login-card">
            <h2 class="title">个人记账本</h2>
            <el-form ref="formRef" :model="form" label-width="80px">
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="form.username" placeholder="请输入用户名" />
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input v-model="form.password" type="password" placeholder="请输入密码" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleLogin" style="width: 100%">登录</el-button>
                </el-form-item>
                <el-form-item>
                    <el-button type="text" @click="$router.push('/register')" style="width: 100%">还没有账号？立即注册</el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userApi } from '@/api'
import { store } from '@/store'

const router = useRouter()
const formRef = ref()
const form = ref({
    username: '',
    password: ''
})

const handleLogin = async () => {
    try {
        const user = await userApi.login(form.value)
        store.setUser(user)
        ElMessage.success('登录成功')
        router.push('/')
    } catch (error) {
        console.error('登录失败', error)
    }
}
</script>

<style scoped>
.login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
    width: 400px;
    padding: 20px;
}
.title {
    text-align: center;
    margin-bottom: 30px;
    color: #333;
}
</style>
