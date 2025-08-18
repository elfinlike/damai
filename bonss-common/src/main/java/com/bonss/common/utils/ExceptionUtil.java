package com.bonss.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;

import com.bonss.common.exception.ServiceException;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * 错误信息处理类。
 *
 * @author hzx
 */
public class ExceptionUtil
{
    /**
     * 获取exception的详细错误信息。
     */
    public static String getExceptionMessage(Throwable e)
    {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }

    public static String getRootErrorMessage(Exception e)
    {
        Throwable root = ExceptionUtils.getRootCause(e);
        root = (root == null ? e : root);
        if (root == null)
        {
            return "";
        }
        String msg = root.getMessage();
        if (msg == null)
        {
            return "null";
        }
        return StringUtils.defaultString(msg);
    }

    public static void throwException(Class<? extends RuntimeException> exceptionClass, String message) {
        try {
            Constructor<? extends RuntimeException> constructor = exceptionClass.getConstructor(String.class);
            throw constructor.newInstance(message);
        } catch (ReflectiveOperationException e) {
            throw new ServiceException("创建异常实例失败");
        }
    }
}
