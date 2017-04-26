package com.skyline.json.staticjson.test;

import com.google.gson.Gson;
import com.skyline.json.staticjson.core.PrintLogger;
import com.skyline.json.staticjson.core.StaticJsonConverter;
import com.skyline.json.staticjson.core.util.LoggerHolder;
import com.skyline.json.staticjson.generator.ConverterGenerator;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Created by chenliang on 2017/4/26.
 */
public class JsonAspectInjectorTest {

    static StaticJsonConverter staticJsonConverter;
    static Gson gson;

    @BeforeClass
    public static void before() throws NotFoundException, ClassNotFoundException, CannotCompileException, BadBytecode, IOException, IllegalAccessException, InstantiationException {
        LoggerHolder.logger = new PrintLogger();
        ClassPool pool = ClassPool.getDefault();
        pool.appendSystemPath();
        CtClass ctClass = pool.get(PrimitiveTest.TestClass.class.getName());
        ConverterGenerator converter = new ConverterGenerator();
        converter.gen(ctClass);
        staticJsonConverter = (StaticJsonConverter) Class.forName(PrimitiveTest.TestClass.class.getName() + "$JsonConverter").newInstance();
        //converter.inject(pool.get(JsonUtil.class.getName()));
        gson = new Gson();
    }

    @Test
    public void test() throws NoSuchFieldException, IllegalAccessException {


        int n = 10;
        PrimitiveTest.TestClass[] objs = new PrimitiveTest.TestClass[n];
        for (int i = 0; i < n; i++) {
            objs[i] = new PrimitiveTest.TestClass();
        }
        String[] str1 = new String[n];
        String[] str2 = new String[n];
        long tt1 = System.nanoTime();
        for (int i = 0; i < n; i++) {
            str2[i] = gson.toJson(objs[i]);
        }
        long tt2 = System.nanoTime();
        System.gc();
        long tt3 = System.nanoTime();
        for (int i = 0; i < n; i++) {
            str1[i] = JsonUtil.toJson(objs[i]);
        }
        long tt4 = System.nanoTime();

        System.out.println((tt2 - tt1));
        System.out.println((tt4 - tt3));
        System.out.println((double) (tt2 - tt1) /(tt4 - tt3) );


        int[] arrays = new int[n];
        long t1 = System.nanoTime();
        for (int i = 0; i < n; i++) {
            arrays[i] = objs[i].anInt;
        }
        long t2 = System.nanoTime();
        Field field = PrimitiveTest.TestClass.class.getDeclaredField("anInt");
        field.setAccessible(true);
        System.gc();
        long t3 = System.nanoTime();
        for (int i = 0; i < n; i++) {
            arrays[i] = field.getInt(objs[i]);
        }
        long t4 = System.nanoTime();
        System.out.println((double) (t4 - t3) / (t2 - t1));
    }
}
