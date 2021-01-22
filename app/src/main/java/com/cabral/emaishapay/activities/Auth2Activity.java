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
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.database.User_Info_DB;
import com.cabral.emaishapay.models.WalletAuthentication;
import com.cabral.emaishapay.models.user_model.UserData;
import com.cabral.emaishapay.models.user_model.UserDetails;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.cabral.emaishapay.network.Connectivity;
import com.cabral.emaishapay.network.StartAppRequests;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.venmo.android.pin.PinFragment;
import com.venmo.android.pin.PinFragmentConfiguration;
import com.venmo.android.pin.PinSaver;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Auth2Activity extends AppCompatActivity implements PinFragment.Listener {
    private static final String TAG = "TokenAuthActivity";
    static TextView errorTextView;
    private static String userFirstname;
    private static String userLastname;
    private static String village;
    private static String subCounty;
    private static String district;
    private Context context;
    private static String phonenumber;
    public  static int ACTION_CODE = 1;
    private static SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    User_Info_DB userInfoDB;
    DialogLoader dialogLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_auth2);

        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        userInfoDB = new User_Info_DB();

        errorTextView = findViewById(R.id.text_view_crop_user_error);
        context = Auth2Activity.this;
        dialogLoader = new DialogLoader(this);

        if ( Auth2Activity.ACTION_CODE == 1) {

            PinFragmentConfiguration pinConfig = new PinFragmentConfiguration(context)
                    .validator(submission -> {

                        String WalletPass = WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION + submission;

                        if (submission.length() < 4) {
                            Toast.makeText(context, "Enter PIN!", Toast.LENGTH_SHORT).show();

                        }else {
                            //login and get token
                            SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
                            checkLogin(WalletPass, phonenumber,  context, dialogLoader, sharedPreferences);


                        }
                        return WalletPass.equals(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_USER_PASSWORD, context));
                    });

            PinFragment toShow = PinFragment.newInstanceForVerification(pinConfig);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, toShow )
                    .commit();

        }else if ( Auth2Activity.ACTION_CODE == 2){

            PinFragmentConfiguration pinConfig = new PinFragmentConfiguration(context)
                    .validator(submission -> {

                        if (submission.length() < 4) {
                            Toast.makeText(context, "Enter PIN!", Toast.LENGTH_SHORT).show();
                        }
                        return submission.length() == 4;
                    }).pinSaver(new PinSaver(){
                        @Override
                        public void save(String pin) {
                            //submit registration details to server

                            String WalletPass = WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION + pin;
                            processRegistration( WalletPass,phonenumber, userFirstname, userLastname, village, subCounty, district,dialogLoader);

                        }
                    });

            PinFragment toShow = PinFragment.newInstanceForCreation(pinConfig);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, toShow )
                    .commit();
        }


    }


    public  void checkLogin(final String rawpassword,final String phoneNumber, final Context context, final   DialogLoader dialogLoader, SharedPreferences sharedPreferences) {

        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<WalletAuthentication> call = apiRequests.authenticate(phoneNumber, rawpassword);

        dialogLoader.showProgressDialog();
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
                        if(Connectivity.isConnected(context)){
                            TokenAuthActivity.getLoginToken(rawpassword, phoneNumber, context, dialogLoader);
                        }else{
                            Snackbar.make(errorTextView,getString(R.string.internet_connection_error),Snackbar.LENGTH_LONG).show();


                        }


                    } catch (Exception e) {
                        Log.e("response", response.toString());
                        e.printStackTrace();
                    } finally {
                        dialogLoader.hideProgressDialog();
                    }

                }
                else {
                    dialogLoader.hideProgressDialog();

                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    String message = response.body().getMessage();
                    //Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<WalletAuthentication> call, Throwable t) {
                Log.e("info2 : ", t.getMessage());
                dialogLoader.hideProgressDialog();
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
        editor.putString(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, userDetails.getId() + "");
        editor.putString(WalletHomeActivity.PREFERENCES_USER_EMAIL, userDetails.getEmail());
        editor.putString(WalletHomeActivity.PREFERENCES_FIRST_NAME, userDetails.getFirstname());
        editor.putString(WalletHomeActivity.PREFERENCES_LAST_NAME, userDetails.getLastname());
        editor.putString(WalletHomeActivity.PREFERENCES_PHONE_NUMBER, userDetails.getPhoneNumber());
        editor.putString(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_PIC, userDetails.getPictrure());
        editor.putString(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_PIC, userDetails.getPictrure());

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

        EmaishaPayApp.checkWalletAccount(userDetails.getEmail(), userDetails.getPhoneNumber());

        // Navigate back to MainActivity
        Intent i = new Intent(context, WalletHomeActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
    }


    private void processRegistration(String userPassword, String phoneNumber, String userFirstname, String userLastname, String village, String subCounty, String district, DialogLoader dialogLoader) {

        dialogLoader.showProgressDialog();

        RequestBody fName = RequestBody.create(MediaType.parse("text/plain"),userFirstname);
        RequestBody lName = RequestBody.create(MediaType.parse("text/plain"), userLastname);
        RequestBody customersTelephone = RequestBody.create(MediaType.parse("text/plain"), phoneNumber);
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), userPassword);
        RequestBody countryCode = RequestBody.create(MediaType.parse("text/plain"), getResources().getString(R.string.ugandan_code));
        RequestBody addressStreet = RequestBody.create(MediaType.parse("text/plain"), village);
        RequestBody addressCityOrTown = RequestBody.create(MediaType.parse("text/plain"), subCounty);
        RequestBody addressDistrict = RequestBody.create(MediaType.parse("text/plain"), district);

        Call<UserData> call = APIClient.getWalletInstance()
                .processRegistration(fName, lName, password, countryCode, customersTelephone, addressStreet, addressCityOrTown, addressDistrict);

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(@NotNull Call<UserData> call, @NotNull retrofit2.Response<UserData> response) {

                dialogLoader.hideProgressDialog();

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("1")) {

                        // Finish SignUpActivity to goto the LoginActivity
                        Intent authenticate = new Intent(context, Login.class);
                        context.startActivity(authenticate);
                        finish();

                    } else if (response.body().getStatus().equalsIgnoreCase("0")) {
                        // Get the Error Message from Response
                        String message = response.body().getMessage();
                        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();

                    } else {
                        // Unable to get Success status
                        Toast.makeText(getApplicationContext(), getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // Show the Error Message
                    String Str = response.message();
                    Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NotNull Call<UserData> call, @NotNull Throwable t) {
                dialogLoader.hideProgressDialog();
                String Str = "" + t;
                Toast.makeText(getApplicationContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void startAuth(Context context,String phonenumber,int ACTION_CODE) {
        Auth2Activity.phonenumber=phonenumber;
        Auth2Activity.ACTION_CODE=ACTION_CODE;
        Intent authenticate = new Intent(context, Auth2Activity.class);
        context.startActivity(authenticate);
    }

    public static void processFurtherRegistration(Context context,String phonenumber, String userFirstname, String userLastname, String village, String subCounty, String district,int ACTION_CODE) {
        Auth2Activity.phonenumber=phonenumber;
        Auth2Activity.userFirstname=userFirstname;
        Auth2Activity.userLastname=userLastname;
        Auth2Activity.village=village;
        Auth2Activity.subCounty=subCounty;
        Auth2Activity.district=district;
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