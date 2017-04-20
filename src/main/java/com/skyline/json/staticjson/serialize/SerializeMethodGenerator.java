package com.skyline.json.staticjson.serialize;

import com.skyline.json.staticjson.ConverterGenerator;
import com.skyline.json.staticjson.LoggerHolder;
import com.skyline.json.staticjson.util.VelocityHelper;
import com.skyline.json.staticjson.util.StringUtil;
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
     * @param converterClass 转换类，类型为{@link com.skyline.json.staticjson.StaticJsonConverter}
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
        Template t = ve.getTemplate("serialize_method.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("codeLines", codeLines);
        ctx.put("instanceType", targetClass.getName());
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        String methodBody = sw.toString();
        LoggerHolder.logger.info(TAG, "gen, success, methodBody: \n" + methodBody);

        CtMethod convert2JsonMethod = null;
        try {
            //如果已经存在了convert2Json方法，则直接把它删除
            convert2JsonMethod = converterClass.getDeclaredMethod("convert2Json");
            converterClass.removeMethod(convert2JsonMethod);
        } catch (NotFoundException ignore) {
        }
        convert2JsonMethod = CtNewMethod.make(methodBody, converterClass);
        return convert2JsonMethod;
    }


}
