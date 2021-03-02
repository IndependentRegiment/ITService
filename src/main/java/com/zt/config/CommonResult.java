package com.zt.config;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

/*
 * 功能描述: <br>
 * 〈统一返回格式〉
 * @Param:
 * @Return:
 * @Author:
 * @Date:
 */
@Getter
@Setter
@Repository
@ApiModel
public class CommonResult<T> {
    //@ApiModelProperty(value = "返回码", dataType = "String")
    private long code;
    //@ApiModelProperty(value = "提示信息", dataType = "String")
    private String message;
    //@ApiModelProperty(value = "返回值", dataType = "Object")
    private T data;

    protected CommonResult() {}
    // 返回结果
    protected CommonResult(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // request success
    public static<T> CommonResult<T> success(T data) {
        return new CommonResult<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    // request success point message
    public static<T> CommonResult<T> success(T data, String message) {
        return new CommonResult<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    // request error
    public static<T> CommonResult<T> failed(IErrorCode errorCode) {
        return  new CommonResult<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    // request error point message
    public static<T> CommonResult<T> failed(String message) {
        return new CommonResult<>(ResultCode.FAILED.getCode(), message, null);
    }

    // request error
    public static<T> CommonResult<T> failed() {
        return failed(ResultCode.FAILED);
    }

    // validate error
    public static<T> CommonResult<T> validateFailed() {
        return failed(ResultCode.VALIDATE_FAILED);
    }

    // validate error point message
    public static<T> CommonResult<T> validateFailed(String message) {
        return new CommonResult<>(ResultCode.VALIDATE_FAILED.getCode(), message, null);
    }

    // not login
    public static<T> CommonResult<T> unauthorized(T data) {
        return new CommonResult<>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(), data);
    }

    // not authorized
    public static<T> CommonResult<T> forbidden(T data) {
        return new CommonResult<>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage(), data);
    }
}
