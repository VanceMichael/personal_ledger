<template>
  <div class="category-container">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>支出分类</span>
              <el-button type="primary" size="small" @click="showAddDialog(1)">新增分类</el-button>
            </div>
          </template>
          <el-table :data="expenseCategories" style="width: 100%">
            <el-table-column prop="icon" label="图标" width="80" align="center">
              <template #default="scope">
                <el-icon size="24">
                  <component :is="scope.row.icon" />
                </el-icon>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="分类名称" />
            <el-table-column prop="sort" label="排序" width="80" align="center" />
            <el-table-column prop="type" label="类型" width="80" align="center">
              <template #default="scope">
                <el-tag size="small" :type="scope.row.userId === 0 ? 'info' : 'success'">
                  {{ scope.row.userId === 0 ? '系统' : '自定义' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right" v-if="scope.row.userId !== 0">
              <template #default="scope">
                <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
                <el-button size="small" type="danger" @click="handleDelete(scope.row.id)" :disabled="scope.row.userId === 0">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>收入分类</span>
              <el-button type="primary" size="small" @click="showAddDialog(2)">新增分类</el-button>
            </div>
          </template>
          <el-table :data="incomeCategories" style="width: 100%">
            <el-table-column prop="icon" label="图标" width="80" align="center">
              <template #default="scope">
                <el-icon size="24">
                  <component :is="scope.row.icon" />
                </el-icon>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="分类名称" />
            <el-table-column prop="sort" label="排序" width="80" align="center" />
            <el-table-column prop="type" label="类型" width="80" align="center">
              <template #default="scope">
                <el-tag size="small" :type="scope.row.userId === 0 ? 'info' : 'success'">
                  {{ scope.row.userId === 0 ? '系统' : '自定义' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right" v-if="scope.row.userId !== 0">
              <template #default="scope">
                <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
                <el-button size="small" type="danger" @click="handleDelete(scope.row.id)" :disabled="scope.row.userId === 0">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" :title="editMode ? '编辑分类' : '新增分类'" width="400px">
      <el-form ref="formRef" :model="form" label-width="80px">
        <el-form-item label="分类名称" prop="name" :rules="[{ required: true, message: '请输入分类名称', trigger: 'blur' }]">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="图标" prop="icon" :rules="[{ required: true, message: '请选择图标', trigger: 'change' }]">
          <el-select v-model="form.icon" placeholder="请选择图标">
            <el-option
              v-for="icon in iconList"
              :key="icon"
              :label="icon"
              :value="icon"
            >
              <el-icon class="mr-1">
                <component :is="icon" />
              </el-icon>
              {{ icon }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as Icons from '@element-plus/icons-vue'
import { getCategoryList, addCategory, updateCategory, deleteCategory } from '@/api/category'

const user = JSON.parse(localStorage.getItem('user') || '{}')
const loading = ref(false)
const dialogVisible = ref(false)
const editMode = ref(false)
const editId = ref(null)
const formRef = ref()
const categories = ref([])

const iconList = Object.keys(Icons)

const form = reactive({
  userId: user.id,
  type: 1,
  name: '',
  icon: '',
  sort: 99
})

const expenseCategories = computed(() => {
  return categories.value.filter(c => c.type === 1)
})

const incomeCategories = computed(() => {
  return categories.value.filter(c => c.type === 2)
})

const loadCategories = async () => {
  const data = await getCategoryList({
    userId: user.id
  })
  categories.value = data
}

const showAddDialog = (type) => {
  editMode.value = false
  editId.value = null
  form.type = type
  form.name = ''
  form.icon = ''
  form.sort = 99
  formRef.value?.resetFields()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  editMode.value = true
  editId.value = row.id
  form.type = row.type
  form.name = row.name
  form.icon = row.icon
  form.sort = row.sort
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  try {
    loading.value = true
    if (editMode.value) {
      await updateCategory(editId.value, form)
      ElMessage.success('修改成功')
    } else {
      await addCategory(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadCategories()
  } finally {
    loading.value = false
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这个分类吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteCategory(id)
    ElMessage.success('删除成功')
    loadCategories()
  }).catch(() => {})
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.category-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
