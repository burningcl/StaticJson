package com.skyline.json.staticjson.deserialize;

import com.skyline.json.staticjson.ConverterGenerator;
import com.skyline.json.staticjson.util.LoggerHolder;
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
            return genArrayValueSetter(ctClass, varName, jsonTokenName);
        } else if (typeArguments != null && typeArguments.length > 0) {
            String classTypeName = ctClass.getName();
            Class<?> clazz = Class.forName(classTypeName);
            int type = GenUtils.getType(clazz);
            switch (type) {
                case TYPE_ITERABLE:
                    return this.genIterableValueSetter(ctClass, varName, jsonTokenName, typeArguments);
                case TYPE_MAP:
                    return this.genMapValueSetter(ctClass, varName, jsonTokenName, typeArguments);
                default:
                    LoggerHolder.logger.warn(TAG, "gen, ctClass: " + ctClass + ", value: " + type + ", unknown type");
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
                return varName + " = (" + ctClass.getName() + ")(new " + GenUtils.getJsonConverterName(ctClass.getName()) + "().read(jsonReader));";
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
        ctx.put("valReader", this.primitiveValReader(ctClass, PrimitiveUtil.isPrimitiveWrappedType(ctClass)));
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        return sw.toString();
    }

    protected String primitiveValReader(CtClass ctClass, boolean wrapped) {
        int index = PrimitiveUtil.getPrimitiveIndex(ctClass);
        switch (index) {
            case 0:
                return wrapped ? "Byte.valueOf((byte)jsonReader.nextInt())" : "(byte)jsonReader.nextInt()";
            case 1:
                return wrapped ? "Short.valueOf((short)jsonReader.nextInt())" : "(short)jsonReader.nextInt()";
            case 2:
                return wrapped ? "Integer.valueOf(jsonReader.nextInt())" : "jsonReader.nextInt()";
            case 3:
                return wrapped ? "Long.valueOf(jsonReader.nextLong())" : "jsonReader.nextLong()";
            case 4:
                return wrapped ? "Float.valueOf((float)jsonReader.nextDouble())" : "(float)jsonReader.nextDouble()";
            case 5:
                return wrapped ? "Double.valueOf(jsonReader.nextDouble())" : "jsonReader.nextDouble()";
            case 6:
                return wrapped ? "Boolean.valueOf(jsonReader.nextBoolean())" : "jsonReader.nextBoolean()";
            case 7:
                return wrapped ? "Character.valueOf(jsonReader.nextString().charAt(0))" : "jsonReader.nextString().charAt(0)";
            default:
                LoggerHolder.logger.error(TAG, "primitiveValReader, fail, is this a PrimitiveDataType, ctClass: " + ctClass, null);
                return "";
        }
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
    public String genArrayValueSetter(CtClass ctClass, String varName, String jsonTokenName) throws NotFoundException, BadBytecode, ClassNotFoundException, CannotCompileException, IOException {

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
    public String genIterableValueSetter(CtClass ctClass, String varName, String jsonTokenName, SignatureAttribute.TypeArgument[] typeArguments) throws NotFoundException, BadBytecode, ClassNotFoundException, CannotCompileException, IOException {

        if (typeArguments == null || typeArguments.length != 1) {
            throw new TypeMissException(varName + "'s typeArguments is missing, or typeArguments length is not 1!");
        }
        SignatureAttribute.ObjectType objectType = typeArguments[0].getType();
        SignatureAttribute.TypeArgument[] subTypeArguments = GenUtils.getSubTypeArguments(objectType);
        String itemType = GenUtils.getTypeName(objectType);
        CtClass elementTypeClass = ClassPoolHelper.getClassPool().get(itemType);

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
        ctx.put("varTmpName", "varTmp" + getIndexValue());
        ctx.put("itemName", itemName);
        ctx.put("subTokenName", subTokenName);
        ctx.put("valueSetter", valueSetter);
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
    public String genMapValueSetter(CtClass ctClass, String varName, String jsonTokenName, SignatureAttribute.TypeArgument[] typeArguments) throws NotFoundException, BadBytecode, ClassNotFoundException, CannotCompileException, IOException {
        if (typeArguments == null || typeArguments.length != 2) {
            LoggerHolder.logger.warn(TAG, "gen, fail, type: " + TYPE_MAP + ", typeArguments is missing!");
            throw new TypeMissException(varName + "'s typeArguments is missing, or typeArguments length is not 2!");
        }
        SignatureAttribute.ObjectType keyType = typeArguments[0].getType();
        String keyTypeName = GenUtils.getTypeName(keyType);
        String keyName = "key" + getIndexValue();
        String keyTokenName = "keyToken" + getIndexValue();
        String keySetter = this.genImpl(ClassPoolHelper.getClassPool().get(keyTypeName), keyName, keyTokenName, GenUtils.getSubTypeArguments(keyType));

        SignatureAttribute.ObjectType valueType = typeArguments[1].getType();
        String valueTypeName = GenUtils.getTypeName(valueType);
        String valueName = "value" + getIndexValue();
        String valueTokenName = "valueToken" + getIndexValue();
        String valueSetter = this.genImpl(ClassPoolHelper.getClassPool().get(valueTypeName), valueName, valueTokenName, GenUtils.getSubTypeArguments(valueType));

        Class<?> mapType = GenUtils.getMapClass(Class.forName(ctClass.getName()));
        String mapTypeName = mapType.getName();

        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("deserialize_map_subline.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("jsonTokenName", jsonTokenName);
        ctx.put("varType", mapTypeName);
        ctx.put("varName", varName);
        ctx.put("keyType", keyTypeName);
        ctx.put("keyName", keyName);
        ctx.put("keyTokenName", keyTokenName);
        ctx.put("keyJson", "keyJson" + getIndexValue());
        ctx.put("jsonReader", "jsonReader" + getIndexValue());
        ctx.put("stringReader", "stringReader" + getIndexValue());
        ctx.put("keySetter", keySetter);
        ctx.put("valueType", valueTypeName);
        ctx.put("valueName", valueName);
        ctx.put("valueTokenName", valueTokenName);
        ctx.put("valueSetter", valueSetter);

        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        return sw.toString();
    }


}