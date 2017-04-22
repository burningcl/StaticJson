package com.skyline.json.staticjson;

/**
 * Created by chenliang on 2017/4/12.
 */
public class PrintLogger implements Logger {

    public void debug(String tag, String msg) {
        System.out.println("debug: " + tag + ", " + msg);
    }

    public void info(String tag, String msg) {
        System.out.println("info: " + tag + ", " + msg);
    }

    public void warn(String tag, String msg) {
        System.out.println("warn: " + tag + ", " + msg);
    }

    public void error(String tag, String msg, Exception e) {
        System.out.println("error: " + tag + ", " + msg);
        if (e != null) {
            e.printStackTrace();
        }
    }
}
