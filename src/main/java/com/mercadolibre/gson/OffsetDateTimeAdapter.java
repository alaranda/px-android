package com.mercadolibre.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeAdapter implements JsonSerializer<OffsetDateTime>, JsonDeserializer<OffsetDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    @Override
    public JsonElement serialize(final OffsetDateTime src, final Type srcType, final JsonSerializationContext context) {
        return new JsonPrimitive(src.format(FORMATTER));
    }

    @Override
    public OffsetDateTime deserialize(final JsonElement json, final Type type, final JsonDeserializationContext context)
            throws JsonParseException {
        return OffsetDateTime.parse(json.getAsString(), FORMATTER);
    }

}