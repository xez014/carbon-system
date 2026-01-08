package com.example.trade.controller;

import com.example.trade.entity.MarketQuote;
import com.example.trade.entity.QuoteHistoryDaily;
import com.example.trade.entity.QuoteHistoryMinute;
import com.example.trade.entity.TradeExecution;
import com.example.trade.service.MarketQuoteService;
import com.example.trade.service.QuoteHistoryDailyService;
import com.example.trade.service.QuoteHistoryMinuteService;
import com.example.trade.service.TradeExecutionService;
import com.example.common.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trade/quote")
public class MarketQuoteController {

    @Autowired
    private MarketQuoteService marketQuoteService;

    @Autowired
    private QuoteHistoryDailyService quoteHistoryDailyService;

    @Autowired
    private QuoteHistoryMinuteService quoteHistoryMinuteService;

    @Autowired
    private TradeExecutionService tradeExecutionService;

    // 获取所有资产类型的实时行情
    @GetMapping("/market/all")
    public Result<List<MarketQuote>> getAllMarketQuotes() {
        return Result.success(marketQuoteService.getAllQuotes());
    }

    // 根据资产类型获取实时行情
    @GetMapping("/market/{assetType}")
    public Result<MarketQuote> getMarketQuote(@PathVariable String assetType) {
        MarketQuote quote = marketQuoteService.getQuoteByAssetType(assetType);
        return quote != null ? Result.success(quote) : Result.error("未找到该资产的行情数据");
    }

    // 获取日级历史行情
    @GetMapping("/history/daily")
    public Result<List<QuoteHistoryDaily>> getDailyHistory(
            @RequestParam String assetType,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<QuoteHistoryDaily> history = quoteHistoryDailyService.getHistoryByAssetTypeAndDateRange(assetType, start, end);
        return Result.success(history);
    }

    // 获取分钟级历史行情
    @GetMapping("/history/minute")
    public Result<List<QuoteHistoryMinute>> getMinuteHistory(
            @RequestParam String assetType,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);
        List<QuoteHistoryMinute> history = quoteHistoryMinuteService.getHistoryByAssetTypeAndTimeRange(assetType, start, end);
        return Result.success(history);
    }

    // 获取成交记录（分页）
    @GetMapping("/execution/list")
    public Result<IPage<TradeExecution>> getExecutionList(
            @RequestParam(required = false) String assetType,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Page<TradeExecution> pageParam = new Page<>(page, size);
        IPage<TradeExecution> result;
        if (assetType != null && !assetType.isEmpty()) {
            result = tradeExecutionService.getExecutionsByAssetType(assetType, page, size);
        } else {
            result = tradeExecutionService.page(pageParam, null);
        }
        return Result.success(result);
    }

    // 根据订单ID获取成交记录
    @GetMapping("/execution/order/{orderId}")
    public Result<IPage<TradeExecution>> getExecutionsByOrderId(
            @PathVariable Long orderId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        IPage<TradeExecution> result = tradeExecutionService.getExecutionsByOrderId(orderId, page, size);
        return Result.success(result);
    }
}