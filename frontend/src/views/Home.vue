<template>
  <div class="home-container">
    <el-row :gutter="20" class="overview-row">
      <el-col :span="6">
        <el-card class="overview-card income">
          <div class="card-content">
            <div class="icon-box income-icon">
              <el-icon><ArrowUp /></el-icon>
            </div>
            <div class="info">
              <p class="label">本月收入</p>
              <p class="amount">¥{{ statistic.totalIncome || '0.00' }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card expense">
          <div class="card-content">
            <div class="icon-box expense-icon">
              <el-icon><ArrowDown /></el-icon>
            </div>
            <div class="info">
              <p class="label">本月支出</p>
              <p class="amount">¥{{ statistic.totalExpense || '0.00' }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card balance">
          <div class="card-content">
            <div class="icon-box balance-icon">
              <el-icon><Wallet /></el-icon>
            </div>
            <div class="info">
              <p class="label">本月结余</p>
              <p class="amount">¥{{ statistic.balance || '0.00' }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card budget">
          <div class="card-content">
            <div class="icon-box budget-icon">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="info">
              <p class="label">预算剩余</p>
              <p class="amount">¥{{ statistic.budgetRemaining || '0.00' }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-row">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近记录</span>
              <el-button type="primary" size="small" @click="goToRecord">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentRecords" style="width: 100%">
            <el-table-column prop="recordTime" label="日期" width="120">
              <template #default="scope">
                {{ formatDate(scope.row.recordTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="categoryName" label="分类" width="100">
              <template #default="scope">
                <el-tag size="small" :type="scope.row.type === 1 ? 'danger' : 'success'">
                  <el-icon class="mr-1">
                    <component :is="scope.row.categoryIcon" />
                  </el-icon>
                  {{ scope.row.categoryName }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" />
            <el-table-column prop="amount" label="金额" width="120" align="right">
              <template #default="scope">
                <span :class="scope.row.type === 1 ? 'expense-text' : 'income-text'">
                  {{ scope.row.type === 1 ? '-' : '+' }}¥{{ scope.row.amount }}
                </span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>支出分类占比</span>
          </template>
          <div id="pieChart" style="width: 100%; height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-button class="add-btn" type="primary" circle @click="showAddDialog = true">
      <el-icon><Plus /></el-icon>
    </el-button>

    <AddRecordDialog v-model="showAddDialog" @success="loadData" />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import dayjs from 'dayjs'
import { ArrowUp, ArrowDown, Wallet, Calendar, Plus } from '@element-plus/icons-vue'
import { getMonthStatistic } from '@/api/statistic'
import { getRecordList } from '@/api/record'
import AddRecordDialog from '@/components/AddRecordDialog.vue'

const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')
const statistic = ref({})
const recentRecords = ref([])
const showAddDialog = ref(false)
let pieChart = null

const formatDate = (date) => {
  return dayjs(date).format('MM-DD')
}

const goToRecord = () => {
  router.push('/record')
}

const loadStatistic = async () => {
  const now = dayjs()
  const data = await getMonthStatistic({
    userId: user.id,
    year: now.year(),
    month: now.month() + 1
  })
  statistic.value = data
  initPieChart(data.categoryStats || [])
}

const loadRecentRecords = async () => {
  const data = await getRecordList({
    userId: user.id,
    pageNum: 1,
    pageSize: 10
  })
  recentRecords.value = data.slice(0, 10)
}

const initPieChart = (data) => {
  const chartDom = document.getElementById('pieChart')
  if (!chartDom) return
  if (!pieChart) {
    pieChart = echarts.init(chartDom)
  }
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    series: [
      {
        name: '支出分类',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: data.map(item => ({
          value: item.amount,
          name: item.categoryName
        }))
      }
    ]
  }
  pieChart.setOption(option)
}

const loadData = () => {
  loadStatistic()
  loadRecentRecords()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', () => {
    pieChart?.resize()
  })
})

onUnmounted(() => {
  pieChart?.dispose()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
  position: relative;
}

.overview-row {
  margin-bottom: 20px;
}

.overview-card {
  height: 120px;
  border: none;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.icon-box {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
  margin-right: 20px;
}

.income-icon {
  background: linear-gradient(135deg, #67c23a, #85ce61);
}

.expense-icon {
  background: linear-gradient(135deg, #f56c6c, #f78989);
}

.balance-icon {
  background: linear-gradient(135deg, #409eff, #66b1ff);
}

.budget-icon {
  background: linear-gradient(135deg, #e6a23c, #ebb563);
}

.info .label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.info .amount {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.content-row {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.income-text {
  color: #67c23a;
  font-weight: 600;
}

.expense-text {
  color: #f56c6c;
  font-weight: 600;
}

.add-btn {
  position: fixed;
  right: 40px;
  bottom: 40px;
  width: 60px;
  height: 60px;
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}
</style>
