package com.skyline.json.staticjson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * 不做任何事情，只是为了在JsonField中default值
 * Created by chenliang on 2017/4/21.
 */
public class TypeAdapterNull extends TypeAdapter {

    @Override
    public void write(JsonWriter out, Object value) throws IOException {

    }

    @Override
    public Object read(JsonReader in) throws IOException {
        return null;
    }
}
