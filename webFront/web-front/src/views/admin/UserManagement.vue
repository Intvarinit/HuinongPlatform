<template>
  <div class="user-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
        </div>
      </template>
      <!-- 搜索栏 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="searchForm.userType" placeholder="请选择类型" clearable style="min-width: 120px">
            <el-option label="管理员" :value="1" />
            <el-option label="抽检者" :value="2" />
            <el-option label="农户" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="min-width: 100px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" style="width: 100%" border>
        <el-table-column prop="id" label="ID" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="真实姓名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="userType" label="用户类型">
          <template #default="{ row }">
            <el-tag v-if="row.userType === 1" type="danger">管理员</el-tag>
            <el-tag v-else-if="row.userType === 2" type="warning">抽检者</el-tag>
            <el-tag v-else type="success">农户</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="showDetail(row)">详情</el-button>
            <el-dropdown @command="cmd => changeType(row, cmd)">
              <el-button size="small">修改身份<el-icon><arrow-down /></el-icon></el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="row.userType !== 1" command="1">管理员</el-dropdown-item>
                  <el-dropdown-item v-if="row.userType !== 2" command="2">抽检者</el-dropdown-item>
                  <el-dropdown-item v-if="row.userType !== 3" command="3">农户</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button size="small" :type="row.status === 1 ? 'info' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    <!-- 详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="用户详情" width="80%">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ detailRow.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ detailRow.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ detailRow.realName }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailRow.phone }}</el-descriptions-item>
        <el-descriptions-item label="用户类型">
          <el-tag v-if="detailRow.userType === 1" type="danger">管理员</el-tag>
          <el-tag v-else-if="detailRow.userType === 2" type="warning">抽检者</el-tag>
          <el-tag v-else type="success">农户</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailRow.status === 1 ? 'success' : 'info'">{{ detailRow.status === 1 ? '启用' : '禁用' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ formatTime(detailRow.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="地址">{{ detailRow.address }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, ArrowDown } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { userApi } from '../../api/user'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})
const searchForm = reactive({
  username: '',
  phone: '',
  userType: undefined,
  status: undefined
})

const detailDialogVisible = ref(false)
const detailRow = ref({})

const loadData = async () => {
  try {
    loading.value = true
    const res = await userApi.getUserList({
      pageNum: pagination.current,
      pageSize: pagination.size,
      ...searchForm
    })
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取用户失败')
    }
  } catch (e) {
    ElMessage.error('获取用户失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}
const handleReset = () => {
  Object.assign(searchForm, {
    username: '',
    phone: '',
    userType: undefined,
    status: undefined
  })
  pagination.current = 1
  loadData()
}
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  loadData()
}
const handleCurrentChange = (current) => {
  pagination.current = current
  loadData()
}
const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

const showDetail = (row) => {
  detailRow.value = row
  detailDialogVisible.value = true
}

const changeType = async (row, userType) => {
  try {
    await userApi.updateUserType(row.id, userType)
    ElMessage.success('身份修改成功')
    loadData()
  } catch (e) {
    ElMessage.error('身份修改失败')
  }
}

const toggleStatus = async (row) => {
  try {
    await userApi.updateUserStatus(row.id, row.status === 1 ? 0 : 1)
    ElMessage.success('操作成功')
    loadData()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.user-management {
  width: 100%;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
.el-card {
  width: 100%;
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.search-form {
  margin-bottom: 16px;
}
.pagination {
  margin-top: 20px;
  text-align: right;
}
</style> 