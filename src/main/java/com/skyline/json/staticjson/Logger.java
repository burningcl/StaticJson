package com.skyline.json.staticjson;

/**
 * Created by chenliang on 2017/4/10.
 */
public interface Logger {

    void debug(String tag, String msg);

    void info(String tag, String msg);

    void warn(String tag, String msg);

    void error(String tag, String msg, Exception e);

}
