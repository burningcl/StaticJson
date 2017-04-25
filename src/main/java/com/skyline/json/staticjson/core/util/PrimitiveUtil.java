package com.skyline.json.staticjson.core.util;

import javassist.CtClass;

import java.util.HashMap;
import java.util.Map;

/**
 * 基础数据类型的工具类
 * Created by chenliang on 2017/4/11.
 */
public class PrimitiveUtil {

    /**
     * 基础数据类型数组
     */
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

    /**
     * 基础数据类型的封装类型数组
     */
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

    /**
     * 读取基础数据类型value时，相对应的JsonReader的方法
     */
    public static String[] JSON_GET_METHOD = {
            "nextInt",
            "nextInt",
            "nextInt",
            "nextLong",
            "nextString",
            "nextDouble",
            "nextBoolean",
            "nextString"};

    /**
     * 读取基础数据类型value时，相对应的封装类型的方法
     */
    public static String[] WRAPPED_GET_METHOD = {
            "byteValue",
            "shortValue",
            "intValue",
            "longValue",
            "floatValue",
            "doubleValue",
            "booleanValue",
            "charValue"};

    static Map<String, Integer> PRIMITIVE_DATA_TYPE_MAP = null;

    static {
        PRIMITIVE_DATA_TYPE_MAP = new HashMap<String, Integer>();
        for (int i = 0; i < TYPES.length; i++) {
            PRIMITIVE_DATA_TYPE_MAP.put(TYPES[i], i);
        }
    }

    static Map<String, Integer> PRIMITIVE_WRAPPED_TYPE_MAP = null;

    static {
        PRIMITIVE_WRAPPED_TYPE_MAP = new HashMap<>();
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
    public static int getPrimitiveIndex(CtClass ctClass) {
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
