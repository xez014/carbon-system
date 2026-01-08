package com.example.assets.exception;

import com.example.common.exception.CarbonQuotaAdjustmentException;
import com.example.common.exception.CarbonQuotaFulfillmentException;
import com.example.common.exception.CarbonQuotaNotFoundException;
import com.example.common.model.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        Result<?> result = Result.error(400, e.getMessage());
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理碳配额不存在异常
     */
    @ExceptionHandler(CarbonQuotaNotFoundException.class)
    public ResponseEntity<Result<?>> handleCarbonQuotaNotFoundException(CarbonQuotaNotFoundException e) {
        Result<?> result = Result.error(404, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    /**
     * 处理碳配额调整异常
     */
    @ExceptionHandler(CarbonQuotaAdjustmentException.class)
    public ResponseEntity<Result<?>> handleCarbonQuotaAdjustmentException(CarbonQuotaAdjustmentException e) {
        Result<?> result = Result.error(400, e.getMessage());
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理碳配额履约异常
     */
    @ExceptionHandler(CarbonQuotaFulfillmentException.class)
    public ResponseEntity<Result<?>> handleCarbonQuotaFulfillmentException(CarbonQuotaFulfillmentException e) {
        Result<?> result = Result.error(400, e.getMessage());
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Result<?>> handleNullPointerException(NullPointerException e) {
        Result<?> result = Result.error(500, "系统内部错误，请稍后重试");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 处理其他所有异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleException(Exception e) {
        Result<?> result = Result.error(500, "系统内部错误，请稍后重试");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
}
