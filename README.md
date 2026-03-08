# 蚂蚁记账微信小程序

基于Spring Boot和uni-app开发的微信小程序记账应用。

## 项目结构

```
mayi_book/
├── backend/          # 后端Spring Boot项目
├── frontend/         # 前端uni-app项目
├── 数据库设计.sql    # 数据库设计文件
├── 技术方案设计.md   # 技术方案文档
└── README.md         # 项目说明
```

## 技术栈

### 后端
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.3.1
- MySQL 8.0
- Druid 连接池
- JWT 认证
- 端口：9091

### 前端
- uni-app
- Vue 3
- 微信小程序

## 快速开始

### 1. 数据库准备

1. 创建MySQL数据库
2. 执行 `数据库设计.sql` 文件创建表结构和初始化数据

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS `mayi_book` 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

-- 执行数据库设计.sql文件
```

### 2. 后端启动

1. 进入后端目录
```bash
cd backend
```

2. 修改数据库配置
编辑 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mayi_book?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password  # 修改为你的数据库密码
```

3. 修改微信小程序配置
编辑 `src/main/resources/application.yml`：
```yaml
wechat:
  appid: your-appid      # 修改为你的小程序AppID
  secret: your-secret    # 修改为你的小程序AppSecret
```

4. 编译运行
```bash
mvn clean package
java -jar target/mayi-book-1.0.0.jar
```

或者使用IDE直接运行 `MayiBookApplication.java`

### 3. 前端启动

1. 进入前端目录
```bash
cd frontend
```

2. 安装依赖（如果需要）
```bash
npm install
```

3. 使用HBuilderX或微信开发者工具打开项目
   - 在HBuilderX中：文件 -> 打开目录 -> 选择frontend目录
   - 在微信开发者工具中：导入项目 -> 选择frontend目录

4. 修改API地址
编辑 `utils/request.js`：
```javascript
const BASE_URL = 'http://localhost:9091/api'  // 修改为你的后端地址
```

5. 配置小程序AppID
编辑 `manifest.json`，在 `mp-weixin` 节点下配置：
```json
"mp-weixin": {
  "appid": "your-appid"  // 修改为你的小程序AppID
}
```

6. 编译运行
   - HBuilderX：运行 -> 运行到小程序模拟器 -> 微信开发者工具
   - 微信开发者工具：点击编译按钮

## API接口

### 用户相关
- `POST /api/user/login` - 微信登录
- `GET /api/user/info` - 获取用户信息

### 分类相关
- `GET /api/category/expense` - 获取支出分类列表
- `GET /api/category/income` - 获取收入分类列表

### 记账相关
- `POST /api/record/add` - 添加记账记录
- `GET /api/record/list` - 获取记账明细列表
- `GET /api/record/statistics` - 获取月度统计

## 功能特性

1. **微信授权登录** - 使用微信小程序授权登录
2. **记账功能** - 支持支出和收入记账
3. **分类管理** - 预置支出和收入分类
4. **明细查询** - 按天分组展示记账明细
5. **月度统计** - 显示月度收支统计
6. **逻辑删除** - 所有表支持逻辑删除

## 注意事项

1. 确保MySQL数据库已启动
2. 确保后端服务运行在9091端口
3. 前端需要配置正确的后端API地址
4. 微信小程序需要配置正确的AppID和AppSecret
5. 开发环境需要关闭小程序的域名校验

## 开发规范

- 后端遵循阿里巴巴Java开发手册
- 前端遵循uni-app开发规范
- 统一使用UTF-8编码
- 统一响应格式：`{code, message, data}`

## 后续扩展

- 搜索功能
- 日历功能
- 统计图表功能
- 账单、预算、资产管家功能
- 分类设置功能
