<template>
  <div class="record-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>账目管理</span>
          <el-button type="primary" @click="showAddDialog = true">添加账目</el-button>
        </div>
      </template>
      
      <div class="filter-bar">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-select v-model="queryParams.type" placeholder="选择类型" clearable>
              <el-option label="支出" :value="1" />
              <el-option label="收入" :value="2" />
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-select v-model="queryParams.categoryId" placeholder="选择分类" clearable>
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              >
                <el-icon class="mr-1">
                  <component :is="category.icon" />
                </el-icon>
                {{ category.name }}
              </el-option>
            </el-select>
          </el-col>
          <el-col :span="8">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
            />
          </el-col>
          <el-col :span="4">
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-col>
        </el-row>
      </div>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="recordTime" label="日期" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.recordTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.type === 1 ? 'danger' : 'success'">
              {{ scope.row.type === 1 ? '支出' : '收入' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="120">
          <template #default="scope">
            <el-icon class="mr-1">
              <component :is="scope.row.categoryIcon" />
            </el-icon>
            {{ scope.row.categoryName }}
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120" align="right">
          <template #default="scope">
            <span :class="scope.row.type === 1 ? 'expense-text' : 'income-text'">
              {{ scope.row.type === 1 ? '-' : '+' }}¥{{ scope.row.amount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <AddRecordDialog v-model="showAddDialog" :edit-data="editData" @success="loadData" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import { getCategoryList } from '@/api/category'
import { getRecordPage, deleteRecord } from '@/api/record'
import AddRecordDialog from '@/components/AddRecordDialog.vue'

const user = JSON.parse(localStorage.getItem('user') || '{}')
const loading = ref(false)
const categories = ref([])
const tableData = ref([])
const total = ref(0)
const showAddDialog = ref(false)
const editData = ref(null)
const dateRange = ref([])

const queryParams = reactive({
  userId: user.id,
  type: null,
  categoryId: null,
  startTime: null,
  endTime: null,
  pageNum: 1,
  pageSize: 10
})

const formatDateTime = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const loadCategories = async () => {
  const data = await getCategoryList({
    userId: user.id
  })
  categories.value = data
}

const loadData = async () => {
  loading.value = true
  try {
    if (dateRange.value && dateRange.value.length === 2) {
      queryParams.startTime = dateRange.value[0] + ' 00:00:00'
      queryParams.endTime = dateRange.value[1] + ' 23:59:59'
    } else {
      queryParams.startTime = null
      queryParams.endTime = null
    }
    const data = await getRecordPage(queryParams)
    tableData.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const resetFilter = () => {
  queryParams.type = null
  queryParams.categoryId = null
  dateRange.value = []
  queryParams.pageNum = 1
  loadData()
}

const handleEdit = (row) => {
  editData.value = row
  showAddDialog.value = true
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteRecord(id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

onMounted(() => {
  loadCategories()
  loadData()
})
</script>

<style scoped>
.record-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-bar {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.income-text {
  color: #67c23a;
  font-weight: 600;
}

.expense-text {
  color: #f56c6c;
  font-weight: 600;
}
</style>
