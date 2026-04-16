<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>
      <el-form ref="formRef" :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="form.password" type="password" placeholder="不修改请留空" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdate" :loading="loading">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { updateUser, getUserInfo } from '@/api/user'

const formRef = ref()
const loading = ref(false)
const user = JSON.parse(localStorage.getItem('user') || '{}')
const form = ref({
  username: '',
  nickname: '',
  email: '',
  password: ''
})

const loadUserInfo = async () => {
  const data = await getUserInfo(user.id)
  form.value.username = data.username
  form.value.nickname = data.nickname
  form.value.email = data.email || ''
}

const handleUpdate = async () => {
  try {
    loading.value = true
    const updateData = {
      nickname: form.value.nickname,
      email: form.value.email
    }
    if (form.value.password) {
      updateData.password = form.value.password
    }
    const updatedUser = await updateUser(user.id, updateData)
    localStorage.setItem('user', JSON.stringify(updatedUser))
    ElMessage.success('修改成功')
    form.value.password = ''
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}

.profile-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
}
</style>
