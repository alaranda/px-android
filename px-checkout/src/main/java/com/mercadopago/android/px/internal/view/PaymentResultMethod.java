package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.extensions.ImageViewExtensionsKt;
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsText;
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentInfo;
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentResultInfo;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.model.PaymentTypes;
import java.util.Locale;

public class PaymentResultMethod extends ConstraintLayout {

    private final ImageView icon;
    private final MPTextView description;
    private final MPTextView paymentMethodStatement;
    private final MPTextView statement;
    private final PaymentResultAmount amount;
    private final MPTextView infoTitle;
    private final MPTextView infoSubtitle;

    public PaymentResultMethod(final Context context) {
        this(context, null);
    }

    public PaymentResultMethod(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaymentResultMethod(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.px_payment_result_method, this);
        icon = findViewById(R.id.icon);
        description = findViewById(R.id.description);
        paymentMethodStatement = findViewById(R.id.pm_statement);
        statement = findViewById(R.id.statement);
        amount = findViewById(R.id.amount);
        infoTitle = findViewById(R.id.info_title);
        infoSubtitle = findViewById(R.id.info_subtitle);
    }

    public void setModel(@NonNull final Model model) {
        ImageViewExtensionsKt.loadOrElse(icon, model.imageUrl, R.drawable.px_generic_method);
        ViewUtils.loadOrGone(getDescription(model), description);
        ViewUtils.loadOrGone(model.paymentMethodDescriptionText, paymentMethodStatement);
        ViewUtils.loadOrGone(getStatement(model), statement);
        amount.setModel(model.amountModel);
        renderInfo(model.info);
    }

    @Nullable
    private String getStatement(@NonNull final Model model) {
        if (PaymentTypes.isCardPaymentType(model.paymentTypeId) && TextUtil.isNotEmpty(model.statement)) {
            return TextUtil.format(getContext(), R.string.px_text_state_account_activity_congrats, model.statement);
        } else if (PaymentTypes.isBankTransfer(model.paymentTypeId) && model.statementText != null) {
            return model.statementText.getMessage();
        }
        return null;
    }

    @Nullable
    private String getDescription(@NonNull final Model model) {
        if (PaymentTypes.isCardPaymentType(model.paymentTypeId)) {
            return String.format(Locale.getDefault(), "%s %s %s",
                model.paymentMethodName,
                getResources().getString(R.string.px_ending_in),
                model.lastFourDigits);
        } else if (PaymentTypes.isBankTransfer(model.paymentTypeId) && model.descriptionText != null) {
            return model.descriptionText.getMessage();
        } else if (!PaymentTypes.isAccountMoney(model.paymentTypeId) || model.paymentMethodDescriptionText == null ||
            model.paymentMethodDescriptionText.getMessage() == null) {
            return model.paymentMethodName;
        }
        return null;
    }

    private void renderInfo(@Nullable final PaymentResultInfo info) {
        if (info != null) {
            ViewUtils.loadOrGone(info.getTitle(), infoTitle);
            ViewUtils.loadOrGone(info.getSubtitle(), infoSubtitle);
        } else {
            infoTitle.setVisibility(GONE);
            infoSubtitle.setVisibility(GONE);
        }
    }

    public static final class Model {

        public static Model with(@NonNull final PaymentInfo paymentInfo, @Nullable final String statement) {

            final PaymentResultAmount.Model amountModel = new PaymentResultAmount.Model.Builder(
                paymentInfo.paidAmount, paymentInfo.rawAmount)
                .setDiscountName(paymentInfo.discountName)
                .setNumberOfInstallments(paymentInfo.installmentsCount)
                .setInstallmentsAmount(paymentInfo.installmentsAmount)
                .setInstallmentsRate(paymentInfo.installmentsRate)
                .setInstallmentsTotalAmount(paymentInfo.installmentsTotalAmount)
                .build();

            final PaymentCongratsText description =
                paymentInfo.paymentMethodDescriptionText != null ? paymentInfo.paymentMethodDescriptionText : PaymentCongratsText.EMPTY;

            return new Builder(paymentInfo.paymentMethodName, paymentInfo.iconUrl,
                description,
                paymentInfo.paymentMethodType.value)
                .setLastFourDigits(paymentInfo.lastFourDigits)
                .setAmountModel(amountModel)
                .setInfo(paymentInfo.consumerCreditsInfo)
                .setDescriptionText(paymentInfo.descriptionText)
                .setStatement(statement)
                .setStatementText(paymentInfo.statementText)
                .build();
        }

        @NonNull /* default */ final String paymentMethodId;
        @NonNull /* default */ final String paymentMethodName;
        @Nullable /* default */ final String imageUrl;
        @NonNull /* default */ final PaymentCongratsText paymentMethodDescriptionText;
        @NonNull /* default */ final String paymentTypeId;
        @NonNull /* default */ final PaymentResultAmount.Model amountModel;
        @Nullable /* default */ final String lastFourDigits;
        @Nullable /* default */ final String statement;
        @Nullable /* default */ final PaymentResultInfo info;
        @Nullable /* default */ final PaymentCongratsText descriptionText;
        @Nullable /* default */ final PaymentCongratsText statementText;

        /* default */ Model(@NonNull final Builder builder) {
            paymentMethodId = builder.paymentMethodId;
            paymentMethodName = builder.paymentMethodName;
            imageUrl = builder.imageUrl;
            paymentMethodDescriptionText = builder.paymentMethodDescriptionText;
            paymentTypeId = builder.paymentTypeId;
            amountModel = builder.amountModel;
            lastFourDigits = builder.lastFourDigits;
            statement = builder.statement;
            info = builder.info;
            descriptionText = builder.descriptionText;
            statementText = builder.statementText;
        }

        public static class Builder {
            @Nullable /* default */ String imageUrl;
            @NonNull /* default */ final PaymentCongratsText paymentMethodDescriptionText;
            @NonNull /* default */ String paymentMethodId;
            @NonNull /* default */ String paymentMethodName;
            @NonNull /* default */ String paymentTypeId;
            /* default */ PaymentResultAmount.Model amountModel;
            @Nullable /* default */ String lastFourDigits;
            @Nullable /* default */ String statement;
            @Nullable /* default */ PaymentResultInfo info;
            @Nullable /* default */ PaymentCongratsText descriptionText;
            @Nullable /* default */ PaymentCongratsText statementText;

            public Builder(@NonNull final String paymentMethodName,
                @Nullable final String imageUrl,
                @NonNull final PaymentCongratsText paymentMethodDescriptionText, @NonNull final String paymentTypeId) {
                this.paymentMethodName = paymentMethodName;
                this.imageUrl = imageUrl;
                this.paymentMethodDescriptionText = paymentMethodDescriptionText;
                this.paymentTypeId = paymentTypeId;
            }

            public Builder setAmountModel(@NonNull final PaymentResultAmount.Model amountModel) {
                this.amountModel = amountModel;
                return this;
            }

            public Builder setLastFourDigits(@Nullable final String lastFourDigits) {
                this.lastFourDigits = lastFourDigits;
                return this;
            }

            public Builder setInfo(@Nullable final PaymentResultInfo info) {
                this.info = info;
                return this;
            }

            public Builder setDescriptionText(@Nullable final PaymentCongratsText descriptionText) {
                this.descriptionText = descriptionText;
                return this;
            }

            public Builder setStatementText(@Nullable final PaymentCongratsText statementText) {
                this.statementText = statementText;
                return this;
            }

            public Builder setStatement(@Nullable final String statement) {
                this.statement = statement;
                return this;
            }

            public Model build() {
                return new Model(this);
            }
        }
    }
}