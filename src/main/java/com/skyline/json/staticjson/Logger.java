package com.skyline.json.staticjson;

/**
 * Created by chenliang on 2017/4/10.
 */
public interface Logger {

    /**
     * debug
     *
     * @param tag
     * @param msg
     */
    void debug(String tag, String msg);

    /**
     * info
     *
     * @param tag
     * @param msg
     */
    void info(String tag, String msg);

    /**
     * warn
     *
     * @param tag
     * @param msg
     */
    void warn(String tag, String msg);

    /**
     * error
     *
     * @param tag
     * @param msg
     * @param e
     */
    void error(String tag, String msg, Exception e);

}
