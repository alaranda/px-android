package com.mercadopago.android.px.internal.features.pay_button

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.configuration.AdvancedConfiguration
import com.mercadopago.android.px.internal.audio.AudioPlayer
import com.mercadopago.android.px.internal.audio.PlaySoundUseCase
import com.mercadopago.android.px.internal.core.ConnectionHelper
import com.mercadopago.android.px.internal.core.ProductIdProvider
import com.mercadopago.android.px.internal.features.PaymentResultViewModelFactory
import com.mercadopago.android.px.internal.features.checkout.PostPaymentUrlsMapper
import com.mercadopago.android.px.internal.features.explode.ExplodeDecorator
import com.mercadopago.android.px.internal.features.one_tap.RenderMode
import com.mercadopago.android.px.internal.features.payment_congrats.CongratsPaymentResult
import com.mercadopago.android.px.internal.features.payment_congrats.CongratsResult
import com.mercadopago.android.px.internal.features.payment_congrats.CongratsResultFactory
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModel
import com.mercadopago.android.px.internal.features.payment_result.remedies.RemediesModel
import com.mercadopago.android.px.internal.features.security_code.RenderModeMapper
import com.mercadopago.android.px.internal.features.security_code.model.SecurityCodeParams
import com.mercadopago.android.px.internal.livedata.MutableSingleLiveData
import com.mercadopago.android.px.internal.mappers.PayButtonViewModelMapper
import com.mercadopago.android.px.internal.model.SecurityType
import com.mercadopago.android.px.internal.repository.CustomTextsRepository
import com.mercadopago.android.px.internal.repository.PaymentRepository
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.internal.viewmodel.PaymentResultType
import com.mercadopago.android.px.model.BusinessPayment
import com.mercadopago.android.px.model.Card
import com.mercadopago.android.px.model.IPaymentDescriptor
import com.mercadopago.android.px.model.Payment
import com.mercadopago.android.px.model.PaymentData
import com.mercadopago.android.px.model.PaymentRecovery
import com.mercadopago.android.px.model.Sites
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.CustomTexts
import com.mercadopago.android.px.model.internal.PaymentConfiguration
import com.mercadopago.android.px.tracking.internal.model.Reason
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.internal.matchers.apachecommons.ReflectionEquals
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import com.mercadopago.android.px.internal.viewmodel.PayButtonViewModel as PayButtonTexts

@RunWith(MockitoJUnitRunner::class)
internal class PayButtonViewModelTest {

    private lateinit var payButtonViewModel: PayButtonViewModel

    @Mock
    private lateinit var congratsResultFactory: CongratsResultFactory
    @Mock
    private lateinit var paymentService: PaymentRepository
    @Mock
    private lateinit var productIdProvider: ProductIdProvider
    @Mock
    private lateinit var connectionHelper: ConnectionHelper
    @Mock
    private lateinit var paymentSettingRepository: PaymentSettingRepository
    @Mock
    private lateinit var customTextsRepository: CustomTextsRepository
    @Mock
    private lateinit var payButtonViewModelMapper: PayButtonViewModelMapper
    @Mock
    private lateinit var customTexts: CustomTexts
    @Mock
    private lateinit var payButtonTexts: PayButtonTexts
    @Mock
    private lateinit var handler: PayButton.Handler
    @Mock
    private lateinit var buttonTextObserver: Observer<PayButtonTexts>
    @Mock
    private lateinit var uiStateObserver: Observer<PayButtonUiState>
    @Mock
    private lateinit var congratsStateObserver: Observer<CongratsResult>
    @Mock
    private lateinit var cvvRequiredObserver: Observer<SecurityCodeParams>
    @Mock
    private lateinit var paymentResultViewModelFactory: PaymentResultViewModelFactory
    @Mock
    private lateinit var state: PayButtonViewModel.State
    @Mock
    private lateinit var postPaymentUrlsMapper: PostPaymentUrlsMapper
    @Mock
    private lateinit var renderModeMapper: RenderModeMapper
    @Mock
    private lateinit var playSoundUseCase: PlaySoundUseCase
    @Mock
    private lateinit var advancedConfiguration: AdvancedConfiguration

    private val paymentErrorLiveData = MutableSingleLiveData<MercadoPagoError>()
    private val postPaymentStartedLiveData = MutableSingleLiveData<IPaymentDescriptor>()
    private val paymentFinishedLiveData = MutableSingleLiveData<PaymentModel>()
    private val requireCvvLiveData = MutableSingleLiveData<Pair<Card,Reason>>()
    private val recoverInvalidEscLiveData = MutableSingleLiveData<PaymentRecovery>()
    private val visualPaymentLiveData = MutableSingleLiveData<Unit>()

    private val redirectUrl = "redirect_url"

    /*
    * https://stackoverflow.com/questions/29945087/kotlin-and-new-activitytestrule-the-rule-must-be-public
    * */
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        whenever(customTextsRepository.customTexts).thenReturn(customTexts)
        whenever(payButtonViewModelMapper.map(customTexts)).thenReturn(payButtonTexts)
        whenever(connectionHelper.hasConnection()).thenReturn(true)
        whenever(paymentSettingRepository.checkoutPreference).thenReturn(mock())
        whenever(paymentSettingRepository.site).thenReturn(Sites.ARGENTINA)
        whenever(renderModeMapper.map(any<RenderMode>())).thenReturn(mock())
        whenever(paymentSettingRepository.advancedConfiguration).thenReturn(advancedConfiguration)

        configurePaymentSettingServiceObservableEvents()

        payButtonViewModel = PayButtonViewModel(
            congratsResultFactory,
            paymentService,
            productIdProvider,
            connectionHelper,
            paymentSettingRepository,
            customTextsRepository,
            payButtonViewModelMapper,
            postPaymentUrlsMapper,
            renderModeMapper,
            playSoundUseCase,
            paymentResultViewModelFactory,
            mock())

        payButtonViewModel.stateUILiveData.observeForever(uiStateObserver)
        payButtonViewModel.stateUILiveData.observeForever(uiStateObserver)
        payButtonViewModel.congratsResultLiveData.observeForever(congratsStateObserver)
        payButtonViewModel.buttonTextLiveData.observeForever(buttonTextObserver)
        payButtonViewModel.cvvRequiredLiveData.observeForever(cvvRequiredObserver)
        payButtonViewModel.attach(handler)

        whenever(state.paymentConfiguration).thenReturn(mock())
        payButtonViewModel.restoreState(state)

        verify(buttonTextObserver).onChanged(any())
        assertNotNull(handler)
    }

    @Test
    fun preparePaymentWhenNonConnection() {
        whenever(connectionHelper.hasConnection()).thenReturn(false)
        payButtonViewModel.preparePayment()
        verify(uiStateObserver).onChanged(any<UIError.ConnectionError>())
    }

    @Test
    fun preparePaymentWhenHasConnection() {
        val callback = argumentCaptor<PayButton.OnReadyForPaymentCallback>()
        payButtonViewModel.preparePayment()
        verify(handler).prePayment(callback.capture())
        callback.firstValue.call(mock())
        verify(uiStateObserver).onChanged(any<UIProgress.FingerprintRequired>())
    }

    @Test
    fun handleBiometricsResultWithoutFrictionAndSuccess() {
        val callback = argumentCaptor<PayButton.OnEnqueueResolvedCallback>()

        payButtonViewModel.handleBiometricsResult(isSuccess = true, securityRequested = true)

        verify(uiStateObserver).onChanged(any<UIProgress.ButtonLoadingStarted>())
        verify(paymentSettingRepository).configure(SecurityType.SECOND_FACTOR)
        verify(handler).enqueueOnExploding(callback.capture())
        callback.firstValue.success()
        verify(paymentService).startExpressPayment(any())
    }

    @Test
    fun handleBiometricsResultWithoutFrictionAndFailure() {
        val callback = argumentCaptor<PayButton.OnEnqueueResolvedCallback>()

        payButtonViewModel.handleBiometricsResult(isSuccess = true, securityRequested = true)

        verify(paymentSettingRepository).configure(SecurityType.SECOND_FACTOR)
        verify(handler).enqueueOnExploding(callback.capture())
        callback.firstValue.failure()
        verify(uiStateObserver).onChanged(any<UIProgress.ButtonLoadingCanceled>())
    }

    @Test
    fun startPaymentAndObserveServiceWhenIsErrorEventAndIsPaymentProcessingError() {
        val callback = argumentCaptor<PayButton.OnEnqueueResolvedCallback>()
        val error = mock<MercadoPagoError> {
            on { isPaymentProcessing }.thenReturn(true)
        }
        val payButtonViewModelSpy = spy(payButtonViewModel)
        whenever(paymentSettingRepository.currency).thenReturn(mock())
        whenever(paymentService.paymentDataList).thenReturn(mock() as MutableList<PaymentData>)
        whenever(paymentResultViewModelFactory.createPaymentResultDecorator(any())).thenReturn(mock())

        payButtonViewModelSpy.startPayment()
        paymentErrorLiveData.value = error

        verify(handler).enqueueOnExploding(callback.capture())
        callback.firstValue.success()
        verify(payButtonViewModelSpy).onPostPayment(any())
        verify(uiStateObserver).onChanged(any<UIProgress.ButtonLoadingFinished>())
        verify(handler).onPaymentError(error)
        verify(uiStateObserver).onChanged(any<UIProgress.ButtonLoadingCanceled>())
    }

    @Test
    fun startPaymentAndObserveServiceWhenIsErrorEventAndIsNoRecoverableError() {
        val callback = argumentCaptor<PayButton.OnEnqueueResolvedCallback>()
        val error = mock<MercadoPagoError> {
            on { isPaymentProcessing }.thenReturn(false)
        }

        payButtonViewModel.startPayment()
        paymentErrorLiveData.value = error

        verify(handler).enqueueOnExploding(callback.capture())
        callback.firstValue.success()
        verify(uiStateObserver).onChanged(any<UIError.BusinessError>())
        verify(handler).onPaymentError(error)
        verify(uiStateObserver).onChanged(any<UIProgress.ButtonLoadingCanceled>())
    }

    @Test
    fun startPaymentAndObserveServiceWhenIsVisualPaymentEvent() {
        val callback = argumentCaptor<PayButton.OnEnqueueResolvedCallback>()

        payButtonViewModel.startPayment()
        visualPaymentLiveData.value = Unit

        verify(handler).enqueueOnExploding(callback.capture())
        callback.firstValue.success()
        verify(uiStateObserver).onChanged(any<UIResult.VisualProcessorResult>())
    }

    @Test
    fun startPaymentAndObserveServiceWhenIsPaymentFinishedEventAndIsRemedies() {
        val callback = argumentCaptor<PayButton.OnEnqueueResolvedCallback>()
        val paymentModel = mock<PaymentModel> {
            on { remedies }.thenReturn(mock())
            on { remedies.hasRemedies() }.thenReturn(true)
        }

        payButtonViewModel.startPayment()
        paymentFinishedLiveData.value = paymentModel

        verify(handler).enqueueOnExploding(callback.capture())
        callback.firstValue.success()
        verify(uiStateObserver).onChanged(any<UIProgress.ButtonLoadingFinished>())

        val actual = (payButtonViewModel.stateUILiveData.value as UIProgress.ButtonLoadingFinished)
        assertTrue(ReflectionEquals(actual.explodeDecorator).matches(ExplodeDecorator.from(RemediesModel.DECORATOR)))
    }

    @Test
    fun startPaymentAndObserveServiceWhenIsPostPaymentStartedEvent() {
        val callback = argumentCaptor<PayButton.OnEnqueueResolvedCallback>()

        payButtonViewModel.startPayment()
        postPaymentStartedLiveData.value = mock()

        verify(handler).enqueueOnExploding(callback.capture())
        callback.firstValue.success()
        verify(uiStateObserver).onChanged(any<UIProgress.ButtonLoadingFinished>())
    }

    @Test
    fun onResultIconAnimationAndPaymentSuccessThenPlayAudio() {
        val paymentModel = mock<PaymentModel> {
            on { paymentResult }.thenReturn(mock())
            on { paymentResult.isApproved }.thenReturn(true)
        }
        whenever(state.paymentModel).thenReturn(paymentModel)

        payButtonViewModel.onResultIconAnimation()
        verify(playSoundUseCase).execute(AudioPlayer.Sound.SUCCESS)
    }

    @Test
    fun onResultIconAnimationAndPaymentRejectedThenPlayAudio() {
        val paymentModel = mock<PaymentModel> {
            on { paymentResult }.thenReturn(mock())
            on { paymentResult.isRejected }.thenReturn(true)
        }
        whenever(state.paymentModel).thenReturn(paymentModel)

        payButtonViewModel.onResultIconAnimation()

        verify(playSoundUseCase).execute(AudioPlayer.Sound.FAILURE)
    }

    @Test
    fun startPaymentAndObserveServiceWhenIsPaymentFinishedEventAndIsBusiness() {
        val callback = argumentCaptor<PayButton.OnEnqueueResolvedCallback>()
        val decorator = BusinessPayment.Decorator.APPROVED
        val payment = mock<BusinessPayment> {
            on { this.decorator }.thenReturn(decorator)
        }
        val paymentModel = mock<BusinessPaymentModel> {
            on { this.remedies }.thenReturn(mock())
            on { this.payment }.thenReturn(payment)
        }

        payButtonViewModel.startPayment()
        paymentFinishedLiveData.value = paymentModel

        verify(handler).enqueueOnExploding(callback.capture())
        callback.firstValue.success()
        verify(uiStateObserver).onChanged(any<UIProgress.ButtonLoadingFinished>())

        val actual = payButtonViewModel.stateUILiveData.value as UIProgress.ButtonLoadingFinished
        assertTrue(ReflectionEquals(actual.explodeDecorator).matches(ExplodeDecorator.from(PaymentResultType.from(decorator))))
    }

    @Test
    fun startPaymentAndObserveServiceWhenIsPaymentFinishedEventAndIsPaymentResult() {
        val callback = argumentCaptor<PayButton.OnEnqueueResolvedCallback>()
        val paymentModel = mock<PaymentModel> {
            on { paymentResult }.thenReturn(mock())
            on { remedies }.thenReturn(mock())
            on { remedies.hasRemedies() }.thenReturn(false)
        }
        whenever(paymentResultViewModelFactory.createPaymentResultDecorator(any())).thenReturn(mock())

        payButtonViewModel.startPayment()
        paymentFinishedLiveData.value = paymentModel

        verify(handler).enqueueOnExploding(callback.capture())
        callback.firstValue.success()
        verify(uiStateObserver).onChanged(any<UIProgress.ButtonLoadingFinished>())
    }

    @Test
    fun startPaymentAndObserveServiceWhenIsPaymentCvvRequiredEvent() {
        val callback = argumentCaptor<PayButton.OnEnqueueResolvedCallback>()
        val paymentConfigurationCaptor = argumentCaptor<PaymentConfiguration>()
        val onChangeCaptor = argumentCaptor<SecurityCodeParams>()
        val reason = mock<Reason>()
        val card = mock<Card>()
        val cvvRequested = mock<PayButton.CvvRequestedModel> {
            on { renderMode }.thenReturn(mock())
        }
        whenever(handler.onCvvRequested()).thenReturn(cvvRequested)

        payButtonViewModel.startPayment()
        requireCvvLiveData.value = Pair(card,reason)

        verify(handler).enqueueOnExploding(callback.capture())
        callback.firstValue.success()
        verify(paymentService).startExpressPayment(paymentConfigurationCaptor.capture())
        verify(handler).onCvvRequested()
        verify(cvvRequiredObserver).onChanged(onChangeCaptor.capture())
        val actualResult = onChangeCaptor.firstValue
        assertTrue(ReflectionEquals(actualResult.paymentConfiguration).matches(paymentConfigurationCaptor.firstValue))
        assertTrue(ReflectionEquals(actualResult.reason).matches(reason))
    }

    @Test
    fun startPaymentAndObserveServiceWhenIsInvalidEscEventAndShouldAskForCvv() {
        val callback = argumentCaptor<PayButton.OnEnqueueResolvedCallback>()
        val paymentConfigurationCaptor = argumentCaptor<PaymentConfiguration>()
        val onChangeCaptor = argumentCaptor<SecurityCodeParams>()
        val paymentRecovery = mock<PaymentRecovery> {
            on { shouldAskForCvv() }.thenReturn(true)
        }
        val cvvRequestedModel = mock<PayButton.CvvRequestedModel> {
            on { renderMode }.thenReturn(mock())
        }
        whenever(handler.onCvvRequested()).thenReturn(cvvRequestedModel)

        payButtonViewModel.startPayment()
        recoverInvalidEscLiveData.value = paymentRecovery

        verify(handler).enqueueOnExploding(callback.capture())
        callback.firstValue.success()
        verify(paymentService).startExpressPayment(paymentConfigurationCaptor.capture())
        verify(handler).onCvvRequested()
        verify(cvvRequiredObserver).onChanged(onChangeCaptor.capture())
        val actualResult = onChangeCaptor.firstValue
        assertTrue(ReflectionEquals(actualResult.paymentConfiguration).matches(paymentConfigurationCaptor.firstValue))
        assertTrue(ReflectionEquals(actualResult.paymentRecovery).matches(paymentRecovery))
    }

    @Test
    fun onFinishPaymentAnimationWithRegularPaymentThenShowCongrats() {
        val callback = argumentCaptor<PayButton.OnPaymentFinishedCallback>()
        val paymentModel = mock<PaymentModel> {
            on { congratsResponse }.thenReturn(mock())
        }
        whenever(state.paymentModel).thenReturn(paymentModel)
        whenever(postPaymentUrlsMapper.map(any<PostPaymentUrlsMapper.Model>())).thenReturn(mock())
        whenever(congratsResultFactory.create(paymentModel, null)).thenReturn(CongratsResult.PaymentResult(paymentModel))

        payButtonViewModel.hasFinishPaymentAnimation()

        verify(handler).onPaymentFinished(eq(paymentModel), callback.capture())
        callback.firstValue.call()
        verify(congratsStateObserver).onChanged(any<CongratsResult.PaymentResult>())
        val actual = payButtonViewModel.congratsResultLiveData.value as CongratsResult.PaymentResult
        actual.paymentModel.assertEquals(paymentModel)
    }

    @Test
    fun onFinishPaymentAnimationWithRedirectUrlThenSkipCongrats() {
        val callback = argumentCaptor<PayButton.OnPaymentFinishedCallback>()
        val paymentModel = mock<PaymentModel> {
            on { congratsResponse }.thenReturn(mock())
        }
        whenever(postPaymentUrlsMapper.map(any<PostPaymentUrlsMapper.Model>()))
            .thenReturn(PostPaymentUrlsMapper.Response(redirectUrl, null))
        whenever(congratsResultFactory.create(paymentModel, redirectUrl))
            .thenReturn(CongratsPaymentResult.SkipCongratsResult(paymentModel))
        whenever(state.paymentModel).thenReturn(paymentModel)

        payButtonViewModel.hasFinishPaymentAnimation()

        verify(handler).onPaymentFinished(eq(paymentModel), callback.capture())
        callback.firstValue.call()
        verify(congratsStateObserver).onChanged(any<CongratsPaymentResult.SkipCongratsResult>())
        val actual = payButtonViewModel.congratsResultLiveData.value as CongratsPaymentResult.SkipCongratsResult
        actual.paymentModel.assertEquals(paymentModel)
    }

    @Test
    fun onFinishPaymentAnimationWithPaymentModelAsNullThenPostPaymentFlowStarted() {
        val deeplink = "mercadopago://px/congrats"
        val advancedConfiguration = mock<AdvancedConfiguration> {
            on { postPaymentConfiguration }.thenReturn(mock())
            on { postPaymentConfiguration.hasPostPaymentUrl() }.thenReturn(true)
            on { postPaymentConfiguration.postPaymentDeepLinkUrl }.thenReturn(deeplink)
        }

        val state = mock<PayButtonViewModel.State>{
            on { iParcelablePaymentDescriptor }.thenReturn(mock())
        }

        whenever(paymentSettingRepository.advancedConfiguration).thenReturn(advancedConfiguration)

        payButtonViewModel.restoreState(state)

        payButtonViewModel.hasFinishPaymentAnimation()

        verify(uiStateObserver).onChanged(any<UIProgress.PostPaymentFlowStarted>())
    }

    @Test
    fun onFinishPaymentAnimationWithBusinessPaymentThenShowCongrats() {
        val callback = argumentCaptor<PayButton.OnPaymentFinishedCallback>()
        val congratsModel = mock<PaymentCongratsModel>()
        val paymentModel = mock<BusinessPaymentModel> {
            on { congratsResponse }.thenReturn(mock())
        }

        whenever(congratsResultFactory.create(paymentModel, null)).thenReturn(
            CongratsResult.BusinessPaymentResult(congratsModel)
        )
        whenever(state.paymentModel).thenReturn(paymentModel)
        whenever(postPaymentUrlsMapper.map(any<PostPaymentUrlsMapper.Model>())).thenReturn(mock())

        payButtonViewModel.hasFinishPaymentAnimation()

        verify(handler).onPaymentFinished(eq(paymentModel), callback.capture())
        callback.firstValue.call()
        verify(congratsStateObserver).onChanged(any<CongratsResult.BusinessPaymentResult>())
        val actual = payButtonViewModel.congratsResultLiveData.value as CongratsResult.BusinessPaymentResult
        actual.paymentCongratsModel.assertEquals(congratsModel)
    }

    @Test
    fun onSkipRevealAnimationWithPostPaymentDeepLinkUrlNotEmptyAndPaymentIsApprovedThenReturnTrue() {
        val advancedConfiguration = mock<AdvancedConfiguration> {
            on { postPaymentConfiguration }.thenReturn(mock())
            on { postPaymentConfiguration.hasPostPaymentUrl() }.thenReturn(true)
        }

        val state = mock<PayButtonViewModel.State> {
            on { iParcelablePaymentDescriptor }.thenReturn(mock())
            on { iParcelablePaymentDescriptor?.paymentStatus }.thenReturn(Payment.StatusCodes.STATUS_APPROVED)
        }

        whenever(paymentSettingRepository.advancedConfiguration).thenReturn(advancedConfiguration)

        payButtonViewModel.restoreState(state)

        assertTrue(payButtonViewModel.skipRevealAnimation())
    }

    @Test
    fun onSkipRevealAnimationWithPostPaymentDeepLinkUrlEmptyThenReturnsFalse() {
        val advancedConfiguration = mock<AdvancedConfiguration> {
            on { postPaymentConfiguration }.thenReturn(mock())
        }

        whenever(paymentSettingRepository.advancedConfiguration).thenReturn(advancedConfiguration)

        assertFalse(payButtonViewModel.skipRevealAnimation())
    }

    private fun configurePaymentSettingServiceObservableEvents() {
        whenever(paymentService.observableEvents).thenReturn(mock())
        whenever(paymentService.isExplodingAnimationCompatible).thenReturn(true)
        whenever(paymentService.observableEvents?.paymentErrorLiveData).thenReturn(paymentErrorLiveData)
        whenever(paymentService.observableEvents?.postPaymentStartedLiveData).thenReturn(postPaymentStartedLiveData)
        whenever(paymentService.observableEvents?.paymentFinishedLiveData).thenReturn(paymentFinishedLiveData)
        whenever(paymentService.observableEvents?.requireCvvLiveData).thenReturn(requireCvvLiveData)
        whenever(paymentService.observableEvents?.recoverInvalidEscLiveData).thenReturn(recoverInvalidEscLiveData)
        whenever(paymentService.observableEvents?.visualPaymentLiveData).thenReturn(visualPaymentLiveData)
    }
}
