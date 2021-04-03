package com.cabral.emaishapay.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.ConfirmActivity;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.models.InitiateTransferResponse;
import com.cabral.emaishapay.models.InitiateWithdrawResponse;
import com.cabral.emaishapay.models.WalletTransaction;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.ugmobilemoney.UgandaMobileMoneyPaymentCallback;
import com.flutterwave.raveandroid.rave_presentation.ugmobilemoney.UgandaMobileMoneyPaymentManager;
import com.flutterwave.raveutils.verification.RaveVerificationUtils;

import java.text.NumberFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AcceptPaymentFragment extends Fragment {

    TextView  txtPaymentMethod;

    private DialogLoader dialogLoader;
    LinearLayout layoutMobileMoney;
    Spinner spPaymentMethod;
    Button saveBtn;
    EditText mobileMoneyNo, amountEdt;
    FragmentManager fm;
    private Context context;
    String txRef;
    private RaveVerificationUtils verificationUtils;


    private Dialog otpDialog;
    private EditText code1,code2,code3,code4,code5,code6;
    TextView resendtxtview;
    APIRequests apiRequests;
    private  String otp_code, sms_code,smsResults;

    public AcceptPaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_accept_payment, container, false);

        Toolbar toolbar=view.findViewById(R.id.toolbar_accept_payment);
        context=getContext();
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Accept Payment");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);

        dialogLoader = new DialogLoader(getContext());
        this.txRef = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, getContext()) + (new Date().getTime());
        verificationUtils = new RaveVerificationUtils(this, false, BuildConfig.PUBLIC_KEY);

        txtPaymentMethod=view.findViewById(R.id.text_mobile_number);
        layoutMobileMoney=view.findViewById(R.id.layout_mobile_number);
        spPaymentMethod=view.findViewById(R.id.sp_payment_method);
        saveBtn=view.findViewById(R.id.btn_save);
        mobileMoneyNo=view.findViewById(R.id.pay_mobile_no);
        amountEdt=view.findViewById(R.id.total_amount);

        spPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //Change selected text color
                    ((TextView) view).setTextColor(getResources().getColor(R.color.textColor));
                    //((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);//Change selected text size
                } catch (Exception e) {

                }
                String selectedItem=spPaymentMethod.getSelectedItem().toString();
                if(selectedItem.equalsIgnoreCase("emaisha pay")){
                    layoutMobileMoney.setVisibility(View.VISIBLE);
                    txtPaymentMethod.setText("eMaisha Account");
                }
                else if(selectedItem.equalsIgnoreCase("Mobile Money")){
                    layoutMobileMoney.setVisibility(View.VISIBLE);
                    txtPaymentMethod.setText("Mobile Number");
                }
                else {
                    layoutMobileMoney.setVisibility(View.GONE);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String paymentMethod=txtPaymentMethod.getText().toString();
                if(paymentMethod.equalsIgnoreCase("Mobile Number")){
                    initiateMobileMoneyCharge("0"+mobileMoneyNo.getText().toString(), Double.parseDouble(amountEdt.getText().toString()) );
                }else if(paymentMethod.equalsIgnoreCase("eMaisha Account")){
                    initiateAcceptPayment("0"+mobileMoneyNo.getText().toString(), Double.parseDouble(amountEdt.getText().toString()) );
                }
            }
        });

        return view;
    }



    public void initiateMobileMoneyCharge(String phoneNumber,double amount) {

        dialogLoader.showProgressDialog();

        txRef = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, this.context) + (new Date().getTime());
        String eMaishaPayServiceMail="info@cabraltech.com";

        RaveNonUIManager raveNonUIManager = new RaveNonUIManager().setAmount(amount)
                .setCurrency("UGX")
                .setEmail(eMaishaPayServiceMail)
                .setfName(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME, this.context))
                .setlName(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_LAST_NAME, this.context))
                .setPhoneNumber(phoneNumber)
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
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                Log.e("MobileMoneypaymentError", errorMessage);
            }

            @Override
            public void onSuccessful(String flwRef) {
                dialogLoader.hideProgressDialog();
                Log.e("Success code :", flwRef);
                Toast.makeText(context, "Transaction Successful", Toast.LENGTH_LONG).show();
                creditAfterSale(flwRef,amount,phoneNumber);
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

    public void initiateAcceptPayment(final String phoneNumber, final double amount) {

        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        dialogLoader.showProgressDialog();

        /*****RETROFIT IMPLEMENTATION*****/
        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<InitiateTransferResponse> call = apiRequests.initiateAgentTransaction(access_token, amount,phoneNumber,"Agent Payment",request_id,category,"initiateAgentTransaction");
        call.enqueue(new Callback<InitiateTransferResponse>() {
            @Override
            public void onResponse(Call<InitiateTransferResponse> call, Response<InitiateTransferResponse> response) {
                if(response.code() ==200){
                    dialogLoader.hideProgressDialog();
                    getOTPFromUser(phoneNumber,amount);

                }
                else if(response.code() == 401) {
                    TokenAuthFragment.startAuth( true);
                    getActivity().finish();
                }
                else if(response.code() == 401) {
                    TokenAuthFragment.startAuth( true);

                }
                else if (response.code() == 500) {
                    Log.e("info 500", new String(String.valueOf(response.errorBody())) + ", code: " + response.code());
                } else if (response.code() == 400) {
                    Log.e("info 400", new String(String.valueOf(response.errorBody()) + ", code: " + response.code()));
                } else if (response.code() == 406) {

                    Log.e("info 406", new String(String.valueOf(response.errorBody())) + ", code: " + response.code());
                } else {
                    Log.e("info 406", new String("Error Occurred Try again later"));

                }
                dialogLoader.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<InitiateTransferResponse> call, Throwable t) {
                Log.e("info : " , new String(String.valueOf(t.getMessage())));

                dialogLoader.hideProgressDialog();
            }
        });

    }

    public void comfirmAcceptPayment(final String OTPCode,final String customerNumber, final double amount) {
        ProgressDialog dialog;
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        dialogLoader.showProgressDialog();

        /*****RETROFIT IMPLEMENTATION*****/
        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<InitiateWithdrawResponse> call = apiRequests.confirmAcceptPayment(access_token, amount,customerNumber,OTPCode,request_id,category,"confirmAgentPayment");
        call.enqueue(new Callback<InitiateWithdrawResponse>() {
            @Override
            public void onResponse(Call<InitiateWithdrawResponse> call, Response<InitiateWithdrawResponse> response) {
                if(response.code() ==200){
                    dialogLoader.hideProgressDialog();
                    refreshActivity();

                }
                else if(response.code() == 401) {
                    TokenAuthFragment.startAuth( true);

                }
                else if (response.code() == 500) {
                    Log.e("info 500", new String(String.valueOf(response.errorBody())) + ", code: " + response.code());
                } else if (response.code() == 400) {
                    Log.e("info 400", new String(String.valueOf(response.errorBody()) + ", code: " + response.code()));
                } else if (response.code() == 406) {

                    Log.e("info 406", new String(String.valueOf(response.errorBody())) + ", code: " + response.code());
                } else {
                    Log.e("info 406", new String("Error Occurred Try again later"));

                }
                dialogLoader.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<InitiateWithdrawResponse> call, Throwable t) {
                Log.e("info 406", new String("Error Occurred Try again later"));
                dialogLoader.hideProgressDialog();
            }
        });

    }

    private void getOTPFromUser(final String customerNumber, final double amount) {
        otpDialog  = new Dialog(context);
        otpDialog.setContentView(R.layout.login_dialog_otp);
        otpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        otpDialog.setCancelable(false);

        code1= otpDialog.findViewById(R.id.otp_code1_et);
        code2= otpDialog.findViewById(R.id.otp_code2_et);
        code3= otpDialog.findViewById(R.id.otp_code3_et);
        code4= otpDialog.findViewById(R.id.otp_code4_et);
        code5=otpDialog.findViewById(R.id.otp_code5_et);
        code6= otpDialog.findViewById(R.id.otp_code6_et);
        resendtxtview= otpDialog.findViewById(R.id.login_otp_resend_code);


        code1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                code2.requestFocus();
            }
        });

        code2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                code3.requestFocus();
            }
        });

        code3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                code4.requestFocus();
            }
        });

        code4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                code5.requestFocus();
            }
        });

        code5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                code6.requestFocus();
            }
        });

        code6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                otp_code = code1.getText().toString() + code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString().trim();
                otp_code = otp_code.replaceAll("\\s+", "");
                if(otp_code.length()>=6){
                    comfirmAcceptPayment(otp_code,customerNumber,amount);
                }

            }
        });

        otpDialog.findViewById(R.id.login_otp_resend_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // processLogin(password,ConfirmActivity.phonenumber);
            }
        });
        otpDialog.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpDialog.dismiss();
                otp_code = code1.getText().toString() + code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString();
                comfirmAcceptPayment(otp_code,customerNumber,amount);
            }
        });
        otpDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams params = otpDialog.getWindow().getAttributes(); // change this to your otpDialog.

        params.x = 100; // Here is the param to set your dialog position. Same with params.x
        otpDialog.getWindow().setAttributes(params);
        otpDialog.show();
    }

    public void creditAfterSale(String txRef, double amount, String thirdParty_id) {
        /******************RETROFIT IMPLEMENTATION**************************/
        dialogLoader.showProgressDialog();

        String referenceNumber = txRef;
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());

        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<WalletTransaction> call = apiRequests.creditUser(access_token,null,amount,referenceNumber,"External Purchase","flutterwave",thirdParty_id,false,request_id,category,"creditUserAfterTransaction");
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
                        Toast.makeText(context,response.body().getRecepient(), Toast.LENGTH_LONG).show();
                    } else {

                        Log.e("info", "Something got very wrong, code: " + response.code());
                    }
                    Log.e("info 500", String.valueOf(response.errorBody()) + ", code: " + response.code());
                } else if (response.code() == 400) {
                    if (response.errorBody() != null) {
                        Toast.makeText(context, response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    } else {

                        Log.e("info", "Something got very wrong, code: " + response.code());
                    }
                    Log.e("info 500", String.valueOf(response.errorBody()) + ", code: " + response.code());
                } else if (response.code() == 406) {
                    if (response.errorBody() != null) {

                        Toast.makeText(context, response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    } else {

                        Log.e("info", "Something got very very wrong, code: " + response.code());
                    }
                    Log.e("info 406", String.valueOf(response.errorBody()) + ", code: " + response.code());
                } else {

                    if (response.errorBody() != null) {

                        Toast.makeText(context, response.errorBody().toString(), Toast.LENGTH_LONG).show();
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
        Intent goToWallet = new Intent(getActivity(), WalletHomeActivity.class);
        startActivity(goToWallet);
    }

}