package com.hsdroid.paymentdetails;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.hsdroid.paymentdetails.model.Payment;
import com.hsdroid.paymentdetails.utils.FileUtils;
import com.hsdroid.paymentdetails.utils.LoadCallback;
import com.hsdroid.paymentdetails.utils.PaymentDialog;
import com.hsdroid.paymentdetails.utils.PaymentListener;
import com.hsdroid.paymentdetails.utils.PaymentType;
import com.hsdroid.paymentdetails.utils.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PaymentListener, SaveCallback {
    private List<Payment> payments = new ArrayList<>();
    private ChipGroup chipGroup;
    private TextView totalAmountText, noDataText, addPaymentText;
    private Button saveToFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chipGroup = findViewById(R.id.chip_group);
        totalAmountText = findViewById(R.id.text_total_amount);
        noDataText = findViewById(R.id.tv_no_data);
        addPaymentText = findViewById(R.id.tv_add_payment);
        saveToFile = findViewById(R.id.button_save);

        FileUtils.loadPayments(this, new LoadCallback() {
            @Override
            public void onLoadComplete(List<Payment> loadedPayments) {
                payments = loadedPayments;
                updateUI();
            }
        });

        addPaymentText.setOnClickListener(v -> showPaymentDialog());
        saveToFile.setOnClickListener(v -> FileUtils.savePayments(this, payments, this));
    }

    private void updateUI() {
        chipGroup.removeAllViews();
        int totalAmount = 0;

        if (payments.isEmpty()) {
            noDataText.setVisibility(View.VISIBLE);
            chipGroup.setVisibility(View.GONE);
        } else {
            noDataText.setVisibility(View.GONE);
            chipGroup.setVisibility(View.VISIBLE);

            for (Payment payment : payments) {
                String formattedText = String.format("%s %s %s", payment.getType().getDisplayName(), getString(R.string.rupee_placeholder), payment.getAmount());
                Chip chip = new Chip(this, null, R.style.Custom_Chip);
                chip.setText(formattedText);
                chip.setCloseIconVisible(true);
                chip.setOnCloseIconClickListener(v -> {
                    payments.remove(payment);
                    updateUI();
                });
                chipGroup.addView(chip);
                totalAmount += payment.getAmount();
            }
        }

        String formattedAmount = String.format(" %s%s", getString(R.string.rupee_symbol), totalAmount);
        totalAmountText.setText(formattedAmount);
    }

    private void showPaymentDialog() {
        List<PaymentType> availableTypes = new ArrayList<>();
        for (PaymentType type : PaymentType.values()) {
            boolean isTypeAvailable = true;
            for (Payment payment : payments) {
                if (payment.getType() == type) {
                    isTypeAvailable = false;
                    break;
                }
            }
            if (isTypeAvailable) {
                availableTypes.add(type);
            }
        }

        if (!availableTypes.isEmpty()) {
            new PaymentDialog(availableTypes).show(getSupportFragmentManager(), "PaymentDialog");
        } else {
            Toast.makeText(this, getString(R.string.no_payment_mode_available), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentAdded(Payment payment) {
        payments.add(payment);
        updateUI();
    }

    @Override
    public void onSaveComplete(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}