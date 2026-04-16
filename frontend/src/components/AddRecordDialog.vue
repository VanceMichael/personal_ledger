<template>
  <el-dialog v-model="visible" title="添加账目" width="500px" @closed="resetForm">
    <el-form ref="formRef" :model="form" label-width="80px">
      <el-form-item label="类型">
        <el-radio-group v-model="form.type" @change="loadCategories">
          <el-radio :label="1">支出</el-radio>
          <el-radio :label="2">收入</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="金额" prop="amount" :rules="[{ required: true, message: '请输入金额', trigger: 'blur' }]">
        <el-input v-model.number="form.amount" type="number" placeholder="请输入金额" />
      </el-form-item>
      <el-form-item label="分类" prop="categoryId" :rules="[{ required: true, message: '请选择分类', trigger: 'change' }]">
        <el-select v-model="form.categoryId" placeholder="请选择分类">
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
      </el-form-item>
      <el-form-item label="日期" prop="recordTime" :rules="[{ required: true, message: '请选择日期', trigger: 'change' }]">
        <el-date-picker
          v-model="form.recordTime"
          type="datetime"
          placeholder="选择日期时间"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" :rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { getCategoryList } from '@/api/category'
import { addRecord, updateRecord } from '@/api/record'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  editData: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const formRef = ref()
const loading = ref(false)
const categories = ref([])
const user = JSON.parse(localStorage.getItem('user') || '{}')
const form = ref({
  userId: user.id,
  type: 1,
  amount: '',
  categoryId: '',
  recordTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
  remark: ''
})

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const loadCategories = async () => {
  const data = await getCategoryList({
    userId: user.id,
    type: form.value.type
  })
  categories.value = data
}

const resetForm = () => {
  form.value = {
    userId: user.id,
    type: 1,
    amount: '',
    categoryId: '',
    recordTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
    remark: ''
  }
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  try {
    loading.value = true
    if (props.editData) {
      await updateRecord(props.editData.id, form.value)
      ElMessage.success('修改成功')
    } else {
      await addRecord(form.value)
      ElMessage.success('添加成功')
    }
    visible.value = false
    emit('success')
  } finally {
    loading.value = false
  }
}

watch(() => props.editData, (val) => {
  if (val) {
    form.value = {
      userId: user.id,
      type: val.type,
      amount: val.amount,
      categoryId: val.categoryId,
      recordTime: val.recordTime,
      remark: val.remark || ''
    }
  }
}, { immediate: true })

onMounted(() => {
  loadCategories()
})
</script>
