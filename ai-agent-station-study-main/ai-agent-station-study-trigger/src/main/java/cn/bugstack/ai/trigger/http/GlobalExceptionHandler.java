package cn.bugstack.ai.trigger.http;

import cn.bugstack.ai.api.response.Response;
import cn.bugstack.ai.types.enums.ResponseCode;
import cn.bugstack.ai.types.exception.AppException;
import cn.bugstack.ai.types.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

/**
 * 全局异常处理器
 * 
 * @author xiaofuge bugstack.cn @小傅哥
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response<String>> handleBizException(BizException e, HttpServletRequest request) {
        log.warn("业务异常 - URI: {}, 异常信息: {}", request.getRequestURI(), e.getMessage());
        
        Response<String> response = Response.<String>builder()
                .code(e.getCode())
                .info(e.getInfo())
                .data(null)
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理应用异常
     */
    @ExceptionHandler(AppException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Response<String>> handleAppException(AppException e, HttpServletRequest request) {
        log.error("应用异常 - URI: {}, 异常信息: {}", request.getRequestURI(), e.getMessage(), e);
        
        Response<String> response = Response.<String>builder()
                .code(e.getCode())
                .info(e.getInfo())
                .data(null)
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 处理参数校验异常 - @Valid
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response<String>> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn("参数校验异常 - URI: {}, 异常信息: {}", request.getRequestURI(), e.getMessage());
        
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("参数校验失败");
        
        Response<String> response = Response.<String>builder()
                .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                .info(errorMessage)
                .data(null)
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response<String>> handleBindException(BindException e, HttpServletRequest request) {
        log.warn("参数绑定异常 - URI: {}, 异常信息: {}", request.getRequestURI(), e.getMessage());
        
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("参数绑定失败");
        
        Response<String> response = Response.<String>builder()
                .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                .info(errorMessage)
                .data(null)
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理约束校验异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response<String>> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        log.warn("约束校验异常 - URI: {}, 异常信息: {}", request.getRequestURI(), e.getMessage());
        
        String errorMessage = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("约束校验失败");
        
        Response<String> response = Response.<String>builder()
                .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                .info(errorMessage)
                .data(null)
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response<String>> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("非法参数异常 - URI: {}, 异常信息: {}", request.getRequestURI(), e.getMessage());
        
        Response<String> response = Response.<String>builder()
                .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                .info(e.getMessage())
                .data(null)
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Response<String>> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        log.error("空指针异常 - URI: {}, 异常信息: {}", request.getRequestURI(), e.getMessage(), e);
        
        Response<String> response = Response.<String>builder()
                .code(ResponseCode.UN_ERROR.getCode())
                .info("系统内部错误，请联系管理员")
                .data(null)
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Response<String>> handleException(Exception e, HttpServletRequest request) {
        log.error("未知异常 - URI: {}, 异常信息: {}", request.getRequestURI(), e.getMessage(), e);
        
        Response<String> response = Response.<String>builder()
                .code(ResponseCode.UN_ERROR.getCode())
                .info("系统异常，请联系管理员")
                .data(null)
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
