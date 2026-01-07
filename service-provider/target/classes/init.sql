-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `nickname` VARCHAR(100) DEFAULT NULL COMMENT '昵称',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `bio` VARCHAR(500) DEFAULT NULL COMMENT '个人简介',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建碳配额表 (Moved to service-assets)
-- CREATE TABLE IF NOT EXISTS `carbon_quota` ...

-- 创建碳信用(CCER)表 (Moved to service-assets)
-- CREATE TABLE IF NOT EXISTS `carbon_credit` ...
VALUES (1, '塞罕坝林业碳汇项目', '林业', 2000.00, 0, '2024-08-15');

-- 创建企业信息表
CREATE TABLE IF NOT EXISTS `company_info` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `company_name` VARCHAR(200) NOT NULL COMMENT '企业全称',
  `credit_code` VARCHAR(50) NOT NULL COMMENT '统一社会信用代码',
  `legal_person` VARCHAR(50) NOT NULL COMMENT '法人代表',
  `address` VARCHAR(200) DEFAULT NULL COMMENT '企业地址',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `license_url` VARCHAR(500) DEFAULT NULL COMMENT '营业执照URL',
  `registered_capital` VARCHAR(50) DEFAULT NULL COMMENT '注册资本',
  `establish_date` DATETIME DEFAULT NULL COMMENT '成立日期',
  `business_scope` TEXT DEFAULT NULL COMMENT '经营范围',
  `status` INT(1) DEFAULT 0 COMMENT '认证状态：0-未认证，1-审核中，2-已认证，3-已驳回',
  `reject_reason` VARCHAR(500) DEFAULT NULL COMMENT '驳回原因',
  `reviewer` VARCHAR(50) DEFAULT NULL COMMENT '审核人',
  `review_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_credit_code` (`credit_code`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业信息表';

-- 插入测试数据
INSERT INTO `company_info` (`id`, `user_id`, `company_name`, `credit_code`, `legal_person`, `address`, `phone`, `license_url`, `registered_capital`, `establish_date`, `business_scope`, `status`, `reviewer`, `review_time`)
VALUES 
(1, 1, '北京绿色能源科技有限公司', '91110000MA01ABC123', '张三', '北京市朝阳区建国路88号', '010-12345678', 'https://example.com/license/001.jpg', '5000万元', '2018-06-15 00:00:00', '新能源技术开发、碳资产管理咨询、节能设备销售', 2, '管理员', '2024-01-10 14:30:00'),
(2, 2, '上海碳资产管理有限公司', '91310000MA02DEF456', '李四', '上海市浦东新区世纪大道1000号', '021-87654321', 'https://example.com/license/002.jpg', '3000万元', '2019-03-20 00:00:00', '碳资产管理、碳交易咨询服务、环保技术咨询', 2, '管理员', '2024-02-15 10:20:00'),
(3, 3, '广东新能源开发集团', '91440000MA03GHI789', '王五', '广州市天河区珠江新城', '020-11112222', 'https://example.com/license/003.jpg', '10000万元', '2017-09-01 00:00:00', '可再生能源项目开发、碳汇造林、节能减排技术服务', 1, NULL, NULL),
(4, 4, '四川林业碳汇开发公司', '91510000MA04JKL012', '赵六', '成都市高新区天府大道中段', '028-99998888', NULL, '2000万元', '2020-11-10 00:00:00', '林业碳汇项目开发、森林资源管理、生态修复工程', 0, NULL, NULL);

