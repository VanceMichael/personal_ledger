# 个人记账本应用

一个功能完整的全栈个人记账应用，帮助您轻松管理个人财务。

## 功能特性

### 用户模块
- 用户注册、登录
- 个人信息管理（昵称、邮箱、手机号）
- 密码修改

### 账目管理
- 记录收入/支出（金额、分类、备注、日期）
- 账目列表展示与分页
- 按日期范围、分类、类型筛选
- 编辑和删除账目

### 分类管理
- 系统预设分类（餐饮、交通、购物、工资、理财等）
- 用户自定义分类
- 分类图标选择（emoji）

### 统计报表
- 月度收支汇总卡片
- 分类占比饼图
- 收支趋势折线图
- 年度账单柱状图

### 预算功能
- 设置月度预算
- 预算使用进度展示
- 超支预警（正常/预警/超支三级状态）

## 技术栈

### 后端
- **框架**: Spring Boot 3.2.0
- **ORM**: MyBatis Plus 3.5.5
- **数据库**: MySQL 8.0
- **认证**: JWT 0.12.3
- **构建工具**: Maven

### 前端
- **框架**: Vue 3.4（Composition API）
- **构建工具**: Vite 5.0
- **路由**: Vue Router 4.2
- **状态管理**: Pinia 2.1
- **UI组件**: Element Plus 2.5
- **图表**: ECharts 5.4
- **HTTP**: Axios 1.6
- **日期处理**: Day.js 1.11

### 部署
- Docker & Docker Compose
- Nginx 反向代理

## 快速开始

### 方式一：Docker Compose 部署（推荐）

1. 确保已安装 Docker 和 Docker Compose

2. 在项目根目录执行：
```bash
docker-compose up -d
```

3. 访问应用：
   - 前端: http://localhost
   - 后端API: http://localhost/api
   - MySQL: localhost:3306

4. 停止服务：
```bash
docker-compose down
```

### 方式二：本地开发运行

#### 后端启动

1. 创建 MySQL 数据库：
```sql
CREATE DATABASE personal_ledger DEFAULT CHARACTER SET utf8mb4;
```

2. 修改 `backend/src/main/resources/application.yml` 中的数据库配置

3. 进入后端目录并启动：
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 http://localhost:8080/api 启动

#### 前端启动

1. 进入前端目录：
```bash
cd frontend
```

2. 安装依赖：
```bash
npm install
```

3. 启动开发服务器：
```bash
npm run dev
```

前端将在 http://localhost:3000 启动

## 项目结构

```
.
├── backend/                 # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/personal/ledger/
│   │   │   │   ├── common/      # 公共类（响应封装、异常处理）
│   │   │   │   ├── config/      # 配置类
│   │   │   │   ├── controller/  # 控制器
│   │   │   │   ├── dto/         # 数据传输对象
│   │   │   │   ├── entity/      # 实体类
│   │   │   │   ├── interceptor/ # 拦截器
│   │   │   │   ├── mapper/      # 数据访问层
│   │   │   │   ├── service/     # 业务逻辑层
│   │   │   │   └── util/        # 工具类
│   │   │   └── resources/
│   │   │       ├── mapper/      # MyBatis XML映射文件
│   │   │       ├── application.yml
│   │   │       └── schema.sql   # 数据库初始化脚本
│   ├── Dockerfile
│   └── pom.xml
├── frontend/               # 前端项目
│   ├── src/
│   │   ├── router/         # 路由配置
│   │   ├── stores/         # Pinia状态管理
│   │   ├── utils/          # 工具函数
│   │   ├── views/          # 页面组件
│   │   ├── App.vue
│   │   └── main.js
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   └── vite.config.js
├── docker-compose.yml      # Docker编排配置
└── README.md
```

## API 接口说明

### 用户相关
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `GET /api/user/info` - 获取用户信息
- `PUT /api/user/update` - 更新用户信息
- `PUT /api/user/password` - 修改密码

### 账目相关
- `GET /api/transaction` - 获取账目列表
- `POST /api/transaction` - 添加账目
- `PUT /api/transaction/{id}` - 更新账目
- `DELETE /api/transaction/{id}` - 删除账目

### 分类相关
- `GET /api/category` - 获取分类列表
- `POST /api/category` - 添加分类
- `PUT /api/category/{id}` - 更新分类
- `DELETE /api/category/{id}` - 删除分类

### 统计相关
- `GET /api/statistics/monthly` - 月度统计
- `GET /api/statistics/category` - 分类统计
- `GET /api/statistics/trend` - 趋势统计
- `GET /api/statistics/annual` - 年度统计
- `GET /api/statistics/dashboard` - 首页统计数据

### 预算相关
- `POST /api/budget` - 设置预算
- `GET /api/budget/progress` - 获取预算进度

## 默认数据

系统初始化时会创建以下预设分类：

**支出分类**: 餐饮 🍚、交通 🚗、购物 🛒、娱乐 🎮、医疗 💊、教育 📚、住房 🏠、通讯 📱、其他支出 💰

**收入分类**: 工资 💵、奖金 🎁、理财 📈、兼职 💼、其他收入 ⭐

## 开发说明

### 后端开发
- 使用 MyBatis Plus 简化 CRUD 操作
- JWT 令牌认证，拦截器统一处理权限验证
- 统一异常处理和响应封装
- ThreadLocal 存储当前用户上下文

### 前端开发
- Vue 3 Composition API 开发
- Pinia 管理用户状态
- Element Plus 组件库构建界面
- ECharts 实现数据可视化
- Axios 封装 HTTP 请求，统一处理响应

## 许可证

MIT License
