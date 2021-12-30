package com.mercadopago.android.px.internal.di

import com.mercadopago.android.px.internal.datasource.TransactionInfoFactory

internal class FactoryModule {
    val transactionInfoFactory: TransactionInfoFactory
        get() {
            val session = Session.getInstance()
            return TransactionInfoFactory(session.payerPaymentMethodRepository)
        }
}
