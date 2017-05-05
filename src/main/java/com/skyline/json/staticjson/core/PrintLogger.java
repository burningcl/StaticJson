package com.skyline.json.staticjson.core;

/**
 * Created by chenliang on 2017/4/12.
 */
public class PrintLogger implements Logger {

    public void debug(String tag, String msg) {
        System.out.println("DEBUG: " + tag + ", " + msg);
    }

    public void info(String tag, String msg) {
        System.out.println("INFO: " + tag + ", " + msg);
    }

    public void warn(String tag, String msg) {
        System.out.println("WARNING: " + tag + ", " + msg);
    }

    public void error(String tag, String msg, Exception e) {
        System.out.println("ERROR: " + tag + ", " + msg);
        if (e != null) {
            System.out.println("ERROR: " + tag + ", " + e.getClass());
            System.out.println("ERROR: " + tag + ", " + e.toString());
            System.out.println("ERROR: " + tag + ", " + e.getMessage());
            System.out.println("ERROR: " + tag + ", " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
