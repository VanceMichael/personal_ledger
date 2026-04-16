<template>
    <div class="category-container">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>分类管理</span>
                    <el-button type="primary" @click="openDialog">
                        <el-icon><Plus /></el-icon>
                        新增分类
                    </el-button>
                </div>
            </template>

            <el-tabs v-model="activeTab" @tab-change="handleTabChange">
                <el-tab-pane label="支出分类" name="expense" />
                <el-tab-pane label="收入分类" name="income" />
            </el-tabs>

            <el-table :data="filteredCategories" border stripe>
                <el-table-column prop="icon" label="图标" width="80">
                    <template #default="{ row }">
                        <span style="font-size: 24px">{{ row.icon }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="name" label="分类名称" />
                <el-table-column prop="sort" label="排序" width="100" />
                <el-table-column label="操作" width="150" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" size="small" @click="openDialog(row)">编辑</el-button>
                        <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <el-dialog v-model="dialogVisible" title="分类信息" width="500px">
            <el-form :model="form" label-width="80px">
                <el-form-item label="类型">
                    <el-radio-group v-model="form.type">
                        <el-radio label="expense">支出</el-radio>
                        <el-radio label="income">收入</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="名称">
                    <el-input v-model="form.name" placeholder="请输入分类名称" />
                </el-form-item>
                <el-form-item label="图标">
                    <el-popover v-model:visible="iconPickerVisible" width="400" trigger="click">
                        <div class="icon-grid">
                            <span
                                v-for="icon in iconList"
                                :key="icon"
                                class="icon-item"
                                :class="{ active: form.icon === icon }"
                                @click="selectIcon(icon)"
                            >
                                {{ icon }}
                            </span>
                        </div>
                        <template #reference>
                            <el-input v-model="form.icon" readonly placeholder="点击选择图标">
                                <template #prefix>
                                    <span style="font-size: 20px">{{ form.icon || '😊' }}</span>
                                </template>
                            </el-input>
                        </template>
                    </el-popover>
                </el-form-item>
                <el-form-item label="排序">
                    <el-input-number v-model="form.sort" :min="0" />
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
import { categoryApi } from '@/api'
import { store } from '@/store'

const categories = ref([])
const activeTab = ref('expense')
const dialogVisible = ref(false)
const iconPickerVisible = ref(false)
const form = ref({
    id: null,
    userId: null,
    type: 'expense',
    name: '',
    icon: '',
    sort: 0
})

const iconList = [
    '🍔', '🍜', '☕', '🍞', '🥘', '🍱', '🥤', '🍰',
    '🚗', '🚌', '🚇', '✈️', '🚕', '🚲', '⛽', '🚄',
    '🛍️', '👕', '👟', '💄', '🎒', '💍', '📱', '💻',
    '🏠', '💡', '💧', '📺', '❄️', '🔥', '📞', '🧹',
    '💊', '🏥', '💉', '💪', '🧴', '🦷', '👓', '💈',
    '🎓', '📚', '✏️', '🎨', '🎵', '🎬', '🎮', '🎭',
    '💰', '💳', '💵', '💎', '📈', '🏦', '💸', '🧧',
    '💼', '📝', '📊', '🖨️', '📎', '✂️', '📦', '🔧',
    '🎁', '🎉', '💐', '🎂', '🍺', '🍷', '🎈', '🎊',
    '🐶', '🐱', '🐟', '🐦', '🌳', '🌺', '🌻', '🌷'
]

const filteredCategories = computed(() => {
    return categories.value.filter(item => item.type === activeTab.value)
})

onMounted(() => {
    loadData()
})

const loadData = async () => {
    categories.value = await categoryApi.list(store.user.id)
}

const handleTabChange = () => {
    form.value.type = activeTab.value
}

const openDialog = (row = null) => {
    if (row) {
        form.value = { ...row }
    } else {
        form.value = {
            id: null,
            userId: store.user.id,
            type: activeTab.value,
            name: '',
            icon: '',
            sort: 0
        }
    }
    dialogVisible.value = true
}

const selectIcon = (icon) => {
    form.value.icon = icon
    iconPickerVisible.value = false
}

const handleSave = async () => {
    try {
        if (form.value.id) {
            await categoryApi.update(form.value)
            ElMessage.success('更新成功')
        } else {
            await categoryApi.add(form.value)
            ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadData()
    } catch (error) {
        console.error('保存失败', error)
    }
}

const handleDelete = (id) => {
    ElMessageBox.confirm('确定要删除这个分类吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        await categoryApi.delete(id)
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
.icon-grid {
    display: grid;
    grid-template-columns: repeat(8, 1fr);
    gap: 8px;
}
.icon-item {
    font-size: 24px;
    padding: 8px;
    text-align: center;
    cursor: pointer;
    border-radius: 4px;
    transition: background-color 0.3s;
}
.icon-item:hover {
    background-color: #f5f7fa;
}
.icon-item.active {
    background-color: #409eff;
    color: white;
}
</style>
