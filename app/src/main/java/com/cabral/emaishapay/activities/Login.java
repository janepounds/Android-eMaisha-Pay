package com.cabral.emaishapay.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.database.User_Info_DB;
import com.cabral.emaishapay.databinding.LoginBinding;
import com.cabral.emaishapay.models.WalletAuthentication;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.google.android.material.snackbar.Snackbar;

import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.app.MyAppPrefsManager;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.models.user_model.UserData;
import com.cabral.emaishapay.network.StartAppRequests;
import com.cabral.emaishapay.utils.LocaleHelper;
import com.cabral.emaishapay.utils.ValidateInputs;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import am.appwise.components.ni.NoInternetDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Login activity handles User's Email, Facebook and Google Login
 **/

public class Login extends AppCompatActivity {
    private static final String TAG = "Login";
    private LoginBinding binding;

    User_Info_DB userInfoDB;
    DialogLoader dialogLoader;
    APIRequests apiRequests;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

    private WalletAuthentication.UserData userDetails;

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

        userInfoDB = new User_Info_DB();
        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);

        binding.userEmail.setText(sharedPreferences.getString("userEmail", null));

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
                processLogin();
            }
        });
    }

    //*********** Proceed Login with User Email and Password ********//

    private void processLogin() {
        dialogLoader.showProgressDialog();

        Call<WalletAuthentication> call = apiRequests.authenticate(binding.userEmail.getText().toString().trim(), binding.userPassword.getText().toString().trim());

        call.enqueue(new Callback<WalletAuthentication>() {
            @Override
            public void onResponse(@NotNull Call<WalletAuthentication> call, @NotNull Response<WalletAuthentication> response) {



                if (response.isSuccessful()) {

                    if (response.body().getStatus() == 1 || response.body().getStatus() == 2) {
                        // Get the User Details from Response
                        userDetails = response.body().getData();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, userDetails.getId()+"");
                        editor.apply();

                        Log.d(TAG, "onResponse: Email = " + userDetails.getEmail());
                        Log.d(TAG, "onResponse: First Name = " + userDetails.getFirstname());
                        Log.d(TAG, "onResponse: Last Name = " + userDetails.getLastname());
                        Log.d(TAG, "onResponse: Username = " + userDetails.getEmail());
                        Log.d(TAG, "onResponse: addressStreet = " + userDetails.getAddressStreet());
                        Log.d(TAG, "onResponse: addressCityOrTown = " + userDetails.getAddressCityOrTown());
                        Log.d(TAG, "onResponse: address_district = " + userDetails.getAddressCityOrTown());

                        loginUser(userDetails, binding.userPassword.getText().toString().trim());
                        WalletAuthActivity.getLoginToken(binding.userPassword.getText().toString().trim(), userDetails.getPhoneNumber(), Login.this,null);
                    } else if (response.body().getStatus() == 0) {
                        // Get the Error Message from Response
                        String message = response.body().getMessage();
                        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // Show the Error Message
                    if (response.message().equals("Bad Request")) {
                        Snackbar.make(findViewById(android.R.id.content), "Incorrect email or password", Snackbar.LENGTH_LONG).show();
                    }
                }
                dialogLoader.hideProgressDialog();
            }

            @Override
            public void onFailure(@NotNull Call<WalletAuthentication> call, @NotNull Throwable t) {
                dialogLoader.hideProgressDialog();
                Snackbar.make(findViewById(android.R.id.content), "Unexpected error, please try again later", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void loginUser(WalletAuthentication.UserData userDetails, String password) {
        // Save User Data to Local Databases
        if (userInfoDB.getUserData(userDetails.getId() + "") != null) {
            // User already exists
            userInfoDB.updateUserData(userDetails, password);
        } else {
            // Insert Details of New User
            userInfoDB.insertUserData(userDetails, password);
        }
        Log.e(TAG, userDetails.getId() + "");
        // Save necessary details in SharedPrefs
        editor = sharedPreferences.edit();
        editor.putString(WalletHomeActivity.PREFERENCES_USER_ID, userDetails.getId() + "");
        editor.putString(WalletHomeActivity.PREFERENCES_USER_EMAIL, userDetails.getEmail());
        editor.putString(WalletHomeActivity.PREFERENCES_FIRST_NAME, userDetails.getFirstname());
        editor.putString(WalletHomeActivity.PREFERENCES_LAST_NAME, userDetails.getLastname());
        editor.putString(WalletHomeActivity.PREFERENCES_PHONE_NUMBER, userDetails.getPhoneNumber());

        editor.putString("addressStreet", userDetails.getAddressStreet());
        editor.putString("addressCityOrTown", userDetails.getAddressCityOrTown());
        editor.putString("address_district", userDetails.getAddressCityOrTown());
        editor.putString("addressCountry", userDetails.getAddressCityOrTown());

        editor.putBoolean("isLogged_in", true);
        editor.apply();
        WalletAuthActivity.WALLET_ACCESS_TOKEN = null;

        // Set UserLoggedIn in MyAppPrefsManager
        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(this);
        myAppPrefsManager.logInUser();

        // Set isLogged_in of ConstantValues
        ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();
        StartAppRequests.RegisterDeviceForFCM(this);
        EmaishaPayApp.checkWalletAccount(userDetails.getEmail(), userDetails.getPhoneNumber());

        // Navigate back to MainActivity
        Intent i = new Intent(com.cabral.emaishapay.activities.Login.this, WalletHomeActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
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

    //*********** Validate Login Form Inputs ********//

    private boolean validateLogin() {
        if (!ValidateInputs.isValidEmail(binding.userEmail.getText().toString().trim())) {
            binding.userEmail.setError(getString(R.string.invalid_email));
            return false;
        } else if (!ValidateInputs.isValidPassword(binding.userPassword.getText().toString().trim())) {
            binding.userPassword.setError(getString(R.string.invalid_password));
            return false;
        } else {
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
}

