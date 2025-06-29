<template>
  <div class="recovery-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>回收管理</span>
          <el-button type="primary" @click="$router.push('/recovery/apply')">
            <el-icon><Plus /></el-icon>
            申请回收
          </el-button>
        </div>
      </template>
      
      <!-- 搜索条件 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px;" @change="handleStatusChange">
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="作物类型">
          <el-input v-model="searchForm.cropType" placeholder="请输入作物类型" clearable />
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
      
      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="cropType" label="作物类型" />
        <el-table-column prop="weight" label="重量(kg)" width="120" />
        <el-table-column prop="location" label="地点" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="appointmentTime" label="预约时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.appointmentTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleView(row)"
            >
              查看
            </el-button>
            
            <el-button
              v-if="row.status === 0"
              type="success"
              size="small"
              @click="handleAudit(row, 1)"
            >
              通过
            </el-button>
            
            <el-button
              v-if="row.status === 0"
              type="danger"
              size="small"
              @click="handleAudit(row, 2)"
            >
              拒绝
            </el-button>
            
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(row)"
            >
              删除
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
    
    <!-- 审核对话框 -->
    <el-dialog
      v-model="auditDialogVisible"
      title="审核回收申请"
      width="500px"
    >
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.status">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input
            v-model="auditForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入审核备注"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="auditDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmAudit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import { recoveryApi } from '../../api/recovery'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = reactive({
  status: '',
  cropType: ''
})

// 审核对话框
const auditDialogVisible = ref(false)
const auditForm = reactive({
  id: null,
  status: 1,
  remark: ''
})

// 加载数据
const loadData = async () => {
  try {
    loading.value = true
    // 根据用户类型选择API
    const api = userStore.userInfo?.userType === 1 ? recoveryApi.allRecords : recoveryApi.myRecords
    // 构造参数，status仅在有明确值时传递
    const params = {
      page: pagination.current,
      size: pagination.size,
      cropType: searchForm.cropType
    }
    if (searchForm.status !== '' && searchForm.status !== null && searchForm.status !== undefined) {
      params.status = searchForm.status
    }
    const response = await api(params)
    if (response.code === 200) {
      tableData.value = response.data.records || []
      pagination.total = response.data.total || 0
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    status: '',
    cropType: ''
  })
  pagination.current = 1
  loadData()
}

// 查看详情
const handleView = (row) => {
  router.push(`/recovery/detail/${row.id}`)
}

// 审核
const handleAudit = (row, status) => {
  auditForm.id = row.id
  auditForm.status = status
  auditForm.remark = ''
  auditDialogVisible.value = true
}

// 确认审核
const confirmAudit = async () => {
  try {
    const response = await recoveryApi.audit(auditForm)
    if (response.code === 200) {
      ElMessage.success('审核成功')
      auditDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(response.message || '审核失败')
    }
  } catch (error) {
    console.error('审核失败:', error)
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条回收记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await recoveryApi.delete(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 分页
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  loadData()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  loadData()
}

// 格式化时间
const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    0: 'info',
    1: 'success',
    2: 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝'
  }
  return statusMap[status] || '未知'
}

// 状态变化
const handleStatusChange = () => {
  pagination.current = 1
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.recovery-list {
  width: 100%;
  padding: 16px 16px 0 16px;
  box-sizing: border-box;
}

.el-card {
  width: 100%;
  box-sizing: border-box;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 