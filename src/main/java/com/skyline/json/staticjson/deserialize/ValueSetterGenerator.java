package com.skyline.json.staticjson.deserialize;

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
     * @param varName
     * @param jsonTokenName
     * @param signatureAttribute
     * @return
     * @throws NotFoundException
     * @throws BadBytecode
     * @throws ClassNotFoundException
     * @throws CannotCompileException
     * @throws IOException
     */
    public String gen(CtClass ctClass, String varName, String jsonTokenName, SignatureAttribute signatureAttribute) throws NotFoundException, BadBytecode, ClassNotFoundException, CannotCompileException, IOException {

        SignatureAttribute.TypeArgument[] typeArguments = null;
        if (signatureAttribute != null) {
            SignatureAttribute.ClassType classType = SignatureAttribute.toClassSignature(signatureAttribute.getSignature()).getSuperClass();
            String classTypeName = ctClass.getName();
            Class<?> clazz = Class.forName(classTypeName);
            int type = GenUtils.getType(clazz);
            LoggerHolder.logger.debug(TAG, "gen, getType, classType: " + classType + ", value: " + type);
            typeArguments = classType.getTypeArguments();
        }
        return this.genImpl(ctClass, varName, jsonTokenName, typeArguments);
    }

    /**
     * 生成设置值的代码
     *
     * @param ctClass
     * @param varName
     * @param jsonTokenName
     * @param typeArguments
     * @return
     * @throws NotFoundException
     * @throws BadBytecode
     * @throws ClassNotFoundException
     * @throws CannotCompileException
     * @throws IOException
     */
    protected String genImpl(CtClass ctClass, String varName, String jsonTokenName, SignatureAttribute.TypeArgument[] typeArguments) throws NotFoundException, BadBytecode, ClassNotFoundException, CannotCompileException, IOException {
        LoggerHolder.logger.debug(TAG, "gen, start, ctClass: " + ctClass.getName() + ", varName: " + varName + ", typeArguments: " + typeArguments);
        if (ctClass.isArray()) {
            return genArrayValueGetter(ctClass, varName, jsonTokenName);
        } else if (typeArguments != null && typeArguments.length > 0) {
            String classTypeName = ctClass.getName();
            Class<?> clazz = Class.forName(classTypeName);
            int type = GenUtils.getType(clazz);
            switch (type) {
                case TYPE_ITERABLE:
                    return this.genIterableValueGetter(ctClass, varName, jsonTokenName, typeArguments);
//                case TYPE_MAP:
//                    return this.genMapValueGetter(ctClass, instanceFieldName, jsonParamName, typeArguments);
                default:
                    LoggerHolder.logger.warn(TAG, "gen, ctClass: " + ctClass + ", value: " + type + ", use Gson");

                    return "";
            }
        } else if (PrimitiveUtil.isPrimitiveDataType(ctClass) || PrimitiveUtil.isPrimitiveWrappedType(ctClass)) {
            return genPrimitiveValueSetter(ctClass, varName, jsonTokenName);
        } else if (StringUtil.isString(ctClass)) {
            return genStringValueSetter(ctClass, varName, jsonTokenName);
        } else {
            if (ctClass.getName().equals(Object.class.getName())) {
                //如果是Object，则不处理
                return varName + " = com.skyline.json.staticjson.util.GsonUtil.getGson().fromJson(jsonReader, Object.class);";
            } else if (ctClass.isEnum()) {
                return this.genEnumValueSetter(ctClass, varName, jsonTokenName);
            } else {
                converterGenerator.gen(ctClass);
                return varName + " = ("+ctClass.getName()+")(new " + GenUtils.getJsonConverterName(ctClass.getName()) + "().read(jsonReader));";
            }
        }
    }

    /**
     * @param ctClass
     * @param varName
     * @param jsonTokenName
     * @return
     */
    public String genPrimitiveValueSetter(CtClass ctClass, String varName, String jsonTokenName) {
        int index = PrimitiveUtil.getPrimitiveIndex(ctClass);
        String methodName = PrimitiveUtil.JSON_GET_METHOD[index];

        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("deserialize_primitive_subline.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("varName", varName);
        ctx.put("varType", ctClass.getName());
        ctx.put("methodName", methodName);
        ctx.put("jsonTokenName", jsonTokenName);
        ctx.put("isPrimitive", PrimitiveUtil.isPrimitiveDataType(ctClass));
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        return sw.toString();
    }

    /**
     * @param ctClass
     * @param varName
     * @param jsonTokenName
     * @return
     */
    public String genStringValueSetter(CtClass ctClass, String varName, String jsonTokenName) {
        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("deserialize_string_subline.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("varName", varName);
        ctx.put("varType", ctClass.getName());
        ctx.put("jsonTokenName", jsonTokenName);
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        return sw.toString();
    }

    /**
     * @param ctClass
     * @param varName
     * @param jsonTokenName
     * @return
     */
    public String genEnumValueSetter(CtClass ctClass, String varName, String jsonTokenName) {
        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("deserialize_enum_subline.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("varName", varName);
        ctx.put("jsonTokenName", jsonTokenName);
        ctx.put("varType", ctClass.getName());
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        return sw.toString();
    }

    /**
     * @param ctClass
     * @param varName
     * @param jsonTokenName
     * @return
     * @throws NotFoundException
     * @throws BadBytecode
     * @throws ClassNotFoundException
     * @throws CannotCompileException
     * @throws IOException
     */
    public String genArrayValueGetter(CtClass ctClass, String varName, String jsonTokenName) throws NotFoundException, BadBytecode, ClassNotFoundException, CannotCompileException, IOException {

        String listName = "list" + getIndexValue();
        CtClass componentType = ctClass.getComponentType();
        int primitiveIndex = PrimitiveUtil.getPrimitiveIndex(componentType);
        if (primitiveIndex >= 0) {
            componentType = ClassPoolHelper.getClassPool().get(PrimitiveUtil.WRAPPED_TYPES[primitiveIndex]);
        }
        String itemType = componentType.getName();
        String itemName = "item" + getIndexValue();
        String subTokenName = "subToken" + getIndexValue();

        String valueSetter = this.genImpl(componentType, itemName, subTokenName, null);

        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("deserialize_array_subline.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("jsonTokenName", jsonTokenName);
        ctx.put("listName", listName);
        ctx.put("itemType", itemType);
        ctx.put("itemName", itemName);
        ctx.put("subTokenName", subTokenName);
        ctx.put("valueSetter", valueSetter);
        ctx.put("varName", varName);
        ctx.put("varType", ctClass.getName());
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        return sw.toString();
    }

    /**
     * @param ctClass
     * @param varName
     * @param jsonTokenName
     * @param typeArguments
     * @return
     * @throws NotFoundException
     * @throws BadBytecode
     * @throws ClassNotFoundException
     * @throws CannotCompileException
     * @throws IOException
     */
    public String genIterableValueGetter(CtClass ctClass, String varName, String jsonTokenName, SignatureAttribute.TypeArgument[] typeArguments) throws NotFoundException, BadBytecode, ClassNotFoundException, CannotCompileException, IOException {

        if (typeArguments == null || typeArguments.length != 1) {
            throw new TypeMissException(varName + "'s typeArguments is missing, or typeArguments length is not 1!");
        }
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

        String subTokenName = "token" + getIndexValue();

        String itemName = "item" + getIndexValue();
        String valueSetter = this.genImpl(elementTypeClass, itemName, subTokenName, subTypeArguments);

        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("deserialize_iterable_subline.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("jsonTokenName", jsonTokenName);
        ctx.put("varType", iterableType.getName());
        ctx.put("varName", varName);
        ctx.put("itemType", itemType);
        ctx.put("itemName", itemName);
        ctx.put("subTokenName", subTokenName);
        ctx.put("valueSetter", valueSetter);
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        return sw.toString();
    }
//
//    /**
//     * @param typeArguments
//     * @return
//     * @throws NotFoundException
//     * @throws BadBytecode
//     * @throws ClassNotFoundException
//     * @throws CannotCompileException
//     * @throws IOException
//     */
//    public String genMapValueGetter(CtClass ctClass, String instanceFieldName, String jsonParamName, SignatureAttribute.TypeArgument[] typeArguments) throws NotFoundException, BadBytecode, ClassNotFoundException, CannotCompileException, IOException {
//        if (typeArguments == null || typeArguments.length != 2) {
//            LoggerHolder.logger.warn(TAG, "gen, fail, type: " + TYPE_ITERABLE + ", typeArguments is missing!");
//            throw new TypeMissException("paramName's typeArguments is missing, or typeArguments length is not 2!");
//        }
//        SignatureAttribute.ObjectType keyType = typeArguments[0].getType();
//        String keyTypeName = keyType.toString();
//        if (!keyTypeName.equals(String.class.getName())) {
//            throw new TypeNotMatchedException(instanceFieldName + ".key", keyTypeName, String.class.getName());
//        }
//        SignatureAttribute.ObjectType valueType = typeArguments[1].getType();
//
//        SignatureAttribute.TypeArgument[] subTypeArguments = null;
//        CtClass elementTypeClass = null;
//        if (valueType instanceof SignatureAttribute.ClassType) {
//            SignatureAttribute.ClassType classType = (SignatureAttribute.ClassType) valueType;
//            elementTypeClass = ClassPoolHelper.getClassPool().get(classType.getName());
//            subTypeArguments = classType.getTypeArguments();
//        } else if (valueType instanceof SignatureAttribute.ArrayType) {
//            //todo:
//        }
//        String localVarName = "localVar" + getIndexValue();
//        String itemName = "item" + getIndexValue();
//        String setName = "set" + getIndexValue();
//        String iteratorName = "iterator" + getIndexValue();
//        Class<?> mapType = GenUtils.getMapClass(Class.forName(ctClass.getName()));
//        String mapTypeName = mapType.getName();
//        String entryName = "entry" + getIndexValue();
//        String valueName = "value" + getIndexValue();
//
//        String valueSetter = this.genImpl(elementTypeClass, valueName, entryName + ".getValue()", subTypeArguments);
//
//        VelocityEngine ve = VelocityHelper.getVelocityEngine();
//        Template t = ve.getTemplate("deserialize_map_subline.vm");
//        VelocityContext ctx = new VelocityContext();
//        ctx.put("localVarName", localVarName);
//        ctx.put("jsonParamName", jsonParamName);
//        ctx.put("itemName", itemName);
//        ctx.put("setName", setName);
//        ctx.put("iteratorName", iteratorName);
//        ctx.put("instanceFieldName", instanceFieldName);
//        ctx.put("mapType", mapTypeName);
//        ctx.put("valueType", elementTypeClass.getName());
//        ctx.put("entryName", entryName);
//        ctx.put("valueName", valueName);
//        ctx.put("valueSetter", valueSetter);
//        StringWriter sw = new StringWriter();
//        t.merge(ctx, sw);
//        return sw.toString();
//    }
}