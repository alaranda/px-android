package com.mercadolibre.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mercadolibre.utils.Either;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.util.OptionalInt;
import java.util.OptionalLong;

public enum GsonWrapper {
    ;

    private static final Gson GSON;

    static {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder
                .registerTypeAdapter(Either.class, new EitherAdapter())
                .registerTypeAdapter(OptionalLong.class, new OptionalLongAdapter())
                .registerTypeAdapterFactory(OptionalAdapter.FACTORY)
                .registerTypeAdapter(OptionalInt.class, new OptionalIntAdapter())
                .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        GSON = gsonBuilder.create();
    }

    /**
     * Serializes the given object into a JSON string
     *
     * @param object the object to be serialized
     * @return the JSON string representing the object
     */
    public static String toJson(final Object object) {
        return GSON.toJson(object);
    }

    /**
     * Deserializes the given JSON into a object
     *
     * @param json the JSON string
     * @param clazz the class of the object that the JSON represents
     * @param <T> the type of the object
     * @return the object represented by the JSON string
     */
    public static <T> T fromJson(final String json, final Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    /**
     * Deserializes the given JSON into a object
     *
     * @param json the JSON string
     * @param type the type of the object that the JSON represents
     * @param <T> the type of the object
     * @return the object represented by the JSON string
     */
    public static <T> T fromJson(final String json, final Type type) {
        return GSON.fromJson(json, type);
    }

}