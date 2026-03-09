// 环境配置入口文件
// 通过修改此文件中的 ENV 变量来切换环境
// 可选值: 'test' | 'prod'
// 默认使用 test 环境

// ========== 环境配置 ==========
// 修改此处的值来切换环境: 'test' 或 'prod'
const ENV = 'test'
// ==============================

import testConfig from './test.js'
import prodConfig from './prod.js'

let config

if (ENV === 'prod') {
  config = prodConfig
} else {
  // 默认使用 test 环境
  config = testConfig
}

console.log('当前环境:', config.env)
console.log('API 地址:', config.baseUrl)

export default config
