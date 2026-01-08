package com.example.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.trade.entity.TradeExecution;
import com.example.trade.mapper.TradeExecutionMapper;
import com.example.trade.service.TradeExecutionService;
import org.springframework.stereotype.Service;

@Service
public class TradeExecutionServiceImpl extends ServiceImpl<TradeExecutionMapper, TradeExecution> implements TradeExecutionService {

    @Override
    public IPage<TradeExecution> getExecutionsByOrderId(Long orderId, Integer page, Integer size) {
        Page<TradeExecution> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<TradeExecution> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradeExecution::getOrderId, orderId);
        queryWrapper.orderByDesc(TradeExecution::getCreateTime);
        return this.page(pageParam, queryWrapper);
    }

    @Override
    public IPage<TradeExecution> getExecutionsByAssetType(String assetType, Integer page, Integer size) {
        Page<TradeExecution> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<TradeExecution> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradeExecution::getAssetType, assetType);
        queryWrapper.orderByDesc(TradeExecution::getCreateTime);
        return this.page(pageParam, queryWrapper);
    }

    @Override
    public boolean saveExecution(TradeExecution execution) {
        return this.save(execution);
    }
}