<template>
    <div class="profile">
        <el-row :gutter="20">
            <el-col :span="8">
                <el-card class="user-card">
                    <div class="user-avatar">
                        <el-avatar :size="100">
                            {{ userStore.userInfo?.nickname?.charAt(0) || userStore.userInfo?.username?.charAt(0) }}
                        </el-avatar>
                    </div>
                    <div class="user-name">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</div>
                    <div class="user-username">@{{ userStore.userInfo?.username }}</div>
                    <div class="user-stats">
                        <div class="stat-item">
                            <div class="stat-value">{{ stats.transactionCount || 0 }}</div>
                            <div class="stat-label">记账笔数</div>
                        </div>
                        <div class="stat-item">
                            <div class="stat-value">{{ stats.categoryCount || 0 }}</div>
                            <div class="stat-label">自定义分类</div>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="16">
                <el-card>
                    <template #header>
                        <div class="card-header">
                            <span>个人信息</span>
                            <el-button type="primary" @click="handleEdit" v-if="!isEditing">
                                <el-icon><Edit /></el-icon>
                                编辑
                            </el-button>
                            <div v-else class="edit-actions">
                                <el-button @click="cancelEdit">取消</el-button>
                                <el-button type="primary" @click="saveProfile">保存</el-button>
                            </div>
                        </div>
                    </template>

                    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="profile-form">
                        <el-form-item label="用户名">
                            <el-input v-model="form.username" disabled />
                        </el-form-item>
                        <el-form-item label="昵称" prop="nickname">
                            <el-input v-model="form.nickname" :disabled="!isEditing" placeholder="请输入昵称" />
                        </el-form-item>
                        <el-form-item label="邮箱" prop="email">
                            <el-input v-model="form.email" :disabled="!isEditing" placeholder="请输入邮箱" />
                        </el-form-item>
                        <el-form-item label="手机号" prop="phone">
                            <el-input v-model="form.phone" :disabled="!isEditing" placeholder="请输入手机号" />
                        </el-form-item>
                        <el-form-item label="注册时间">
                            <el-input :model-value="form.createTime" disabled />
                        </el-form-item>
                    </el-form>
                </el-card>

                <el-card class="password-card" style="margin-top: 20px;">
                    <template #header>
                        <span>修改密码</span>
                    </template>

                    <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px">
                        <el-form-item label="原密码" prop="oldPassword">
                            <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" show-password />
                        </el-form-item>
                        <el-form-item label="新密码" prop="newPassword">
                            <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
                        </el-form-item>
                        <el-form-item label="确认密码" prop="confirmPassword">
                            <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="changePassword">修改密码</el-button>
                        </el-form-item>
                    </el-form>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const userStore = useUserStore()
const isEditing = ref(false)
const formRef = ref(null)
const passwordFormRef = ref(null)

const form = reactive({
    username: '',
    nickname: '',
    email: '',
    phone: '',
    createTime: ''
})

const passwordForm = reactive({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
})

const stats = ref({
    transactionCount: 0,
    categoryCount: 0
})

const rules = {
    nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
}

const validateConfirmPassword = (rule, value, callback) => {
    if (value !== passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
    } else {
        callback()
    }
}

const passwordRules = {
    oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
    newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, message: '请确认密码', trigger: 'blur' },
        { validator: validateConfirmPassword, trigger: 'blur' }
    ]
}

const loadUserInfo = async () => {
    if (!userStore.userInfo) {
        await userStore.getUserInfo()
    }
    Object.assign(form, {
        username: userStore.userInfo?.username || '',
        nickname: userStore.userInfo?.nickname || '',
        email: userStore.userInfo?.email || '',
        phone: userStore.userInfo?.phone || '',
        createTime: userStore.userInfo?.createTime || ''
    })
}

const loadStats = async () => {
    try {
        const [transactionRes, categoryRes] = await Promise.all([
            request.get('/transaction', { params: { page: 1, size: 1 } }),
            request.get('/category')
        ])
        stats.value.transactionCount = transactionRes.data.total || 0
        stats.value.categoryCount = (categoryRes.data || []).filter(c => c.userId).length
    } catch (e) {
        console.error('加载统计数据失败', e)
    }
}

const handleEdit = () => {
    isEditing.value = true
}

const cancelEdit = () => {
    isEditing.value = false
    loadUserInfo()
}

const saveProfile = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (valid) {
            try {
                await request.put('/user', {
                    nickname: form.nickname,
                    email: form.email,
                    phone: form.phone
                })
                ElMessage.success('保存成功')
                isEditing.value = false
                await userStore.getUserInfo()
            } catch (e) {
                ElMessage.error('保存失败')
            }
        }
    })
}

const changePassword = async () => {
    if (!passwordFormRef.value) return
    await passwordFormRef.value.validate(async (valid) => {
        if (valid) {
            try {
                await request.put('/user/password', {
                    oldPassword: passwordForm.oldPassword,
                    newPassword: passwordForm.newPassword
                })
                ElMessage.success('密码修改成功')
                passwordForm.oldPassword = ''
                passwordForm.newPassword = ''
                passwordForm.confirmPassword = ''
            } catch (e) {
                ElMessage.error('密码修改失败')
            }
        }
    })
}

onMounted(() => {
    loadUserInfo()
    loadStats()
})
</script>

<style scoped>
.profile {
    padding: 0;
}
.user-card {
    text-align: center;
}
.user-avatar {
    margin-bottom: 20px;
}
.user-name {
    font-size: 24px;
    font-weight: bold;
    color: #303133;
    margin-bottom: 5px;
}
.user-username {
    font-size: 14px;
    color: #909399;
    margin-bottom: 30px;
}
.user-stats {
    display: flex;
    justify-content: center;
    gap: 40px;
    padding-top: 20px;
    border-top: 1px solid #ebeef5;
}
.stat-item {
    text-align: center;
}
.stat-value {
    font-size: 28px;
    font-weight: bold;
    color: #409eff;
    margin-bottom: 5px;
}
.stat-label {
    font-size: 14px;
    color: #909399;
}
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.edit-actions {
    display: flex;
    gap: 10px;
}
.profile-form {
    max-width: 500px;
}
.password-card {
    max-width: 600px;
}
</style>