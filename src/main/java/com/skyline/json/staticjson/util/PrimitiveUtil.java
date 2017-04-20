package com.skyline.json.staticjson.util;

import javassist.CtClass;
import javassist.NotFoundException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by chenliang on 2017/4/11.
 */
public class PrimitiveUtil {

    static Map<String, Integer> PRIMITIVE_DATA_TYPE_MAP = null;

    public static String[] TYPES = {
            "byte",
            "short",
            "int",
            "long",
            "float",
            "double",
            "boolean",
            "char"
    };

    public static String[] WRAPPED_TYPES = {
            Byte.class.getName(),
            Short.class.getName(),
            Integer.class.getName(),
            Long.class.getName(),
            Float.class.getName(),
            Double.class.getName(),
            Boolean.class.getName(),
            Character.class.getName()
    };

    public static String[] JSON_GET_METHOD = {
            "getAsByte",
            "getAsShort",
            "getAsInt",
            "getAsLong",
            "getAsFloat",
            "getAsDouble",
            "getAsBoolean",
            "getAsCharacter"};

    static {
        PRIMITIVE_DATA_TYPE_MAP = new HashMap<String, Integer>();
        for (int i = 0; i < TYPES.length; i++) {
            PRIMITIVE_DATA_TYPE_MAP.put(TYPES[i], i);
        }
    }

    static Map<String, Integer> PRIMITIVE_WRAPPED_TYPE_MAP = null;

    static {
        PRIMITIVE_WRAPPED_TYPE_MAP = new HashMap<String, Integer>();
        for (int i = 0; i < WRAPPED_TYPES.length; i++) {
            PRIMITIVE_WRAPPED_TYPE_MAP.put(WRAPPED_TYPES[i], i);
        }
    }

    /**
     * 获取具体是哪一种基础数据类型
     *
     * @param ctClass
     * @return
     */
    public static int getPrimitiveType(CtClass ctClass) {
        String className = ctClass.getName();
        Integer index = PRIMITIVE_DATA_TYPE_MAP.get(className);
        if (index != null) {
            return index;
        }
        index = PRIMITIVE_WRAPPED_TYPE_MAP.get(className);
        return index != null ? index : -1;
    }

    /**
     * 是否为基础数据类型
     *
     * @param ctClass
     * @return
     */
    public static boolean isPrimitiveDataType(CtClass ctClass) {
        return PRIMITIVE_DATA_TYPE_MAP.containsKey(ctClass.getName());
    }

    /**
     * 是否为基础数据的封装类型
     *
     * @param ctClass
     * @return
     */
    public static boolean isPrimitiveWrappedType(CtClass ctClass) {
        return PRIMITIVE_WRAPPED_TYPE_MAP.containsKey(ctClass.getName());
    }
}
