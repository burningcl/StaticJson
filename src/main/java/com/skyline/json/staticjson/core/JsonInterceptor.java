package com.skyline.json.staticjson.core;

import java.io.IOException;

/**
 * Created by chenliang on 2017/4/25.
 */
public class JsonInterceptor {

    /**
     * InterceptResult
     */
    public static class InterceptResult {

        public boolean success;

        public Object obj;

        public InterceptResult(boolean success, Object obj) {
            this.success = success;
            this.obj = obj;
        }
    }

    /**
     * 序列化
     *
     * @param obj
     * @return
     * @throws IOException
     */
    public static InterceptResult doSerialization(Object obj) throws IOException {
        if (obj == null) {
            return new InterceptResult(false, null);
        }
        Class<?> clazz = obj.getClass();
//        if (obj instanceof Iterable) {
//            Iterable iterable = (Iterable)obj;
//            Iterator iterator= iterable.iterator();
//            iterator.
//        } else if (clazz.isArray()) {
//            Class<?> componentType = clazz.getComponentType();
//            if (componentType != null) {
//                Object[] array = (Object[]) obj;
//
//            }
//        } else
        if (obj instanceof StaticJsonObject) {
            StaticJsonConverter converter = JsonConverterFactory.get(clazz);
            if (converter != null) {
                return new InterceptResult(true, converter.convert2Json(obj));
            }
        }
        return new InterceptResult(false, null);
    }


    /**
     * 反序列化
     *
     * @param json
     * @param clazz
     * @return
     * @throws IOException
     */
    public static InterceptResult doDeserialization(String json, Class<?> clazz) throws IOException {
        if (json != null && clazz != null) {
            StaticJsonConverter converter = JsonConverterFactory.get(clazz);
            if (converter != null) {
                return new InterceptResult(true, converter.convert2Object(json));
            }
        }
        return new InterceptResult(false, null);
    }
}
