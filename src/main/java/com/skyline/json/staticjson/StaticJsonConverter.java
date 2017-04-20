package com.skyline.json.staticjson;

import com.google.gson.stream.JsonReader;

import java.util.List;

/**
 * Created by chenliang on 2017/4/13.
 */
public interface StaticJsonConverter {

    /**
     * @param object
     * @return
     */
    String convert2Json(Object object);

    /**
     * @param object
     * @return
     */
    void writer(Object object);

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
