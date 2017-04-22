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
import java.util.HashMap;
import java.util.List;
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

        Map<Integer, String> map1;

        Map<Integer, Map<Integer, String>> map2;

        Map<Key, Value> map3;

        Map<Long, Value[]> map4;

        Map<Long, List<Value>> map5;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestClass testClass = (TestClass) o;

            if (map1 != null ? !map1.equals(testClass.map1) : testClass.map1 != null) return false;
            if (map2 != null ? !map2.equals(testClass.map2) : testClass.map2 != null) return false;
            if (map3 != null ? !map3.equals(testClass.map3) : testClass.map3 != null) return false;
            return map4 != null ? map4.equals(testClass.map4) : testClass.map4 == null;
        }

        @Override
        public int hashCode() {
            int result = map1 != null ? map1.hashCode() : 0;
            result = 31 * result + (map2 != null ? map2.hashCode() : 0);
            result = 31 * result + (map3 != null ? map3.hashCode() : 0);
            result = 31 * result + (map4 != null ? map4.hashCode() : 0);
            return result;
        }
    }

    public static class Key {

        String keyStr;

        long keyLong;

        public Key(String keyStr, long keyLong) {
            this.keyStr = keyStr;
            this.keyLong = keyLong;
        }

        public Key() {

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (keyLong != key.keyLong) return false;
            return keyStr != null ? keyStr.equals(key.keyStr) : key.keyStr == null;
        }

        @Override
        public int hashCode() {
            int result = keyStr != null ? keyStr.hashCode() : 0;
            result = 31 * result + (int) (keyLong ^ (keyLong >>> 32));
            return result;
        }
    }

    public static class Value {

        String valStr;

        long valLong;

        public Value(String valStr, long valLong) {
            this.valStr = valStr;
            this.valLong = valLong;
        }

        public Value() {

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Value value = (Value) o;

            if (valLong != value.valLong) return false;
            return valStr != null ? valStr.equals(value.valStr) : value.valStr == null;
        }

        @Override
        public int hashCode() {
            int result = valStr != null ? valStr.hashCode() : 0;
            result = 31 * result + (int) (valLong ^ (valLong >>> 32));
            return result;
        }
    }

    @Test
    public void test1() throws IOException {
        TestClass t = new TestClass();
        t.map1 = new HashMap<>();
        fill(t.map1);
        String json1 = gson.toJson(t);
        String json2 = staticJsonConverter.convert2Json(t);
        System.out.println(json1);
        System.out.println(json2);
        Assert.assertEquals(json1, json2);
    }

    @Test
    public void test11() {
        String json = "{\"map1\":{\"0\":\"value0\",\"1\":\"value1\",\"2\":\"value2\"}}";
        TestClass t1 = (TestClass) staticJsonConverter.convert2Object(json);
        TestClass t2 = gson.fromJson(json, TestClass.class);
        System.out.println(gson.toJson(t1));
        System.out.println(gson.toJson(t2));
        Assert.assertEquals(t1, t2);
    }

    @Test
    public void test2() throws IOException {
        TestClass t = new TestClass();
        t.map2 = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            Map<Integer, String> value = new HashMap<>();
            fill(value);
            t.map2.put(i, value);
        }
        String json1 = gson.toJson(t);
        String json2 = staticJsonConverter.convert2Json(t);
        System.out.println(json1);
        System.out.println(json2);
        Assert.assertEquals(json1, json2);
    }

    @Test
    public void test21() {
        String json = "{\"map2\":{\"0\":{\"0\":\"value0\",\"1\":\"value1\",\"2\":\"value2\"},\"1\":{\"0\":\"value0\",\"1\":\"value1\",\"2\":\"value2\"},\"2\":{\"0\":\"value0\",\"1\":\"value1\",\"2\":\"value2\"}}}";
        TestClass t1 = (TestClass) staticJsonConverter.convert2Object(json);
        TestClass t2 = gson.fromJson(json, TestClass.class);
        System.out.println(gson.toJson(t1));
        System.out.println(gson.toJson(t2));
        Assert.assertEquals(t1, t2);
    }

    @Test
    public void test3() throws IOException {
        TestClass t = new TestClass();
        t.map3 = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            t.map3.put(new Key("key" + i, i), new Value("value" + i, i));
        }
        String json1 = gson.toJson(t);
        String json2 = staticJsonConverter.convert2Json(t);
        System.out.println(json1);
        System.out.println(json2);
        Assert.assertEquals(json1, json2);
    }

    @Test
    public void test4() throws IOException {
        TestClass t = new TestClass();
        t.map4 = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            Value[] val = new Value[i + 1];
            for (int j = 0; j <= i; j++) {
                val[j] = new Value("value" + j, j);
            }
            t.map4.put(Long.valueOf(i), val);
        }
        String json1 = gson.toJson(t);
        String json2 = staticJsonConverter.convert2Json(t);
        System.out.println(json1);
        System.out.println(json2);
        Assert.assertEquals(json1, json2);
    }

    @Test
    public void test41() {
        String json = "{\"map4\":{\"0\":[{\"valStr\":\"value0\",\"valLong\":0}],\"1\":[{\"valStr\":\"value0\",\"valLong\":0},{\"valStr\":\"value1\",\"valLong\":1}],\"2\":[{\"valStr\":\"value0\",\"valLong\":0},{\"valStr\":\"value1\",\"valLong\":1},{\"valStr\":\"value2\",\"valLong\":2}]}}\n";
        TestClass t1 = (TestClass) staticJsonConverter.convert2Object(json);
        TestClass t2 = gson.fromJson(json, TestClass.class);
        System.out.println(gson.toJson(t1));
        System.out.println(gson.toJson(t2));
        // Assert.assertTrue(t1.equals(t2));
    }

    @Test
    public void test5() throws IOException {
        TestClass t = new TestClass();
        t.map5 = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            List<Value> val = new ArrayList<>();
            for (int j = 0; j <= i; j++) {
                val.add(new Value("value" + j, j));
            }
            t.map5.put(Long.valueOf(i), val);
        }
        String json1 = gson.toJson(t);
        String json2 = staticJsonConverter.convert2Json(t);
        System.out.println(json1);
        System.out.println(json2);
        Assert.assertEquals(json1, json2);
    }

    @Test
    public void test51() {
        String json = "{\"map5\":{\"0\":[{\"valStr\":\"value0\",\"valLong\":0}],\"1\":[{\"valStr\":\"value0\",\"valLong\":0},{\"valStr\":\"value1\",\"valLong\":1}],\"2\":[{\"valStr\":\"value0\",\"valLong\":0},{\"valStr\":\"value1\",\"valLong\":1},{\"valStr\":\"value2\",\"valLong\":2}]}}\n";
        TestClass t1 = (TestClass) staticJsonConverter.convert2Object(json);
        TestClass t2 = gson.fromJson(json, TestClass.class);
        System.out.println(gson.toJson(t1));
        System.out.println(gson.toJson(t2));
        // Assert.assertTrue(t1.equals(t2));
    }

    @Test
    public void test6() throws IOException {
        TestClass t = new TestClass();
        t.map5 = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            List<Value> val = new ArrayList<>();
            for (int j = 0; j <= i; j++) {
                val.add(new Value("value" + j, j));
            }
            t.map5.put(i != 0 ? Long.valueOf(i) : null, val);
        }
        String json1 = gson.toJson(t);
        String json2 = staticJsonConverter.convert2Json(t);
        System.out.println(json1);
        System.out.println(json2);
        Assert.assertEquals(json1, json2);
    }

    /**
     * 含key为null的map的反序列化
     * Gson不支持此类反序列化
     *
     * @throws IOException
     */
    @Test
    public void test61() throws IOException {
        String json = "{\"map5\":{\"null\":[{\"valStr\":\"value0\",\"valLong\":0}],\"1\":[{\"valStr\":\"value0\",\"valLong\":0},{\"valStr\":\"value1\",\"valLong\":1}],\"2\":[{\"valStr\":\"value0\",\"valLong\":0},{\"valStr\":\"value1\",\"valLong\":1},{\"valStr\":\"value2\",\"valLong\":2}]}}\n";
        TestClass t1 = (TestClass) staticJsonConverter.convert2Object(json);
        System.out.println(gson.toJson(t1));
    }

    /**
     * 包含value为null的map的序列化
     *
     * @throws IOException
     */
    @Test
    public void test7() throws IOException {
        TestClass t = new TestClass();
        t.map5 = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            List<Value> val = null;
            if (i != 1) {
                val = new ArrayList<>();
                for (int j = 0; j <= i; j++) {
                    val.add(new Value("value" + j, j));
                }
            }
            t.map5.put(Long.valueOf(i), val);
        }
        String json1 = gson.toJson(t);
        String json2 = staticJsonConverter.convert2Json(t);
        System.out.println(json1);
        System.out.println(json2);
        Assert.assertEquals(json1, json2);
    }

    private void fill(Map<Integer, String> map) {
        for (int i = 0; i < 3; i++) {
            map.put(i, "value" + i);
        }
    }

}