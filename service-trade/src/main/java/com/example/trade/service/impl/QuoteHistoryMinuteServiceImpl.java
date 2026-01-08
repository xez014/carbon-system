package com.example.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.trade.entity.QuoteHistoryMinute;
import com.example.trade.mapper.QuoteHistoryMinuteMapper;
import com.example.trade.service.QuoteHistoryMinuteService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuoteHistoryMinuteServiceImpl extends ServiceImpl<QuoteHistoryMinuteMapper, QuoteHistoryMinute> implements QuoteHistoryMinuteService {

    @Override
    public List<QuoteHistoryMinute> getHistoryByAssetTypeAndTimeRange(String assetType, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<QuoteHistoryMinute> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QuoteHistoryMinute::getAssetType, assetType);
        queryWrapper.between(QuoteHistoryMinute::getTimeSlot, startTime, endTime);
        queryWrapper.orderByAsc(QuoteHistoryMinute::getTimeSlot);
        return this.list(queryWrapper);
    }

    @Override
    public boolean saveHistoryMinute(QuoteHistoryMinute history) {
        return this.save(history);
    }
}