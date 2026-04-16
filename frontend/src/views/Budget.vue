<template>
  <div class="budget-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>预算管理</span>
          <el-button type="primary" @click="showSetDialog = true">设置预算</el-button>
        </div>
      </template>

      <div class="date-selector">
        <el-date-picker
          v-model="currentMonth"
          type="month"
          format="YYYY年MM月"
          value-format="YYYY-MM"
          @change="loadData"
        />
      </div>

      <div class="budget-overview" v-if="statistic.budgetAmount">
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="info-item">
              <p class="label">预算金额</p>
              <p class="amount">¥{{ statistic.budgetAmount }}</p>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <p class="label">已使用</p>
              <p class="amount used">¥{{ statistic.budgetUsed || '0.00' }}</p>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <p class="label">剩余</p>
              <p class="amount remaining" :class="{ danger: statistic.budgetRemaining <= 0 }">
                ¥{{ statistic.budgetRemaining || '0.00' }}
              </p>
            </div>
          </el-col>
        </el-row>

        <div class="progress-section">
          <div class="progress-header">
            <span>使用进度</span>
            <span class="percentage">{{ statistic.budgetUsagePercent || 0 }}%</span>
          </div>
          <el-progress
            :percentage="statistic.budgetUsagePercent || 0"
            :color="getProgressColor(statistic.budgetUsagePercent || 0)"
            :stroke-width="20"
          />
          <div class="warning" v-if="statistic.budgetUsagePercent >= 90 && statistic.budgetUsagePercent < 100">
            <el-icon><Warning /></el-icon>
            <span>您本月预算已使用90%，请注意控制消费</span>
          </div>
          <div class="danger warning" v-if="statistic.budgetUsagePercent >= 100">
            <el-icon><CircleCloseFilled /></el-icon>
            <span>您本月预算已超支，请合理安排消费</span>
          </div>
        </div>
      </div>

      <div class="empty-state" v-else>
        <el-empty description="暂无预算设置，点击右上角按钮设置本月预算">
          <el-button type="primary" @click="showSetDialog = true">立即设置</el-button>
        </el-empty>
      </div>
    </el-card>

    <el-dialog v-model="showSetDialog" title="设置月度预算" width="400px">
      <el-form ref="formRef" :model="form" label-width="80px">
        <el-form-item label="预算金额" prop="amount" :rules="[{ required: true, message: '请输入预算金额', trigger: 'blur' }]">
          <el-input v-model.number="form.amount" type="number" placeholder="请输入预算金额" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showSetDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSetBudget" :loading="loading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { Warning, CircleCloseFilled } from '@element-plus/icons-vue'
import { setBudget, getMonthStatistic } from '@/api/statistic'

const user = JSON.parse(localStorage.getItem('user') || '{}')
const loading = ref(false)
const showSetDialog = ref(false)
const currentMonth = ref(dayjs().format('YYYY-MM'))
const statistic = ref({})
const formRef = ref()

const form = reactive({
  userId: user.id,
  amount: '',
  year: 0,
  month: 0
})

const getProgressColor = (percent) => {
  if (percent < 70) return '#67c23a'
  if (percent < 90) return '#e6a23c'
  return '#f56c6c'
}

const loadData = async () => {
  const [year, month] = currentMonth.value.split('-').map(Number)
  const data = await getMonthStatistic({
    userId: user.id,
    year,
    month
  })
  statistic.value = data
}

const handleSetBudget = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  const [year, month] = currentMonth.value.split('-').map(Number)
  form.year = year
  form.month = month
  try {
    loading.value = true
    await setBudget(form)
    ElMessage.success('预算设置成功')
    showSetDialog.value = false
    loadData()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.budget-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.date-selector {
  margin-bottom: 30px;
  text-align: center;
}

.budget-overview {
  padding: 20px 0;
}

.info-item {
  text-align: center;
  padding: 30px 0;
  background: #f8f9fa;
  border-radius: 12px;
}

.info-item .label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.info-item .amount {
  font-size: 32px;
  font-weight: 600;
  color: #303133;
}

.amount.used {
  color: #f56c6c;
}

.amount.remaining {
  color: #67c23a;
}

.amount.remaining.danger {
  color: #f56c6c;
}

.progress-section {
  margin-top: 40px;
  padding: 0 20px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  font-size: 16px;
}

.percentage {
  font-weight: 600;
  color: #409eff;
}

.warning {
  margin-top: 20px;
  padding: 12px 16px;
  background: #fdf6ec;
  border: 1px solid #fae4c5;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #e6a23c;
}

.warning.danger {
  background: #fef0f0;
  border-color: #fbc4c4;
  color: #f56c6c;
}

.empty-state {
  padding: 60px 0;
}
</style>
