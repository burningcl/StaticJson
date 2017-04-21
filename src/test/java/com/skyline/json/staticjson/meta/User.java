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
                case "jsonName":
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (age != user.age) return false;
        if (!name.equals(user.name)) return false;
        return gender == user.gender;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + gender.hashCode();
        result = 31 * result + age;
        return result;
    }
}
