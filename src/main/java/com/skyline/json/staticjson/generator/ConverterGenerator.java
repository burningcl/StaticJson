package com.skyline.json.staticjson.generator;

import com.skyline.json.staticjson.core.*;
import com.skyline.json.staticjson.core.annotation.JsonTarget;
import com.skyline.json.staticjson.core.util.LoggerHolder;
import com.skyline.json.staticjson.generator.deserialize.DeserializeMethodGenerator;
import com.skyline.json.staticjson.generator.serialize.SerializeMethodGenerator;
import com.skyline.json.staticjson.generator.util.AnnotationUtil;
import com.skyline.json.staticjson.generator.util.ClassPoolHelper;
import com.skyline.json.staticjson.generator.util.VelocityHelper;
import javassist.*;
import javassist.bytecode.BadBytecode;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * JsonConverter生成器
 * Created by chenliang on 2017/4/10.
 */
public class ConverterGenerator {

    static final String TAG = "ConverterGenerator";

    private String targetClassPath;

    /**
     * 已经处理的CtClass集合，以防止处理为一个CtClass生成JsonConverter
     */
    Set<CtClass> processedSet = new HashSet<CtClass>();

    JsonAspectInjector injector = new JsonAspectInjector();

    private String getOutputPath() {
        if (targetClassPath == null) {
            targetClassPath = ConverterGenerator.class.getClassLoader().getResource("").getPath();
        }
        return targetClassPath;
    }

    /**
     * @param targetClassPath
     * @param dependedClassPaths
     */
    public void gen(String targetClassPath, String... dependedClassPaths) {
        VelocityHelper.LOAD_FROM_CLASS_PATH = false;
        LoggerHolder.logger = new PrintLogger();
        try {
            this.targetClassPath = targetClassPath;
            ClassPool pool = ClassPoolHelper.getClassPool();
            this.insertClassPath(pool, targetClassPath);
            this.insertClassPath(pool, dependedClassPaths);
            this.gen(new File(targetClassPath));
        } catch (Exception e) {
            LoggerHolder.logger.error(TAG, "gen, fail", e);
        }
    }

    protected void insertClassPath(ClassPool pool, String... classpaths) throws NotFoundException {
        if (classpaths != null && classpaths.length > 0) {
            for (String classpath : classpaths) {
                if (classpath.contains(":")) {
                    this.insertClassPath(pool, classpath.split(":"));
                } else {
                    LoggerHolder.logger.warn(TAG, "gen, insertClassPath, classpath: " + classpath);
                    pool.insertClassPath(classpath);
                }
            }
        }
    }

    protected void gen(File dir) throws NotFoundException, CannotCompileException, IOException, BadBytecode, ClassNotFoundException {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return;
        }
        LoggerHolder.logger.warn(TAG, "gen, file: " + dir);
        File[] files = dir.listFiles();
        if (files == null || files.length <= 0) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                this.gen(file);
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }

                String filename = file.getAbsolutePath();
                if (!filename.startsWith(targetClassPath)) {
                    continue;
                }

                LoggerHolder.logger.warn(TAG, "gen, class: " + filename);
                filename = filename.substring(targetClassPath.length() + 1);
                filename = filename.replace(".class", "");
                filename = filename.replace("/", ".");
                filename = filename.replace("\\", ".");

                CtClass srcClass = ClassPoolHelper.getClassPool().get(filename);
                this.gen(srcClass);
            }
        }
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

        if (AnnotationUtil.getAnnotation4Class(ctClass, JsonTarget.class) != null) {
            this.genConverter(ctClass);
        } else {
            this.inject(ctClass);
        }
    }

    public void genConverter(CtClass ctClass) throws NotFoundException, CannotCompileException, IOException, BadBytecode, ClassNotFoundException {

        if (processedSet.contains(ctClass)) {
            return;
        }
        if (ctClass.isFrozen()) {
            ctClass.defrost();
        }

        ctClass.getClassFile().setMajorVersion(51);
        String nestedClassName = "JsonConverter";
        CtClass converterClass = findNestedClass(ctClass, nestedClassName);
        if (converterClass == null) {
            converterClass = ctClass.makeNestedClass(nestedClassName, true);
        }
        converterClass.getClassFile().setMajorVersion(51);
        this.addInterface(converterClass, StaticJsonConverter.class.getName());
        converterClass.setSuperclass(ClassPoolHelper.getClassPool().get(BaseStaticJsonConverter.class.getName()));

        // 生成序列化方法
        SerializeMethodGenerator serializeMethodGenerator = new SerializeMethodGenerator(this);
        CtMethod serializeMethod = serializeMethodGenerator.gen(ctClass, converterClass);
        converterClass.addMethod(serializeMethod);
        //生成反序列化方法
        DeserializeMethodGenerator deserializeMethodGenerator = new DeserializeMethodGenerator(this);
        CtMethod deserializeMethod = deserializeMethodGenerator.gen(ctClass, converterClass);
        converterClass.addMethod(deserializeMethod);

        this.addInterface(ctClass, StaticJsonObject.class.getName());

        ctClass.writeFile(getOutputPath());
        converterClass.writeFile(getOutputPath());
        processedSet.add(ctClass);
        processedSet.add(converterClass);
    }

    public void inject(CtClass ctClass) throws CannotCompileException, ClassNotFoundException, IOException {
        if (processedSet.contains(ctClass)) {
            return;
        }
        if (ctClass.isFrozen()) {
            ctClass.defrost();
        }
        processedSet.add(ctClass);
        ctClass.getClassFile().setMajorVersion(51);
        if (injector.inject(ctClass)) {
            ctClass.writeFile(getOutputPath());
        }
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
     * @param interfaceName
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    protected void addInterface(CtClass converterClass, String interfaceName) throws NotFoundException, CannotCompileException {
        String[] interfaces = converterClass.getClassFile().getInterfaces();
        if (interfaces != null) {
            for (String interfaceStr : interfaces) {
                if (interfaceStr.equals(interfaceName)) {
                    return;
                }
            }
        }
        converterClass.getClassFile().addInterface(interfaceName);
    }

}
