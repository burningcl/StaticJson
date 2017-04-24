package com.skyline.json.staticjson.deserialize;

import com.skyline.json.staticjson.ConverterGenerator;
import com.skyline.json.staticjson.TypeAdapterCodeGenerator;
import com.skyline.json.staticjson.TypeAdapterNull;
import com.skyline.json.staticjson.util.LoggerHolder;
import com.skyline.json.staticjson.annotation.JsonField;
import com.skyline.json.staticjson.util.AnnotationUtil;
import com.skyline.json.staticjson.util.StringUtil;
import com.skyline.json.staticjson.util.VelocityHelper;
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
 * 反序列化方法每一个field转换代码生成器
 * Created by chenliang on 2017/4/10.
 */
public class DeserializeLineGenerator {

    static final String TAG = "DeserializeLineGenerator";

    /**
     * ConverterGenerator
     */
    ConverterGenerator converterGenerator;

    /**
     * ValueSetterGenerator
     */
    ValueSetterGenerator valueSetterGenerator;

    public DeserializeLineGenerator(ConverterGenerator converterGenerator) {
        this.converterGenerator = converterGenerator;
        valueSetterGenerator = new ValueSetterGenerator(converterGenerator);
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
        if (jsonField != null && jsonField.ignored()) {
            return "";
        }
        String jsonFieldName = jsonField != null && !StringUtil.isBlank(jsonField.jsonName()) ?
                jsonField.jsonName()
                : field.getName();

        CtClass fieldClass = field.getType();

        String valueSetter;
        String varName = "instance." + field.getName();
        if (jsonField != null && !jsonField.typeAdapter().equals(TypeAdapterNull.class)) {
            valueSetter = TypeAdapterCodeGenerator.genDeserializationCode(jsonField.typeAdapter(), fieldClass, varName);
        } else {
            valueSetter = valueSetterGenerator.gen(fieldClass, varName, "jsonToken", signatureAttribute);
        }

        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("deserialize_line.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("fieldName", jsonFieldName);
        ctx.put("valueSetter", valueSetter);
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);

        return sw.toString();
    }

}
