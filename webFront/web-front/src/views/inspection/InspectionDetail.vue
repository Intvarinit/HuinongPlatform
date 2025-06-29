<template>
  <div class="inspection-detail">
    <el-card>
      <template #header>
        <span>抽检详情</span>
      </template>
      <div v-if="loading" style="text-align:center;padding:40px 0;">
        <el-icon><Loading /></el-icon>
      </div>
      <div v-else-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="ID">{{ detail.id }}</el-descriptions-item>
          <el-descriptions-item label="堆肥批次ID">{{ detail.compostId }}</el-descriptions-item>
          <el-descriptions-item label="抽检结果">
            <el-tag :type="detail.result === 1 ? 'success' : 'danger'">
              {{ detail.result === 1 ? '合格' : '不合格' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="抽检时间">{{ formatTime(detail.inspectionTime) }}</el-descriptions-item>
          <el-descriptions-item label="温度(°C)">{{ detail.temperature ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="湿度(%)">{{ detail.humidity ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="pH值">{{ detail.phValue ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ detail.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="imageList.length" class="images-section">
          <h3>抽检图片</h3>
          <div class="image-list">
            <div v-for="(img, idx) in imageList" :key="idx" class="image-item">
              <el-image :src="img" fit="cover" :preview-src-list="imageList" :initial-index="idx" />
            </div>
          </div>
        </div>
      </div>
      <div v-else style="text-align:center;padding:40px 0;">暂无数据</div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { inspectionApi } from '../../api/inspection'

const route = useRoute()
const loading = ref(false)
const detail = ref(null)

const imageList = ref([])

const loadDetail = async () => {
  try {
    loading.value = true
    const response = await inspectionApi.detail(route.params.id)
    if (response.code === 200) {
      detail.value = response.data
      // 处理图片
      if (response.data.images) {
        try {
          imageList.value = JSON.parse(response.data.images)
        } catch {
          imageList.value = response.data.images.split(',').filter(img => img.trim())
        }
      } else {
        imageList.value = []
      }
    } else {
      ElMessage.error(response.message || '加载详情失败')
    }
  } catch (error) {
    ElMessage.error('加载详情失败')
  } finally {
    loading.value = false
  }
}

const formatTime = (time) => {
  if (!time) return '-'
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.inspection-detail {
  width: 100%;
  padding: 16px 16px 0 16px;
  box-sizing: border-box;
}

.el-card {
  width: 100%;
  box-sizing: border-box;
}

.images-section {
  margin-top: 30px;
}
.images-section h3 {
  margin-bottom: 15px;
  color: #333;
  font-size: 16px;
}
.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}
.image-item {
  width: 150px;
  height: 150px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e6e6e6;
}
.el-image {
  width: 100%;
  height: 100%;
}
</style> 