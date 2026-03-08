<template>
  <view class="login-container">
    <view class="logo">
      <text class="logo-text">蚂蚁记账</text>
    </view>
    <view class="login-btn" @click="handleLogin">
      <text>微信授权登录</text>
    </view>
  </view>
</template>

<script>
import { userApi } from '@/utils/api.js'
import { mapMutations } from 'vuex'

export default {
  data() {
    return {
      loading: false
    }
  },
  
  onLoad() {
    // 检查是否已登录
    const token = uni.getStorageSync('token')
    if (token) {
      uni.switchTab({
        url: '/pages/index/index'
      })
    }
  },
  
  methods: {
    ...mapMutations(['SET_TOKEN', 'SET_USER_INFO']),
    
    handleLogin() {
      if (this.loading) return
      
      this.loading = true
      console.log('开始登录流程...')
      
      // 注意：getUserProfile 必须在用户点击事件的直接回调中调用，不能放在 Promise 或 async/await 中
      // 步骤1: 获取用户信息（必须在点击事件中直接调用）
      console.log('步骤1: 获取用户信息（必须在点击事件中直接调用）')
      uni.getUserProfile({
        desc: '用于完善用户资料',
        success: (userProfileRes) => {
          console.log('uni.getUserProfile 成功:', userProfileRes)
          
          if (!userProfileRes || !userProfileRes.userInfo) {
            console.error('用户信息为空')
            uni.showToast({
              title: '用户信息获取失败',
              icon: 'none',
              duration: 2000
            })
            this.loading = false
            return
          }
          
          // 检查用户信息是否完整
          const userInfo = userProfileRes.userInfo || {}
          const nickName = userInfo.nickName || ''
          const avatarUrl = userInfo.avatarUrl || ''
          
          console.log('获取到用户信息:', {
            nickName: nickName,
            avatarUrl: avatarUrl,
            hasNickName: !!nickName,
            hasAvatarUrl: !!avatarUrl
          })
          
          // 如果昵称为空，给出提示（但不阻止登录）
          if (!nickName) {
            console.warn('警告：未获取到用户昵称，可能是微信API限制')
            uni.showToast({
              title: '未获取到昵称，可在个人中心设置',
              icon: 'none',
              duration: 2000
            })
          }
          
          // 如果头像为空，给出提示（但不阻止登录）
          if (!avatarUrl) {
            console.warn('警告：未获取到用户头像，可能是微信API限制')
          }
          
          // 步骤2: 获取微信登录code
          console.log('步骤2: 获取微信登录code')
          uni.login({
            provider: 'weixin',
            success: (loginRes) => {
              console.log('uni.login 成功:', loginRes)
              
              if (!loginRes.code) {
                console.error('登录code为空')
                uni.showToast({
                  title: '获取登录凭证失败',
                  icon: 'none',
                  duration: 2000
                })
                this.loading = false
                return
              }
              
              console.log('获取到登录code:', loginRes.code)
              
              // 步骤3: 调用后端登录接口
              console.log('步骤3: 调用后端登录接口')
              console.log('请求参数:', {
                code: loginRes.code,
                nickName: nickName,
                avatarUrl: avatarUrl
              })
              
              userApi.login({
                code: loginRes.code,
                nickName: nickName,
                avatarUrl: avatarUrl
              }).then((res) => {
                console.log('后端登录接口响应:', res)
                
                if (res.code === 200) {
                  console.log('登录成功，保存token和用户信息')
                  // 保存token和用户信息
                  // 注意：如果头像为空，保存为 null 而不是空字符串
                  this.SET_TOKEN(res.data.token)
                  this.SET_USER_INFO({
                    userId: res.data.userId,
                    openid: res.data.openid,
                    nickName: nickName || '未设置昵称',
                    avatarUrl: avatarUrl || null  // 空字符串转为 null
                  })
                  
                  uni.showToast({
                    title: '登录成功',
                    icon: 'success',
                    duration: 1500
                  })
                  
                  // 跳转到首页
                  setTimeout(() => {
                    uni.switchTab({
                      url: '/pages/index/index'
                    })
                  }, 1500)
                } else {
                  console.error('后端返回错误:', res)
                  uni.showToast({
                    title: res.message || '登录失败',
                    icon: 'none',
                    duration: 2000
                  })
                }
                this.loading = false
              }).catch((error) => {
                console.error('后端登录接口调用失败:', error)
                console.error('错误详情:', {
                  message: error.message,
                  stack: error.stack,
                  error: error
                })
                
                // 根据错误类型显示不同的提示
                let errorMsg = '登录失败'
                if (error.message) {
                  errorMsg = error.message
                } else if (error.errMsg) {
                  errorMsg = error.errMsg
                } else if (typeof error === 'string') {
                  errorMsg = error
                }
                
                // 如果是网络错误，提示检查后端服务
                if (errorMsg.includes('网络错误') || errorMsg.includes('fail') || errorMsg.includes('timeout')) {
                  errorMsg = '网络连接失败，请检查后端服务是否启动（端口9091）'
                }
                
                uni.showToast({
                  title: errorMsg,
                  icon: 'none',
                  duration: 3000
                })
                this.loading = false
              })
            },
            fail: (err) => {
              console.error('uni.login 失败:', err)
              uni.showToast({
                title: '获取微信登录凭证失败: ' + (err.errMsg || '未知错误'),
                icon: 'none',
                duration: 2000
              })
              this.loading = false
            }
          })
        },
        fail: (err) => {
          console.error('uni.getUserProfile 失败:', err)
          let errorMsg = '获取用户信息失败: ' + (err.errMsg || '用户拒绝授权')
          
          // 特殊处理 getUserProfile 的错误
          if (err.errMsg && err.errMsg.includes('can only be invoked by user TAP gesture')) {
            errorMsg = '请直接点击按钮进行授权'
          }
          
          uni.showToast({
            title: errorMsg,
            icon: 'none',
            duration: 2000
          })
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.logo {
  margin-bottom: 100rpx;
}

.logo-text {
  font-size: 60rpx;
  font-weight: bold;
  color: #fff;
}

.login-btn {
  width: 500rpx;
  height: 88rpx;
  background-color: #fff;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  color: #667eea;
  font-weight: bold;
}
</style>
