package com.example.trade.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.trade.entity.TradeOrder;
import com.example.trade.service.TradeOrderService;
import com.example.common.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trade/orders")
public class TradeOrderController {

    @Autowired
    private TradeOrderService tradeOrderService;

    // 获取市场挂单列表 (分页)
    @GetMapping("/market")
    public Result<Page<TradeOrder>> getMarketOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String direction) {
        
        Page<TradeOrder> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<TradeOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradeOrder::getStatus, "OPEN"); // 只查询进行中的
        
        if (type != null && !type.isEmpty()) {
            queryWrapper.eq(TradeOrder::getItemType, type);
        }
        if (direction != null && !direction.isEmpty()) {
            queryWrapper.eq(TradeOrder::getDirection, direction);
        }
        
        queryWrapper.orderByDesc(TradeOrder::getCreateTime);
        
        return Result.success(tradeOrderService.page(pageParam, queryWrapper));
    }

    // 发布挂单
    @PostMapping("/publish")
    public Result<Boolean> publishOrder(@RequestBody TradeOrder order) {
        order.setStatus("OPEN");
        order.setTradedQuantity(new java.math.BigDecimal(0));
        order.setCreateTime(java.time.LocalDateTime.now());
        order.setUpdateTime(java.time.LocalDateTime.now());
        // TODO: 校验用户资产是否足够 (需要调用 service-assets)
        return Result.success(tradeOrderService.save(order));
    }
    
    // 我的订单
    @GetMapping("/my")
    public Result<Page<TradeOrder>> getMyOrders(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Page<TradeOrder> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<TradeOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradeOrder::getUserId, userId);
        queryWrapper.orderByDesc(TradeOrder::getCreateTime);
        return Result.success(tradeOrderService.page(pageParam, queryWrapper));
    }

    // 取消订单
    @PostMapping("/cancel/{orderId}")
    public Result<Boolean> cancelOrder(@PathVariable Long orderId) {
        TradeOrder order = tradeOrderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!"OPEN".equals(order.getStatus())) {
            return Result.error("只有挂单中的订单才能取消");
        }
        
        order.setStatus("CANCELLED");
        return Result.success(tradeOrderService.updateById(order));
    }
}
