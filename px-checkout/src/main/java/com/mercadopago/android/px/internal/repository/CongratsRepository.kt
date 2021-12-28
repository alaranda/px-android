package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.model.IPaymentDescriptor
import com.mercadopago.android.px.model.PaymentResult

internal interface CongratsRepository {
    fun getPostPaymentData(payment: IPaymentDescriptor, paymentResult: PaymentResult,
                           callback: PostPaymentCallback)

    fun getPostPaymentFlowData(payment: IPaymentDescriptor, deeplink: String, callback: PostPaymentFlowCallback)

    interface PostPaymentCallback {
        fun handleResult(paymentModel: PaymentModel)
    }

    interface PostPaymentFlowCallback {
        fun handlePostPaymentResult(iPaymentDescriptor: IPaymentDescriptor, deeplink: String)
    }
}