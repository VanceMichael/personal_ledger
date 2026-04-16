<template>
    <div class="statistics-container">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>统计报表</span>
                    <el-date-picker
                        v-model="selectedMonth"
                        type="month"
                        placeholder="选择月份"
                        @change="loadData"
                    />
                </div>
            </template>

            <el-row :gutter="20" class="summary-row">
                <el-col :span="6">
                    <div class="summary-card income">
                        <div class="summary-label">本月收入</div>
                        <div class="summary-value">+{{ summaryData.totalIncome }}</div>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="summary-card expense">
                        <div class="summary-label">本月支出</div>
                        <div class="summary-value">-{{ summaryData.totalExpense }}</div>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="summary-card balance">
                        <div class="summary-label">本月结余</div>
                        <div class="summary-value">{{ summaryData.balance }}</div>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="summary-card count">
                        <div class="summary-label">记账笔数</div>
                        <div class="summary-value">{{ summaryData.count }}</div>
                    </div>
                </el-col>
            </el-row>

            <el-tabs v-model="activeTab" class="chart-tabs">
                <el-tab-pane label="分类占比" name="category" />
                <el-tab-pane label="收支趋势" name="trend" />
                <el-tab-pane label="年度账单" name="yearly" />
            </el-tabs>

            <div class="chart-container" v-if="activeTab === 'category'">
                <el-row :gutter="20">
                    <el-col :span="12">
                        <div class="chart-box">
                            <h3>支出分类</h3>
                            <div ref="expensePieChart" class="chart"></div>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-box">
                            <h3>收入分类</h3>
                            <div ref="incomePieChart" class="chart"></div>
                        </div>
                    </el-col>
                </el-row>
            </div>

            <div class="chart-container" v-if="activeTab === 'trend'">
                <div class="chart-box">
                    <h3>月度收支趋势</h3>
                    <div ref="trendChart" class="chart trend-chart"></div>
                </div>
            </div>

            <div class="chart-container" v-if="activeTab === 'yearly'">
                <div class="chart-box">
                    <h3>年度收支统计</h3>
                    <div ref="yearlyChart" class="chart yearly-chart"></div>
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { accountApi } from '@/api'
import { store } from '@/store'
import * as echarts from 'echarts'

const selectedMonth = ref(new Date())
const activeTab = ref('category')
const summaryData = ref({
    totalIncome: 0,
    totalExpense: 0,
    balance: 0,
    count: 0
})

const expensePieChart = ref(null)
const incomePieChart = ref(null)
const trendChart = ref(null)
const yearlyChart = ref(null)

let expensePieChartInstance = null
let incomePieChartInstance = null
let trendChartInstance = null
let yearlyChartInstance = null

const formatMonth = (date) => {
    const d = new Date(date)
    const year = d.getFullYear()
    const month = String(d.getMonth() + 1).padStart(2, '0')
    return `${year}-${month}`
}

const formatYear = (date) => {
    return new Date(date).getFullYear()
}

onMounted(() => {
    loadData()
    window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
    disposeCharts()
})

const disposeCharts = () => {
    expensePieChartInstance?.dispose()
    incomePieChartInstance?.dispose()
    trendChartInstance?.dispose()
    yearlyChartInstance?.dispose()
}

const handleResize = () => {
    expensePieChartInstance?.resize()
    incomePieChartInstance?.resize()
    trendChartInstance?.resize()
    yearlyChartInstance?.resize()
}

const loadData = async () => {
    const month = formatMonth(selectedMonth.value)
    const year = formatYear(selectedMonth.value)
    const userId = store.user.id

    try {
        const categoryData = await accountApi.categoryStatistics({ userId, month })
        const trendData = await accountApi.trendStatistics({ userId, year })
        const yearlyData = await accountApi.yearlyStatistics({ userId, year })

        updateSummary(categoryData)
        
        await nextTick()
        
        renderPieCharts(categoryData)
        renderTrendChart(trendData)
        renderYearlyChart(yearlyData)
    } catch (error) {
        console.error('加载统计数据失败', error)
    }
}

const updateSummary = (categoryData) => {
    const expenseData = categoryData.filter(item => item.type === 'expense')
    const incomeData = categoryData.filter(item => item.type === 'income')
    
    const totalExpense = expenseData.reduce((sum, item) => sum + Number(item.totalAmount), 0)
    const totalIncome = incomeData.reduce((sum, item) => sum + Number(item.totalAmount), 0)
    const count = categoryData.reduce((sum, item) => sum + item.count, 0)
    
    summaryData.value = {
        totalIncome: totalIncome.toFixed(2),
        totalExpense: totalExpense.toFixed(2),
        balance: (totalIncome - totalExpense).toFixed(2),
        count
    }
}

const renderPieCharts = (data) => {
    const expenseData = data.filter(item => item.type === 'expense')
    const incomeData = data.filter(item => item.type === 'income')

    const expenseOption = {
        tooltip: {
            trigger: 'item',
            formatter: '{b}: ¥{c} ({d}%)'
        },
        series: [{
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
                borderRadius: 10,
                borderColor: '#fff',
                borderWidth: 2
            },
            label: {
                show: true,
                formatter: '{b}\n¥{c}'
            },
            data: expenseData.map(item => ({
                value: item.totalAmount,
                name: item.categoryName
            }))
        }]
    }

    const incomeOption = {
        tooltip: {
            trigger: 'item',
            formatter: '{b}: ¥{c} ({d}%)'
        },
        series: [{
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
                borderRadius: 10,
                borderColor: '#fff',
                borderWidth: 2
            },
            label: {
                show: true,
                formatter: '{b}\n¥{c}'
            },
            data: incomeData.map(item => ({
                value: item.totalAmount,
                name: item.categoryName
            }))
        }]
    }

    if (expensePieChart.value) {
        expensePieChartInstance = echarts.init(expensePieChart.value)
        expensePieChartInstance.setOption(expenseOption)
    }

    if (incomePieChart.value) {
        incomePieChartInstance = echarts.init(incomePieChart.value)
        incomePieChartInstance.setOption(incomeOption)
    }
}

const renderTrendChart = (data) => {
    const months = [...new Set(data.map(item => item.month))].sort()
    const incomeData = months.map(month => {
        const item = data.find(d => d.month === month && d.type === 'income')
        return item ? item.totalAmount : 0
    })
    const expenseData = months.map(month => {
        const item = data.find(d => d.month === month && d.type === 'expense')
        return item ? item.totalAmount : 0
    })

    const option = {
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data: ['收入', '支出']
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
            data: months
        },
        yAxis: {
            type: 'value'
        },
        series: [
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
            },
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
            }
        ]
    }

    if (trendChart.value) {
        trendChartInstance = echarts.init(trendChart.value)
        trendChartInstance.setOption(option)
    }
}

const renderYearlyChart = (data) => {
    const months = ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12']
    const incomeData = months.map(month => {
        const item = data.find(d => d.month === month && d.type === 'income')
        return item ? item.totalAmount : 0
    })
    const expenseData = months.map(month => {
        const item = data.find(d => d.month === month && d.type === 'expense')
        return item ? item.totalAmount : 0
    })

    const option = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: ['收入', '支出']
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            data: months.map(m => m + '月')
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                name: '收入',
                type: 'bar',
                data: incomeData,
                itemStyle: {
                    color: '#67c23a'
                }
            },
            {
                name: '支出',
                type: 'bar',
                data: expenseData,
                itemStyle: {
                    color: '#f56c6c'
                }
            }
        ]
    }

    if (yearlyChart.value) {
        yearlyChartInstance = echarts.init(yearlyChart.value)
        yearlyChartInstance.setOption(option)
    }
}
</script>

<style scoped>
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.summary-row {
    margin-bottom: 30px;
}
.summary-card {
    padding: 20px;
    border-radius: 8px;
    text-align: center;
}
.summary-card.income {
    background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}
.summary-card.expense {
    background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
}
.summary-card.balance {
    background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}
.summary-card.count {
    background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
}
.summary-label {
    color: rgba(255, 255, 255, 0.8);
    font-size: 14px;
    margin-bottom: 8px;
}
.summary-value {
    color: white;
    font-size: 28px;
    font-weight: bold;
}
.chart-tabs {
    margin-top: 20px;
}
.chart-container {
    margin-top: 20px;
}
.chart-box {
    padding: 20px;
}
.chart-box h3 {
    margin-bottom: 20px;
    text-align: center;
    color: #303133;
}
.chart {
    height: 400px;
}
.trend-chart,
.yearly-chart {
    height: 450px;
}
</style>
