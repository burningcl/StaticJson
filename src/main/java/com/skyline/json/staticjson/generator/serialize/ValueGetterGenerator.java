package com.skyline.json.staticjson.generator.serialize;

import com.skyline.json.staticjson.core.JsonConverterFactory;
import com.skyline.json.staticjson.generator.ConverterGenerator;
import com.skyline.json.staticjson.core.util.LoggerHolder;
import com.skyline.json.staticjson.core.exception.TypeMissException;
import com.skyline.json.staticjson.core.util.*;
import com.skyline.json.staticjson.generator.util.ClassPoolHelper;
import com.skyline.json.staticjson.generator.util.GenUtils;
import com.skyline.json.staticjson.generator.util.VelocityHelper;
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

import static com.skyline.json.staticjson.generator.util.GenUtils.TYPE_ITERABLE;
import static com.skyline.json.staticjson.generator.util.GenUtils.TYPE_MAP;

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
        SignatureAttribute.TypeArgument[] typeArguments = null;
        if (signatureAttribute != null) {
            SignatureAttribute.ClassType classType = SignatureAttribute.toClassSignature(signatureAttribute.getSignature()).getSuperClass();
            String classTypeName = classType.getName();
            Class<?> clazz = Class.forName(classTypeName);
            int type = GenUtils.getType(clazz);
            LoggerHolder.logger.debug(TAG, "gen, getType, classType: " + classType + ", value: " + type);
            typeArguments = classType.getTypeArguments();
        }
        return this.genImpl(ctClass, varName, typeArguments);
    }

    /**
     * 生成读取值的代码
     *
     * @param ctClass
     * @param varName
     * @param typeArguments
     * @return
     * @throws NotFoundException
     * @throws BadBytecode
     * @throws ClassNotFoundException
     * @throws CannotCompileException
     * @throws IOException
     */
    public String genImpl(CtClass ctClass, String varName, SignatureAttribute.TypeArgument[] typeArguments) throws NotFoundException, BadBytecode, ClassNotFoundException, CannotCompileException, IOException {
        LoggerHolder.logger.debug(TAG, "gen, start, ctClass: " + ctClass.getName() + ", varName: " + varName + ", typeArguments: " + typeArguments);
        if (ctClass.isArray()) {
            return genArrayValueGetter(ctClass, varName);
        } else if (typeArguments != null) {
            String classTypeName = ctClass.getName();
            Class<?> clazz = Class.forName(classTypeName);
            int type = GenUtils.getType(clazz);
            switch (type) {
                case TYPE_ITERABLE:
                    return this.genIterableValueGetter(typeArguments, varName);
                case TYPE_MAP:
                    return this.genMapValueGetter(typeArguments, varName);
                default:
                    LoggerHolder.logger.warn(TAG, "gen, ctClass: " + ctClass.getName() + ", value: " + type + ", unknown type!");
                    return "json.append(com.skyline.json.staticjson.core.util.GsonUtil.toJson(" + varName + "));";
            }
        } else if (PrimitiveUtil.isPrimitiveDataType(ctClass)) {
            return "jsonWriter.value(" + primitiveValWriter(ctClass, varName, false) + ");";
        } else if (PrimitiveUtil.isPrimitiveWrappedType(ctClass)) {
            return "jsonWriter.value(" + primitiveValWriter(ctClass, varName, true) + ");";
        } else if (StringUtil.isString(ctClass)) {
            return "jsonWriter.value(" + varName + ".toString());";
        } else if (ctClass.getName().equals(Object.class.getName())) {
            //如果是Object，则交给Gson来处理
            return "com.skyline.json.staticjson.core.util.GsonUtil.getGson().toJson(" + varName + ", Object.class, jsonWriter );";
        } else if (ctClass.getSuperclass().getName().equals(Enum.class.getName())) {
            return "jsonWriter.value((" + varName + ".toString()));";
        } else {
            converterGenerator.genConverter(ctClass);
            return JsonConverterFactory.class.getName()+".get(" + ctClass.getName() + ".class).write(" + varName + ", jsonWriter);";
        }
    }


    protected String primitiveValWriter(CtClass ctClass, String varName, boolean wrapped) {
        int index = PrimitiveUtil.getPrimitiveIndex(ctClass);
        switch (index) {
            case 0:
            case 1:
            case 2:
            case 3:
                return wrapped ? varName : "(long)" + varName;
            case 4:
                return wrapped ? varName : "Double.valueOf(String.valueOf(" + varName + "))";
            case 5:
                return varName;
            case 6:
                return varName;
            case 7:
                return "String.valueOf(" + varName + ")";
            default:
                return "";
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
        String valueGetter = this.genImpl(componentType, varName + "[" + indexName + "]", null);

        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("vm/serialize_array_subline.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("indexName", indexName);
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
    public String genIterableValueGetter(SignatureAttribute.TypeArgument[] typeArguments, String varName) throws NotFoundException, BadBytecode, ClassNotFoundException, CannotCompileException, IOException {
        if (typeArguments == null || typeArguments.length <= 0) {
            LoggerHolder.logger.warn(TAG, "gen, fail, type: " + TYPE_ITERABLE + ", typeArguments is missing!");
            throw new TypeMissException(varName + "'s typeArguments is missing!");
        }
        SignatureAttribute.ObjectType itemType = typeArguments[0].getType();
        String itemTypeName = GenUtils.getTypeName(itemType);
        CtClass itemTypeClass = ClassPoolHelper.getClassPool().get(itemTypeName);

        String iteratorName = "iterator" + getIndexValue();
        String varNextName = "varNext" + getIndexValue();
        String valueGetter = this.genImpl(itemTypeClass, varNextName, GenUtils.getSubTypeArguments(itemType));

        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("vm/serialize_iterable_subline.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("iteratorName", iteratorName);
        ctx.put("varName", varName);
        ctx.put("elementTypeName", itemTypeName);
        ctx.put("varNextName", varNextName);
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
        String valueGetter = this.genImpl(ctClass, varName + ".get(key)", null);

        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("vm/serialize_map_subline.vm");
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