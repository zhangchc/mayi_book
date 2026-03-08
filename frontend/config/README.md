# 前端环境配置说明

## 环境配置

本项目支持多环境配置，参考后端项目的分环境方式。

### 环境类型

- **test**: 测试环境（默认）
- **prod**: 生产环境

### 配置文件

- `config/test.js` - 测试环境配置
- `config/prod.js` - 生产环境配置
- `config/index.js` - 环境配置入口文件

### 切换环境

在 `config/index.js` 文件中修改 `ENV` 变量的值：

```javascript
// 修改此处的值来切换环境: 'test' 或 'prod'
const ENV = 'test'  // 改为 'prod' 切换到生产环境
```

### 环境地址配置

#### 测试环境 (test)
- API 地址: `http://localhost:9091/api`

#### 生产环境 (prod)
- API 地址: `http://8.153.192.39:9091/api`

### 使用方式

配置文件会自动被 `utils/request.js` 引用，无需手动修改其他文件。

### 注意事项

1. 切换环境后需要重新编译小程序
2. 确保对应环境的后端服务已启动
3. 生产环境部署前，请确认 `config/index.js` 中的 `ENV` 设置为 `'prod'`
