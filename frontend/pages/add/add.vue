<template>
  <view class="add-container">
    <!-- 顶部栏（黄色背景） -->
    <view class="header">
      <view class="header-tabs">
        <view 
          class="tab-item" 
          :class="{ active: recordType === 'expense' }"
          @click="switchTab('expense')"
        >
          <text>支出</text>
        </view>
        <view 
          class="tab-item" 
          :class="{ active: recordType === 'income' }"
          @click="switchTab('income')"
        >
          <text>收入</text>
        </view>
      </view>
      <view class="header-cancel" @click="goBack">
        <text>取消</text>
      </view>
    </view>
    
    <!-- 分类网格 -->
    <category-grid 
      :categories="categories" 
      :record-type="recordType"
      @select="handleCategorySelect"
    />
    
    <!-- 计算器 -->
    <calculator 
      v-if="showCalculator"
      :category="selectedCategory"
      :record-type="recordType"
      @confirm="handleConfirm"
      @cancel="handleCancel"
    />
  </view>
</template>

<script>
import { categoryApi, recordApi } from '@/utils/api.js'
import CategoryGrid from '@/components/category-grid/category-grid.vue'
import Calculator from '@/components/calculator/calculator.vue'

export default {
  components: {
    CategoryGrid,
    Calculator
  },
  
  data() {
    return {
      recordType: 'expense', // expense: 支出, income: 收入
      categories: [],
      selectedCategory: null,
      showCalculator: false
    }
  },
  
  onLoad(options) {
    if (options.type) {
      this.recordType = options.type
    }
    this.loadCategories()
  },
  
  onShow() {
    // 每次页面显示时重置状态，确保显示分类选择界面
    this.showCalculator = false
    this.selectedCategory = null
  },
  
  methods: {
    async switchTab(type) {
      if (this.recordType === type) {
        return
      }
      
      // 如果计算器打开，先关闭
      if (this.showCalculator) {
        this.showCalculator = false
        this.selectedCategory = null
      }
      
      this.recordType = type
      await this.loadCategories()
    },
    
    async loadCategories() {
      try {
        const res = this.recordType === 'expense' 
          ? await categoryApi.getExpenseCategories()
          : await categoryApi.getIncomeCategories()
        
        if (res.code === 200) {
          this.categories = res.data || []
        }
      } catch (error) {
        console.error('加载分类失败:', error)
        uni.showToast({
          title: '加载分类失败',
          icon: 'none'
        })
      }
    },
    
    handleCategorySelect(category) {
      this.selectedCategory = category
      this.showCalculator = true
    },
    
    async handleConfirm(data) {
      try {
        const res = await recordApi.addRecord({
          categoryId: this.selectedCategory.id,
          amount: data.amount,
          remark: data.remark,
          type: this.recordType
        })
        
        if (res.code === 200) {
          uni.showToast({
            title: '保存成功',
            icon: 'success'
          })
          
          // 返回首页
          setTimeout(() => {
            uni.switchTab({
              url: '/pages/index/index'
            })
          }, 1500)
        }
      } catch (error) {
        console.error('保存失败:', error)
        uni.showToast({
          title: error.message || '保存失败',
          icon: 'none'
        })
      }
    },
    
    handleCancel() {
      this.showCalculator = false
      this.selectedCategory = null
    },
    
    goBack() {
      uni.switchTab({
        url: '/pages/index/index'
      })
    }
  }
}
</script>

<style scoped>
.add-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 100rpx 30rpx 20rpx 30rpx;
  background-color: #ffeb3b;
  /* 亮黄色背景，符合图片中的vibrant yellow色调 */
}

.header-tabs {
  display: flex;
  align-items: center;
  gap: 40rpx;
}

.tab-item {
  position: relative;
  padding: 10rpx 0;
}

.tab-item text {
  font-size: 32rpx;
  color: #333;
  transition: all 0.3s;
}

.tab-item.active text {
  font-weight: bold;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2rpx;
  background-color: #333;
  /* 黑色下划线，符合图片设计 */
}

.header-cancel {
  position: absolute;
  right: 30rpx;
  top: 50%;
  transform: translateY(-50%);
  font-size: 32rpx;
  color: #333;
  padding: 10rpx 0;
}
</style>
