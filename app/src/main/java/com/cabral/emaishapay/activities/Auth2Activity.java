package com.cabral.emaishapay.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.app.MyAppPrefsManager;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.database.User_Info_DB;
import com.cabral.emaishapay.models.WalletAuthentication;
import com.cabral.emaishapay.models.user_model.UserDetails;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.cabral.emaishapay.network.StartAppRequests;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.venmo.android.pin.PinFragment;
import com.venmo.android.pin.PinFragmentConfiguration;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Auth2Activity extends AppCompatActivity implements PinFragment.Listener {
    private static final String TAG = "TokenAuthActivity";
    static TextView errorTextView;
    private Context context;
    private static String phonenumber;
    public  static int ACTION_CODE = 1;
    private static SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    User_Info_DB userInfoDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_auth2);

        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        userInfoDB = new User_Info_DB();

        errorTextView = findViewById(R.id.text_view_crop_user_error);
        context = Auth2Activity.this;

        ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);


        if ( Auth2Activity.ACTION_CODE == 1) {

            PinFragmentConfiguration pinConfig = new PinFragmentConfiguration(context)
                    .validator(submission -> {

                        String WalletPass = WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION + submission;

                        if (submission.length() < 4) {
                            Toast.makeText(context, "Enter PIN!", Toast.LENGTH_SHORT).show();
                        } else {
                            //login and get token
                            SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
                            checkLogin(WalletPass, phonenumber,  context, dialog, sharedPreferences);


                        }
                        return WalletPass.equals(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_USER_PASSWORD, context));
                        // return submission.equals(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_USER_PIN, context)); // ...check against where you saved the pin

                    });

            PinFragment toShow = PinFragment.newInstanceForVerification(pinConfig);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, toShow )
                    .commit();

        }


    }


    public  void checkLogin(final String rawpassword,final String phoneNumber, final Context context, final ProgressDialog dialog, SharedPreferences sharedPreferences) {

        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<WalletAuthentication> call = apiRequests.authenticate(phoneNumber, rawpassword);

        dialog.show();
        call.enqueue(new Callback<WalletAuthentication>() {
            @Override
            public void onResponse(@NotNull Call<WalletAuthentication> call, @NotNull Response<WalletAuthentication> response) {
                if (response.code() == 200) {
                    try {
                        Gson gson = new Gson();
                        String user = gson.toJson(response.body().getData());
                        JSONObject userobject = new JSONObject(user);
                        //userobject.getInt("id")
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, userobject.getString("id"));
                        editor.apply();

                        WalletAuthentication.UserData userDetails = response.body().getData();
                        Log.d(TAG, "onResponse: Email = " + userDetails.getEmail());
                        Log.d(TAG, "onResponse: First Name = " + userDetails.getFirstname());
                        Log.d(TAG, "onResponse: Last Name = " + userDetails.getLastname());
                        Log.d(TAG, "onResponse: Username = " + userDetails.getEmail());
                        Log.d(TAG, "onResponse: addressStreet = " + userDetails.getAddressStreet());
                        Log.d(TAG, "onResponse: addressCityOrTown = " + userDetails.getAddressCityOrTown());
                        Log.d(TAG, "onResponse: address_district = " + userDetails.getAddressCityOrTown());

                        loginUser(userDetails, rawpassword);

                        Log.w("WALLET_ID", WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context));
                        TokenAuthActivity.getLoginToken(rawpassword, phoneNumber, context, dialog);

                    } catch (Exception e) {
                        Log.e("response", response.toString());
                        e.printStackTrace();
                    } finally {
                        dialog.dismiss();
                    }
                } else {
                    dialog.dismiss();
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    String message = response.body().getMessage();
                    //Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<WalletAuthentication> call, Throwable t) {
                Log.e("info2 : ", t.getMessage());
                dialog.dismiss();
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
        TokenAuthActivity.WALLET_ACCESS_TOKEN = null;

        // Set UserLoggedIn in MyAppPrefsManager
        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(this);
        myAppPrefsManager.logInUser();

        // Set isLogged_in of ConstantValues
        ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();
        StartAppRequests.RegisterDeviceForFCM(this);
        EmaishaPayApp.checkWalletAccount(userDetails.getEmail(), userDetails.getPhoneNumber());

        // Navigate back to MainActivity
        Intent i = new Intent(context, WalletHomeActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
    }


    public static void startAuth(Context context,String phonenumber,int ACTION_CODE) {
        Auth2Activity.phonenumber=phonenumber;
        Auth2Activity.ACTION_CODE=ACTION_CODE;
        Intent authenticate = new Intent(context, Auth2Activity.class);
        context.startActivity(authenticate);
    }

    @Override
    public void onValidated() {
        Log.w(TAG, "Pin validated");
    }

    @Override
    public void onPinCreated() {
        Log.w(TAG, "Pin created");
    }

}