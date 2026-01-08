package com.example.trade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.trade.entity.MarketQuote;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MarketQuoteMapper extends BaseMapper<MarketQuote> {
}