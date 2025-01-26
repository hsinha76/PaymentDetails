package com.hsdroid.paymentdetails.utils;

import com.hsdroid.paymentdetails.model.Payment;

public interface PaymentListener {
    void onPaymentAdded(Payment payment);
}