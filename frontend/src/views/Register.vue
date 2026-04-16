<template>
    <div class="register-container">
        <el-card class="register-card">
            <h2 class="title">用户注册</h2>
            <el-form ref="formRef" :model="form" label-width="80px">
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="form.username" placeholder="请输入用户名" />
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input v-model="form.password" type="password" placeholder="请输入密码" />
                </el-form-item>
                <el-form-item label="昵称" prop="nickname">
                    <el-input v-model="form.nickname" placeholder="请输入昵称" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleRegister" style="width: 100%">注册</el-button>
                </el-form-item>
                <el-form-item>
                    <el-button type="text" @click="$router.push('/login')" style="width: 100%">已有账号？立即登录</el-button>
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
    password: '',
    nickname: ''
})

const handleRegister = async () => {
    try {
        const user = await userApi.register(form.value)
        store.setUser(user)
        ElMessage.success('注册成功')
        router.push('/')
    } catch (error) {
        console.error('注册失败', error)
    }
}
</script>

<style scoped>
.register-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.register-card {
    width: 400px;
    padding: 20px;
}
.title {
    text-align: center;
    margin-bottom: 30px;
    color: #333;
}
</style>
