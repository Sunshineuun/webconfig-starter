package icu.uun.starter.global;

import feign.FeignException;
import icu.uun.base.model.ResDto;
import icu.uun.starter.common.Global;
import icu.uun.starter.exception.UunException;
import icu.uun.starter.util.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

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

/*
    @ExceptionHandler({BaseBusinessException.class})
    public BaseDTO<Object> businessException(HttpServletRequest req, BaseBusinessException exception) {
        String parameterMap = ServletUtils.getParameterMap(req);
        log.warn("businessException ex header:{}", JSON.toJSONString(ServletUtils.parseHeaderMap(req)));
        log.warn("businessException ex info:{}==={}==={}", exception.getClass(), exception.getBusinessCode(),
                exception.getMessage());
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
    }*/

    @ExceptionHandler({FeignException.class})
    public ResDto<Object> feignException(HttpServletRequest req, UunException exception) {
        toExDto(exception.getMessage(), req, exception);
        return this.getResult(99999, exception.getMessage());
    }
    @ExceptionHandler({UunException.class})
    public ResDto<Object> businessException(HttpServletRequest req, UunException exception) {
        toExDto(exception.getMessage(), req, exception);
        return this.getResult(exception.getCode(), exception.getMessage());
    }

    protected ResDto<Object> getResult(Integer code, String message) {
        ResDto<Object> baseDto = new ResDto<>();
        baseDto.setCodeAndMsg(code, message);
        return baseDto;
    }

    @ExceptionHandler({Exception.class})
    public ResDto<Object> handleException(HttpServletRequest req, Exception exception) {
        String msg = "系统正在维护中，请稍候再试.";
        toExDto(msg, req, exception);
        return this.getResult(Integer.parseInt(Global.CODE_ERROR), msg);
    }

    public void toExDto(String msg, HttpServletRequest req, Exception exception) {
//        ExDto exDto = new ExDto()
//                .setParams(ServletUtils.getParameterMap(req))
//                .setExClass(exception.getClass().getName())
//                .setMsg(exception.getMessage())
//                .setHead(ServletUtils.parseHeaderMap(req));
//                .setStackTrace(toStackTraceStr(exception));

        Integer code = -999;
        if (exception instanceof UunException) {
//            exDto.setExCode(((UunException) exception).getCode());
            code = ((UunException) exception).getCode();
        }
        log.error("{},{},{}. \nclass: {} \nparams: {} \nhead: {} \n\nstack trace: {}", msg, code,
                exception.getMessage(), exception.getClass().getName(),
                ServletUtils.getParameterMap(req), ServletUtils.parseHeaderMap(req), toStackTraceStr(exception));
    }

    public StringWriter toStackTraceStr(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw;
    }
}