package com.mercadolibre.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mercadolibre.utils.Either;

import java.lang.reflect.Type;

public class EitherAdapter implements JsonSerializer<Either<Object, Object>> {

    @Override
    public JsonElement serialize(final Either<Object, Object> src, final Type srcType,
                                 final JsonSerializationContext context) {
        return context.serialize(src.isValuePresent() ? src.getValue() : src.getAlternative());
    }
}