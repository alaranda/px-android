package com.mercadolibre.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.mercadolibre.dto.User;
import com.mercadolibre.px.dto.lib.context.Context;
import com.mercadolibre.px.toolkit.exceptions.ApiException;
import com.mercadolibre.restclient.RestClientTestBase;
import com.mercadolibre.restclient.mock.RequestMockHolder;
import java.io.IOException;
import java.util.UUID;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import spark.utils.IOUtils;

public class UserAPITest extends RestClientTestBase {

  private final Long USER_ID = 243962506L;
  private final String USER_EMAIL = "testuser.mla.02@gmail.com";
  private final Context context = Context.builder().requestId(UUID.randomUUID().toString()).build();
  private final UserAPI userAPI = UserAPI.INSTANCE;

  @Before
  public void setUp() {
    RequestMockHolder.clear();
  }

  @Test
  public void getById_ok() throws IOException, ApiException {
    MockUserAPI.getById(
        USER_ID,
        HttpStatus.SC_OK,
        IOUtils.toString(getClass().getResourceAsStream("/user/243962506.json")));
    User user = userAPI.getById(context, USER_ID);
    assertEquals(String.valueOf(USER_ID), user.getId());
    assertEquals(USER_EMAIL, user.getEmail());
  }

  @Test
  public void getById_throws() throws IOException {
    try {
      MockUserAPI.getById(
          12345L,
          HttpStatus.SC_OK,
          IOUtils.toString(getClass().getResourceAsStream("/user/243962506.json")));
      User user = userAPI.getById(context, USER_ID);
      fail("Exception expected");
    } catch (ApiException e) {
      assertEquals("API call to users failed", e.getDescription());
    }
  }

  @Test
  public void getById_fail() throws IOException {
    try {
      MockUserAPI.getById(
          12L,
          HttpStatus.SC_NOT_FOUND,
          IOUtils.toString(getClass().getResourceAsStream("/user/userNotFound.json")));
      User user = userAPI.getById(context, 12L);
      fail("Exception expected");
    } catch (ApiException e) {
      assertEquals("User, 12, not found", e.getDescription());
      assertEquals(HttpStatus.SC_NOT_FOUND, e.getStatusCode());
    }
  }
}
