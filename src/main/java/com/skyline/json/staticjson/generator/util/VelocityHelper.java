package com.skyline.json.staticjson.generator.util;

import com.skyline.json.staticjson.generator.ConverterGenerator;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.util.Properties;

/**
 * Created by chenliang on 2017/4/12.
 */
public class VelocityHelper {

    static VelocityEngine VE;

    public static boolean LOAD_FROM_CLASS_PATH = true;

    /**
     * @return
     */
    public static VelocityEngine getVelocityEngine() {
        if (VE == null) {
            if (LOAD_FROM_CLASS_PATH) {
                Properties properties = new Properties();
                properties.setProperty("resource.loader", "class");
                properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
                VE = new VelocityEngine(properties);
            } else {
                Properties properties = new Properties();
                properties.setProperty("resource.loader", "jar");
                properties.setProperty("jar.resource.loader.class", "org.apache.velocity.runtime.resource.loader.JarResourceLoader");
                properties.setProperty("jar.resource.loader.path", "jar:file:" + VelocityHelper.class.getProtectionDomain().getCodeSource().getLocation().getFile());
                VE = new VelocityEngine(properties);
            }
            VE.init();
        }
        return VE;
    }

}
