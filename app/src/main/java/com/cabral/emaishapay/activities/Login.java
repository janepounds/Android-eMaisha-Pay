package com.cabral.emaishapay.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.databinding.LoginBinding;
import com.cabral.emaishapay.models.WalletAuthenticationResponse;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.google.android.material.snackbar.Snackbar;

import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.models.user_model.UserData;
import com.cabral.emaishapay.utils.LocaleHelper;
import com.cabral.emaishapay.utils.ValidateInputs;
import com.venmo.android.pin.PinFragment;

import org.jetbrains.annotations.NotNull;

import am.appwise.components.ni.NoInternetDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Login activity handles User's Email, Facebook and Google Login
 **/

public class Login extends AppCompatActivity implements PinFragment.Listener{
    private static final String TAG = "Login";
    private LoginBinding binding;

    DialogLoader dialogLoader;
    APIRequests apiRequests;
    private Dialog dialog;
    private Context context;
    private EditText code1,code2,code3,code4,code5,code6;
    private  String code, sms_code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=com.cabral.emaishapay.activities.Login.this;
        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(context).build();
        // noInternetDialog.show();
        binding = DataBindingUtil.setContentView(this, R.layout.login);
        apiRequests = APIClient.getWalletInstance();

        // Binding Layout Views
        dialogLoader = new DialogLoader(com.cabral.emaishapay.activities.Login.this);

//        binding.userEmail.setText(sharedPreferences.getString("userEmail", null));

        binding.forgotPasswordText.setOnClickListener(view -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.DialogFullscreen);
            View dialogView = getLayoutInflater().inflate(R.layout.buy_inputs_dialog_input, null);
            dialog.setView(dialogView);
            dialog.setCancelable(true);

            final Button dialog_button = dialogView.findViewById(R.id.dialog_button);
            final TextView dialog_forgotText = dialogView.findViewById(R.id.forgot_password_text);
            final EditText dialog_input = dialogView.findViewById(R.id.dialog_input);
            final ImageView dismiss_button = dialogView.findViewById(R.id.dismissButton);
            dialog_forgotText.setVisibility(View.VISIBLE);
            dialog_button.setText(getString(R.string.sendemail));

            final AlertDialog alertDialog = dialog.create();
            alertDialog.show();

            dismiss_button.setOnClickListener(v -> alertDialog.dismiss());

            dialog_button.setOnClickListener(v -> {
                if (ValidateInputs.isValidEmail(dialog_input.getText().toString().trim())) {
                    // Request for Password Reset
                    processForgotPassword(dialog_input.getText().toString());
                } else {
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.invalid_email), Snackbar.LENGTH_LONG).show();
                }
                alertDialog.dismiss();
            });
        });

        binding.loginSignupText.setOnClickListener(v -> {
            // Navigate to SignUp Activity
            startActivity(new Intent(com.cabral.emaishapay.activities.Login.this, com.cabral.emaishapay.activities.SignUp.class));
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
        });

        binding.loginBtn.setOnClickListener(v -> {
            // Validate Login Form Inputs
            boolean isValidData = validateLogin();

            if (isValidData) {
                // Proceed User Login
                processLogin("0"+binding.userPhone.getText().toString());
            }



        });
    }

    //*********** Proceed Login with User Email and Password ********//

    public void processLogin(String phonenumber) {
        //call the otp end point
        dialogLoader.showProgressDialog();
        Call<WalletAuthenticationResponse>call = apiRequests.authenticate(phonenumber);
        call.enqueue(new Callback<WalletAuthenticationResponse>() {
            @Override
            public void onResponse(Call<WalletAuthenticationResponse> call, Response<WalletAuthenticationResponse> response) {
                if(response.isSuccessful()){
                   sms_code =response.body().getData().getSms_code();

                    //call otp dialog
                    dialog  = new Dialog(context);
                    dialog.setContentView(R.layout.login_dialog_otp);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(false);



                    code1= dialog.findViewById(R.id.otp_code1_et);
                    code2= dialog.findViewById(R.id.otp_code2_et);
                    code3= dialog.findViewById(R.id.otp_code3_et);
                    code4= dialog.findViewById(R.id.otp_code4_et);
                    code5=dialog.findViewById(R.id.otp_code5_et);
                    code6= dialog.findViewById(R.id.otp_code6_et);
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
                validateEnteredCode(code1.getText().toString()+code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString(),sms_code);
            }
        });

                    dialog.findViewById(R.id.login_otp_resend_code).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                          //  processLogin("0"+binding.userPhone.getText().toString());
                        }
                    });
                    dialog.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            code = code1.getText().toString() + code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString();

                            validateEnteredCode(code,sms_code);
                        }
                    });
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes(); // change this to your dialog.

                    params.x = 100; // Here is the param to set your dialog position. Same with params.x
                    dialog.getWindow().setAttributes(params);
                    dialog.show();
                }else{

                }
                dialogLoader.hideProgressDialog();

            }

            @Override
            public void onFailure(Call<WalletAuthenticationResponse> call, Throwable t) {

            }
        });

    }
    private void validateEnteredCode(String code,String sent_code) {
        String phone ="0"+binding.userPhone.getText().toString();

        if (sent_code.equalsIgnoreCase(code)) {
            //fetch session data
            Auth2Activity.startAuth(context,phone,1);
        } else {
            Toast.makeText(context, "Enter valid code", Toast.LENGTH_LONG).show();
        }
    }

    private void processForgotPassword(String email) {
        dialogLoader.showProgressDialog();

        Call<UserData> call = apiRequests
                .processForgotPassword(email);

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(@NotNull Call<UserData> call, @NotNull Response<UserData> response) {

                dialogLoader.hideProgressDialog();

                if (response.isSuccessful()) {
                    // Show the Response Message
                    String message = response.body().getMessage();
                    Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();

                } else {
                    // Show the Error Message
                    Toast.makeText(com.cabral.emaishapay.activities.Login.this, response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserData> call, @NotNull Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(com.cabral.emaishapay.activities.Login.this, "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }


    private boolean validateLogin(){
        if(binding.userPhone.getText().toString().isEmpty()) {
            binding.userPhone.setError("Enter a phone number");
            return false;
        }else if(binding.userPhone.getText().toString().length()<9){
            binding.userPhone.setError("Enter valid phone number");
            return false;
        }else{
            return true;
        }

    }

    //*********** Set the Base Context for the ContextWrapper ********//
    @Override
    protected void attachBaseContext(Context newBase) {

        String languageCode = ConstantValues.LANGUAGE_CODE;
        if ("".equalsIgnoreCase(languageCode))
            languageCode = ConstantValues.LANGUAGE_CODE = "en";

        super.attachBaseContext(LocaleHelper.wrapLocale(newBase, languageCode));
    }

    //*********** Called when the Activity has detected the User pressed the Back key ********//

    @Override
    public void onBackPressed() {
        // Close app entirely
        finishAffinity();
    }

    @Override
    public void onValidated() {

    }

    @Override
    public void onPinCreated() {

    }
}

