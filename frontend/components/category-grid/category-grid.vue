<template>
  <view class="category-grid">
    <view 
      v-for="category in categories" 
      :key="category.id"
      class="category-item"
      @click="handleSelect(category)"
    >
      <view class="category-icon">
        <text>{{ getIcon(category.icon) }}</text>
      </view>
      <text class="category-name">{{ category.name }}</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'CategoryGrid',
  props: {
    categories: {
      type: Array,
      default: () => []
    },
    recordType: {
      type: String,
      default: 'expense'
    }
  },
  
  methods: {
    handleSelect(category) {
      this.$emit('select', category)
    },
    
    getIcon(iconName) {
      // 简单的图标映射，实际可以使用图标字体或图片
      const iconMap = {
        'food': '🍔',
        'transport': '🚗',
        'shopping': '🛍️',
        'entertainment': '🎬',
        'medical': '🏥',
        'education': '📚',
        'housing': '🏠',
        'communication': '📱',
        'clothing': '👔',
        'beauty': '💄',
        'sports': '⚽',
        'travel': '✈️',
        'pet': '🐶',
        'gift': '🎁',
        'insurance': '🛡️',
        'bill': '💳',
        'repair': '🔧',
        'investment': '📈',
        'other': '📦',
        'salary': '💰',
        'bonus': '🎉',
        'parttime': '💼',
        'redpacket': '🧧'
      }
      return iconMap[iconName] || '📝'
    }
  }
}
</script>

<style scoped>
.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30rpx;
  padding: 30rpx;
  background-color: #fff;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10rpx;
}

.category-icon {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50rpx;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48rpx;
}

.category-name {
  font-size: 24rpx;
  color: #333;
}
</style>
