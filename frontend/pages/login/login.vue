<template>
  <view class="login-container">
    <view class="logo">
      <text class="logo-text">蚂蚁记账</text>
    </view>
    
    <!-- 用户信息填写区域 -->
    <view class="user-info-section">
      <!-- 头像选择 -->
      <view class="avatar-section">
        <text class="label">选择头像</text>
        <button class="avatar-btn" open-type="chooseAvatar" @chooseavatar="onChooseAvatar">
          <image v-if="avatarUrl" class="avatar-img" :src="avatarUrl" mode="aspectFill"></image>
          <view v-else class="avatar-placeholder">
            <text class="avatar-icon">📷</text>
            <text class="avatar-text">点击选择头像</text>
          </view>
        </button>
      </view>
      
      <!-- 昵称输入 -->
      <view class="nickname-section">
        <text class="label">输入昵称</text>
        <input 
          class="nickname-input" 
          type="nickname" 
          placeholder="请输入昵称"
          v-model="nickName"
          @blur="onNicknameBlur"
        />
      </view>
    </view>
    
    <!-- 登录按钮 -->
    <view class="login-btn" @click="handleLogin" :class="{ disabled: loading }">
      <text>{{ loading ? '登录中...' : '微信授权登录' }}</text>
    </view>
  </view>
</template>

<script>
import { userApi } from '@/utils/api.js'
import { HTTP_CODE_OK } from '@/utils/util.js'
import { mapMutations } from 'vuex'

export default {
  data() {
    return {
      loading: false,
      avatarUrl: '',  // 用户选择的头像
      nickName: ''    // 用户输入的昵称
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
    
    // 选择头像回调（必须在用户点击事件中触发）
    onChooseAvatar(e) {
      const { avatarUrl } = e.detail
      if (avatarUrl) {
        this.avatarUrl = avatarUrl
      } else {
        console.warn('未获取到头像URL')
      }
    },
    
    // 昵称输入失焦回调
    onNicknameBlur(e) {
      // uni-app 中 input 的 blur 事件通过 e.detail.value 获取值
      const value = (e.detail && e.detail.value) ? e.detail.value : (this.nickName || '')
      if (value) {
        this.nickName = value.trim()
      }
    },
    
    // 登录处理（必须在用户点击事件的直接回调中调用）
    handleLogin() {
      if (this.loading) return
      
      // 验证用户信息（可选，不强制要求）
      if (!this.nickName || !this.nickName.trim()) {
        uni.showToast({
          title: '请输入昵称',
          icon: 'none',
          duration: 2000
        })
        return
      }
      
      this.loading = true
      
      // 步骤1: 获取微信登录code（必须在点击事件中直接调用）
      uni.login({
        provider: 'weixin',
        success: (loginRes) => {
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
          
          // 步骤2: 调用后端登录接口
          userApi.login({
            code: loginRes.code,
            nickName: this.nickName,
            avatarUrl: this.avatarUrl || null  // 如果头像为空，传 null
          }).then((res) => {
            if (res.code === HTTP_CODE_OK) {
              // 保存token和用户信息
              this.SET_TOKEN(res.data.token)
              this.SET_USER_INFO({
                userId: res.data.userId,
                openid: res.data.openid,
                nickName: this.nickName || '未设置昵称',
                avatarUrl: this.avatarUrl || null  // 空字符串转为 null
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
  min-height: 100vh;
  padding: 60rpx 40rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.logo {
  margin-bottom: 80rpx;
}

.logo-text {
  font-size: 60rpx;
  font-weight: bold;
  color: #fff;
}

.user-info-section {
  width: 100%;
  max-width: 600rpx;
  margin-bottom: 60rpx;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 50rpx;
}

.label {
  font-size: 28rpx;
  color: #fff;
  margin-bottom: 20rpx;
  opacity: 0.9;
}

.avatar-btn {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  padding: 0;
  margin: 0;
  background-color: rgba(255, 255, 255, 0.2);
  border: 4rpx solid rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.avatar-btn::after {
  border: none;
}

.avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

.avatar-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.avatar-icon {
  font-size: 50rpx;
  margin-bottom: 10rpx;
}

.avatar-text {
  font-size: 22rpx;
  color: #fff;
  opacity: 0.8;
}

.nickname-section {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.nickname-input {
  width: 100%;
  height: 88rpx;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 44rpx;
  padding: 0 30rpx;
  font-size: 28rpx;
  color: #333;
  box-sizing: border-box;
}

.nickname-input::placeholder {
  color: #999;
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
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.login-btn.disabled {
  opacity: 0.6;
  background-color: #f5f5f5;
  color: #999;
}
</style>
