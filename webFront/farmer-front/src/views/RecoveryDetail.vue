<template>
  <div class="recovery-detail">
    <van-nav-bar title="回收详情" left-arrow @click-left="$router.back()" />
    <van-cell-group>
      <van-cell title="作物类型" :value="detail.cropType" />
      <van-cell title="重量(kg)" :value="detail.weight" />
      <van-cell title="回收地点" :value="detail.location" />
      <van-cell title="状态" :value="statusText(detail.status)" />
      <van-cell title="申请时间" :value="detail.createTime" />
      <van-cell title="更新时间" :value="detail.updateTime" />
      <van-cell title="备注" :value="detail.remark || '-'" />
      <van-cell v-if="imgList.length" title="图片">
        <template #value>
          <div style="display:flex;gap:8px;flex-wrap:wrap;">
            <van-image v-for="(img, idx) in imgList" :key="idx" :src="img" width="80" height="80" fit="cover" @click="preview(img)" style="border-radius:8px;cursor:pointer;" />
          </div>
        </template>
      </van-cell>
    </van-cell-group>
    <van-image-preview v-model:show="showPreview" :images="imgList" :start-position="previewIndex" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { recoveryApi } from '../api/recovery'

const route = useRoute()
const detail = ref({})
const showPreview = ref(false)
const previewIndex = ref(0)
const imgList = ref([])

const statusText = (status) => {
  switch (status) {
    case 0: return '待审核'
    case 1: return '已通过'
    case 2: return '已完成'
    case 3: return '已拒绝'
    default: return '未知'
  }
}

const preview = (img) => {
  previewIndex.value = imgList.value.indexOf(img)
  showPreview.value = true
}

const getFullUrl = (url) => {
  if (!url) return ''
  // 如果已经是http/https开头，直接返回，否则拼接服务器地址
  if (/^https?:\/\//.test(url)) return url
  // 这里假设前端和后端同域，直接用location.origin拼接
  return location.origin + url
}

function toBeijingTime(str) {
  if (!str) return ''
  // 兼容 "2025-06-30T10:09:32" 或 "2025-06-30 10:09:32"
  let date = new Date(str.replace(/-/g, '/').replace('T', ' '))
  // 转为北京时间（东八区）
  const offset = date.getTimezoneOffset() / 60
  if (offset !== -8) {
    date = new Date(date.getTime() + (8 + offset) * 60 * 60 * 1000)
  }
  // 格式化 yyyy-MM-dd HH:mm:ss
  const pad = n => n < 10 ? '0' + n : n
  return `${date.getFullYear()}-${pad(date.getMonth()+1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

onMounted(async () => {
  const res = await recoveryApi.detail(route.params.id)
  if (res.code === 200) {
    let images = res.data.images
    if (typeof images === 'string') {
      try {
        images = JSON.parse(images)
      } catch {
        images = images ? [images] : []
      }
    }
    // 拼接图片完整URL
    imgList.value = (images || []).map(getFullUrl)
    // 转换所有时间字段为北京时间
    detail.value = {
      ...res.data,
      images,
      createTime: toBeijingTime(res.data.createTime),
      updateTime: toBeijingTime(res.data.updateTime)
    }
  }
})
</script>

<style scoped>
.recovery-detail {
  min-height: 100vh;
  background: #f7f8fa;
}

.recovery-detail .van-cell-group {
  width: 95vw;
  max-width: 420px;
  margin: 32px auto 0 auto;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 4px 24px 0 rgba(0,0,0,0.08);
  padding-bottom: 16px;
}

@media (max-width: 480px) {
  .recovery-detail .van-cell-group {
    width: 100vw;
    max-width: 100vw;
    border-radius: 0;
    box-shadow: none;
    margin: 0;
    padding-bottom: 8px;
  }
}
</style> 