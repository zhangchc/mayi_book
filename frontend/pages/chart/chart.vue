<template>
  <view class="chart-container">
    <!-- 顶部：黄色背景，中间 支出+下拉箭头，右侧图标 -->
    <view class="header">
      <view class="header-top">
        <view class="header-title-wrap" @click="onTapType">
          <text class="header-title">{{ recordType === 1 ? '支出' : '收入' }}</text>
          <text class="header-arrow">▼</text>
        </view>
        <view class="header-right-icon" @click="goPieStatistics">
          <text>📊</text>
        </view>
      </view>
      <!-- 周 / 月 / 年：选中=深灰底白字，未选=黄底深灰字 -->
      <view class="period-tabs">
        <view 
          class="period-tab" 
          :class="{ active: periodRange === 'week' }"
          @click="periodRange = 'week'"
        >
          <text>周</text>
        </view>
        <view 
          class="period-tab" 
          :class="{ active: periodRange === 'month' }"
          @click="periodRange = 'month'"
        >
          <text>月</text>
        </view>
        <view 
          class="period-tab" 
          :class="{ active: periodRange === 'year' }"
          @click="periodRange = 'year'"
        >
          <text>年</text>
        </view>
      </view>
    </view>

    <!-- 周下的子选择：本周排最前，按时间顺序最近的周排最前 -->
    <view v-if="periodRange === 'week'" class="week-row">
      <scroll-view scroll-x class="week-scroll" :show-scrollbar="false">
        <view class="week-tabs">
          <view
            v-for="(item, idx) in weekTabList"
            :key="'w-' + idx"
            class="week-tab-item"
            :class="{ active: selectedWeekOption === item.type }"
            @click="selectedWeekOption = item.type"
          >
            <text>{{ item.label }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 月下的子选择：本月最前，上月、12月…1月 -->
    <view v-if="periodRange === 'month'" class="week-row">
      <scroll-view scroll-x class="week-scroll" :show-scrollbar="false">
        <view class="week-tabs">
          <view
            v-for="(item, idx) in monthTabList"
            :key="'m-' + idx"
            class="week-tab-item"
            :class="{ active: selectedMonthOption === item.type }"
            @click="selectedMonthOption = item.type"
          >
            <text>{{ item.label }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 年下的子选择：今年最前，去年、更早年份 -->
    <view v-if="periodRange === 'year'" class="week-row">
      <scroll-view scroll-x class="week-scroll" :show-scrollbar="false">
        <view class="week-tabs">
          <view
            v-for="(item, idx) in yearTabList"
            :key="'y-' + idx"
            class="week-tab-item"
            :class="{ active: selectedYearOption === item.type }"
            @click="selectedYearOption = item.type"
          >
            <text>{{ item.label }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 统计两行：总支出 / 平均值 -->
    <view v-if="periodRange === 'week'" class="summary">
      <text class="summary-line">总支出: {{ formatAmount(weeklySummary.totalAmount) }}</text>
      <text class="summary-line">平均值: {{ formatAmount(weeklySummary.averageAmount) }}</text>
    </view>
    <view v-if="periodRange === 'month'" class="summary">
      <text class="summary-line">总支出: {{ formatAmount(monthSummary.totalAmount) }}</text>
      <text class="summary-line">平均值: {{ formatAmount(monthSummary.averageAmount) }}</text>
    </view>
    <view v-if="periodRange === 'year'" class="summary">
      <text class="summary-line">总支出: {{ formatAmount(yearSummary.totalAmount) }}</text>
      <text class="summary-line">平均值: {{ formatAmount(yearSummary.averageAmount) }}</text>
    </view>

    <!-- 折线图 -->
    <view v-if="periodRange === 'week'" class="chart-wrapper">
      <canvas canvas-id="weekChart" class="line-chart" :style="{ width: chartWidth + 'px', height: chartHeight + 'px' }"></canvas>
    </view>
    <view v-if="periodRange === 'month'" class="chart-wrapper">
      <canvas canvas-id="monthChart" class="line-chart" :style="{ width: chartWidth + 'px', height: chartHeight + 'px' }"></canvas>
    </view>
    <view v-if="periodRange === 'year'" class="chart-wrapper">
      <canvas canvas-id="yearChart" class="line-chart" :style="{ width: chartWidth + 'px', height: chartHeight + 'px' }"></canvas>
    </view>

    <!-- 支出排行榜（周/月/年均展示） -->
    <view v-if="(periodRange === 'week' || periodRange === 'month' || periodRange === 'year') && recordType === 1" class="ranking-section">
      <view class="ranking-title">支出排行榜</view>
      <view v-if="expenseRankList.length === 0" class="ranking-empty">暂无支出数据</view>
      <view v-else class="ranking-list">
        <view
          v-for="(item, index) in expenseRankList"
          :key="item.categoryId"
          class="ranking-item"
        >
          <view class="ranking-icon-wrap">{{ getCategoryIconDisplay(item.icon, item.categoryName) }}</view>
          <view class="ranking-info">
            <text class="ranking-name-pct">{{ item.categoryName }} {{ formatPct(item.percentage) }}%</text>
            <view class="ranking-bar-bg">
              <view class="ranking-bar-fill" :style="{ width: (item.percentage || 0) + '%' }"></view>
            </view>
          </view>
          <text class="ranking-amount">{{ formatAmount(item.amount) }}</text>
        </view>
      </view>
    </view>

  </view>
</template>

<script>
// 获取某日期在当年中的 ISO 周次（周一为一周开始）
function getISOWeek(date) {
  const d = new Date(date)
  d.setHours(0, 0, 0, 0)
  const day = d.getDay() || 7
  const thursday = new Date(d)
  thursday.setDate(d.getDate() - day + 4)
  const jan1 = new Date(thursday.getFullYear(), 0, 1)
  const weekNo = 1 + Math.floor((thursday - jan1) / 86400000 / 7)
  return weekNo
}

// 获取当年共有多少周（52 或 53）
function getWeeksInYear(year) {
  const dec28 = new Date(year, 11, 28)
  return getISOWeek(dec28)
}

import { recordApi } from '@/utils/api.js'
import { formatAmount, getCategoryIconDisplay } from '@/utils/util.js'

export default {
  data() {
    return {
      recordType: 1,   // 1-支出 2-收入
      periodRange: 'week',     // 周/月/年，默认周（与原型一致）
      selectedWeekOption: 'this',
      weeklySummary: { totalAmount: 0, averageAmount: 0 },
      weeklyPoints: [],
      selectedMonthOption: 'this',  // 'this'|'last'|'yyyy-MM'
      monthSummary: { totalAmount: 0, averageAmount: 0 },
      monthPoints: [],              // 当月每日 [{ date, amount }]
      selectedYearOption: 'this',   // 'this'|'last'|2026
      yearSummary: { totalAmount: 0, averageAmount: 0 },
      yearPoints: [],               // 12个月 [{ month: '2026-01', amount }]
      expenseRankList: [],
      chartWidth: 350,
      chartHeight: 160
    }
  },
  watch: {
    recordType() {
      this.loadPeriodData()
    },
    periodRange() {
      this.loadPeriodData()
    },
    selectedWeekOption() {
      if (this.periodRange === 'week') {
        this.tryLoadWeekData()
        this.loadExpenseRanking()
      }
    },
    selectedMonthOption() {
      if (this.periodRange === 'month') {
        this.tryLoadMonthData()
        this.loadExpenseRanking()
      }
    },
    selectedYearOption() {
      if (this.periodRange === 'year') {
        this.tryLoadYearData()
        this.loadExpenseRanking()
      }
    }
  },
  mounted() {
    this.loadPeriodData()
  },
  computed: {
    periodLabel() {
      const map = { week: '周', month: '月', year: '年' }
      return map[this.periodRange] || '月'
    },
    yearWeekCount() {
      const year = new Date().getFullYear()
      return getWeeksInYear(year)
    },
    weekTabList() {
      const currentWeek = getISOWeek(new Date())
      const list = [{ type: 'this', label: '本周' }, { type: 'last', label: '上周' }]
      for (let n = currentWeek - 2; n >= 1; n--) {
        list.push({ type: n, label: String(n).padStart(2, '0') + '周' })
      }
      return list
    },
    /** 月 tab：本月、上月、12月…1月（最近在前） */
    monthTabList() {
      const now = new Date()
      const y = now.getFullYear()
      const m = now.getMonth() + 1
      const list = [{ type: 'this', label: '本月' }, { type: 'last', label: '上月' }]
      for (let i = 12; i >= 1; i--) {
        list.push({ type: `${y}-${String(i).padStart(2, '0')}`, label: `${i}月` })
      }
      return list
    },
    /** 年 tab：今年、去年、前年… */
    yearTabList() {
      const y = new Date().getFullYear()
      const list = [{ type: 'this', label: '今年' }, { type: 'last', label: '去年' }]
      for (let i = y - 2; i >= y - 10; i--) {
        if (i >= 2020) list.push({ type: i, label: `${i}年` })
      }
      return list
    }
  },
  methods: {
    formatAmount,
    onTapType() {
      uni.showActionSheet({
        itemList: ['支出', '收入'],
        success: (res) => {
          this.recordType = res.tapIndex === 0 ? 1 : 2
        }
      })
    },
    goPieStatistics() {
      uni.navigateTo({ url: '/pages/statistics/pie' })
    },
    // 根据起止日期生成一周 7 天数据，dailyList 中有的日期填金额，没有的填 0
    buildWeekPoints(startDate, endDate, dailyList) {
      const dateToAmount = {}
      ;(dailyList || []).forEach(item => {
        if (item.date) dateToAmount[item.date] = Number(item.amount) || 0
      })
      const points = []
      const start = new Date(startDate.replace(/-/g, '/'))
      const end = new Date(endDate.replace(/-/g, '/'))
      for (let i = 0; i < 7; i++) {
        const d = new Date(start)
        d.setDate(d.getDate() + i)
        if (d > end) break
        const dateStr = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
        points.push({ date: dateStr, amount: dateToAmount[dateStr] != null ? dateToAmount[dateStr] : 0 })
      }
      return points
    },
    formatPct(val) {
      if (val == null) return '0.00'
      const n = Number(val)
      return isNaN(n) ? '0.00' : n.toFixed(2)
    },
    getCategoryIconDisplay,

    // 根据当前周选项计算起止日期（yyyy-MM-dd）
    calcWeekRange() {
      const today = new Date()
      const currentYear = today.getFullYear()
      const currentWeek = getISOWeek(today)

      const makeRange = (week, year) => {
        // 计算某年某周的周一
        const jan4 = new Date(year, 0, 4)
        const jan4Day = jan4.getDay() || 7
        const weekStart = new Date(jan4)
        weekStart.setDate(jan4.getDate() - (jan4Day - 1) + (week - 1) * 7)
        const weekEnd = new Date(weekStart)
        weekEnd.setDate(weekStart.getDate() + 6)
        const fmt = d => `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
        return { startDate: fmt(weekStart), endDate: fmt(weekEnd) }
      }

      let targetYear = currentYear
      let targetWeek = currentWeek
      if (this.selectedWeekOption === 'last') {
        if (currentWeek === 1) {
          targetYear = currentYear - 1
          targetWeek = getWeeksInYear(targetYear)
        } else {
          targetWeek = currentWeek - 1
        }
      } else if (typeof this.selectedWeekOption === 'number') {
        targetWeek = this.selectedWeekOption
      }

      return makeRange(targetWeek, targetYear)
    },

    loadPeriodData() {
      if (this.periodRange === 'week') this.tryLoadWeekData()
      else if (this.periodRange === 'month') this.tryLoadMonthData()
      else if (this.periodRange === 'year') this.tryLoadYearData()
      this.loadExpenseRanking()
    },
    getPeriodDateRange() {
      if (this.periodRange === 'week') return this.calcWeekRange()
      if (this.periodRange === 'month') return this.calcMonthRange()
      if (this.periodRange === 'year') return this.getYearRange()
      return { startDate: '', endDate: '' }
    },
    async loadExpenseRanking() {
      if (this.recordType !== 1) {
        this.expenseRankList = []
        return
      }
      const { startDate, endDate } = this.getPeriodDateRange()
      if (!startDate || !endDate) {
        this.expenseRankList = []
        return
      }
      // 快照当前维度，防止异步返回时已切换维度导致显示错误数据
      const snapshot = {
        periodRange: this.periodRange,
        selectedWeekOption: this.selectedWeekOption,
        selectedMonthOption: this.selectedMonthOption,
        selectedYearOption: this.selectedYearOption
      }
      this.expenseRankList = []
      try {
        const rankRes = await recordApi.getExpenseRanking({ startDate, endDate })
        const stillMatch =
          this.periodRange === snapshot.periodRange &&
          this.selectedWeekOption === snapshot.selectedWeekOption &&
          this.selectedMonthOption === snapshot.selectedMonthOption &&
          this.selectedYearOption === snapshot.selectedYearOption
        if (stillMatch) {
          this.expenseRankList = (rankRes.code === 200 && Array.isArray(rankRes.data)) ? rankRes.data : []
        }
      } catch (e) {
        if (
          this.periodRange === snapshot.periodRange &&
          this.selectedWeekOption === snapshot.selectedWeekOption &&
          this.selectedMonthOption === snapshot.selectedMonthOption &&
          this.selectedYearOption === snapshot.selectedYearOption
        ) {
          this.expenseRankList = []
        }
      }
    },
    async tryLoadWeekData() {
      if (this.periodRange !== 'week') return
      const { startDate, endDate } = this.calcWeekRange()
      try {
        const res = await recordApi.getWeeklyStatistics({ startDate, endDate, type: this.recordType })
        if (res.code === 200 && res.data) {
          this.weeklySummary = { totalAmount: res.data.totalAmount || 0, averageAmount: res.data.averageAmount || 0 }
          this.weeklyPoints = this.buildWeekPoints(startDate, endDate, res.data.dailyList || [])
          this.$nextTick(() => { setTimeout(() => this.drawWeekChart(), 100) })
        }
      } catch (e) {
        console.error('加载周统计失败:', e)
      }
    },
    calcMonthRange() {
      const now = new Date()
      let y = now.getFullYear()
      let m = now.getMonth() + 1
      if (this.selectedMonthOption === 'last') {
        if (m === 1) { y--; m = 12 } else m--
      } else if (this.selectedMonthOption !== 'this' && this.selectedMonthOption) {
        const [yy, mm] = String(this.selectedMonthOption).split('-').map(Number)
        y = yy
        m = mm
      }
      const startDate = `${y}-${String(m).padStart(2, '0')}-01`
      const lastDay = new Date(y, m, 0).getDate()
      const endDate = `${y}-${String(m).padStart(2, '0')}-${String(lastDay).padStart(2, '0')}`
      return { startDate, endDate }
    },
    buildMonthPoints(startDate, endDate, dailyList) {
      const dateToAmount = {}
      ;(dailyList || []).forEach(item => {
        if (item.date) dateToAmount[item.date] = Number(item.amount) || 0
      })
      const points = []
      const start = new Date(startDate.replace(/-/g, '/'))
      const end = new Date(endDate.replace(/-/g, '/'))
      const days = Math.round((end - start) / 86400000) + 1
      for (let i = 0; i < days; i++) {
        const d = new Date(start)
        d.setDate(d.getDate() + i)
        const dateStr = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
        points.push({ date: dateStr, amount: dateToAmount[dateStr] != null ? dateToAmount[dateStr] : 0 })
      }
      return points
    },
    async tryLoadMonthData() {
      if (this.periodRange !== 'month') return
      const { startDate, endDate } = this.calcMonthRange()
      try {
        const res = await recordApi.getWeeklyStatistics({ startDate, endDate, type: this.recordType })
        if (res.code === 200 && res.data) {
          this.monthSummary = { totalAmount: res.data.totalAmount || 0, averageAmount: res.data.averageAmount || 0 }
          this.monthPoints = this.buildMonthPoints(startDate, endDate, res.data.dailyList || [])
          this.$nextTick(() => { setTimeout(() => this.drawMonthChart(), 100) })
        }
      } catch (e) {
        console.error('加载月统计失败:', e)
      }
    },
    getYearRange() {
      const y = this.selectedYearOption === 'this' ? new Date().getFullYear()
        : this.selectedYearOption === 'last' ? new Date().getFullYear() - 1
        : Number(this.selectedYearOption) || new Date().getFullYear()
      return { startDate: `${y}-01-01`, endDate: `${y}-12-31`, year: y }
    },
    buildYearPoints(monthlyList, year) {
      const monthToAmount = {}
      ;(monthlyList || []).forEach(item => {
        if (item.month) monthToAmount[item.month] = Number(item.amount) || 0
      })
      const points = []
      for (let m = 1; m <= 12; m++) {
        const monthStr = `${year}-${String(m).padStart(2, '0')}`
        points.push({ month: monthStr, amount: monthToAmount[monthStr] != null ? monthToAmount[monthStr] : 0 })
      }
      return points
    },
    async tryLoadYearData() {
      if (this.periodRange !== 'year') return
      const { year } = this.getYearRange()
      try {
        const res = await recordApi.getYearStatistics({ year, type: this.recordType })
        if (res.code === 200 && res.data) {
          this.yearSummary = { totalAmount: res.data.totalAmount || 0, averageAmount: res.data.averageAmount || 0 }
          this.yearPoints = this.buildYearPoints(res.data.monthlyList || [], year)
          this.$nextTick(() => { setTimeout(() => this.drawYearChart(), 100) })
        }
      } catch (e) {
        console.error('加载年统计失败:', e)
      }
    },

    drawWeekChart() {
      const ctx = uni.createCanvasContext('weekChart', this)
      const width = this.chartWidth
      const height = this.chartHeight
      const paddingLeft = 36
      const paddingRight = 16
      const paddingTop = 16
      const paddingBottom = 32

      ctx.clearRect(0, 0, width, height)

      // 背景
      ctx.setFillStyle('#FFFFFF')
      ctx.fillRect(0, 0, width, height)

      // 轴线（更细更淡）
      ctx.setStrokeStyle('#F0F0F0')
      ctx.setLineWidth(0.5)
      ctx.beginPath()
      ctx.moveTo(paddingLeft, paddingTop)
      ctx.lineTo(paddingLeft, height - paddingBottom)
      ctx.lineTo(width - paddingRight, height - paddingBottom)
      ctx.stroke()

      const points = this.weeklyPoints || []
      if (points.length === 0) {
        ctx.setFillStyle('#999999')
        ctx.setFontSize(12)
        ctx.fillText('本周暂无数据', paddingLeft + 20, height / 2)
        ctx.draw(true)
        return
      }

      const amounts = points.map(p => Number(p.amount || 0))
      const maxAmount = Math.max(...amounts, 1)
      const yMax = maxAmount * 1.15

      const xCount = points.length
      const xStep = xCount > 1 ? (width - paddingLeft - paddingRight) / (xCount - 1) : 0
      const plotHeight = height - paddingTop - paddingBottom

      const todayStr = this.getTodayStr()
      const plotPoints = points.map((p, index) => {
        const x = paddingLeft + xStep * index
        const y = height - paddingBottom - (Number(p.amount || 0) / yMax) * plotHeight
        const isToday = p.date === todayStr
        const xLabel = isToday ? '今天' : this.formatChartDate(p.date)
        return { x, y, date: p.date, amount: Number(p.amount || 0), xLabel }
      })

      // 淡虚线水平线（平均值）
      const avgAmount = Number(this.weeklySummary.averageAmount) || 0
      if (avgAmount > 0) {
        const avgY = height - paddingBottom - (avgAmount / yMax) * plotHeight
        if (avgY > paddingTop && avgY < height - paddingBottom) {
          ctx.setStrokeStyle('#E8E8E8')
          ctx.setLineWidth(0.5)
          try { ctx.setLineDash([4, 4]) } catch (e) {}
          ctx.beginPath()
          ctx.moveTo(paddingLeft, avgY)
          ctx.lineTo(width - paddingRight, avgY)
          ctx.stroke()
          try { ctx.setLineDash([]) } catch (e) {}
        }
      }

      // 折线：细、深灰（原型）
      ctx.setStrokeStyle('#666666')
      ctx.setLineWidth(1)
      ctx.setLineCap('round')
      ctx.setLineJoin('round')
      ctx.beginPath()
      plotPoints.forEach((pt, idx) => {
        if (idx === 0) ctx.moveTo(pt.x, pt.y)
        else ctx.lineTo(pt.x, pt.y)
      })
      ctx.stroke()

      // 数据点：小黄圆 + 细深色描边
      const maxIdx = amounts.indexOf(Math.max(...amounts))
      plotPoints.forEach((pt) => {
        ctx.setFillStyle('#FFEB3B')
        ctx.beginPath()
        ctx.arc(pt.x, pt.y, 3.5, 0, Math.PI * 2)
        ctx.fill()
        ctx.setStrokeStyle('#333333')
        ctx.setLineWidth(1)
        ctx.stroke()
      })

      // 最高点上方显示数值
      if (plotPoints[maxIdx] && amounts[maxIdx] > 0) {
        const pt = plotPoints[maxIdx]
        ctx.setFillStyle('#333333')
        ctx.setFontSize(9)
        ctx.fillText(String(amounts[maxIdx].toFixed(2)), pt.x - 18, pt.y - 6)
      }

      // X 轴：3-02 … 今天
      ctx.setFillStyle('#666666')
      ctx.setFontSize(9)
      plotPoints.forEach(pt => {
        ctx.fillText(pt.xLabel, pt.x - (pt.xLabel.length * 3), height - paddingBottom + 14)
      })

      ctx.draw(true)
    },
    drawMonthChart() {
      const ctx = uni.createCanvasContext('monthChart', this)
      const width = this.chartWidth
      const height = this.chartHeight
      const paddingLeft = 36
      const paddingRight = 16
      const paddingTop = 28
      const paddingBottom = 36
      ctx.clearRect(0, 0, width, height)
      ctx.setFillStyle('#FFFFFF')
      ctx.fillRect(0, 0, width, height)
      const points = this.monthPoints || []
      if (points.length === 0) {
        ctx.setFillStyle('#999999')
        ctx.setFontSize(12)
        ctx.fillText('本月暂无数据', paddingLeft + 20, height / 2)
        ctx.draw(true)
        return
      }
      const amounts = points.map(p => Number(p.amount || 0))
      const maxAmount = Math.max(...amounts, 1)
      const yMax = maxAmount * 1.15
      const xCount = points.length
      const xStep = xCount > 1 ? (width - paddingLeft - paddingRight) / (xCount - 1) : 0
      const plotHeight = height - paddingTop - paddingBottom
      const plotPoints = points.map((p, i) => {
        const x = paddingLeft + xStep * i
        const y = height - paddingBottom - (Number(p.amount || 0) / yMax) * plotHeight
        const day = p.date ? p.date.split('-')[2] : ''
        return { x, y, amount: Number(p.amount || 0), xLabel: day + '日' }
      })
      ctx.setStrokeStyle('#F0F0F0')
      ctx.setLineWidth(0.5)
      ctx.beginPath()
      ctx.moveTo(paddingLeft, paddingTop)
      ctx.lineTo(paddingLeft, height - paddingBottom)
      ctx.lineTo(width - paddingRight, height - paddingBottom)
      ctx.stroke()
      ctx.setStrokeStyle('#666666')
      ctx.setLineWidth(1)
      ctx.setLineCap('round')
      ctx.setLineJoin('round')
      ctx.beginPath()
      plotPoints.forEach((pt, idx) => {
        if (idx === 0) ctx.moveTo(pt.x, pt.y)
        else ctx.lineTo(pt.x, pt.y)
      })
      ctx.stroke()
      plotPoints.forEach(pt => {
        ctx.setFillStyle('#FFEB3B')
        ctx.beginPath()
        ctx.arc(pt.x, pt.y, 3.5, 0, Math.PI * 2)
        ctx.fill()
        ctx.setStrokeStyle('#333333')
        ctx.setLineWidth(1)
        ctx.stroke()
      })
      const maxIdx = amounts.indexOf(Math.max(...amounts))
      if (plotPoints[maxIdx] && amounts[maxIdx] > 0) {
        const pt = plotPoints[maxIdx]
        ctx.setFillStyle('#333333')
        ctx.setFontSize(9)
        ctx.fillText(String(amounts[maxIdx].toFixed(2)), pt.x - 18, pt.y - 6)
      }
      ctx.setFillStyle('#666666')
      ctx.setFontSize(9)
      plotPoints.forEach((pt, i) => {
        if (i % 5 === 0 || i === plotPoints.length - 1) ctx.fillText(pt.xLabel, pt.x - 6, height - paddingBottom + 14)
      })
      ctx.draw(true)
    },
    drawYearChart() {
      const ctx = uni.createCanvasContext('yearChart', this)
      const width = this.chartWidth
      const height = this.chartHeight
      const paddingLeft = 36
      const paddingRight = 16
      const paddingTop = 28
      const paddingBottom = 36
      ctx.clearRect(0, 0, width, height)
      ctx.setFillStyle('#FFFFFF')
      ctx.fillRect(0, 0, width, height)
      const points = this.yearPoints || []
      if (points.length === 0) {
        ctx.setFillStyle('#999999')
        ctx.setFontSize(12)
        ctx.fillText('本年暂无数据', paddingLeft + 20, height / 2)
        ctx.draw(true)
        return
      }
      const amounts = points.map(p => Number(p.amount || 0))
      const maxAmount = Math.max(...amounts, 1)
      const yMax = maxAmount * 1.15
      const xStep = (width - paddingLeft - paddingRight) / (points.length - 1 || 1)
      const plotHeight = height - paddingTop - paddingBottom
      const plotPoints = points.map((p, i) => {
        const x = paddingLeft + xStep * i
        const y = height - paddingBottom - (Number(p.amount || 0) / yMax) * plotHeight
        const monthNum = p.month ? parseInt(p.month.split('-')[1], 10) : i + 1
        return { x, y, amount: Number(p.amount || 0), xLabel: monthNum + '月' }
      })
      ctx.setStrokeStyle('#F0F0F0')
      ctx.setLineWidth(0.5)
      ctx.beginPath()
      ctx.moveTo(paddingLeft, paddingTop)
      ctx.lineTo(paddingLeft, height - paddingBottom)
      ctx.lineTo(width - paddingRight, height - paddingBottom)
      ctx.stroke()
      ctx.setStrokeStyle('#666666')
      ctx.setLineWidth(1)
      ctx.setLineCap('round')
      ctx.setLineJoin('round')
      ctx.beginPath()
      plotPoints.forEach((pt, idx) => {
        if (idx === 0) ctx.moveTo(pt.x, pt.y)
        else ctx.lineTo(pt.x, pt.y)
      })
      ctx.stroke()
      plotPoints.forEach(pt => {
        ctx.setFillStyle('#FFEB3B')
        ctx.beginPath()
        ctx.arc(pt.x, pt.y, 3.5, 0, Math.PI * 2)
        ctx.fill()
        ctx.setStrokeStyle('#333333')
        ctx.setLineWidth(1)
        ctx.stroke()
      })
      const maxIdx = amounts.indexOf(Math.max(...amounts))
      if (plotPoints[maxIdx] && amounts[maxIdx] > 0) {
        const pt = plotPoints[maxIdx]
        ctx.setFillStyle('#333333')
        ctx.setFontSize(9)
        ctx.fillText(String(amounts[maxIdx].toFixed(2)), pt.x - 18, pt.y - 6)
      }
      ctx.setFillStyle('#666666')
      ctx.setFontSize(9)
      plotPoints.forEach(pt => {
        ctx.fillText(pt.xLabel, pt.x - 8, height - paddingBottom + 14)
      })
      ctx.draw(true)
    },
    getTodayStr() {
      const d = new Date()
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
    },
    formatChartDate(dateStr) {
      if (!dateStr) return ''
      const p = dateStr.split('-')
      if (p.length >= 3) return `${parseInt(p[1], 10)}-${p[2]}`
      return dateStr
    }
  }
}
</script>

<style scoped>
.chart-container {
  min-height: 100vh;
  background-color: #fff;
}

/* 顶部黄色区域：支出+下拉箭头、右侧图标 */
.header {
  background-color: #FFEB3B;
  padding: 80rpx 30rpx 24rpx;
}

.header-top {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-bottom: 28rpx;
  position: relative;
  min-height: 56rpx;
}

.header-title-wrap {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
}

.header-title {
  font-size: 36rpx;
  color: #333;
  font-weight: 500;
}

.header-arrow {
  font-size: 20rpx;
  color: #333;
}

.header-right-icon {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.06);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
}

/* 周/月/年：选中=深灰底白字，未选=黄底深灰字 */
.period-tabs {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
}

.period-tab {
  padding: 14rpx 40rpx;
  border-radius: 8rpx;
  background-color: rgba(255, 255, 255, 0.5);
}

.period-tab text {
  font-size: 28rpx;
  color: #333;
}

.period-tab.active {
  background-color: #5C5C5C;
}

.period-tab.active text {
  color: #fff;
}

/* 周选择行：白底、浅灰字、选中下划线 */
.week-row {
  background-color: #fff;
  padding: 24rpx 0 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.week-scroll {
  width: 100%;
  white-space: nowrap;
}

.week-tabs {
  display: inline-flex;
  align-items: center;
  gap: 32rpx;
  padding: 0 30rpx;
}

.week-tab-item {
  flex-shrink: 0;
  position: relative;
  padding: 8rpx 0;
}

.week-tab-item text {
  font-size: 26rpx;
  color: #999;
}

.week-tab-item.active text {
  color: #333;
}

.week-tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2rpx;
  background-color: #333;
}

/* 统计两行：总支出 / 平均值 */
.summary {
  padding: 24rpx 30rpx 16rpx;
  background-color: #fff;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.summary-line {
  font-size: 28rpx;
  color: #333;
}

.chart-wrapper {
  padding: 0 20rpx 20rpx;
}

.line-chart {
  width: 710rpx;
  height: 360rpx;
  background-color: #fff;
  border-radius: 16rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.04);
}

.ranking-section {
  margin: 0 30rpx 40rpx;
  padding: 28rpx 24rpx;
  background-color: #fff;
}

.ranking-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 24rpx;
}

.ranking-empty {
  font-size: 26rpx;
  color: #999;
  text-align: center;
  padding: 40rpx;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 32rpx;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

/* 与明细 record-item 一致：圆形、灰底、无边框 */
.ranking-icon-wrap {
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

.ranking-info {
  flex: 1;
  min-width: 0;
}

.ranking-name-pct {
  font-size: 28rpx;
  color: #333;
  display: block;
  margin-bottom: 10rpx;
}

.ranking-bar-bg {
  height: 12rpx;
  background-color: #F0F0F0;
  border-radius: 6rpx;
  overflow: hidden;
}

.ranking-bar-fill {
  height: 100%;
  background-color: #FFEB3B;
  border-radius: 6rpx;
  transition: width 0.3s ease;
}

.ranking-amount {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
  flex-shrink: 0;
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
