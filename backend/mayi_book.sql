/*
 Navicat Premium Data Transfer

 Source Server         : 泰享 测试环境
 Source Server Type    : MySQL
 Source Server Version : 50744
 Source Host           : mysql-test.txjk.space:3306
 Source Schema         : mayi_book

 Target Server Type    : MySQL
 Target Server Version : 50744
 File Encoding         : 65001

 Date: 08/03/2026 21:04:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `type` tinyint(1) NOT NULL COMMENT '分类类型: 1-支出, 2-收入',
  `icon` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类图标（存 emoji 或图标标识，展示时按此字段显示）',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序顺序',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态: 1-启用, 0-禁用',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='记账分类表';

-- ----------------------------
-- Records of category
-- ----------------------------
BEGIN;
INSERT INTO `category` VALUES (1, '餐饮', 1, '🍔', 1, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (2, '交通', 1, '🚗', 2, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (3, '购物', 1, '🛍️', 3, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (4, '娱乐', 1, '🎬', 4, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (5, '医疗', 1, '🏥', 5, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (6, '教育', 1, '📚', 6, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (7, '住房', 1, '🏠', 7, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (8, '通讯', 1, '📱', 8, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (9, '服饰', 1, '👔', 9, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (10, '美容', 1, '💄', 10, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (11, '运动', 1, '⚽', 11, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (12, '旅行', 1, '✈️', 12, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (13, '宠物', 1, '🐶', 13, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (14, '人情', 1, '🎁', 14, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (15, '保险', 1, '🛡️', 15, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (16, '缴费', 1, '💳', 16, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (17, '维修', 1, '🔧', 17, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (18, '投资', 1, '📈', 18, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (19, '其他', 1, '📦', 19, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (20, '工资', 2, '💰', 1, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (21, '奖金', 2, '🎉', 2, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (22, '投资收益', 2, '📈', 3, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (23, '兼职', 2, '💼', 4, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (24, '红包', 2, '🧧', 5, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (25, '礼金', 2, '🎁', 6, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
INSERT INTO `category` VALUES (26, '其他', 2, '📦', 7, 1, 0, '2026-03-08 15:04:17', '2026-03-08 15:04:17');
COMMIT;

-- ----------------------------
-- Table structure for record
-- ----------------------------
DROP TABLE IF EXISTS `record`;
CREATE TABLE `record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `type` tinyint(1) NOT NULL COMMENT '类型: 1-支出, 2-收入',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `record_date` date NOT NULL COMMENT '记账日期',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_type` (`type`),
  KEY `idx_record_date` (`record_date`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_del_flag` (`del_flag`),
  CONSTRAINT `fk_record_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`),
  CONSTRAINT `fk_record_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='记账记录表';

-- ----------------------------
-- Records of record
-- ----------------------------
BEGIN;
INSERT INTO `record` VALUES (1, 3, 1, 1, 19.00, '', '2026-02-08', 0, '2026-03-08 16:26:02', '2026-03-08 20:20:39');
INSERT INTO `record` VALUES (2, 3, 2, 1, 12.00, '', '2026-02-08', 0, '2026-03-08 16:26:31', '2026-03-08 20:20:43');
INSERT INTO `record` VALUES (3, 3, 9, 1, 85.00, '鞋子', '2026-03-08', 0, '2026-03-08 16:27:14', '2026-03-08 16:27:14');
INSERT INTO `record` VALUES (4, 3, 20, 2, 1222.00, '', '2026-03-08', 0, '2026-03-08 16:46:08', '2026-03-08 16:46:08');
INSERT INTO `record` VALUES (5, 3, 10, 1, 2.00, '', '2026-03-08', 0, '2026-03-08 17:01:10', '2026-03-08 17:01:10');
INSERT INTO `record` VALUES (6, 3, 7, 2, 22.00, '', '2026-02-08', 0, '2026-03-08 17:04:12', '2026-03-08 20:20:57');
INSERT INTO `record` VALUES (7, 3, 7, 1, 22.00, '', '2026-03-08', 0, '2026-03-08 17:13:43', '2026-03-08 17:13:43');
INSERT INTO `record` VALUES (8, 3, 2, 1, 22.00, '', '2026-03-08', 0, '2026-03-08 17:15:03', '2026-03-08 17:15:03');
INSERT INTO `record` VALUES (9, 3, 18, 1, 6.00, '', '2026-03-08', 0, '2026-03-08 17:16:34', '2026-03-08 17:16:34');
INSERT INTO `record` VALUES (10, 3, 14, 1, 6.00, '', '2026-03-08', 0, '2026-03-08 17:16:40', '2026-03-08 17:16:40');
INSERT INTO `record` VALUES (11, 3, 18, 1, 333.00, '', '2026-03-08', 0, '2026-03-08 18:07:14', '2026-03-08 18:07:14');
INSERT INTO `record` VALUES (12, 3, 1, 1, 35.00, '', '2026-03-08', 0, '2026-03-08 20:20:00', '2026-03-08 20:20:00');
COMMIT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `openid` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '微信用户唯一标识',
  `nick_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户昵称',
  `avatar_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户头像URL',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` VALUES (3, 'otprE5E4MQ-rcwo2W4BwMl4Y_dW4', '大帅', 'http://tmp/uHtGmuB7BeVTbf3bce39270dbc4c94a7b801c431774f.jpeg', 0, '2026-03-08 16:13:52', '2026-03-08 20:23:58');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
