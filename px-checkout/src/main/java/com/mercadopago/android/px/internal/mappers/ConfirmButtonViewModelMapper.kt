package com.mercadopago.android.px.internal.mappers

import com.mercadopago.android.px.internal.datasource.CustomOptionIdSolver
import com.mercadopago.android.px.internal.repository.DisabledPaymentMethodRepository
import com.mercadopago.android.px.internal.viewmodel.ConfirmButtonViewModel
import com.mercadopago.android.px.model.internal.OneTapItem
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodKey as Key
import com.mercadopago.android.px.internal.viewmodel.ConfirmButtonViewModel.ByApplication as ModelByApplication

internal class ConfirmButtonViewModelMapper(
    private val disabledPaymentMethodRepository: DisabledPaymentMethodRepository)
    : Mapper<OneTapItem, ModelByApplication>() {

    override fun map(value: OneTapItem): ModelByApplication {
        return ModelByApplication().also { model ->
            value.getApplications().forEach { application ->
                model[application] = ConfirmButtonViewModel(
                    value.isNewCard || value.isOfflineMethods ||
                        disabledPaymentMethodRepository.hasKey(
                            Key(CustomOptionIdSolver.getByApplication(value, application), application.paymentMethod.type)))
            }
        }
    }

}