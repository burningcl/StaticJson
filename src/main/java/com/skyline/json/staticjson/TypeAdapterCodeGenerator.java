package com.skyline.json.staticjson;

import com.google.gson.TypeAdapter;
import com.skyline.json.staticjson.deserialize.ValueSetterGenerator;
import com.skyline.json.staticjson.serialize.ValueGetterGenerator;
import com.skyline.json.staticjson.util.LoggerHolder;
import com.skyline.json.staticjson.util.PrimitiveUtil;
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

    /**
     * @param adapterClass
     * @param varClass
     * @param varName
     * @return
     */
    public static String genSerializationCode(Class<? extends TypeAdapter> adapterClass, CtClass varClass, String varName) {
        if (adapterClass == null) {
            LoggerHolder.logger.warn(TAG, "genSerializationCode, fail, adapterClass is null");
            return "";
        }
        boolean isPrimitiveDataType = false;
        String wrappedClassName = varClass.getName();
        if (PrimitiveUtil.isPrimitiveDataType(varClass)) {
            isPrimitiveDataType = true;
            wrappedClassName = PrimitiveUtil.WRAPPED_TYPES[PrimitiveUtil.getPrimitiveIndex(varClass)];
        }
        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("serialize_typeadapter.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("typeAdapterName", adapterClass.getName());
        ctx.put("varType", varClass.getName());
        ctx.put("varName", varName);
        ctx.put("isPrimitive", isPrimitiveDataType);
        ctx.put("varTmpName", "varTmp" + ValueGetterGenerator.getIndexValue());
        ctx.put("varWrappedType", wrappedClassName);
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        return sw.toString();
    }

    /**
     * @param adapterClass
     * @param varClass
     * @param varName
     * @return
     */
    public static String genDeserializationCode(Class<? extends TypeAdapter> adapterClass, CtClass varClass, String varName) {
        if (adapterClass == null) {
            LoggerHolder.logger.warn(TAG, "genDeserializationCode, fail, adapterClass is null");
            return "";
        }
        boolean isPrimitiveDataType = false;
        String wrappedClassName = varClass.getName();
        String getValueMethod = "";
        if (PrimitiveUtil.isPrimitiveDataType(varClass)) {
            isPrimitiveDataType = true;
            int index = PrimitiveUtil.getPrimitiveIndex(varClass);
            wrappedClassName = PrimitiveUtil.WRAPPED_TYPES[index];
            getValueMethod = PrimitiveUtil.WRAPPED_GET_METHOD[index];
        }
        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("deserialize_typeadapter.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("typeAdapterName", adapterClass.getName());
        ctx.put("varType", varClass.getName());
        ctx.put("varName", varName);
        ctx.put("isPrimitive", isPrimitiveDataType);
        ctx.put("varTmpName", "varTmp" + ValueSetterGenerator.getIndexValue());
        ctx.put("varWrappedType", wrappedClassName);
        ctx.put("getValueMethod", getValueMethod);
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        return sw.toString();
    }
}
