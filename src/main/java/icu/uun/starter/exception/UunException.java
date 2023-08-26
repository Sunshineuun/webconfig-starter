package icu.uun.starter.exception;

import icu.uun.base.enums.BaseCodeEnum;

/**
 * @author qiushengming
 */
public class UunException extends RuntimeException {
    private Integer code;

    public UunException(String message) {
        super(message);
        this.code = -1;
    }

    public UunException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public UunException(BaseCodeEnum resCode) {
        super(resCode.getMessage());
        this.code = resCode.getCode();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
