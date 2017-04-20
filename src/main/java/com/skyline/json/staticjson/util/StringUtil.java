package com.skyline.json.staticjson.util;

import javassist.CtClass;
import javassist.NotFoundException;

/**
 * Created by chenliang on 2017/4/11.
 */
public class StringUtil {

    /**
     * 是否为空
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        return (str == null) || str.length() <= 0;
    }

    /**
     * 是否为字符串
     *
     * @param ctClass
     * @return
     * @throws NotFoundException
     */
    public static boolean isString(CtClass ctClass) throws NotFoundException {
        return ctClass.getName().equals(String.class.getName());
    }
}
