import request from './request.js'

// 用户相关API
export const userApi = {
  // 微信登录
  login(data) {
    return request({
      url: '/user/login',
      method: 'POST',
      data
    })
  },
  
  // 获取用户信息
  getUserInfo() {
    return request({
      url: '/user/info',
      method: 'GET'
    })
  }
}

// 分类相关API
export const categoryApi = {
  // 获取支出分类
  getExpenseCategories() {
    return request({
      url: '/category/expense',
      method: 'GET'
    })
  },
  
  // 获取收入分类
  getIncomeCategories() {
    return request({
      url: '/category/income',
      method: 'GET'
    })
  }
}

// 记账相关API
export const recordApi = {
  // 添加记账记录
  addRecord(data) {
    return request({
      url: '/record/add',
      method: 'POST',
      data
    })
  },
  
  // 获取记账明细列表
  getRecordList(params) {
    return request({
      url: '/record/list',
      method: 'GET',
      data: params
    })
  },
  
  // 获取月度统计
  getStatistics(month) {
    return request({
      url: '/record/statistics',
      method: 'GET',
      data: { month }
    })
  }
}
