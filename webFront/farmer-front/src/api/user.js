import request from '../utils/request'

export const userApi = {
  // 发送登录验证码
  sendLoginCode(data) {
    return request.post('/user/send-login-code', data)
  },
  // 验证码登录
  loginByCode(data) {
    return request.post('/user/login-by-code', data)
  },
  // 密码登录
  login(data) {
    return request.post('/user/login', data)
  },
  // 注册
  register(data) {
    return request.post('/user/register', data)
  }
} 