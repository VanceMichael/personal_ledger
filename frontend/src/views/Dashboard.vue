<template>
    <div class="dashboard">
        <el-row :gutter="20" class="stats-cards">
            <el-col :span="6">
                <el-card class="stat-card income">
                    <div class="stat-icon">
                        <el-icon><ArrowUp /></el-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">本月收入</div>
                        <div class="stat-value">¥{{ monthlyStats.income || 0 }}</div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card class="stat-card expense">
                    <div class="stat-icon">
                        <el-icon><ArrowDown /></el-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">本月支出</div>
                        <div class="stat-value">¥{{ monthlyStats.expense || 0 }}</div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card class="stat-card balance">
                    <div class="stat-icon">
                        <el-icon><Money /></el-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">本月结余</div>
                        <div class="stat-value">¥{{ monthlyStats.balance || 0 }}</div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card class="stat-card budget">
                    <div class="stat-icon">
                        <el-icon><Wallet /></el-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">预算剩余</div>
                        <div class="stat-value">¥{{ budgetRemaining }}</div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <el-row :gutter="20" class="content-row">
            <el-col :span="16">
                <el-card class="recent-transactions">
                    <template #header>
                        <div class="card-header">
                            <span>最近账目</span>
                            <el-button type="primary" link @click="$router.push('/transaction')">查看全部</el-button>
                        </div>
                    </template>
                    <el-table :data="recentTransactions" stripe>
                        <el-table-column prop="categoryName" label="分类" width="120">
                            <template #default="{ row }">
                                <span class="category-tag" :class="row.type === 1 ? 'income' : 'expense'">
                                    {{ row.categoryName }}
                                </span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="type" label="类型" width="80">
                            <template #default="{ row }">
                                <el-tag :type="row.type === 1 ? 'success' : 'danger'">
                                    {{ row.type === 1 ? '收入' : '支出' }}
                                </el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="amount" label="金额" width="120">
                            <template #default="{ row }">
                                <span :class="row.type === 1 ? 'text-success' : 'text-danger'">
                                    {{ row.type === 1 ? '+' : '-' }}¥{{ row.amount }}
                                </span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
                        <el-table-column prop="transactionDate" label="日期" width="120" />
                    </el-table>
                    <el-empty v-if="recentTransactions.length === 0" description="暂无账目记录" />
                </el-card>
            </el-col>
            <el-col :span="8">
                <el-card class="category-stats">
                    <template #header>
                        <span>支出分类占比</span>
                    </template>
                    <div ref="pieChartRef" class="pie-chart"></div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import * as echarts from 'echarts'

const router = useRouter()
const monthlyStats = ref({})
const recentTransactions = ref([])
const categoryStats = ref([])
const budgetInfo = ref(null)
const pieChartRef = ref(null)
let pieChart = null

const budgetRemaining = computed(() => {
    if (!budgetInfo.value) return 0
    const used = monthlyStats.value.expense || 0
    return Math.max(0, budgetInfo.value.amount - used)
})

const loadMonthlyStats = async () => {
    try {
        const now = new Date()
        const res = await request.get('/statistics/monthly', {
            params: {
                year: now.getFullYear(),
                month: now.getMonth() + 1
            }
        })
        monthlyStats.value = res.data
    } catch (e) {
        console.error('加载月度统计失败', e)
    }
}

const loadRecentTransactions = async () => {
    try {
        const res = await request.get('/transaction', {
            params: { page: 1, size: 5 }
        })
        recentTransactions.value = res.data.records || []
    } catch (e) {
        console.error('加载最近账目失败', e)
    }
}

const loadCategoryStats = async () => {
    try {
        const now = new Date()
        const res = await request.get('/statistics/category', {
            params: {
                year: now.getFullYear(),
                month: now.getMonth() + 1,
                type: 2
            }
        })
        categoryStats.value = res.data || []
        renderPieChart()
    } catch (e) {
        console.error('加载分类统计失败', e)
    }
}

const loadBudgetInfo = async () => {
    try {
        const now = new Date()
        const res = await request.get('/budget', {
            params: {
                year: now.getFullYear(),
                month: now.getMonth() + 1
            }
        })
        budgetInfo.value = res.data
    } catch (e) {
        console.error('加载预算信息失败', e)
    }
}

const renderPieChart = () => {
    if (!pieChartRef.value || categoryStats.value.length === 0) return
    
    if (!pieChart) {
        pieChart = echarts.init(pieChartRef.value)
    }
    
    const data = categoryStats.value.map(item => ({
        name: item.categoryName,
        value: item.totalAmount
    }))
    
    const option = {
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
                show: false
            },
            emphasis: {
                label: {
                    show: true,
                    fontSize: 14,
                    fontWeight: 'bold'
                }
            },
            labelLine: {
                show: false
            },
            data: data
        }]
    }
    
    pieChart.setOption(option)
}

onMounted(() => {
    loadMonthlyStats()
    loadRecentTransactions()
    loadCategoryStats()
    loadBudgetInfo()
    
    window.addEventListener('resize', () => {
        pieChart?.resize()
    })
})
</script>

<style scoped>
.dashboard {
    padding: 0;
}
.stats-cards {
    margin-bottom: 20px;
}
.stat-card {
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}
.stat-card :deep(.el-card__body) {
    display: flex;
    align-items: center;
    padding: 20px;
}
.stat-icon {
    width: 50px;
    height: 50px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    margin-right: 15px;
}
.income .stat-icon {
    background: #e6f7ff;
    color: #1890ff;
}
.expense .stat-icon {
    background: #fff1f0;
    color: #f5222d;
}
.balance .stat-icon {
    background: #f6ffed;
    color: #52c41a;
}
.budget .stat-icon {
    background: #fff7e6;
    color: #fa8c16;
}
.stat-content {
    flex: 1;
}
.stat-label {
    font-size: 14px;
    color: #8c8c8c;
    margin-bottom: 5px;
}
.stat-value {
    font-size: 24px;
    font-weight: bold;
    color: #262626;
}
.content-row {
    margin-bottom: 20px;
}
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.category-tag {
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 12px;
}
.category-tag.income {
    background: #f6ffed;
    color: #52c41a;
}
.category-tag.expense {
    background: #fff1f0;
    color: #f5222d;
}
.text-success {
    color: #52c41a;
    font-weight: bold;
}
.text-danger {
    color: #f5222d;
    font-weight: bold;
}
.pie-chart {
    height: 300px;
}
</style>