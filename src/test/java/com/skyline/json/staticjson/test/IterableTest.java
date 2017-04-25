package com.skyline.json.staticjson.test;

import com.google.gson.Gson;
import com.skyline.json.staticjson.generator.ConverterGenerator;
import com.skyline.json.staticjson.core.PrintLogger;
import com.skyline.json.staticjson.core.util.LoggerHolder;
import com.skyline.json.staticjson.core.StaticJsonConverter;
import com.skyline.json.staticjson.core.annotation.JsonTarget;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

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

        Queue<Item> itemQueue;

        List<Queue<Item>> itemQueues;

        List<Item[]> itemArrays;

    }

    public static class Item {

        boolean val1;

        String val2;

        public Item(boolean val1, String val2) {
            this.val1 = val1;
            this.val2 = val2;
        }

        public Item() {

        }
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

    @Test
    public void test2() throws IOException {
        TestClass t = new TestClass();

        t.characterSet = new HashSet<>();
        for (int i = 0; i < 6; i++) {
            t.characterSet.add((char) ('a' + i));
        }
        String json1 = gson.toJson(t);
        System.out.println(json1);
        String json2 = staticJsonConverter.convert2Json(t);
        System.out.println(json2);
        Assert.assertEquals(json1, json2);
    }

    @Test
    public void test21() throws IOException {
        testDeserialization("{\"characterSet\":[\"a\",\"b\",\"c\",\"d\",\"e\",\"f\"]}");
    }

    public void testDeserialization(String json) throws IOException {
        TestClass t1 = (TestClass) staticJsonConverter.convert2Object(json);
        TestClass t2 = gson.fromJson(json, TestClass.class);
        String json1 = staticJsonConverter.convert2Json(t1);
        System.out.println(json1);
        String json2 = gson.toJson(t2);
        System.out.println(json2);
        Assert.assertEquals(json, json1);
        Assert.assertEquals(json, json2);
    }

    @Test
    public void test3() throws IOException {
        TestClass t = new TestClass();

        t.integerList = new LinkedList<>();
        for (int i = 0; i < 6; i++) {
            t.integerList.add(i);
        }
        String json1 = gson.toJson(t);
        System.out.println(json1);
        String json2 = staticJsonConverter.convert2Json(t);
        System.out.println(json2);
        Assert.assertEquals(json1, json2);
    }

    @Test
    public void test31() throws IOException {
        testDeserialization("{\"integerList\":[0,1,2,3,4,5]}");
    }


    @Test
    public void test4() throws IOException {
        TestClass t = new TestClass();
        t.doubleArrayList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            t.doubleArrayList.add(i + (double) i / 10);
        }
        String json1 = gson.toJson(t);
        System.out.println(json1);
        String json2 = staticJsonConverter.convert2Json(t);
        System.out.println(json2);
        Assert.assertEquals(json1, json2);
    }

    @Test
    public void test41() throws IOException {
        testDeserialization("{\"doubleArrayList\":[0.0,1.1,2.2,3.3,4.4,5.5]}");
    }

    @Test
    public void test5() throws IOException {
        TestClass t = new TestClass();
        t.itemQueue = new ArrayBlockingQueue<Item>(100);
        for (int i = 0; i < 6; i++) {
            t.itemQueue.add(new Item(i % 2 == 0, "str" + i));
        }
        String json1 = gson.toJson(t);
        System.out.println(json1);
        String json2 = staticJsonConverter.convert2Json(t);
        System.out.println(json2);
        Assert.assertEquals(json1, json2);
    }

    @Test
    public void test51() throws IOException {
        testDeserialization("{\"itemQueue\":[{\"val1\":true,\"val2\":\"str0\"},{\"val1\":false,\"val2\":\"str1\"},{\"val1\":true,\"val2\":\"str2\"},{\"val1\":false,\"val2\":\"str3\"},{\"val1\":true,\"val2\":\"str4\"},{\"val1\":false,\"val2\":\"str5\"}]}");
    }

    @Test
    public void test6() throws IOException {
        TestClass t = new TestClass();
        t.characterSet = new HashSet<>();
        for (int i = 0; i < 6; i++) {
            t.characterSet.add((char) ('a' + i));
        }
        t.characterSet.add(null);
        String json1 = gson.toJson(t);
        System.out.println(json1);
        String json2 = staticJsonConverter.convert2Json(t);
        System.out.println(json2);
        Assert.assertEquals(json1, json2);
    }

    @Test
    public void test61() throws IOException {
        testDeserialization("{\"characterSet\":[null,\"a\",\"b\",\"c\",\"d\",\"e\",\"f\"]}");
    }

    @Test
    public void test7() throws IOException {
        TestClass t = new TestClass();
        t.itemQueues = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ArrayBlockingQueue itemQueue = new ArrayBlockingQueue<Item>(4);
            for (int j = 0; j < 4; j++) {
                itemQueue.add(new Item(j % 2 == 0, "str" + j));
            }
            t.itemQueues.add(itemQueue);
        }
        t.itemQueues.add(null);
        String json1 = gson.toJson(t);
        System.out.println(json1);
        String json2 = staticJsonConverter.convert2Json(t);
        System.out.println(json2);
        Assert.assertEquals(json1, json2);
    }

    @Test
    public void test71() throws IOException {
        String json = "{\"itemQueues\":[[{\"val1\":true,\"val2\":\"str0\"},{\"val1\":false,\"val2\":\"str1\"},{\"val1\":true,\"val2\":\"str2\"},{\"val1\":false,\"val2\":\"str3\"}],[{\"val1\":true,\"val2\":\"str0\"},{\"val1\":false,\"val2\":\"str1\"},{\"val1\":true,\"val2\":\"str2\"},{\"val1\":false,\"val2\":\"str3\"}],[{\"val1\":true,\"val2\":\"str0\"},{\"val1\":false,\"val2\":\"str1\"},{\"val1\":true,\"val2\":\"str2\"},{\"val1\":false,\"val2\":\"str3\"}],null]}";
        TestClass t1 = gson.fromJson(json, TestClass.class);
        TestClass t2 = (TestClass) staticJsonConverter.convert2Object(json);
        String json1 = gson.toJson(t1);
        System.out.println(json1);
        String json2 = staticJsonConverter.convert2Json(t2);
        System.out.println(json2);
        Assert.assertEquals(json1, json2);
    }

    @Test
    public void test8() throws IOException {
        TestClass t = new TestClass();
        t.itemArrays = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Item[] itemArray = new Item[4];
            for (int j = 0; j < 4; j++) {
                itemArray[j] = new Item(j % 2 == 0, "str" + j);
            }
            t.itemArrays.add(itemArray);
        }
        t.itemArrays.add(null);
        String json1 = gson.toJson(t);
        System.out.println(json1);
        String json2 = staticJsonConverter.convert2Json(t);
        System.out.println(json2);
        Assert.assertEquals(json1, json2);
    }

    @Test
    public void test81() throws IOException {
        String json = "{\"itemArrays\":[[{\"val1\":true,\"val2\":\"str0\"},{\"val1\":false,\"val2\":\"str1\"},{\"val1\":true,\"val2\":\"str2\"},{\"val1\":false,\"val2\":\"str3\"}],[{\"val1\":true,\"val2\":\"str0\"},{\"val1\":false,\"val2\":\"str1\"},{\"val1\":true,\"val2\":\"str2\"},{\"val1\":false,\"val2\":\"str3\"}],[{\"val1\":true,\"val2\":\"str0\"},{\"val1\":false,\"val2\":\"str1\"},{\"val1\":true,\"val2\":\"str2\"},{\"val1\":false,\"val2\":\"str3\"}],null]}";
        TestClass t1 = gson.fromJson(json, TestClass.class);
        TestClass t2 = (TestClass) staticJsonConverter.convert2Object(json);
        String json1 = gson.toJson(t1);
        System.out.println(json1);
        String json2 = staticJsonConverter.convert2Json(t2);
        System.out.println(json2);
        Assert.assertEquals(json1, json2);
    }

}
