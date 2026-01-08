package com.example.consumer.feign;

import com.example.common.entity.CarbonQuota;
import com.example.common.entity.CarbonQuotaDetail;
import com.example.common.model.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;

import java.util.List;

@FeignClient(name = "service-assets", contextId = "carbonQuotaFeignClient")
public interface CarbonQuotaFeignClient {

    @GetMapping("/api/assets/quota/get")
    Result<CarbonQuota> getQuota(@RequestParam("userId") Long userId, @RequestParam("year") Integer year);

    @GetMapping("/api/assets/quota/list")
    Result<List<CarbonQuota>> listQuotas(@RequestParam("userId") Long userId);

    @GetMapping("/api/assets/quota/detail/list")
    Result<List<CarbonQuotaDetail>> listQuotaDetails(@RequestParam("quotaId") Long quotaId);

    @PostMapping("/api/assets/quota/save")
    Result<CarbonQuota> saveQuota(@RequestBody CarbonQuota quota);

    @PostMapping("/api/assets/quota/add")
    Result<CarbonQuota> addQuota(@RequestBody CarbonQuota quota);

    @PutMapping("/api/assets/quota/update")
    Result<CarbonQuota> updateQuota(@RequestBody CarbonQuota quota);

    @PostMapping("/api/assets/quota/adjust")
    Result<CarbonQuota> adjustQuota(@RequestParam("quotaId") Long quotaId,
                                  @RequestParam("amount") BigDecimal amount,
                                  @RequestParam("type") String type,
                                  @RequestParam(value = "remark", required = false) String remark);

    @PostMapping("/api/assets/quota/fulfill")
    Result<CarbonQuota> fulfillQuota(@RequestParam("quotaId") Long quotaId,
                                   @RequestParam("verifiedEmission") BigDecimal verifiedEmission);
}
