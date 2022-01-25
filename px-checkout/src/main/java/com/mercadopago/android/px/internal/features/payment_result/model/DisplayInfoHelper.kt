package com.mercadopago.android.px.internal.features.payment_result.model

import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsText
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentInfo
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentResultInfo
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository
import com.mercadopago.android.px.internal.repository.UserSelectionRepository
import com.mercadopago.android.px.model.PaymentData
import com.mercadopago.android.px.model.display_info.BankTransferDisplayInfo
import com.mercadopago.android.px.model.display_info.DisplayInfo
import com.mercadopago.android.px.model.internal.Text

internal class DisplayInfoHelper(
    val payerPaymentMethodRepository: PayerPaymentMethodRepository,
    val userSelectionRepository: UserSelectionRepository
) {

    fun resolve(paymentData: PaymentData, paymentInfoBuilder: PaymentInfo.Builder) {
        val payerPaymentMethod = userSelectionRepository.customOptionId?.let { payerPaymentMethodRepository[it] }
        when {
            payerPaymentMethod?.bankTransferDisplayInfo != null -> resolveBankTransferDisplayInfo(payerPaymentMethod.bankTransferDisplayInfo, paymentInfoBuilder)
            else -> resolveGenericDisplayInfo(paymentData.paymentMethod.displayInfo, paymentInfoBuilder)
        }
    }

    private fun resolveGenericDisplayInfo(
        displayInfo: DisplayInfo?,
        paymentInfoBuilder: PaymentInfo.Builder
    ) {
        displayInfo?.let {
            val paymentResultInfo = PaymentResultInfo(it.resultInfo.title, it.resultInfo.subtitle)
            paymentInfoBuilder.withConsumerCreditsInfo(paymentResultInfo)
            val description = it.description
            if (description != null) {
                val descriptionText = PaymentCongratsText(description.message, description.backgroundColor, description.textColor, description.weight)
                paymentInfoBuilder.withPaymentMethodDescriptionText(descriptionText)
            }
        }
    }

    private fun resolveBankTransferDisplayInfo(
        bankTransferDisplayInfo: BankTransferDisplayInfo?,
        paymentInfoBuilder: PaymentInfo.Builder
    ) {
        bankTransferDisplayInfo?.let {
            with(it.result.paymentMethod) {
                paymentInfoBuilder
                    .withDescriptionText(buildPaymentCongratsText(detail[0]))
                    .withPaymentMethodDescriptionText(buildPaymentCongratsText(detail[1]))
                    .withStatementText(buildPaymentCongratsText(detail[2]))
                    .withIconUrl(iconUrl)
            }
        }
    }

    private fun buildPaymentCongratsText(text: Text): PaymentCongratsText {
        return PaymentCongratsText(text.message, text.backgroundColor, text.textColor, text.weight)
    }
}
