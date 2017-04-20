package com.skyline.json.staticjson.meta;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.skyline.json.staticjson.StaticJsonConverter;
import com.skyline.json.staticjson.annotation.JsonField;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * Created by chenliang on 2017/4/10.
 */
public class Message {

    private static final String LOG_TAG = "Message";

    private long id;

    @JsonField(name = "deletedInt")
    private Boolean deleted;

    private String subject;

    private String content;

    private User from;

    private List<User> to;

    //private List<List<User>> toList;

    // private Map<String, User> toMap;

    private Object toArray;

//    private long[][] toMatrix;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public List<User> getTo() {
        return to;
    }

    public void setTo(List<User> to) {
        this.to = to;
    }

//    public Map<String, User> getToMap() {
//        return toMap;
//    }
//
//    public void setToMap(Map<String, User> toMap) {
//        this.toMap = toMap;
//    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    //public Long[] getToArray() {
//        return toArray;
//    }

    public void setToArray(Long[] toArray) {
        this.toArray = toArray;
    }


    public static Message fromJson(String json) throws IOException {
        JsonReader r = new JsonReader(new StringReader(json));
        r.beginObject();
        Message m = new Message();
        while (r.hasNext()) {
            String name = r.nextName();
            //System.out.println(name);
            switch (name) {
                case "id":
                    m.id = r.nextLong();
                    break;
                case "deleted":
                    m.deleted = r.nextBoolean();
                    break;
                case "subject":
                    m.subject = r.nextString();
                    break;
                case "content":
                    m.content = r.nextString();
                    break;
                case "from":
                    m.from = User.fromJson(r);
                    break;
                case "to":
                    m.to = User.fromJsonArray(r);
                    break;
                case "toArray":
                    r.beginArray();
                    while (r.hasNext()) {
                        r.nextInt();
                    }
                    r.endArray();
                    break;
                    default:
                       r.skipValue();
            }
        }
        r.endObject();
        return m;
    }
}
