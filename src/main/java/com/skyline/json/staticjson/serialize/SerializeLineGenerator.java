package com.skyline.json.staticjson.serialize;

import com.skyline.json.staticjson.ConverterGenerator;
import com.skyline.json.staticjson.LoggerHolder;
import com.skyline.json.staticjson.util.VelocityHelper;
import com.skyline.json.staticjson.annotation.JsonField;
import com.skyline.json.staticjson.util.AnnotationUtil;
import com.skyline.json.staticjson.util.PrimitiveUtil;
import com.skyline.json.staticjson.util.StringUtil;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ConstantAttribute;
import javassist.bytecode.SignatureAttribute;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * 序列化方法每一个field转换代码生成器
 * Created by chenliang on 2017/4/10.
 */
public class SerializeLineGenerator {

    static final String TAG = "SerializeLineGenerator";

    /**
     * ConverterGenerator
     */
    ConverterGenerator converterGenerator;

    /**
     * ValueGetterGenerator
     */
    ValueGetterGenerator valueGetterGenerator;

    public SerializeLineGenerator(ConverterGenerator converterGenerator) {
        this.converterGenerator = converterGenerator;
        valueGetterGenerator = new ValueGetterGenerator(converterGenerator);
    }

    /**
     * @param field
     */
    protected String gen(CtField field) throws ClassNotFoundException, NotFoundException, CannotCompileException, BadBytecode, IOException {

        LoggerHolder.logger.debug(TAG, "gen, start, field: " + field);
        if (field == null) {
            throw new NullPointerException("gen, fail, field is null");
        }

        //获取AttributeInfo
        ConstantAttribute constantAttribute = null;
        SignatureAttribute signatureAttribute = null;

        List<AttributeInfo> attributeInfoList = field.getFieldInfo().getAttributes();
        if (attributeInfoList != null && attributeInfoList.size() > 0) {
            for (AttributeInfo attributeInfo : attributeInfoList) {
                if (attributeInfo instanceof ConstantAttribute) {
                    constantAttribute = (ConstantAttribute) attributeInfo;
                } else if (attributeInfo instanceof SignatureAttribute) {
                    signatureAttribute = (SignatureAttribute) attributeInfo;
                }
            }
        }

        if (constantAttribute != null) {
            // 静态字段，忽略
            LoggerHolder.logger.debug(TAG, "gen, ignore, field: " + field + ", it is static!");
            return null;
        }

        JsonField jsonField = AnnotationUtil.getAnnotation4Field(field, JsonField.class);
        String jsonFieldName = jsonField != null && !StringUtil.isBlank(jsonField.jsonName()) ?
                jsonField.jsonName()
                : field.getName();

        CtClass fieldClass = field.getType();

        //如果不是基础数据类型，则需要对该field进行判空处理
        boolean needCheckNull = !PrimitiveUtil.isPrimitiveDataType(fieldClass);
        if (signatureAttribute != null) {
            LoggerHolder.logger.debug(TAG, "genValueGetter, signatureAttribute: " + signatureAttribute.getSignature());
        }
        String valueGetter = valueGetterGenerator.gen(fieldClass, "instance." + field.getName(), signatureAttribute);

        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("serialize_line.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("needCheckNull", needCheckNull);
        ctx.put("key", jsonFieldName);
        ctx.put("fieldName", field.getName());
        ctx.put("valueGetter", valueGetter);
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);

        return sw.toString();
    }

}
