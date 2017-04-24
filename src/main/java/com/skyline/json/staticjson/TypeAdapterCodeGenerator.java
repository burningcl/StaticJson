package com.skyline.json.staticjson;

import com.google.gson.TypeAdapter;
import com.skyline.json.staticjson.util.LoggerHolder;
import com.skyline.json.staticjson.util.VelocityHelper;
import javassist.CtClass;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;

/**
 * Created by chenliang on 2017/4/24.
 */
public class TypeAdapterCodeGenerator {

    static final String TAG = "TypeAdapterCodeGenerator";

    public static String genSerializationCode(Class<? extends TypeAdapter> adapterClass, CtClass varClass, String varName) {
        if (adapterClass == null) {
            LoggerHolder.logger.warn(TAG, "genSerializationCode, fail, adapterClass is null");
            return "";
        }
        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("serialize_typeadapter.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("typeAdapterName", adapterClass.getName());
        ctx.put("varType", varClass.getName());
        ctx.put("varName", varName);
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        return sw.toString();
    }

    public static String genDeserializationCode(Class<? extends TypeAdapter> adapterClass, CtClass varClass, String varName) {
        if (adapterClass == null) {
            LoggerHolder.logger.warn(TAG, "genDeserializationCode, fail, adapterClass is null");
            return "";
        }
        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("deserialize_typeadapter.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("typeAdapterName", adapterClass.getName());
        ctx.put("varType", varClass.getName());
        ctx.put("varName", varName);
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        return sw.toString();
    }
}
