package com.example.common.exception;

/**
 * 碳配额调整异常
 */
public class CarbonQuotaAdjustmentException extends RuntimeException {
    public CarbonQuotaAdjustmentException() {
        super("碳配额调整失败");
    }

    public CarbonQuotaAdjustmentException(String message) {
        super(message);
    }
}
