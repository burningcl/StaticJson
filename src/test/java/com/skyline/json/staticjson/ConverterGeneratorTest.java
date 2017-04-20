package com.skyline.json.staticjson;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.skyline.json.staticjson.meta.Gender;
import com.skyline.json.staticjson.meta.Message;
import com.skyline.json.staticjson.meta.User;
import com.skyline.json.staticjson.serialize.SerializeLineGenerator;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.Loader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenliang on 2017/4/13.
 */
public class ConverterGeneratorTest {

    @BeforeClass
    public static void before() {
        LoggerHolder.logger = new PrintLogger();
    }

    @Test
    public void genTest() throws Exception {
//        SerializeLineGenerator generator = new SerializeLineGenerator(new ConverterGenerator());
        ClassPool pool = ClassPool.getDefault();
        pool.appendSystemPath();
        CtClass ctClass = pool.get("com.skyline.json.staticjson.meta.Message");
        new ConverterGenerator().gen(ctClass);

        Message message = new Message();
        message.setId(100l);
        message.setSubject("this is the subject!");
        message.setContent("this is the content!");
        User from = new User();
        from.setId(101l);
        from.setAge(20);
        from.setGender(Gender.FEMALE);
        from.setName("UserFrom");
        message.setFrom(from);
        List<User> toList = new ArrayList<User>();
        User to = new User();
        to.setId(102l);
        to.setAge(21);
        to.setGender(Gender.MALE);
        to.setName("UserTo");
        toList.add(to);
        message.setTo(toList);
        Map<String, User> toMap = new HashMap<String, User>();
        toMap.put(from.getId() + "", from);
        toMap.put(to.getId() + "", to);
        //message.setToMap(toMap);
        long[] toArray = new long[2];
        toArray[0] = from.getId();
        toArray[1] = to.getId();
        message.setToArray(toArray);

        long t1, t2, t3;
        StaticJsonConverter staticJsonConverter = (StaticJsonConverter) Class.forName("com.skyline.json.staticjson.meta.Message$JsonConverter").newInstance();
        Gson gson = new Gson();
        t1 = System.nanoTime();
        System.out.println(gson.toJson(message));
        t2 = System.nanoTime();
        System.out.println(staticJsonConverter.convert2Json(message));
        t3 = System.nanoTime();
        System.out.println((double) (t2 - t1) / (t3 - t2));

        String json =
                "{\"id\":100,\"subject\":\"this is the subject!\",\"content\":\"this is the content!\",\"from\":{\"id\":101,\"name\":\"UserFrom\",\"gender\":\"FEMALE\",\"age\":20},\"to\":[{\"id\":102,\"name\":\"UserTo\",\"gender\":\"MALE\",\"age\":21}],\"toArray\":[101,102]}";
        Message m1 = null;
        Message m2 = null;
        t1 = System.nanoTime();
        m1 = gson.fromJson(json, Message.class);
        t2 = System.nanoTime();
        m2 = (Message) staticJsonConverter.convert2Object(json);
        t3 = System.nanoTime();
        System.out.println(m1.equals(m2));
        System.out.println((double) (t2 - t1) / (t3 - t2));

        StringWriter stringWriter = null;
        JsonWriter jsonWriter = null;
        try {
            stringWriter = new StringWriter();
            jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.beginObject();
            jsonWriter.name("key");
            jsonWriter.value("value");
            jsonWriter.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (jsonWriter != null)
                    jsonWriter.close();
                if (stringWriter != null)
                    stringWriter.close();
            } catch (Exception ignored) {
            }
        }
        System.out.println( stringWriter.toString());

    }
}
