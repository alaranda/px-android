package com.mercadopago.android.px.internal.features.payment_congrats

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mercadopago.android.px.internal.core.ConnectionHelper
import com.mercadopago.android.px.internal.features.checkout.PostPaymentUrlsMapper
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModel
import com.mercadopago.android.px.internal.repository.CongratsRepository
import com.mercadopago.android.px.internal.repository.PaymentRepository
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.model.Sites
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class CongratsViewModelTest {

    private lateinit var congratsViewModel: CongratsViewModel

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var state: CongratsViewModel.State
    @Mock
    private lateinit var congratsRepository: CongratsRepository
    @Mock
    private lateinit var paymentRepository: PaymentRepository
    @Mock
    private lateinit var congratsResultFactory: CongratsResultFactory
    @Mock
    private lateinit var connectionHelper: ConnectionHelper
    @Mock
    private lateinit var congratsResultLiveData: Observer<CongratsResult>
    @Mock
    private lateinit var paymentSettingRepository: PaymentSettingRepository
    @Mock
    private lateinit var postPaymentUrlsMapper: PostPaymentUrlsMapper

    @Before
    fun setUp() {
        congratsViewModel = CongratsViewModel(
            congratsRepository,
            paymentRepository,
            congratsResultFactory,
            connectionHelper,
            paymentSettingRepository,
            postPaymentUrlsMapper,
            mock()
        )

        congratsViewModel.congratsResultLiveData.observeForever(congratsResultLiveData)
        congratsViewModel.restoreState(state)
    }

    @Test
    fun `When init CongratsViewModel then State IPaymentDescriptor is null`() {
        congratsViewModel.initState()

        assertNull(state.iPaymentDescriptor)
    }

    @Test
    fun `When createCongratsResult and there is no connectivity then show ConnectionError`() {
        whenever(connectionHelper.hasConnection()).thenReturn(false)

        congratsViewModel.createCongratsResult(mock())

        verify(congratsResultLiveData).onChanged(CongratsPostPaymentResult.Loading)
        verify(congratsResultLiveData).onChanged(CongratsPostPaymentResult.ConnectionError)
    }

    @Test
    fun `When createCongratsResult there is connectivity and IPaymentDescriptor is null`() {
        whenever(connectionHelper.hasConnection()).thenReturn(true)

        congratsViewModel.createCongratsResult(null)

        verify(congratsResultLiveData).onChanged(CongratsPostPaymentResult.Loading)
        verify(congratsResultLiveData).onChanged(CongratsPostPaymentResult.BusinessError)
    }

    @Test
    fun `When createCongratsResult there is connectivity and IPaymentDescriptor is not null of type PaymentResult with a PaymentModel`() {
        whenever(connectionHelper.hasConnection()).thenReturn(true)
        val paymentModel = mock<PaymentModel>{
            on { payment }.thenReturn(mock())
        }
        whenever(paymentModel.payment?.let { paymentRepository.createPaymentResult(it) }).thenReturn(mock())
        whenever(congratsResultFactory.create(paymentModel, null)).thenReturn(CongratsResult.PaymentResult(paymentModel))

        congratsViewModel.createCongratsResult(paymentModel.payment)

        verify(congratsResultLiveData).onChanged(CongratsPostPaymentResult.Loading)

        congratsViewModel.handleResult(paymentModel)

        verify(congratsResultLiveData).onChanged(CongratsResult.PaymentResult(paymentModel))
    }

    @Test
    fun `When createCongratsResult there is connectivity and IPaymentDescriptor is not null of type BusinessPaymentResult with a BusinessPaymentModel`() {
        whenever(connectionHelper.hasConnection()).thenReturn(true)
        val businessModel = mock<BusinessPaymentModel>{
            on { payment }.thenReturn(mock())
        }
        val paymentCongratsModel = mock<PaymentCongratsModel>{}
        whenever(paymentRepository.createPaymentResult(businessModel.payment)).thenReturn(mock())
        whenever(congratsResultFactory.create(businessModel, null))
            .thenReturn(CongratsResult.BusinessPaymentResult(paymentCongratsModel))

        congratsViewModel.createCongratsResult(businessModel.payment)

        verify(congratsResultLiveData).onChanged(CongratsPostPaymentResult.Loading)

        congratsViewModel.handleResult(businessModel)

        verify(congratsResultLiveData).onChanged(CongratsResult.BusinessPaymentResult(paymentCongratsModel))
    }

    @Test
    fun `When createCongratsResult there is connectivity and IPaymentDescriptor is not null and redirectUrl is not null`() {
        val redirectUrl = "www.google.com"
        whenever(connectionHelper.hasConnection()).thenReturn(true)
        val paymentModel = mock<PaymentModel>{
            on { payment }.thenReturn(mock())
            on { congratsResponse }.thenReturn(mock())
        }
        whenever(paymentSettingRepository.site).thenReturn(Sites.ARGENTINA)
        whenever(paymentSettingRepository.checkoutPreference).thenReturn(mock())
        whenever(postPaymentUrlsMapper.map(any<PostPaymentUrlsMapper.Model>()))
            .thenReturn(PostPaymentUrlsMapper.Response(redirectUrl, null))
        whenever(paymentModel.payment?.let { paymentRepository.createPaymentResult(it) }).thenReturn(mock())
        whenever(congratsResultFactory.create(paymentModel, redirectUrl))
            .thenReturn(CongratsPaymentResult.SkipCongratsResult(paymentModel))
        whenever(congratsViewModel.state.redirectUrl).thenReturn(redirectUrl)

        congratsViewModel.createCongratsResult(paymentModel.payment)

        verify(congratsResultLiveData).onChanged(CongratsPostPaymentResult.Loading)

        congratsViewModel.handleResult(paymentModel)

        verify(congratsResultLiveData).onChanged(CongratsPaymentResult.SkipCongratsResult(paymentModel))
    }
}
