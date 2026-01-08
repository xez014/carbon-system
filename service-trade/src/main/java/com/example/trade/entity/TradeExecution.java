package com.example.trade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("trade_execution")
public class TradeExecution {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId; // 订单ID

    private Long counterOrderId; // 对手方订单ID

    private String assetType; // 资产类型：QUOTA-配额，CREDIT-信用

    private BigDecimal price; // 成交价格

    private BigDecimal quantity; // 成交数量

    private BigDecimal amount; // 成交金额

    private String direction; // 交易方向：BUY-买入，SELL-卖出

    private LocalDateTime createTime;
}