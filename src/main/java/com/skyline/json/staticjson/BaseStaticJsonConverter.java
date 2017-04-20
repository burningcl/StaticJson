package com.skyline.json.staticjson;

import com.google.gson.stream.JsonReader;
import com.skyline.json.staticjson.util.StringUtil;

import java.io.StringReader;

/**
 * Created by chenliang on 2017/4/13.
 */
public abstract class BaseStaticJsonConverter implements StaticJsonConverter {

    /**
     * @param json
     * @return
     */
    public Object convert2Object(String json) {
        if (StringUtil.isBlank(json)) {
            return null;
        }
        StringReader stringReader = null;
        JsonReader jsonReader = null;
        try {
            stringReader = new StringReader(json);
            jsonReader = new JsonReader(stringReader);
            return this.read(jsonReader);
        } finally {
            try {
                jsonReader.close();
                stringReader.close();
            } catch (Exception ignored) {
            }
        }

    }

}
