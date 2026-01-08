package com.example.consumer.controller;

import com.example.common.entity.CarbonQuota;
import com.example.common.entity.CarbonQuotaDetail;
import com.example.common.model.Result;
import com.example.consumer.feign.CarbonQuotaFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets/quota")
public class CarbonQuotaController {

    @Autowired
    private CarbonQuotaFeignClient carbonQuotaFeignClient;

    /**
     * 获取指定年份配额
     * 如果未传 userId，默认使用 1 (测试用)
     */
    @GetMapping("/get")
    public Result<CarbonQuota> getQuota(@RequestParam(required = false, defaultValue = "1") Long userId, 
                                @RequestParam Integer year) {
        return carbonQuotaFeignClient.getQuota(userId, year);
    }

    /**
     * 获取列表
     */
    @GetMapping("/list")
    public Result<List<CarbonQuota>> listQuotas(@RequestParam(required = false, defaultValue = "1") Long userId) {
        return carbonQuotaFeignClient.listQuotas(userId);
    }

    /**
     * 获取配额明细
     */
    @GetMapping("/detail/list")
    public Result<List<CarbonQuotaDetail>> listQuotaDetails(@RequestParam Long quotaId) {
        return carbonQuotaFeignClient.listQuotaDetails(quotaId);
    }
}
