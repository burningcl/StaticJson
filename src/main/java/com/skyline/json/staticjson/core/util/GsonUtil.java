package com.skyline.json.staticjson.core.util;

import com.google.gson.Gson;

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
