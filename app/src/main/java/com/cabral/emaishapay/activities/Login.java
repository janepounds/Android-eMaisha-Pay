package com.cabral.emaishapay.activities;

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
    private Context context;

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
            final Button dismiss_button = dialogView.findViewById(R.id.btn_cancel_security_qn);
            final TextView dialog_title = dialogView.findViewById(R.id.title_text);
//            dialog_title.setText("Forgot Password");
//            dialog_forgotText.setVisibility(View.VISIBLE);
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
                String phone ="0"+binding.userPhone.getText().toString();
                ConfirmActivity.startAuth(context,phone,1);
            }



        });
    }


    private void processForgotPassword(String email) {
        dialogLoader.showProgressDialog();
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);
        Call<UserData> call = apiRequests
                .processForgotPassword(access_token,email,request_id,category,"");

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

