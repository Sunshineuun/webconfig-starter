package icu.uun.starter.enums.commone;

/**
 * 性别
 *
 * @author qiushengming
 */
public enum SexEnum {
    /**
     * 性别
     */
    M(1, "男"),
    W(2, "女"),
    ;


    private final int code;
    private final String msg;

    SexEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
