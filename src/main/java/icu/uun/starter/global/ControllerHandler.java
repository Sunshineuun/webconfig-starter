package icu.uun.starter.global;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import icu.uun.starter.dto.BaseResp;
import icu.uun.starter.dto.ResDto;
import icu.uun.starter.exception.UunException;
import icu.uun.starter.util.ClassUtil;
import icu.uun.starter.util.ServletUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author qiushengming
 */
@Slf4j
@RestControllerAdvice(basePackages = {"icu.uun"})
public class ControllerHandler implements ResponseBodyAdvice<Object> {

    /**
     * @param returnType
     * @param converterType
     * @return false 不调用 beforeBodyWrite;
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Method method = returnType.getMethod();
        if (method == null) {
            return false;
        }
        Class<?> returnTypeClass = returnType.getMethod().getReturnType();
        return BaseResp.class.isAssignableFrom(returnTypeClass)
                || BaseResp.class.equals(returnTypeClass)
                || Page.class.equals(returnTypeClass)
                || ClassUtil.isWrapClass(returnTypeClass)
                || ClassUtil.isCommonDataType(returnTypeClass)
                || List.class.equals(returnTypeClass);
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(
            Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request, ServerHttpResponse response) {
        Method method = returnType.getMethod();
        if (method == null) {
            return body;
        }
        Class<?> returnTypeClass = returnType.getMethod().getReturnType();

        // 包装类型、基本类型、翻页、继承BaseResp的都需要被包裹
        boolean isPack = BaseResp.class.isAssignableFrom(returnTypeClass)
                || BaseResp.class.equals(returnTypeClass)
                || Page.class.equals(returnTypeClass)
                || ClassUtil.isWrapClass(returnTypeClass)
                || ClassUtil.isCommonDataType(returnTypeClass)
                || List.class.equals(returnTypeClass);

        if (isPack) {
            ResDto<Object> resDto = new ResDto<>();
            resDto.setData(body);
            return resDto;
        } else if (body == null) {
            ResDto<Object> resDto = new ResDto<>();
            resDto.setData(body);
            return resDto;
        } else {
            return body;
        }
    }

    @ExceptionHandler({UunException.class})
    public ResDto<Object> businessException(HttpServletRequest req, UunException exception) {
        String parameterMap = ServletUtils.getParameterMap(req);
        log.warn("UserApiException ex header:{}", JSON.toJSONString(ServletUtils.parseHeaderMap(req)));
        log.warn("UserApiException ex info:{}==={}==={}", exception.getClass(), exception.getCode(),
                exception.getMessage());
        log.warn("UserApiException parameter info:{}==={}", req.getRequestURI(), parameterMap);
        return this.getResult(exception.getCode(), exception.getMessage());
    }

    protected ResDto<Object> getResult(Integer code, String message) {
        ResDto<Object> baseDto = new ResDto<>();
        baseDto.setCodeAndMsg(code, message);
        return baseDto;
    }
}