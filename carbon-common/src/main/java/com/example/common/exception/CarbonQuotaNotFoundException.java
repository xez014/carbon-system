package com.example.common.exception;

/**
 * 碳配额不存在异常
 */
public class CarbonQuotaNotFoundException extends RuntimeException {
    public CarbonQuotaNotFoundException() {
        super("碳配额不存在");
    }

    public CarbonQuotaNotFoundException(String message) {
        super(message);
    }
}
