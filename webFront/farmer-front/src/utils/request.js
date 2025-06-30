import axios from 'axios'
import { showToast } from 'vant'
import router from '../router'

const service = axios.create({
  baseURL: '/api', // 根据实际后端接口前缀调整
  timeout: 10000
})

// 请求拦截器，自动加token
service.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token
  }
  return config
})

// 响应拦截器，统一错误提示和未登录跳转
service.interceptors.response.use(
  res => {
    // 业务层未登录
    if (res.data && res.data.code === 401) {
      showToast('请先登录')
      localStorage.removeItem('token')
      router.replace('/login')
      return Promise.reject(new Error('未登录'))
    }
    return res.data // 只返回data
  },
  err => {
    // HTTP层未登录
    if (err.response && err.response.status === 401) {
      showToast('请先登录')
      localStorage.removeItem('token')
      router.replace('/login')
    } else {
      showToast(err.response?.data?.message || '请求失败')
    }
    return Promise.reject(err)
  }
)

export default service 