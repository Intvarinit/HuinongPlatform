<template>
  <div class="inspection-create">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>新增抽检记录</span>
        </div>
      </template>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="create-form"
      >
        <el-form-item label="堆肥批次ID" prop="compostId">
          <el-input
            v-model="form.compostId"
            placeholder="请输入堆肥批次ID"
            type="number"
          />
        </el-form-item>
        
        <el-form-item label="抽检时间" prop="inspectionTime">
          <el-date-picker
            v-model="form.inspectionTime"
            type="datetime"
            placeholder="请选择抽检时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="抽检结果" prop="result">
          <el-radio-group v-model="form.result">
            <el-radio :label="1">合格</el-radio>
            <el-radio :label="0">不合格</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="温度(°C)" prop="temperature">
              <el-input-number
                v-model="form.temperature"
                :min="0"
                :max="100"
                :precision="1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="8">
            <el-form-item label="湿度(%)" prop="humidity">
              <el-input-number
                v-model="form.humidity"
                :min="0"
                :max="100"
                :precision="1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="8">
            <el-form-item label="pH值" prop="phValue">
              <el-input-number
                v-model="form.phValue"
                :min="0"
                :max="14"
                :precision="1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
        
        <el-form-item label="图片上传">
          <el-upload
            ref="uploadRef"
            :action="uploadAction"
            :headers="uploadHeaders"
            :before-upload="beforeUpload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :on-remove="handleRemove"
            :file-list="fileList"
            list-type="picture-card"
            :limit="5"
            accept="image/*"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">
            支持jpg、png、gif格式，单个文件不超过5MB，最多上传5张
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleSubmit"
          >
            提交
          </el-button>
          <el-button size="large" @click="$router.back()">
            取消
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { inspectionApi } from '../../api/inspection'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const uploadRef = ref()
const loading = ref(false)
const fileList = ref([])

const form = reactive({
  compostId: '',
  inspectionTime: '',
  result: 1,
  temperature: null,
  humidity: null,
  phValue: null,
  remark: '',
  images: []
})

const rules = {
  compostId: [
    { required: true, message: '请输入堆肥批次ID', trigger: 'blur' },
    { type: 'number', message: '堆肥批次ID必须为数字', trigger: 'blur' }
  ],
  inspectionTime: [
    { required: true, message: '请选择抽检时间', trigger: 'change' }
  ],
  result: [
    { required: true, message: '请选择抽检结果', trigger: 'change' }
  ]
}

// 上传配置
const uploadAction = '/api/common/upload/image'
const uploadHeaders = {
  Authorization: `Bearer ${userStore.token}`
}

// 上传前验证
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

// 上传成功
const handleUploadSuccess = (response, file) => {
  if (response.code === 200) {
    form.images.push(response.data)
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '图片上传失败')
  }
}

// 上传失败
const handleUploadError = () => {
  ElMessage.error('图片上传失败')
}

// 移除图片
const handleRemove = (file) => {
  const index = form.images.indexOf(file.url)
  if (index > -1) {
    form.images.splice(index, 1)
  }
}

// 提交
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    loading.value = true
    
    const response = await inspectionApi.create(form)
    if (response.code === 200) {
      ElMessage.success('创建成功')
      router.push('/inspection')
    } else {
      ElMessage.error(response.message || '创建失败')
    }
  } catch (error) {
    console.error('创建失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.inspection-create {
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

.create-form {
  max-width: 800px;
  margin: 0 auto;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

:deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
  line-height: 100px;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 100px;
  height: 100px;
}
</style> 