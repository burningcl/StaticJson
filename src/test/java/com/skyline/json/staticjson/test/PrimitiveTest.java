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

/**
 * Created by chenliang on 2017/4/21.
 */
public class PrimitiveTest {

    static StaticJsonConverter staticJsonConverter;
    static Gson gson;

    @BeforeClass
    public static void before() throws NotFoundException, ClassNotFoundException, CannotCompileException, BadBytecode, IOException, IllegalAccessException, InstantiationException {
        LoggerHolder.logger = new PrintLogger();
        ClassPool pool = ClassPool.getDefault();
        pool.appendSystemPath();
        CtClass ctClass = pool.get(PrimitiveTest.TestClass.class.getName());
        new ConverterGenerator().gen(ctClass);

        staticJsonConverter = (StaticJsonConverter) Class.forName(PrimitiveTest.TestClass.class.getName() + "$JsonConverter").newInstance();
        gson = new Gson();
    }

    @JsonTarget
    public static class TestClass {

        byte aByte1;

        Byte aByte;

        int anInt;

        Integer integer;

        short aShort;

        Short aShort1;

        long aLong;

        Long aLong1;

        float aFloat;

        Float aFloat1;

        double aDouble;

        Double aDouble1;

        char aChar;

        Character character;

        boolean aBoolean;

        Boolean aBoolean1;


    }

    @Test
    public void test1() throws IOException {
        TestClass t1 = new TestClass();
        t1.aByte1 = 1;
        t1.aByte = 2;
        t1.anInt = 3;
        t1.integer = 4;
        t1.aShort = 5;
        t1.aShort1 = 6;
        t1.aLong = 7;
        t1.aLong1 = 8l;
        t1.aFloat = 1.3f;
        t1.aFloat1 = 1.1f;
        t1.aDouble = 1.2d;
        t1.aDouble1 = 1.3d;
        t1.aChar = 13;
        t1.character = 14;
        t1.aBoolean = true;
        t1.aBoolean1 = false;

        System.out.println(staticJsonConverter.convert2Json(t1));
        System.out.println(gson.toJson(t1));
    }

    @Test
    public void test11() throws IOException {
        String json = "{\"aByte1\":1,\"aByte\":2,\"anInt\":3,\"integer\":4,\"aShort\":5,\"aShort1\":6,\"aLong\":7,\"aLong1\":8,\"aFloat\":1.3,\"aFloat1\":1.1,\"aDouble\":1.2,\"aDouble1\":1.3,\"aChar\":\"\\r\",\"character\":\"\\u000e\",\"aBoolean\":true,\"aBoolean1\":false}\n";

        TestClass t1 = gson.fromJson(json, TestClass.class);
        System.out.println(gson.toJson(t1));
        TestClass t2 = (TestClass) staticJsonConverter.convert2Object(json);
        System.out.println(staticJsonConverter.convert2Json(t2));
    }

    @Test
    public void test2() throws IOException {
        TestClass t1 = new TestClass();
        System.out.println(staticJsonConverter.convert2Json(t1));
        System.out.println(gson.toJson(t1));
    }

    @Test
    public void test21() throws IOException {
        String json = "{\"aByte1\":0,\"anInt\":0,\"aShort\":0,\"aLong\":0,\"aFloat\":0.0,\"aDouble\":0.0,\"aChar\":\"\\u0000\",\"aBoolean\":false}";
        TestClass t1 = gson.fromJson(json, TestClass.class);
        System.out.println(gson.toJson(t1));
        TestClass t2 = (TestClass) staticJsonConverter.convert2Object(json);
        System.out.println(staticJsonConverter.convert2Json(t2));
    }
}
