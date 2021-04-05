package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.fragments.wallet_fragments.TokenAuthFragment;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.ugmobilemoney.UgandaMobileMoneyPaymentCallback;
import com.flutterwave.raveandroid.rave_presentation.ugmobilemoney.UgandaMobileMoneyPaymentManager;
import com.flutterwave.raveutils.verification.RaveVerificationUtils;
import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.models.WalletTransaction;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;

import java.text.NumberFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DepositMoneyMobile extends DialogFragment {
    LinearLayout layoutAddMoney;
    Button addMoneyImg;
    TextView addMoneyTxt, phoneNumberTxt, errorMsgTxt;
    static String PENDING_DEPOSIT_REFERENCE_NUMBER;
    TextView balanceTextView;
    double balance;
    private String txRef;

    private DialogLoader dialogLoader;;
    Context activity;
    private RaveVerificationUtils verificationUtils;

    public DepositMoneyMobile(Context context, double balance) {
        this.activity = context;
        this.balance = balance;

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.wallet_add_money, null);

        builder.setView(view);

        initializeForm(view);

        ImageView close = view.findViewById(R.id.wallet_transfer_money_close);
        close.setOnClickListener(v -> dismiss());
        dialogLoader = new DialogLoader(getContext());
        return builder.create();

    }

    public void initializeForm(View view) {
        layoutAddMoney = view.findViewById(R.id.layout_add_money);
        phoneNumberTxt = view.findViewById(R.id.crop_add_money_mobile_no);
        addMoneyImg = view.findViewById(R.id.button_add_money);
        addMoneyTxt = view.findViewById(R.id.crop_add_money_amount);
        balanceTextView = view.findViewById(R.id.crop_add_money_balance);
        errorMsgTxt = view.findViewById(R.id.text_view_error_message);

        balanceTextView.setText(NumberFormat.getInstance().format(balance));
        this.txRef = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, this.activity) + (new Date().getTime());
        addMoneyImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateDeposit();
            }
        });

        verificationUtils = new RaveVerificationUtils(this, false, BuildConfig.PUBLIC_KEY);

    }


    public void initiateDeposit() {
      
        dialogLoader.showProgressDialog();
        String phoneNumber = phoneNumberTxt.getText().toString();
        String amountEntered = addMoneyTxt.getText().toString();

        double amount = Float.parseFloat(amountEntered);
        txRef = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, this.activity) + (new Date().getTime());
        String eMaishaPayServiceMail="info@cabraltech.com";
        RaveNonUIManager raveNonUIManager = new RaveNonUIManager().setAmount(amount)
                .setCurrency("UGX")
                .setEmail(eMaishaPayServiceMail)
                .setfName(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME, this.activity))
                .setlName(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_LAST_NAME, this.activity))
                .setPhoneNumber("0" + phoneNumber)
                .setNarration("eMaisha Pay")
                .setPublicKey(BuildConfig.PUBLIC_KEY)
                .setEncryptionKey(BuildConfig.ENCRYPTION_KEY)
                .setTxRef(txRef)
                .onStagingEnv(false)
                .isPreAuth(true)
                .initialize();

        UgandaMobileMoneyPaymentCallback mobileMoneyPaymentCallback = new UgandaMobileMoneyPaymentCallback() {
            @Override
            public void showProgressIndicator(boolean active) {
                try {

                    if (dialogLoader == null) {
                        dialogLoader = new DialogLoader(getContext());
                        dialogLoader.showProgressDialog();
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage, @Nullable String flwRef) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show();
                Log.e("MobileMoneypaymentError", errorMessage);
            }

            @Override
            public void onSuccessful(String flwRef) {
                dialogLoader.hideProgressDialog();
                Log.e("Success code :", flwRef);
                Toast.makeText(activity, "Transaction Successful", Toast.LENGTH_LONG).show();
                creditAfterDeposit(flwRef,"0" + phoneNumber);
            }

            @Override
            public void showAuthenticationWebPage(String authenticationUrl) {
                Log.e("Loading auth web page: ", authenticationUrl);
                verificationUtils.showWebpageVerificationScreen(authenticationUrl);
            }
        };
        UgandaMobileMoneyPaymentManager mobilePayManager = new UgandaMobileMoneyPaymentManager(raveNonUIManager, (UgandaMobileMoneyPaymentCallback) mobileMoneyPaymentCallback);

        mobilePayManager.charge();
    }


    public void creditAfterDeposit(String txRef, String phonumber) {
        /******************RETROFIT IMPLEMENTATION**************************/
     
        
        dialogLoader.showProgressDialog();

        String amountEntered = addMoneyTxt.getText().toString();
        double amount = Float.parseFloat(amountEntered);
        String referenceNumber = txRef;
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());

        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<WalletTransaction> call = apiRequests.creditUser(access_token,null,amount,referenceNumber,"Deposit","flutterwave",phonumber,false,request_id,category,"creditUserAfterTransaction");
        call.enqueue(new Callback<WalletTransaction>() {
            @Override
            public void onResponse(Call<WalletTransaction> call, Response<WalletTransaction> response) {
                if(response.code() == 200){

                    dialogLoader.hideProgressDialog();

                    refreshActivity();
                }else if(response.code() == 401){

                        TokenAuthFragment.startAuth( true);

                    } else if (response.code() == 500) {
                        if (response.errorBody() != null) {
                            Toast.makeText(activity,response.body().getRecepient(), Toast.LENGTH_LONG).show();
                        } else {

                            Log.e("info", "Something got very very wrong, code: " + response.code());
                        }
                        Log.e("info 500", String.valueOf(response.errorBody()) + ", code: " + response.code());
                    } else if (response.code() == 400) {
                        if (response.errorBody() != null) {
                            Toast.makeText(activity, response.errorBody().toString(), Toast.LENGTH_LONG).show();
                        } else {

                            Log.e("info", "Something got very very wrong, code: " + response.code());
                        }
                        Log.e("info 500", String.valueOf(response.errorBody()) + ", code: " + response.code());
                    } else if (response.code() == 406) {
                        if (response.errorBody() != null) {

                            Toast.makeText(activity, response.errorBody().toString(), Toast.LENGTH_LONG).show();
                        } else {

                            Log.e("info", "Something got very very wrong, code: " + response.code());
                        }
                        Log.e("info 406", String.valueOf(response.errorBody()) + ", code: " + response.code());
                    } else {

                        if (response.errorBody() != null) {

                            Toast.makeText(activity, response.errorBody().toString(), Toast.LENGTH_LONG).show();
                            Log.e("info", String.valueOf(response.errorBody()) + ", code: " + response.code());
                        } else {

                            Log.e("info", "Something got very very wrong, code: " + response.code());
                        }
                    }
                    dialogLoader.hideProgressDialog();
                }


            @Override
            public void onFailure(Call<WalletTransaction> call, Throwable t) {

            }
        });


    }


    public void refreshActivity() {
        Intent goToWallet = new Intent(activity, WalletHomeActivity.class);
        startActivity(goToWallet);
    }


}
