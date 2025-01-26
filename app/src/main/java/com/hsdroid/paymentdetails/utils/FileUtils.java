package com.hsdroid.paymentdetails.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hsdroid.paymentdetails.R;
import com.hsdroid.paymentdetails.model.Payment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileUtils {
    private static final String FILE_NAME = "LastPayment.txt";
    private static final Gson gson = new Gson();
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void savePayments(Context context, List<Payment> payments, SaveCallback callback) {
        executorService.execute(() -> {
            List<Payment> savedPayments = loadPaymentsSync(context);

            if (payments.equals(savedPayments)) {
                if (callback != null) {
                    callback.onSaveComplete(context.getString(R.string.no_new_data_to_save));
                }
                return;
            }

            String json = gson.toJson(payments);

            try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
                fos.write(json.getBytes());
                if (callback != null) {
                    callback.onSaveComplete(context.getString(R.string.payments_saved_to_file_successfully));
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onSaveComplete(context.getString(R.string.failed_to_save_payments) + e.getMessage());
                }
            }
        });
    }

    public static void loadPayments(Context context, LoadCallback callback) {
        executorService.execute(() -> {
            List<Payment> payments = loadPaymentsSync(context);
            if (callback != null) {
                callback.onLoadComplete(payments);
            }
        });
    }

    private static List<Payment> loadPaymentsSync(Context context) {
        List<Payment> payments = new ArrayList<>();
        File file = new File(context.getFilesDir(), FILE_NAME);

        if (file.exists()) {
            try (FileInputStream fis = context.openFileInput(FILE_NAME)) {
                byte[] data = new byte[(int) file.length()];
                fis.read(data);
                String json = new String(data);

                Type paymentListType = new TypeToken<ArrayList<Payment>>() {
                }.getType();
                payments = gson.fromJson(json, paymentListType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return payments;
    }
}
