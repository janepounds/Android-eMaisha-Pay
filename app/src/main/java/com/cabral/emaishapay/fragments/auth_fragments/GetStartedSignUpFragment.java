package com.cabral.emaishapay.fragments.auth_fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.AuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.databinding.FragmentGetStartedSignUpBinding;
import com.cabral.emaishapay.models.WalletAuthentication;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.services.SmsBroadcastReceiver;
import com.cabral.emaishapay.utils.ValidateInputs;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class GetStartedSignUpFragment extends Fragment {
    FragmentGetStartedSignUpBinding binding;
    Context context;
    CountDownTimer timer;

    private EditText code1,code2,code3,code4,code5,code6;
    private TextView tvTimer, tvChangeNumber;
    private RelativeLayout layoutResendCode;
    private Dialog dialog;
    private DialogLoader dialogLoader;
    String phone_no;
    SmsBroadcastReceiver  smsBroadcastReceiver;
    private static final int REQ_USER_CONSENT = 200;

    @Override
    public void onAttach(@NonNull Context context) {
        this.context=context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_get_started_sign_up,container,false);
        dialogLoader=new DialogLoader(this.context);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        View decorView = getActivity().getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        startSmsUserConsent();

        binding.getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate Login Form Inputs
                boolean isValidData = validateForm();
                if (isValidData) {
                    //authenticate phone
                    phone_no = "256" + binding.userMobile.getText().toString().trim();
                    authenticatePhoneNo(phone_no);

                }
                //navigate to Sign Up Fragment


            }
        });

        binding.layoutSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to Sign Up Fragment
                AuthActivity.navController.navigate(R.id.action_getStartedSignUpFragment_to_loginFragment);

            }
        });
    }


    private boolean validateForm() {
        if (!ValidateInputs.isValidNumber(binding.userMobile.getText().toString().trim())) {
            binding.userMobile.setError(getString(R.string.invalid_contact));
            return false;
        }
        else {
            return true;
        }
    }

    public void authenticatePhoneNo(String phoneNumber){
        dialogLoader.showProgressDialog();
        String request_id = WalletHomeActivity.generateRequestId();
        String service_code = "120224";
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);
        /******************RETROFIT IMPLEMENTATION***********************/
        Call<WalletAuthentication> call = APIClient.getWalletInstance(context).initiatePhoneAuth(phoneNumber,request_id,category,service_code,"initiatePhoneAuth");
        call.enqueue(new Callback<WalletAuthentication>() {
            @Override
            public void onResponse(Call<WalletAuthentication> call, Response<WalletAuthentication> response) {
                if(response.isSuccessful()){
                    dialogLoader.hideProgressDialog();

                    if(response.body().getStatus()==1){
                        if(response.body().getMessage().equalsIgnoreCase("Phone was already Verified")){
                            //navigate to signup
                            Bundle bundle = new Bundle();
                            bundle.putString("phone",phoneNumber.substring(3));
                            AuthActivity.navController.navigate(R.id.action_getStartedSignUpFragment_to_signUpFragment,bundle);

                        }else {

                            showOTPDialog(context);
                        }
                    }else{

                        Snackbar.make(context,getView(),response.body().getMessage(),Snackbar.LENGTH_SHORT).show();
                    }


                }else if (response.code() == 401) {
                    Toast.makeText(context,response.body().getMessage(),Toast.LENGTH_LONG).show();
                    dialogLoader.hideProgressDialog();

                }

            }

            @Override
            public void onFailure(Call<WalletAuthentication> call, Throwable t){
                dialogLoader.hideProgressDialog();
            }
        });

    }

    public void verifyVerificationCode(String code){
        //dialogLoader.showProgressDialog();

        String request_id = WalletHomeActivity.generateRequestId();
        String service_code = "120224";
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);
        /******************RETROFIT IMPLEMENTATION***********************/
        Call<WalletAuthentication> call = APIClient.getWalletInstance(context).validatePhoneNo(phone_no,request_id,category,service_code,"validatePhoneNumber",code);
        call.enqueue(new Callback<WalletAuthentication>() {
            @Override
            public void onResponse(Call<WalletAuthentication> call, Response<WalletAuthentication> response) {
                if(response.isSuccessful()){
                    if(response.body().getStatus()==1){
                        //go to signup fragment

                        //navigate to signup
                        Bundle bundle = new Bundle();
                        bundle.putString("phone", "0" + binding.userMobile.getText().toString().trim() );
                        AuthActivity.navController.navigate(R.id.action_getStartedSignUpFragment_to_signUpFragment,bundle);

                        dialogLoader.hideProgressDialog();
                        dialog.dismiss();
                    }else{
                        dialogLoader.hideProgressDialog();
                        Snackbar.make(context,dialog.findViewById(R.id.text_view_change_number),response.body().getMessage(),Snackbar.LENGTH_SHORT).show();

                    }


                }else if (response.code() == 401) {
                    Toast.makeText(context,response.body().getMessage(),Toast.LENGTH_LONG).show();
                    dialogLoader.hideProgressDialog();

                }

            }

            @Override
            public void onFailure(Call<WalletAuthentication> call, Throwable t){
                dialogLoader.hideProgressDialog();
            }
        });

    }


    /// Custom dialog for OTP
    public void showOTPDialog(Context activity) {
        //call success dialog
        dialog  = new Dialog(activity);
        dialog.setContentView(R.layout.login_dialog_otp);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(false);
        code1= dialog.findViewById(R.id.otp_code1_et);
        code2= dialog.findViewById(R.id.otp_code2_et);
        code3= dialog.findViewById(R.id.otp_code3_et);
        code4= dialog.findViewById(R.id.otp_code4_et);
        code5=dialog.findViewById(R.id.otp_code5_et);
        code6= dialog.findViewById(R.id.otp_code6_et);
        tvTimer= dialog.findViewById(R.id.tv_timer);
        layoutResendCode= dialog.findViewById(R.id.layout_resend_code);
        tvChangeNumber = dialog.findViewById(R.id.text_view_change_number);

        registerBroadcastReceiver();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                context.unregisterReceiver(smsBroadcastReceiver);
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                context.unregisterReceiver(smsBroadcastReceiver);
            }
        });



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
                String code = code1.getText().toString() + code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString();

                if(code.replaceAll("\\s+","").length()==6){
                    verifyVerificationCode(code);
                }
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
                String code = code1.getText().toString() + code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString();

                if(code.replaceAll("\\s+","").length()==6){
                    verifyVerificationCode(code);
                }
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
                String code = code1.getText().toString() + code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString();

                if(code.replaceAll("\\s+","").length()==6){
                    verifyVerificationCode(code);
                }
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
                String code = code1.getText().toString() + code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString();

                if(code.replaceAll("\\s+","").length()==6){
                    verifyVerificationCode(code);
                }
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
                String code = code1.getText().toString() + code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString();

                if(code.replaceAll("\\s+","").length()==6){
                    verifyVerificationCode(code);
                }
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
                String code = code1.getText().toString() + code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString();

                if(code.replaceAll("\\s+","").length()==6){
                    verifyVerificationCode(code);
                }

            }
        });
        dialog.findViewById(R.id.login_otp_resend_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendAuthenticatePhoneNo("256"+binding.userMobile.getText().toString().trim());
                layoutResendCode.setVisibility(View.VISIBLE);
            }
        });

        dialog.findViewById(R.id.text_view_change_number).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

    }

    public void resendAuthenticatePhoneNo(String phoneNumber){
        dialogLoader.showProgressDialog();
        String request_id = WalletHomeActivity.generateRequestId();
        String service_code = "120224";
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);
        /******************RETROFIT IMPLEMENTATION***********************/
        Call<WalletAuthentication> call = APIClient.getWalletInstance(context).restartInitiatePhoneAuth(phoneNumber,request_id,category,service_code,"resendPhoneAuthOTP");
        call.enqueue(new Callback<WalletAuthentication>() {
            @Override
            public void onResponse(Call<WalletAuthentication> call, Response<WalletAuthentication> response) {
                if(response.isSuccessful()){
                    dialogLoader.hideProgressDialog();

                    if(response.body().getStatus()==1){
                        if(response.body().getMessage().equalsIgnoreCase("Phone was already Verified")){
                            //navigate to signup
                            Bundle bundle = new Bundle();
                            bundle.putString("phone", "0" + binding.userMobile.getText().toString().trim() );
                            AuthActivity.navController.navigate(R.id.action_getStartedSignUpFragment_to_signUpFragment,bundle);

                        }else {
                            registerBroadcastReceiver();
                            timer.start();

                            //restart timer
                        }
                    }else{

                        Snackbar.make(context,getView(),response.body().getMessage(),Snackbar.LENGTH_SHORT).show();
                    }


                }else if (response.code() == 401) {
                    Toast.makeText(context,response.body().getMessage(),Toast.LENGTH_LONG).show();
                    dialogLoader.hideProgressDialog();

                }

            }

            @Override
            public void onFailure(Call<WalletAuthentication> call, Throwable t){
                dialogLoader.hideProgressDialog();
            }
        });

    }


    private void startSmsUserConsent() {
        SmsRetrieverClient client = SmsRetriever.getClient(context);
        //We can add sender phone number or leave it blank
        // I'm adding null here
        client.startSmsUserConsent(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Toast.makeText(context, "On Success", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(context, "On OnFailure", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getOtpFromMessage(String message) {
        // This will match any 6 digit number in the message
        Pattern pattern = Pattern.compile("(|^)\\d{6}");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String retrievedCode=matcher.group(0);
            if(retrievedCode.length()==6){
                code1.setText(retrievedCode.charAt(0)+"");
                code2.setText(retrievedCode.charAt(1)+"");
                code3.setText(retrievedCode.charAt(2)+"");
                code4.setText(retrievedCode.charAt(3)+"");
                code5.setText(retrievedCode.charAt(4)+"");
                code6.setText(retrievedCode.charAt(5)+"");

                String code = code1.getText().toString() + code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString();
                verifyVerificationCode(code);

            }

        }
    }

    private void registerBroadcastReceiver() {
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.smsBroadcastReceiverListener =
                new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, REQ_USER_CONSENT);
                    }
                    @Override
                    public void onFailure() {
                    }
                };
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        context.registerReceiver(smsBroadcastReceiver, intentFilter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_USER_CONSENT) {
            if ((resultCode == RESULT_OK) && (data != null)) {
                //That gives all message to us.
                // We need to get the code from inside with regex
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);

                //Toast.makeText(context, message, Toast.LENGTH_LONG).show();


                getOtpFromMessage(message);
            }
        }
    }

}
