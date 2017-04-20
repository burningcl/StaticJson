package com.skyline.json.staticjson.deserialize;

import com.skyline.json.staticjson.ConverterGenerator;
import com.skyline.json.staticjson.LoggerHolder;
import com.skyline.json.staticjson.exception.TypeExpectedException;
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
 * 生成读设值的代码
 * Created by chenliang on 2017/4/12.
 */
public class ValueSetterGenerator {

    public static final String TAG = "ValueSetterGenerator";

    /**
     * 为了保证所生成的变量名不会重复，由此变量来加以区分
     */
    private static int VARIABLE_INDEX = 0;

    ConverterGenerator converterGenerator;

    public ValueSetterGenerator(ConverterGenerator converterGenerator) {
        this.converterGenerator = converterGenerator;
    }

    public static int getIndexValue() {
        return VARIABLE_INDEX++;
    }

    /**
     * 生成设置值的代码
     *
     * @param ctClass
     * @param instanceFieldName
     * @param jsonParamName
     * @param signatureAttribute
     * @return
     * @throws NotFoundException
     * @throws BadBytecode
     * @throws ClassNotFoundException
     * @throws CannotCompileException
     * @throws IOException
     */
    public String gen(CtClass ctClass, String instanceFieldName, String jsonParamName, SignatureAttribute signatureAttribute) throws NotFoundException, BadBytecode, ClassNotFoundException, CannotCompileException, IOException {

        LoggerHolder.logger.debug(TAG, "gen, start, ctClass: " + ctClass + ", jsonParamName: " + jsonParamName + ", signatureAttribute: " + signatureAttribute);
        SignatureAttribute.TypeArgument[] typeArguments = null;
        if (signatureAttribute != null) {
            SignatureAttribute.ClassType classType = SignatureAttribute.toClassSignature(signatureAttribute.getSignature()).getSuperClass();
            String classTypeName = ctClass.getName();
            Class<?> clazz = Class.forName(classTypeName);
            int type = GenUtils.getType(clazz);
            LoggerHolder.logger.debug(TAG, "gen, getType, classType: " + classType + ", value: " + type);
            typeArguments = classType.getTypeArguments();
        }
        return this.genImpl(ctClass, instanceFieldName, jsonParamName, typeArguments);
    }

    /**
     * 生成设置值的代码
     *
     * @param ctClass
     * @param instanceFieldName
     * @param jsonParamName
     * @param typeArguments
     * @return
     * @throws NotFoundException
     * @throws BadBytecode
     * @throws ClassNotFoundException
     * @throws CannotCompileException
     * @throws IOException
     */
    protected String genImpl(CtClass ctClass, String instanceFieldName, String jsonParamName, SignatureAttribute.TypeArgument[] typeArguments) throws NotFoundException, BadBytecode, ClassNotFoundException, CannotCompileException, IOException {
        LoggerHolder.logger.debug(TAG, "gen, start, ctClass: " + ctClass + ", jsonParamName: " + jsonParamName + ", typeArguments: " + typeArguments);
        if (ctClass.isArray()) {
            return genArrayValueGetter(ctClass, instanceFieldName, jsonParamName);
        } else if (typeArguments != null && typeArguments.length > 0) {
            String classTypeName = ctClass.getName();
            Class<?> clazz = Class.forName(classTypeName);
            int type = GenUtils.getType(clazz);
            switch (type) {
                case TYPE_ITERABLE:
                    return this.genIterableValueGetter(ctClass, instanceFieldName, jsonParamName, typeArguments);
                case TYPE_MAP:
                    return this.genMapValueGetter(ctClass, instanceFieldName, jsonParamName, typeArguments);
                default:
                    LoggerHolder.logger.warn(TAG, "gen, ctClass: " + ctClass + ", value: " + type + ", use Gson");

                    return "";
            }
        } else if (PrimitiveUtil.isPrimitiveDataType(ctClass) || PrimitiveUtil.isPrimitiveWrappedType(ctClass)) {
            return genPrimitiveStringValueSetter(ctClass, instanceFieldName, jsonParamName, true);
        } else if (StringUtil.isString(ctClass)) {
            return genPrimitiveStringValueSetter(ctClass, instanceFieldName, jsonParamName, false);
        } else {
            if (ctClass.getName().equals(Object.class.getName())) {
                //如果是Object，则不处理
                return instanceFieldName + " = " + jsonParamName;
            } else if (ctClass.isEnum()) {
                return this.genEnumValueSetter(ctClass, instanceFieldName, jsonParamName);
            } else {
                converterGenerator.gen(ctClass);
                return "";//instanceFieldName + " = ("+ctClass.getName()+")(new " + GenUtils.getJsonConverterName(ctClass.getName()) + "().convert2Object(" + jsonParamName + ".toString()));";
            }
        }
    }


    /**
     * @param ctClass
     * @param instanceFieldName
     * @param jsonParamName
     * @param isPrimitive
     * @return
     */
    public String genPrimitiveStringValueSetter(CtClass ctClass, String instanceFieldName, String jsonParamName, boolean isPrimitive) {
        String getMethodName = "getAsString";
        if (isPrimitive) {
            int index = PrimitiveUtil.getPrimitiveType(ctClass);
            getMethodName = PrimitiveUtil.JSON_GET_METHOD[index];
        }

        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("deserialize_primitive_string_subline.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("jsonParamName", jsonParamName);
        ctx.put("instanceFieldType", ctClass.getName());
        ctx.put("instanceFieldName", instanceFieldName);
        ctx.put("getMethodName", getMethodName);
        ctx.put("localVarName", "localVar" + getIndexValue());
        ctx.put("isPrimitive", PrimitiveUtil.isPrimitiveDataType(ctClass));
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        return sw.toString();
    }

    /**
     * @param ctClass
     * @param instanceFieldName
     * @param jsonParamName
     * @return
     */
    public String genEnumValueSetter(CtClass ctClass, String instanceFieldName, String jsonParamName) {
        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("deserialize_enum_subline.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("jsonParamName", jsonParamName);
        ctx.put("instanceFieldName", instanceFieldName);
        ctx.put("instanceFieldType", ctClass.getName());
        ctx.put("localVarName", "localVar" + getIndexValue());
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        return sw.toString();
    }

    /**
     * @param ctClass
     * @param instanceFieldName
     * @param jsonParamName
     * @return
     * @throws NotFoundException
     * @throws BadBytecode
     * @throws ClassNotFoundException
     * @throws CannotCompileException
     * @throws IOException
     */
    public String genArrayValueGetter(CtClass ctClass, String instanceFieldName, String jsonParamName) throws NotFoundException, BadBytecode, ClassNotFoundException, CannotCompileException, IOException {
        String jsonArrayName = "jsonArray" + getIndexValue();
        String indexName = "index" + getIndexValue();
        CtClass componentType = ctClass.getComponentType();
        String instanceFieldType = componentType.getName();
        String itemType = instanceFieldType;
        int primitiveIndex = PrimitiveUtil.getPrimitiveType(componentType);
        if (primitiveIndex >= 0) {
            itemType = PrimitiveUtil.WRAPPED_TYPES[primitiveIndex];
        }
        String itemName = "item" + getIndexValue();
        CtClass cur = componentType;
        String typeTail = "";
        boolean isItemArray = false;
        while (cur.isArray()) {
            typeTail += "[]";
            cur = cur.getComponentType();
            isItemArray = true;
        }
        String basicType = cur.getName();

        String valueSetter = this.genImpl(componentType, itemName, jsonArrayName + ".get(" + indexName + ")", null);

        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("deserialize_array_subline.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("jsonParamName", jsonParamName);
        ctx.put("jsonArrayName", jsonArrayName);
        ctx.put("instanceFieldName", instanceFieldName);
        ctx.put("instanceFieldType", instanceFieldType);
        ctx.put("typeTail", typeTail);
        ctx.put("isItemArray", isItemArray);
        ctx.put("basicType", basicType);
        ctx.put("itemType", itemType);
        ctx.put("indexName", indexName);
        ctx.put("itemName", itemName);
        ctx.put("valueSetter", valueSetter);
        ctx.put("localVarName", "localVar" + getIndexValue());
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        return sw.toString();
    }

    /**
     * @param ctClass
     * @param instanceFieldName
     * @param jsonParamName
     * @param typeArguments
     * @return
     * @throws NotFoundException
     * @throws BadBytecode
     * @throws ClassNotFoundException
     * @throws CannotCompileException
     * @throws IOException
     */
    public String genIterableValueGetter(CtClass ctClass, String instanceFieldName, String jsonParamName, SignatureAttribute.TypeArgument[] typeArguments) throws NotFoundException, BadBytecode, ClassNotFoundException, CannotCompileException, IOException {

        SignatureAttribute.ObjectType objectType = typeArguments[0].getType();
        SignatureAttribute.TypeArgument[] subTypeArguments = null;
        CtClass elementTypeClass = null;
        String itemType = "";
        if (objectType instanceof SignatureAttribute.ClassType) {
            SignatureAttribute.ClassType classType = (SignatureAttribute.ClassType) objectType;
            elementTypeClass = ClassPoolHelper.getClassPool().get(classType.getName());
            itemType = classType.getName();
            subTypeArguments = classType.getTypeArguments();
        } else if (objectType instanceof SignatureAttribute.ArrayType) {
            //todo:
        }
        Class<?> iterableType = GenUtils.getIterableClass(Class.forName(ctClass.getName()));
        String iterableTypeName = iterableType.getName();

        String localVarName = "localVar" + getIndexValue();
        String jsonArrayName = "jsonArray" + getIndexValue();
        String indexName = "index" + getIndexValue();

        String itemName = "item" + getIndexValue();
        String valueSetter = this.genImpl(elementTypeClass, itemName, jsonArrayName + ".get(" + indexName + ")", subTypeArguments);

        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("deserialize_iterable_subline.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("localVarName", localVarName);
        ctx.put("jsonParamName", jsonParamName);
        ctx.put("jsonArrayName", jsonArrayName);
        ctx.put("iterableTypeName", iterableTypeName);
        ctx.put("itemType", itemType);
        ctx.put("itemName", itemName);
        ctx.put("instanceFieldName", instanceFieldName);
        ctx.put("valueSetter", valueSetter);
        ctx.put("indexName", indexName);
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        return sw.toString();
    }

    /**
     * @param typeArguments
     * @return
     * @throws NotFoundException
     * @throws BadBytecode
     * @throws ClassNotFoundException
     * @throws CannotCompileException
     * @throws IOException
     */
    public String genMapValueGetter(CtClass ctClass, String instanceFieldName, String jsonParamName, SignatureAttribute.TypeArgument[] typeArguments) throws NotFoundException, BadBytecode, ClassNotFoundException, CannotCompileException, IOException {
        if (typeArguments == null || typeArguments.length != 2) {
            LoggerHolder.logger.warn(TAG, "gen, fail, type: " + TYPE_ITERABLE + ", typeArguments is missing!");
            throw new TypeMissException("paramName's typeArguments is missing, or typeArguments length is not 2!");
        }
        SignatureAttribute.ObjectType keyType = typeArguments[0].getType();
        String keyTypeName = keyType.toString();
        if (!keyTypeName.equals(String.class.getName())) {
            throw new TypeExpectedException(instanceFieldName + ".key", keyTypeName, String.class.getName());
        }
        SignatureAttribute.ObjectType valueType = typeArguments[1].getType();

        SignatureAttribute.TypeArgument[] subTypeArguments = null;
        CtClass elementTypeClass = null;
        if (valueType instanceof SignatureAttribute.ClassType) {
            SignatureAttribute.ClassType classType = (SignatureAttribute.ClassType) valueType;
            elementTypeClass = ClassPoolHelper.getClassPool().get(classType.getName());
            subTypeArguments = classType.getTypeArguments();
        } else if (valueType instanceof SignatureAttribute.ArrayType) {
            //todo:
        }
        String localVarName = "localVar" + getIndexValue();
        String itemName = "item" + getIndexValue();
        String setName = "set" + getIndexValue();
        String iteratorName = "iterator" + getIndexValue();
        Class<?> mapType = GenUtils.getMapClass(Class.forName(ctClass.getName()));
        String mapTypeName = mapType.getName();
        String entryName = "entry" + getIndexValue();
        String valueName = "value" + getIndexValue();

        String valueSetter = this.genImpl(elementTypeClass, valueName, entryName + ".getValue()", subTypeArguments);

        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("deserialize_map_subline.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("localVarName", localVarName);
        ctx.put("jsonParamName", jsonParamName);
        ctx.put("itemName", itemName);
        ctx.put("setName", setName);
        ctx.put("iteratorName", iteratorName);
        ctx.put("instanceFieldName", instanceFieldName);
        ctx.put("mapType", mapTypeName);
        ctx.put("valueType", elementTypeClass.getName());
        ctx.put("entryName", entryName);
        ctx.put("valueName", valueName);
        ctx.put("valueSetter", valueSetter);
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        return sw.toString();
    }
}