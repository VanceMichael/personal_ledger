<template>
    <div class="register-container">
        <div class="register-box">
            <h2>用户注册</h2>
            <el-form ref="formRef" :model="form" label-width="80px">
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="form.username" placeholder="请输入用户名" />
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
                </el-form-item>
                <el-form-item label="昵称" prop="nickname">
                    <el-input v-model="form.nickname" placeholder="请输入昵称" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleRegister" style="width: 100%">注册</el-button>
                </el-form-item>
                <el-form-item>
                    <el-button @click="$router.push('/login')" style="width: 100%">已有账号？去登录</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const form = ref({
    username: '',
    password: '',
    nickname: ''
})

const handleRegister = async () => {
    try {
        await userStore.register(form.value)
        ElMessage.success('注册成功，请登录')
        router.push('/login')
    } catch (error) {
        console.error(error)
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
.register-box {
    background: white;
    padding: 40px;
    border-radius: 10px;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
    width: 400px;
}
.register-box h2 {
    text-align: center;
    margin-bottom: 30px;
    color: #333;
}
</style>
