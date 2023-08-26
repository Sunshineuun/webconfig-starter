package icu.uun.starter.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author qiushengming
 */
@Data
@Accessors(chain = true)
public class ExDto {
    private Map<String, String> head;
    private String params;
    private String exClass;
    private Integer exCode;
    private String msg;
    private String stackTrace;
}
