<template>
  <div class="profile">
    <el-row :gutter="20">
      <!-- 个人信息 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>个人信息</span>
          </template>
          
          <div v-loading="loading" class="profile-info">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="用户名">
                {{ userInfo?.username }}
              </el-descriptions-item>
              
              <el-descriptions-item label="真实姓名">
                {{ userInfo?.realName }}
              </el-descriptions-item>
              
              <el-descriptions-item label="手机号">
                {{ userInfo?.phone }}
              </el-descriptions-item>
              
              <el-descriptions-item label="地址">
                {{ userInfo?.address }}
              </el-descriptions-item>
              
              <el-descriptions-item label="用户类型">
                {{ getUserTypeText(userInfo?.userType) }}
              </el-descriptions-item>
              
              <el-descriptions-item label="状态">
                <el-tag :type="userInfo?.status === 1 ? 'success' : 'danger'">
                  {{ userInfo?.status === 1 ? '正常' : '禁用' }}
                </el-tag>
              </el-descriptions-item>
              
              <el-descriptions-item label="注册时间">
                {{ formatTime(userInfo?.createTime) }}
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-col>
      
      <!-- 修改密码 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>修改密码</span>
          </template>
          
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="100px"
            class="password-form"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                placeholder="请输入原密码"
                show-password
              />
            </el-form-item>
            
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码"
                show-password
              />
            </el-form-item>
            
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                show-password
              />
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                :loading="passwordLoading"
                @click="handleChangePassword"
              >
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()

const loading = ref(false)
const passwordLoading = ref(false)
const userInfo = ref(null)

const passwordFormRef = ref()
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码确认验证
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    loading.value = true
    await userStore.getUserInfo()
    userInfo.value = userStore.userInfo
  } catch (error) {
    console.error('加载用户信息失败:', error)
  } finally {
    loading.value = false
  }
}

// 修改密码
const handleChangePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    
    passwordLoading.value = true
    
    // 这里需要后端提供修改密码的API
    // const response = await userApi.changePassword(passwordForm)
    // if (response.code === 200) {
    //   ElMessage.success('密码修改成功')
    //   passwordFormRef.value.resetFields()
    // } else {
    //   ElMessage.error(response.message || '密码修改失败')
    // }
    
    ElMessage.warning('修改密码功能需要后端API支持')
  } catch (error) {
    console.error('修改密码失败:', error)
  } finally {
    passwordLoading.value = false
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

// 获取用户类型文本
const getUserTypeText = (userType) => {
  const typeMap = {
    0: '农户',
    1: '管理员',
    2: '质检员'
  }
  return typeMap[userType] || '未知'
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile {
  width: 100%;
  padding: 16px 16px 0 16px;
  box-sizing: border-box;
}

.el-card {
  width: 100%;
  box-sizing: border-box;
}

.profile-info {
  margin-bottom: 20px;
}

.password-form {
  max-width: 400px;
}
</style> 