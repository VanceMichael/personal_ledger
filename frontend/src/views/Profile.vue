<template>
    <el-card>
        <template #header>
            <span>个人信息</span>
        </template>
        <el-form :model="form" label-width="100px" style="max-width: 500px">
            <el-form-item label="用户名">
                <el-input v-model="form.username" disabled />
            </el-form-item>
            <el-form-item label="昵称">
                <el-input v-model="form.nickname" />
            </el-form-item>
            <el-form-item label="邮箱">
                <el-input v-model="form.email" />
            </el-form-item>
            <el-form-item label="手机号">
                <el-input v-model="form.phone" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="handleUpdate">保存修改</el-button>
            </el-form-item>
        </el-form>
    </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { userApi } from '@/api'
import { store } from '@/store'

const form = ref({
    id: null,
    username: '',
    nickname: '',
    email: '',
    phone: ''
})

onMounted(() => {
    if (store.user) {
        form.value = { ...store.user }
    }
})

const handleUpdate = async () => {
    try {
        await userApi.update(form.value)
        store.setUser(form.value)
        ElMessage.success('更新成功')
    } catch (error) {
        console.error('更新失败', error)
    }
}
</script>
