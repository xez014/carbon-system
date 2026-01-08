package com.example.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.trade.entity.QuoteHistoryDaily;
import java.time.LocalDate;
import java.util.List;

public interface QuoteHistoryDailyService extends IService<QuoteHistoryDaily> {
    /**
     * 根据资产类型和日期范围获取日级历史行情
     */
    List<QuoteHistoryDaily> getHistoryByAssetTypeAndDateRange(String assetType, LocalDate startDate, LocalDate endDate);

    /**
     * 保存日级历史行情
     */
    boolean saveHistoryDaily(QuoteHistoryDaily history);
}