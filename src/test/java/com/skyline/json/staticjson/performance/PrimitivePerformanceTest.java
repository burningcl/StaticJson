package com.skyline.json.staticjson.performance;

import com.google.gson.Gson;
import com.skyline.json.staticjson.core.PrintLogger;
import com.skyline.json.staticjson.core.StaticJsonConverter;
import com.skyline.json.staticjson.core.annotation.JsonTarget;
import com.skyline.json.staticjson.core.util.LoggerHolder;
import com.skyline.json.staticjson.generator.ConverterGenerator;
import com.skyline.json.staticjson.test.PrimitiveTest;
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
public class PrimitivePerformanceTest {

    static StaticJsonConverter staticJsonConverter;
    static Gson gson;

    @JsonTarget
    public static class TestClass {

        public byte aByte1;

        public Byte aByte;

        public int anInt;

        public Integer integer;

        public short aShort;

        public Short aShort1;

        public long aLong;

        public Long aLong1;

        public float aFloat;

        public Float aFloat1;

        public double aDouble;

        public Double aDouble1;

        public char aChar;

        public Character character;

        public boolean aBoolean;

        public Boolean aBoolean1;

    }

    @BeforeClass
    public static void before() throws NotFoundException, ClassNotFoundException, CannotCompileException, BadBytecode, IOException, IllegalAccessException, InstantiationException {
        LoggerHolder.logger = new PrintLogger();
        ClassPool pool = ClassPool.getDefault();
        pool.appendSystemPath();
        CtClass ctClass = pool.get(TestClass.class.getName());
        ConverterGenerator converter = new ConverterGenerator();
        converter.gen(ctClass);
        staticJsonConverter = (StaticJsonConverter) Class.forName(TestClass.class.getName() + "$JsonConverter").newInstance();
        // converter.inject(pool.get(JsonUtil.class.getName()));
        gson = new Gson();
    }

    @Test
    public void testSerialization() throws NoSuchFieldException, IllegalAccessException {
        int n = 100;
        TestClass obj = new TestClass();
        obj.aByte1 = 1;
        obj.aByte = 2;
        obj.anInt = 3;
        obj.integer = 4;
        obj.aShort = 5;
        obj.aShort1 = 6;
        obj.aLong = 7;
        obj.aLong1 = 8l;
        obj.aFloat = 1.3f;
        obj.aFloat1 = 1.1f;
        obj.aDouble = 1.2d;
        obj.aDouble1 = 1.3d;
        obj.aChar = 13;
        obj.character = 14;
        obj.aBoolean = true;
        obj.aBoolean1 = false;
        System.out.println(gson.toJson(obj));

        long[] cost1 = new long[n];
        long[] cost2 = new long[n];
        long t1;
        long t2;
        int j;
        gson.toJson(obj);
        long tt1 = System.nanoTime();
        for (int i = 0; i < n; i++) {
            t1 = System.nanoTime();
            for (j = 0; j < 10; j++) {
                gson.toJson(obj);
            }
            t2 = System.nanoTime();
            cost1[i] = (t2 - t1) / 1000;
        }
        long tt2 = System.nanoTime();
        JsonUtil.toJson(obj);
        long tt3 = System.nanoTime();
        for (int i = 0; i < n; i++) {
            t1 = System.nanoTime();
            for (j = 0; j < 10; j++) {
                JsonUtil.toJson(obj);
            }
            t2 = System.nanoTime();
            cost2[i] = (t2 - t1) / 1000;
        }
        long tt4 = System.nanoTime();
        System.out.println(gson.toJson(cost1));
        System.out.println(gson.toJson(cost2));
        System.out.println((double) (tt2 - tt1) / (tt4 - tt3));


    }

    @Test
    public void testDeserialization() throws NoSuchFieldException, IllegalAccessException {
        String json = "{\"aByte1\":1,\"aByte\":2,\"anInt\":3,\"integer\":4,\"aShort\":5,\"aShort1\":6,\"aLong\":7,\"aLong1\":8,\"aFloat\":1.3,\"aFloat1\":1.1,\"aDouble\":1.2,\"aDouble1\":1.3,\"aChar\":\"\\r\",\"character\":\"\\u000e\",\"aBoolean\":true,\"aBoolean1\":false}\n";

        int n = 100;
        long[] cost1 = new long[n];
        long[] cost2 = new long[n];
        long t1;
        long t2;
        int j;
        gson.fromJson(json, TestClass.class);
        long tt1 = System.nanoTime();
        for (int i = 0; i < n; i++) {
            t1 = System.nanoTime();
            for (j = 0; j < 10; j++) {
                gson.fromJson(json, TestClass.class);
            }
            t2 = System.nanoTime();
            cost1[i] = (t2 - t1) / 1000;
        }
        long tt2 = System.nanoTime();
        JsonUtil.fromJson(json, TestClass.class);
        long tt3 = System.nanoTime();
        for (int i = 0; i < n; i++) {
            t1 = System.nanoTime();
            for (j = 0; j < 10; j++) {
                JsonUtil.fromJson(json, TestClass.class);
            }
            t2 = System.nanoTime();
            cost2[i] = (t2 - t1) / 1000;
        }
        long tt4 = System.nanoTime();
        System.out.println(gson.toJson(cost1));
        System.out.println(gson.toJson(cost2));
        System.out.println((double) (tt2 - tt1) / (tt4 - tt3));
    }
}
