<template>
    <div class="budget-container">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>预算管理</span>
                    <el-button type="primary" @click="openDialog">
                        <el-icon><Edit /></el-icon>
                        设置预算
                    </el-button>
                </div>
            </template>

            <div class="month-selector">
                <el-date-picker
                    v-model="selectedMonth"
                    type="month"
                    placeholder="选择月份"
                    @change="loadData"
                />
            </div>

            <div class="budget-overview" v-if="budgetData">
                <el-statistic title="月度预算" :value="budgetData.budget" prefix="¥" />
                <el-statistic title="已支出" :value="budgetData.spent" prefix="¥" />
                <el-statistic title="剩余" :value="budgetData.remaining" prefix="¥" :value-style="{ color: budgetData.remaining >= 0 ? '#67c23a' : '#f56c6c' }" />
                <el-statistic title="使用率" :value="budgetData.usageRate" suffix="%" :value-style="{ color: budgetData.usageRate >= 100 ? '#f56c6c' : '#409eff' }" />
            </div>

            <div class="progress-section" v-if="budgetData">
                <div class="progress-header">
                    <span>预算使用进度</span>
                    <span>{{ budgetData.usageRate }}%</span>
                </div>
                <el-progress
                    :percentage="Math.min(budgetData.usageRate, 100)"
                    :color="getProgressColor(budgetData.usageRate)"
                    :stroke-width="20"
                />
                <div class="warning-text" v-if="budgetData.usageRate >= 100">
                    <el-icon><Warning /></el-icon>
                    已超支！请注意控制消费
                </div>
                <div class="warning-text" v-else-if="budgetData.usageRate >= 80">
                    <el-icon><InfoFilled /></el-icon>
                    预算使用超过80%，请注意控制消费
                </div>
            </div>

            <div class="no-budget" v-else>
                <el-empty description="暂无预算设置">
                    <el-button type="primary" @click="openDialog">设置本月预算</el-button>
                </el-empty>
            </div>
        </el-card>

        <el-dialog v-model="dialogVisible" title="设置月度预算" width="400px">
            <el-form :model="form" label-width="80px">
                <el-form-item label="月份">
                    <el-date-picker v-model="form.month" type="month" placeholder="选择月份" />
                </el-form-item>
                <el-form-item label="预算金额">
                    <el-input-number v-model="form.amount" :min="0" :precision="2" style="width: 100%" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSave">确定</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { budgetApi, accountApi } from '@/api'
import { store } from '@/store'

const selectedMonth = ref(new Date())
const budgetData = ref(null)
const dialogVisible = ref(false)
const form = ref({
    userId: null,
    month: new Date(),
    amount: 0
})

onMounted(() => {
    loadData()
})

const formatMonth = (date) => {
    const d = new Date(date)
    const year = d.getFullYear()
    const month = String(d.getMonth() + 1).padStart(2, '0')
    return `${year}-${month}`
}

const loadData = async () => {
    const month = formatMonth(selectedMonth.value)
    const userId = store.user.id
    
    try {
        const budget = await budgetApi.getByMonth(userId, month)
        const statistics = await accountApi.categoryStatistics({ userId, month })
        
        const spent = statistics
            .filter(item => item.type === 'expense')
            .reduce((sum, item) => sum + Number(item.totalAmount), 0)
        
        if (budget) {
            const budgetAmount = Number(budget.amount)
            budgetData.value = {
                budget: budgetAmount,
                spent: spent,
                remaining: budgetAmount - spent,
                usageRate: budgetAmount > 0 ? Math.round((spent / budgetAmount) * 100) : 0
            }
        } else {
            budgetData.value = null
        }
    } catch (error) {
        console.error('加载数据失败', error)
    }
}

const openDialog = () => {
    form.value = {
        userId: store.user.id,
        month: selectedMonth.value,
        amount: budgetData.value?.budget || 0
    }
    dialogVisible.value = true
}

const handleSave = async () => {
    try {
        const data = {
            ...form.value,
            month: formatMonth(form.value.month)
        }
        await budgetApi.save(data)
        ElMessage.success('预算设置成功')
        dialogVisible.value = false
        loadData()
    } catch (error) {
        console.error('保存失败', error)
    }
}

const getProgressColor = (percentage) => {
    if (percentage >= 100) return '#f56c6c'
    if (percentage >= 80) return '#e6a23c'
    return '#67c23a'
}
</script>

<style scoped>
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.month-selector {
    margin-bottom: 30px;
}
.budget-overview {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 30px;
}
.progress-section {
    margin-top: 20px;
}
.progress-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
    font-weight: bold;
}
.warning-text {
    margin-top: 15px;
    padding: 12px;
    background-color: #fdf6ec;
    border-radius: 4px;
    color: #e6a23c;
    display: flex;
    align-items: center;
    gap: 8px;
}
.no-budget {
    margin-top: 40px;
}
</style>
