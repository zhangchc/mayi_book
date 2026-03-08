<template>
  <view class="calculator-overlay" @click="handleCancel">
    <view class="calculator" @click.stop>
      <view class="calculator-header">
        <text class="category-name">{{ category ? category.name : '' }}</text>
        <text class="close" @click="handleCancel">✕</text>
      </view>
      
      <view class="amount-display">
        <text class="amount">¥{{ displayAmount }}</text>
      </view>
      
      <view class="remark-input">
        <input 
          v-model="remark" 
          placeholder="添加备注（可选）"
          placeholder-style="color: #999"
        />
      </view>
      
      <view class="keypad">
        <view class="keypad-row">
          <view class="key" @click="handleKey('1')">1</view>
          <view class="key" @click="handleKey('2')">2</view>
          <view class="key" @click="handleKey('3')">3</view>
          <view class="key delete" @click="handleDelete">删除</view>
        </view>
        <view class="keypad-row">
          <view class="key" @click="handleKey('4')">4</view>
          <view class="key" @click="handleKey('5')">5</view>
          <view class="key" @click="handleKey('6')">6</view>
          <view class="key" @click="handleKey('+')">+</view>
        </view>
        <view class="keypad-row">
          <view class="key" @click="handleKey('7')">7</view>
          <view class="key" @click="handleKey('8')">8</view>
          <view class="key" @click="handleKey('9')">9</view>
          <view class="key" @click="handleKey('-')">-</view>
        </view>
        <view class="keypad-row">
          <view class="key" @click="handleKey('.')">.</view>
          <view class="key" @click="handleKey('0')">0</view>
          <view class="key" @click="handleKey('00')">00</view>
          <view class="key confirm" @click="handleConfirm">确定</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'Calculator',
  props: {
    category: {
      type: Object,
      default: null
    },
    recordType: {
      type: String,
      default: 'expense'
    }
  },
  
  data() {
    return {
      amount: '0',
      remark: ''
    }
  },
  
  computed: {
    displayAmount() {
      if (this.amount === '0' || this.amount === '') {
        return '0.00'
      }
      // 确保最多两位小数
      const num = parseFloat(this.amount)
      if (isNaN(num)) {
        return '0.00'
      }
      return num.toFixed(2)
    }
  },
  
  methods: {
    handleKey(key) {
      if (key === '.') {
        if (this.amount.indexOf('.') === -1) {
          this.amount += '.'
        }
      } else if (key === '+' || key === '-') {
        // 简单的加减运算
        // 这里简化处理，实际可以更复杂
      } else {
        if (this.amount === '0') {
          this.amount = key
        } else {
          this.amount += key
        }
      }
    },
    
    handleDelete() {
      if (this.amount.length > 1) {
        this.amount = this.amount.slice(0, -1)
      } else {
        this.amount = '0'
      }
    },
    
    handleConfirm() {
      const amount = parseFloat(this.amount)
      if (isNaN(amount) || amount <= 0) {
        uni.showToast({
          title: '请输入有效金额',
          icon: 'none'
        })
        return
      }
      
      this.$emit('confirm', {
        amount: amount,
        remark: this.remark
      })
      
      // 重置
      this.amount = '0'
      this.remark = ''
    },
    
    handleCancel() {
      this.$emit('cancel')
      this.amount = '0'
      this.remark = ''
    }
  }
}
</script>

<style scoped>
.calculator-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  z-index: 999;
}

.calculator {
  width: 100%;
  background-color: #fff;
  border-radius: 30rpx 30rpx 0 0;
  padding: 30rpx;
}

.calculator-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30rpx;
}

.category-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.close {
  font-size: 40rpx;
  color: #999;
}

.amount-display {
  text-align: center;
  padding: 40rpx 0;
  border-bottom: 1rpx solid #eee;
  margin-bottom: 30rpx;
}

.amount {
  font-size: 60rpx;
  font-weight: bold;
  color: #333;
}

.remark-input {
  margin-bottom: 30rpx;
}

.remark-input input {
  width: 100%;
  height: 80rpx;
  padding: 0 20rpx;
  background-color: #f5f5f5;
  border-radius: 8rpx;
  font-size: 28rpx;
}

.keypad {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.keypad-row {
  display: flex;
  gap: 20rpx;
}

.key {
  flex: 1;
  height: 100rpx;
  background-color: #f5f5f5;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  color: #333;
  font-weight: bold;
}

.key.delete {
  background-color: #ff6b6b;
  color: #fff;
}

.key.confirm {
  background-color: #51cf66;
  color: #fff;
}
</style>
