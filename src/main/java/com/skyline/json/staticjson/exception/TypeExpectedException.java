package com.skyline.json.staticjson.exception;

/**
 * Created by chenliang on 2017/4/12.
 */
public class TypeExpectedException extends RuntimeException {

    public TypeExpectedException(String field, String currentType, String expectedType) {
        super(
                field + ", expected: " + expectedType + ", but it is " + currentType
        );
    }
}
