<template>
    <div class="transaction">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>账目管理</span>
                    <el-button type="primary" @click="handleAdd">
                        <el-icon><Plus /></el-icon>
                        添加账目
                    </el-button>
                </div>
            </template>

            <el-form :inline="true" :model="queryForm" class="query-form">
                <el-form-item label="类型">
                    <el-select v-model="queryForm.type" placeholder="请选择" clearable>
                        <el-option label="全部" :value="null" />
                        <el-option label="收入" :value="1" />
                        <el-option label="支出" :value="2" />
                    </el-select>
                </el-form-item>
                <el-form-item label="分类">
                    <el-select v-model="queryForm.categoryId" placeholder="请选择" clearable>
                        <el-option label="全部" :value="null" />
                        <el-option
                            v-for="cat in categoryList"
                            :key="cat.id"
                            :label="cat.name"
                            :value="cat.id"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="日期范围">
                    <el-date-picker
                        v-model="queryForm.dateRange"
                        type="daterange"
                        range-separator="至"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        value-format="YYYY-MM-DD"
                    />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadTransactions">
                        <el-icon><Search /></el-icon>
                        搜索
                    </el-button>
                    <el-button @click="resetQuery">
                        <el-icon><Refresh /></el-icon>
                        重置
                    </el-button>
                </el-form-item>
            </el-form>

            <el-table :data="tableData" stripe border>
                <el-table-column prop="type" label="类型" width="80">
                    <template #default="{ row }">
                        <el-tag :type="row.type === 1 ? 'success' : 'danger'">
                            {{ row.type === 1 ? '收入' : '支出' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="categoryName" label="分类" width="120" />
                <el-table-column prop="amount" label="金额" width="120">
                    <template #default="{ row }">
                        <span :class="row.type === 1 ? 'text-success' : 'text-danger'">
                            {{ row.type === 1 ? '+' : '-' }}¥{{ row.amount }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column prop="transactionDate" label="日期" width="120" />
                <el-table-column prop="remark" label="备注" show-overflow-tooltip />
                <el-table-column prop="createTime" label="创建时间" width="180" />
                <el-table-column label="操作" width="150" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" link size="small" @click="handleEdit(row)">
                            编辑
                        </el-button>
                        <el-button type="danger" link size="small" @click="handleDelete(row)">
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <el-pagination
                v-model:current-page="queryForm.page"
                v-model:page-size="queryForm.size"
                :total="total"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="loadTransactions"
                @current-change="loadTransactions"
                class="pagination"
            />
        </el-card>

        <el-dialog
            v-model="dialogVisible"
            :title="isEdit ? '编辑账目' : '添加账目'"
            width="500px"
            :close-on-click-modal="false"
        >
            <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
                <el-form-item label="类型" prop="type">
                    <el-radio-group v-model="form.type">
                        <el-radio :value="1">收入</el-radio>
                        <el-radio :value="2">支出</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="分类" prop="categoryId">
                    <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
                        <el-option
                            v-for="cat in filteredCategories"
                            :key="cat.id"
                            :label="cat.name"
                            :value="cat.id"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="金额" prop="amount">
                    <el-input-number v-model="form.amount" :min="0.01" :precision="2" style="width: 100%" />
                </el-form-item>
                <el-form-item label="日期" prop="transactionDate">
                    <el-date-picker
                        v-model="form.transactionDate"
                        type="date"
                        placeholder="选择日期"
                        value-format="YYYY-MM-DD"
                        style="width: 100%"
                    />
                </el-form-item>
                <el-form-item label="备注" prop="remark">
                    <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
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
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const queryForm = reactive({
    page: 1,
    size: 10,
    type: null,
    categoryId: null,
    dateRange: []
})

const tableData = ref([])
const total = ref(0)
const categoryList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
    id: null,
    type: 2,
    categoryId: null,
    amount: null,
    transactionDate: '',
    remark: ''
})

const rules = {
    type: [{ required: true, message: '请选择类型', trigger: 'change' }],
    categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
    amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
    transactionDate: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

const filteredCategories = computed(() => {
    return categoryList.value.filter(cat => cat.type === form.type)
})

watch(() => form.type, () => {
    form.categoryId = null
})

const loadCategories = async () => {
    try {
        const res = await request.get('/category')
        categoryList.value = res.data || []
    } catch (e) {
        ElMessage.error('加载分类失败')
    }
}

const loadTransactions = async () => {
    try {
        const params = {
            page: queryForm.page,
            size: queryForm.size,
            type: queryForm.type,
            categoryId: queryForm.categoryId
        }
        if (queryForm.dateRange && queryForm.dateRange.length === 2) {
            params.startDate = queryForm.dateRange[0]
            params.endDate = queryForm.dateRange[1]
        }
        const res = await request.get('/transaction', { params })
        tableData.value = res.data.records || []
        total.value = res.data.total || 0
    } catch (e) {
        ElMessage.error('加载账目列表失败')
    }
}

const resetQuery = () => {
    queryForm.type = null
    queryForm.categoryId = null
    queryForm.dateRange = []
    queryForm.page = 1
    loadTransactions()
}

const handleAdd = () => {
    isEdit.value = false
    Object.assign(form, {
        id: null,
        type: 2,
        categoryId: null,
        amount: null,
        transactionDate: new Date().toISOString().split('T')[0],
        remark: ''
    })
    dialogVisible.value = true
}

const handleEdit = (row) => {
    isEdit.value = true
    Object.assign(form, {
        id: row.id,
        type: row.type,
        categoryId: row.categoryId,
        amount: row.amount,
        transactionDate: row.transactionDate,
        remark: row.remark
    })
    dialogVisible.value = true
}

const handleDelete = (row) => {
    ElMessageBox.confirm('确定要删除这条账目吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            await request.delete(`/transaction/${row.id}`)
            ElMessage.success('删除成功')
            loadTransactions()
        } catch (e) {
            ElMessage.error('删除失败')
        }
    }).catch(() => {})
}

const submitForm = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (valid) {
            try {
                if (isEdit.value) {
                    await request.put(`/transaction/${form.id}`, form)
                    ElMessage.success('编辑成功')
                } else {
                    await request.post('/transaction', form)
                    ElMessage.success('添加成功')
                }
                dialogVisible.value = false
                loadTransactions()
            } catch (e) {
                ElMessage.error(isEdit.value ? '编辑失败' : '添加失败')
            }
        }
    })
}

onMounted(() => {
    loadCategories()
    loadTransactions()
})
</script>

<style scoped>
.transaction {
    padding: 0;
}
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.query-form {
    margin-bottom: 20px;
}
.text-success {
    color: #52c41a;
    font-weight: bold;
}
.text-danger {
    color: #f5222d;
    font-weight: bold;
}
.pagination {
    margin-top: 20px;
    text-align: right;
}
</style>