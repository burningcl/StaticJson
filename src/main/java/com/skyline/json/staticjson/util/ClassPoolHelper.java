package com.skyline.json.staticjson.util;

import javassist.ClassPool;

/**
 * Created by chenliang on 2017/4/12.
 */
public class ClassPoolHelper {

    private static ClassPool classPool;

    public static ClassPool getClassPool() {
        if (classPool == null) {
            classPool = ClassPool.getDefault();
            classPool.appendSystemPath();
        }
        return classPool;
    }
}
