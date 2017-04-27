package com.skyline.json.staticjson.performance;

import com.google.gson.Gson;
import com.skyline.json.staticjson.core.PrintLogger;
import com.skyline.json.staticjson.core.StaticJsonConverter;
import com.skyline.json.staticjson.core.annotation.JsonAspect;
import com.skyline.json.staticjson.core.annotation.JsonAspectParam;
import com.skyline.json.staticjson.core.util.LoggerHolder;
import com.skyline.json.staticjson.generator.ConverterGenerator;
import com.skyline.json.staticjson.generator.JsonAspectInjector;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by chenliang on 2017/4/26.
 */
public class JsonUtil {

    static Gson gson = new Gson();

    @JsonAspect(action = JsonAspect.Action.SERIALIZATION)
    public static String toJson(@JsonAspectParam(type = JsonAspectParam.Type.OBJECT) Object object) {
        return gson.toJson(object);
    }

    @JsonAspect(action = JsonAspect.Action.DESERIALIZATION)
    public static <T> T fromJson(
            @JsonAspectParam(type = JsonAspectParam.Type.JSON) String json,
            @JsonAspectParam(type = JsonAspectParam.Type.OBJECT_CLASS) Class<T> clazz
    ) {
        return (T) gson.fromJson(json, clazz);
    }

}
