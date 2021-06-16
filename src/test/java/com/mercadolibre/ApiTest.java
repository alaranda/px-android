package com.mercadolibre;

import com.mercadolibre.endpoints.*;
import com.mercadolibre.router.Router;
import com.mercadolibre.utils.ConfigurationServiceTestUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import spark.Spark;

@SuppressWarnings("PMD.UseUtilityClass")
@RunWith(Suite.class)
@Suite.SuiteClasses({
  PaymentRouterTest.class,
  PreferenceRouterTest.class,
  CongratsRouterTest.class,
  CapEscControllerTest.class,
  RemediesControllerTest.class,
  AuthenticationRouterTest.class,
  ConfigurationControllerTest.class
})
public class ApiTest {

  @BeforeClass
  public static void beforeSuite() {
    Spark.port(8080);
    ConfigurationServiceTestUtils.setupPropertiesForLocal();
    new Router().init();
    Spark.awaitInitialization();
  }

  @AfterClass
  public static void afterSuite() {
    Spark.stop();
  }
}
