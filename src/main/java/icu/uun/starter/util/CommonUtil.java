package icu.uun.starter.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author qiushengming
 */
public class CommonUtil {

    /**
     * 获取堆栈信息
     * @param e
     * @return
     */
    public static StringWriter toStackTraceStr(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw;
    }
}
