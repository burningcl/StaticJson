package com.skyline.json.staticjson.deserialize;

import com.skyline.json.staticjson.ConverterGenerator;
import com.skyline.json.staticjson.LoggerHolder;
import com.skyline.json.staticjson.PrintLogger;
import com.skyline.json.staticjson.meta.Gender;
import com.skyline.json.staticjson.serialize.SerializeLineGenerator;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ConstantAttribute;
import javassist.bytecode.SignatureAttribute;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

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
//        System.out.println(new ValueSetterGenerator(new ConverterGenerator()).gen(id.getType(), "instance.id", "idElement", null));
//    }

//    @Test
//    public void genTest4deleted() throws Exception {
//        ClassPool pool = ClassPool.getDefault();
//        CtClass ctClass = pool.get("com.skyline.json.staticjson.meta.Message");
//        CtField deleted = ctClass.getField("deleted");
//        System.out.println(new ValueSetterGenerator(new ConverterGenerator()).gen(deleted.getType(), "instance.deleted", "deletedElement", null));
//    }

//    @Test
//    public void genTest4subject() throws Exception {
//        ClassPool pool = ClassPool.getDefault();
//        CtClass ctClass = pool.get("com.skyline.json.staticjson.meta.Message");
//        CtField subject = ctClass.getField("subject");
//        System.out.println(new ValueSetterGenerator(new ConverterGenerator()).gen(subject.getType(), "instance.subject", "subjectElement", null));
//    }

//    @Test
//    public void genTest4toArray() throws Exception {
//        ClassPool pool = ClassPool.getDefault();
//        CtClass ctClass = pool.get("com.skyline.json.staticjson.meta.Message");
//        CtField toMatrix = ctClass.getField("toMatrix");
//        System.out.println(new ValueSetterGenerator(new ConverterGenerator()).gen(toMatrix.getType(), "instance.toMatrix", "toArrayElement", null));
//    }

//    @Test
//    public void genTest4to() throws Exception {
//        ClassPool pool = ClassPool.getDefault();
//        CtClass ctClass = pool.get("com.skyline.json.staticjson.meta.Message");
//        CtField to = ctClass.getField("toList");
//        SignatureAttribute signatureAttribute = null;
//
//        List<AttributeInfo> attributeInfoList = to.getFieldInfo().getAttributes();
//        if (attributeInfoList != null && attributeInfoList.size() > 0) {
//            for (AttributeInfo attributeInfo : attributeInfoList) {
//                signatureAttribute = (SignatureAttribute) attributeInfo;
//            }
//        }
//        System.out.println(new ValueSetterGenerator(new ConverterGenerator()).gen(to.getType(), "instance.to", "toElement", signatureAttribute));
//    }

    @Test
    public void genTest4toMap() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.get("com.skyline.json.staticjson.meta.Message");
        CtField toMap = ctClass.getField("toMap");
        SignatureAttribute signatureAttribute = null;

        List<AttributeInfo> attributeInfoList = toMap.getFieldInfo().getAttributes();
        if (attributeInfoList != null && attributeInfoList.size() > 0) {
            for (AttributeInfo attributeInfo : attributeInfoList) {
                signatureAttribute = (SignatureAttribute) attributeInfo;
            }
        }
        System.out.println(new ValueSetterGenerator(new ConverterGenerator()).gen(toMap.getType(), "instance.toMap", "toMapElement", signatureAttribute));
    }
}
