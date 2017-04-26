package com.skyline.json.staticjson.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chenliang on 2017/4/25.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.CLASS)
public @interface JsonAspectParam {

    /**
     * @return
     */
    Type type();

    enum Type {

        /**
         * 序列化的实例对象
         */
        OBJECT,

        /**
         * 反序列化的json字符串
         */
        JSON,

        /**
         * 反序列化对象的class
         */
        OBJECT_CLASS

    }

}
