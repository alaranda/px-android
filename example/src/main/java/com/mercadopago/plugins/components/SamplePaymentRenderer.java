package com.mercadopago.plugins.components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.components.Renderer;
import com.mercadopago.examples.R;

/**
 * Created by nfortuna on 12/13/17.
 */

public class SamplePaymentRenderer extends Renderer<SamplePayment> {

    @Override
    public View render(final SamplePayment component, final Context context, final ViewGroup parent) {
        return inflate(R.layout.mpsdk_sample_payment_procesor, parent);
    }
}