package com.cabral.emaishapay.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.database.User_Info_BuyInputsDB;
import com.cabral.emaishapay.network.APIClient;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;

import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.app.MyAppPrefsManager;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.models.user_model.UserData;
import com.cabral.emaishapay.models.user_model.UserDetails;
import com.cabral.emaishapay.network.StartAppRequests;
import com.cabral.emaishapay.utils.LocaleHelper;
import com.cabral.emaishapay.utils.ValidateInputs;

import java.util.concurrent.TimeUnit;

import am.appwise.components.ni.NoInternetDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Login activity handles User's Email, Facebook and Google Login
 **/


public class Login extends AppCompatActivity {
    private static final String TAG = "Login";

    View parentView;
    Toolbar toolbar;
    ActionBar actionBar;

    EditText user_email, user_password;
    TextView forgotPasswordText, signupText;
    Button loginBtn;


    User_Info_BuyInputsDB userInfoDB;
    DialogLoader dialogLoader;

    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
static final int RC_SIGN_IN = 100;

    private UserDetails userDetails;
    private Dialog dialogOTP;
    private FirebaseAuth mAuth;
    EditText ed_otp;
    private int TEMP_USER_TYPE;
    private static String user_current_phone_number;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(com.cabral.emaishapay.activities.Login.this).build();
        // noInternetDialog.show();

        mAuth = FirebaseAuth.getInstance();


        setContentView(R.layout.login);



        // Binding Layout Views
        user_email = findViewById(R.id.user_email);
        user_password = findViewById(R.id.user_password);
        loginBtn = findViewById(R.id.loginBtn);
        signupText = findViewById(R.id.login_signupText);
        forgotPasswordText = findViewById(R.id.forgot_password_text);

        parentView = signupText;


        dialogLoader = new DialogLoader(com.cabral.emaishapay.activities.Login.this);

        userInfoDB = new User_Info_BuyInputsDB();
        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);

        user_email.setText(sharedPreferences.getString("userEmail", null));


        forgotPasswordText.setOnClickListener(view -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(com.cabral.emaishapay.activities.Login.this, R.style.DialogFullscreen);
            View dialogView = getLayoutInflater().inflate(R.layout.buy_inputs_dialog_input, null);
            dialog.setView(dialogView);
            dialog.setCancelable(true);

            final Button dialog_button = dialogView.findViewById(R.id.dialog_button);
            final TextView dialog_forgottext = dialogView.findViewById(R.id.forgot_password_text);
            final EditText dialog_input = dialogView.findViewById(R.id.dialog_input);
            // final TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
            final ImageView dismiss_button = dialogView.findViewById(R.id.dismissButton);
            dialog_forgottext.setVisibility(View.VISIBLE);
            dialog_button.setText(getString(R.string.sendemail));
            //  dialog_title.setText(getString(R.string.forgot_your_password));

            final AlertDialog alertDialog = dialog.create();
            alertDialog.show();

            dismiss_button.setOnClickListener(v -> alertDialog.dismiss());

            dialog_button.setOnClickListener(v -> {
                if (ValidateInputs.isValidEmail(dialog_input.getText().toString().trim())) {
                    // Request for Password Reset
                    processForgotPassword(dialog_input.getText().toString());
                } else {
                    Snackbar.make(parentView, getString(R.string.invalid_email), Snackbar.LENGTH_LONG).show();
                }
                alertDialog.dismiss();
            });
        });

        signupText.setOnClickListener(v -> {
            // Navigate to SignUp Activity
            startActivity(new Intent(com.cabral.emaishapay.activities.Login.this, com.cabral.emaishapay.activities.SignUp.class));
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
        });

        loginBtn.setOnClickListener(v -> {
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

        Call<UserData> call = APIClient.getInstance()
                .processLogin
                        (
                                user_email.getText().toString().trim(),
                                user_password.getText().toString().trim()
                        );

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {

                dialogLoader.hideProgressDialog();

                if (response.isSuccessful()) {

                    if (response.body().getSuccess().equalsIgnoreCase("1") || response.body().getSuccess().equalsIgnoreCase("2")) {
                        // Get the User Details from Response
                        userDetails = response.body().getData().get(0);

                        Log.d(TAG, "onResponse: Email = " + userDetails.getEmail());
                        Log.d(TAG, "onResponse: First Name = " + userDetails.getFirstName());
                        Log.d(TAG, "onResponse: Last Name = " + userDetails.getLastName());
                        Log.d(TAG, "onResponse: Username = " + userDetails.getUserName());
                        Log.d(TAG, "onResponse: addressStreet = " + userDetails.getAddressStreet());
                        Log.d(TAG, "onResponse: addressCityOrTown = " + userDetails.getAddressCityOrTown());
                        Log.d(TAG, "onResponse: address_district = " + userDetails.getAddress_district());
                        Log.d(TAG, "onResponse: addressCountry = " + userDetails.getAddressCountry());

                        TEMP_USER_TYPE = 0; // 0 for Simple Login.
                        loginUser(userDetails);
                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        // Get the Error Message from Response
                        String message = response.body().getMessage();
                        Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(com.cabral.emaishapay.activities.Login.this, getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // Show the Error Message
                    Toast.makeText(com.cabral.emaishapay.activities.Login.this, response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(com.cabral.emaishapay.activities.Login.this, "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }



    private void showPhoneDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(com.cabral.emaishapay.activities.Login.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_phone, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        final Button okayButton = dialogView.findViewById(R.id.dialog_button);
        final EditText phoneEditText = dialogView.findViewById(R.id.dialog_input);
        if (!phoneEditText.getText().toString().isEmpty()) {
            phoneEditText.setText(phoneEditText.getText().toString());
        }

        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        okayButton.setOnClickListener(v -> {
            if (ValidateInputs.isValidPhoneNo(phoneEditText.getText().toString().trim())) {


                // Request for OTP
                sendOTP(getResources().getString(R.string.ugandan_code) + phoneEditText.getText().toString().trim());
                user_current_phone_number = getResources().getString(R.string.ugandan_code) + phoneEditText.getText().toString().trim();
            } else {
                Snackbar.make(parentView, getString(R.string.invalid_contact), Snackbar.LENGTH_LONG).show();
            }
            alertDialog.dismiss();
        });
    }

    private void sendOTP(String phoneNumber) {
        showOTPDialog(phoneNumber);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private void showOTPDialog(final String phoneNumber) {
        dialogOTP = new Dialog(com.cabral.emaishapay.activities.Login.this);
        dialogOTP.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogOTP.setCancelable(true);
        dialogOTP.setContentView(R.layout.dialog_otp);

        ed_otp = dialogOTP.findViewById(R.id.ed_otp);
        AppCompatButton btn_resend, btn_submit;
        btn_resend = dialogOTP.findViewById(R.id.btn_resend);
        btn_submit = dialogOTP.findViewById(R.id.btn_submit);

        btn_resend.setOnClickListener(view -> sendOTP(phoneNumber));
        btn_submit.setOnClickListener(view -> verifyVerificationCode(ed_otp.getText().toString().trim()));
        dialogOTP.show();
    }

    private String mVerificationId;
    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                ed_otp.setText(code);
                verifyVerificationCode(code);
            } else {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(com.cabral.emaishapay.activities.Login.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };

    private void verifyVerificationCode(String code) {
        if (code.isEmpty()) {
            Toast.makeText(this, "Invalid code.", Toast.LENGTH_SHORT).show();
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(com.cabral.emaishapay.activities.Login.this, task -> {
                    if (task.isSuccessful()) {
                        //verification successful we will start the profile activity
                        dialogOTP.dismiss();
                        //Final Proceed to login
                        switch (TEMP_USER_TYPE) {
                            case 0: // simple Login
                                loginUser(userDetails);
                                break;
                            case 1: // Gmail Login
                                loginGmailUser(userDetails);
                                break;
                        }

                    } else {
                        //verification unsuccessful.. display an error message
                        String message = "Something is wrong, we will fix it soon...";
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            message = "Invalid code entered...";
                        }
                        Toast.makeText(com.cabral.emaishapay.activities.Login.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginUser(UserDetails userDetails) {
        // Save User Data to Local Databases
        if (userInfoDB.getUserData(userDetails.getId()) != null) {
            // User already exists
            userInfoDB.updateUserData(userDetails);
        } else {
            // Insert Details of New User
            userInfoDB.insertUserData(userDetails);
        }
        Log.e("USERID", userDetails.getId());
        // Save necessary details in SharedPrefs
        editor = sharedPreferences.edit();
        editor.putString(WalletHomeActivity.PREFERENCES_USER_ID, userDetails.getId());
        editor.putString(WalletHomeActivity.PREFERENCES_USER_EMAIL, userDetails.getEmail());
        editor.putString(WalletHomeActivity.PREFERENCES_FIRST_NAME, userDetails.getFirstName());
        editor.putString(WalletHomeActivity.PREFERENCES_LAST_NAME, userDetails.getLastName());
        editor.putString(WalletHomeActivity.PREFERENCES_PHONE_NUMBER, userDetails.getPhone());
        editor.putString(WalletHomeActivity.USER_DEFAULT_ADDRESS_PREFERENCES_ID, userDetails.getDefaultAddressId());

        editor.putString("addressStreet", userDetails.getAddressStreet());
        editor.putString("addressCityOrTown", userDetails.getAddressCityOrTown());
        editor.putString("address_district", userDetails.getAddress_district());
        editor.putString("addressCountry", userDetails.getAddressCountry());

        editor.putBoolean("isLogged_in", true);
        editor.apply();
        WalletAuthActivity.WALLET_ACCESS_TOKEN=null;
        // Set UserLoggedIn in MyAppPrefsManager
        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(com.cabral.emaishapay.activities.Login.this);
        myAppPrefsManager.logInUser();

        // Set isLogged_in of ConstantValues
        ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();
        StartAppRequests.RegisterDeviceForFCM(com.cabral.emaishapay.activities.Login.this);
        EmaishaPayApp.checkWalletAccount(userDetails.getEmail(),userDetails.getPhone());
        // Navigate back to MainActivity
        Intent i = new Intent(com.cabral.emaishapay.activities.Login.this, WalletHomeActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
    }



    private void loginGmailUser(UserDetails userDetails) {
        // Save User Data to Local Databases
        if (userInfoDB.getUserData(userDetails.getId()) != null) {
            // User already exists
            userInfoDB.updateUserData(userDetails);
        } else {
            // Insert Details of New User
            userInfoDB.insertUserData(userDetails);
        }

        // Save necessary details in SharedPrefs
        editor = sharedPreferences.edit();
        editor.putString("userID", userDetails.getId());
        editor.putString("userEmail", userDetails.getEmail());
        editor.putString("userName", userDetails.getFirstName() + " " + userDetails.getLastName());
        editor.putString("userTelephone", user_current_phone_number);
        editor.putString("userDefaultAddressID", userDetails.getDefaultAddressId());
        editor.putBoolean("isLogged_in", true);
        editor.apply();

        // Set UserLoggedIn in MyAppPrefsManager
        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(com.cabral.emaishapay.activities.Login.this);
        myAppPrefsManager.logInUser();

        ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();

        StartAppRequests.RegisterDeviceForFCM(com.cabral.emaishapay.activities.Login.this);


        // Navigate back to MainActivity
        startActivity(new Intent(com.cabral.emaishapay.activities.Login.this, WalletHomeActivity.class));
        finish();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
    }



    private void processForgotPassword(String email) {

        dialogLoader.showProgressDialog();

        Call<UserData> call = APIClient.getInstance()
                .processForgotPassword
                        (
                                email
                        );

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {

                dialogLoader.hideProgressDialog();

                if (response.isSuccessful()) {
                    // Show the Response Message
                    String message = response.body().getMessage();
                    Snackbar.make(parentView, message, Snackbar.LENGTH_LONG).show();

                } else {
                    // Show the Error Message
                    Toast.makeText(com.cabral.emaishapay.activities.Login.this, response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(com.cabral.emaishapay.activities.Login.this, "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }


    //*********** Validate Login Form Inputs ********//

    private boolean validateLogin() {
        if (!ValidateInputs.isValidEmail(user_email.getText().toString().trim())) {
            user_email.setError(getString(R.string.invalid_email));
            return false;
        } else if (!ValidateInputs.isValidPassword(user_password.getText().toString().trim())) {
            user_password.setError(getString(R.string.invalid_password));
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
        // Navigate back to MainActivity
        //startActivity(new Intent(Login.this, WalletHomeActivity.class));
        finish();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                //startActivity(new Intent(Login.this, WalletHomeActivity.class));
                finish();
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

