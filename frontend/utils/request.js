import config from '../config/index.js'

const BASE_URL = config.baseUrl

// 请求拦截器
const request = (options) => {
  return new Promise((resolve, reject) => {
    // 获取token
    const token = uni.getStorageSync('token')
    
    // 设置请求头
    const header = {
      'Content-Type': 'application/json'
    }
    if (token) {
      header['Authorization'] = `Bearer ${token}`
    }
    
    // 发起请求
    console.log('发起请求:', {
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data
    })
    
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: header,
      success: (res) => {
        console.log('请求响应:', {
          url: BASE_URL + options.url,
          statusCode: res.statusCode,
          data: res.data
        })
        
        if (res.statusCode === 200) {
          if (res.data.code === 200) {
            resolve(res.data)
          } else if (res.data.code === 401) {
            // token过期，清除本地存储，跳转到登录页
            console.warn('Token过期，跳转到登录页')
            uni.removeStorageSync('token')
            uni.removeStorageSync('userInfo')
            uni.reLaunch({
              url: '/pages/login/login'
            })
            reject(new Error(res.data.message || '登录已过期，请重新登录'))
          } else {
            // 后端返回的错误
            const errorMsg = res.data.message || '请求失败'
            console.error('后端返回错误:', res.data)
            reject(new Error(errorMsg))
          }
        } else {
          // HTTP状态码错误
          const errorMsg = `请求失败 (${res.statusCode})`
          console.error('HTTP状态码错误:', res.statusCode, res)
          reject(new Error(errorMsg))
        }
      },
      fail: (err) => {
        console.error('请求失败:', {
          url: BASE_URL + options.url,
          error: err
        })
        
        // 根据错误类型提供更详细的错误信息
        let errorMsg = '网络连接失败'
        if (err.errMsg) {
          if (err.errMsg.includes('timeout')) {
            errorMsg = '请求超时，请检查网络连接'
          } else if (err.errMsg.includes('fail')) {
            errorMsg = '无法连接到服务器，请检查后端服务是否启动'
          } else {
            errorMsg = err.errMsg
          }
        }
        
        reject(new Error(errorMsg))
      }
    })
  })
}

export default request
