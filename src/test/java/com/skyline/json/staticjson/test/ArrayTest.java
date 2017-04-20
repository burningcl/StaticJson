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
 * 多维数组的测试
 * Created by chenliang on 2017/4/20.
 */
public class ArrayTest {

    static StaticJsonConverter staticJsonConverter;
    static Gson gson;

    @BeforeClass
    public static void before() throws NotFoundException, ClassNotFoundException, CannotCompileException, BadBytecode, IOException, IllegalAccessException, InstantiationException {
        LoggerHolder.logger = new PrintLogger();
        ClassPool pool = ClassPool.getDefault();
        pool.appendSystemPath();
        CtClass ctClass = pool.get(TestClass.class.getName());
        new ConverterGenerator().gen(ctClass);

        staticJsonConverter = (StaticJsonConverter) Class.forName(TestClass.class.getName() + "$JsonConverter").newInstance();
        gson = new Gson();
    }

    @JsonTarget
    public static class TestClass {

        int[] intArray1;

        int[][] intArray2;

        int[][][] intArray3;

        Long[] longArray1;

        Long[][] longArray2;

        Long[][][] longArray3;

        Element[] elementArray1;

        Element[][] elementArray2;

        Element[][][] elementArray3;
    }

    public static class Element {
        boolean value;
    }

    @Test
    public void test1() throws NotFoundException, ClassNotFoundException, CannotCompileException, BadBytecode, IOException, IllegalAccessException, InstantiationException {
        TestClass t = new TestClass();
        System.out.println(staticJsonConverter.convert2Json(t));
        System.out.println(gson.toJson(t));
    }

    @Test
    public void test11() throws NotFoundException, ClassNotFoundException, CannotCompileException, BadBytecode, IOException, IllegalAccessException, InstantiationException {
        String json = "{}";
        TestClass t1 = (TestClass)staticJsonConverter.convert2Object(json);
        TestClass t2 = gson.fromJson(json, TestClass.class);
        System.out.println(gson.toJson(t1));
        System.out.println(gson.toJson(t2));
    }

    @Test
    public void test2() throws NotFoundException, ClassNotFoundException, CannotCompileException, BadBytecode, IOException, IllegalAccessException, InstantiationException {
        TestClass t = new TestClass();
        t.intArray1 = new int[3];
        fill(t.intArray1);
        t.intArray2 = new int[3][4];
        fill(t.intArray2);
        t.intArray3 = new int[3][4][5];
        fill(t.intArray3);
        System.out.println(staticJsonConverter.convert2Json(t));
        System.out.println(gson.toJson(t));
    }

    @Test
    public void test21() throws NotFoundException, ClassNotFoundException, CannotCompileException, BadBytecode, IOException, IllegalAccessException, InstantiationException {
        String json = "{\"intArray1\":[0,1,2],\"intArray2\":[[0,1,2,3],[0,1,2,3],[0,1,2,3]],\"intArray3\":[[[0,1,2,3,4],[0,1,2,3,4],[0,1,2,3,4],[0,1,2,3,4]],[[0,1,2,3,4],[0,1,2,3,4],[0,1,2,3,4],[0,1,2,3,4]],[[0,1,2,3,4],[0,1,2,3,4],[0,1,2,3,4],[0,1,2,3,4]]]}";
        TestClass t1 = (TestClass)staticJsonConverter.convert2Object(json);
        TestClass t2 = gson.fromJson(json, TestClass.class);
        System.out.println(gson.toJson(t1));
        System.out.println(gson.toJson(t2));
    }

    @Test
    public void test3() throws NotFoundException, ClassNotFoundException, CannotCompileException, BadBytecode, IOException, IllegalAccessException, InstantiationException {
        TestClass t = new TestClass();
        t.longArray1 = new Long[3];
        fill(t.longArray1);
        t.longArray2 = new Long[3][4];
        fill(t.longArray2);
        t.longArray3 = new Long[3][4][5];
        fill(t.longArray3);
        System.out.println(staticJsonConverter.convert2Json(t));
        System.out.println(gson.toJson(t));
    }

    @Test
    public void test31() throws NotFoundException, ClassNotFoundException, CannotCompileException, BadBytecode, IOException, IllegalAccessException, InstantiationException {
        String json = "{\"longArray1\":[0,1,2],\"longArray2\":[[0,1,2,3],[0,1,2,3],[0,1,2,3]],\"longArray3\":[[[0,1,2,3,4],[0,1,2,3,4],[0,1,2,3,4],[0,1,2,3,4]],[[0,1,2,3,4],[0,1,2,3,4],[0,1,2,3,4],[0,1,2,3,4]],[[0,1,2,3,4],[0,1,2,3,4],[0,1,2,3,4],[0,1,2,3,4]]]}";
        TestClass t1 = (TestClass)staticJsonConverter.convert2Object(json);
        TestClass t2 = gson.fromJson(json, TestClass.class);
        System.out.println(gson.toJson(t1));
        System.out.println(gson.toJson(t2));
    }

    @Test
    public void test4() throws NotFoundException, ClassNotFoundException, CannotCompileException, BadBytecode, IOException, IllegalAccessException, InstantiationException {
        TestClass t = new TestClass();
        t.elementArray1 = new Element[3];
        fill(t.elementArray1);
        t.elementArray2 = new Element[3][4];
        fill(t.elementArray2);
        t.elementArray3 = new Element[3][4][5];
        fill(t.elementArray3);
        System.out.println(staticJsonConverter.convert2Json(t));
        System.out.println(gson.toJson(t));
    }

    @Test
    public void test41() throws NotFoundException, ClassNotFoundException, CannotCompileException, BadBytecode, IOException, IllegalAccessException, InstantiationException {
        String json = "{\"elementArray1\":[{\"value\":true},{\"value\":false},{\"value\":true}],\"elementArray2\":[[{\"value\":true},{\"value\":false},{\"value\":true},{\"value\":false}],[{\"value\":true},{\"value\":false},{\"value\":true},{\"value\":false}],[{\"value\":true},{\"value\":false},{\"value\":true},{\"value\":false}]],\"elementArray3\":[[[{\"value\":true},{\"value\":false},{\"value\":true},{\"value\":false},{\"value\":true}],[{\"value\":true},{\"value\":false},{\"value\":true},{\"value\":false},{\"value\":true}],[{\"value\":true},{\"value\":false},{\"value\":true},{\"value\":false},{\"value\":true}],[{\"value\":true},{\"value\":false},{\"value\":true},{\"value\":false},{\"value\":true}]],[[{\"value\":true},{\"value\":false},{\"value\":true},{\"value\":false},{\"value\":true}],[{\"value\":true},{\"value\":false},{\"value\":true},{\"value\":false},{\"value\":true}],[{\"value\":true},{\"value\":false},{\"value\":true},{\"value\":false},{\"value\":true}],[{\"value\":true},{\"value\":false},{\"value\":true},{\"value\":false},{\"value\":true}]],[[{\"value\":true},{\"value\":false},{\"value\":true},{\"value\":false},{\"value\":true}],[{\"value\":true},{\"value\":false},{\"value\":true},{\"value\":false},{\"value\":true}],[{\"value\":true},{\"value\":false},{\"value\":true},{\"value\":false},{\"value\":true}],[{\"value\":true},{\"value\":false},{\"value\":true},{\"value\":false},{\"value\":true}]]]}";
        TestClass t1 = (TestClass)staticJsonConverter.convert2Object(json);
        TestClass t2 = gson.fromJson(json, TestClass.class);
        System.out.println(gson.toJson(t1));
        System.out.println(gson.toJson(t2));
    }

    private void fill(int[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
    }

    private void fill(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            fill(array[i]);
        }
    }

    private void fill(int[][][] array) {
        for (int i = 0; i < array.length; i++) {
            fill(array[i]);
        }
    }

    private void fill(Long[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = Long.valueOf(i);
        }
    }

    private void fill(Long[][] array) {
        for (int i = 0; i < array.length; i++) {
            fill(array[i]);
        }
    }

    private void fill(Long[][][] array) {
        for (int i = 0; i < array.length; i++) {
            fill(array[i]);
        }
    }

    private void fill(Element[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = new Element();
            array[i].value = i % 2 == 0;
        }
    }

    private void fill(Element[][] array) {
        for (int i = 0; i < array.length; i++) {
            fill(array[i]);
        }
    }

    private void fill(Element[][][] array) {
        for (int i = 0; i < array.length; i++) {
            fill(array[i]);
        }
    }
}
