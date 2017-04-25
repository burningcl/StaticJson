package com.skyline.json.staticjson.generator.util;

import com.skyline.json.staticjson.generator.ConverterGenerator;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * Created by chenliang on 2017/4/12.
 */
public class VelocityHelper {

    static VelocityEngine VE;

    /**
     *
     * @return
     */
    public static VelocityEngine getVelocityEngine() {
        if (VE == null) {
            VE = new VelocityEngine();
            String resourceLoader = ConverterGenerator.class.getClassLoader().getResource("vm").getPath();
            VE.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, resourceLoader);
            VE.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            VE.init();
        }
        return VE;
    }
}
