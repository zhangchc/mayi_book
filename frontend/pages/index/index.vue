<template>
  <view class="index-container">
    <!-- 顶部黄色区域 -->
    <view class="yellow-header">
      <!-- 顶部栏 -->
      <view class="header-top">
        <view class="header-left">
          <text class="smile-icon">😊</text>
          <view class="date-selector-top" @click="showMonthPicker">
            <text class="year-top">{{ formatYear(currentMonth) }}</text>
            <view class="month-row-top">
              <text class="month-top">{{ formatMonth(currentMonth) }}</text>
              <text class="arrow-top">▼</text>
            </view>
          </view>
        </view>
        <view class="header-center">
          <text class="app-name">蚂蚁记账</text>
        </view>
        <view class="header-right">
          <text class="icon">🔍</text>
          <text class="icon">📅</text>
        </view>
      </view>
      
      <!-- 统计区域 -->
      <view class="statistics-section">
        <!-- 收入区域 -->
        <view class="income-section">
          <text class="label">收入</text>
          <text class="amount">{{ formatAmount(statistics.totalIncome) }}</text>
        </view>
        
        <!-- 支出区域 -->
        <view class="expense-section">
          <text class="label">支出</text>
          <view class="expense-row">
            <text class="amount">{{ formatAmount(statistics.totalExpense) }}</text>
            <text class="eye-icon">👁️</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 功能按钮区域 -->
    <view class="function-bar">
      <view class="function-item" @click="handleFunctionClick('账单')">
        <view class="function-icon">📄</view>
        <text class="function-label">账单</text>
      </view>
      <view class="function-item" @click="handleFunctionClick('预算')">
        <view class="function-icon">💰</view>
        <text class="function-label">预算</text>
      </view>
      <view class="function-item" @click="handleFunctionClick('资产管家')">
        <view class="function-icon">🛡️</view>
        <text class="function-label">资产管家</text>
      </view>
      <view class="function-item" @click="handleFunctionClick('购物返现')">
        <view class="function-icon">🛍️</view>
        <text class="function-label">购物返现</text>
      </view>
      <view class="function-item" @click="handleFunctionClick('更多')">
        <view class="function-icon">⋯</view>
        <text class="function-label">更多</text>
      </view>
    </view>
    
    <!-- 记账明细列表 -->
    <scroll-view 
      class="record-list" 
      scroll-y 
      @scrolltolower="loadMore"
      :refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <!-- 加载中状态 -->
      <view v-if="loading && recordList.length === 0" class="loading">
        <text>加载中...</text>
      </view>
      
      <!-- 有数据时显示列表 -->
      <template v-if="!loading || recordList.length > 0">
        <view v-for="(group, index) in recordList" :key="index" class="record-group">
          <date-header 
            :date="group.date" 
            :day-of-week="group.dayOfWeek"
            :total-expense="group.totalExpense"
            :total-income="group.totalIncome"
          />
          <view v-for="record in group.records" :key="record.id" class="record-item-wrapper">
            <record-item :record="record" :type="getRecordType(record)" />
          </view>
        </view>
      </template>
      
      <!-- 空状态 -->
      <view v-if="!loading && recordList.length === 0" class="empty-state">
        <text class="empty-text">暂无记账记录</text>
        <text class="empty-tip">快去记一笔吧~</text>
      </view>
      
      <!-- 加载更多状态 -->
      <view v-if="loading && recordList.length > 0" class="loading">
        <text>加载中...</text>
      </view>
      
      <!-- 没有更多了 -->
      <view v-if="!loading && noMore && recordList.length > 0" class="no-more">
        <text>没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { recordApi } from '@/utils/api.js'
import { formatMonth, formatAmount, getCurrentMonth } from '@/utils/util.js'
import DateHeader from '@/components/date-header/date-header.vue'
import RecordItem from '@/components/record-item/record-item.vue'

export default {
  components: {
    DateHeader,
    RecordItem
  },
  
  data() {
    return {
      currentTime: '',
      currentMonth: getCurrentMonth(),
      statistics: {
        totalExpense: 0,
        totalIncome: 0,
        balance: 0
      },
      recordList: [],
      page: 1,
      pageSize: 20,
      loading: false,
      noMore: false,
      refreshing: false,
      isFirstLoad: true // 标记是否首次加载
    }
  },
  
  onLoad() {
    // 初次加载时同时加载明细和统计数据
    this.startTimer()
    this.loadInitialData()
  },
  
  onShow() {
    // 如果不是首次加载，页面显示时刷新数据（包括从记账页面返回时）
    if (!this.isFirstLoad) {
      this.refreshData()
    }
  },
  
  onUnload() {
    this.stopTimer()
  },
  
  onPullDownRefresh() {
    this.onRefresh()
  },
  
  methods: {
    formatMonth,
    formatAmount,
    
    formatYear(month) {
      if (!month) return ''
      const parts = month.split('-')
      if (parts.length === 2) {
        return `${parts[0]}年`
      }
      return month
    },
    
    startTimer() {
      this.updateTime()
      this.timer = setInterval(() => {
        this.updateTime()
      }, 1000)
    },
    
    stopTimer() {
      if (this.timer) {
        clearInterval(this.timer)
      }
    },
    
    updateTime() {
      const now = new Date()
      const hour = String(now.getHours()).padStart(2, '0')
      const minute = String(now.getMinutes()).padStart(2, '0')
      this.currentTime = `${hour}:${minute}`
    },
    
    async loadData() {
      // 如果是刷新（第一页），允许加载；如果是加载更多，需要检查 noMore
      if (this.loading || (this.page > 1 && this.noMore)) return
      
      this.loading = true
      
      try {
        const res = await recordApi.getRecordList({
          page: this.page,
          pageSize: this.pageSize,
          month: this.currentMonth
        })
        
        if (res.code === 200) {
          const list = res.data.list || []
          if (this.page === 1) {
            this.recordList = list
          } else {
            this.recordList = [...this.recordList, ...list]
          }
          
          // 更新 noMore 状态
          if (list.length < this.pageSize) {
            this.noMore = true
          } else {
            this.noMore = false
          }
        } else {
          console.error('加载数据失败:', res.message || '未知错误')
          uni.showToast({
            title: res.message || '加载失败',
            icon: 'none'
          })
        }
      } catch (error) {
        console.error('加载数据失败:', error)
        uni.showToast({
          title: '加载失败，请重试',
          icon: 'none'
        })
      } finally {
        this.loading = false
        this.refreshing = false
        uni.stopPullDownRefresh()
      }
    },
    
    async loadStatistics() {
      try {
        const res = await recordApi.getStatistics(this.currentMonth)
        if (res.code === 200) {
          this.statistics = res.data
        }
      } catch (error) {
        console.error('加载统计失败:', error)
      }
    },
    
    async loadInitialData() {
      // 首次加载数据
      this.isFirstLoad = true
      await this.refreshData()
      this.isFirstLoad = false
    },
    
    async refreshData() {
      this.page = 1
      this.noMore = false
      await Promise.all([
        this.loadData(),
        this.loadStatistics()
      ])
    },
    
    async onRefresh() {
      this.refreshing = true
      await this.refreshData()
    },
    
    async loadMore() {
      if (this.noMore || this.loading) return
      this.page++
      await this.loadData()
    },
    
    showMonthPicker() {
      // 简单的月份选择，实际可以使用picker组件
      uni.showActionSheet({
        itemList: ['本月', '上月', '选择月份'],
        success: (res) => {
          if (res.tapIndex === 0) {
            this.currentMonth = getCurrentMonth()
            this.refreshData()
          } else if (res.tapIndex === 1) {
            const now = new Date()
            now.setMonth(now.getMonth() - 1)
            const year = now.getFullYear()
            const month = String(now.getMonth() + 1).padStart(2, '0')
            this.currentMonth = `${year}-${month}`
            this.refreshData()
          }
        }
      })
    },
    
    getRecordType(record) {
      // 从record对象中获取type字段
      return record.type || 'expense'
    },
    
    handleFunctionClick(functionName) {
      uni.showToast({
        title: '建设中',
        icon: 'none',
        duration: 2000
      })
    }
  }
}
</script>

<style scoped>
.index-container {
  min-height: 100vh;
  background-color: #fff;
}

/* 顶部黄色区域 */
.yellow-header {
  background-color: #FFC107;
  padding-top: 20rpx;
  padding-bottom: 30rpx;
}

/* 顶部栏 */
.header-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30rpx 20rpx;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.smile-icon {
  font-size: 32rpx;
  line-height: 1;
}

.date-selector-top {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.year-top {
  font-size: 22rpx;
  color: rgba(0, 0, 0, 0.6);
  line-height: 1.2;
}

.month-row-top {
  display: flex;
  align-items: center;
  gap: 6rpx;
}

.month-top {
  font-size: 32rpx;
  color: #333;
  font-weight: 500;
  line-height: 1.2;
}

.arrow-top {
  font-size: 18rpx;
  color: #333;
  line-height: 1.2;
}

.header-center .app-name {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.header-right {
  display: flex;
  gap: 24rpx;
}

.header-right .icon {
  font-size: 36rpx;
  line-height: 1;
}

/* 统计区域 */
.statistics-section {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 0 30rpx;
  gap: 40rpx;
}

/* 收入区域 */
.income-section {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  flex: 1;
  align-items: center;
}

.income-section .label {
  font-size: 24rpx;
  color: rgba(0, 0, 0, 0.6);
  line-height: 1.2;
}

.income-section .amount {
  font-size: 36rpx;
  color: #333;
  font-weight: 500;
  line-height: 1.2;
}

/* 支出区域 */
.expense-section {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  flex: 1;
  align-items: center;
}

.expense-section .label {
  font-size: 24rpx;
  color: rgba(0, 0, 0, 0.6);
  line-height: 1.2;
}

.expense-row {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.expense-section .amount {
  font-size: 36rpx;
  color: #333;
  font-weight: 500;
  line-height: 1.2;
}

.eye-icon {
  font-size: 28rpx;
  color: #333;
  line-height: 1.2;
}

/* 功能按钮区域 */
.function-bar {
  display: flex;
  align-items: center;
  justify-content: space-around;
  background-color: #fff;
  padding: 40rpx 20rpx;
  margin-bottom: 0;
  border-top-left-radius: 30rpx;
  border-top-right-radius: 30rpx;
  margin-top: -20rpx;
  position: relative;
  z-index: 1;
}

.function-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  gap: 12rpx;
  cursor: pointer;
}

.function-icon {
  font-size: 44rpx;
  line-height: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 44rpx;
}

.function-label {
  font-size: 24rpx;
  color: #333;
  line-height: 1.2;
  text-align: center;
}

.record-list {
  height: calc(100vh - 400rpx);
  background-color: #fff;
}

.record-group {
  margin-bottom: 0;
}

.record-item-wrapper {
  background-color: #fff;
}

.loading, .no-more {
  text-align: center;
  padding: 40rpx;
  color: #999;
  font-size: 24rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 40rpx;
  text-align: center;
}

.empty-text {
  font-size: 32rpx;
  color: #999;
  margin-bottom: 20rpx;
}

.empty-tip {
  font-size: 24rpx;
  color: #ccc;
}
</style>
