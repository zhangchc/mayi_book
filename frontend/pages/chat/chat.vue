<template>
  <view class="chat-container">
    <!-- 顶部：支出 / 收入 选择 -->
    <view class="header">
      <view 
        class="tab-item" 
        :class="{ active: recordType === 1 }"
        @click="recordType = 1"
      >
        <text>支出</text>
      </view>
      <view 
        class="tab-item" 
        :class="{ active: recordType === 2 }"
        @click="recordType = 2"
      >
        <text>收入</text>
      </view>
    </view>

    <!-- 占位内容区，后续可扩展 -->
    <view class="content">
      <text class="content-tip">当前为「{{ recordType === 1 ? '支出' : '收入' }}」</text>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      recordType: 1  // 1-支出 2-收入
    }
  }
}
</script>

<style scoped>
.chat-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 60rpx;
  padding: 80rpx 30rpx 30rpx;
  background-color: #FFC107;
}

.tab-item {
  position: relative;
  padding: 16rpx 0;
}

.tab-item text {
  font-size: 32rpx;
  color: #333;
  transition: all 0.3s;
}

.tab-item.active text {
  font-weight: bold;
  color: #333;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 4rpx;
  background-color: #333;
  border-radius: 2rpx;
}

.content {
  padding: 40rpx 30rpx;
  text-align: center;
}

.content-tip {
  font-size: 28rpx;
  color: #999;
}
</style>
