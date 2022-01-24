package com.mercadopago.android.px.internal.di

import com.mercadopago.android.px.internal.features.payment_result.model.DisplayInfoHelper

internal class HelperModule {
    val displayInfoHelper: DisplayInfoHelper
        get() {
            val session = Session.getInstance()
            return DisplayInfoHelper(session.payerPaymentMethodRepository, session.configurationModule.userSelectionRepository)
        }
}
