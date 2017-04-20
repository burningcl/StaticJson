package com.skyline.json.staticjson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chenliang on 2017/4/10.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface JsonField {
    /**
     * 在json中该字段的名字
     *
     * @return
     */
    String name() default "";

    /**
     * 是否在序列化过程中忽略该字段
     *
     * @return
     */
    boolean serializationIgnored() default false;

    /**
     * 是否在反序列化过程中忽略该字段
     *
     * @return
     */
    boolean deserializationIgnored() default false;
}
