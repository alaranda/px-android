package com.mercadopago.android.px.internal.di

import android.content.Context
import com.mercadopago.android.px.R
import com.mercadopago.android.px.addons.BehaviourProvider
import com.mercadopago.android.px.core.MercadoPagoCheckout
import com.mercadopago.android.px.internal.datasource.mapper.FromPayerPaymentMethodToCardMapper
import com.mercadopago.android.px.internal.features.FeatureProviderImpl
import com.mercadopago.android.px.internal.features.checkout.PostPaymentUrlsMapper
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModelMapper
import com.mercadopago.android.px.internal.features.payment_result.instruction.mapper.*
import com.mercadopago.android.px.internal.features.payment_result.mappers.PaymentResultViewModelMapper
import com.mercadopago.android.px.internal.features.payment_result.remedies.AlternativePayerPaymentMethodsMapper
import com.mercadopago.android.px.internal.features.payment_result.remedies.RemediesLinkableMapper
import com.mercadopago.android.px.internal.features.security_code.RenderModeMapper
import com.mercadopago.android.px.internal.features.security_code.mapper.BusinessSecurityCodeDisplayDataMapper
import com.mercadopago.android.px.internal.mappers.*
import com.mercadopago.android.px.internal.view.SummaryDetailDescriptorMapper
import com.mercadopago.android.px.internal.viewmodel.drawables.PaymentMethodDrawableItemMapper
import com.mercadopago.android.px.tracking.internal.mapper.FromApplicationToApplicationInfo

internal object MapperProvider {
    fun getPaymentMethodDrawableItemMapper(): PaymentMethodDrawableItemMapper {
        val session = Session.getInstance()
        return PaymentMethodDrawableItemMapper(
            session.configurationModule.chargeRepository,
            session.configurationModule.disabledPaymentMethodRepository,
            session.configurationModule.applicationSelectionRepository,
            CardUiMapper,
            CardDrawerCustomViewModelMapper,
            session.payerPaymentMethodRepository,
            session.modalRepository
        )
    }

    fun getPaymentMethodDescriptorMapper(): PaymentMethodDescriptorMapper {
        val session = Session.getInstance()
        return PaymentMethodDescriptorMapper(
            session.configurationModule.paymentSettings,
            session.amountConfigurationRepository,
            session.configurationModule.disabledPaymentMethodRepository,
            session.configurationModule.applicationSelectionRepository,
            session.amountRepository
        )
    }

    fun getRenderModeMapper(context: Context): RenderModeMapper {
        with(context.resources) {
            return RenderModeMapper(configuration.screenHeightDp, getString(R.string.px_render_mode))
        }
    }

    fun getPaymentCongratsMapper(): PaymentCongratsModelMapper {
        return PaymentCongratsModelMapper(
            Session.getInstance().configurationModule.paymentSettings,
            Session.getInstance().configurationModule.trackingRepository,
            Session.getInstance().helperModule.displayInfoHelper
        )
    }

    fun getPostPaymentUrlsMapper() = PostPaymentUrlsMapper

    fun getAlternativePayerPaymentMethodsMapper(): AlternativePayerPaymentMethodsMapper {
        return AlternativePayerPaymentMethodsMapper(
            Session.getInstance().oneTapItemRepository,
            Session.getInstance().mercadoPagoESC
        )
    }

    fun getFromPayerPaymentMethodToCardMapper(): FromPayerPaymentMethodToCardMapper {
        return FromPayerPaymentMethodToCardMapper(
            Session.getInstance().oneTapItemRepository,
            Session.getInstance().payerPaymentMethodRepository,
            Session.getInstance().paymentMethodRepository
        )
    }

    fun getPaymentMethodMapper(): PaymentMethodMapper {
        return PaymentMethodMapper(Session.getInstance().paymentMethodRepository)
    }

    fun getSummaryInfoMapper(): SummaryInfoMapper {
        return SummaryInfoMapper()
    }

    fun getElementDescriptorMapper(): ElementDescriptorMapper {
        return ElementDescriptorMapper()
    }

    fun getSummaryDetailDescriptorMapper(): SummaryDetailDescriptorMapper {
        val session = Session.getInstance()
        val paymentSettings = session.configurationModule.paymentSettings
        return SummaryDetailDescriptorMapper(
            session.amountRepository,
            getSummaryInfoMapper().map(paymentSettings.checkoutPreference!!),
            FactoryProvider.amountDescriptorViewModelFactory
        )
    }

    val customChargeToPaymentTypeChargeMapper: CustomChargeToPaymentTypeChargeMapper
        get() = CustomChargeToPaymentTypeChargeMapper(
            Session.getInstance().configurationModule.paymentSettings.paymentConfiguration
        )

    fun getInitRequestBodyMapper(checkout: MercadoPagoCheckout): InitRequestBodyMapper {
        val session = Session.getInstance()
        val featureProvider = FeatureProviderImpl(checkout, BehaviourProvider.getTokenDeviceBehaviour())
        return InitRequestBodyMapper(
            session.mercadoPagoESC,
            featureProvider,
            session.configurationModule.trackingRepository
        )
    }

    fun getInitRequestBodyMapper(): InitRequestBodyMapper {
        val session = Session.getInstance()
        val featureProvider = FeatureProviderImpl(
            session.configurationModule.paymentSettings,
            BehaviourProvider.getTokenDeviceBehaviour()
        )
        return InitRequestBodyMapper(
            session.mercadoPagoESC,
            featureProvider,
            session.configurationModule.trackingRepository
        )
    }

    val oneTapItemToDisabledPaymentMethodMapper: OneTapItemToDisabledPaymentMethodMapper
        get() = OneTapItemToDisabledPaymentMethodMapper()

    val paymentResultViewModelMapper: PaymentResultViewModelMapper
        get() {
            val session = Session.getInstance()
            val paymentSettings = session.configurationModule.paymentSettings
            return PaymentResultViewModelMapper(
                paymentSettings.advancedConfiguration.paymentResultScreenConfiguration,
                session.paymentResultViewModelFactory,
                session.tracker,
                instructionMapper,
                paymentSettings.checkoutPreference?.autoReturn,
                session.helperModule.displayInfoHelper)
        }

    val instructionMapper: InstructionMapper
        get() = InstructionMapper(
            instructionInfoMapper, instructionInteractionMapper, instructionReferenceMapper, instructionActionMapper
        )

    val instructionInfoMapper: InstructionInfoMapper
        get() = InstructionInfoMapper()

    val instructionActionMapper: InstructionActionMapper
        get() = InstructionActionMapper()

    val instructionInteractionMapper: InstructionInteractionMapper
        get() = InstructionInteractionMapper(instructionActionMapper)

    val instructionReferenceMapper: InstructionReferenceMapper
        get() = InstructionReferenceMapper()

    val fromApplicationToApplicationInfo: FromApplicationToApplicationInfo
        get() = FromApplicationToApplicationInfo()

    val fromSecurityCodeDisplayDataToBusinessSecurityCodeDisplayData: BusinessSecurityCodeDisplayDataMapper
        get() = BusinessSecurityCodeDisplayDataMapper()

    val remediesLinkableMapper: RemediesLinkableMapper
        get() = RemediesLinkableMapper(Session.getInstance().applicationContext)
}
