package com.example.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.trade.entity.TradeExecution;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface TradeExecutionService extends IService<TradeExecution> {
    /**
     * 根据订单ID获取成交记录
     */
    IPage<TradeExecution> getExecutionsByOrderId(Long orderId, Integer page, Integer size);

    /**
     * 根据资产类型获取成交记录（分页）
     */
    IPage<TradeExecution> getExecutionsByAssetType(String assetType, Integer page, Integer size);

    /**
     * 保存成交记录
     */
    boolean saveExecution(TradeExecution execution);
}