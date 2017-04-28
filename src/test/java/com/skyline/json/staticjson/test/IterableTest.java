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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestClass testClass = (TestClass) o;

            if (longIteratable != null ? !longIteratable.equals(testClass.longIteratable) : testClass.longIteratable != null)
                return false;
            if (characterSet != null ? !characterSet.equals(testClass.characterSet) : testClass.characterSet != null)
                return false;
            if (integerList != null ? !integerList.equals(testClass.integerList) : testClass.integerList != null)
                return false;
            if (doubleArrayList != null ? !doubleArrayList.equals(testClass.doubleArrayList) : testClass.doubleArrayList != null)
                return false;
            if (itemQueue != null ? !itemQueue.equals(testClass.itemQueue) : testClass.itemQueue != null) return false;
            if (itemQueues != null ? !itemQueues.equals(testClass.itemQueues) : testClass.itemQueues != null)
                return false;
            return itemArrays != null ? itemArrays.equals(testClass.itemArrays) : testClass.itemArrays == null;
        }

        @Override
        public int hashCode() {
            int result = longIteratable != null ? longIteratable.hashCode() : 0;
            result = 31 * result + (characterSet != null ? characterSet.hashCode() : 0);
            result = 31 * result + (integerList != null ? integerList.hashCode() : 0);
            result = 31 * result + (doubleArrayList != null ? doubleArrayList.hashCode() : 0);
            result = 31 * result + (itemQueue != null ? itemQueue.hashCode() : 0);
            result = 31 * result + (itemQueues != null ? itemQueues.hashCode() : 0);
            result = 31 * result + (itemArrays != null ? itemArrays.hashCode() : 0);
            return result;
        }
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Item item = (Item) o;

            if (val1 != item.val1) return false;
            return val2 != null ? val2.equals(item.val2) : item.val2 == null;
        }

        @Override
        public int hashCode() {
            int result = (val1 ? 1 : 0);
            result = 31 * result + (val2 != null ? val2.hashCode() : 0);
            return result;
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
        //Assert.assertEquals(json1, json2);
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
