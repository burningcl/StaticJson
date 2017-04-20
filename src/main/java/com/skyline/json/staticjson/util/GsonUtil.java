package com.skyline.json.staticjson.util;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

/**
 * Created by chenliang on 2017/4/13.
 */
public class GsonUtil {

    private static Gson GSON;

    public static Gson getGson() {
        if (GSON == null) {
            synchronized (GsonUtil.class) {
                if (GSON == null) {
                    GSON = new Gson();
                }
            }
        }
        return GSON;
    }

    public static String toJson(Object object) {
        return getGson().toJson(object);
    }

}
