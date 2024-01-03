package icu.uun.starter.util;

/**
 * @author qiushengming
 */
public class ClassUtil {
    /**
     * 判断是否是基础数据类型，即 int,double,long等类似格式
     */
    public static boolean isCommonDataType(Class clazz) {
        return clazz.isPrimitive();
    }

    /**
     * 判断是否是基础数据类型的包装类型
     */
    public static boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }
}
