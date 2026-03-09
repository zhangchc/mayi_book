<script>
import store from './store/index.js'

export default {
  onLaunch: function() {
    console.log('App Launch')
    // 从 storage 恢复登录状态，保证冷启动后各页面能正确判断是否已登录
    const token = uni.getStorageSync('token')
    const userInfo = uni.getStorageSync('userInfo')
    if (token) {
      store.commit('SET_TOKEN', token)
      if (userInfo) {
        try {
          const info = typeof userInfo === 'string' ? JSON.parse(userInfo) : userInfo
          store.commit('SET_USER_INFO', info)
        } catch (e) {
          console.warn('App onLaunch parse userInfo error', e)
        }
      }
    }
  },
  onShow: function() {
    console.log('App Show')
  },
  onHide: function() {
    console.log('App Hide')
  }
}
</script>

<style>
/*每个页面公共css */
page {
  background-color: #f5f5f5;
  font-size: 28rpx;
  color: #333;
}

.container {
  padding: 20rpx;
}

.flex {
  display: flex;
}

.flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}

.flex-between {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
