<template>
  <view class="record-item">
    <view class="category-icon-wrapper">
      <text class="category-icon">{{ getCategoryIconDisplay(record.categoryIcon, record.categoryName) }}</text>
    </view>
    <view class="category-info">
      <text class="category-name">{{ record.categoryName }}</text>
      <text v-if="record.remark" class="remark">{{ record.remark }}</text>
    </view>
    <view class="amount">
      <text :class="amountClass">{{ type === 1 ? '-' : '+' }}¥{{ formatAmount(record.amount) }}</text>
    </view>
  </view>
</template>

<script>
import { formatAmount, getCategoryIconDisplay } from '@/utils/util.js'

export default {
  name: 'RecordItem',
  props: {
    record: {
      type: Object,
      required: true
    },
    type: {
      type: Number,
      default: 1 // 1-支出 2-收入
    }
  },
  computed: {
    amountClass() {
      return this.type === 1 ? 'expense' : 'income'
    }
  },
  methods: {
    formatAmount,
    getCategoryIconDisplay
  }
}
</script>

<style scoped>
.record-item {
  display: flex;
  align-items: center;
  padding: 24rpx 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
  background-color: #fff;
}

.category-icon-wrapper {
  width: 80rpx;
  height: 80rpx;
  border-radius: 40rpx;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
  flex-shrink: 0;
}

.category-icon {
  font-size: 40rpx;
  line-height: 1;
}

.category-info {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  flex: 1;
  min-width: 0;
}

.category-name {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
  line-height: 1.2;
}

.remark {
  font-size: 24rpx;
  color: #999;
  line-height: 1.2;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.amount {
  font-size: 32rpx;
  font-weight: 500;
  flex-shrink: 0;
  margin-left: 20rpx;
}

.amount .expense {
  color: #333;
}

.amount .income {
  color: #333;
}
</style>
