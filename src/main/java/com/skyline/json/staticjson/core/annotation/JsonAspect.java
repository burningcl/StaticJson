package com.skyline.json.staticjson.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chenliang on 2017/4/25.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface JsonAspect {

    /**
     * 获取action
     *
     * @return
     */
    Action action();

    enum Action {

        /**
         * 序列化
         */
        SERIALIZATION,

        /**
         * 反序列化
         */
        DESERIALIZATION
    }

}
