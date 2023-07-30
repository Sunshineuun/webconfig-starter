package icu.uun.starter.enums.commone;

/**
 * @author qiushengming
 */
public enum BaseCodeEnum {
    /**
     * 状态码
     */
    SUCCESS(0, "Success"),
    FAILED(-1, "操作失败"),
    ACCESS(-998, "非法权限"),
    ERROR(-999, "系统错误"),
    ;

    private final String message;
    private final Integer code;

    BaseCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
