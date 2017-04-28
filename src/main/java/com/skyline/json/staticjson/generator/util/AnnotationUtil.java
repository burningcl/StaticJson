package com.skyline.json.staticjson.generator.util;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

import java.lang.annotation.Annotation;

/**
 * 注解工具类
 * Created by chenliang on 2017/4/11.
 */
public final class AnnotationUtil {

    /**
     * @param annotations
     * @param annotationType
     * @param <T>
     * @return
     * @throws ClassNotFoundException
     */
    public static <T> T getAnnotation(Object[] annotations, Class<T> annotationType) throws ClassNotFoundException {
        if (annotations != null) {
            for (Object annotation : annotations) {
                if (((Annotation) annotation).annotationType().equals(annotationType)) {
                    return (T) annotation;
                }
            }
        }
        return null;
    }

    /**
     * 获取Class上类型为annotationType的注解
     *
     * @param clazz
     * @param annotationType
     * @param <T>
     * @return
     * @throws ClassNotFoundException
     */
    public static <T> T getAnnotation4Class(CtClass clazz, Class<T> annotationType) throws ClassNotFoundException {
        return getAnnotation(clazz.getAnnotations(), annotationType);
    }

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
        return getAnnotation(field.getAnnotations(), annotationType);
    }


    /**
     * 获取Method上类型为annotationType的注解
     *
     * @param method
     * @param annotationType
     * @param <T>
     * @return
     * @throws ClassNotFoundException
     */
    public static <T> T getAnnotation4Method(CtMethod method, Class<T> annotationType) throws ClassNotFoundException {
        return getAnnotation(method.getAnnotations(), annotationType);
    }
}
