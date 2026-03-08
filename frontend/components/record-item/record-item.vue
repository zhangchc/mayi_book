<template>
  <view class="record-item">
    <view class="category-icon-wrapper">
      <text class="category-icon">{{ getCategoryIcon(record.categoryName) }}</text>
    </view>
    <view class="category-info">
      <text class="category-name">{{ record.categoryName }}</text>
      <text v-if="record.remark" class="remark">{{ record.remark }}</text>
    </view>
    <view class="amount">
      <text :class="amountClass">{{ type === 'expense' ? '-' : '+' }}¥{{ formatAmount(record.amount) }}</text>
    </view>
  </view>
</template>

<script>
import { formatAmount } from '@/utils/util.js'

export default {
  name: 'RecordItem',
  props: {
    record: {
      type: Object,
      required: true
    },
    type: {
      type: String,
      default: 'expense' // expense: 支出, income: 收入
    }
  },
  
  computed: {
    amountClass() {
      return this.type === 'expense' ? 'expense' : 'income'
    }
  },
  
  methods: {
    formatAmount,
    getCategoryIcon(categoryName) {
      // 根据分类名称映射图标
      const iconMap = {
        // 支出分类
        '餐饮': '🍔',
        '交通': '🚗',
        '购物': '🛍️',
        '娱乐': '🎬',
        '医疗': '🏥',
        '教育': '📚',
        '住房': '🏠',
        '通讯': '📱',
        '服饰': '👔',
        '美容': '💄',
        '运动': '⚽',
        '旅行': '✈️',
        '宠物': '🐶',
        '人情': '🎁',
        '保险': '🛡️',
        '缴费': '💳',
        '电费': '🎮',
        '日用': '🧻',
        '孩子': '😊',
        '维修': '🔧',
        '投资': '📈',
        '其他': '📦',
        // 收入分类
        '工资': '💰',
        '奖金': '🎉',
        '投资收益': '📈',
        '兼职': '💼',
        '红包': '🧧',
        '礼金': '🎁'
      }
      return iconMap[categoryName] || '📝'
    }
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
