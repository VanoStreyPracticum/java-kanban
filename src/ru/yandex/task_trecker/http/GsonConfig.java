package ru.yandex.task_trecker.http;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class GsonConfig {

    public static Gson buildGson() {
        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(LocalTime.class, new JsonSerializer<LocalTime>() {
            @Override
            public JsonElement serialize(LocalTime src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_TIME));
            }
        });
        builder.registerTypeAdapter(LocalTime.class, new JsonDeserializer<LocalTime>() {
            @Override
            public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                return LocalTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_TIME);
            }
        });

        return builder.setPrettyPrinting().create();
    }
}
