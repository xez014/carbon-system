package com.example.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.trade.entity.MarketQuote;
import com.example.trade.mapper.MarketQuoteMapper;
import com.example.trade.service.MarketQuoteService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MarketQuoteServiceImpl extends ServiceImpl<MarketQuoteMapper, MarketQuote> implements MarketQuoteService {

    @Override
    public List<MarketQuote> getAllQuotes() {
        return this.list();
    }

    @Override
    public MarketQuote getQuoteByAssetType(String assetType) {
        LambdaQueryWrapper<MarketQuote> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MarketQuote::getAssetType, assetType);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean updateQuote(MarketQuote quote) {
        return this.saveOrUpdate(quote);
    }
}