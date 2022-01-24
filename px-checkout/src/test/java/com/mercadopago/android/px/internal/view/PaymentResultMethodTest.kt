package com.mercadopago.android.px.internal.view

import android.widget.TextView
import com.mercadopago.android.px.*
import com.mercadopago.android.px.assertText
import com.mercadopago.android.px.assertVisible
import com.mercadopago.android.px.getField
import com.mercadopago.android.px.internal.di.Session
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsText
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentInfo
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentResultInfo
import com.mercadopago.android.px.internal.util.CurrenciesUtil
import com.mercadopago.android.px.internal.util.JsonUtil
import com.mercadopago.android.px.internal.util.PaymentDataHelper
import com.mercadopago.android.px.model.*
import com.mercadopago.android.px.model.display_info.DisplayInfo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import java.math.BigDecimal

@RunWith(RobolectricTestRunner::class)
class PaymentResultMethodTest : BasicRobolectricTest() {

    @Mock
    private lateinit var currency: Currency

    private lateinit var methodView: PaymentResultMethod

    @Before
    fun setUp() {
        methodView = PaymentResultMethod(getContext())
        Session.initialize(getContext())

        `when`(currency.decimalPlaces).thenReturn(2)
        `when`(currency.decimalSeparator).thenReturn(',')
        `when`(currency.thousandsSeparator).thenReturn('.')
    }

    @Test
    fun whenInitWithCreditCardThenViewsAreCorrectlyLabeled() {
        val paymentMethodName = "Mastercard"
        val paymentMethodStatement = "pm_statement"
        val lastFourDigits = "2222"
        val discount = mock(Discount::class.java)
        `when`(discount.name).thenReturn("discount name")
        `when`(discount.couponAmount).thenReturn(BigDecimal.ONE)
        val infoTitle = "infoTitle"
        val infoSubtitle = "infoSubtitle"
        val displayInfo = JsonUtil.fromJson("""{
            "result_info": {
                "title": "$infoTitle",
                "subtitle": "$infoSubtitle"
            },
            "description": {
                "message": "$paymentMethodStatement",
                "text_color": "#ffffff",
                "background_color": "#000000",
                "weight": "regular"
            }
        }""".trimIndent(), DisplayInfo::class.java)
        val paymentMethod = mock(PaymentMethod::class.java)
        `when`(paymentMethod.paymentTypeId).thenReturn(PaymentTypes.CREDIT_CARD)
        `when`(paymentMethod.displayInfo).thenReturn(displayInfo)
        `when`(paymentMethod.name).thenReturn(paymentMethodName)
        val token = mock(Token::class.java)
        `when`(token.lastFourDigits).thenReturn(lastFourDigits)
        val paymentData = PaymentData.Builder()
            .setToken(token)
            .setDiscount(discount)
            .setRawAmount(BigDecimal.TEN)
            .setNoDiscountAmount(BigDecimal.TEN)
            .setPaymentMethod(paymentMethod)
            .createPaymentData()

        val paymentCongratsText = mock(PaymentCongratsText::class.java)
        `when`(paymentCongratsText.message).thenReturn(displayInfo!!.description.message)
        `when`(paymentCongratsText.backgroundColor).thenReturn(displayInfo.description.backgroundColor)
        `when`(paymentCongratsText.textColor).thenReturn(displayInfo.description.textColor)
        `when`(paymentCongratsText.weight).thenReturn(displayInfo.description.weight)

        val paymentResultInfo = mock(PaymentResultInfo::class.java)
        `when`(paymentResultInfo.title).thenReturn(displayInfo.resultInfo.title)
        `when`(paymentResultInfo.subtitle).thenReturn(displayInfo.resultInfo.subtitle)

        val paymentInfo = PaymentInfo.Builder()
            .withPaymentMethodName(paymentData.paymentMethod.name)
            .withPaymentMethodDescriptionText(paymentCongratsText)
            .withPaymentMethodType(PaymentInfo.PaymentMethodType.fromName(paymentData.paymentMethod.paymentTypeId))
            .withConsumerCreditsInfo(paymentResultInfo)
            .withPaidAmount(getPrettyAmount(currency, PaymentDataHelper.getPrettyAmountToPay(paymentData)))
            .withDiscountData(paymentData.discount!!.name, getPrettyAmount(currency, paymentData.noDiscountAmount))
            .withInstallmentsData(BigDecimal.TEN.toInt(), null, null, null)
            .withLastFourDigits(paymentData.token!!.lastFourDigits)
            .build()

        methodView.setModel(PaymentResultMethod.Model.with(paymentInfo, null))

        with(methodView) {
            getField<TextView>("description").apply {
                assertText("$paymentMethodName completed in $lastFourDigits")
                assertVisible()
            }
            getField<TextView>("infoTitle").apply {
                assertText(infoTitle)
                assertVisible()
            }
            getField<TextView>("infoSubtitle").apply {
                assertText(infoSubtitle)
                assertVisible()
            }
            getField<TextView>("paymentMethodStatement").apply {
                assertText(paymentMethodStatement)
                assertVisible()
            }
            getField<TextView>("statement").assertGone()
        }
    }

    private fun getPrettyAmount(currency: Currency, amount: BigDecimal): String? {
        return CurrenciesUtil.getLocalizedAmountWithoutZeroDecimals(currency, amount)
    }
}
