package com.skyline.json.staticjson.core.annotation;

import com.google.gson.TypeAdapter;
import com.skyline.json.staticjson.core.TypeAdapterNull;

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
    String jsonName() default "";

    /**
     * 是否忽略该字段
     *
     * @return
     */
    boolean ignored() default false;

    /**
     * 如果该字段在json中与在instance中类型不一致时，
     * 可以使用TypeAdapter来进行转换
     *
     * @return
     */
    Class<? extends TypeAdapter> typeAdapter() default TypeAdapterNull.class;
}
