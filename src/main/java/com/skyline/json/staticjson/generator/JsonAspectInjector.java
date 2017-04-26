package com.skyline.json.staticjson.generator;

import com.skyline.json.staticjson.core.JsonInterceptor;
import com.skyline.json.staticjson.core.annotation.JsonAspect;
import com.skyline.json.staticjson.core.annotation.JsonAspectParam;
import com.skyline.json.staticjson.core.util.LoggerHolder;
import com.skyline.json.staticjson.generator.util.AnnotationUtil;
import com.skyline.json.staticjson.generator.util.VelocityHelper;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;

/**
 * Created by chenliang on 2017/4/26.
 */
public class JsonAspectInjector {

    static final String TAG = "JsonAspectInjector";

    public boolean inject(CtClass ctClass) throws ClassNotFoundException, CannotCompileException {
        if (ctClass == null) {
            return false;
        }
        LoggerHolder.logger.debug(TAG, "inject, ctClass: " + ctClass.getName());
        CtMethod[] ctMethods = ctClass.getDeclaredMethods();
        if (ctMethods == null || ctMethods.length <= 0) {
            return false;
        }
        boolean ret = false;
        for (CtMethod ctMethod : ctMethods) {
            ret = ret | inject(ctMethod);
        }
        return ret;
    }

    public boolean inject(CtMethod ctMethod) throws ClassNotFoundException, CannotCompileException {
        if (ctMethod == null) {
            return false;
        }
        JsonAspect jsonAspect = AnnotationUtil.getAnnotation4Method(ctMethod, JsonAspect.class);
        if (jsonAspect == null) {
            return false;
        }
        if (jsonAspect.action().equals(JsonAspect.Action.SERIALIZATION)) {
            return this.injectSerialization(ctMethod);
        } else if (jsonAspect.action().equals(JsonAspect.Action.DESERIALIZATION)) {
            return this.injectDeserialization(ctMethod);
        }
        return false;
    }

    protected boolean injectSerialization(CtMethod ctMethod) throws ClassNotFoundException, CannotCompileException {
        Object[][] annotations = ctMethod.getParameterAnnotations();
        int argIndex = -1;
        if (annotations.length == 1) {
            argIndex = 0;
        } else {
            for (int i = 0; i < annotations.length; i++) {
                Object[] ans = annotations[i];
                JsonAspectParam jsonAspectParam = AnnotationUtil.getAnnotation(ans, JsonAspectParam.class);
                if (jsonAspectParam != null && jsonAspectParam.type().equals(JsonAspectParam.Type.OBJECT)) {
                    argIndex = i;
                }
            }
        }
        if (argIndex < 0) {
            return false;
        }
        LoggerHolder.logger.debug(TAG, "injectSerialization, ctMethod: " + ctMethod.getName() + ", argIndex: " + argIndex);
        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("inject_serialization.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("argIndex", argIndex + 1);
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        String insertBefore = sw.toString();
        ctMethod.insertBefore(insertBefore);

        return true;
    }

    protected boolean injectDeserialization(CtMethod ctMethod) throws ClassNotFoundException, CannotCompileException {
        Object[][] annotations = ctMethod.getParameterAnnotations();
        int jsonIndex = -1;
        int classIndex = -1;
        for (int i = 0; i < annotations.length; i++) {
            Object[] ans = annotations[i];
            JsonAspectParam jsonAspectParam = AnnotationUtil.getAnnotation(ans, JsonAspectParam.class);
            if (jsonAspectParam != null) {
                if (jsonAspectParam.type().equals(JsonAspectParam.Type.JSON)) {
                    jsonIndex = i;
                } else if (jsonAspectParam.type().equals(JsonAspectParam.Type.OBJECT_CLASS)) {
                    classIndex = i;
                }
            }
        }
        if (jsonIndex < 0 || classIndex < 0) {
            return false;
        }
        LoggerHolder.logger.debug(TAG, "injectDeserialization, ctMethod: " + ctMethod.getName() + ", jsonIndex: " + jsonIndex + ", classIndex: " + classIndex);
        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("inject_deserialization.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("jsonIndex", jsonIndex + 1);
        ctx.put("classIndex", classIndex + 1);
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        String insertBefore = sw.toString();
        ctMethod.insertBefore(insertBefore);

        return true;
    }

}
