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
  PRIMARY KEY (`id`),
  KEY `idx_item_type` (`item_type`),
  KEY `idx_direction` (`direction`),
  KEY `idx_status` (`status`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='碳交易订单表';

-- 插入测试挂单
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

-- 行情表（实时行情）
CREATE TABLE IF NOT EXISTS `market_quote` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `asset_type` VARCHAR(20) NOT NULL COMMENT '资产类型：QUOTA-配额，CREDIT-信用',
  `last_price` DECIMAL(20, 2) NOT NULL COMMENT '最新成交价',
  `open_price` DECIMAL(20, 2) NOT NULL COMMENT '开盘价',
  `close_price` DECIMAL(20, 2) NOT NULL COMMENT '收盘价（昨日）',
  `high_price` DECIMAL(20, 2) NOT NULL COMMENT '最高价',
  `low_price` DECIMAL(20, 2) NOT NULL COMMENT '最低价',
  `volume` DECIMAL(20, 2) DEFAULT 0.00 COMMENT '成交量（吨）',
  `amount` DECIMAL(20, 2) DEFAULT 0.00 COMMENT '成交额（元）',
  `change` DECIMAL(20, 2) NOT NULL COMMENT '涨跌额',
  `change_rate` DECIMAL(10, 4) NOT NULL COMMENT '涨跌幅',
  `bid_price` DECIMAL(20, 2) NOT NULL COMMENT '买一价',
  `bid_quantity` DECIMAL(20, 2) DEFAULT 0.00 COMMENT '买一量',
  `ask_price` DECIMAL(20, 2) NOT NULL COMMENT '卖一价',
  `ask_quantity` DECIMAL(20, 2) DEFAULT 0.00 COMMENT '卖一量',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_asset_type` (`asset_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实时行情表';

-- 插入初始行情数据
INSERT INTO `market_quote` (`asset_type`, `last_price`, `open_price`, `close_price`, `high_price`, `low_price`, `volume`, `amount`, `change`, `change_rate`, `bid_price`, `bid_quantity`, `ask_price`, `ask_quantity`)
VALUES 
('QUOTA', 48.50, 45.00, 46.00, 50.00, 44.00, 2500.00, 121250.00, 2.50, 5.4348, 48.00, 300.00, 49.00, 200.00),
('CREDIT', 52.00, 50.00, 51.00, 55.00, 49.00, 1800.00, 93600.00, 1.00, 1.9608, 51.50, 500.00, 52.50, 300.00);

-- 成交记录表
CREATE TABLE IF NOT EXISTS `trade_execution` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` BIGINT(20) NOT NULL COMMENT '订单ID',
  `counter_order_id` BIGINT(20) DEFAULT NULL COMMENT '对手方订单ID',
  `asset_type` VARCHAR(20) NOT NULL COMMENT '资产类型：QUOTA-配额，CREDIT-信用',
  `price` DECIMAL(20, 2) NOT NULL COMMENT '成交价格',
  `quantity` DECIMAL(20, 2) NOT NULL COMMENT '成交数量',
  `amount` DECIMAL(20, 2) NOT NULL COMMENT '成交金额',
  `direction` VARCHAR(10) NOT NULL COMMENT '交易方向：BUY-买入，SELL-卖出',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '成交时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_counter_order_id` (`counter_order_id`),
  KEY `idx_asset_type` (`asset_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成交记录表';

-- 历史行情表（分钟级）
CREATE TABLE IF NOT EXISTS `quote_history_minute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `asset_type` VARCHAR(20) NOT NULL COMMENT '资产类型：QUOTA-配额，CREDIT-信用',
  `time_slot` DATETIME NOT NULL COMMENT '时间点',
  `open_price` DECIMAL(20, 2) NOT NULL COMMENT '开盘价',
  `close_price` DECIMAL(20, 2) NOT NULL COMMENT '收盘价',
  `high_price` DECIMAL(20, 2) NOT NULL COMMENT '最高价',
  `low_price` DECIMAL(20, 2) NOT NULL COMMENT '最低价',
  `volume` DECIMAL(20, 2) DEFAULT 0.00 COMMENT '成交量（吨）',
  `amount` DECIMAL(20, 2) DEFAULT 0.00 COMMENT '成交额（元）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_asset_time` (`asset_type`, `time_slot`),
  KEY `idx_asset_type` (`asset_type`),
  KEY `idx_time_slot` (`time_slot`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分钟级历史行情表';

-- 历史行情表（日级）
CREATE TABLE IF NOT EXISTS `quote_history_daily` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `asset_type` VARCHAR(20) NOT NULL COMMENT '资产类型：QUOTA-配额，CREDIT-信用',
  `date` DATE NOT NULL COMMENT '日期',
  `open_price` DECIMAL(20, 2) NOT NULL COMMENT '开盘价',
  `close_price` DECIMAL(20, 2) NOT NULL COMMENT '收盘价',
  `high_price` DECIMAL(20, 2) NOT NULL COMMENT '最高价',
  `low_price` DECIMAL(20, 2) NOT NULL COMMENT '最低价',
  `volume` DECIMAL(20, 2) DEFAULT 0.00 COMMENT '成交量（吨）',
  `amount` DECIMAL(20, 2) DEFAULT 0.00 COMMENT '成交额（元）',
  `change` DECIMAL(20, 2) NOT NULL COMMENT '涨跌额',
  `change_rate` DECIMAL(10, 4) NOT NULL COMMENT '涨跌幅',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_asset_date` (`asset_type`, `date`),
  KEY `idx_asset_type` (`asset_type`),
  KEY `idx_date` (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日级历史行情表';

-- 插入测试历史行情数据
INSERT INTO `quote_history_daily` (`asset_type`, `date`, `open_price`, `close_price`, `high_price`, `low_price`, `volume`, `amount`, `change`, `change_rate`)
VALUES 
('QUOTA', '2024-01-01', 45.00, 46.00, 48.00, 44.00, 2000.00, 92000.00, 1.00, 2.2222),
('QUOTA', '2024-01-02', 46.00, 47.50, 49.00, 45.50, 2800.00, 132200.00, 1.50, 3.2609),
('QUOTA', '2024-01-03', 47.50, 48.50, 50.00, 47.00, 2500.00, 121250.00, 1.00, 2.1053),
('CREDIT', '2024-01-01', 50.00, 51.00, 53.00, 49.00, 1500.00, 76500.00, 1.00, 2.0000),
('CREDIT', '2024-01-02', 51.00, 52.00, 54.00, 50.50, 1800.00, 93600.00, 1.00, 1.9608),
('CREDIT', '2024-01-03', 52.00, 53.00, 55.00, 51.50, 2200.00, 116600.00, 1.00, 1.9231);

