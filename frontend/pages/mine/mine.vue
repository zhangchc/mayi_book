<template>
  <view class="mine-container">
    <!-- 用户信息 -->
    <view class="user-info">
      <image 
        class="avatar" 
        :src="localUserInfo.avatarUrl || '/static/default-avatar.png'"
        mode="aspectFill"
      />
      <text class="nickname">{{ localUserInfo.nickName || '未设置昵称' }}</text>
    </view>
    
    <!-- 退出登录 -->
    <view class="logout-btn" @click="handleLogout">
      <text>退出登录</text>
    </view>
  </view>
</template>

<script>
import { mapMutations, mapState } from 'vuex'
import { userApi } from '@/utils/api.js'

export default {
  computed: {
    ...mapState(['userInfo'])
  },
  
  data() {
    return {
      localUserInfo: {}
    }
  },
  
  onLoad() {
    this.loadUserInfo()
  },
  
  onShow() {
    // 每次显示页面时，也尝试从后端获取最新信息
    this.loadUserInfo()
  },
  
  methods: {
    ...mapMutations(['CLEAR_USER_INFO', 'SET_USER_INFO']),
    
    async loadUserInfo() {
      // 优先使用 Vuex 中的用户信息
      if (this.userInfo && this.userInfo.userId) {
        this.localUserInfo = this.userInfo
        console.log('使用 Vuex 中的用户信息:', this.localUserInfo)
      }
      
      // 从后端获取最新信息（用于同步）
      try {
        const res = await userApi.getUserInfo()
        if (res.code === 200 && res.data) {
          // 合并后端返回的信息（后端信息更准确）
          this.localUserInfo = {
            ...this.localUserInfo,
            ...res.data
          }
          // 更新 Vuex 中的用户信息
          this.SET_USER_INFO(this.localUserInfo)
          console.log('从后端获取用户信息:', this.localUserInfo)
        }
      } catch (error) {
        console.error('加载用户信息失败:', error)
        // 如果后端获取失败，至少使用 Vuex 中的信息
        if (this.userInfo && this.userInfo.userId) {
          this.localUserInfo = this.userInfo
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
