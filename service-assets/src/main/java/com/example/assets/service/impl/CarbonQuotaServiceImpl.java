package com.example.assets.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.assets.mapper.CarbonQuotaMapper;
import com.example.assets.mapper.CarbonQuotaDetailMapper;
import com.example.assets.service.CarbonQuotaService;
import com.example.common.entity.CarbonQuota;
import com.example.common.entity.CarbonQuotaDetail;
import com.example.common.exception.CarbonQuotaAdjustmentException;
import com.example.common.exception.CarbonQuotaFulfillmentException;
import com.example.common.exception.CarbonQuotaNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CarbonQuotaServiceImpl implements CarbonQuotaService {

    @Autowired
    private CarbonQuotaMapper carbonQuotaMapper;

    @Autowired
    private CarbonQuotaDetailMapper carbonQuotaDetailMapper;

    @Override
    public CarbonQuota getQuotaByUserIdAndYear(Long userId, Integer year) {
        QueryWrapper<CarbonQuota> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("year", year);
        return carbonQuotaMapper.selectOne(queryWrapper);
    }

    @Override
    public List<CarbonQuota> listQuotasByUserId(Long userId) {
        QueryWrapper<CarbonQuota> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("year");
        return carbonQuotaMapper.selectList(queryWrapper);
    }

    @Override
    public List<CarbonQuotaDetail> listQuotaDetails(Long quotaId) {
        QueryWrapper<CarbonQuotaDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("quota_id", quotaId)
                .orderByDesc("change_date");
        return carbonQuotaDetailMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CarbonQuota addQuota(CarbonQuota quota) {
        // 新增配额
        quota.setCreatedTime(LocalDateTime.now());
        quota.setUpdatedTime(LocalDateTime.now());
        carbonQuotaMapper.insert(quota);
        return quota;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CarbonQuota updateQuota(CarbonQuota quota) {
        // 更新配额前检查是否存在
        CarbonQuota existingQuota = carbonQuotaMapper.selectById(quota.getId());
        if (existingQuota == null) {
            throw new CarbonQuotaNotFoundException("配额不存在");
        }
        
        // 更新配额
        quota.setUpdatedTime(LocalDateTime.now());
        carbonQuotaMapper.updateById(quota);
        return quota;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CarbonQuota adjustQuota(Long quotaId, BigDecimal amount, String type, String remark) {
        // 获取当前配额
        CarbonQuota quota = carbonQuotaMapper.selectById(quotaId);
        if (quota == null) {
            throw new CarbonQuotaNotFoundException("配额不存在");
        }

        // 计算新的总配额
        BigDecimal newTotalQuota = quota.getTotalQuota().add(amount);
        if (newTotalQuota.compareTo(BigDecimal.ZERO) < 0) {
            throw new CarbonQuotaAdjustmentException("调整后配额不能为负数");
        }

        // 更新配额
        quota.setTotalQuota(newTotalQuota);
        quota.setUpdatedTime(LocalDateTime.now());
        carbonQuotaMapper.updateById(quota);

        // 记录配额明细
        CarbonQuotaDetail detail = new CarbonQuotaDetail();
        detail.setQuotaId(quotaId);
        detail.setType(type);
        detail.setAmount(amount);
        detail.setBalance(newTotalQuota);
        detail.setRemark(remark);
        detail.setChangeDate(LocalDateTime.now());
        carbonQuotaDetailMapper.insert(detail);

        return quota;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CarbonQuota fulfillQuota(Long quotaId, BigDecimal verifiedEmission) {
        // 获取当前配额
        CarbonQuota quota = carbonQuotaMapper.selectById(quotaId);
        if (quota == null) {
            throw new CarbonQuotaNotFoundException("配额不存在");
        }

        // 验证排放量不能为负数
        if (verifiedEmission.compareTo(BigDecimal.ZERO) < 0) {
            throw new CarbonQuotaFulfillmentException("核查排放量不能为负数");
        }

        // 更新履约状态
        quota.setVerifiedEmission(verifiedEmission);
        // 根据排放量与总配额的关系设置履约状态
        if (verifiedEmission.compareTo(quota.getTotalQuota()) <= 0) {
            quota.setStatus(1); // 履约成功
        } else {
            quota.setStatus(0); // 未履约
        }
        quota.setUpdatedTime(LocalDateTime.now());
        carbonQuotaMapper.updateById(quota);

        return quota;
    }
}
