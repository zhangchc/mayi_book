<template>
  <view class="date-header">
    <view class="date-info">
      <text class="date">{{ formatDateDisplay(date) }}</text>
      <text class="day-of-week">{{ dayOfWeek }}</text>
    </view>
    <view class="summary">
      <text v-if="totalExpense > 0" class="expense">支出: {{ formatAmount(totalExpense) }}</text>
      <text v-if="totalIncome > 0" class="income">收入: {{ formatAmount(totalIncome) }}</text>
    </view>
  </view>
</template>

<script>
import { formatAmount, formatDateDisplay } from '@/utils/util.js'

export default {
  name: 'DateHeader',
  props: {
    date: {
      type: String,
      required: true
    },
    dayOfWeek: {
      type: String,
      default: ''
    },
    totalExpense: {
      type: [Number, String],
      default: 0
    },
    totalIncome: {
      type: [Number, String],
      default: 0
    }
  },
  
  methods: {
    formatAmount,
    formatDateDisplay
  }
}
</script>

<style scoped>
.date-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 30rpx;
  background-color: #fff;
  border-bottom: 1rpx solid #f0f0f0;
}

.date-info {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.date {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
}

.day-of-week {
  font-size: 24rpx;
  color: #999;
}

.summary {
  display: flex;
  gap: 20rpx;
}

.summary .expense {
  font-size: 26rpx;
  color: #333;
  font-weight: 500;
}

.summary .income {
  font-size: 26rpx;
  color: #333;
  font-weight: 500;
}
</style>
