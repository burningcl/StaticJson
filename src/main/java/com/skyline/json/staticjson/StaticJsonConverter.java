package com.skyline.json.staticjson;

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
     * @param json
     * @return
     */
    Object convert2Object(String json);

}
