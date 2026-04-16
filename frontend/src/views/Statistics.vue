<template>
    <div class="statistics">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>统计报表</span>
                    <div class="header-actions">
                        <el-date-picker
                            v-model="selectedDate"
                            type="month"
                            placeholder="选择月份"
                            format="YYYY-MM"
                            value-format="YYYY-MM"
                            @change="loadStatistics"
                        />
                    </div>
                </div>
            </template>

            <el-row :gutter="20" class="summary-row">
                <el-col :span="6">
                    <div class="summary-card income">
                        <div class="summary-label">总收入</div>
                        <div class="summary-value">¥{{ monthlyStats.income || 0 }}</div>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="summary-card expense">
                        <div class="summary-label">总支出</div>
                        <div class="summary-value">¥{{ monthlyStats.expense || 0 }}</div>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="summary-card balance">
                        <div class="summary-label">结余</div>
                        <div class="summary-value">¥{{ monthlyStats.balance || 0 }}</div>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="summary-card count">
                        <div class="summary-label">记账笔数</div>
                        <div class="summary-value">{{ monthlyStats.count || 0 }} 笔</div>
                    </div>
                </el-col>
            </el-row>

            <el-row :gutter="20" class="charts-row">
                <el-col :span="12">
                    <el-card class="chart-card">
                        <template #header>
                            <span>支出分类占比</span>
                        </template>
                        <div ref="pieChartRef" class="chart-container"></div>
                    </el-card>
                </el-col>
                <el-col :span="12">
                    <el-card class="chart-card">
                        <template #header>
                            <span>收入分类占比</span>
                        </template>
                        <div ref="incomePieChartRef" class="chart-container"></div>
                    </el-card>
                </el-col>
            </el-row>

            <el-row :gutter="20" class="charts-row">
                <el-col :span="24">
                    <el-card class="chart-card">
                        <template #header>
                            <span>收支趋势（近6个月）</span>
                        </template>
                        <div ref="trendChartRef" class="trend-chart"></div>
                    </el-card>
                </el-col>
            </el-row>

            <el-row :gutter="20" class="charts-row">
                <el-col :span="24">
                    <el-card class="chart-card">
                        <template #header>
                            <span>年度账单（{{ currentYear }}年）</span>
                        </template>
                        <div ref="yearChartRef" class="year-chart"></div>
                    </el-card>
                </el-col>
            </el-row>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import * as echarts from 'echarts'
import dayjs from 'dayjs'

const selectedDate = ref(dayjs().format('YYYY-MM'))
const monthlyStats = ref({})
const expenseCategoryStats = ref([])
const incomeCategoryStats = ref([])
const trendStats = ref([])
const yearStats = ref([])
const currentYear = ref(new Date().getFullYear())

const pieChartRef = ref(null)
const incomePieChartRef = ref(null)
const trendChartRef = ref(null)
const yearChartRef = ref(null)

let pieChart = null
let incomePieChart = null
let trendChart = null
let yearChart = null

const loadStatistics = async () => {
    if (!selectedDate.value) return
    
    const [year, month] = selectedDate.value.split('-').map(Number)
    
    try {
        const [monthlyRes, expenseRes, incomeRes, trendRes, yearRes] = await Promise.all([
            request.get('/statistics/monthly', { params: { year, month } }),
            request.get('/statistics/category', { params: { year, month, type: 2 } }),
            request.get('/statistics/category', { params: { year, month, type: 1 } }),
            request.get('/statistics/trend', { params: { months: 6 } }),
            request.get('/statistics/yearly', { params: { year: currentYear.value } })
        ])
        
        monthlyStats.value = monthlyRes.data || {}
        expenseCategoryStats.value = expenseRes.data || []
        incomeCategoryStats.value = incomeRes.data || []
        trendStats.value = trendRes.data || []
        yearStats.value = yearRes.data || []
        
        renderPieChart()
        renderIncomePieChart()
        renderTrendChart()
        renderYearChart()
    } catch (e) {
        ElMessage.error('加载统计数据失败')
    }
}

const renderPieChart = () => {
    if (!pieChartRef.value) return
    
    if (!pieChart) {
        pieChart = echarts.init(pieChartRef.value)
    }
    
    const data = expenseCategoryStats.value.map(item => ({
        name: item.categoryName,
        value: item.totalAmount
    }))
    
    const option = {
        tooltip: {
            trigger: 'item',
            formatter: '{b}: ¥{c} ({d}%)'
        },
        legend: {
            orient: 'vertical',
            right: 10,
            top: 'center'
        },
        series: [{
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['40%', '50%'],
            data: data,
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }]
    }
    
    pieChart.setOption(option)
}

const renderIncomePieChart = () => {
    if (!incomePieChartRef.value) return
    
    if (!incomePieChart) {
        incomePieChart = echarts.init(incomePieChartRef.value)
    }
    
    const data = incomeCategoryStats.value.map(item => ({
        name: item.categoryName,
        value: item.totalAmount
    }))
    
    const option = {
        tooltip: {
            trigger: 'item',
            formatter: '{b}: ¥{c} ({d}%)'
        },
        legend: {
            orient: 'vertical',
            right: 10,
            top: 'center'
        },
        series: [{
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['40%', '50%'],
            data: data,
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }]
    }
    
    incomePieChart.setOption(option)
}

const renderTrendChart = () => {
    if (!trendChartRef.value) return
    
    if (!trendChart) {
        trendChart = echarts.init(trendChartRef.value)
    }
    
    const months = trendStats.value.map(item => item.month)
    const incomeData = trendStats.value.map(item => item.income)
    const expenseData = trendStats.value.map(item => item.expense)
    
    const option = {
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data: ['收入', '支出'],
            top: 10
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
            type: 'value',
            axisLabel: {
                formatter: '¥{value}'
            }
        },
        series: [
            {
                name: '收入',
                type: 'line',
                smooth: true,
                data: incomeData,
                itemStyle: { color: '#52c41a' },
                areaStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: 'rgba(82, 196, 26, 0.3)' },
                        { offset: 1, color: 'rgba(82, 196, 26, 0.05)' }
                    ])
                }
            },
            {
                name: '支出',
                type: 'line',
                smooth: true,
                data: expenseData,
                itemStyle: { color: '#f5222d' },
                areaStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: 'rgba(245, 34, 45, 0.3)' },
                        { offset: 1, color: 'rgba(245, 34, 45, 0.05)' }
                    ])
                }
            }
        ]
    }
    
    trendChart.setOption(option)
}

const renderYearChart = () => {
    if (!yearChartRef.value) return
    
    if (!yearChart) {
        yearChart = echarts.init(yearChartRef.value)
    }
    
    const months = yearStats.value.map(item => `${item.month}月`)
    const incomeData = yearStats.value.map(item => item.income)
    const expenseData = yearStats.value.map(item => item.expense)
    
    const option = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: ['收入', '支出'],
            top: 10
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            data: months
        },
        yAxis: {
            type: 'value',
            axisLabel: {
                formatter: '¥{value}'
            }
        },
        series: [
            {
                name: '收入',
                type: 'bar',
                data: incomeData,
                itemStyle: { color: '#52c41a' }
            },
            {
                name: '支出',
                type: 'bar',
                data: expenseData,
                itemStyle: { color: '#f5222d' }
            }
        ]
    }
    
    yearChart.setOption(option)
}

const handleResize = () => {
    pieChart?.resize()
    incomePieChart?.resize()
    trendChart?.resize()
    yearChart?.resize()
}

onMounted(() => {
    loadStatistics()
    window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
    pieChart?.dispose()
    incomePieChart?.dispose()
    trendChart?.dispose()
    yearChart?.dispose()
})
</script>

<style scoped>
.statistics {
    padding: 0;
}
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.summary-row {
    margin-bottom: 20px;
}
.summary-card {
    padding: 20px;
    border-radius: 8px;
    text-align: center;
}
.summary-card.income {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.summary-card.expense {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}
.summary-card.balance {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}
.summary-card.count {
    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}
.summary-label {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.8);
    margin-bottom: 10px;
}
.summary-value {
    font-size: 28px;
    font-weight: bold;
    color: white;
}
.charts-row {
    margin-bottom: 20px;
}
.chart-card {
    height: 100%;
}
.chart-container {
    height: 350px;
}
.trend-chart {
    height: 350px;
}
.year-chart {
    height: 400px;
}
</style>