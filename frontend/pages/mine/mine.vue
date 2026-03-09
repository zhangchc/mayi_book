<template>
  <view class="mine-container">
    <!-- 已登录：用户信息 -->
    <view v-if="isLoggedIn" class="user-info">
      <image 
        class="avatar" 
        :src="localUserInfo.avatarUrl || '/static/default-avatar.png'"
        mode="aspectFill"
      />
      <text class="nickname">{{ localUserInfo.nickName || '未设置昵称' }}</text>
    </view>
    
    <!-- 未登录：占位与微信连登 -->
    <view v-else class="user-info guest">
      <image 
        class="avatar" 
        src="/static/default-avatar.png"
        mode="aspectFill"
      />
      <text class="nickname">未登录</text>
      <view class="wechat-login-btn" @click="goWechatLogin">
        <text>微信连登</text>
      </view>
    </view>
    
    <!-- 已登录时显示退出登录 -->
    <view v-if="isLoggedIn" class="logout-btn" @click="handleLogout">
      <text>退出登录</text>
    </view>
  </view>
</template>

<script>
import { mapMutations, mapState } from 'vuex'
import { userApi } from '@/utils/api.js'
import { HTTP_CODE_OK } from '@/utils/util.js'

export default {
  computed: {
    ...mapState(['userInfo', 'token']),
    // 必须依赖 Vuex 的 token，这样登录成功后切回「我的」tab 时界面会响应式更新
    isLoggedIn() {
      return !!this.token
    }
  },
  
  data() {
    return {
      localUserInfo: {}
    }
  },
  
  onLoad() {
    this.syncUserInfoFromStorage()
    this.loadUserInfo()
  },
  
  onShow() {
    this.syncUserInfoFromStorage()
    this.loadUserInfo()
  },
  
  methods: {
    ...mapMutations(['CLEAR_USER_INFO', 'SET_USER_INFO']),
    
    // 从 storage 恢复用户信息到 store（用于冷启动后「我的」页正确显示已登录状态）
    syncUserInfoFromStorage() {
      const token = uni.getStorageSync('token')
      const userInfo = uni.getStorageSync('userInfo')
      if (token && userInfo && !this.userInfo) {
        try {
          const info = typeof userInfo === 'string' ? JSON.parse(userInfo) : userInfo
          this.SET_TOKEN(token)
          this.SET_USER_INFO(info)
        } catch (e) {
          console.warn('syncUserInfoFromStorage parse error', e)
        }
      }
    },
    
    goWechatLogin() {
      uni.navigateTo({
        url: '/pages/login/login'
      })
    },
    
    async loadUserInfo() {
      if (!this.isLoggedIn) return
      // 优先使用 Vuex 中的用户信息
      if (this.userInfo && this.userInfo.userId) {
        this.localUserInfo = { ...this.userInfo }
      }
      // 从后端获取最新信息（用于同步）
      try {
        const res = await userApi.getUserInfo()
        if (res.code === HTTP_CODE_OK && res.data) {
          this.localUserInfo = { ...this.localUserInfo, ...res.data }
          this.SET_USER_INFO(this.localUserInfo)
        }
      } catch (error) {
        console.error('加载用户信息失败:', error)
        if (this.userInfo && this.userInfo.userId) {
          this.localUserInfo = { ...this.userInfo }
        }
      }
    },
    
    handleLogout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            this.CLEAR_USER_INFO()
            uni.reLaunch({
              url: '/pages/login/login'
            })
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.mine-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.user-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 0;
  background-color: #fff;
  margin-bottom: 20rpx;
}

.avatar {
  width: 160rpx;
  height: 160rpx;
  border-radius: 80rpx;
  margin-bottom: 30rpx;
  border: 4rpx solid #eee;
}

.nickname {
  font-size: 32rpx;
  color: #333;
  font-weight: bold;
}

.user-info.guest .nickname {
  color: #999;
  font-weight: normal;
}

.wechat-login-btn {
  margin-top: 40rpx;
  width: 400rpx;
  height: 88rpx;
  background: linear-gradient(135deg, #07c160 0%, #06ad56 100%);
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  color: #fff;
  font-weight: 500;
}

.logout-btn {
  margin: 40rpx 30rpx;
  height: 88rpx;
  background-color: #fff;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  color: #ff6b6b;
}
</style>
