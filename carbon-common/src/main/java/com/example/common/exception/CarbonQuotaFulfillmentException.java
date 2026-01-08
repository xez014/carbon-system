package com.example.common.exception;

/**
 * 碳配额履约异常
 */
public class CarbonQuotaFulfillmentException extends RuntimeException {
    public CarbonQuotaFulfillmentException() {
        super("碳配额履约失败");
    }

    public CarbonQuotaFulfillmentException(String message) {
        super(message);
    }
}
