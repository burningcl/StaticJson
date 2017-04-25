package com.skyline.json.staticjson.generator.util;

import javassist.ClassPool;

/**
 * Created by chenliang on 2017/4/12.
 */
public class ClassPoolHelper {

    private static ClassPool CLASS_POOL;

    public static ClassPool getClassPool() {
        if (CLASS_POOL == null) {
            CLASS_POOL = ClassPool.getDefault();
            CLASS_POOL.appendSystemPath();
        }
        return CLASS_POOL;
    }
}
