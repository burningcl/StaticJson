package com.skyline.json.staticjson.core;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JsonConverterFactory
 * Created by chenliang on 2017/4/25.
 */
public class JsonConverterFactory {

    /**
     *
     */
    public static final int MAX_SIZE = 128;

    /**
     * CONVERTER_CACHE
     */
    public static Map<Class, StaticJsonConverter> CONVERTER_CACHE = new LinkedHashMap<Class, StaticJsonConverter>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Class, StaticJsonConverter> eldest) {
            return size() > MAX_SIZE;
        }
    };

    /**
     * NO_CONVERTER_CLASSES
     */
    public static Map<Class, Object> NO_CONVERTER_CLASSES = new LinkedHashMap<Class, Object>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Class, Object> eldest) {
            return size() > MAX_SIZE;
        }
    };

    /**
     * 获取StaticJsonConverter实例
     *
     * @param clazz
     * @return
     */
    public static StaticJsonConverter get(Class<?> clazz) {

        if (clazz == null || NO_CONVERTER_CLASSES.containsKey(clazz)) {
            return null;
        }
        StaticJsonConverter converter = CONVERTER_CACHE.get(clazz);
        if (converter != null) {
            return converter;
        }
        synchronized (JsonConverterFactory.class) {
            if (clazz == null || NO_CONVERTER_CLASSES.containsKey(clazz)) {
                return null;
            }
            converter = CONVERTER_CACHE.get(clazz);
            if (converter != null) {
                return converter;
            }
            try {
                String converterClassName = clazz.getName() + "$JsonConverter";
                Class<? extends StaticJsonConverter> converterClass = (Class<? extends StaticJsonConverter>) Class.forName(converterClassName);
                converter = converterClass.newInstance();
                CONVERTER_CACHE.put(clazz, converter);
                return converter;
            } catch (Exception e) {
                NO_CONVERTER_CLASSES.put(clazz, null);
                return null;
            }
        }
    }
}
