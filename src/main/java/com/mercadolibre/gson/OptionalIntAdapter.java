package com.mercadolibre.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.OptionalInt;

public class OptionalIntAdapter implements JsonSerializer<OptionalInt>, JsonDeserializer<OptionalInt> {

    @Override
    public JsonElement serialize(final OptionalInt src, final Type srcType, final JsonSerializationContext context) {
        return src != null && src.isPresent() ? new JsonPrimitive(src.getAsInt()) : JsonNull.INSTANCE;
    }

    @Override
    public OptionalInt deserialize(final JsonElement json, final Type type, final JsonDeserializationContext context)
            throws JsonParseException {
        return OptionalInt.of(json.getAsInt());
    }

}