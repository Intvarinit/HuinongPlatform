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
  },

  // 分页查询用户列表
  getUserList(params) {
    return request.get('/user/list', { params })
  },

  // 修改用户身份
  updateUserType(id, userType) {
    return request.put(`/user/${id}/type`, null, { params: { userType } })
  },

  // 修改用户状态
  updateUserStatus(id, status) {
    return request.put(`/user/${id}/status`, null, { params: { status } })
  }
} 