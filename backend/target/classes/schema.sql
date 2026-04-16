CREATE DATABASE IF NOT EXISTS personal_ledger DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE personal_ledger;

CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(255) COMMENT '头像',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `category` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    `user_id` BIGINT COMMENT '用户ID（系统预设为null）',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `type` TINYINT NOT NULL COMMENT '类型：1-收入 2-支出',
    `icon` VARCHAR(50) COMMENT '图标',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_user_id (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

CREATE TABLE IF NOT EXISTS `transaction` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '账目ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `type` TINYINT NOT NULL COMMENT '类型：1-收入 2-支出',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `remark` VARCHAR(255) COMMENT '备注',
    `transaction_date` DATE NOT NULL COMMENT '交易日期',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_user_date (`user_id`, `transaction_date`),
    INDEX idx_user_category (`user_id`, `category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账目表';

CREATE TABLE IF NOT EXISTS `budget` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '预算ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `year` INT NOT NULL COMMENT '年份',
    `month` INT NOT NULL COMMENT '月份',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '预算金额',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除',
    UNIQUE KEY uk_user_month (`user_id`, `year`, `month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预算表';

INSERT INTO `category` (`user_id`, `name`, `type`, `icon`, `sort`) VALUES
(NULL, '餐饮', 2, '🍚', 1),
(NULL, '交通', 2, '🚗', 2),
(NULL, '购物', 2, '🛒', 3),
(NULL, '娱乐', 2, '🎮', 4),
(NULL, '医疗', 2, '💊', 5),
(NULL, '教育', 2, '📚', 6),
(NULL, '住房', 2, '🏠', 7),
(NULL, '通讯', 2, '📱', 8),
(NULL, '其他支出', 2, '💰', 99),
(NULL, '工资', 1, '💵', 1),
(NULL, '奖金', 1, '🎁', 2),
(NULL, '理财', 1, '📈', 3),
(NULL, '兼职', 1, '💼', 4),
(NULL, '其他收入', 1, '⭐', 99);
