package com.mercadopago.android.px.internal.features.payment_congrats

import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModel
import com.mercadopago.android.px.internal.viewmodel.PaymentModel

internal open class CongratsResult {
    data class PaymentResult(val paymentModel: PaymentModel) : CongratsResult()
    data class BusinessPaymentResult(val paymentCongratsModel: PaymentCongratsModel) : CongratsResult()
}

internal open class CongratsPaymentResult : CongratsResult() {
    data class SkipCongratsResult(val paymentModel: PaymentModel) : CongratsPaymentResult()
}

internal open class CongratsPostPaymentResult : CongratsResult() {
    object Loading : CongratsPostPaymentResult()
    object ConnectionError : CongratsPostPaymentResult()
    object BusinessError : CongratsPostPaymentResult()
}
