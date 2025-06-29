<template>
  <div class="compost-detail">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>堆肥详情</span>
          <div>
            <el-button @click="$router.back()">返回</el-button>
            <el-button
              v-if="detail?.status === 0"
              type="primary"
              @click="showProgressDialog = true"
            >
              添加进度日志
            </el-button>
          </div>
        </div>
      </template>
      
      <div v-loading="loading" class="detail-content">
        <!-- 基本信息 -->
        <el-descriptions
          v-if="detail"
          :column="2"
          border
          class="detail-descriptions"
        >
          <el-descriptions-item label="批次ID">
            {{ detail.id }}
          </el-descriptions-item>
          
          <el-descriptions-item label="回收记录ID">
            {{ detail.recoveryId }}
          </el-descriptions-item>
          
          <el-descriptions-item label="批次号">
            {{ detail.batchNo }}
          </el-descriptions-item>
          
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(detail.status)">
              {{ getStatusText(detail.status) }}
            </el-tag>
          </el-descriptions-item>
          
          <el-descriptions-item label="开始时间">
            {{ formatTime(detail.startTime) }}
          </el-descriptions-item>
          
          <el-descriptions-item label="结束时间">
            {{ formatTime(detail.endTime) || '-' }}
          </el-descriptions-item>
          
          <el-descriptions-item label="当前温度">
            {{ detail.temperature || '-' }} °C
          </el-descriptions-item>
          
          <el-descriptions-item label="当前湿度">
            {{ detail.humidity || '-' }} %
          </el-descriptions-item>
          
          <el-descriptions-item label="当前pH值">
            {{ detail.phValue || '-' }}
          </el-descriptions-item>
          
          <el-descriptions-item label="创建时间">
            {{ formatTime(detail.createTime) }}
          </el-descriptions-item>
          
          <el-descriptions-item label="备注" :span="2">
            {{ detail.remark || '无' }}
          </el-descriptions-item>
        </el-descriptions>
        
        <!-- 进度日志 -->
        <div class="progress-section">
          <h3>进度日志</h3>
          <el-table
            v-loading="progressLoading"
            :data="progressList"
            style="width: 100%"
            border
          >
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="time" label="记录时间" width="180">
              <template #default="{ row }">
                {{ formatTime(row.time) }}
              </template>
            </el-table-column>
            <el-table-column prop="temperature" label="温度(°C)" width="100">
              <template #default="{ row }">
                {{ row.temperature || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="humidity" label="湿度(%)" width="100">
              <template #default="{ row }">
                {{ row.humidity || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="phValue" label="pH值" width="100">
              <template #default="{ row }">
                {{ row.phValue || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" />
            <el-table-column prop="createTime" label="创建时间" width="180">
              <template #default="{ row }">
                {{ formatTime(row.createTime) }}
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 进度日志分页 -->
          <div class="pagination">
            <el-pagination
              v-model:current-page="progressPagination.current"
              v-model:page-size="progressPagination.size"
              :page-sizes="[10, 20, 50]"
              :total="progressPagination.total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleProgressSizeChange"
              @current-change="handleProgressCurrentChange"
            />
          </div>
        </div>
      </div>
    </el-card>
    
    <!-- 添加进度日志对话框 -->
    <el-dialog
      v-model="showProgressDialog"
      title="添加进度日志"
      width="600px"
    >
      <el-form :model="progressForm" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="温度(°C)">
              <el-input-number
                v-model="progressForm.temperature"
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
                v-model="progressForm.humidity"
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
            v-model="progressForm.phValue"
            :min="0"
            :max="14"
            :precision="1"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input
            v-model="progressForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showProgressDialog = false">取消</el-button>
          <el-button type="primary" @click="confirmAddProgress">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { compostApi } from '../../api/compost'

const route = useRoute()

const loading = ref(false)
const progressLoading = ref(false)
const detail = ref(null)
const progressList = ref([])
const progressPagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 进度日志对话框
const showProgressDialog = ref(false)
const progressForm = reactive({
  compostId: null,
  temperature: null,
  humidity: null,
  phValue: null,
  remark: ''
})

// 加载详情
const loadDetail = async () => {
  try {
    loading.value = true
    const response = await compostApi.detail(route.params.id)
    if (response.code === 200) {
      detail.value = response.data
    } else {
      ElMessage.error(response.message || '加载详情失败')
    }
  } catch (error) {
    console.error('加载详情失败:', error)
    ElMessage.error('加载详情失败')
  } finally {
    loading.value = false
  }
}

// 加载进度日志
const loadProgressLog = async () => {
  try {
    progressLoading.value = true
    const compostId = parseInt(route.params.id)
    if (!compostId || compostId <= 0) {
      ElMessage.error('无效的堆肥批次ID')
      return
    }
    
    const response = await compostApi.listProgressLog({
      compostId: compostId,
      page: progressPagination.current,
      size: progressPagination.size
    })
    
    if (response.code === 200) {
      progressList.value = response.data.records || []
      progressPagination.total = response.data.total || 0
    }
  } catch (error) {
    console.error('加载进度日志失败:', error)
  } finally {
    progressLoading.value = false
  }
}

// 添加进度日志
const confirmAddProgress = async () => {
  try {
    progressForm.compostId = parseInt(route.params.id)
    
    const response = await compostApi.addProgressLog(progressForm)
    if (response.code === 200) {
      ElMessage.success('添加进度日志成功')
      showProgressDialog.value = false
      
      // 重置表单
      Object.assign(progressForm, {
        compostId: null,
        temperature: null,
        humidity: null,
        phValue: null,
        remark: ''
      })
      
      // 重新加载数据
      loadDetail()
      loadProgressLog()
    } else {
      ElMessage.error(response.message || '添加进度日志失败')
    }
  } catch (error) {
    console.error('添加进度日志失败:', error)
  }
}

// 进度日志分页
const handleProgressSizeChange = (size) => {
  progressPagination.size = size
  progressPagination.current = 1
  loadProgressLog()
}

const handleProgressCurrentChange = (current) => {
  progressPagination.current = current
  loadProgressLog()
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
  loadDetail()
  loadProgressLog()
})
</script>

<style scoped>
.compost-detail {
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

.detail-content {
  max-width: 1200px;
  margin: 0 auto;
}

.detail-descriptions {
  margin-bottom: 30px;
}

.progress-section {
  margin-bottom: 30px;
}

.progress-section h3 {
  margin-bottom: 15px;
  color: #333;
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
</style> 