package com.mercadopago.android.px.internal.features.payment_congrats

import com.mercadopago.android.px.internal.base.BaseState
import com.mercadopago.android.px.internal.base.BaseViewModelWithState
import com.mercadopago.android.px.internal.core.ConnectionHelper
import com.mercadopago.android.px.internal.livedata.MediatorSingleLiveData
import com.mercadopago.android.px.internal.repository.CongratsRepository
import com.mercadopago.android.px.internal.repository.PaymentRepository
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.model.IPaymentDescriptor
import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.tracking.internal.events.NoConnectionFrictionTracker
import kotlinx.android.parcel.Parcelize

internal class CongratsViewModel(
    private val congratsRepository: CongratsRepository,
    private val paymentRepository: PaymentRepository,
    private val congratsResultFactory: CongratsResultFactory,
    private val connectionHelper: ConnectionHelper,
    tracker: MPTracker
) : BaseViewModelWithState<CongratsViewModel.State>(tracker), CongratsRepository.PostPaymentCallback {

    val congratsResultLiveData = MediatorSingleLiveData<CongratsResult>()

    override fun initState() = State()

    fun createCongratsResult(iPaymentDescriptor: IPaymentDescriptor?) {
        congratsResultLiveData.value = CongratsPostPaymentResult.Loading

        if (connectionHelper.hasConnection()) {
            val descriptor = iPaymentDescriptor ?: paymentRepository.payment
            if (descriptor != null) {
                val paymentResult = paymentRepository.createPaymentResult(descriptor)
                congratsRepository.getPostPaymentData(descriptor, paymentResult, this@CongratsViewModel)
            } else {
                congratsResultLiveData.value = CongratsPostPaymentResult.BusinessError
            }
        } else {
            manageNoConnection()
        }
    }

    override fun handleResult(paymentModel: PaymentModel) {
        congratsResultLiveData.value = congratsResultFactory.create(paymentModel)
    }

    private fun manageNoConnection() {
        track(NoConnectionFrictionTracker)
        congratsResultLiveData.value = CongratsPostPaymentResult.ConnectionError
    }

    @Parcelize
    data class State(
        var iPaymentDescriptor: IPaymentDescriptor? = null
    ) : BaseState
}
