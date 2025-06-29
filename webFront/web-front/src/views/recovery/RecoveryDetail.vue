<template>
  <div class="recovery-detail">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>回收详情</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>
      
      <div v-loading="loading" class="detail-content">
        <el-descriptions
          v-if="detail"
          :column="2"
          border
          class="detail-descriptions"
        >
          <el-descriptions-item label="申请ID">
            {{ detail.id }}
          </el-descriptions-item>
          
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(detail.status)">
              {{ getStatusText(detail.status) }}
            </el-tag>
          </el-descriptions-item>
          
          <el-descriptions-item label="作物类型">
            {{ detail.cropType }}
          </el-descriptions-item>
          
          <el-descriptions-item label="重量">
            {{ detail.weight }} kg
          </el-descriptions-item>
          
          <el-descriptions-item label="回收地点">
            {{ detail.location }}
          </el-descriptions-item>
          
          <el-descriptions-item label="预约时间">
            {{ formatTime(detail.appointmentTime) }}
          </el-descriptions-item>
          
          <el-descriptions-item label="申请时间">
            {{ formatTime(detail.createTime) }}
          </el-descriptions-item>
          
          <el-descriptions-item label="更新时间">
            {{ formatTime(detail.updateTime) }}
          </el-descriptions-item>
          
          <el-descriptions-item label="备注" :span="2">
            {{ detail.remark || '无' }}
          </el-descriptions-item>
        </el-descriptions>
        
        <!-- 图片展示 -->
        <div v-if="detail?.images" class="images-section">
          <h3>申请图片</h3>
          <div class="image-list">
            <div
              v-for="(image, index) in imageList"
              :key="index"
              class="image-item"
            >
              <el-image
                :src="image"
                fit="cover"
                :preview-src-list="imageList"
                :initial-index="index"
                class="detail-image"
              />
            </div>
          </div>
        </div>
        
        <!-- 审核信息 -->
        <div v-if="detail?.operatorId" class="audit-section">
          <h3>审核信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="审核人">
              {{ detail.operatorName || '未知' }}
            </el-descriptions-item>
            
            <el-descriptions-item label="审核时间">
              {{ formatTime(detail.updateTime) }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { recoveryApi } from '../../api/recovery'

const route = useRoute()
const loading = ref(false)
const detail = ref(null)

// 图片列表
const imageList = computed(() => {
  if (!detail.value?.images) return []
  try {
    return JSON.parse(detail.value.images)
  } catch {
    return detail.value.images.split(',').filter(img => img.trim())
  }
})

// 加载详情
const loadDetail = async () => {
  try {
    loading.value = true
    const response = await recoveryApi.detail(route.params.id)
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

// 格式化时间
const formatTime = (time) => {
  if (!time) return '未知'
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
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

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.recovery-detail {
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
  max-width: 1000px;
  margin: 0 auto;
}

.detail-descriptions {
  margin-bottom: 30px;
}

.images-section {
  margin-bottom: 30px;
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

.detail-image {
  width: 100%;
  height: 100%;
}

.audit-section {
  margin-bottom: 30px;
}

.audit-section h3 {
  margin-bottom: 15px;
  color: #333;
  font-size: 16px;
}
</style> 