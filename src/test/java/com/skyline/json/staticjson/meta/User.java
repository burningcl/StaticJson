package com.skyline.json.staticjson.meta;

import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenliang on 2017/4/10.
 */
public class User {

    /**
     *
     */
    private long id;

    /**
     *
     */
    private String name;

    /**
     *
     */
    private Gender gender;

    /**
     *
     */
    private int age;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public static User fromJson(JsonReader r) throws IOException {
        r.beginObject();
        User m = new User();
        while (r.hasNext()) {
            String name = r.nextName();
            switch (name) {
                case "id":
                    m.id = r.nextLong();
                    break;
                case "name":
                    m.name = r.nextString();
                    break;
                case "gender":
                    m.gender = Gender.valueOf(r.nextString());
                    break;
                case "age":
                    m.age = r.nextInt();
                    break;
            }
        }
        r.endObject();
        return m;
    }

    public static List<User> fromJsonArray(JsonReader r) throws IOException {
        r.beginArray();
        List<User> list = new ArrayList<User>();
        while (r.hasNext()) {
            list.add(fromJson(r));
        }
        r.endArray();
        return list;
    }

}
