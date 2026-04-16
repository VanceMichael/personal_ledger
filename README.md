# 个人记账本应用

一个功能完整的个人记账本全栈应用，支持账目管理、分类管理、预算设置和统计报表。

## 技术栈

### 后端
- Spring Boot 3.2.0
- MyBatis Plus 3.5.5
- MySQL 8.0.33
- Lombok

### 前端
- Vue 3.4 + Vite 5.0
- Element Plus 2.4
- Vue Router 4
- Axios
- ECharts 5.4

## 功能特性

- 用户模块：注册、登录、个人信息管理
- 账目管理：记录收入/支出、账目列表（支持筛选）、编辑删除
- 分类管理：预设分类、自定义分类、图标选择
- 统计报表：月度收支汇总、分类占比饼图、收支趋势折线图、年度账单
- 预算功能：月度预算设置、使用进度提醒、超支预警

## 快速开始

### 方式一：Docker Compose 部署（推荐）

```bash
docker-compose up -d
```

访问地址：
- 前端：http://localhost:3000
- 后端API：http://localhost:8080

### 方式二：本地开发

#### 1. 启动数据库

确保本地已安装 MySQL 8.0，创建数据库：

```sql
CREATE DATABASE personal_ledger CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

执行 `backend/src/main/resources/schema.sql` 初始化表结构。

#### 2. 启动后端

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

#### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端服务将在 http://localhost:3000 启动

## 项目结构

```
personal-ledger/
├── backend/                 # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/ledger/
│   │   │   │   ├── common/      # 通用类
│   │   │   │   ├── config/      # 配置类
│   │   │   │   ├── controller/  # 控制器
│   │   │   │   ├── dto/         # 数据传输对象
│   │   │   │   ├── entity/      # 实体类
│   │   │   │   ├── mapper/      # 数据访问层
│   │   │   │   └── service/     # 业务逻辑层
│   │   │   └── resources/
│   │   │       ├── mapper/      # MyBatis XML映射文件
│   │   │       ├── application.yml
│   │   │       └── schema.sql
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                # 前端项目
│   ├── src/
│   │   ├── api/             # API接口
│   │   ├── router/          # 路由配置
│   │   ├── store/           # 状态管理
│   │   ├── utils/           # 工具类
│   │   ├── views/           # 页面组件
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   ├── vite.config.js
│   ├── nginx.conf
│   └── Dockerfile
├── docker-compose.yml
└── README.md
```

## API 接口

### 用户接口
- POST /user/login - 用户登录
- POST /user/register - 用户注册
- PUT /user/update - 更新用户信息
- GET /user/{id} - 获取用户信息

### 分类接口
- GET /category/list/{userId} - 获取分类列表
- POST /category/add - 新增分类
- PUT /category/update - 更新分类
- DELETE /category/{id} - 删除分类

### 账目接口
- POST /account/list - 获取账目列表
- POST /account/add - 新增账目
- PUT /account/update - 更新账目
- DELETE /account/{id} - 删除账目
- GET /account/statistics/category - 分类统计
- GET /account/statistics/trend - 趋势统计
- GET /account/statistics/yearly - 年度统计

### 预算接口
- GET /budget/{userId}/{month} - 获取月度预算
- POST /budget/save - 保存预算

## 默认配置

### 数据库配置
- 数据库名：personal_ledger
- 用户名：ledger
- 密码：ledger123456

### 后端端口：8080
### 前端端口：3000

## 开发说明

### 后端开发
- 使用 MyBatis Plus 代码生成器生成基础代码
- 遵循 MVC 三层架构
- 使用统一响应封装 Result

### 前端开发
- 使用 Vue 3 Composition API
- 使用 Element Plus 组件库
- 使用 ECharts 进行数据可视化

## License

MIT
