package com.skyline.json.staticjson.util;

import com.skyline.json.staticjson.ConverterGenerator;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * Created by chenliang on 2017/4/12.
 */
public class VelocityHelper {

    static VelocityEngine ve;

    /**
     *
     * @return
     */
    public static VelocityEngine getVelocityEngine() {
        if (ve == null) {
            ve = new VelocityEngine();
            String resourceLoader = ConverterGenerator.class.getClassLoader().getResource("vm").getPath();
            ve.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, resourceLoader);
            ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            ve.init();
        }
        return ve;
    }
}
