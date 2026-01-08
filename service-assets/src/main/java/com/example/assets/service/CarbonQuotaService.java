package com.example.assets.service;

import com.example.common.entity.CarbonQuota;
import com.example.common.entity.CarbonQuotaDetail;
import java.math.BigDecimal;
import java.util.List;

/**
 * 碳配额服务接口
 */
public interface CarbonQuotaService {

    /**
     * 获取指定年份的配额信息
     * @param userId 用户ID
     * @param year 履约年度
     * @return 碳配额信息
     */
    CarbonQuota getQuotaByUserIdAndYear(Long userId, Integer year);

    /**
     * 获取用户所有年份的配额列表
     * @param userId 用户ID
     * @return 配额列表
     */
    List<CarbonQuota> listQuotasByUserId(Long userId);

    /**
     * 获取配额明细
     * @param quotaId 配额ID
     * @return 配额明细列表
     */
    List<CarbonQuotaDetail> listQuotaDetails(Long quotaId);

    /**
     * 添加配额信息
     * @param quota 配额信息
     * @return 添加后的配额信息
     */
    CarbonQuota addQuota(CarbonQuota quota);

    /**
     * 更新配额信息
     * @param quota 配额信息
     * @return 更新后的配额信息
     */
    CarbonQuota updateQuota(CarbonQuota quota);

    /**
     * 保存配额信息（兼容旧版本接口）
     * @param quota 配额信息
     * @return 保存后的配额信息
     */
    default CarbonQuota saveQuota(CarbonQuota quota) {
        if (quota.getId() == null) {
            return addQuota(quota);
        } else {
            return updateQuota(quota);
        }
    }

    /**
     * 调整配额
     * @param quotaId 配额ID
     * @param amount 调整金额
     * @param type 调整类型
     * @param remark 备注
     * @return 调整后的配额
     */
    CarbonQuota adjustQuota(Long quotaId, BigDecimal amount, String type, String remark);

    /**
     * 履行配额
     * @param quotaId 配额ID
     * @param verifiedEmission 核查排放量
     * @return 履约后的配额
     */
    CarbonQuota fulfillQuota(Long quotaId, BigDecimal verifiedEmission);
}