-- 碳资讯服务数据库初始化脚本

-- 1. 新闻资讯表
CREATE TABLE IF NOT EXISTS `news` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` VARCHAR(200) NOT NULL COMMENT '新闻标题',
  `content` TEXT NOT NULL COMMENT '新闻内容（支持HTML或Markdown）',
  `summary` VARCHAR(500) NULL COMMENT '新闻摘要',
  `category` VARCHAR(50) NOT NULL COMMENT '新闻分类（政策法规/行业动态/技术创新/市场分析）',
  `author` VARCHAR(50) NOT NULL COMMENT '作者',
  `source` VARCHAR(100) NULL COMMENT '来源',
  `cover_image` VARCHAR(255) NULL COMMENT '封面图片URL',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-草稿 1-已发布 2-已下线',
  `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶：0-否 1-是',
  `publish_time` DATETIME NULL COMMENT '发布时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-正常 1-已删除',
  PRIMARY KEY (`id`),
  INDEX `idx_category` (`category`),
  INDEX `idx_status` (`status`),
  INDEX `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新闻资讯表';

-- 2. 插入测试数据
INSERT INTO `news` (`title`, `content`, `summary`, `category`, `author`, `source`, `cover_image`, `view_count`, `status`, `is_top`, `publish_time`) VALUES
('全国碳市场累计成交额突破100亿元', 
'<p>截至2025年底，全国碳排放权交易市场累计成交量突破2.3亿吨，成交额突破100亿元。市场运行总体平稳有序，碳价合理波动，有效发挥了碳定价功能。</p><p>生态环境部表示，将进一步扩大全国碳市场行业覆盖范围，逐步纳入钢铁、水泥等重点行业。</p>', 
'全国碳市场成交额破百亿，市场运行稳健，将逐步扩大行业覆盖范围', 
'市场分析', '环境日报', '生态环境部', 
'https://example.com/carbon-market.jpg', 1523, 1, 1, '2026-01-05 10:00:00'),

('《碳排放权交易管理暂行条例》正式施行', 
'<p>《碳排放权交易管理暂行条例》于2024年5月1日起正式施行，标志着我国碳市场建设进入法治化轨道。</p><p>条例明确了碳排放权市场交易主体、交易产品、交易方式等内容，为碳市场健康发展提供法律保障。</p>', 
'碳交易管理条例正式施行，碳市场建设进入法治化新阶段', 
'政策法规', '法制日报', '国务院', 
'https://example.com/carbon-law.jpg', 2341, 1, 1, '2026-01-03 14:30:00'),

('CCER重启交易，首日成交活跃', 
'<p>全国温室气体自愿减排交易市场（CCER）重启首日交易活跃，共有21个项目参与交易，成交量达15万吨，成交额超过900万元。</p><p>CCER的重启将为企业提供更多履约选择，激活碳市场流动性。</p>', 
'CCER重启首日交易活跃，为碳市场注入新活力', 
'行业动态', '碳交易网', '北京绿色交易所', 
'https://example.com/ccer-trading.jpg', 1876, 1, 0, '2026-01-02 09:15:00'),

('碳捕集技术取得重大突破，成本降低30%', 
'<p>中国科学院最新研发的新型碳捕集材料实现了工业化应用，碳捕集成本降低30%，为大规模减排提供技术支撑。</p><p>该技术已在多个火电厂开展试点应用，预计年捕集二氧化碳超过100万吨。</p>', 
'新型碳捕集技术成本降低30%，为大规模减排提供支撑', 
'技术创新', '科技日报', '中国科学院', 
'https://example.com/carbon-capture.jpg', 987, 1, 0, '2025-12-28 16:45:00'),

('企业如何参与碳交易市场？完整指南来了', 
'<p>本文详细介绍企业参与碳交易市场的完整流程，包括配额分配、碳盘查、交易策略、履约要求等关键环节。</p><p>企业应建立碳资产管理体系，制定科学的碳交易策略，实现减排与经济效益双赢。</p>', 
'企业参与碳交易完整指南：从配额分配到履约全流程解析', 
'行业动态', '碳管理专家', '碳交易培训中心', 
'https://example.com/enterprise-guide.jpg', 3245, 1, 0, '2025-12-25 11:20:00'),

('2026年碳价走势预测：预计稳中有升', 
'<p>多家机构预测，2026年全国碳市场价格将在50-80元/吨区间波动，整体呈现稳中有升态势。</p><p>影响因素包括：配额总量收紧、行业覆盖扩大、国际碳市场联动等。建议企业提前布局碳资产管理。</p>', 
'2026碳价预测：50-80元/吨，稳中有升，企业需提前布局', 
'市场分析', '碳市场研究', '绿色金融研究院', 
'https://example.com/price-forecast.jpg', 2156, 1, 0, '2025-12-20 13:50:00'),

('绿色金融支持碳中和，专项贷款突破5000亿', 
'<p>截至2025年底，各大银行累计发放碳中和专项贷款超过5000亿元，支持清洁能源、节能减排等绿色项目建设。</p><p>金融机构创新推出碳排放权抵押贷款、碳资产证券化等产品，助力实现"双碳"目标。</p>', 
'绿色金融发力，碳中和专项贷款突破5000亿元', 
'政策法规', '金融时报', '人民银行', 
'https://example.com/green-finance.jpg', 1432, 1, 0, '2025-12-18 10:00:00'),

('碳中和背景下的新能源汽车发展机遇', 
'<p>随着碳中和目标的推进，新能源汽车产业迎来黄金发展期。预计2026年新能源汽车渗透率将突破40%。</p><p>车企应把握碳交易机遇，将碳减排量转化为经济效益，增强市场竞争力。</p>', 
'碳中和推动新能源汽车发展，2026年渗透率预计破40%', 
'行业动态', '汽车观察', '新能源汽车协会', 
'https://example.com/ev-opportunity.jpg', 1765, 1, 0, '2025-12-15 14:30:00');

-- 3. 新闻分类统计视图（可选）
CREATE OR REPLACE VIEW `news_category_stats` AS
SELECT 
    category,
    COUNT(*) as total_count,
    SUM(view_count) as total_views,
    AVG(view_count) as avg_views
FROM news
WHERE status = 1 AND deleted = 0
GROUP BY category;
