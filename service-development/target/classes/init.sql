-- 项目开发表
CREATE TABLE IF NOT EXISTS `development_project` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(255) NOT NULL COMMENT '项目名称',
  `methodology` VARCHAR(255) DEFAULT NULL COMMENT '适用方法学',
  `status` VARCHAR(50) DEFAULT 'PLANNED' COMMENT '状态: PLANNED, REGISTERED, VERIFIED, ISSUED',
  `owner_id` BIGINT(20) NOT NULL COMMENT '业主ID',
  `location` VARCHAR(255) DEFAULT NULL COMMENT '项目地点',
  `estimated_emission_reduction` DOUBLE DEFAULT NULL COMMENT '预估减排量',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='减排项目开发表';

-- 插入测试项目
INSERT INTO `development_project` (`name`, `methodology`, `status`, `owner_id`, `location`, `estimated_emission_reduction`)
VALUES ('内蒙古阿拉善造林项目', 'CM-001-V01', 'PLANNED', 1, '内蒙古阿拉善左旗', 50000.0);

-- 方法学管理表
CREATE TABLE IF NOT EXISTS `methodology` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` VARCHAR(50) NOT NULL COMMENT '方法学编号',
  `name` VARCHAR(255) NOT NULL COMMENT '方法学名称',
  `category` VARCHAR(100) DEFAULT NULL COMMENT '适用领域',
  `version` VARCHAR(20) DEFAULT NULL COMMENT '版本号',
  `description` TEXT COMMENT '方法学描述',
  `publisher` VARCHAR(100) DEFAULT NULL COMMENT '发布机构',
  `publish_date` DATETIME DEFAULT NULL COMMENT '发布日期',
  `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-有效, DEPRECATED-已废弃, DRAFT-草案',
  `file_url` VARCHAR(500) DEFAULT NULL COMMENT '方法学文件URL',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='方法学管理表';

-- 插入测试方法学数据
INSERT INTO `methodology` (`code`, `name`, `category`, `version`, `description`, `publisher`, `publish_date`, `status`)
VALUES 
('CM-001-V01', '碳汇造林项目方法学', '林业碳汇', 'V1.0', '适用于在无林地上通过人工造林增加碳汇的项目', '国家发改委', '2023-01-15 00:00:00', 'ACTIVE'),
('CM-002-V01', '竹子造林碳汇项目方法学', '林业碳汇', 'V1.0', '适用于通过竹子造林增加碳汇的项目', '生态环境部', '2023-03-20 00:00:00', 'ACTIVE'),
('CM-003-V01', '森林经营碳汇项目方法学', '林业碳汇', 'V1.0', '适用于通过森林抚育、补植等经营活动增加碳汇的项目', '国家林草局', '2023-05-10 00:00:00', 'ACTIVE'),
('CM-011-V01', '并网光伏发电项目方法学', '可再生能源', 'V1.0', '适用于并网太阳能光伏发电减排项目', '国家发改委', '2022-11-01 00:00:00', 'ACTIVE'),
('CM-012-V01', '并网海上风力发电项目方法学', '可再生能源', 'V1.0', '适用于海上风力发电项目', '国家能源局', '2023-02-15 00:00:00', 'ACTIVE'),
('CM-021-V01', '工业企业节能改造项目方法学', '节能减排', 'V1.0', '适用于通过技术改造提高能源利用效率的项目', '工信部', '2023-04-01 00:00:00', 'ACTIVE'),
('CM-031-V01', '垃圾焚烧发电项目方法学', '废弃物处理', 'V1.0', '适用于城市生活垃圾焚烧发电项目', '住建部', '2023-06-10 00:00:00', 'ACTIVE');

