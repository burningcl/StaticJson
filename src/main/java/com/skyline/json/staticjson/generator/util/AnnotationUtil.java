package com.skyline.json.staticjson.generator.util;

import javassist.CtField;

import java.lang.annotation.Annotation;

/**
 * 注解工具类
 * Created by chenliang on 2017/4/11.
 */
public final class AnnotationUtil {

    /**
     * 获取Field上类型为annotationType的注解
     *
     * @param field
     * @param annotationType
     * @param <T>
     * @return
     * @throws ClassNotFoundException
     */
    public static <T> T getAnnotation4Field(CtField field, Class<T> annotationType) throws ClassNotFoundException {
        Object[] annotations = field.getAnnotations();
        if (annotations != null) {
            for (Object annotation : annotations) {
                if (((Annotation) annotation).annotationType().equals(annotationType)) {
                    return (T) annotation;
                }
            }
        }
        return null;
    }
}
