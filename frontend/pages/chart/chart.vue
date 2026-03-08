<template>
  <view class="chart-container">
    <view class="coming-soon">
      <view class="icon">📊</view>
      <text class="title">功能建设中</text>
      <text class="desc">图表功能即将上线，敬请期待</text>
    </view>
  </view>
</template>

<script>
export default {
  onLoad() {
    // 页面加载时提示
    uni.showToast({
      title: '功能建设中',
      icon: 'none',
      duration: 2000
    })
  }
}
</script>

<style scoped>
.chart-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.coming-soon {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60rpx;
}

.icon {
  font-size: 120rpx;
  margin-bottom: 40rpx;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.desc {
  font-size: 28rpx;
  color: #999;
  text-align: center;
}
</style>
