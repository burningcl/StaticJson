package com.skyline.json.staticjson.generator.util;

import com.skyline.json.staticjson.core.util.LoggerHolder;
import javassist.NotFoundException;
import javassist.bytecode.SignatureAttribute;

import java.util.*;
import java.util.concurrent.*;

/**
 * 生成工具类
 * Created by chenliang on 2017/4/13.
 */
public class GenUtils {

    /**
     * TAG
     */
    private final static String TAG = "GenUtils";

    /**
     * @param targetClassName
     * @return
     */
    public static String getJsonConverterName(String targetClassName) {
        return targetClassName + ".JsonConverter";
    }

    /**
     * 未知类型
     */
    public static final int TYPE_OTHER = 0;

    /**
     * Iterable类型
     */
    public static final int TYPE_ITERABLE = TYPE_OTHER + 1;

    /**
     * Map类型
     */
    public static final int TYPE_MAP = TYPE_ITERABLE + 1;

    /**
     * 可用的Iterable类
     */
    public static final Class[] AVAILABLE_ITERABLE_CLASSES = {
            ArrayList.class,
            LinkedList.class,
            HashSet.class,
            ConcurrentLinkedQueue.class,
            ArrayDeque.class,
            TreeSet.class,
            LinkedBlockingDeque.class,
            ArrayBlockingQueue.class,
            PriorityBlockingQueue.class,
            PriorityQueue.class,
    };

    /**
     * 可用的Map类
     */
    public static final Class[] AVAILABLE_MAP_CLASSES = {
            HashMap.class,
            ConcurrentHashMap.class,
            Hashtable.class,
            TreeMap.class,
            LinkedHashMap.class
    };

    /**
     * 获取clazz的类型
     *
     * @param clazz
     * @return 类型： TYPE_ITERABLE，TYPE_MAP，TYPE_OTHER
     */
    public static int getType(Class<?> clazz) {
        if (clazz == null) {
            return TYPE_OTHER;
        } else if (clazz.equals(Iterable.class)) {
            return TYPE_ITERABLE;
        } else if (clazz.equals(Map.class)) {
            return TYPE_MAP;
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> cur : interfaces) {
            int type = getType(cur);
            if (type != TYPE_OTHER) {
                return type;
            }
        }
        return getType(clazz.getSuperclass());
    }

    /**
     * @param clazz
     * @return
     */
    public static Class<?> getTypedClass(Class<?> clazz, int type) {
        if (clazz.isInterface()) {
            return getTypedClass4Interface(clazz, type);
        } else {
            return getTypedClass4Class(clazz, type);
        }
    }

    /**
     * @param clazz
     * @return
     */
    public static Class<?> getIterableClass(Class<?> clazz) {
        Class<?> retClass = getTypedClass(clazz, TYPE_ITERABLE);
        if (retClass == null)
            LoggerHolder.logger.error(TAG, "getIterableClass, input: " + clazz + ", ret: " + retClass, null);
        return retClass;
    }

    /**
     * @param clazz
     * @return
     */
    public static Class<?> getMapClass(Class<?> clazz) {
        return getTypedClass(clazz, TYPE_MAP);
    }

    /**
     * @param clazz
     * @param type
     * @return
     */
    public static Class<?> getTypedClass4Interface(Class<?> clazz, int type) {
        Class[] classes = type == TYPE_ITERABLE ? AVAILABLE_ITERABLE_CLASSES : AVAILABLE_MAP_CLASSES;
        for (Class<?> test : classes) {
            if (instanceOf(test, clazz)) {
                return test;
            }
        }
        return null;
    }

    /**
     * 判断test这个类型是否是target类型的
     *
     * @param test
     * @param target
     * @return
     */
    public static boolean instanceOf(final Class<?> test, final Class<?> target) {
        if (test == null) {
            return false;
        } else if (test.equals(target)) {
            return true;
        }
        Class[] interfaces = test.getInterfaces();
        if (interfaces != null && interfaces.length > 0) {
            for (Class<?> curTest : interfaces) {
                if (instanceOf(curTest, target)) {
                    return true;
                }
            }
        }
        return instanceOf(test.getSuperclass(), target);
    }

    /**
     * @param clazz
     * @param type
     * @return
     */
    public static Class<?> getTypedClass4Class(Class<?> clazz, int type) {
        Class[] classes = type == TYPE_ITERABLE ? AVAILABLE_ITERABLE_CLASSES : AVAILABLE_MAP_CLASSES;
        for (Class<?> test : classes) {
            if (isSuperclass(test, clazz)) {
                return test;
            }
        }
        return null;
    }

    /**
     * 判断test类型是否是target类型的子类
     *
     * @param test
     * @param target
     * @return
     */
    public static boolean isSuperclass(final Class<?> test, final Class<?> target) {
        if (test == null) {
            return false;
        } else if (test.equals(target)) {
            return true;
        }
        return isSuperclass(test.getSuperclass(), target);
    }

    /**
     *
     * @param type
     * @return
     * @throws NotFoundException
     */
    public static SignatureAttribute.TypeArgument[] getSubTypeArguments(SignatureAttribute.ObjectType type) throws NotFoundException {
        if (type instanceof SignatureAttribute.ClassType) {
            SignatureAttribute.ClassType classType = (SignatureAttribute.ClassType) type;
            return classType.getTypeArguments();
        }
        return null;
    }

    /**
     *
     * @param type
     * @return
     * @throws NotFoundException
     */
    public static String getTypeName(SignatureAttribute.Type type) throws NotFoundException {
        String typeName = null;
        if (type instanceof SignatureAttribute.ClassType) {
            SignatureAttribute.ClassType classType = (SignatureAttribute.ClassType) type;
            typeName = classType.getName();
            if (!typeName.contains(".") && classType.getDeclaringClass() != null) {
                typeName = classType.getDeclaringClass().getName() + "$" + typeName;
            }
        } else if (type instanceof SignatureAttribute.ArrayType) {
            SignatureAttribute.ArrayType arrayType = (SignatureAttribute.ArrayType) type;
            typeName = getTypeName(arrayType.getComponentType()) + "[]";
        }
        LoggerHolder.logger.debug(TAG, "getTypeName, typeName: " + typeName + ", type: " + type);
        return typeName;
    }

}
