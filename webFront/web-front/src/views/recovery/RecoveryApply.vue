<template>
  <div class="recovery-apply">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>申请回收</span>
        </div>
      </template>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="apply-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="作物类型" prop="cropType">
              <el-select
                v-model="form.cropType"
                placeholder="请选择作物类型"
                style="width: 100%"
              >
                <el-option label="水稻" value="水稻" />
                <el-option label="小麦" value="小麦" />
                <el-option label="玉米" value="玉米" />
                <el-option label="大豆" value="大豆" />
                <el-option label="蔬菜" value="蔬菜" />
                <el-option label="水果" value="水果" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="重量(kg)" prop="weight">
              <el-input-number
                v-model="form.weight"
                :min="0"
                :precision="2"
                style="width: 100%"
                placeholder="请输入重量"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="回收地点" prop="location">
          <el-input
            v-model="form.location"
            placeholder="请输入回收地点"
          />
        </el-form-item>
        
        <el-form-item label="预约时间" prop="appointmentTime">
          <el-date-picker
            v-model="form.appointmentTime"
            type="datetime"
            placeholder="请选择预约时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        
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
            提交申请
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
import { recoveryApi } from '../../api/recovery'
import { commonApi } from '../../api/common'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const uploadRef = ref()
const loading = ref(false)
const fileList = ref([])

const form = reactive({
  cropType: '',
  weight: null,
  location: '',
  appointmentTime: '',
  remark: '',
  images: []
})

const rules = {
  cropType: [
    { required: true, message: '请选择作物类型', trigger: 'change' }
  ],
  weight: [
    { required: true, message: '请输入重量', trigger: 'blur' },
    { type: 'number', min: 0, message: '重量必须大于0', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入回收地点', trigger: 'blur' }
  ],
  appointmentTime: [
    { required: true, message: '请选择预约时间', trigger: 'change' }
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

// 提交申请
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    if (form.images.length === 0) {
      ElMessage.warning('请至少上传一张图片')
      return
    }
    
    loading.value = true
    
    const response = await recoveryApi.apply(form)
    if (response.code === 200) {
      ElMessage.success('申请提交成功')
      router.push('/recovery')
    } else {
      ElMessage.error(response.message || '申请提交失败')
    }
  } catch (error) {
    console.error('提交申请失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.recovery-apply {
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

.apply-form {
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