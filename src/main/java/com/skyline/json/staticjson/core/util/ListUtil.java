package com.skyline.json.staticjson.core.util;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Created by chenliang on 2017/4/20.
 */
public class ListUtil {

    public static <T> T toArray(List list, Class<T> arrayType) {
        if (list == null) {
            return null;
        }
        Class<?> elementType = arrayType.getComponentType();
        Object array = Array.newInstance(elementType, list.size());
        for (int i = 0; i < list.size(); i++) {
            Array.set(array, i, list.get(i));
        }
        return (T)array;
    }
}