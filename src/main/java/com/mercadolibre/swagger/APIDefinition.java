package com.mercadolibre.swagger;

import com.mercadolibre.dto.cha.CardHolderAuthenticationRequest;
import com.mercadolibre.dto.cha.CardHolderAuthenticationResponse;
import com.mercadolibre.dto.congrats.Congrats;
import com.mercadolibre.dto.fraud.ResetStatus;
import com.mercadolibre.dto.payment.Payment;
import com.mercadolibre.dto.payment.PaymentDataBody;
import com.mercadolibre.dto.payment.PaymentRequestBody;
import com.mercadolibre.dto.preference.PreferenceResponse;
import com.mercadolibre.dto.remedy.RemediesRequest;
import com.mercadolibre.dto.remedy.RemediesResponse;
import com.mercadolibre.px.dto.lib.swagger.params.AccessTokenOptionalQueryParam;
import com.mercadolibre.px.dto.lib.swagger.params.CallerIdOptionalQueryParam;
import com.mercadolibre.px.dto.lib.swagger.params.CallerIdRequiredQueryParam;
import com.mercadolibre.px.dto.lib.swagger.params.CallerSiteIdOptionalQueryParam;
import com.mercadolibre.px.dto.lib.swagger.params.ClientIdOptionalQueryParam;
import com.mercadolibre.px.dto.lib.swagger.params.ClientIdRequiredQueryParam;
import com.mercadolibre.px.dto.lib.swagger.params.DensityRequiredHeaderParam;
import com.mercadolibre.px.dto.lib.swagger.params.LanguageOptionalHeaderParam;
import com.mercadolibre.px.dto.lib.swagger.params.PlatformOptionalHeaderParam;
import com.mercadolibre.px.dto.lib.swagger.params.PlatformRequiredQueryParam;
import com.mercadolibre.px.dto.lib.swagger.params.ProductIdRequiredHeaderParam;
import com.mercadolibre.px.dto.lib.swagger.params.PublicKeyOptionalQueryParam;
import com.mercadolibre.px.dto.lib.swagger.params.PublicKeyRequiredQueryParam;
import com.mercadolibre.px.dto.lib.swagger.params.SessionIdOptionalHeaderParam;
import com.mercadolibre.px.dto.lib.swagger.params.UserAgentOptionalHeaderParam;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

/** The Api Definition. */
@OpenAPIDefinition(
    info =
        @Info(
            title = "[PX] Checkout Mobile Payments",
            // Fury sets the version number automatically
            version = "1.0",
            contact = @Contact(name = "Team PX Nativo", email = "px_nativo@mercadolibre.com")),
    servers = @Server(url = "https://internal-api.mercadopago.com/v1"))
public interface APIDefinition {

  String PX_MOBILE = "px_mobile";

  String HTTP_STATUS_OK = "200";

  String APPLICATION_RESPONDED_SUCCESSFULLY = "Application responded successfully";

  String INTERNAL_API_MERCADOPAGO_COM_PRODUCTION =
      "https://internal-api.mercadopago.com/production";

  /**
   * The POST Legacy Payment.
   *
   * @return the Payment response
   */
  @POST
  @Path("/px_mobile/legacy_payments")
  @Operation(tags = PX_MOBILE, summary = "Legacy payment", description = "Do legacy payment")
  @PublicKeyRequiredQueryParam
  @SessionIdOptionalHeaderParam
  @RequestBody(
      required = true,
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = PaymentRequestBody.class)))
  @ApiResponse(
      responseCode = HTTP_STATUS_OK,
      description = APPLICATION_RESPONDED_SUCCESSFULLY,
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = Payment.class)))
  Payment doLegacyPayment();

  /**
   * The POST Payment.
   *
   * @return the Payment response
   */
  @POST
  @Path("/px_mobile/payments")
  @Operation(tags = PX_MOBILE, summary = "Payment", description = "Do payment")
  @PublicKeyRequiredQueryParam
  @CallerIdOptionalQueryParam
  @ClientIdOptionalQueryParam
  @SessionIdOptionalHeaderParam
  @RequestBody(
      required = true,
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = PaymentDataBody.class)))
  @ApiResponse(
      responseCode = HTTP_STATUS_OK,
      description = APPLICATION_RESPONDED_SUCCESSFULLY,
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = Payment.class)))
  Payment doPayment();

  /**
   * The GET Init Checkout by Preference.
   *
   * @return the Preference response
   */
  @GET
  @Path("/px_mobile/init/preference")
  @Operation(tags = PX_MOBILE, summary = "Init Checkout by Preference")
  @Parameters({
    @Parameter(
        in = ParameterIn.QUERY,
        name = "short_id",
        description = "The id in its short version. If 'pref_id is passed, it's not required.",
        required = true,
        example = "23BYCZ"),
    @Parameter(
        in = ParameterIn.QUERY,
        name = "pref_id",
        description = "The preference id. If 'short_id' is passed, it's not required.",
        required = true,
        example = "49177602-ab824142-b64e-468c-84c0-cf71fe36d5dc"),
    @Parameter(in = ParameterIn.QUERY, name = "flow_id", description = "The flow id.")
  })
  @CallerIdOptionalQueryParam
  @ClientIdOptionalQueryParam
  @SessionIdOptionalHeaderParam
  @ApiResponse(
      responseCode = HTTP_STATUS_OK,
      description = APPLICATION_RESPONDED_SUCCESSFULLY,
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = PreferenceResponse.class)))
  PreferenceResponse initCheckoutByPref();

  /**
   * The GET Congrats.
   *
   * @return the Congrats response
   */
  @GET
  @Path("/px_mobile/congrats")
  @Operation(tags = PX_MOBILE, summary = "Congrats")
  @Parameters({
    @Parameter(
        in = ParameterIn.QUERY,
        name = "payment_ids",
        description = "The list of payments, comma separated",
        allowReserved = true,
        example = "1231234,1241241"),
    @Parameter(in = ParameterIn.QUERY, name = "campaign_id", description = "The campaign's ID"),
    @Parameter(in = ParameterIn.QUERY, name = "flow_name", description = "The flow's name"),
    @Parameter(
        in = ParameterIn.QUERY,
        name = "ifpe",
        description = "The IFPE",
        schema = @Schema(implementation = Boolean.class)),
    @Parameter(
        in = ParameterIn.QUERY,
        name = "payment_methods_ids",
        description = "The list of payment methods, comma separated",
        example = "credit_card,account_money"),
    @Parameter(
        in = ParameterIn.QUERY,
        name = "pref_id",
        description = "The preference id.",
        example = "49177602-ab824142-b64e-468c-84c0-cf71fe36d5dc"),
    @Parameter(
        in = ParameterIn.QUERY,
        name = "merchant_order_id",
        description = "The merchant order id."),
    @Parameter(
        in = ParameterIn.QUERY,
        name = "merchant_account_id",
        description = "The merchant account id."),
    @Parameter(
        in = ParameterIn.QUERY,
        name = "payment_type_id",
        description = "The payment type id.")
  })
  @CallerIdRequiredQueryParam
  @ClientIdOptionalQueryParam
  @CallerSiteIdOptionalQueryParam
  @DensityRequiredHeaderParam
  @AccessTokenOptionalQueryParam
  @LanguageOptionalHeaderParam
  @ProductIdRequiredHeaderParam
  @UserAgentOptionalHeaderParam
  @PublicKeyOptionalQueryParam
  @PlatformRequiredQueryParam
  @SessionIdOptionalHeaderParam
  @ApiResponse(
      responseCode = HTTP_STATUS_OK,
      description = APPLICATION_RESPONDED_SUCCESSFULLY,
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = Congrats.class)))
  Congrats getCongrats();

  /**
   * The DELETE Reset Cap Esc.
   *
   * @return the Reset response
   */
  @DELETE
  @Path("/px_mobile/v1/esc_cap/:cardId")
  @Operation(
      tags = PX_MOBILE,
      summary = "Reset Cap Esc",
      servers = @Server(url = INTERNAL_API_MERCADOPAGO_COM_PRODUCTION))
  @Parameter(in = ParameterIn.PATH, name = "cardId", description = "The card ID", required = true)
  @ClientIdRequiredQueryParam
  @SessionIdOptionalHeaderParam
  @ApiResponse(
      responseCode = HTTP_STATUS_OK,
      description = APPLICATION_RESPONDED_SUCCESSFULLY,
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = ResetStatus.class)))
  ResetStatus resetCapEsc();

  /**
   * The POST Get Remedy.
   *
   * @return the Remedies response
   */
  @POST
  @Path("/px_mobile/v1/remedies/:paymentId")
  @Operation(
      tags = PX_MOBILE,
      summary = "Get Remedy",
      servers = @Server(url = INTERNAL_API_MERCADOPAGO_COM_PRODUCTION))
  @Parameters({
    @Parameter(
        in = ParameterIn.PATH,
        name = "paymentId",
        description = "The payment ID",
        required = true),
    @Parameter(
        in = ParameterIn.HEADER,
        name = "one_tap",
        description = "The one tap indicator",
        schema = @Schema(implementation = Boolean.class))
  })
  @CallerSiteIdOptionalQueryParam
  @CallerIdOptionalQueryParam
  @PlatformOptionalHeaderParam
  @SessionIdOptionalHeaderParam
  @RequestBody(
      required = true,
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = RemediesRequest.class)))
  @ApiResponse(
      responseCode = HTTP_STATUS_OK,
      description = APPLICATION_RESPONDED_SUCCESSFULLY,
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = RemediesResponse.class)))
  RemediesResponse getRemedy();

  /**
   * The POST Authenticate Card Holder.
   *
   * @return the Card Holder Authentication response
   */
  @POST
  @Path("/px_mobile/authentication/v1/card_holder")
  @Operation(
      tags = PX_MOBILE,
      summary = "Authenticate Card Holder",
      servers = @Server(url = INTERNAL_API_MERCADOPAGO_COM_PRODUCTION))
  @Parameter(
      in = ParameterIn.QUERY,
      name = "card_token",
      description = "The card's token",
      required = true)
  @CallerIdRequiredQueryParam
  @SessionIdOptionalHeaderParam
  @RequestBody(
      required = true,
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = CardHolderAuthenticationRequest.class)))
  @ApiResponse(
      responseCode = HTTP_STATUS_OK,
      description = APPLICATION_RESPONDED_SUCCESSFULLY,
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON,
              schema = @Schema(implementation = CardHolderAuthenticationResponse.class)))
  CardHolderAuthenticationResponse authenticateCardHolder();
}
