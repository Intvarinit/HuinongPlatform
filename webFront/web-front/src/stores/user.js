import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '../api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  const loading = ref(false)

  const isLoggedIn = computed(() => !!token.value)

  // 登录
  const login = async (loginData) => {
    try {
      loading.value = true
      const response = await userApi.login(loginData)
      if (response.code === 200) {
        token.value = response.data
        localStorage.setItem('token', response.data)
        await getUserInfo()
        return { success: true }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      return { success: false, message: error.message || '登录失败' }
    } finally {
      loading.value = false
    }
  }

  // 验证码登录
  const loginByCode = async (loginData) => {
    try {
      loading.value = true
      const response = await userApi.loginByCode(loginData)
      if (response.code === 200) {
        token.value = response.data
        localStorage.setItem('token', response.data)
        await getUserInfo()
        return { success: true }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      return { success: false, message: error.message || '登录失败' }
    } finally {
      loading.value = false
    }
  }

  // 注册
  const register = async (registerData) => {
    try {
      loading.value = true
      const response = await userApi.register(registerData)
      if (response.code === 200) {
        return { success: true }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      return { success: false, message: error.message || '注册失败' }
    } finally {
      loading.value = false
    }
  }

  // 获取用户信息
  const getUserInfo = async () => {
    try {
      const response = await userApi.getUserInfo()
      if (response.code === 200) {
        userInfo.value = response.data
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }

  // 发送验证码
  const sendLoginCode = async (phone) => {
    try {
      const response = await userApi.sendLoginCode({ phone })
      return response.code === 200
    } catch (error) {
      return false
    }
  }

  // 注销
  const logout = async () => {
    try {
      await userApi.logout()
    } catch (error) {
      console.error('注销失败:', error)
    } finally {
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
    }
  }

  return {
    token,
    userInfo,
    loading,
    isLoggedIn,
    login,
    loginByCode,
    register,
    getUserInfo,
    sendLoginCode,
    logout
  }
}) 