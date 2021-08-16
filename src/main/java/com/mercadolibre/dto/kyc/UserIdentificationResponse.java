package com.mercadolibre.dto.kyc;

import com.mercadolibre.px.dto.lib.kyc.v2.KycError;
import com.mercadolibre.px.dto.lib.kyc.v2.KycUserResponse;
import com.mercadolibre.px.dto.lib.kyc.v2.KycUserResponseData;
import java.util.List;

public class UserIdentificationResponse extends KycUserResponse<UserIdentification> {

  public UserIdentificationResponse(
      List<KycError> errors, KycUserResponseData<UserIdentification> data) {
    super(errors, data);
  }
}
