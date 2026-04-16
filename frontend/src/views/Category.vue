<template>
    <div class="category">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>分类管理</span>
                    <el-button type="primary" @click="handleAdd">
                        <el-icon><Plus /></el-icon>
                        添加分类
                    </el-button>
                </div>
            </template>

            <el-tabs v-model="activeTab" type="card">
                <el-tab-pane label="支出分类" name="expense">
                    <div class="category-list">
                        <div
                            v-for="item in expenseCategories"
                            :key="item.id"
                            class="category-item"
                            :class="{ 'system-item': !item.userId }"
                        >
                            <div class="category-info">
                                <span class="category-icon">{{ item.icon }}</span>
                                <span class="category-name">{{ item.name }}</span>
                                <el-tag v-if="!item.userId" size="small" type="info">系统</el-tag>
                            </div>
                            <div class="category-actions" v-if="item.userId">
                                <el-button type="primary" link size="small" @click="handleEdit(item)">
                                    编辑
                                </el-button>
                                <el-button type="danger" link size="small" @click="handleDelete(item)">
                                    删除
                                </el-button>
                            </div>
                        </div>
                    </div>
                </el-tab-pane>
                <el-tab-pane label="收入分类" name="income">
                    <div class="category-list">
                        <div
                            v-for="item in incomeCategories"
                            :key="item.id"
                            class="category-item"
                            :class="{ 'system-item': !item.userId }"
                        >
                            <div class="category-info">
                                <span class="category-icon">{{ item.icon }}</span>
                                <span class="category-name">{{ item.name }}</span>
                                <el-tag v-if="!item.userId" size="small" type="info">系统</el-tag>
                            </div>
                            <div class="category-actions" v-if="item.userId">
                                <el-button type="primary" link size="small" @click="handleEdit(item)">
                                    编辑
                                </el-button>
                                <el-button type="danger" link size="small" @click="handleDelete(item)">
                                    删除
                                </el-button>
                            </div>
                        </div>
                    </div>
                </el-tab-pane>
            </el-tabs>
        </el-card>

        <el-dialog
            v-model="dialogVisible"
            :title="isEdit ? '编辑分类' : '添加分类'"
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
                <el-form-item label="名称" prop="name">
                    <el-input v-model="form.name" placeholder="请输入分类名称" />
                </el-form-item>
                <el-form-item label="图标" prop="icon">
                    <div class="icon-selector">
                        <div
                            v-for="icon in iconList"
                            :key="icon"
                            class="icon-option"
                            :class="{ active: form.icon === icon }"
                            @click="form.icon = icon"
                        >
                            {{ icon }}
                        </div>
                    </div>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const activeTab = ref('expense')
const categoryList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const iconList = ['🍚', '🚗', '🛒', '👔', '🏠', '💊', '🎮', '📚', '🎁', '💄', '💼', '💰', '💵', '🎯', '⭐', '❤️', '🔥', '✨', '🌟', '💎']

const form = reactive({
    id: null,
    type: 2,
    name: '',
    icon: '💰'
})

const rules = {
    type: [{ required: true, message: '请选择类型', trigger: 'change' }],
    name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
    icon: [{ required: true, message: '请选择图标', trigger: 'change' }]
}

const expenseCategories = computed(() => {
    return categoryList.value.filter(cat => cat.type === 2)
})

const incomeCategories = computed(() => {
    return categoryList.value.filter(cat => cat.type === 1)
})

const loadCategories = async () => {
    try {
        const res = await request.get('/category')
        categoryList.value = res.data || []
    } catch (e) {
        ElMessage.error('加载分类列表失败')
    }
}

const handleAdd = () => {
    isEdit.value = false
    Object.assign(form, {
        id: null,
        type: activeTab.value === 'expense' ? 2 : 1,
        name: '',
        icon: '💰'
    })
    dialogVisible.value = true
}

const handleEdit = (item) => {
    isEdit.value = true
    Object.assign(form, {
        id: item.id,
        type: item.type,
        name: item.name,
        icon: item.icon
    })
    dialogVisible.value = true
}

const handleDelete = (item) => {
    ElMessageBox.confirm('确定要删除这个分类吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            await request.delete(`/category/${item.id}`)
            ElMessage.success('删除成功')
            loadCategories()
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
                    await request.put(`/category/${form.id}`, form)
                    ElMessage.success('编辑成功')
                } else {
                    await request.post('/category', form)
                    ElMessage.success('添加成功')
                }
                dialogVisible.value = false
                loadCategories()
            } catch (e) {
                ElMessage.error(isEdit.value ? '编辑失败' : '添加失败')
            }
        }
    })
}

onMounted(() => {
    loadCategories()
})
</script>

<style scoped>
.category {
    padding: 0;
}
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.category-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 15px;
    padding: 10px 0;
}
.category-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    transition: all 0.3s;
}
.category-item:hover {
    border-color: #409eff;
    box-shadow: 0 2px 12px rgba(64, 158, 255, 0.1);
}
.category-item.system-item {
    background: #f5f7fa;
}
.category-info {
    display: flex;
    align-items: center;
    gap: 10px;
}
.category-icon {
    font-size: 24px;
}
.category-name {
    font-size: 14px;
    color: #303133;
}
.category-actions {
    display: flex;
    gap: 5px;
}
.icon-selector {
    display: grid;
    grid-template-columns: repeat(10, 1fr);
    gap: 8px;
    padding: 10px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
}
.icon-option {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.3s;
}
.icon-option:hover {
    border-color: #409eff;
}
.icon-option.active {
    border-color: #409eff;
    background: #ecf5ff;
}
</style>