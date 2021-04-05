package com.cabral.emaishapay.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.app.MyAppPrefsManager;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.database.User_Info_DB;
import com.cabral.emaishapay.models.WalletAuthentication;
import com.cabral.emaishapay.models.WalletAuthenticationResponse;
import com.cabral.emaishapay.models.user_model.UserData;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.venmo.android.pin.PinFragment;
import com.venmo.android.pin.PinFragmentConfiguration;
import com.venmo.android.pin.PinSaver;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.cabral.emaishapay.activities.WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE;
import static com.cabral.emaishapay.app.EmaishaPayApp.getContext;

public class ConfirmActivity extends AppCompatActivity implements PinFragment.Listener {
    private static final String TAG = "TokenAuthActivity";
    static TextView errorTextView;
    private static String userFirstname;
    private static String userLastname;
    private static String village;
    private static String subCounty;
    private static String district;
    private static String idType;
    private static String idNo;
    private static String firstSecurityQn;
    private static String secondSecurityQn;
    private static String thirdSecurityQn;
    private static String firstQnAnswer;
    private static String secondQnAnswer;
    private static String thirdQnAnswer;
    private Context context;
    private static String phonenumber;
    public  static int ACTION_CODE = 1;
    private static SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    User_Info_DB userInfoDB;
    DialogLoader dialogLoader;
    private Dialog otpDialog;

    private EditText code1,code2,code3,code4,code5,code6;
    TextView resendtxtview;
    APIRequests apiRequests;
    private  String otp_code, sms_code,smsResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_auth2);

        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        userInfoDB = new User_Info_DB();

        errorTextView = findViewById(R.id.text_view_crop_user_error);
        context = ConfirmActivity.this;
        dialogLoader = new DialogLoader(this);
        apiRequests = APIClient.getWalletInstance(getContext());

        if ( ConfirmActivity.ACTION_CODE == 1) {

            PinFragmentConfiguration pinConfig = new PinFragmentConfiguration(context)
                    .validator(submission -> {

                        String WalletPass = WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION + submission;

                        if (submission.length() < 4) {
                            Toast.makeText(context, "Enter PIN!", Toast.LENGTH_SHORT).show();

                        }else {
                            //Initialise Login Process
                            processLogin(WalletPass, ConfirmActivity.phonenumber);

                        }
                        return WalletPass.equals(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_USER_PASSWORD, context));
                    });

            PinFragment toShow = PinFragment.newInstanceForVerification(pinConfig);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, toShow )
                    .commit();

        }
        else if ( ConfirmActivity.ACTION_CODE == 2){

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
                            processRegistration( WalletPass,phonenumber, userFirstname, userLastname, village, subCounty, district,idType,idNo,firstSecurityQn,secondSecurityQn,thirdSecurityQn,firstQnAnswer,secondQnAnswer,thirdQnAnswer,dialogLoader);

                        }
                    });

            PinFragment toShow = PinFragment.newInstanceForCreation(pinConfig);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, toShow )
                    .commit();
        }


    }

    private void getOTPFromUser(String password) {
        otpDialog  = new Dialog(context,R.style.myFullscreenAlertDialogStyle);
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
                    confirmLogin(password,ConfirmActivity.phonenumber,otp_code,otpDialog);
                }

            }
        });

        otpDialog.findViewById(R.id.login_otp_resend_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call resend otp
                resendOtp(password,ConfirmActivity.phonenumber);

//                processLogin(password,ConfirmActivity.phonenumber);
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
        otpDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams params = otpDialog.getWindow().getAttributes(); // change this to your otpDialog.

        params.x = 100; // Here is the param to set your dialog position. Same with params.x
        otpDialog.getWindow().setAttributes(params);
        otpDialog.show();
    }

    public void processLogin(String password, String phonenumber) {
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);
        Log.d(TAG, "processLogin: request_id"+request_id);

        //call the otp end point
        dialogLoader.showProgressDialog();
        Call<WalletAuthenticationResponse>call = apiRequests.authenticate(phonenumber,password,request_id,category,"initiateUserLogin");
        call.enqueue(new Callback<WalletAuthenticationResponse>() {
            @Override
            public void onResponse(Call<WalletAuthenticationResponse> call, Response<WalletAuthenticationResponse> response) {
                if(response.isSuccessful() && response.body().getStatus()==1 ){
                    smsResults =response.body().getData().getSms_results();

                    //Call the OTP Dialog
                    getOTPFromUser(password);
                }else{
                    Snackbar.make(errorTextView,response.body().getMessage(),Snackbar.LENGTH_LONG).show();
                }
                dialogLoader.hideProgressDialog();

            }

            @Override
            public void onFailure(Call<WalletAuthenticationResponse> call, Throwable t) {
                Snackbar.make(errorTextView,getString(R.string.error_occured),Snackbar.LENGTH_LONG).show();
                dialogLoader.hideProgressDialog();
            }
        });

    }

    public void resendOtp(String password, String phonenumber) {
        String request_id = WalletHomeActivity.generateRequestId();
        Log.d(TAG, "processLogin: request_id"+request_id);

        //call the otp end point
        dialogLoader.showProgressDialog();
        Call<WalletAuthenticationResponse>call = apiRequests.resendOtp(phonenumber,password,request_id,"ResendOTP");
        call.enqueue(new Callback<WalletAuthenticationResponse>() {
            @Override
            public void onResponse(Call<WalletAuthenticationResponse> call, Response<WalletAuthenticationResponse> response) {
                if(response.isSuccessful() && response.body().getStatus()==1 ) {
                    smsResults = response.body().getData().getSms_results();

                    //Call the OTP Dialog
                    getOTPFromUser(password);
                }
                else{
                    Snackbar.make(errorTextView,response.body().getMessage(),Snackbar.LENGTH_LONG).show();
                }
                dialogLoader.hideProgressDialog();

            }

            @Override
            public void onFailure(Call<WalletAuthenticationResponse> call, Throwable t) {
                Snackbar.make(errorTextView,getString(R.string.error_occured),Snackbar.LENGTH_LONG).show();
                dialogLoader.hideProgressDialog();
            }
        });

    }

    public  void confirmLogin(final String rawpassword, final String phoneNumber, final String otp, Dialog otpDialog) {
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);
        Log.d(TAG, "processLogin: request_id"+request_id);
        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<WalletAuthentication> call = apiRequests.confirmLogin(phoneNumber,otp, rawpassword,request_id,category,"comfirmUserLogin");

        dialogLoader.showProgressDialog();
        call.enqueue(new Callback<WalletAuthentication>() {
            @Override
            public void onResponse(@NotNull Call<WalletAuthentication> call, @NotNull Response<WalletAuthentication> response) {
                if (response.code() == 200) {

                    if (response.body().getStatus() == 0) {
                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        if (dialogLoader != null)
                            dialogLoader.hideProgressDialog();

                    }
                    else if(response.body().getStatus() == 1){
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
                            otpDialog.dismiss();
                            loginUser(userDetails, rawpassword);

                            Log.w("WALLET_ID", WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context));

                            String accessToken = response.body().getAccess_token();
                            String accountRole = userDetails.getAccountRole();
                            Log.d(TAG, accessToken);
                            WalletHomeActivity.WALLET_ACCESS_TOKEN = accessToken;
                            WalletHomeActivity.savePreferences(PREFERENCES_WALLET_ACCOUNT_ROLE, accountRole, context);
                            if (dialogLoader != null)
                                dialogLoader.hideProgressDialog();
                            WalletHomeActivity.startHome(context);


                        } catch (Exception e) {
                            Log.e("response", response.toString());
                            e.printStackTrace();
                        } finally {
                            dialogLoader.hideProgressDialog();
                        }
                    }



                }
                else {
                    dialogLoader.hideProgressDialog();

                    //Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    String message = response.body().getMessage();
                    Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
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
        editor.putString(WalletHomeActivity.PREFERENCES_USER_BALANCE, userDetails.getBalance());
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
        WalletHomeActivity.WALLET_ACCESS_TOKEN = null;

        // Set UserLoggedIn in MyAppPrefsManager
        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(this);
        myAppPrefsManager.logInUser();

        // Set isLogged_in of ConstantValues
        ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();


        // Navigate back to MainActivity
        Intent i = new Intent(context, WalletHomeActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
    }


    private void processRegistration(String userPassword, String phoneNumber, String userFirstname, String userLastname, String village, String subCounty, String district,String idType,String idNo,String firstSecurityQn,String secondSecurityQn,String thirdSecurityQn,String firstQnAnswer,String secondQnAnswer,String thirdQnAnswer, DialogLoader dialogLoader) {
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);
        String action_id = "storeUser";
        dialogLoader.showProgressDialog();

        String countryCode = getResources().getString(R.string.ugandan_code);
        Call<UserData> call = APIClient.getWalletInstance(getContext())
                .processRegistration(userFirstname, userLastname, userPassword, countryCode, phoneNumber, village, subCounty, district,idType,idNo,firstSecurityQn,secondSecurityQn,thirdSecurityQn,firstQnAnswer,secondQnAnswer,thirdQnAnswer,request_id,category,action_id);

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(@NotNull Call<UserData> call, @NotNull retrofit2.Response<UserData> response) {

                dialogLoader.hideProgressDialog();

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("1")) {

                        // Finish SignUpActivity to goto the LoginActivity
                       // Intent authenticate = new Intent(context, Login.class);
                        //context.startActivity(authenticate);
                        //finish();

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
        ConfirmActivity.phonenumber=phonenumber;
        ConfirmActivity.ACTION_CODE=ACTION_CODE;
        Intent authenticate = new Intent(context, ConfirmActivity.class);
        context.startActivity(authenticate);
    }

    public static void processFurtherRegistration(Context context,String phonenumber, String userFirstname, String userLastname, String village, String subCounty, String district,String idType,String idNo,String firstSecurityQn,String secondSecurityQn,String thirdSecurityQn,String firstQnAnswer,String secondQnAnswer,String thirdQnAnswer,int ACTION_CODE) {
        ConfirmActivity.phonenumber=phonenumber;
        ConfirmActivity.userFirstname=userFirstname;
        ConfirmActivity.userLastname=userLastname;
        ConfirmActivity.village=village;
        ConfirmActivity.subCounty=subCounty;
        ConfirmActivity.district=district;
        ConfirmActivity.idType=idType;
        ConfirmActivity.idNo=idNo;
        ConfirmActivity.firstSecurityQn=firstSecurityQn;
        ConfirmActivity.secondSecurityQn=secondSecurityQn;
        ConfirmActivity.thirdSecurityQn=thirdSecurityQn;
        ConfirmActivity.firstQnAnswer=firstQnAnswer;
        ConfirmActivity.secondQnAnswer=secondQnAnswer;
        ConfirmActivity.thirdQnAnswer=thirdQnAnswer;
        ConfirmActivity.ACTION_CODE=ACTION_CODE;
        Intent authenticate = new Intent(context, ConfirmActivity.class);
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