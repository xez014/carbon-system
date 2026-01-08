package com.example.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.trade.entity.MarketQuote;
import java.util.List;

public interface MarketQuoteService extends IService<MarketQuote> {
    /**
     * 获取所有资产类型的实时行情
     */
    List<MarketQuote> getAllQuotes();

    /**
     * 根据资产类型获取实时行情
     */
    MarketQuote getQuoteByAssetType(String assetType);

    /**
     * 更新实时行情
     */
    boolean updateQuote(MarketQuote quote);
}