package com.skyline.json.staticjson.core;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.skyline.json.staticjson.core.util.StringUtil;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * StaticJsonConverter基类
 *
 * Created by chenliang on 2017/4/13.
 */
public abstract class BaseStaticJsonConverter implements StaticJsonConverter {

    public String convert2Json(Object object) throws IOException {
        if (object == null) {
            return null;
        }
        StringWriter stringWriter = null;
        JsonWriter jsonWriter = null;
        try {
            stringWriter = new StringWriter();
            jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.setLenient(true);
            this.write(object, jsonWriter);
        } finally {
            try {
                if (jsonWriter != null)
                    jsonWriter.close();
                if (stringWriter != null)
                    stringWriter.close();
            } catch (Exception ignored) {
            }
        }
        return stringWriter.toString();
    }


    public Object convert2Object(String json) {
        if (StringUtil.isBlank(json)) {
            return null;
        }
        StringReader stringReader = null;
        JsonReader jsonReader = null;
        try {
            stringReader = new StringReader(json);
            jsonReader = new JsonReader(stringReader);
            jsonReader.setLenient(true);
            return this.read(jsonReader);
        } finally {
            try {
                if (stringReader != null) {
                    jsonReader.close();
                }
                if (jsonReader != null) {
                    stringReader.close();
                }
            } catch (Exception ignored) {
            }
        }

    }

}
