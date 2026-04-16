<template>
  <div class="statistics-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>统计报表</span>
          <div class="header-right">
            <el-radio-group v-model="statType" @change="loadData">
              <el-radio :label="1">月度统计</el-radio>
              <el-radio :label="2">年度统计</el-radio>
            </el-radio-group>
            <el-date-picker
              v-model="currentDate"
              :type="statType === 1 ? 'month' : 'year'"
              format="YYYY年MM月"
              value-format="YYYY-MM"
              @change="loadData"
              class="ml-20"
            />
          </div>
        </div>
      </template>

      <el-row :gutter="20" class="overview-row">
        <el-col :span="6">
          <el-card class="overview-card income">
            <div class="card-content">
              <div class="icon-box income-icon">
                <el-icon><ArrowUp /></el-icon>
              </div>
              <div class="info">
                <p class="label">{{ statType === 1 ? '本月收入' : '本年收入' }}</p>
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
                <p class="label">{{ statType === 1 ? '本月支出' : '本年支出' }}</p>
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
                <p class="label">{{ statType === 1 ? '本月结余' : '本年结余' }}</p>
                <p class="amount">¥{{ statistic.balance || '0.00' }}</p>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6" v-if="statType === 1">
          <el-card class="overview-card budget">
            <div class="card-content">
              <div class="icon-box budget-icon">
                <el-icon><Calendar /></el-icon>
              </div>
              <div class="info">
                <p class="label">预算进度</p>
                <p class="amount">{{ statistic.budgetUsagePercent || 0 }}%</p>
              </div>
            </div>
            <el-progress
              :percentage="statistic.budgetUsagePercent || 0"
              :color="getProgressColor(statistic.budgetUsagePercent || 0)"
              :show-text="false"
              class="progress-bar"
            />
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="chart-row">
        <el-col :span="8">
          <el-card>
            <template #header>
              <span>支出分类占比</span>
            </template>
            <div id="pieChart" style="width: 100%; height: 400px"></div>
          </el-card>
        </el-col>
        <el-col :span="16">
          <el-card>
            <template #header>
              <span>{{ statType === 1 ? '每日收支趋势' : '每月收支趋势' }}</span>
            </template>
            <div id="lineChart" style="width: 100%; height: 400px"></div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import dayjs from 'dayjs'
import { ArrowUp, ArrowDown, Wallet, Calendar } from '@element-plus/icons-vue'
import { getMonthStatistic, getYearStatistic } from '@/api/statistic'

const user = JSON.parse(localStorage.getItem('user') || '{}')
const statType = ref(1)
const currentDate = ref(dayjs().format('YYYY-MM'))
const statistic = ref({})
let pieChart = null
let lineChart = null

const getProgressColor = (percent) => {
  if (percent < 70) return '#67c23a'
  if (percent < 90) return '#e6a23c'
  return '#f56c6c'
}

const loadData = async () => {
  const [year, month] = currentDate.value.split('-').map(Number)
  let data
  if (statType.value === 1) {
    data = await getMonthStatistic({
      userId: user.id,
      year,
      month
    })
  } else {
    data = await getYearStatistic({
      userId: user.id,
      year
    })
  }
  statistic.value = data
  initCharts(data)
}

const initCharts = (data) => {
  initPieChart(data.categoryStats || [])
  initLineChart(data.dailyStats || [], statType.value)
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
    legend: {
      orient: 'vertical',
      left: 'left'
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
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
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

const initLineChart = (data, type) => {
  const chartDom = document.getElementById('lineChart')
  if (!chartDom) return
  if (!lineChart) {
    lineChart = echarts.init(chartDom)
  }
  const xAxisData = data.map(item => item.date)
  const expenseData = data.map(item => item.expense)
  const incomeData = data.map(item => item.income)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['支出', '收入']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: xAxisData
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [
      {
        name: '支出',
        type: 'line',
        data: expenseData,
        smooth: true,
        itemStyle: {
          color: '#f56c6c'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(245, 108, 108, 0.3)' },
            { offset: 1, color: 'rgba(245, 108, 108, 0.05)' }
          ])
        }
      },
      {
        name: '收入',
        type: 'line',
        data: incomeData,
        smooth: true,
        itemStyle: {
          color: '#67c23a'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
          ])
        }
      }
    ]
  }
  lineChart.setOption(option)
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', () => {
    pieChart?.resize()
    lineChart?.resize()
  })
})

onUnmounted(() => {
  pieChart?.dispose()
  lineChart?.dispose()
})
</script>

<style scoped>
.statistics-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.overview-row {
  margin-bottom: 20px;
}

.overview-card {
  height: 120px;
  border: none;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  position: relative;
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

.progress-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  width: 100%;
  --el-progress-height: 6px;
}

.chart-row {
  margin-bottom: 20px;
}
</style>
