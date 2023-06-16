package icu.uun.starter.common;

import com.qiusm.parent.base.model.BaseDTOCode;

/**
 * @author qiushengming
 */
public final class Global {
    public static final String ATTRIBUTE_MESSAGE_PREFIX = "validator.attribute.";
    public static final String CODE_SUCCESS;
    public static final String CODE_FAIL;
    public static final String CODE_ERROR;
    public static final String CODE_ACCESS;

    public Global() {
    }


    static {
        CODE_SUCCESS = BaseDTOCode.SUCCESS.getCode();
        CODE_FAIL = BaseDTOCode.FAILED.getCode();
        CODE_ERROR = BaseDTOCode.ERROR.getCode();
        CODE_ACCESS = BaseDTOCode.ACCESS.getCode();
    }
}