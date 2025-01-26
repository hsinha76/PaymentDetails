package com.hsdroid.paymentdetails.utils;

import com.hsdroid.paymentdetails.model.Payment;

import java.util.List;

public interface LoadCallback {
    void onLoadComplete(List<Payment> payments);
}
