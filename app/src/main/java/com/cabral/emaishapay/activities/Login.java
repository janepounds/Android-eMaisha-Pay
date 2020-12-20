package com.cabral.emaishapay.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cabral.emaishapay.DailogFragments.LoginOtpDialog;
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
;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(com.cabral.emaishapay.activities.Login.this).build();
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
                processLogin(binding.userPhone.getText().toString());
            }



        });
    }

    //*********** Proceed Login with User Email and Password ********//

    private void processLogin(String phonenumber) {
        //call the otp end point
        dialogLoader.showProgressDialog();
        Call<WalletAuthenticationResponse>call = apiRequests.authenticate(phonenumber);
        call.enqueue(new Callback<WalletAuthenticationResponse>() {
            @Override
            public void onResponse(Call<WalletAuthenticationResponse> call, Response<WalletAuthenticationResponse> response) {
                if(response.isSuccessful()){

                    //call otp dialog
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Fragment prev = fm.findFragmentByTag("dialog");
                    if (prev != null) {
                        ft.remove(prev);
                    }

                    ft.addToBackStack(null);
                    Bundle bundle = new Bundle();
                    bundle.putString("sms_code",response.body().getData().getSms_code());
                    // Create and show the dialog.
                    DialogFragment payLoandialog = new LoginOtpDialog(Login.this,fm,phonenumber);
                    payLoandialog.setArguments(bundle);
                    payLoandialog.show(ft, "dialog");
                }else{

                }
                dialogLoader.hideProgressDialog();

            }

            @Override
            public void onFailure(Call<WalletAuthenticationResponse> call, Throwable t) {

            }
        });

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
        }else if(binding.userPhone.getText().toString().length()<10){
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

