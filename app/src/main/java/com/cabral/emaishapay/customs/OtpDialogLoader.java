package com.cabral.emaishapay.customs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.models.WalletAuthenticationResponse;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;
import com.cabral.emaishapay.services.SmsBroadcastReceiver;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class OtpDialogLoader {
    private  Context context;
    private Activity activity;
    private Dialog otpDialog;
    private  String otp_code;
    SmsBroadcastReceiver  smsBroadcastReceiver;
    EditText code1, code2, code3, code4, code5, code6;
    CountDownTimer timer;
    private static final int REQ_USER_CONSENT = 200;

    public OtpDialogLoader(Activity activity) {
        this.context=activity.getApplicationContext();
        this.activity=activity;
        otpDialog  = new Dialog(activity, R.style.myFullscreenAlertDialogStyle);
        otpDialog.setContentView(R.layout.login_dialog_otp);
        otpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        otpDialog.setCancelable(false);
    }


    public void showOTPDialog() {
        code1= otpDialog.findViewById(R.id.otp_code1_et);
        code2= otpDialog.findViewById(R.id.otp_code2_et);
        code3= otpDialog.findViewById(R.id.otp_code3_et);
        code4= otpDialog.findViewById(R.id.otp_code4_et);
        code5=otpDialog.findViewById(R.id.otp_code5_et);
        code6= otpDialog.findViewById(R.id.otp_code6_et);
        TextView resendtxtview= otpDialog.findViewById(R.id.login_otp_resend_code);
        TextView tvTimer= otpDialog.findViewById(R.id.tv_timer);
        RelativeLayout layoutResendCode= otpDialog.findViewById(R.id.layout_resend_code);
        TextView tvChangeNumber = otpDialog.findViewById(R.id.text_view_change_number);



        timer = new CountDownTimer(90000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvTimer.setText(millisUntilFinished / 1000 + " Seconds" );
            }

            public void onFinish() {
                layoutResendCode.setVisibility(View.VISIBLE);
            }
        };
        timer.start();

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
                    onConfirmOtp(otp_code,otpDialog);
                }

            }
        });

        otpDialog.findViewById(R.id.login_otp_resend_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call resend otp
                onResendOtp();

                layoutResendCode.setVisibility(View.GONE);
//                processLogin(password,ConfirmActivity.phonenumber);
            }
        });

        otpDialog.findViewById(R.id.text_view_change_number).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpDialog.dismiss();
            }
        });
//        otpDialog.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                otpDialog.dismiss();
//                otp_code = code1.getText().toString() + code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString();
//                confirmLogin(password,ConfirmActivity.phonenumber,otp_code,otpDialog);
//            }
//        });



        registerBroadcastReceiver();//register receiver to service to listen to incoming otp messages
        otpDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                context.unregisterReceiver(smsBroadcastReceiver);
            }
        });
        otpDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                context.unregisterReceiver(smsBroadcastReceiver);
            }
        });

        otpDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams params = otpDialog.getWindow().getAttributes(); // change this to your otpDialog.

        params.x = 100; // Here is the param to set your dialog position. Same with params.x
        otpDialog.getWindow().setAttributes(params);
        otpDialog.show();
    }

    protected abstract void onConfirmOtp(String otp_code, Dialog otpDialog);

    protected abstract void onResendOtp();


    private void registerBroadcastReceiver() {
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.smsBroadcastReceiverListener =
                new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
                    @Override
                    public void onSuccess(Intent intent) {
                        activity.startActivityForResult(intent, REQ_USER_CONSENT);
                    }
                    @Override
                    public void onFailure() {
                    }
                };
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        context.registerReceiver(smsBroadcastReceiver, intentFilter);
    }

    public void resendOtp( String phonenumber,DialogLoader dialogLoader,View snackerView) {
        String request_id = WalletHomeActivity.generateRequestId();

        //call the otp end point
        dialogLoader.showProgressDialog();
        Call<WalletAuthenticationResponse> call = APIClient.getWalletInstance(context).
                resendOtp(phonenumber,
                        request_id,
                        "ResendOTP");

        call.enqueue(new Callback<WalletAuthenticationResponse>() {
            @Override
            public void onResponse(Call<WalletAuthenticationResponse> call, Response<WalletAuthenticationResponse> response) {
                if(response.isSuccessful() && response.body().getStatus()==1 ) {
                   // String smsResults = response.body().getData().getSms_results();
                    timer.start();
                    registerBroadcastReceiver();//register receiver to service to listen to incoming otp messages
                }
                else{
                    Snackbar.make(snackerView,response.body().getMessage(),Snackbar.LENGTH_LONG).show();
                }
                dialogLoader.hideProgressDialog();

            }

            @Override
            public void onFailure(Call<WalletAuthenticationResponse> call, Throwable t) {
                Snackbar.make(snackerView,activity.getString(R.string.error_occured),Snackbar.LENGTH_LONG).show();
                dialogLoader.hideProgressDialog();
            }
        });

    }


}
