package com.skyline.json.staticjson.core.util;

import com.skyline.json.staticjson.core.annotation.JsonField;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import org.junit.Test;

/**
 * Created by chenliang on 2017/4/11.
 */
public class StringUtilTest {

    @Test
    public void getAnnotation4FieldTest() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.get("com.skyline.json.staticjson.meta.Message");
        CtField subjectField = ctClass.getField("subject");
        System.out.println(StringUtil.isString(subjectField.getType()));
    }
}
