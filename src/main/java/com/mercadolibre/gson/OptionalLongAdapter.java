package com.mercadolibre.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.OptionalLong;

public class OptionalLongAdapter implements JsonSerializer<OptionalLong>, JsonDeserializer<OptionalLong> {

    @Override
    public JsonElement serialize(final OptionalLong src, final Type srcType, final JsonSerializationContext context) {
        return src != null && src.isPresent() ? new JsonPrimitive(src.getAsLong()) : JsonNull.INSTANCE;
    }

    @Override
    public OptionalLong deserialize(final JsonElement json, final Type type, final JsonDeserializationContext context)
            throws JsonParseException {
        return OptionalLong.of(json.getAsLong());
    }

}