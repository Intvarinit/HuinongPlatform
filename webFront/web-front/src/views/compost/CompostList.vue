<template>
  <div class="compost-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>堆肥管理</span>
          <el-button type="primary" @click="$router.push('/compost/create')">
            <el-icon><Plus /></el-icon>
            创建批次
          </el-button>
        </div>
      </template>
      
      <!-- 搜索条件 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="回收记录ID">
          <el-input
            v-model.number="searchForm.recoveryId"
            placeholder="请输入回收记录ID"
            clearable
            type="number"
          />
        </el-form-item>
        
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="min-width: 120px"
            @change="handleSearch"
          >
            <el-option label="进行中" :value="0" />
            <el-option label="已完成" :value="1" />
            <el-option label="异常" :value="2" />
            <el-option label="已取消" :value="3" />
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
      
      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="recoveryId" label="回收记录ID" />
        <el-table-column prop="batchNo" label="批次号" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间">
          <template #default="{ row }">
            {{ formatTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间">
          <template #default="{ row }">
            {{ formatTime(row.endTime) || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="temperature" label="温度(°C)">
          <template #default="{ row }">
            {{ row.temperature || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="humidity" label="湿度(%)">
          <template #default="{ row }">
            {{ row.humidity || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="phValue" label="pH值">
          <template #default="{ row }">
            {{ row.phValue || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300">
          <template #default="{ row }">
            <div class="action-btns">
              <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
              <el-button v-if="row.status === 0" type="warning" size="small" @click="handleUpdate(row)">更新</el-button>
              <el-button v-if="row.status === 0" type="success" size="small" @click="handleFinish(row)">完成</el-button>
              <el-button v-if="row.status === 0" type="danger" size="small" @click="handleAbnormal(row)">异常</el-button>
            </div>
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
    
    <!-- 更新对话框 -->
    <el-dialog
      v-model="updateDialogVisible"
      title="更新堆肥批次"
      width="600px"
    >
      <el-form :model="updateForm" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="温度(°C)">
              <el-input-number
                v-model="updateForm.temperature"
                :min="0"
                :max="100"
                :precision="1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="湿度(%)">
              <el-input-number
                v-model="updateForm.humidity"
                :min="0"
                :max="100"
                :precision="1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="pH值">
          <el-input-number
            v-model="updateForm.phValue"
            :min="0"
            :max="14"
            :precision="1"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input
            v-model="updateForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="updateDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmUpdate">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 完成/异常对话框 -->
    <el-dialog
      v-model="actionDialogVisible"
      :title="actionType === 'finish' ? '完成堆肥批次' : '标记异常'"
      width="500px"
    >
      <el-form :model="actionForm" label-width="80px">
        <el-form-item label="备注">
          <el-input
            v-model="actionForm.remark"
            type="textarea"
            :rows="3"
            :placeholder="actionType === 'finish' ? '请输入完成备注' : '请输入异常原因'"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="actionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmAction">确定</el-button>
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
import { compostApi } from '../../api/compost'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = reactive({
  recoveryId: null,
  status: null
})

// 更新对话框
const updateDialogVisible = ref(false)
const updateForm = reactive({
  id: null,
  temperature: null,
  humidity: null,
  phValue: null,
  remark: ''
})

// 操作对话框
const actionDialogVisible = ref(false)
const actionType = ref('')
const actionForm = reactive({
  id: null,
  remark: ''
})

// 加载数据
const loadData = async () => {
  try {
    loading.value = true
    
    const params = {
      page: pagination.current,
      size: pagination.size
    }
    
    if (searchForm.recoveryId && searchForm.recoveryId > 0) {
      params.recoveryId = searchForm.recoveryId
    }
    if (searchForm.status !== null && searchForm.status !== '') {
      params.status = searchForm.status
    }
    
    const response = await compostApi.page(params)
    
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
    recoveryId: null,
    status: null
  })
  pagination.current = 1
  loadData()
}

// 查看详情
const handleView = (row) => {
  router.push(`/compost/detail/${row.id}`)
}

// 更新
const handleUpdate = (row) => {
  updateForm.id = row.id
  updateForm.temperature = row.temperature
  updateForm.humidity = row.humidity
  updateForm.phValue = row.phValue
  updateForm.remark = ''
  updateDialogVisible.value = true
}

// 确认更新
const confirmUpdate = async () => {
  try {
    const response = await compostApi.update(updateForm)
    if (response.code === 200) {
      ElMessage.success('更新成功')
      updateDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error) {
    console.error('更新失败:', error)
  }
}

// 完成
const handleFinish = (row) => {
  actionType.value = 'finish'
  actionForm.id = row.id
  actionForm.remark = ''
  actionDialogVisible.value = true
}

// 异常
const handleAbnormal = (row) => {
  actionType.value = 'abnormal'
  actionForm.id = row.id
  actionForm.remark = ''
  actionDialogVisible.value = true
}

// 确认操作
const confirmAction = async () => {
  try {
    let response
    if (actionType.value === 'finish') {
      response = await compostApi.finish(actionForm.id, actionForm.remark)
    } else {
      response = await compostApi.abnormal(actionForm.id, actionForm.remark)
    }
    
    if (response.code === 200) {
      ElMessage.success(actionType.value === 'finish' ? '完成成功' : '标记异常成功')
      actionDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    console.error('操作失败:', error)
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
  if (!time) return '-'
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    0: 'info',
    1: 'success',
    2: 'warning',
    3: 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '进行中',
    1: '已完成',
    2: '异常',
    3: '已取消'
  }
  return statusMap[status] || '未知'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.compost-list {
  width: 100%;
  padding: 16px 60px 0 60px;
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

.el-table__row {
  height: 56px !important;
}

.el-table__cell {
  text-align: left !important;
  font-size: 16px;
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

.action-btns {
  display: flex;
  gap: 16px;
  flex-wrap: nowrap;
  align-items: center;
}
</style> 