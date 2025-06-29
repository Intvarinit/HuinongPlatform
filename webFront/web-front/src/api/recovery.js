import request from '../utils/request'

export const recoveryApi = {
  // 提交回收申请
  apply(data) {
    return request.post('/recovery/apply', data)
  },

  // 审核回收申请
  audit(data) {
    return request.post('/recovery/audit', data)
  },

  // 分页查询我的回收记录
  myRecords(params) {
    return request.get('/recovery/my', { params })
  },

  // 分页查询所有回收记录
  allRecords(params) {
    return request.get('/recovery/all', { params })
  },

  // 查询回收记录详情
  detail(id) {
    return request.get(`/recovery/detail/${id}`)
  },

  // 回收量统计
  statistics(params) {
    return request.get('/recovery/statistics', { params })
  },

  // 删除回收记录
  delete(id) {
    return request.delete(`/recovery/delete/${id}`)
  }
} 