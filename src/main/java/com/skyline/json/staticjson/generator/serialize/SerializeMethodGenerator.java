package com.skyline.json.staticjson.generator.serialize;

import com.skyline.json.staticjson.generator.ConverterGenerator;
import com.skyline.json.staticjson.core.StaticJsonConverter;
import com.skyline.json.staticjson.core.util.LoggerHolder;
import com.skyline.json.staticjson.generator.util.VelocityHelper;
import com.skyline.json.staticjson.core.util.StringUtil;
import javassist.*;
import javassist.bytecode.BadBytecode;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 序列化方法生成器
 * Created by chenliang on 2017/4/10.
 */
public class SerializeMethodGenerator {

    static final String TAG = "SerializeMethodGenerator";

    ConverterGenerator converterGenerator;

    SerializeLineGenerator serializeLineGenerator;

    public SerializeMethodGenerator(ConverterGenerator converterGenerator) {
        this.converterGenerator = converterGenerator;
        serializeLineGenerator = new SerializeLineGenerator(converterGenerator);
    }

    /**
     * 为targetClass生成序列化方法
     *
     * @param targetClass    目标类
     * @param converterClass 转换类，类型为{@link StaticJsonConverter}
     * @return
     */
    public CtMethod gen(CtClass targetClass, CtClass converterClass) throws ClassNotFoundException, NotFoundException, CannotCompileException, BadBytecode, IOException {
        if (targetClass == null) {
            throw new NullPointerException("gen, fail, targetClass is null");

        }

        CtField[] fields = targetClass.getDeclaredFields();

        List<String> codeLines = new ArrayList<String>();
        for (CtField field : fields) {
            //对各个field生成转换代码
            String codeLine = serializeLineGenerator.gen(field);
            if (!StringUtil.isBlank(codeLine)) {
                codeLines.add(codeLine);
            }
        }

        VelocityEngine ve = VelocityHelper.getVelocityEngine();
        Template t = ve.getTemplate("vm/serialize_method.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("codeLines", codeLines);
        ctx.put("instanceType", targetClass.getName());
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        String methodBody = sw.toString();
        LoggerHolder.logger.error(TAG, "gen, success, methodBody: \n" + methodBody,null);

        CtMethod writeMethod = null;
        try {
            //如果已经存在了write方法，则直接把它删除
            writeMethod = converterClass.getDeclaredMethod("write");
            converterClass.removeMethod(writeMethod);
        } catch (NotFoundException ignore) {
        }
        writeMethod = CtNewMethod.make(methodBody, converterClass);
        return writeMethod;
    }


}
