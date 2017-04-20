package com.skyline.json.staticjson.test;

import com.google.gson.Gson;
import com.skyline.json.staticjson.ConverterGenerator;
import com.skyline.json.staticjson.LoggerHolder;
import com.skyline.json.staticjson.PrintLogger;
import com.skyline.json.staticjson.StaticJsonConverter;
import com.skyline.json.staticjson.annotation.JsonTarget;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenliang on 2017/4/20.
 */
public class MapTest {

    static StaticJsonConverter staticJsonConverter;
    static Gson gson;

    @BeforeClass
    public static void before() throws NotFoundException, ClassNotFoundException, CannotCompileException, BadBytecode, IOException, IllegalAccessException, InstantiationException {
        LoggerHolder.logger = new PrintLogger();
        ClassPool pool = ClassPool.getDefault();
        pool.appendSystemPath();
        CtClass ctClass = pool.get(MapTest.TestClass.class.getName());
        new ConverterGenerator().gen(ctClass);

        staticJsonConverter = (StaticJsonConverter) Class.forName(MapTest.TestClass.class.getName() + "$JsonConverter").newInstance();
        gson = new Gson();
    }

    @JsonTarget
    public static class TestClass {
        Map<Integer, String> map;

    }

    private void fill(Map<Integer, String> map) {
        for (int i = 0; i < 3; i++) {
            map.put(i, "value" + i);
        }
    }

    @Test
    public void test1() throws IOException {
        TestClass t = new TestClass();
        t.map = new HashMap<>();
        fill(t.map);
        System.out.println(gson.toJson(t));
        System.out.println(staticJsonConverter.convert2Json(t));
    }

    @Test
    public void test21() {
        String json = "{\"map\":{\"0\":\"value0\",\"1\":\"value1\",\"2\":\"value2\"}}";
        TestClass t1 = (TestClass) staticJsonConverter.convert2Object(json);
        TestClass t2 = gson.fromJson(json, TestClass.class);
        System.out.println(gson.toJson(t1));
        System.out.println(gson.toJson(t2));
    }

}