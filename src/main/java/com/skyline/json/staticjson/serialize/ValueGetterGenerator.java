package com.skyline.json.staticjson.serialize;

import com.skyline.json.staticjson.ConverterGenerator;
import com.skyline.json.staticjson.LoggerHolder;
import com.skyline.json.staticjson.exception.TypeMissException;
import com.skyline.json.staticjson.util.*;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.SignatureAttribute;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.StringWriter;

import static com.skyline.json.staticjson.util.GenUtils.TYPE_ITERABLE;
import static com.skyline.json.staticjson.util.GenUtils.TYPE_MAP;

/**
 * 生成读取值的代码
 * Created by chenliang on 2017/4/12.
 */
public class ValueGetterGenerator {

    public static final String TAG = "ValueGetterGenerator";

    /**
     * 为了保证所生成的变量名不会重复，由此变量来加以区分
     */
    private static int VARIABLE_INDEX = 0;

    ConverterGenerator converterGenerator;

    public ValueGetterGenerator(ConverterGenerator converterGenerator) {
        this.converterGenerator = converterGenerator;
    }

    public static int getIndexValue() {
        return VARIABLE_INDEX++;
    }

    /**
     * 生成读取值的代码
     *
     * @param ctClass
     * @param varName
     * @param signatureAttribute
     * @return
     * @throws NotFoundException
     * @throws BadBytecode
     * @throws ClassNotFoundException
     * @throws CannotCompileException
     * @throws IOException
     */
    public String gen(CtClass ctClass, String varName, SignatureAttribute signatureAttribute) throws NotFoundException, BadBytecode, ClassNotFoundException, CannotCompileException, IOException {
        LoggerHolder.logger.debug(TAG, "gen, start, ctClass: " + ctClass.getName() + ", varName: " + varName + ", signatureAttribute: " + signatureAttribute);
        if (ctClass.isArray()) {
            return genArrayValueGetter(ctClass, varName);
        } else if (signatureAttribute != null) {
            SignatureAttribute.ClassType classType = SignatureAttribute.toClassSignature(signatureAttribute.getSignature()).getSuperClass();
            String classTypeName = classType.getName();
            Class<?> clazz = Class.forName(classTypeName);
            int type = GenUtils.getType(clazz);
            LoggerHolder.logger.debug(TAG, "gen, getType, classType: " + classType + ", value: " + type);
            SignatureAttribute.TypeArgument[] typeArguments = classType.getTypeArguments();
            switch (type) {
                case TYPE_ITERABLE:
                    return this.genIterableValueGetter(typeArguments, varName);
                case TYPE_MAP:
                    return this.genMapValueGetter(typeArguments, varName);
                default:
                    LoggerHolder.logger.warn(TAG, "gen, classType: " + classType + ", value: " + type + ", use Gson");
                    return "json.append(com.skyline.json.staticjson.util.GsonUtil.toJson(" + varName + "));";
            }
        } else if (PrimitiveUtil.isPrimitiveDataType(ctClass)) {
            int index = PrimitiveUtil.getPrimitiveIndex(ctClass);
            String wrappedClassName = PrimitiveUtil.WRAPPED_TYPES[index];
            String valueMethod = valueMethod(wrappedClassName);
            return "jsonWriter.value( "
                    + wrappedClassName
                    + ".valueOf("
                    + varName
                    +")."+ valueMethod + "());";
        } else if (PrimitiveUtil.isPrimitiveWrappedType(ctClass)) {
            String valueMethod = valueMethod(ctClass.getName());
            return "jsonWriter.value(" + varName + "." + valueMethod + "());";
        } else if (StringUtil.isString(ctClass)) {
            return "jsonWriter.value(" + varName + ".toString());";
        } else {
            if (ctClass.getName().equals(Object.class.getName())) {
                //如果是Object，则交给Gson来处理
                return "com.skyline.json.staticjson.util.GsonUtil.getGson().toJson(" + varName + ", Object.class, jsonWriter );";
            } else {
                if (ctClass.getSuperclass().getName().equals(Enum.class.getName())) {
                    return "jsonWriter.value((" + varName + ".toString()));";
                } else {
                    converterGenerator.gen(ctClass);
                    return "new " + GenUtils.getJsonConverterName(ctClass.getName()) + "().write(" + varName + ", jsonWriter);";
                }
            }
        }
    }

    private String valueMethod(String className) {
        if (className.equals(Boolean.class.getName())) {
            return "booleanValue";
        } else if (className.equals(Float.class.getName()) || className.equals(Double.class.getName())) {
            return "doubleValue";
        } else {
            return "longValue";
        }
    }

    /**
     * @param ctClass
     * @param varName
     * @return
     * @throws NotFoundException
     * @throws BadBytecode
     * @throws ClassNotFoundException
     * @throws CannotCompileException
     * @throws IOException
     */
    public String genArrayValueGetter(CtClass ctClass, String varName) throws NotFoundException, BadBytecode, ClassNotFoundException, CannotCompileException, IOException {
        String indexName = "index" + getIndexValue();
        CtClass componentType = ctClass.getComponentType();
        String valueGetter = this.gen(componentType, varName + "[" + indexName + "]", null);

        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("serialize_array_subline.vm");
        // 设置变量
        VelocityContext ctx = new VelocityContext();
        ctx.put("indexName", indexName);
        ctx.put("varName", varName);
        ctx.put("valueGetter", valueGetter);
        // 输出
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        return sw.toString();
    }

    /**
     * @param typeArguments
     * @param varName
     * @return
     * @throws NotFoundException
     * @throws BadBytecode
     * @throws ClassNotFoundException
     * @throws CannotCompileException
     * @throws IOException
     */
    public String genIterableValueGetter(SignatureAttribute.TypeArgument[] typeArguments, String varName) throws NotFoundException, BadBytecode, ClassNotFoundException, CannotCompileException, IOException {
        if (typeArguments == null || typeArguments.length <= 0) {
            LoggerHolder.logger.warn(TAG, "gen, fail, type: " + TYPE_ITERABLE + ", typeArguments is missing!");
            throw new TypeMissException(varName + "'s typeArguments is missing!");
        }
        String elementTypeName = typeArguments[0].getType().toString();
        CtClass ctClass = null;
        try {
            ctClass = ClassPoolHelper.getClassPool().get(elementTypeName);
        } catch (NotFoundException e) {
            LoggerHolder.logger.warn(TAG, "gen, type: " + TYPE_ITERABLE + ", elementType: " + elementTypeName + " not found!");
            ctClass = ClassPoolHelper.getClassPool().get(Object.class.getName());
        }
        elementTypeName = ctClass.getName();

        String iteratorName = "iterator" + getIndexValue();
        String valueGetter = this.gen(ctClass, "(" + elementTypeName + ")(" + iteratorName + ".next())", null);

        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("serialize_iterable_subline.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("iteratorName", iteratorName);
        ctx.put("varName", varName);
        ctx.put("valueGetter", valueGetter);
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        return sw.toString();
    }

    /**
     * @param typeArguments
     * @param varName
     * @return
     * @throws NotFoundException
     * @throws BadBytecode
     * @throws ClassNotFoundException
     * @throws CannotCompileException
     * @throws IOException
     */
    public String genMapValueGetter(SignatureAttribute.TypeArgument[] typeArguments, String varName) throws NotFoundException, BadBytecode, ClassNotFoundException, CannotCompileException, IOException {
        if (typeArguments == null || typeArguments.length != 2) {
            LoggerHolder.logger.warn(TAG, "gen, fail, type: " + TYPE_MAP + ", typeArguments is missing!");
            throw new TypeMissException(varName + "'s typeArguments is missing, or typeArguments length is not 2!");
        }
        String keyTypeName = typeArguments[0].getType().toString();
        String valueTypeName = typeArguments[1].getType().toString();
        CtClass ctClass = null;
        try {
            ctClass = ClassPoolHelper.getClassPool().get(valueTypeName);
        } catch (NotFoundException e) {
            LoggerHolder.logger.warn(TAG, "gen, type: " + TYPE_MAP + ", valueTypeName: " + valueTypeName + " not found!");
            ctClass = ClassPoolHelper.getClassPool().get(Object.class.getName());
        }

        String iteratorName = "iterator" + getIndexValue();
        String valueGetter = this.gen(ctClass,   varName + ".get(key)", null);

        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("serialize_map_subline.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("keyType", keyTypeName);
        ctx.put("iteratorName", iteratorName);
        ctx.put("varName", varName);
        ctx.put("valueGetter", valueGetter);
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        return sw.toString();
    }
}