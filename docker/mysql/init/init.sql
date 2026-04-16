CREATE DATABASE IF NOT EXISTS ledger DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE ledger;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(255) COMMENT '头像',
    `email` VARCHAR(100) COMMENT '邮箱',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 分类表
CREATE TABLE IF NOT EXISTS `category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT COMMENT '用户ID，系统分类为0',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `type` TINYINT NOT NULL COMMENT '分类类型 1-支出 2-收入',
    `icon` VARCHAR(50) NOT NULL COMMENT '分类图标',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- 插入预设分类
INSERT INTO `category` (`user_id`, `name`, `type`, `icon`, `sort`) VALUES
(0, '餐饮', 1, 'Food', 1),
(0, '交通', 1, 'Van', 2),
(0, '购物', 1, 'ShoppingBag', 3),
(0, '娱乐', 1, 'Film', 4),
(0, '医疗', 1, 'FirstAidKit', 5),
(0, '教育', 1, 'BookOpen', 6),
(0, '住房', 1, 'Home', 7),
(0, '通讯', 1, 'Phone', 8),
(0, '服装', 1, 'Shirt', 9),
(0, '美容', 1, 'Scissors', 10),
(0, '工资', 2, 'Wallet', 1),
(0, '奖金', 2, 'Award', 2),
(0, '理财', 2, 'TrendingUp', 3),
(0, '兼职', 2, 'Briefcase', 4),
(0, '红包', 2, 'Gift', 5),
(0, '退款', 2, 'RefreshCw', 6),
(0, '其他收入', 2, 'Plus', 7);

-- 账目表
CREATE TABLE IF NOT EXISTS `record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `type` TINYINT NOT NULL COMMENT '类型 1-支出 2-收入',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `remark` VARCHAR(255) COMMENT '备注',
    `record_time` DATETIME NOT NULL COMMENT '记录时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_record_time` (`record_time`),
    KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账目表';

-- 预算表
CREATE TABLE IF NOT EXISTS `budget` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '预算金额',
    `year` INT NOT NULL COMMENT '年份',
    `month` INT NOT NULL COMMENT '月份',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
    UNIQUE KEY `uk_user_year_month` (`user_id`, `year`, `month`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预算表';
