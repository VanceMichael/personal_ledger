<template>
    <div class="account-container">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>账目管理</span>
                    <el-button type="primary" @click="openDialog">
                        <el-icon><Plus /></el-icon>
                        记一笔
                    </el-button>
                </div>
            </template>

            <el-form :inline="true" :model="filterForm" class="filter-form">
                <el-form-item label="日期范围">
                    <el-date-picker
                        v-model="dateRange"
                        type="daterange"
                        range-separator="至"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                    />
                </el-form-item>
                <el-form-item label="类型">
                    <el-select v-model="filterForm.type" placeholder="请选择" clearable>
                        <el-option label="支出" value="expense" />
                        <el-option label="收入" value="income" />
                    </el-select>
                </el-form-item>
                <el-form-item label="分类">
                    <el-select v-model="filterForm.categoryId" placeholder="请选择" clearable>
                        <el-option
                            v-for="item in categories"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadData">查询</el-button>
                    <el-button @click="resetFilter">重置</el-button>
                </el-form-item>
            </el-form>

            <el-table :data="accountList" border stripe>
                <el-table-column prop="date" label="日期" width="120" />
                <el-table-column prop="categoryIcon" label="分类" width="80">
                    <template #default="{ row }">
                        <span style="font-size: 20px">{{ row.categoryIcon }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="categoryName" label="分类名称" width="120" />
                <el-table-column prop="type" label="类型" width="80">
                    <template #default="{ row }">
                        <el-tag :type="row.type === 'income' ? 'success' : 'danger'">
                            {{ row.type === 'income' ? '收入' : '支出' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="amount" label="金额" width="120">
                    <template #default="{ row }">
                        <span :style="{ color: row.type === 'income' ? '#67c23a' : '#f56c6c' }">
                            {{ row.type === 'income' ? '+' : '-' }}{{ row.amount }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column prop="remark" label="备注" show-overflow-tooltip />
                <el-table-column label="操作" width="150" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" size="small" @click="openDialog(row)">编辑</el-button>
                        <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <el-dialog v-model="dialogVisible" title="记账" width="500px">
            <el-form :model="form" label-width="80px">
                <el-form-item label="类型">
                    <el-radio-group v-model="form.type">
                        <el-radio label="expense">支出</el-radio>
                        <el-radio label="income">收入</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="金额">
                    <el-input-number v-model="form.amount" :min="0" :precision="2" />
                </el-form-item>
                <el-form-item label="分类">
                    <el-select v-model="form.categoryId" placeholder="请选择">
                        <el-option
                            v-for="item in filteredCategories"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="日期">
                    <el-date-picker v-model="form.date" type="date" placeholder="选择日期" />
                </el-form-item>
                <el-form-item label="备注">
                    <el-input v-model="form.remark" type="textarea" :rows="3" />
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { accountApi, categoryApi } from '@/api'
import { store } from '@/store'

const accountList = ref([])
const categories = ref([])
const dateRange = ref([])
const filterForm = ref({
    type: '',
    categoryId: null
})
const dialogVisible = ref(false)
const form = ref({
    id: null,
    userId: null,
    type: 'expense',
    amount: 0,
    categoryId: null,
    date: new Date(),
    remark: ''
})

const filteredCategories = computed(() => {
    return categories.value.filter(item => item.type === form.value.type)
})

onMounted(() => {
    loadCategories()
    loadData()
})

const loadCategories = async () => {
    categories.value = await categoryApi.list(store.user.id)
}

const loadData = async () => {
    const params = {
        userId: store.user.id,
        type: filterForm.value.type || null,
        categoryId: filterForm.value.categoryId,
        startDate: dateRange.value?.[0]?.toISOString().split('T')[0],
        endDate: dateRange.value?.[1]?.toISOString().split('T')[0]
    }
    accountList.value = await accountApi.list(params)
}

const resetFilter = () => {
    dateRange.value = []
    filterForm.value = { type: '', categoryId: null }
    loadData()
}

const openDialog = (row = null) => {
    if (row) {
        form.value = { ...row }
    } else {
        form.value = {
            id: null,
            userId: store.user.id,
            type: 'expense',
            amount: 0,
            categoryId: null,
            date: new Date(),
            remark: ''
        }
    }
    dialogVisible.value = true
}

const handleSave = async () => {
    try {
        if (form.value.id) {
            await accountApi.update(form.value)
            ElMessage.success('更新成功')
        } else {
            await accountApi.add(form.value)
            ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadData()
    } catch (error) {
        console.error('保存失败', error)
    }
}

const handleDelete = (id) => {
    ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        await accountApi.delete(id)
        ElMessage.success('删除成功')
        loadData()
    }).catch(() => {})
}
</script>

<style scoped>
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.filter-form {
    margin-bottom: 20px;
}
</style>
