// 收支类型：1-支出 2-收入（与后端、数据库一致）
export const TYPE_EXPENSE = 1
export const TYPE_INCOME = 2

// 业务状态码：统一使用常量而不是魔法数字
export const HTTP_CODE_OK = 200

// 格式化日期
export function formatDate(date, format = 'YYYY-MM-DD') {
  if (!date) return ''
  
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const minute = String(d.getMinutes()).padStart(2, '0')
  const second = String(d.getSeconds()).padStart(2, '0')
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hour)
    .replace('mm', minute)
    .replace('ss', second)
}

// 格式化金额
export function formatAmount(amount) {
  if (!amount) return '0.00'
  return parseFloat(amount).toFixed(2)
}

// 获取当前月份（YYYY-MM格式）
export function getCurrentMonth() {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  return `${year}-${month}`
}

// 格式化月份显示（03月）
export function formatMonth(month) {
  if (!month) return ''
  const parts = month.split('-')
  if (parts.length === 2) {
    return `${parts[1]}月`
  }
  return month
}

// 格式化日期显示（03月08日）
export function formatDateDisplay(date) {
  if (!date) return ''
  const d = new Date(date)
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${month}月${day}日`
}

// 分类图标：优先使用接口返回的 icon（数据库），否则按 key/名称回退（兼容旧数据）
const ICON_BY_KEY = {
  food: '🍔', transport: '🚗', shopping: '🛍️', entertainment: '🎬', medical: '🏥',
  education: '📚', housing: '🏠', communication: '📱', clothing: '👔', beauty: '💄',
  sports: '⚽', travel: '✈️', pet: '🐶', gift: '🎁', insurance: '🛡️', bill: '💳',
  repair: '🔧', investment: '📈', other: '📦', salary: '💰', bonus: '🎉',
  parttime: '💼', redpacket: '🧧'
}
const ICON_BY_NAME = {
  '餐饮': '🍔', '交通': '🚗', '购物': '🛍️', '娱乐': '🎬', '医疗': '🏥', '教育': '📚',
  '住房': '🏠', '通讯': '📱', '服饰': '👔', '美容': '💄', '运动': '⚽', '旅行': '✈️',
  '宠物': '🐶', '人情': '🎁', '保险': '🛡️', '缴费': '💳', '维修': '🔧', '投资': '📈', '其他': '📦',
  '工资': '💰', '奖金': '🎉', '投资收益': '📈', '兼职': '💼', '红包': '🧧', '礼金': '🎁'
}
export function getCategoryIconDisplay(icon, categoryName) {
  if (icon != null && icon !== '') {
    if (ICON_BY_KEY[icon]) return ICON_BY_KEY[icon]
    return icon
  }
  return (categoryName && ICON_BY_NAME[categoryName]) || '📝'
}
