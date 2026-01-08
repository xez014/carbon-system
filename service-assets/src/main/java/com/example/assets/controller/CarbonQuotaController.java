package com.example.assets.controller;

import com.example.common.entity.CarbonQuota;
import com.example.common.entity.CarbonQuotaDetail;
import com.example.common.model.Result;
import com.example.assets.service.CarbonQuotaService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 碳配额管理 - 服务端接口
 */
@RestController
@RequestMapping("/api/assets/quota")
public class CarbonQuotaController {

    @Autowired
    private CarbonQuotaService carbonQuotaService;

    /**
     * 获取指定年份的配额信息
     */
    @GetMapping("/get")
    public Result<CarbonQuota> getQuota(@RequestParam Long userId, @RequestParam Integer year) {
        CarbonQuota quota = carbonQuotaService.getQuotaByUserIdAndYear(userId, year);
        return Result.success(quota);
    }
    
    /**
     * 获取用户所有年份的配额列表
     */
    @GetMapping("/list")
    public Result<List<CarbonQuota>> listQuotas(@RequestParam Long userId) {
        List<CarbonQuota> quotas = carbonQuotaService.listQuotasByUserId(userId);
        return Result.success(quotas);
    }

    /**
     * 获取配额明细
     */
    @GetMapping("/detail/list")
    public Result<List<CarbonQuotaDetail>> listQuotaDetails(@RequestParam Long quotaId) {
        List<CarbonQuotaDetail> details = carbonQuotaService.listQuotaDetails(quotaId);
        return Result.success(details);
    }

    /**
     * 新增配额
     */
    @PostMapping("/add")
    public Result<CarbonQuota> addQuota(@RequestBody CarbonQuota quota) {
        CarbonQuota addedQuota = carbonQuotaService.addQuota(quota);
        return Result.success("配额新增成功", addedQuota);
    }

    /**
     * 更新配额
     */
    @PutMapping("/update")
    public Result<CarbonQuota> updateQuota(@RequestBody CarbonQuota quota) {
        CarbonQuota updatedQuota = carbonQuotaService.updateQuota(quota);
        return Result.success("配额更新成功", updatedQuota);
    }

    /**
     * 调整配额
     */
    @PostMapping("/adjust")
    public Result<CarbonQuota> adjustQuota(@RequestParam Long quotaId, 
                                         @RequestParam BigDecimal amount,
                                         @RequestParam String type,
                                         @RequestParam(required = false) String remark) {
        CarbonQuota adjustedQuota = carbonQuotaService.adjustQuota(quotaId, amount, type, remark);
        return Result.success("配额调整成功", adjustedQuota);
    }

    /**
     * 履行配额
     */
    @PostMapping("/fulfill")
    public Result<CarbonQuota> fulfillQuota(@RequestParam Long quotaId, 
                                          @RequestParam BigDecimal verifiedEmission) {
        CarbonQuota fulfilledQuota = carbonQuotaService.fulfillQuota(quotaId, verifiedEmission);
        return Result.success("配额履约成功", fulfilledQuota);
    }
}
