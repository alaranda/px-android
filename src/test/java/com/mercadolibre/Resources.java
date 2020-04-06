package com.mercadolibre;

import java.io.IOException;
import spark.utils.IOUtils;

public class Resources {

  public static String loadMock(final String mockedJson) throws IOException {
    return IOUtils.toString(Resources.class.getResourceAsStream(mockedJson));
  }
}
