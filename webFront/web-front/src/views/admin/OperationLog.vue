<template>
  <div class="operation-log">
    <el-card>
      <template #header>
        <span>操作日志</span>
      </template>
      <el-table :data="tableData" v-loading="loading" style="width: 100%" border>
        <el-table-column prop="username" label="操作人" width="120" />
        <el-table-column prop="operation" label="操作" />
        <el-table-column prop="method" label="方法" />
        <el-table-column prop="params" label="参数" />
        <el-table-column prop="ip" label="IP地址" width="120" />
        <el-table-column prop="createTime" label="操作时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button size="small" @click="showDetail(row)">详情</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="detailDialogVisible" title="日志详情" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="ID">{{ detailRow.id }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detailRow.username }}</el-descriptions-item>
        <el-descriptions-item label="操作">{{ detailRow.operation }}</el-descriptions-item>
        <el-descriptions-item label="方法">{{ detailRow.method }}</el-descriptions-item>
        <el-descriptions-item label="参数">{{ detailRow.params }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ detailRow.ip }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ formatTime(detailRow.createTime) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import axios from 'axios'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

const detailDialogVisible = ref(false)
const detailRow = ref({})

const loadData = async () => {
  try {
    loading.value = true
    const res = await axios.get('/api/log/page', {
      params: {
        page: pagination.current,
        size: pagination.size
      }
    })
    if (res.data.code === 200) {
      tableData.value = res.data.data.records || []
      pagination.total = res.data.data.total || 0
    } else {
      ElMessage.error(res.data.message || '获取日志失败')
    }
  } catch (e) {
    ElMessage.error('获取日志失败')
  } finally {
    loading.value = false
  }
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

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该日志吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await axios.delete(`/api/log/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.operation-log {
  width: 100%;
  padding: 16px 16px 0 16px;
  box-sizing: border-box;
}
.el-card {
  width: 100%;
  box-sizing: border-box;
}
.pagination {
  margin-top: 20px;
  text-align: right;
}
</style> 