package com.skyline.json.staticjson;

import com.skyline.json.staticjson.deserialize.DeserializeMethodGenerator;
import com.skyline.json.staticjson.serialize.SerializeMethodGenerator;
import com.skyline.json.staticjson.util.ClassPoolHelper;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * JsonConverter生成器
 * Created by chenliang on 2017/4/10.
 */
public class ConverterGenerator {

    private String outPath = null;

    /**
     * 已经处理的CtClass集合，以防止处理为一个CtClass生成JsonConverter
     */
    Set<CtClass> processedSet = new HashSet<CtClass>();

    private String getOutPath() {
        if (outPath == null) {
            outPath = ConverterGenerator.class.getClassLoader().getResource("").getPath();
        }
        return outPath;
    }

    /**
     * @param paths
     */
    public void gen(String paths) {

    }

    /**
     * @param ctClass
     * @throws NotFoundException
     * @throws CannotCompileException
     * @throws IOException
     * @throws BadBytecode
     * @throws ClassNotFoundException
     */
    public void gen(CtClass ctClass) throws NotFoundException, CannotCompileException, IOException, BadBytecode, ClassNotFoundException {
        if (processedSet.contains(ctClass)) {
            return;
        }
        processedSet.add(ctClass);
        String nestedClassName = "JsonConverter";
        CtClass converterClass = findNestedClass(ctClass, nestedClassName);
        if (converterClass == null) {
            converterClass = ctClass.makeNestedClass(nestedClassName, true);
        }
        this.addInterface(converterClass);
        converterClass.setSuperclass(ClassPoolHelper.getClassPool().get(BaseStaticJsonConverter.class.getName()));

        SerializeMethodGenerator serializeMethodGenerator = new SerializeMethodGenerator(this);
        CtMethod serializeMethod = serializeMethodGenerator.gen(ctClass, converterClass);
        converterClass.addMethod(serializeMethod);
        DeserializeMethodGenerator deserializeMethodGenerator = new DeserializeMethodGenerator(this);
        CtMethod deserializeMethod = deserializeMethodGenerator.gen(ctClass, converterClass);
        converterClass.addMethod(deserializeMethod);
        ctClass.writeFile(getOutPath());
        converterClass.writeFile(getOutPath());
    }

    /**
     * @param ctClass
     * @param nestedClassName
     * @return
     * @throws NotFoundException
     */
    protected CtClass findNestedClass(CtClass ctClass, String nestedClassName) throws NotFoundException {
        CtClass[] nestedClasses = ctClass.getNestedClasses();
        String fullName = ctClass.getName() + "$" + nestedClassName;
        if (nestedClasses == null || nestedClasses.length <= 0) {
            return null;
        }
        for (CtClass clazz : nestedClasses) {
            if (clazz.getName().equals(fullName)) {
                return clazz;
            }
        }
        return null;
    }

    /**
     * @param converterClass
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    protected void addInterface(CtClass converterClass) throws NotFoundException, CannotCompileException {
        String[] interfaces = converterClass.getClassFile().getInterfaces();
        if (interfaces != null) {
            for (String interfaceStr : interfaces) {
                if (interfaceStr.equals(StaticJsonConverter.class.getName())) {
                    return;
                }
            }
        }
        converterClass.getClassFile().addInterface(StaticJsonConverter.class.getName());
    }

}
