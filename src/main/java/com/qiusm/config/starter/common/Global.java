package com.qiusm.config.starter.common;

import com.qiusm.parent.base.model.BaseDTOCode;

/**
 * @author qiushengming
 */
public final class Global {
    public static final String ATTRIBUTE_MESSAGE_PREFIX = "validator.attribute.";
    public static final String CODE_SUCCESS;
    public static final String CODE_FAIL;
    public static final String CODE_ERROR;

    public Global() {
    }


    static {
        CODE_SUCCESS = BaseDTOCode.SUCCESS.getCode();
        CODE_FAIL = BaseDTOCode.FAILED.getCode();
        CODE_ERROR = BaseDTOCode.ERROR.getCode();
    }
}