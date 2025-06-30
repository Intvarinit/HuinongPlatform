<template>
  <div class="register-page">
    <van-nav-bar title="注册" left-arrow @click-left="$router.back()" />
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field v-model="form.phone" label="手机号" placeholder="请输入手机号" required />
        <van-field v-model="form.code" label="验证码" placeholder="请输入验证码" required>
          <template #button>
            <van-button size="small" type="primary" @click="sendCode" :disabled="countdown > 0">
              {{ countdown > 0 ? `${countdown}s后重发` : '获取验证码' }}
            </van-button>
          </template>
        </van-field>
        <van-field v-model="form.password" label="密码" type="password" placeholder="请输入密码" required />
        <van-field v-model="form.realName" label="真实姓名" placeholder="请输入真实姓名" required />
      </van-cell-group>
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">注册</van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { showToast } from 'vant'
import { userApi } from '../api/user'
import { useRouter } from 'vue-router'

const form = ref({ phone: '', code: '', password: '', realName: '' })
const countdown = ref(0)
const router = useRouter()
let timer = null

const sendCode = async () => {
  if (!/^1\d{10}$/.test(form.value.phone)) {
    showToast('请输入正确的手机号')
    return
  }
  await userApi.sendLoginCode({ phone: form.value.phone })
  showToast('验证码已发送')
  countdown.value = 60
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) clearInterval(timer)
  }, 1000)
}

const onSubmit = async () => {
  if (!/^1\d{10}$/.test(form.value.phone) || !form.value.code || !form.value.password || !form.value.realName) {
    showToast('请填写完整信息')
    return
  }
  const res = await userApi.register(form.value)
  if (res.code === 200) {
    showToast('注册成功')
    router.replace('/login')
  } else {
    showToast(res.message || '注册失败')
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: #f7f8fa;
}
</style> 