package com.hsdroid.paymentdetails.model;

import com.hsdroid.paymentdetails.utils.PaymentType;

import java.util.Objects;

public class Payment {
    private PaymentType type;
    private int amount;
    private String provider;
    private String transactionReference;

    public Payment(PaymentType type, int amount, String provider, String transactionReference) {
        this.type = type;
        this.amount = amount;
        this.provider = provider;
        this.transactionReference = transactionReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return amount == payment.amount &&
                type == payment.type &&
                Objects.equals(provider, payment.provider) &&
                Objects.equals(transactionReference, payment.transactionReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, amount, provider, transactionReference);
    }

    public PaymentType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public String getProvider() {
        return provider;
    }

    public String getTransactionReference() {
        return transactionReference;
    }
}