# 个人记账本应用

一个功能完整的个人财务管理系统，支持用户记账、分类管理、统计分析、预算控制等功能。

## 技术栈

### 后端
- Spring Boot 3.2.x
- MyBatis Plus 3.5.x
- MySQL 8.0
- Maven 3.9.x

### 前端
- Vue 3.x
- Element Plus
- ECharts
- Vite
- Pinia

## 功能特性

### 用户模块
- ✅ 用户注册、登录
- ✅ 个人信息管理（昵称、头像、邮箱、密码修改）

### 账目管理
- ✅ 记录收入/支出：金额、分类、备注、日期
- ✅ 账目列表：支持按日期范围、分类筛选
- ✅ 编辑、删除账目
- ✅ 分页查询

### 分类管理
- ✅ 预设分类：餐饮、交通、购物、工资、理财等17种常用分类
- ✅ 自定义分类：用户可添加自己的分类
- ✅ 分类图标选择，支持所有Element Plus图标
- ✅ 分类排序

### 统计报表
- ✅ 月度收支汇总
- ✅ 分类占比饼图
- ✅ 收支趋势折线图
- ✅ 年度账单统计
- ✅ 预算使用进度展示

### 预算功能
- ✅ 设置月度预算
- ✅ 预算使用进度提醒
- ✅ 超支预警（90%提醒，100%超支警告）

## 快速启动

### 环境要求
- Docker
- Docker Compose

### 启动命令
```bash
# 进入项目根目录
cd personal_ledger

# 启动所有服务
docker-compose up -d
```

### 访问地址
- 前端应用：http://localhost:3000
- 后端接口：http://localhost:8080/api
- MySQL数据库：localhost:3306 账号：root 密码：123456

### 停止服务
```bash
# 停止服务
docker-compose down

# 停止并删除数据（慎用）
docker-compose down -v
```

## 项目结构

```
personal_ledger/
├── backend/                 # 后端项目
│   ├── src/main/java/com/personal/ledger/
│   │   ├── config/         # 配置类
│   │   ├── controller/     # 控制器层
│   │   ├── entity/         # 实体类
│   │   ├── mapper/         # 数据访问层
│   │   ├── service/        # 业务逻辑层
│   │   ├── dto/            # 数据传输对象
│   │   ├── vo/             # 视图对象
│   │   └── utils/          # 工具类
│   ├── src/main/resources/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/               # 前端项目
│   ├── src/
│   │   ├── views/          # 页面组件
│   │   ├── components/     # 公共组件
│   │   ├── router/         # 路由配置
│   │   ├── store/          # 状态管理
│   │   ├── api/            # API接口
│   │   ├── utils/          # 工具函数
│   │   └── assets/         # 静态资源
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── vite.config.js
│   └── package.json
├── docker/                 # Docker相关配置
│   └── mysql/
│       ├── init/           # 数据库初始化脚本
│       └── data/           # MySQL数据持久化目录
└── docker-compose.yml      # Docker Compose配置文件
```

## API 接口文档

### 用户相关
- `POST /api/user/register` 用户注册
- `POST /api/user/login` 用户登录
- `PUT /api/user/{id}` 更新用户信息
- `GET /api/user/{id}` 获取用户信息

### 账目相关
- `POST /api/record` 添加账目
- `PUT /api/record/{id}` 更新账目
- `DELETE /api/record/{id}` 删除账目
- `GET /api/record/page` 分页查询账目列表
- `GET /api/record/list` 查询所有账目列表

### 分类相关
- `GET /api/category/list` 获取分类列表
- `POST /api/category` 添加分类
- `PUT /api/category/{id}` 更新分类
- `DELETE /api/category/{id}` 删除分类

### 统计相关
- `POST /api/statistic/budget` 设置预算
- `GET /api/statistic/budget` 获取预算
- `GET /api/statistic/month` 获取月度统计
- `GET /api/statistic/year` 获取年度统计

## 开发说明

### 后端开发
```bash
cd backend
# 配置本地数据库连接
# 修改src/main/resources/application.yml中的数据库配置
mvn spring-boot:run
```

### 前端开发
```bash
cd frontend
npm install
npm run dev
```

## 数据库设计

### 用户表(user)
存储用户基本信息，包含用户名、密码、昵称、邮箱等。

### 分类表(category)
存储收支分类，包含系统预设分类和用户自定义分类，类型分为支出和收入。

### 账目表(record)
存储每一笔收支记录，关联用户和分类，包含金额、备注、记录时间等。

### 预算表(budget)
存储用户每月的预算设置，关联用户、年份、月份，金额。
