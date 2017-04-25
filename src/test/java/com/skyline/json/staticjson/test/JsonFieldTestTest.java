package com.skyline.json.staticjson.test;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.skyline.json.staticjson.generator.ConverterGenerator;
import com.skyline.json.staticjson.core.PrintLogger;
import com.skyline.json.staticjson.core.StaticJsonConverter;
import com.skyline.json.staticjson.core.annotation.JsonField;
import com.skyline.json.staticjson.core.annotation.JsonTarget;
import com.skyline.json.staticjson.core.util.LoggerHolder;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by chenliang on 2017/4/21.
 */
public class JsonFieldTestTest {

    static StaticJsonConverter staticJsonConverter;
    static StaticJsonConverter staticJsonConverter2;
    static Gson gson;

    @BeforeClass
    public static void before() throws NotFoundException, ClassNotFoundException, CannotCompileException, BadBytecode, IOException, IllegalAccessException, InstantiationException {
        LoggerHolder.logger = new PrintLogger();
        ClassPool pool = ClassPool.getDefault();
        pool.appendSystemPath();
        CtClass ctClass = pool.get(JsonFieldTestTest.TestClass.class.getName());
        new ConverterGenerator().gen(ctClass);
        CtClass ctClass2 = pool.get(JsonFieldTestTest.TestClass2.class.getName());
        new ConverterGenerator().gen(ctClass2);

        staticJsonConverter = (StaticJsonConverter) Class.forName(JsonFieldTestTest.TestClass.class.getName() + "$JsonConverter").newInstance();
        staticJsonConverter2 = (StaticJsonConverter) Class.forName(JsonFieldTestTest.TestClass2.class.getName() + "$JsonConverter").newInstance();
        gson = new Gson();
    }

    @JsonTarget
    public static class TestClass {

        @JsonField(jsonName = "alias")
        String string;

        @JsonField(ignored = true)
        String ignored;

        @JsonField(typeAdapter = DeletedTypeAdapter.class)
        Boolean deleted;

//        @JsonField(typeAdapter = DeletedTypeAdapter.class)
//        boolean deletedPrimitive;

    }

    @JsonTarget
    public static class TestClass2 {

        @JsonField(typeAdapter = DeletedTypeAdapter.class)
        Boolean deleted;

    }

    public static class DeletedTypeAdapter extends TypeAdapter<Boolean> {

        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            out.value(value ? 1 : 0);
        }

        @Override
        public Boolean read(JsonReader in) throws IOException {
            return in.nextInt() != 0 ? true : false;
        }
    }

    @Test
    public void testJsonName() throws IOException {
        TestClass t = new TestClass();
        t.string = "string value";
        String json = staticJsonConverter.convert2Json(t);
        System.out.println(json);
        Assert.assertEquals("{\"alias\":\"string value\"}", json);
    }

    @Test
    public void testJsonName1() throws IOException {
        String json = "{\"alias\":\"string value\"}";
        TestClass t = (TestClass) staticJsonConverter.convert2Object(json);
        String json1 = staticJsonConverter.convert2Json(t);
        System.out.println(json1);
        Assert.assertEquals(json, json1);
    }


    @Test
    public void testIgnored() throws IOException {
        TestClass t = new TestClass();
        t.string = "string value";
        t.ignored = "ignored";
        String json = staticJsonConverter.convert2Json(t);
        System.out.println(json);
        Assert.assertEquals("{\"alias\":\"string value\"}", json);
    }

    @Test
    public void testIgnored1() throws IOException {
        String json = "{\"alias\":\"string value\", \"ignored\": \"ignored value\"}";
        TestClass t = (TestClass) staticJsonConverter.convert2Object(json);
        String json1 = staticJsonConverter.convert2Json(t);
        System.out.println(json1);
        Assert.assertEquals("{\"alias\":\"string value\"}", json1);
    }

    @Test
    public void testTypeAdapter() throws IOException {
        TestClass t = new TestClass();
        t.deleted = true;
        String json = staticJsonConverter.convert2Json(t);
        System.out.println(json);
        Assert.assertEquals("{\"deleted\":1}", json);
    }

    @Test
    public void testTypeAdapter1() throws IOException {
        TestClass t = new TestClass();
        t.deleted = false;
        String json = staticJsonConverter.convert2Json(t);
        System.out.println(json);
        Assert.assertEquals("{\"deleted\":0}", json);
    }

    @Test
    public void testTypeAdapter2() throws IOException {
        String json = "{\"deleted\":0}";
        TestClass2 t = (TestClass2) staticJsonConverter2.convert2Object(json);
        String json1 = staticJsonConverter2.convert2Json(t);
        System.out.println(json1);
        Assert.assertEquals(json, json1);
    }


    @Test
    public void testTypeAdapter3() throws IOException {
        String json = "{\"deleted\":1}";
        TestClass2 t = (TestClass2) staticJsonConverter2.convert2Object(json);
        String json1 = staticJsonConverter2.convert2Json(t);
        System.out.println(json1);
        Assert.assertEquals(json, json1);
    }
}
