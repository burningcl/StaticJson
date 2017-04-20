package com.skyline.json.staticjson.exception;

/**
 * 类型不匹配
 * Created by chenliang on 2017/4/12.
 */
public class TypeNotMatchedException extends RuntimeException {

    public TypeNotMatchedException(String field, String currentType, String expectedType) {
        super(
                field + ", expected: " + expectedType + ", but it is " + currentType
        );
    }
}
