package com.skyline.json.staticjson.core;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by chenliang on 2017/4/13.
 */
public interface StaticJsonConverter {

    /**
     * @param object
     * @return
     */
    String convert2Json(Object object) throws IOException;

    /**
     * @param object
     * @param jsonWriter
     * @return
     */
    void write(Object object, JsonWriter jsonWriter) throws IOException;

    /**
     * @param json
     * @return
     */
    Object convert2Object(String json);

    /**
     * @param jsonReader
     * @return
     */
    Object read(JsonReader jsonReader);

}
