package com.skyline.json.staticjson.test;

import com.google.gson.Gson;
import com.skyline.json.staticjson.ConverterGenerator;
import com.skyline.json.staticjson.PrintLogger;
import com.skyline.json.staticjson.util.LoggerHolder;
import com.skyline.json.staticjson.StaticJsonConverter;
import com.skyline.json.staticjson.annotation.JsonTarget;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by chenliang on 2017/4/22.
 */
public class IterableTest {

    static StaticJsonConverter staticJsonConverter;
    static Gson gson;

    @BeforeClass
    public static void before() throws NotFoundException, ClassNotFoundException, CannotCompileException, BadBytecode, IOException, IllegalAccessException, InstantiationException {
        LoggerHolder.logger = new PrintLogger();
        ClassPool pool = ClassPool.getDefault();
        pool.appendSystemPath();
        CtClass ctClass = pool.get(IterableTest.TestClass.class.getName());
        new ConverterGenerator().gen(ctClass);

        staticJsonConverter = (StaticJsonConverter) Class.forName(IterableTest.TestClass.class.getName() + "$JsonConverter").newInstance();
        gson = new Gson();
    }

    @JsonTarget
    public static class TestClass {

        Iterable<Long> longIteratable;

        Set<Character> characterSet;

        List<Integer> integerList;

        ArrayList<Double> doubleArrayList;

        Set<Item> itemSet;

    }

    public static class Item {

        boolean val1;

        String val2;

    }

    /**
     * Iterable的序列化测试
     * Gson不支持Iterable的序列化
     *
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        TestClass t = new TestClass();
        List<Long> list = new ArrayList<>();
        t.longIteratable = list;
        for (int i = 0; i < 6; i++) {
            list.add(Long.valueOf(i));
        }
        String json2 = staticJsonConverter.convert2Json(t);
        System.out.println(json2);
        Assert.assertEquals("{\"longIteratable\":[0,1,2,3,4,5]}", json2);
    }

    /**
     * Iterable的反序列化测试
     * Gson不支持Iterable的反序列化
     */
    @Test
    public void test11() throws IOException {
        String json = "{\"longIteratable\":[0,1,2,3,4,5]}";
        TestClass t = (TestClass) staticJsonConverter.convert2Object(json);
        String json2 = staticJsonConverter.convert2Json(t);
        System.out.println(json2);
        Assert.assertEquals(json, json2);
    }


}
