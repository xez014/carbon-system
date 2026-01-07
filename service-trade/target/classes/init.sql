-- 交易订单表
CREATE TABLE IF NOT EXISTS `trade_order` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `item_type` VARCHAR(20) NOT NULL COMMENT '商品类型: QUOTA, CREDIT',
  `item_id` BIGINT(20) DEFAULT NULL COMMENT '商品ID',
  `direction` VARCHAR(10) NOT NULL COMMENT '方向: BUY, SELL',
  `price` DECIMAL(20, 2) NOT NULL COMMENT '单价',
  `quantity` DECIMAL(20, 2) NOT NULL COMMENT '数量',
  `traded_quantity` DECIMAL(20, 2) DEFAULT 0.00 COMMENT '已成交数量',
  `status` VARCHAR(20) DEFAULT 'OPEN' COMMENT '状态: OPEN, CLOSED, CANCELLED',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳交易订单表';

-- 插入一条测试挂单
INSERT INTO `trade_order` (`user_id`, `item_type`, `item_id`, `direction`, `price`, `quantity`, `status`)
VALUES (1, 'CREDIT', NULL, 'SELL', 50.00, 100.00, 'OPEN');

-- 交易账户表
CREATE TABLE IF NOT EXISTS `trade_account` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `balance` DECIMAL(20, 2) DEFAULT 0.00 COMMENT '账户余额（可用资金）',
  `frozen_amount` DECIMAL(20, 2) DEFAULT 0.00 COMMENT '冻结金额（挂单占用）',
  `total_assets` DECIMAL(20, 2) DEFAULT 0.00 COMMENT '总资产（余额+冻结）',
  `total_recharge` DECIMAL(20, 2) DEFAULT 0.00 COMMENT '累计充值',
  `total_withdraw` DECIMAL(20, 2) DEFAULT 0.00 COMMENT '累计提现',
  `status` INT(1) DEFAULT 0 COMMENT '账户状态：0-正常，1-冻结，2-注销',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易账户表';

-- 插入测试账户数据
INSERT INTO `trade_account` (`user_id`, `balance`, `frozen_amount`, `total_assets`, `total_recharge`, `total_withdraw`, `status`)
VALUES 
(1, 10000.00, 5000.00, 15000.00, 20000.00, 5000.00, 0),
(2, 50000.00, 0.00, 50000.00, 50000.00, 0.00, 0),
(3, 8000.00, 2000.00, 10000.00, 15000.00, 5000.00, 0);

-- 询报价单表
CREATE TABLE IF NOT EXISTS `quote_order` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `type` VARCHAR(10) NOT NULL COMMENT '订单类型：BUY-询价买入，SELL-询价卖出',
  `asset_type` VARCHAR(20) NOT NULL COMMENT '资产类型：QUOTA-配额，CREDIT-信用',
  `quantity` DECIMAL(20, 2) NOT NULL COMMENT '数量（吨）',
  `expect_price` DECIMAL(20, 2) NOT NULL COMMENT '期望价格（元/吨）',
  `final_price` DECIMAL(20, 2) DEFAULT NULL COMMENT '最终成交价格（元/吨）',
  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-待报价，QUOTED-已报价，ACCEPTED-已接受，REJECTED-已拒绝，CANCELLED-已取消，COMPLETED-已完成',
  `valid_until` DATETIME DEFAULT NULL COMMENT '有效期至',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_valid_until` (`valid_until`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='询报价单表';

-- 插入测试询报价数据
INSERT INTO `quote_order` (`user_id`, `type`, `asset_type`, `quantity`, `expect_price`, `final_price`, `status`, `valid_until`, `remark`)
VALUES 
(1, 'BUY', 'QUOTA', 1000.00, 45.00, NULL, 'PENDING', DATE_ADD(NOW(), INTERVAL 7 DAY), '急需购买配额1000吨'),
(2, 'SELL', 'CREDIT', 500.00, 55.00, NULL, 'PENDING', DATE_ADD(NOW(), INTERVAL 5 DAY), '出售CCER信用500吨'),
(3, 'BUY', 'CREDIT', 200.00, 50.00, 52.00, 'QUOTED', DATE_ADD(NOW(), INTERVAL 3 DAY), '小量购买信用'),
(1, 'SELL', 'QUOTA', 300.00, 48.00, 48.50, 'ACCEPTED', DATE_ADD(NOW(), INTERVAL 10 DAY), '配额出售已接受'),
(2, 'BUY', 'QUOTA', 800.00, 46.00, NULL, 'CANCELLED', DATE_ADD(NOW(), INTERVAL -1 DAY), '已取消的询价单');

