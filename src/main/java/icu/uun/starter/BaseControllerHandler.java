package icu.uun.starter;

import com.alibaba.fastjson.JSON;
import icu.uun.starter.common.Global;
import icu.uun.starter.util.ServletUtils;
import com.qiusm.parent.base.exception.BaseBusinessException;
import com.qiusm.parent.base.model.BaseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 统一异常管理，后续再封装 <br>
 * 1. CAT的集成 <br>
 *
 * @author qiushengming
 */
@RestControllerAdvice
public class BaseControllerHandler {
    private static final Logger log = LoggerFactory.getLogger(BaseControllerHandler.class);

    public BaseControllerHandler() {
    }

    private BaseDTO<Object> getResult(String code, String message, String subcode) {
        BaseDTO<Object> baseDTO = new BaseDTO<>();
        baseDTO.setCodeAndMsg(code, message, subcode);
        return baseDTO;
    }

    private BaseDTO<Object> getResult(String code, String message) {
        BaseDTO<Object> baseDTO = new BaseDTO<>();
        baseDTO.setCodeAndMsg(code, message);
        return baseDTO;
    }

    @ExceptionHandler({BaseBusinessException.class})
    public BaseDTO<Object> businessException(HttpServletRequest req, BaseBusinessException exception) {
        String parameterMap = ServletUtils.getParameterMap(req);
        log.warn("businessException ex header:{}", JSON.toJSONString(ServletUtils.parseHeaderMap(req)));
        log.warn("businessException ex info:{}==={}==={}", exception.getClass(), exception.getBusinessCode(), exception.getMessage());
        log.warn("businessException parameter info:{}==={}", req.getRequestURI(), parameterMap);
        // Cat.logEvent("URL.param", parameterMap);
        return this.getResult(exception.getBusinessCode(), exception.getMessage(), exception.getSubcode());
    }

    @ExceptionHandler({IllegalArgumentException.class,
            NoHandlerFoundException.class,
            HttpMessageNotReadableException.class,
            TypeMismatchException.class,
            MissingServletRequestParameterException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            MethodArgumentNotValidException.class})
    public BaseDTO<Object> handle400Exception(HttpServletRequest req, Exception ex) {
        String parameterMap = ServletUtils.getParameterMap(req);
        log.error("handleException ex header:{}", JSON.toJSONString(ServletUtils.parseHeaderMap(req)));
        log.error("handleException ex uri:{},param:{}", req.getRequestURI(), parameterMap);
        log.error("handleException ex class:{},ex:{}", ex.getClass(), ex.getMessage());
        log.error("不支持的操作", ex);
        // Cat.logEvent("URL.param", parameterMap);
        if (ex instanceof IllegalArgumentException) {
            return this.getResult(Global.CODE_FAIL, ex.getMessage());
        } else if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException e = (MethodArgumentNotValidException) ex;
            BindingResult exceptions = e.getBindingResult();
            if (exceptions.hasErrors()) {
                List<ObjectError> errors = exceptions.getAllErrors();
                if (!errors.isEmpty()) {
                    FieldError fieldError = (FieldError) errors.get(0);
                    return this.getResult(Global.CODE_FAIL, fieldError.getDefaultMessage());
                }
            }

            return this.getResult(Global.CODE_FAIL, "请填写正确信息");
        } else {
            return this.getResult(Global.CODE_FAIL, "不支持的操作" + ex.getMessage());
        }
    }

    @ExceptionHandler({Exception.class})
    public BaseDTO<Object> handleException(HttpServletRequest req, Exception exception) {
        log.error("handleException ex header:{}", JSON.toJSONString(ServletUtils.parseHeaderMap(req)));
        log.error("handleException ex uri:{},param:{}", req.getRequestURI(), ServletUtils.getParameterMap(req));
        log.error("handleException ex class:{},ex:{}", exception.getClass(), exception.getMessage());
        log.error("系统正在维护中，请稍候再试", exception);
        // Cat.logError(Global.CODE_ERROR, exception);
        return this.getResult(Global.CODE_ERROR, "系统正在维护中，请稍候再试");
    }

    @ModelAttribute
    public BaseDTO<Object> getBaseDTO() {
        return new BaseDTO();
    }
}