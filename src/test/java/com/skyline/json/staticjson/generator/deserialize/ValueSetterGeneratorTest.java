package com.skyline.json.staticjson.generator.deserialize;

import com.skyline.json.staticjson.generator.ConverterGenerator;
import com.skyline.json.staticjson.core.PrintLogger;
import com.skyline.json.staticjson.core.util.LoggerHolder;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by chenliang on 2017/4/14.
 */
public class ValueSetterGeneratorTest {

    @BeforeClass
    public static void before() {
        LoggerHolder.logger = new PrintLogger();
    }

//    @Test
//    public void genTest4id() throws Exception {
//        ClassPool pool = ClassPool.getDefault();
//        CtClass ctClass = pool.get("com.skyline.json.staticjson.meta.Message");
//        CtField id = ctClass.getField("id");
//        System.out.println(new ValueSetterGenerator(new ConverterGenerator()).gen(id.getType(), "instance.id",null));
//    }

//    @Test
//    public void genTest4deleted() throws Exception {
//        ClassPool pool = ClassPool.getDefault();
//        CtClass ctClass = pool.get("com.skyline.json.staticjson.meta.Message");
//        CtField deleted = ctClass.getField("deleted");
//        System.out.println(new ValueSetterGenerator(new ConverterGenerator()).gen(deleted.getType(), "instance.deleted",  null));
//    }

//    @Test
//    public void genTest4subject() throws Exception {
//        ClassPool pool = ClassPool.getDefault();
//        CtClass ctClass = pool.get("com.skyline.json.staticjson.meta.Message");
//        CtField subject = ctClass.getField("subject");
//        System.out.println(new ValueSetterGenerator(new ConverterGenerator()).gen(subject.getType(), "instance.subject", "jsonToken",null));
//    }

//    @Test
//    public void genTest4toArray() throws Exception {
//        ClassPool pool = ClassPool.getDefault();
//        CtClass ctClass = pool.get("com.skyline.json.staticjson.meta.Message");
//        CtField toArray = ctClass.getField("toArray");
//        System.out.println(new ValueSetterGenerator(new ConverterGenerator()).gen(toArray.getType(), "instance.toArray", "jsonToken", null));
//    }

    @Test
    public void genTest4gender() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.get("com.skyline.json.staticjson.meta.User");
        CtField gender = ctClass.getField("gender");
        System.out.println(new ValueSetterGenerator(new ConverterGenerator()).gen(gender.getType(), "instance.gender", "jsonToken", null));
    }

//    @Test
//    public void genTest4to() throws Exception {
//        ClassPool pool = ClassPool.getDefault();
//        CtClass ctClass = pool.get("com.skyline.json.staticjson.meta.Message");
//        CtField to = ctClass.getField("to");
//        SignatureAttribute signatureAttribute = null;
//
//        List<AttributeInfo> attributeInfoList = to.getFieldInfo().getAttributes();
//        if (attributeInfoList != null && attributeInfoList.size() > 0) {
//            for (AttributeInfo attributeInfo : attributeInfoList) {
//                signatureAttribute = (SignatureAttribute) attributeInfo;
//            }
//        }
//        System.out.println(new ValueSetterGenerator(new ConverterGenerator()).gen(to.getType(), "instance.to", "jsonToken", signatureAttribute));
//    }

//    @Test
//    public void genTest4toMap() throws Exception {
//        ClassPool pool = ClassPool.getDefault();
//        CtClass ctClass = pool.get("com.skyline.json.staticjson.meta.Message");
//        CtField toMap = ctClass.getField("toMap");
//        SignatureAttribute signatureAttribute = null;
//
//        List<AttributeInfo> attributeInfoList = toMap.getFieldInfo().getAttributes();
//        if (attributeInfoList != null && attributeInfoList.size() > 0) {
//            for (AttributeInfo attributeInfo : attributeInfoList) {
//                signatureAttribute = (SignatureAttribute) attributeInfo;
//            }
//        }
//        System.out.println(new ValueSetterGenerator(new ConverterGenerator()).gen(toMap.getType(), "instance.toMap", "toMapElement", signatureAttribute));
//    }
}
