package com.mercadopago.android.px.internal.features.pay_button

import android.content.Intent
import com.mercadopago.android.px.internal.features.explode.ExplodingFragment
import com.mercadopago.android.px.internal.features.one_tap.RenderMode
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.internal.viewmodel.PostPaymentAction
import com.mercadopago.android.px.model.PaymentRecovery
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.PaymentConfiguration

internal interface PayButton {

    interface View : ExplodingFragment.Handler {
        fun isExploding(): Boolean
        fun stimulate()
        fun enable()
        fun disable()
    }

    interface ViewModel {
        fun attach(handler: Handler)
        fun detach()
        fun onButtonPressed()
        fun preparePayment()
        fun handleBiometricsResult(isSuccess: Boolean, securityRequested: Boolean)
        fun startPayment()
        fun hasFinishPaymentAnimation()
        fun recoverPayment()
        fun onRecoverPaymentEscInvalid(recovery: PaymentRecovery)
        fun onPostPayment(paymentModel: PaymentModel)
        fun onPostPaymentAction(postPaymentAction: PostPaymentAction)
        fun skipRevealAnimation(): Boolean
        fun handleCongratsResult(resultCode: Int, data: Intent?)
        fun handleSecurityCodeResult(resultCode: Int, data: Intent?)
        fun onResultIconAnimation()
    }

    interface Handler {
        fun getViewTrackPath(callback: ViewTrackPathCallback)
        fun prePayment(callback: OnReadyForPaymentCallback)
        @JvmDefault fun enqueueOnExploding(callback: OnEnqueueResolvedCallback) = callback.success()
        @JvmDefault fun onPostPaymentAction(postPaymentAction: PostPaymentAction) = Unit
        @JvmDefault fun onCvvRequested() = CvvRequestedModel()
        @JvmDefault fun onPaymentExecuted(configuration: PaymentConfiguration) = Unit
        @JvmDefault fun onPaymentFinished(paymentModel: PaymentModel, callback: OnPaymentFinishedCallback) = callback.call()
        @JvmDefault fun onPaymentError(error: MercadoPagoError) = Unit
        @JvmDefault fun onPostCongrats(resultCode: Int, data: Intent?) = Unit
    }

    interface ViewTrackPathCallback {
        fun call(viewTrackPath: String)
    }

    interface OnReadyForPaymentCallback {
        fun call(paymentConfiguration: PaymentConfiguration)
    }

    interface OnEnqueueResolvedCallback {
        fun success()
        fun failure()
    }

    interface OnPaymentFinishedCallback {
        fun call()
    }

    interface StateChange {
        fun overrideStateChange(uiState: State): Boolean
    }

    data class CvvRequestedModel(val fragmentContainer: Int = 0, val renderMode: RenderMode = RenderMode.DYNAMIC)

    enum class State {
        IN_PROGRESS,
        ENABLE,
        DISABLE
    }
}
