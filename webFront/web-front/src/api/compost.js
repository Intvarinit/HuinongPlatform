import request from '../utils/request'

export const compostApi = {
  // 创建堆肥批次
  create(data) {
    return request.post('/compost/create', data)
  },

  // 更新堆肥批次
  update(data) {
    return request.post('/compost/update', data)
  },

  // 完成堆肥批次
  finish(id, remark) {
    return request.post(`/compost/finish/${id}`, null, { params: { remark } })
  },

  // 异常标记堆肥批次
  abnormal(id, remark) {
    return request.post(`/compost/abnormal/${id}`, null, { params: { remark } })
  },

  // 分页查询堆肥批次
  page(params) {
    return request.get('/compost/page', { params })
  },

  // 查询堆肥批次详情
  detail(id) {
    return request.get(`/compost/detail/${id}`)
  },

  // 导出指定回收记录下堆肥批次
  export(recoveryId) {
    return request.get('/compost/export', { 
      params: { recoveryId },
      responseType: 'blob'
    })
  },

  // 导出全部堆肥批次
  exportAll() {
    return request.get('/compost/export/all', { responseType: 'blob' })
  },

  // 合格率统计
  passRate(params) {
    return request.get('/compost/pass-rate', { params })
  },

  // 异常率统计
  exceptionRate(params) {
    return request.get('/compost/exception-rate', { params })
  },

  // 添加进度日志
  addProgressLog(data) {
    return request.post('/compost/progress-log/add', data)
  },

  // 分页查询进度日志
  listProgressLog(params) {
    return request.get('/compost/progress-log/list', { params })
  }
} 