package com.example.trade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.trade.entity.TradeExecution;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TradeExecutionMapper extends BaseMapper<TradeExecution> {
}