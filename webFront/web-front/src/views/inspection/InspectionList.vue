<template>
  <div class="inspection-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>抽检管理</span>
          <el-button type="primary" @click="$router.push('/inspection/create')">
            <el-icon><Plus /></el-icon>
            新增抽检
          </el-button>
        </div>
      </template>
      
      <!-- 搜索条件 -->
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="堆肥批次ID">
          <el-input
            v-model.number="searchForm.compostId"
            placeholder="请输入堆肥批次ID"
            clearable
            type="number"
          />
        </el-form-item>
        
        <el-form-item label="抽检结果">
          <el-select
            v-model="searchForm.result"
            placeholder="请选择结果"
            clearable
            style="min-width: 120px"
            @change="handleSearch"
          >
            <el-option label="合格" :value="1" />
            <el-option label="不合格" :value="0" />
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
        <el-table-column prop="compostId" label="堆肥批次ID" />
        <el-table-column prop="result" label="抽检结果">
          <template #default="{ row }">
            <el-tag :type="row.result === 1 ? 'success' : 'danger'">
              {{ row.result === 1 ? '合格' : '不合格' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="inspectionTime" label="抽检时间">
          <template #default="{ row }">
            {{ formatTime(row.inspectionTime) }}
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
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <div class="action-btns">
              <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { inspectionApi } from '../../api/inspection'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = reactive({
  compostId: null,
  result: ''
})

// 加载数据
const loadData = async () => {
  try {
    loading.value = true
    
    const params = {
      page: pagination.current,
      size: pagination.size
    }
    
    // 只有当 compostId 不为空时才添加到参数中
    if (searchForm.compostId && searchForm.compostId > 0) {
      params.compostId = searchForm.compostId
    }
    // 修复 result=0 时无法传递的问题
    if (searchForm.result !== null && searchForm.result !== '' && searchForm.result !== undefined) {
      params.result = searchForm.result
    }
    
    const response = await inspectionApi.page(params)
    
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
    compostId: null,
    result: ''
  })
  pagination.current = 1
  loadData()
}

// 查看详情
const handleView = (row) => {
  router.push(`/inspection/detail/${row.id}`)
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

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.inspection-list {
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

.action-btns {
  display: flex;
  gap: 16px;
  flex-wrap: nowrap;
  align-items: center;
}
</style> 