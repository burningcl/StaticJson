package com.skyline.json.staticjson.serialize;

import com.google.gson.Gson;
import com.skyline.json.staticjson.ConverterGenerator;
import com.skyline.json.staticjson.LoggerHolder;
import com.skyline.json.staticjson.PrintLogger;
import com.skyline.json.staticjson.meta.User;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by chenliang on 2017/4/12.
 */
public class SerializeLineGeneratorTest {

    @BeforeClass
    public static void before() {
        LoggerHolder.logger = new PrintLogger();
    }

//    @Test
//    public void genTest4from() throws Exception {
//        SerializeLineGenerator generator = new SerializeLineGenerator(new ConverterGenerator());
//        ClassPool pool = ClassPool.getDefault();
//        CtClass ctClass = pool.get("com.skyline.json.staticjson.meta.Message");
//        CtField from = ctClass.getField("from");
//        System.out.println(generator.gen(from));
//    }
//
//    @Test
//    public void genTest4toArray() throws Exception {
//        SerializeLineGenerator generator = new SerializeLineGenerator(new ConverterGenerator());
//        ClassPool pool = ClassPool.getDefault();
//        CtClass ctClass = pool.get("com.skyline.json.staticjson.meta.Message");
//        CtField toArray = ctClass.getField("toArray");
//        System.out.println(generator.gen(toArray));
//    }
//
//    @Test
//    public void genTest4to() throws Exception {
//        SerializeLineGenerator generator = new SerializeLineGenerator(new ConverterGenerator());
//        ClassPool pool = ClassPool.getDefault();
//        CtClass ctClass = pool.get("com.skyline.json.staticjson.meta.Message");
//        CtField to = ctClass.getField("to");
//        System.out.println(generator.gen(to));
//    }

    @Test
    public void genTest4toMap() throws Exception {
        SerializeLineGenerator generator = new SerializeLineGenerator(new ConverterGenerator());
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.get("com.skyline.json.staticjson.meta.Message");
        CtField toMap = ctClass.getField("toMap");
        System.out.println(generator.gen(toMap));
    }
}
