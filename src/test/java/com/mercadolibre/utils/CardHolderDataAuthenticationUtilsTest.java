package com.mercadolibre.utils;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.junit.Test;

public class CardHolderDataAuthenticationUtilsTest {

  private static final Character DECIMAL_SEPARATOR = ',';
  private static final Character THOUSAND_SEPARATOR = '.';

  @Test
  public void testFormatAmount_fourCases() {

    final String case1 = "123.123";
    final String result1 =
        CardHolderAuthenticationUtils.formatAmount(case1, THOUSAND_SEPARATOR, DECIMAL_SEPARATOR);

    final String case2 = "123.123,1";
    final String result2 =
        CardHolderAuthenticationUtils.formatAmount(case2, THOUSAND_SEPARATOR, DECIMAL_SEPARATOR);

    final String case3 = "123,12";
    final String result3 =
        CardHolderAuthenticationUtils.formatAmount(case3, THOUSAND_SEPARATOR, DECIMAL_SEPARATOR);

    final String case4 = "123.123,123";
    final String result4 =
        CardHolderAuthenticationUtils.formatAmount(case4, THOUSAND_SEPARATOR, DECIMAL_SEPARATOR);

    assertThat(result1, is("12312300"));
    assertThat(result2, is("12312310"));
    assertThat(result3, is("12312"));
    assertThat(result4, is("12312312"));
  }

  @Test
  public void testFormatDate_customDate() {

    final LocalDateTime date = LocalDateTime.of(2021, 3, 2, 1, 2, 3);
    final String formattedDate =
        CardHolderAuthenticationUtils.formatDate(
            Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));

    assertThat(formattedDate, is("20210302010203"));
  }
}
