package icu.uun.starter.dto;

import icu.uun.starter.enums.commone.BaseCodeEnum;
import lombok.Data;
import org.springframework.util.DigestUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class ResDto<T> implements Serializable {
    private static final long serialVersionUID = -5809782578272943999L;

    private Integer code;
    private String msg;
    private Map<String, Object> result;
    private T data;
    private String sign;
    private String subcode;
    private Boolean success = false;

    public ResDto() {
        this.code = BaseCodeEnum.SUCCESS.getCode();
        this.msg = BaseCodeEnum.SUCCESS.getMessage();
        this.success = true;
    }

    public void setCodeAndMsg(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public void setCodeAndMsg(Integer code, String msg, String subcode) {
        this.code = code;
        this.msg = msg;
        this.subcode = subcode;
    }

    public ResDto<T> addObject(String name, Object o) {
        if (this.result == null) {
            this.result = new HashMap<>(8);
        }

        this.result.put(name, o);
        return this.signature();
    }

    public ResDto<T> fail(String msg) {
        this.code = BaseCodeEnum.FAILED.getCode();
        this.msg = msg;
        this.success = false;
        return this;
    }

    public boolean isSuccess() {
        return BaseCodeEnum.SUCCESS.getCode().equals(this.code);
    }

    public ResDto<T> signature() {
        this.sign = DigestUtils.md5DigestAsHex(this.toString().getBytes());
        return this;
    }

}