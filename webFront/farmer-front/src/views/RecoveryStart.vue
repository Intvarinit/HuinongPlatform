<template>
  <div class="recovery-start">
    <van-nav-bar title="开始回收" left-arrow @click-left="$router.back()" />
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field v-model="form.cropType" label="作物类型" placeholder="请输入作物类型" required />
        <van-field v-model="form.weight" label="重量(kg)" placeholder="请输入重量" type="number" required />
        <van-field v-model="form.location" label="回收地点" placeholder="请输入回收地点" required />
        <van-field v-model="form.remark" label="备注" placeholder="选填" />
        <van-field label="上传图片">
          <template #input>
            <van-uploader v-model="fileList" :max-count="3" :after-read="onAfterRead" :before-delete="onBeforeDelete" />
          </template>
        </van-field>
      </van-cell-group>
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">提交回收</van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { showToast } from 'vant'
import { recoveryApi } from '../api/recovery'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const form = ref({ cropType: '', weight: '', location: '', remark: '', images: [] })
const fileList = ref([])
const router = useRouter()

// 处理图片上传
const onAfterRead = async (file) => {
  if (Array.isArray(file)) {
    for (const f of file) {
      await uploadFile(f)
    }
  } else {
    await uploadFile(file)
  }
}

const uploadFile = async (file) => {
  try {
    const formData = new FormData()
    formData.append('file', file.file)
    
    const res = await request.post('/common/upload/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    if (res.code === 200) {
      // 将返回的图片URL添加到表单中
      form.value.images.push(res.data)
      showToast('图片上传成功')
    } else {
      showToast(res.message || '图片上传失败')
    }
  } catch (error) {
    showToast('图片上传失败')
    console.error('上传失败:', error)
  }
}

const onBeforeDelete = (file, detail) => {
  // 删除时从表单中移除对应的图片URL
  const index = fileList.value.findIndex(f => f.file === file.file)
  if (index > -1) {
    form.value.images.splice(index, 1)
  }
  return true
}

const onSubmit = async () => {
  if (!form.value.cropType || !form.value.weight || !form.value.location) {
    showToast('请填写完整信息')
    return
  }
  
  const payload = { ...form.value }
  const res = await recoveryApi.apply(payload)
  if (res.code === 200) {
    showToast('提交成功')
    router.replace('/recovery')
  } else {
    showToast(res.message || '提交失败')
  }
}
</script>

<style scoped>
.recovery-start {
  min-height: 100vh;
  background: #f7f8fa;
}
</style> 