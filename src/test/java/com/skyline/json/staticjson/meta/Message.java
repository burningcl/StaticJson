package com.skyline.json.staticjson.meta;

import com.skyline.json.staticjson.core.annotation.JsonField;
import com.skyline.json.staticjson.core.annotation.JsonTarget;

import java.util.*;

/**
 * Created by chenliang on 2017/4/10.
 */
@JsonTarget
public class Message {

    private static final String LOG_TAG = "Message";

    private long id;

    @JsonField(jsonName = "deletedInt")
    private Boolean deleted;

    private String subject;

    private String content;

    private User from;

    private List<User> to;

    private long[] toArray;

//    Map<String, User> toMap;

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

    public void setToArray(long[] toArray) {
        this.toArray = toArray;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (id != message.id) return false;
        // if (!deleted.equals(message.deleted)) return false;
        if (!subject.equals(message.subject)) return false;
        if (!content.equals(message.content)) return false;
        if (!from.equals(message.from)) return false;
        if (!to.equals(message.to)) return false;
        return Arrays.equals(toArray, message.toArray);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + deleted.hashCode();
        result = 31 * result + subject.hashCode();
        result = 31 * result + content.hashCode();
        result = 31 * result + from.hashCode();
        result = 31 * result + to.hashCode();
        result = 31 * result + Arrays.hashCode(toArray);
        return result;
    }
}
