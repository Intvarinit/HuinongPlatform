import request from '../utils/request'

export const recoveryApi = {
  // 提交回收申请
  apply(data) {
    return request.post('/recovery/apply', data)
  },

  // 查询我的回收记录
  myRecords(params) {
    return request.get('/recovery/my', { params })
  },

  // 查询回收记录详情
  detail(id) {
    return request.get(`/recovery/detail/${id}`)
  },

  // 查询已完成回收记录
  finished(params) {
    return request.get('/recovery/finished', { params })
  },

  // 通用分页查询（适配list页面）
  list(params) {
    return request.get('/recovery/my', { params })
  }
} 