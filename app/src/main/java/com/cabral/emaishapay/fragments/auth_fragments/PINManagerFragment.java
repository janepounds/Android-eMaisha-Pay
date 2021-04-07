package com.cabral.emaishapay.fragments.auth_fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.app.MyAppPrefsManager;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.database.User_Info_DB;
import com.cabral.emaishapay.databinding.FragmentTokenAuthBinding;
import com.cabral.emaishapay.models.WalletAuthentication;
import com.cabral.emaishapay.models.WalletAuthenticationResponse;
import com.cabral.emaishapay.models.user_model.UserData;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.cabral.emaishapay.activities.WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE;

//This fragment is used for creating or picking a user's PIN and Continue with Login or SignUp processes.
public class PINManagerFragment  extends Fragment implements View.OnClickListener{

    private static final String TAG = "TokenAuthFragment";
    private Context context;
    private  String pin="", pin1="",phonenumber,otp_code,smsResults;
    static FragmentTokenAuthBinding binding;

    private static SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    User_Info_DB userInfoDB;
    DialogLoader dialogLoader;
    APIRequests apiRequests;
    private Dialog otpDialog;


    public static int ACTION;
    private SparseArray<String> keyValues = new SparseArray<>();
    private static InputConnection inputConnection;

    private String userFirstname, userLastname, village, subCounty, district,idType,idNo,firstSecurityQn,secondSecurityQn,thirdSecurityQn,firstQnAnswer,secondQnAnswer,thirdQnAnswer;

    @Override
    public void onAttach(@NonNull Context context) {
        this.context=context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_token_auth, container, false);

        keyValues.put(R.id.tv_key_0, "0");
        keyValues.put(R.id.tv_key_1, "1");
        keyValues.put(R.id.tv_key_2, "2");
        keyValues.put(R.id.tv_key_3, "3");
        keyValues.put(R.id.tv_key_4, "4");
        keyValues.put(R.id.tv_key_5, "5");
        keyValues.put(R.id.tv_key_6, "6");
        keyValues.put(R.id.tv_key_7, "7");
        keyValues.put(R.id.tv_key_8, "8");
        keyValues.put(R.id.tv_key_9, "9");

        sharedPreferences = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        userInfoDB = new User_Info_DB();
        dialogLoader = new DialogLoader(context);
        apiRequests = APIClient.getWalletInstance(getContext());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(getArguments()!=null){
            ACTION=getArguments().getInt("action");
            phonenumber=getArguments().getString("phone");
            if(ACTION==2){
                binding.pinTitle.setText(getString(R.string.create_pin));
                userFirstname=getArguments().getString("userFirstname");
                userLastname=getArguments().getString("userLastname");
                village=getArguments().getString("village");
                subCounty=getArguments().getString("subCounty");
                district=getArguments().getString("district");
                idType=getArguments().getString("idType");
                idNo=getArguments().getString("idNo");
                firstSecurityQn=getArguments().getString("firstSecurityQn");
                secondSecurityQn=getArguments().getString("secondSecurityQn");
                thirdSecurityQn=getArguments().getString("thirdSecurityQn");
                firstQnAnswer=getArguments().getString("firstQnAnswer");
                secondQnAnswer=getArguments().getString("secondQnAnswer");
                thirdQnAnswer=getArguments().getString("thirdQnAnswer");

            }
            else{
                binding.pinTitle.setText(getString(R.string.enter_pin));
            }
        }


        binding.tvKey0.setOnClickListener(this);
        binding.tvKey1.setOnClickListener(this);
        binding.tvKey2.setOnClickListener(this);
        binding.tvKey3.setOnClickListener(this);
        binding.tvKey4.setOnClickListener(this);
        binding.tvKey5.setOnClickListener(this);
        binding.tvKey6.setOnClickListener(this);
        binding.tvKey7.setOnClickListener(this);
        binding.tvKey8.setOnClickListener(this);
        binding.tvKey9.setOnClickListener(this);
        binding.tvKeyBackspace.setOnClickListener(this);
        binding.tvKeyClear.setOnClickListener(this);

        binding.pinCode1Edt.setRawInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        binding.pinCode1Edt.setTextIsSelectable(true);
        setInputConnection(binding.pinCode1Edt);
        binding.pinCode1Edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.pinCode2Edt.requestFocus();
            }
        });


        binding.pinCode2Edt.setRawInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        binding.pinCode2Edt.setTextIsSelectable(true);
        binding.pinCode2Edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.pinCode3Edt.requestFocus();
            }
        });

        binding.pinCode3Edt.setRawInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        binding.pinCode3Edt.setTextIsSelectable(true);
        binding.pinCode3Edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.pinCode4Edt.requestFocus();
            }
        });

        binding.pinCode4Edt.setRawInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        binding.pinCode4Edt.setTextIsSelectable(true);
        binding.pinCode4Edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                pin = binding.pinCode1Edt.getText().toString() + binding.pinCode2Edt.getText().toString() + binding.pinCode3Edt.getText().toString() + binding.pinCode4Edt.getText().toString();
                pin = pin.replaceAll("\\s+", "");
                if (pin.length() >= 4) {
                    //if Action 1 login , if 2 proceed with registration
                    if(ACTION==1){
                        String WalletPass = WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION + pin;
                        initiateLoginProcess(WalletPass,phonenumber);
                    }else if(ACTION==2){
                        if(pin1.length()==0){
                            pin1=pin;
                            binding.pinTitle.setText(getString(R.string.comfirm_pin));
                            clearPin(binding);
                        }else if(pin1.length()==4 && pin.equals(pin1)){

                            String WalletPass = WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION + pin;
                            processRegistration( WalletPass,phonenumber, userFirstname, userLastname, village, subCounty, district,idType,idNo,firstSecurityQn,secondSecurityQn,thirdSecurityQn,firstQnAnswer,secondQnAnswer,thirdQnAnswer);

                        }

                    }

                }
                else {
                    Toast.makeText(context, "Enter PIN!", Toast.LENGTH_SHORT).show();

                }

            }

        });

    }

    @Override
    public void onClick(View v) {
        if (inputConnection == null)
            return;

        if (v.getId() == R.id.tv_key_backspace) {
            pin = binding.pinCode1Edt.getText().toString() + binding.pinCode2Edt.getText().toString() + binding.pinCode3Edt.getText().toString() + binding.pinCode4Edt.getText().toString();
            pin = pin.replaceAll("\\s+", "");

            switch (pin.length()){
                case 1:
                    binding.pinCode1Edt.setText("");
                    setInputConnection( binding.pinCode1Edt);
                    break;
                case 2:
                    binding.pinCode2Edt.setText("");
                    setInputConnection( binding.pinCode2Edt);
                    break;
                case 3:
                    binding.pinCode3Edt.setText("");
                    setInputConnection( binding.pinCode3Edt);
                    break;
            }
        }
        else if(v.getId() == R.id.tv_key_clear){
            clearPin(binding);
        }
        else {
            String value = keyValues.get(v.getId()).toString();
            inputConnection.commitText(value, 1);

            pin = binding.pinCode1Edt.getText().toString() + binding.pinCode2Edt.getText().toString() + binding.pinCode3Edt.getText().toString() + binding.pinCode4Edt.getText().toString();
            pin = pin.replaceAll("\\s+", "");

            switch (pin.length()){
                case 0:
                    setInputConnection( binding.pinCode1Edt);
                    break;
                case 1:
                    setInputConnection( binding.pinCode2Edt);
                    break;
                case 2:
                    setInputConnection( binding.pinCode3Edt);
                    break;
                case 3:
                    setInputConnection( binding.pinCode4Edt);
                    break;
            }
        }



    }

    private static void  clearPin(FragmentTokenAuthBinding binding){
        binding.pinCode1Edt.setText("");
        binding.pinCode2Edt.setText("");
        binding.pinCode3Edt.setText("");
        binding.pinCode4Edt.setText("");

        InputConnection ic = binding.pinCode1Edt.onCreateInputConnection(new EditorInfo());
        inputConnection = ic;
    }

    public void setInputConnection(EditText editText) {
        InputConnection ic = editText.onCreateInputConnection(new EditorInfo());
        inputConnection = ic;
    }

    private void getLogInOTPFromUser(String password) {
        otpDialog  = new Dialog(context,R.style.myFullscreenAlertDialogStyle);
        otpDialog.setContentView(R.layout.login_dialog_otp);
        otpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        otpDialog.setCancelable(false);

        EditText code1= otpDialog.findViewById(R.id.otp_code1_et);
        EditText code2= otpDialog.findViewById(R.id.otp_code2_et);
        EditText code3= otpDialog.findViewById(R.id.otp_code3_et);
        EditText code4= otpDialog.findViewById(R.id.otp_code4_et);
        EditText code5=otpDialog.findViewById(R.id.otp_code5_et);
        EditText code6= otpDialog.findViewById(R.id.otp_code6_et);
        TextView resendtxtview= otpDialog.findViewById(R.id.login_otp_resend_code);
        TextView tvTimer= otpDialog.findViewById(R.id.tv_timer);
        RelativeLayout layoutResendCode= otpDialog.findViewById(R.id.layout_resend_code);

        CountDownTimer timer = new CountDownTimer(90000, 1000) {

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
                    confirmLogin(password, phonenumber,otp_code,otpDialog);
                }

            }
        });

        otpDialog.findViewById(R.id.login_otp_resend_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call resend otp
                resendOtp(password,phonenumber);

                layoutResendCode.setVisibility(View.GONE);
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

    public void initiateLoginProcess(String password, String phonenumber) {
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);
        Log.d(TAG, "processLogin: request_id"+request_id);

        //call the otp end point
        dialogLoader.showProgressDialog();
        Call<WalletAuthenticationResponse> call = apiRequests.authenticate(phonenumber,password,request_id,category,"initiateUserLogin");
        call.enqueue(new Callback<WalletAuthenticationResponse>() {
            @Override
            public void onResponse(Call<WalletAuthenticationResponse> call, Response<WalletAuthenticationResponse> response) {
                if(response.isSuccessful() && response.body().getStatus()==1 ){
                    smsResults =response.body().getData().getSms_results();

                    //Call the OTP Dialog
                    getLogInOTPFromUser(password);
                }else{
                    Snackbar.make(binding.textForgotPin,response.body().getMessage(),Snackbar.LENGTH_LONG).show();
                }
                dialogLoader.hideProgressDialog();

            }

            @Override
            public void onFailure(Call<WalletAuthenticationResponse> call, Throwable t) {
                Snackbar.make(binding.textForgotPin,getString(R.string.error_occured),Snackbar.LENGTH_LONG).show();
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
                    getLogInOTPFromUser(password);
                }
                else{
                    Snackbar.make(binding.textForgotPin,response.body().getMessage(),Snackbar.LENGTH_LONG).show();
                }
                dialogLoader.hideProgressDialog();

            }

            @Override
            public void onFailure(Call<WalletAuthenticationResponse> call, Throwable t) {
                Snackbar.make(binding.textForgotPin,getString(R.string.error_occured),Snackbar.LENGTH_LONG).show();
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
                    Snackbar.make(binding.textForgotPin, message, Snackbar.LENGTH_SHORT).show();
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
        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(context);
        myAppPrefsManager.logInUser();

        // Set isLogged_in of ConstantValues
        ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();


        // Navigate back to MainActivity
        Intent i = new Intent(context, WalletHomeActivity.class);
        startActivity(i);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
    }

    private void processRegistration(String userPassword, String phoneNumber, String userFirstname, String userLastname, String village, String subCounty, String district,String idType,String idNo,String firstSecurityQn,String secondSecurityQn,String thirdSecurityQn,String firstQnAnswer,String secondQnAnswer,String thirdQnAnswer) {
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);
        String action_id = "storeUser";
        dialogLoader.showProgressDialog();

        String countryCode = getResources().getString(R.string.ugandan_code);
        Call<UserData> call = APIClient.getWalletInstance(getContext())
                .processRegistration(
                        userFirstname,userLastname, userPassword,
                        countryCode, phoneNumber, village, subCounty,
                        district,idType,idNo,
                        firstSecurityQn,secondSecurityQn,thirdSecurityQn,
                        firstQnAnswer,secondQnAnswer,thirdQnAnswer,
                        request_id,category,action_id);

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(@NotNull Call<UserData> call, @NotNull retrofit2.Response<UserData> response) {

                dialogLoader.hideProgressDialog();

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("1")) {

                        Intent authenticate = new Intent(context, WalletHomeActivity.class);
                        context.startActivity(authenticate);
                        getActivity().finish();

                    } else if (response.body().getStatus().equalsIgnoreCase("0")) {
                        // Get the Error Message from Response
                        String message = response.body().getMessage();
                        Snackbar.make(binding.textForgotPin, message, Snackbar.LENGTH_SHORT).show();

                    } else {
                        // Unable to get Success status
                        Toast.makeText(context, getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // Show the Error Message
                    String Str = response.message();
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NotNull Call<UserData> call, @NotNull Throwable t) {
                dialogLoader.hideProgressDialog();
                String Str = "" + t;
                Toast.makeText(context, "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

}
