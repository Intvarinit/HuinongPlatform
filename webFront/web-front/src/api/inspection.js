import request from '../utils/request'

export const inspectionApi = {
  // 新增抽检记录
  create(data) {
    return request.post('/inspection/create', data)
  },

  // 分页查询抽检记录
  page(params) {
    return request.get('/inspection/page', { params })
  },

  // 查询抽检详情
  detail(id) {
    return request.get(`/inspection/detail/${id}`)
  },

  // 导出抽检记录Excel
  export(compostId) {
    return request.get('/inspection/export', { 
      params: { compostId },
      responseType: 'blob'
    })
  }
} 