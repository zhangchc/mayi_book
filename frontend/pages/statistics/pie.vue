<template>
  <view class="pie-container">
    <!-- 顶部黄色区域：与图表页一致 -->
    <view class="header">
      <view class="header-title">当月支出分类占比</view>
      <view class="header-month">{{ monthLabel }}</view>
    </view>

    <!-- 饼图 -->
    <view class="chart-section">
      <canvas
        canvas-id="pieChart"
        class="pie-canvas"
        :style="{ width: pieSize + 'px', height: pieSize + 'px' }"
      ></canvas>
      <view v-if="list.length === 0 && !loading" class="chart-empty">当月暂无支出数据</view>
    </view>

    <!-- 分类列表 -->
    <view class="list-section">
      <view class="list-title">分类占比</view>
      <view v-if="list.length === 0 && !loading" class="list-empty">暂无数据</view>
      <view v-else class="list-inner">
        <view
          v-for="(item, index) in list"
          :key="item.categoryId || index"
          class="list-item"
        >
          <view class="item-icon">{{ getCategoryIconDisplay(item.icon, item.categoryName) }}</view>
          <view class="item-info">
            <text class="item-name-pct">{{ item.categoryName }} {{ formatPct(item.percentage) }}%</text>
            <view class="item-bar-bg">
              <view
                class="item-bar-fill"
                :style="{ width: (item.percentage || 0) + '%', backgroundColor: pieColors[index % pieColors.length] }"
              ></view>
            </view>
          </view>
          <text class="item-amount">{{ formatAmount(item.amount) }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { recordApi } from '@/utils/api.js'
import { formatAmount, getCategoryIconDisplay, HTTP_CODE_OK } from '@/utils/util.js'

const PIE_COLORS = ['#FFEB3B', '#5C5C5C', '#8BC34A', '#03A9F4', '#FF9800', '#9C27B0', '#00BCD4', '#E91E63']

export default {
  data() {
    return {
      list: [],
      loading: false,
      pieSize: 320,
      pieColors: PIE_COLORS
    }
  },
  computed: {
    monthLabel() {
      const now = new Date()
      return `${now.getFullYear()}年${now.getMonth() + 1}月`
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    formatAmount,
    formatPct(val) {
      if (val == null) return '0.00'
      const n = Number(val)
      return isNaN(n) ? '0.00' : n.toFixed(2)
    },
    getCategoryIconDisplay,
    getCurrentMonthRange() {
      const now = new Date()
      const y = now.getFullYear()
      const m = now.getMonth() + 1
      const startDate = `${y}-${String(m).padStart(2, '0')}-01`
      const lastDay = new Date(y, m, 0).getDate()
      const endDate = `${y}-${String(m).padStart(2, '0')}-${String(lastDay).padStart(2, '0')}`
      return { startDate, endDate }
    },
    async loadData() {
      this.loading = true
      const { startDate, endDate } = this.getCurrentMonthRange()
      try {
        const res = await recordApi.getExpenseRanking({ startDate, endDate })
        this.list = (res.code === HTTP_CODE_OK && Array.isArray(res.data)) ? res.data : []
        this.$nextTick(() => { setTimeout(() => this.drawPie(), 150) })
      } catch (e) {
        this.list = []
      } finally {
        this.loading = false
      }
    },
    drawPie() {
      const ctx = uni.createCanvasContext('pieChart', this)
      const size = this.pieSize
      const cx = size / 2
      const cy = size / 2
      const r = Math.min(cx, cy) * 0.68
      const innerR = r * 0.52
      const labelStartR = r + 6
      const labelEndR = r + 28
      const strokeWidth = 1.5

      ctx.clearRect(0, 0, size, size)

      const list = this.list || []
      if (list.length === 0) {
        ctx.setFillStyle('#999999')
        ctx.setFontSize(14)
        ctx.setTextAlign('center')
        ctx.fillText('暂无数据', cx, cy - 6)
        ctx.draw(true)
        return
      }

      const total = list.reduce((sum, item) => sum + Number(item.amount || 0), 0)
      if (total <= 0) {
        ctx.setFillStyle('#999999')
        ctx.setFontSize(14)
        ctx.setTextAlign('center')
        ctx.fillText('暂无数据', cx, cy - 6)
        ctx.draw(true)
        return
      }

      const sectors = []
      let startAngle = -Math.PI / 2
      list.forEach((item, i) => {
        const ratio = Number(item.amount || 0) / total
        const sweep = Math.max(ratio * Math.PI * 2, 0.02)
        const endAngle = startAngle + sweep
        const midAngle = startAngle + sweep / 2
        sectors.push({
          startAngle,
          endAngle,
          midAngle,
          name: item.categoryName || '',
          pct: this.formatPct(item.percentage),
          color: this.pieColors[i % this.pieColors.length]
        })
        startAngle = endAngle
      })

      sectors.forEach((s) => {
        ctx.setFillStyle(s.color)
        ctx.setStrokeStyle('#FFFFFF')
        ctx.setLineWidth(strokeWidth)
        ctx.beginPath()
        ctx.moveTo(cx, cy)
        ctx.arc(cx, cy, r, s.startAngle, s.endAngle)
        ctx.closePath()
        ctx.fill()
        ctx.stroke()
      })

      ctx.setFillStyle('#FFFFFF')
      ctx.setStrokeStyle('#FFFFFF')
      ctx.setLineWidth(2)
      ctx.beginPath()
      ctx.arc(cx, cy, innerR, 0, Math.PI * 2)
      ctx.fill()
      ctx.stroke()

      ctx.setFillStyle('#333333')
      ctx.setFontSize(11)
      ctx.setTextAlign('center')
      ctx.fillText('当月支出', cx, cy - 8)
      ctx.setFontSize(10)
      ctx.setFillStyle('#666666')
      ctx.fillText(formatAmount(total), cx, cy + 8)

      ctx.setFontSize(10)
      ctx.setStrokeStyle('#666666')
      ctx.setLineWidth(0.8)
      sectors.forEach((s) => {
        const cos = Math.cos(s.midAngle)
        const sin = Math.sin(s.midAngle)
        const x1 = cx + r * cos
        const y1 = cy + r * sin
        const x2 = cx + labelStartR * cos
        const y2 = cy + labelStartR * sin
        const tx = cx + labelEndR * cos
        const ty = cy + labelEndR * sin
        ctx.setStrokeStyle('#999999')
        ctx.beginPath()
        ctx.moveTo(x1, y1)
        ctx.lineTo(x2, y2)
        ctx.stroke()
        const label = (s.name.length > 4 ? s.name.slice(0, 4) : s.name) + ' ' + s.pct + '%'
        ctx.setFillStyle('#333333')
        ctx.setTextAlign(cos >= 0 ? 'left' : 'right')
        ctx.fillText(label, tx + (cos >= 0 ? 4 : -4), ty + 4)
      })

      ctx.draw(true)
    }
  }
}
</script>

<style scoped>
.pie-container {
  min-height: 100vh;
  background-color: #F8F8F8;
}

.header {
  background-color: #FFEB3B;
  padding: 80rpx 30rpx 32rpx;
}

.header-title {
  font-size: 36rpx;
  color: #333;
  font-weight: 600;
  text-align: center;
}

.header-month {
  font-size: 28rpx;
  color: #666;
  text-align: center;
  margin-top: 12rpx;
}

.chart-section {
  padding: 32rpx 0;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #fff;
  margin: 0 24rpx 24rpx;
  border-radius: 16rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.04);
  min-height: 680rpx;
}

.pie-canvas {
  display: block;
}

.chart-empty {
  font-size: 28rpx;
  color: #999;
}

.list-section {
  margin: 0 24rpx 40rpx;
  padding: 28rpx 24rpx;
  background-color: #fff;
  border-radius: 16rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.04);
}

.list-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 24rpx;
}

.list-empty {
  font-size: 26rpx;
  color: #999;
  text-align: center;
  padding: 40rpx;
}

.list-inner {
  display: flex;
  flex-direction: column;
  gap: 32rpx;
}

.list-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.item-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 40rpx;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  line-height: 1;
  flex-shrink: 0;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-name-pct {
  font-size: 28rpx;
  color: #333;
  display: block;
  margin-bottom: 10rpx;
}

.item-bar-bg {
  height: 12rpx;
  background-color: #F0F0F0;
  border-radius: 6rpx;
  overflow: hidden;
}

.item-bar-fill {
  height: 100%;
  border-radius: 6rpx;
  transition: width 0.3s ease;
}

.item-amount {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
  flex-shrink: 0;
}
</style>
