package com.example.trade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("market_quote")
public class MarketQuote {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String assetType; // 资产类型：QUOTA-配额，CREDIT-信用

    private BigDecimal lastPrice; // 最新成交价

    private BigDecimal openPrice; // 开盘价

    private BigDecimal closePrice; // 收盘价（昨日）

    private BigDecimal highPrice; // 最高价

    private BigDecimal lowPrice; // 最低价

    private BigDecimal volume; // 成交量（吨）

    private BigDecimal amount; // 成交额（元）

    private BigDecimal change; // 涨跌额

    private BigDecimal changeRate; // 涨跌幅

    private BigDecimal bidPrice; // 买一价

    private BigDecimal bidQuantity; // 买一量

    private BigDecimal askPrice; // 卖一价

    private BigDecimal askQuantity; // 卖一量

    private LocalDateTime updateTime;
}