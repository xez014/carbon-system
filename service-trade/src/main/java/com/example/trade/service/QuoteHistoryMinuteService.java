package com.example.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.trade.entity.QuoteHistoryMinute;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.time.LocalDateTime;
import java.util.List;

public interface QuoteHistoryMinuteService extends IService<QuoteHistoryMinute> {
    /**
     * 根据资产类型和时间范围获取分钟级历史行情
     */
    List<QuoteHistoryMinute> getHistoryByAssetTypeAndTimeRange(String assetType, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 保存分钟级历史行情
     */
    boolean saveHistoryMinute(QuoteHistoryMinute history);
}