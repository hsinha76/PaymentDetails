package com.hsdroid.paymentdetails.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.hsdroid.paymentdetails.R;
import com.hsdroid.paymentdetails.model.Payment;

import java.util.List;

public class PaymentDialog extends DialogFragment {
    private PaymentListener listener;
    private final List<PaymentType> availableTypes;

    public PaymentDialog(List<PaymentType> availableTypes) {
        this.availableTypes = availableTypes;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof PaymentListener) {
            listener = (PaymentListener) context;
        } else {
            Log.e("harish", "Context must implement PaymentListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_payment, null);
        builder.setCancelable(false);
        builder.setView(view);

        Spinner spinner = view.findViewById(R.id.spinner_payment_type);
        EditText amountInput = view.findViewById(R.id.edit_amount);
        TextInputLayout providerInputLayout = view.findViewById(R.id.provider_input_layout);
        EditText providerInput = view.findViewById(R.id.edit_provider);
        TextInputLayout referenceInputLayout = view.findViewById(R.id.reference_input_layout);
        EditText referenceInput = view.findViewById(R.id.edit_reference);
        Button addButton = view.findViewById(R.id.button_add);
        TextView cancelButton = view.findViewById(R.id.button_cancel);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item);
        for (PaymentType type : availableTypes) {
            adapter.add(type.getDisplayName());
        }
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDisplayName = parent.getItemAtPosition(position).toString();
                PaymentType selectedType = PaymentType.fromDisplayName(selectedDisplayName);
                if (selectedType == PaymentType.CREDIT_CARD || selectedType == PaymentType.BANK_TRANSFER) {
                    providerInputLayout.setVisibility(View.VISIBLE);
                    referenceInputLayout.setVisibility(View.VISIBLE);
                } else {
                    providerInputLayout.setVisibility(View.GONE);
                    referenceInputLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        addButton.setOnClickListener(v -> {
            String provider = providerInput.getText().toString().trim();
            String reference = referenceInput.getText().toString().trim();
            String amountText = amountInput.getText().toString().trim();

            if (amountText.isEmpty()) {
                amountInput.setError(getString(R.string.required));
                return;
            }

            int amount = Integer.parseInt(amountText);
            PaymentType type = PaymentType.fromDisplayName(spinner.getSelectedItem().toString());

            if (type != PaymentType.CASH && (provider.isEmpty() || reference.isEmpty())) {
                if (provider.isEmpty()) providerInput.setError(getString(R.string.required));
                if (reference.isEmpty()) referenceInput.setError(getString(R.string.required));
                return;
            }

            listener.onPaymentAdded(new Payment(type, amount, provider, reference));
            dismiss();
        });

        cancelButton.setOnClickListener(v -> {
            dismiss();
        });

        return builder.create();
    }
}