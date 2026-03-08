-- ============================================
-- 蚂蚁记账微信小程序数据库设计
-- 数据库名: mayi_book
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `mayi_book` 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

USE `mayi_book`;

-- ============================================
-- 1. 用户表
-- ============================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `openid` VARCHAR(100) NOT NULL COMMENT '微信用户唯一标识',
  `nick_name` VARCHAR(100) DEFAULT NULL COMMENT '用户昵称',
  `avatar_url` VARCHAR(500) DEFAULT NULL COMMENT '用户头像URL',
  `del_flag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 2. 记账分类表
-- ============================================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `type` VARCHAR(20) NOT NULL COMMENT '分类类型: expense-支出, income-收入',
  `icon` VARCHAR(100) DEFAULT NULL COMMENT '分类图标（图标名称或URL）',
  `sort_order` INT(11) DEFAULT 0 COMMENT '排序顺序',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
  `del_flag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='记账分类表';

-- 初始化支出分类数据（19个分类，排除"设置"分类）
INSERT INTO `category` (`name`, `type`, `icon`, `sort_order`) VALUES
('餐饮', 'expense', 'food', 1),
('交通', 'expense', 'transport', 2),
('购物', 'expense', 'shopping', 3),
('娱乐', 'expense', 'entertainment', 4),
('医疗', 'expense', 'medical', 5),
('教育', 'expense', 'education', 6),
('住房', 'expense', 'housing', 7),
('通讯', 'expense', 'communication', 8),
('服饰', 'expense', 'clothing', 9),
('美容', 'expense', 'beauty', 10),
('运动', 'expense', 'sports', 11),
('旅行', 'expense', 'travel', 12),
('宠物', 'expense', 'pet', 13),
('人情', 'expense', 'gift', 14),
('保险', 'expense', 'insurance', 15),
('缴费', 'expense', 'bill', 16),
('维修', 'expense', 'repair', 17),
('投资', 'expense', 'investment', 18),
('其他', 'expense', 'other', 19);

-- 初始化收入分类数据
INSERT INTO `category` (`name`, `type`, `icon`, `sort_order`) VALUES
('工资', 'income', 'salary', 1),
('奖金', 'income', 'bonus', 2),
('投资收益', 'income', 'investment', 3),
('兼职', 'income', 'parttime', 4),
('红包', 'income', 'redpacket', 5),
('礼金', 'income', 'gift', 6),
('其他', 'income', 'other', 7);

-- ============================================
-- 3. 记账记录表
-- ============================================
DROP TABLE IF EXISTS `record`;
CREATE TABLE `record` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `category_id` BIGINT(20) NOT NULL COMMENT '分类ID',
  `type` VARCHAR(20) NOT NULL COMMENT '类型: expense-支出, income-收入',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `record_date` DATE NOT NULL COMMENT '记账日期',
  `del_flag` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_type` (`type`),
  KEY `idx_record_date` (`record_date`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_del_flag` (`del_flag`),
  CONSTRAINT `fk_record_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_record_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='记账记录表';

-- ============================================
-- 4. 索引优化说明
-- ============================================
-- user表:
--   - uk_openid: 唯一索引，快速查找用户
--   - idx_create_time: 按创建时间查询
--   - idx_del_flag: 逻辑删除标识索引

-- category表:
--   - idx_type: 按类型查询（支出/收入）
--   - idx_sort_order: 按排序顺序查询
--   - idx_del_flag: 逻辑删除标识索引

-- record表:
--   - idx_user_id: 按用户查询（最常用）
--   - idx_category_id: 按分类查询
--   - idx_type: 按类型查询
--   - idx_record_date: 按日期查询（用于按天分组）
--   - idx_create_time: 按创建时间排序（用于时间倒序）
--   - idx_del_flag: 逻辑删除标识索引

-- ============================================
-- 5. 常用查询SQL示例
-- ============================================

-- 查询用户某月的记账明细（按天分组，时间倒序）
-- SELECT 
--   DATE(r.record_date) as date,
--   DAYNAME(r.record_date) as day_of_week,
--   SUM(CASE WHEN r.type = 'expense' THEN r.amount ELSE 0 END) as total_expense,
--   SUM(CASE WHEN r.type = 'income' THEN r.amount ELSE 0 END) as total_income,
--   GROUP_CONCAT(
--     JSON_OBJECT(
--       'id', r.id,
--       'categoryName', c.name,
--       'amount', r.amount,
--       'remark', r.remark,
--       'createTime', r.create_time
--     )
--   ) as records
-- FROM record r
-- LEFT JOIN category c ON r.category_id = c.id
-- WHERE r.user_id = ? 
--   AND DATE_FORMAT(r.record_date, '%Y-%m') = ?
--   AND r.del_flag = 0
--   AND c.del_flag = 0
-- GROUP BY DATE(r.record_date)
-- ORDER BY r.record_date DESC, r.create_time DESC;

-- 查询用户某月的收支统计
-- SELECT 
--   SUM(CASE WHEN type = 'expense' THEN amount ELSE 0 END) as total_expense,
--   SUM(CASE WHEN type = 'income' THEN amount ELSE 0 END) as total_income,
--   SUM(CASE WHEN type = 'income' THEN amount ELSE 0 END) - 
--   SUM(CASE WHEN type = 'expense' THEN amount ELSE 0 END) as balance
-- FROM record
-- WHERE user_id = ? 
--   AND DATE_FORMAT(record_date, '%Y-%m') = ?
--   AND del_flag = 0;

-- ============================================
-- 6. 数据字典
-- ============================================

-- category.type 枚举值:
--   - expense: 支出
--   - income: 收入

-- record.type 枚举值:
--   - expense: 支出
--   - income: 收入

-- category.status 枚举值:
--   - 1: 启用
--   - 0: 禁用

-- del_flag 逻辑删除标识（所有表通用）:
--   - 0: 未删除
--   - 1: 已删除
-- 注意：所有查询都需要添加 del_flag = 0 条件，确保只查询未删除的数据
