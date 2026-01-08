package com.example.trade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("quote_history_minute")
public class QuoteHistoryMinute {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String assetType; // 资产类型：QUOTA-配额，CREDIT-信用

    private LocalDateTime timeSlot; // 时间点

    private BigDecimal openPrice; // 开盘价

    private BigDecimal closePrice; // 收盘价

    private BigDecimal highPrice; // 最高价

    private BigDecimal lowPrice; // 最低价

    private BigDecimal volume; // 成交量（吨）

    private BigDecimal amount; // 成交额（元）
}