<template>
  <div class="compost-create">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>创建堆肥批次</span>
        </div>
      </template>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="create-form"
      >
        <el-form-item label="回收记录ID" prop="recoveryId">
          <el-input
            v-model="form.recoveryId"
            placeholder="请输入回收记录ID"
            type="number"
          />
        </el-form-item>
        
        <el-form-item label="批次号" prop="batchNo">
          <el-input
            v-model="form.batchNo"
            placeholder="请输入批次号"
          />
        </el-form-item>
        
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="请选择开始时间"
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
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleSubmit"
          >
            创建批次
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
import { compostApi } from '../../api/compost'

const router = useRouter()

const formRef = ref()
const loading = ref(false)

const form = reactive({
  recoveryId: '',
  batchNo: '',
  startTime: '',
  remark: ''
})

const rules = {
  recoveryId: [
    { required: true, message: '请输入回收记录ID', trigger: 'blur' },
    { type: 'number', message: '回收记录ID必须为数字', trigger: 'blur' }
  ],
  batchNo: [
    { required: true, message: '请输入批次号', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ]
}

// 提交
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    loading.value = true
    
    const response = await compostApi.create(form)
    if (response.code === 200) {
      ElMessage.success('创建成功')
      router.push('/compost')
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
.compost-create {
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
  max-width: 600px;
  margin: 0 auto;
}
</style> 