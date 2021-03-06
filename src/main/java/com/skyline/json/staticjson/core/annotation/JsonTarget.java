package com.skyline.json.staticjson.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chenliang on 2017/4/10.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface JsonTarget {

    Type type() default Type.CLASS;

    enum Type {
        CLASS,
        UTIL
    }
}
