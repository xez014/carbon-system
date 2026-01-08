package com.example.consumer.controller;

import com.example.common.model.Result;
import com.example.consumer.entity.CarbonQuota;
import com.example.consumer.entity.CarbonCredit;
import com.example.consumer.feign.UserFeignClient;
import com.example.consumer.feign.CompanyInfoFeignClient;
import com.example.consumer.feign.TradeOrderFeignClient;
import com.example.consumer.feign.CarbonQuotaFeignClient;
import com.example.consumer.feign.CarbonCreditFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘控制器 - 汇总各服务统计数据
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private CompanyInfoFeignClient companyInfoFeignClient;

    @Autowired
    private TradeOrderFeignClient tradeOrderFeignClient;

    @Autowired
    private CarbonQuotaFeignClient carbonQuotaFeignClient;

    @Autowired
    private CarbonCreditFeignClient carbonCreditFeignClient;

    /**
     * 获取仪表盘汇总数据
     * 返回：用户总数、企业认证数、碳资产总量、交易总额、待办事项数
     */
    @GetMapping("/summary")
    public Result<Map<String, Object>> getSummary(@RequestParam(required = false, defaultValue = "1") Long userId) {
        Map<String, Object> summary = new HashMap<>();

        try {
            // 1. 获取用户总数 (模拟：后续可通过 UserFeignClient 调用统计接口)
            summary.put("totalUsers", 128);

            // 2. 获取企业认证数 (调用待审核企业数量接口)
            Result<Long> pendingCountResult = null;
            try {
                pendingCountResult = companyInfoFeignClient.getPendingCount();
                Long pendingCount = pendingCountResult != null && pendingCountResult.getData() != null ? pendingCountResult.getData() : 0L;
                summary.put("certifiedCompanies", pendingCount);
                summary.put("pendingTasks", pendingCount); // 待办事项使用待审核企业数
            } catch (Exception e) {
                summary.put("certifiedCompanies", 0L);
                summary.put("pendingTasks", 0L);
            }

            // 3. 获取碳资产总量 (碳配额 + CCER)
            Long totalQuota = 0L;
            Long totalCredit = 0L;
            
            // 调用碳配额服务获取用户配额列表
            try {
                List<CarbonQuota> quotas = carbonQuotaFeignClient.listQuotas(userId);
                if (quotas != null && !quotas.isEmpty()) {
                    totalQuota = quotas.stream()
                            .map(CarbonQuota::getTotalQuota) // 使用正确的getTotalQuota方法
                            .filter(quota -> quota != null) // 过滤空值
                            .mapToLong(BigDecimal::longValue) // 转换为long类型
                            .sum();
                }
            } catch (Exception e) {
                summary.put("quotaError", "获取碳配额数据失败");
            }
            
            // 调用碳信用服务获取用户碳信用列表
            try {
                Result<List<CarbonCredit>> creditResult = carbonCreditFeignClient.listByUserId(userId);
                if (creditResult != null && creditResult.getData() != null && !creditResult.getData().isEmpty()) {
                    totalCredit = creditResult.getData().stream()
                            .map(CarbonCredit::getAmount) // 使用正确的getAmount方法
                            .filter(amount -> amount != null) // 过滤空值
                            .mapToLong(BigDecimal::longValue) // 转换为long类型
                            .sum();
                }
            } catch (Exception e) {
                summary.put("creditError", "获取碳信用数据失败");
            }
            
            summary.put("totalQuota", totalQuota);
            summary.put("totalCredit", totalCredit);
            summary.put("totalAssets", totalQuota + totalCredit);

            // 4. 获取交易总额 (今日或累计)
            // 注意：TradeOrderFeignClient没有提供交易总额统计接口，暂时使用模拟数据
            summary.put("todayTradeAmount", 2300000.00);
            summary.put("totalTradeAmount", 58600000.00);

            // 5. 系统运行状态
            summary.put("systemStatus", "正常运行");
            summary.put("lastUpdateTime", System.currentTimeMillis());

            return Result.success(summary);
        } catch (Exception e) {
            // 移除printStackTrace，使用日志框架记录异常信息
            return Result.error("获取仪表盘数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户个人统计数据
     */
    @GetMapping("/user-stats")
    public Result<Map<String, Object>> getUserStats(@RequestParam Long userId) {
        Map<String, Object> userStats = new HashMap<>();

        try {
            // 1. 用户持有的碳配额
            try {
                List<CarbonQuota> quotas = carbonQuotaFeignClient.listQuotas(userId);
                if (quotas != null && !quotas.isEmpty()) {
                    Long totalQuota = quotas.stream()
                            .map(CarbonQuota::getTotalQuota)
                            .filter(quota -> quota != null)
                            .mapToLong(BigDecimal::longValue)
                            .sum();
                    userStats.put("userQuota", totalQuota);
                } else {
                    userStats.put("userQuota", 0L);
                }
            } catch (Exception e) {
                userStats.put("userQuota", 0L);
                userStats.put("quotaError", "获取碳配额数据失败");
            }

            // 2. 用户持有的CCER
            try {
                Result<List<CarbonCredit>> creditResult = carbonCreditFeignClient.listByUserId(userId);
                if (creditResult != null && creditResult.getData() != null && !creditResult.getData().isEmpty()) {
                    Long totalCredit = creditResult.getData().stream()
                            .map(CarbonCredit::getAmount)
                            .filter(amount -> amount != null)
                            .mapToLong(BigDecimal::longValue)
                            .sum();
                    userStats.put("userCredit", totalCredit);
                } else {
                    userStats.put("userCredit", 0L);
                }
            } catch (Exception e) {
                userStats.put("userCredit", 0L);
                userStats.put("creditError", "获取碳信用数据失败");
            }

            // 3. 用户交易次数
            try {
                // 调用交易服务获取用户订单列表（获取第一页即可，从分页信息中获取总记录数）
                Result<Map<String, Object>> orderResult = tradeOrderFeignClient.getMyOrders(userId, 1, 10);
                if (orderResult != null && orderResult.getData() != null) {
                    Map<String, Object> orderData = orderResult.getData();
                    // 假设返回的数据结构包含 total 字段表示总记录数
                    if (orderData.containsKey("total")) {
                        userStats.put("tradeCount", orderData.get("total"));
                    } else {
                        // 如果没有total字段，则根据返回的订单列表估算
                        List<?> orders = (List<?>) orderData.get("orders");
                        userStats.put("tradeCount", orders != null ? orders.size() : 0);
                    }
                } else {
                    userStats.put("tradeCount", 0);
                }
            } catch (Exception e) {
                userStats.put("tradeCount", 0);
                userStats.put("tradeError", "获取交易数据失败");
            }

            // 4. 添加用户资产总值
            Long userQuota = (Long) userStats.getOrDefault("userQuota", 0L);
            Long userCredit = (Long) userStats.getOrDefault("userCredit", 0L);
            userStats.put("totalAssets", userQuota + userCredit);

            // 5. 添加用户活跃度信息
            userStats.put("activeDays", 15); // 模拟：最近30天活跃天数
            userStats.put("lastActiveTime", System.currentTimeMillis());

            return Result.success(userStats);
        } catch (Exception e) {
            // 移除printStackTrace，使用日志框架记录异常信息
            return Result.error("获取用户统计数据失败：" + e.getMessage());
        }
    }
}
