<template>
    <div class="budget">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>预算管理</span>
                    <el-button type="primary" @click="handleSetBudget">
                        <el-icon><Setting /></el-icon>
                        设置预算
                    </el-button>
                </div>
            </template>

            <div class="budget-selector">
                <el-date-picker
                    v-model="selectedMonth"
                    type="month"
                    placeholder="选择月份"
                    format="YYYY-MM"
                    value-format="YYYY-MM"
                    @change="loadBudget"
                />
            </div>

            <div v-if="budgetInfo" class="budget-card">
                <div class="budget-header">
                    <div class="budget-title">{{ selectedMonth }} 月度预算</div>
                    <el-tag :type="budgetStatus.type" size="large">
                        {{ budgetStatus.text }}
                    </el-tag>
                </div>
                
                <div class="budget-stats">
                    <div class="budget-stat">
                        <div class="stat-label">预算总额</div>
                        <div class="stat-value">¥{{ budgetInfo.amount }}</div>
                    </div>
                    <div class="budget-stat">
                        <div class="stat-label">已支出</div>
                        <div class="stat-value expense">¥{{ budgetInfo.used }}</div>
                    </div>
                    <div class="budget-stat">
                        <div class="stat-label">剩余预算</div>
                        <div class="stat-value" :class="budgetInfo.remaining <= 0 ? 'danger' : 'success'">
                            ¥{{ budgetInfo.remaining }}
                        </div>
                    </div>
                    <div class="budget-stat">
                        <div class="stat-label">使用进度</div>
                        <div class="stat-value">{{ budgetInfo.percentage }}%</div>
                    </div>
                </div>

                <div class="progress-section">
                    <div class="progress-label">
                        <span>预算使用进度</span>
                        <span>{{ budgetInfo.percentage }}%</span>
                    </div>
                    <el-progress
                        :percentage="Math.min(budgetInfo.percentage, 100)"
                        :color="progressColor"
                        :stroke-width="20"
                    />
                </div>

                <div v-if="budgetInfo.status !== 'normal'" class="warning-box" :class="budgetInfo.status">
                    <el-icon class="warning-icon">
                        <component :is="budgetInfo.status === 'warning' ? 'Warning' : 'CircleClose'" />
                    </el-icon>
                    <div class="warning-text">
                        {{ budgetInfo.status === 'warning' ? '注意：预算使用已超过80%，请合理控制支出！' : '警告：本月预算已超支，请及时调整！' }}
                    </div>
                </div>
            </div>

            <el-empty v-else description="暂无预算设置，点击右上角按钮设置本月预算" />
        </el-card>

        <el-dialog
            v-model="dialogVisible"
            title="设置月度预算"
            width="400px"
            :close-on-click-modal="false"
        >
            <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
                <el-form-item label="月份">
                    <el-date-picker
                        v-model="form.month"
                        type="month"
                        placeholder="选择月份"
                        format="YYYY-MM"
                        value-format="YYYY-MM"
                        style="width: 100%"
                    />
                </el-form-item>
                <el-form-item label="预算金额" prop="amount">
                    <el-input-number
                        v-model="form.amount"
                        :min="1"
                        :precision="2"
                        style="width: 100%"
                        placeholder="请输入预算金额"
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitForm">确定</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import dayjs from 'dayjs'

const selectedMonth = ref(dayjs().format('YYYY-MM'))
const budgetInfo = ref(null)
const dialogVisible = ref(false)
const formRef = ref(null)

const form = reactive({
    month: '',
    amount: null
})

const rules = {
    amount: [{ required: true, message: '请输入预算金额', trigger: 'blur' }]
}

const budgetStatus = computed(() => {
    if (!budgetInfo.value) return { type: 'info', text: '未设置' }
    const status = budgetInfo.value.status
    if (status === 'normal') return { type: 'success', text: '正常' }
    if (status === 'warning') return { type: 'warning', text: '预警' }
    return { type: 'danger', text: '超支' }
})

const progressColor = computed(() => {
    if (!budgetInfo.value) return '#409eff'
    const status = budgetInfo.value.status
    if (status === 'normal') return '#67c23a'
    if (status === 'warning') return '#e6a23c'
    return '#f56c6c'
})

const loadBudget = async () => {
    if (!selectedMonth.value) return
    
    const [year, month] = selectedMonth.value.split('-').map(Number)
    
    try {
        const res = await request.get('/budget', { params: { year, month } })
        budgetInfo.value = res.data
    } catch (e) {
        budgetInfo.value = null
    }
}

const handleSetBudget = () => {
    form.month = selectedMonth.value
    form.amount = budgetInfo.value?.amount || null
    dialogVisible.value = true
}

const submitForm = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (valid) {
            try {
                const [year, month] = form.month.split('-').map(Number)
                await request.post('/budget', { year, month, amount: form.amount })
                ElMessage.success('预算设置成功')
                dialogVisible.value = false
                loadBudget()
            } catch (e) {
                ElMessage.error('预算设置失败')
            }
        }
    })
}

onMounted(() => {
    loadBudget()
})
</script>

<style scoped>
.budget {
    padding: 0;
}
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.budget-selector {
    margin-bottom: 20px;
}
.budget-card {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 12px;
    padding: 30px;
    color: white;
}
.budget-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
}
.budget-title {
    font-size: 20px;
    font-weight: bold;
}
.budget-stats {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 30px;
}
.budget-stat {
    text-align: center;
}
.stat-label {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.8);
    margin-bottom: 8px;
}
.stat-value {
    font-size: 24px;
    font-weight: bold;
}
.stat-value.expense {
    color: #ffccc7;
}
.stat-value.success {
    color: #d9f7be;
}
.stat-value.danger {
    color: #ffa39e;
}
.progress-section {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 8px;
    padding: 20px;
}
.progress-label {
    display: flex;
    justify-content: space-between;
    margin-bottom: 15px;
    font-size: 14px;
}
.warning-box {
    margin-top: 20px;
    padding: 15px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    gap: 10px;
}
.warning-box.warning {
    background: rgba(230, 162, 60, 0.2);
    border: 1px solid rgba(230, 162, 60, 0.5);
}
.warning-box.over {
    background: rgba(245, 108, 108, 0.2);
    border: 1px solid rgba(245, 108, 108, 0.5);
}
.warning-icon {
    font-size: 24px;
}
.warning-box.warning .warning-icon {
    color: #e6a23c;
}
.warning-box.over .warning-icon {
    color: #f56c6c;
}
.warning-text {
    font-size: 14px;
}
</style>