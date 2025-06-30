<template>
  <div class="login-bg">
    <div class="login-container">
      <div class="login-header">
        <div class="login-title">农户回收平台</div>
        <div class="login-subtitle">欢迎使用智能回收服务</div>
      </div>
      
      <!-- 登录方式切换 -->
      <div class="login-tabs">
        <div 
          class="tab-item" 
          :class="{ active: loginType === 'code' }"
          @click="switchLoginType('code')"
        >
          验证码登录
        </div>
        <div 
          class="tab-item" 
          :class="{ active: loginType === 'password' }"
          @click="switchLoginType('password')"
        >
          密码登录
        </div>
      </div>
      
      <van-form @submit="onSubmit" class="login-form">
        <van-cell-group inset class="form-group">
          <van-field 
            v-if="loginType === 'password'"
            v-model="username"
            label="账号"
            placeholder="请输入账号"
            required
            class="form-field"
          />
          <van-field 
            v-else
            v-model="phone"
            label="手机号"
            placeholder="请输入手机号"
            type="tel"
            required
            class="form-field"
          />
          
          <!-- 验证码登录 -->
          <van-field 
            v-if="loginType === 'code'"
            v-model="code" 
            label="验证码" 
            placeholder="请输入验证码" 
            required
            class="form-field"
          >
            <template #button>
              <van-button 
                size="small" 
                type="primary" 
                @click="sendCode" 
                :disabled="countdown > 0"
                class="code-btn"
              >
                {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
              </van-button>
            </template>
          </van-field>
          
          <!-- 密码登录 -->
          <van-field 
            v-if="loginType === 'password'"
            v-model="password" 
            label="密码" 
            placeholder="请输入密码" 
            type="password" 
            required
            class="form-field"
          />
        </van-cell-group>
        
        <div class="button-group">
          <van-button 
            round 
            block 
            type="primary" 
            native-type="submit" 
            class="login-btn"
          >
            登录
          </van-button>
          <van-button 
            round 
            block 
            type="default" 
            @click="router.push('/register')" 
            class="register-btn"
          >
            去注册
          </van-button>
        </div>
      </van-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { showToast } from 'vant'
import { userApi } from '../api/user'
import { useRouter } from 'vue-router'

const phone = ref('')
const code = ref('')
const password = ref('')
const username = ref('')
const loginType = ref('code') // 'code' 或 'password'
const countdown = ref(0)
const router = useRouter()
let timer = null

// 切换登录方式
const switchLoginType = (type) => {
  loginType.value = type
  code.value = ''
  password.value = ''
  username.value = ''
  phone.value = ''
  if (timer) {
    clearInterval(timer)
    countdown.value = 0
  }
}

const sendCode = async () => {
  if (!/^1\d{10}$/.test(phone.value)) {
    showToast('请输入正确的手机号')
    return
  }
  await userApi.sendLoginCode({ phone: phone.value })
  showToast('验证码已发送')
  countdown.value = 60
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) clearInterval(timer)
  }, 1000)
}

const onSubmit = async () => {
  if (loginType.value === 'code') {
    if (!/^1\d{10}$/.test(phone.value)) {
      showToast('请输入正确的手机号')
      return
    }
    if (!code.value) {
      showToast('请输入验证码')
      return
    }
    const res = await userApi.loginByCode({ phone: phone.value, code: code.value })
    if (res.code === 200) {
      showToast('登录成功')
      localStorage.setItem('token', res.data)
      router.replace('/recovery')
    } else {
      showToast(res.message || '登录失败')
    }
  } else {
    if (!username.value) {
      showToast('请输入账号')
      return
    }
    if (!password.value) {
      showToast('请输入密码')
      return
    }
    const res = await userApi.login({ username: username.value, password: password.value })
    if (res.code === 200) {
      showToast('登录成功')
      localStorage.setItem('token', res.data)
      router.replace('/recovery')
    } else {
      showToast(res.message || '登录失败')
    }
  }
}
</script>

<style scoped>
.login-bg {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
  box-sizing: border-box;
}

.login-container {
  width: 100%;
  max-width: 400px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 32px 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.login-header {
  text-align: center;
  margin-bottom: 24px;
}

.login-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  letter-spacing: 0.5px;
}

.login-subtitle {
  font-size: 14px;
  color: #666;
  letter-spacing: 0.3px;
}

/* 登录方式切换样式 */
.login-tabs {
  display: flex;
  background: #f5f5f5;
  border-radius: 12px;
  padding: 4px;
  margin-bottom: 24px;
  width: 100%;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 12px 16px;
  font-size: 16px;
  color: #666;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
}

.tab-item.active {
  background: #667eea;
  color: #fff;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.login-form {
  width: 100%;
}

.form-group {
  margin-bottom: 24px;
  border-radius: 12px;
  overflow: hidden;
}

.form-field {
  font-size: 16px;
}

.form-field :deep(.van-field__label) {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.form-field :deep(.van-field__control) {
  font-size: 16px;
}

.code-btn {
  font-size: 14px;
  height: 32px;
  padding: 0 12px;
  border-radius: 16px;
}

.button-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.login-btn {
  height: 48px;
  font-size: 18px;
  font-weight: 500;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.register-btn {
  height: 48px;
  font-size: 18px;
  font-weight: 500;
  color: #667eea;
  border: 2px solid #667eea;
  background: transparent;
}

/* 手机端适配 */
@media (max-width: 480px) {
  .login-bg {
    padding: 12px;
  }
  
  .login-container {
    padding: 24px 20px;
    border-radius: 16px;
  }
  
  .login-title {
    font-size: 22px;
  }
  
  .login-subtitle {
    font-size: 13px;
  }
  
  .login-tabs {
    margin-bottom: 20px;
  }
  
  .tab-item {
    padding: 10px 12px;
    font-size: 15px;
  }
  
  .form-field :deep(.van-field__label) {
    font-size: 15px;
  }
  
  .form-field :deep(.van-field__control) {
    font-size: 15px;
  }
  
  .login-btn,
  .register-btn {
    height: 44px;
    font-size: 17px;
  }
  
  .code-btn {
    font-size: 13px;
    height: 30px;
    padding: 0 10px;
  }
}

/* 超小屏幕适配 */
@media (max-width: 360px) {
  .login-container {
    padding: 20px 16px;
  }
  
  .login-title {
    font-size: 20px;
  }
  
  .login-subtitle {
    font-size: 12px;
  }
  
  .tab-item {
    padding: 8px 10px;
    font-size: 14px;
  }
  
  .form-field :deep(.van-field__label) {
    font-size: 14px;
  }
  
  .form-field :deep(.van-field__control) {
    font-size: 14px;
  }
  
  .login-btn,
  .register-btn {
    height: 42px;
    font-size: 16px;
  }
}

/* 横屏适配 */
@media (orientation: landscape) and (max-height: 500px) {
  .login-bg {
    padding: 8px;
  }
  
  .login-container {
    padding: 20px 24px;
  }
  
  .login-header {
    margin-bottom: 16px;
  }
  
  .login-title {
    font-size: 20px;
    margin-bottom: 4px;
  }
  
  .login-subtitle {
    font-size: 12px;
  }
  
  .login-tabs {
    margin-bottom: 16px;
  }
  
  .tab-item {
    padding: 8px 12px;
    font-size: 14px;
  }
  
  .form-group {
    margin-bottom: 16px;
  }
  
  .button-group {
    gap: 8px;
  }
  
  .login-btn,
  .register-btn {
    height: 40px;
    font-size: 16px;
  }
}
</style> 