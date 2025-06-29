<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>慧农循环管理平台</h2>
        <p>农业废弃物资源化利用管理平台</p>
      </div>
      
      <el-tabs v-model="activeTab" class="login-tabs">
        <el-tab-pane label="密码登录" name="password">
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            class="login-form"
          >
            <el-form-item prop="username">
              <el-input
                v-model="passwordForm.username"
                placeholder="请输入用户名"
                prefix-icon="User"
                size="large"
              />
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input
                v-model="passwordForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                size="large"
                show-password
              />
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="login-btn"
                :loading="userStore.loading"
                @click="handlePasswordLogin"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="验证码登录" name="code">
          <el-form
            ref="codeFormRef"
            :model="codeForm"
            :rules="codeRules"
            class="login-form"
          >
            <el-form-item prop="phone">
              <el-input
                v-model="codeForm.phone"
                placeholder="请输入手机号"
                prefix-icon="Phone"
                size="large"
              />
            </el-form-item>
            
            <el-form-item prop="code">
              <div class="code-input">
                <el-input
                  v-model="codeForm.code"
                  placeholder="请输入验证码"
                  prefix-icon="Key"
                  size="large"
                />
                <el-button
                  type="primary"
                  :disabled="countdown > 0"
                  @click="handleSendCode"
                >
                  {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
                </el-button>
              </div>
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="login-btn"
                :loading="userStore.loading"
                @click="handleCodeLogin"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      
      <div class="login-footer">
        <el-link type="primary" @click="$router.push('/register')">
          还没有账号？立即注册
        </el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('password')
const countdown = ref(0)

// 密码登录表单
const passwordFormRef = ref()
const passwordForm = reactive({
  username: '',
  password: ''
})

const passwordRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

// 验证码登录表单
const codeFormRef = ref()
const codeForm = reactive({
  phone: '',
  code: ''
})

const codeRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位数字', trigger: 'blur' }
  ]
}

// 密码登录
const handlePasswordLogin = async () => {
  try {
    await passwordFormRef.value.validate()
    const result = await userStore.login(passwordForm)
    if (result.success) {
      // 只允许管理员和抽检员登录
      const userType = userStore.userInfo?.userType
      if (userType === 1 || userType === 2) {
        ElMessage.success('登录成功')
        router.push('/dashboard')
      } else {
        ElMessage.error('仅管理员和抽检员可登录系统')
        await userStore.logout()
      }
    } else {
      ElMessage.error(result.message)
    }
  } catch (error) {
    console.error('登录失败:', error)
  }
}

// 验证码登录
const handleCodeLogin = async () => {
  try {
    await codeFormRef.value.validate()
    const result = await userStore.loginByCode(codeForm)
    if (result.success) {
      // 只允许管理员和抽检员登录
      const userType = userStore.userInfo?.userType
      if (userType === 1 || userType === 2) {
        ElMessage.success('登录成功')
        router.push('/dashboard')
      } else {
        ElMessage.error('仅管理员和抽检员可登录系统')
        await userStore.logout()
      }
    } else {
      ElMessage.error(result.message)
    }
  } catch (error) {
    console.error('登录失败:', error)
  }
}

// 发送验证码
const handleSendCode = async () => {
  try {
    await codeFormRef.value.validateField('phone')
    const success = await userStore.sendLoginCode(codeForm.phone)
    if (success) {
      ElMessage.success('验证码发送成功')
      countdown.value = 60
      const timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    } else {
      ElMessage.error('验证码发送失败')
    }
  } catch (error) {
    console.error('发送验证码失败:', error)
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  width: 100vw;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
}

.login-box {
  width: 600px;
  padding: 56px 56px 40px 56px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 6px 32px rgba(0, 0, 0, 0.13);
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h2 {
  color: #333;
  margin-bottom: 8px;
  font-size: 32px;
}

.login-header p {
  color: #666;
  font-size: 16px;
}

.login-tabs {
  margin-bottom: 28px;
  width: 100%;
}

.login-form {
  margin-top: 28px;
  width: 100%;
}

.login-btn {
  width: 100%;
  font-size: 18px;
}

.code-input {
  display: flex;
  gap: 14px;
}

.code-input .el-input {
  flex: 1;
}

.code-input .el-button {
  width: 140px;
}

.login-footer {
  text-align: center;
  margin-top: 32px;
}
</style> 