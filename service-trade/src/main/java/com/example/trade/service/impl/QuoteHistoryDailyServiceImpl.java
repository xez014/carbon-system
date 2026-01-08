package com.example.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.trade.entity.QuoteHistoryDaily;
import com.example.trade.mapper.QuoteHistoryDailyMapper;
import com.example.trade.service.QuoteHistoryDailyService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class QuoteHistoryDailyServiceImpl extends ServiceImpl<QuoteHistoryDailyMapper, QuoteHistoryDaily> implements QuoteHistoryDailyService {

    @Override
    public List<QuoteHistoryDaily> getHistoryByAssetTypeAndDateRange(String assetType, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<QuoteHistoryDaily> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QuoteHistoryDaily::getAssetType, assetType);
        queryWrapper.between(QuoteHistoryDaily::getDate, startDate, endDate);
        queryWrapper.orderByAsc(QuoteHistoryDaily::getDate);
        return this.list(queryWrapper);
    }

    @Override
    public boolean saveHistoryDaily(QuoteHistoryDaily history) {
        return this.save(history);
    }
}