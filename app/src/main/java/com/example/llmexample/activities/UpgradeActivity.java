package com.example.llmexample.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.llmexample.R;
import com.example.llmexample.utilities.SharedPreferencesHelper;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UpgradeActivity extends AppCompatActivity {

    private Button starterPurchaseButton;
    private Button intermediatePurchaseButton;
    private Button advancedPurchaseButton;
    private PaymentsClient paymentsClient;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;

    // Your Google Pay API key
    private static final String GOOGLE_PAY_API_KEY = "AIzaSyAgZJFMHhPcP9dOsskqzoQCKQAdh0IfE7s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        // Initialize Google Pay client
        Wallet.WalletOptions walletOptions = new Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                .build();
        paymentsClient = Wallet.getPaymentsClient(this, walletOptions);

        // Initialize buttons
        starterPurchaseButton = findViewById(R.id.starterPurchaseButton);
        intermediatePurchaseButton = findViewById(R.id.intermediatePurchaseButton);
        advancedPurchaseButton = findViewById(R.id.advancedPurchaseButton);

        // Check if Google Pay is available
        isReadyToPay();

        // Set click listeners
        starterPurchaseButton.setOnClickListener(v -> requestPayment("starter", 4.99));
        intermediatePurchaseButton.setOnClickListener(v -> requestPayment("intermediate", 9.99));
        advancedPurchaseButton.setOnClickListener(v -> requestPayment("advanced", 19.99));
    }

    private JSONObject getBaseRequest() {
        try {
            return new JSONObject()
                    .put("apiVersion", 2)
                    .put("apiVersionMinor", 0)
                    .put("allowedPaymentMethods", new JSONArray()
                            .put(new JSONObject()
                                    .put("type", "CARD")
                                    .put("parameters", new JSONObject()
                                            .put("allowedAuthMethods", new JSONArray()
                                                    .put("PAN_ONLY")
                                                    .put("CRYPTOGRAM_3DS"))
                                            .put("allowedCardNetworks", new JSONArray()
                                                    .put("VISA")
                                                    .put("MASTERCARD")))
                                    .put("tokenizationSpecification", new JSONObject()
                                            .put("type", "PAYMENT_GATEWAY")
                                            .put("parameters", new JSONObject()
                                                    .put("gateway", "stripe")
                                                    .put("gatewayMerchantId", GOOGLE_PAY_API_KEY)))));
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    private void isReadyToPay() {
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(getBaseRequest().toString());
        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(this,
                task1 -> {
                    try {
                        boolean result = task1.getResult(ApiException.class);
                        if (result) {
                            // Google Pay is available, enable the buttons
                            starterPurchaseButton.setEnabled(true);
                            intermediatePurchaseButton.setEnabled(true);
                            advancedPurchaseButton.setEnabled(true);
                        } else {
                            showError("Google Pay is not available on this device");
                        }
                    } catch (ApiException exception) {
                        // Error determining readiness to pay
                        String errorMessage = String.format("Google Pay Error:\nCode: %d\nMessage: %s", 
                            exception.getStatusCode(),
                            exception.getMessage());
                        showError(errorMessage);
                    }
                });
    }

    private void showError(String message) {
        // Disable the buttons
        starterPurchaseButton.setEnabled(false);
        intermediatePurchaseButton.setEnabled(false);
        advancedPurchaseButton.setEnabled(false);

        // Show error in an AlertDialog for better visibility and copying
        new AlertDialog.Builder(this)
                .setTitle("Payment Error")
                .setMessage(message)
                .setPositiveButton("Copy", (dialog, which) -> {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("error_message", message);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, "Error message copied to clipboard", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("OK", null)
                .show();
    }

    private void requestPayment(String level, double price) {
        try {
            JSONObject paymentDataRequest = getBaseRequest()
                    .put("merchantInfo", new JSONObject()
                            .put("merchantName", "LLM Learning App")
                            .put("merchantId", GOOGLE_PAY_API_KEY))
                    .put("transactionInfo", new JSONObject()
                            .put("totalPrice", String.format("%.2f", price))
                            .put("totalPriceStatus", "FINAL")
                            .put("countryCode", "US")
                            .put("currencyCode", "USD"));

            Task<PaymentData> task = paymentsClient.loadPaymentData(
                    PaymentDataRequest.fromJson(paymentDataRequest.toString()));
            AutoResolveHelper.resolveTask(task, this, LOAD_PAYMENT_DATA_REQUEST_CODE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        // Payment successful, handle the response
                        handlePaymentSuccess(paymentData);
                        break;
                    case Activity.RESULT_CANCELED:
                        // Payment canceled by user
                        Toast.makeText(this, "Payment canceled", Toast.LENGTH_SHORT).show();
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        // Payment failed, handle the error
                        handleError(status.getStatusCode());
                        break;
                }
                break;
        }
    }

    private void handlePaymentSuccess(PaymentData paymentData) {
        // In a real app, you would send this payment data to your server
        // For this demo, we'll just save the subscription
        try {
            JSONObject paymentDataJson = new JSONObject(paymentData.toJson());
            // Extract the payment information
            JSONObject paymentMethodData = paymentDataJson.getJSONObject("paymentMethodData");

            // Get the subscription level from the transaction info
            JSONObject transactionInfo = paymentDataJson.getJSONObject("transactionInfo");
            double amount = Double.parseDouble(transactionInfo.getString("totalPrice"));

            // Determine subscription level based on amount
            String subscriptionLevel;
            if (amount <= 4.99) {
                subscriptionLevel = "starter";
            } else if (amount <= 9.99) {
                subscriptionLevel = "intermediate";
            } else {
                subscriptionLevel = "advanced";
            }

            SharedPreferencesHelper.saveSubscriptionLevel(this, subscriptionLevel);
            Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error processing payment data", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleError(int statusCode) {
        String message = "Payment failed with code: " + statusCode;
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}