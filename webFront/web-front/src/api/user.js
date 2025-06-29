import request from '../utils/request'

export const userApi = {
  // 发送登录验证码
  sendLoginCode(data) {
    return request.post('/user/send-login-code', data)
  },

  // 用户注册
  register(data) {
    return request.post('/user/register', data)
  },

  // 用户登录（用户名密码）
  login(data) {
    return request.post('/user/login', data)
  },

  // 用户登录（验证码）
  loginByCode(data) {
    return request.post('/user/login-by-code', data)
  },

  // 获取当前用户信息
  getUserInfo() {
    return request.get('/user/info')
  },

  // 用户注销
  logout() {
    return request.post('/user/logout')
  }
} 