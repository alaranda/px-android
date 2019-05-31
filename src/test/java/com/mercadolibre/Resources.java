package com.mercadolibre;

import spark.utils.IOUtils;

import java.io.IOException;

public class Resources {

    public static String loadMock(final String mockedJson) throws IOException {
        return IOUtils.toString(Resources.class.getResourceAsStream(mockedJson));
    }
}
