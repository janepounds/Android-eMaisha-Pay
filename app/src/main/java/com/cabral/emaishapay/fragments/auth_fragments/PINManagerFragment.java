package com.cabral.emaishapay.fragments.auth_fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.AuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.app.MyAppPrefsManager;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.customs.OtpDialogLoader;
import com.cabral.emaishapay.database.User_Info_DB;
import com.cabral.emaishapay.databinding.FragmentTokenAuthBinding;
import com.cabral.emaishapay.models.SecurityQnsResponse;
import com.cabral.emaishapay.models.WalletAuthentication;
import com.cabral.emaishapay.models.WalletAuthenticationResponse;
import com.cabral.emaishapay.models.user_model.UserData;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;
import com.cabral.emaishapay.services.SmsBroadcastReceiver;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.cabral.emaishapay.activities.WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE;
import static com.cabral.emaishapay.activities.WalletHomeActivity.PREFERENCES_WALLET_BUSINESS_ID;
import static com.cabral.emaishapay.activities.WalletHomeActivity.navController;

//This fragment is used for creating or picking a user's PIN and Continue with Login or SignUp processes.
public class PINManagerFragment  extends  Fragment  implements View.OnClickListener  {

    private static final String TAG = "TokenAuthFragment";
    private static Context context;
    private  String pin="", pin1="",phonenumber,otp_code,smsResults;
    static FragmentTokenAuthBinding binding;

    private static SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    User_Info_DB userInfoDB;
    DialogLoader dialogLoader;
    APIRequests apiRequests;
    private OtpDialogLoader otpDialogLoader;
    private List<SecurityQnsResponse.SecurityQns> securityQnsList = new ArrayList();
    ArrayList<String> securityQns = new ArrayList<>();
    ArrayList<String> securityQnsSubList1 = new ArrayList<>();
    ArrayList<String> securityQnsSubList2 = new ArrayList<>();
    ArrayList<String> securityQnsSubList3 = new ArrayList<>();
    private Spinner spFirstSecurityQn,spSecondSecurityQn,spThirdSecurityQn;
    private EditText etFirstQnAnswer,etSecondQnAnswer,etThirdQnAnswer;

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
        setKeyValues();

        sharedPreferences = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        userInfoDB = new User_Info_DB();
        dialogLoader = new DialogLoader(context);
        apiRequests = APIClient.getWalletInstance(getContext());

        return binding.getRoot();
    }

    private void setKeyValues() {
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
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(getArguments()!=null){
            ACTION=getArguments().getInt("action");
            phonenumber=getArguments().getString("phone");
            if(ACTION==2){
                binding.pinTitle.setText(getString(R.string.create_pin));
                binding.layoutSignin.setVisibility(View.GONE);
                binding.tokenAuthClose.setVisibility(View.GONE);

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
                binding.layoutSignin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Go to security qns
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.DialogFullscreen);
                        View dialogView = getLayoutInflater().inflate(R.layout.new_forgot_pin, null);
                        dialog.setView(dialogView);
                        dialog.setCancelable(true);

                        //            RequestUserQns(user_id);
                        //display security qns and
                        Button submit = dialogView.findViewById(R.id.btn_submit_security_qn);
                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                validateSecurityQns();
                            }
                        });
                        Spinner firstSecurityQn,secondSecurityQn,thirdSecurityQn;
                        EditText firstQnAnswer,secondQnAnswer,thirdQnAnswer,phoneNumber;

                        //calling security qns form
                        phoneNumber =dialogView.findViewById(R.id.phone_no);
                        firstSecurityQn = dialogView.findViewById(R.id.sp_first_security_qn);
                        secondSecurityQn = dialogView.findViewById(R.id.sp_second_security_qn);
                        thirdSecurityQn = dialogView.findViewById(R.id.sp_third_security_qn);
                        firstQnAnswer = dialogView.findViewById(R.id.etxt_first_security_qn);
                        secondQnAnswer = dialogView.findViewById(R.id.etxt_second_security_qn);
                        thirdQnAnswer = dialogView.findViewById(R.id.etxt_third_security_qn);
                        RequestSecurityQns();

                        final AlertDialog alertDialog = dialog.create();
                        alertDialog.show();
                    }
                });
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
        binding.textForgotPin.setOnClickListener(this);
        binding.tokenAuthClose.setOnClickListener(this);

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
                binding.pinCode1Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_dark_blue_bg, null));
                submitAction();
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
                binding.pinCode2Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_dark_blue_bg, null));
                submitAction();
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
                binding.pinCode3Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_dark_blue_bg, null));
                submitAction();
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

                binding.pinCode4Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_dark_blue_bg, null));
                submitAction();

            }

        });

    }

    private void submitAction() {

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
                    binding.pinTitle.setText(getString(R.string.confirm_pin));
                    clearPin(binding);
                    binding.pinCode1Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_light_blue_bg, null));
                    binding.pinCode2Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_light_blue_bg, null));
                    binding.pinCode3Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_light_blue_bg, null));
                    binding.pinCode4Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_light_blue_bg, null));

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
                    binding.pinCode1Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_light_blue_bg, null));
                    break;
                case 2:
                    binding.pinCode2Edt.setText("");
                    setInputConnection( binding.pinCode2Edt);
                    binding.pinCode2Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_light_blue_bg, null));
                    break;
                case 3:
                    binding.pinCode3Edt.setText("");
                    setInputConnection( binding.pinCode3Edt);
                    binding.pinCode3Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_light_blue_bg, null));
                    break;

                case 4:
                    binding.pinCode4Edt.setText("");
                    setInputConnection( binding.pinCode4Edt);
                    binding.pinCode4Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_light_blue_bg, null));
                    break;
            }
        }
        else if(v.getId() == R.id.tv_key_clear){
            clearPin(binding);
            binding.pinCode1Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_light_blue_bg, null));
            binding.pinCode2Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_light_blue_bg, null));
            binding.pinCode3Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_light_blue_bg, null));
            binding.pinCode4Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_light_blue_bg, null));
        }

        else if(v.getId() == R.id.text_forgot_pin){
            AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.DialogFullscreen);
            View dialogView = getLayoutInflater().inflate(R.layout.buy_inputs_dialog_input, null);
            dialog.setView(dialogView);
            dialog.setCancelable(true);

//            RequestUserQns(user_id);
            //display security qns and
            Button submit = dialogView.findViewById(R.id.btn_submit_security_qn);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean validSecurityQns = validateSecurityQns();
                    if(validSecurityQns){
                        //enter new pin
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
                        dialog.setView(dialogView);
                        dialog.setCancelable(true);
                        final EditText current_pin = dialogView.findViewById(R.id.current_pin);
                        current_pin.setVisibility(View.GONE);
                        final EditText new_pin = dialogView.findViewById(R.id.new_pin);
                        final EditText confirm_new_pin = dialogView.findViewById(R.id.confirm_new_pin);
                        final TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                        final Button submit = dialogView.findViewById(R.id.update);
                        final Button cancel = dialogView.findViewById(R.id.cancel);
                        dialog_title.setText("Create New Pin");


                        final AlertDialog alertDialog = dialog.create();
                        alertDialog.show();
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });
                    }

                }
            });

            //calling security qns form
            spFirstSecurityQn = dialogView.findViewById(R.id.sp_first_security_qn);
            spSecondSecurityQn = dialogView.findViewById(R.id.sp_second_security_qn);
            spThirdSecurityQn = dialogView.findViewById(R.id.sp_third_security_qn);
            etFirstQnAnswer = dialogView.findViewById(R.id.etxt_first_security_qn);
            etSecondQnAnswer = dialogView.findViewById(R.id.etxt_second_security_qn);
            etThirdQnAnswer = dialogView.findViewById(R.id.etxt_third_security_qn);
            RequestSecurityQns();

            final AlertDialog alertDialog = dialog.create();
            alertDialog.show();

        }

        else if(v.getId() == R.id.token_auth_close){
            AuthActivity.navController.popBackStack();
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

        binding.pinCode1Edt.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.round_light_blue_bg, null));
        binding.pinCode2Edt.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.round_light_blue_bg, null));
        binding.pinCode3Edt.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.round_light_blue_bg, null));
        binding.pinCode4Edt.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.round_light_blue_bg, null));

        InputConnection ic = binding.pinCode1Edt.onCreateInputConnection(new EditorInfo());
        inputConnection = ic;
    }

    public void setInputConnection(EditText editText) {
        InputConnection ic = editText.onCreateInputConnection(new EditorInfo());
        inputConnection = ic;
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
                    otpDialogLoader=new OtpDialogLoader(PINManagerFragment.this) {
                        @Override
                        protected void onConfirmOtp(String otp_code, Dialog otpDialog) {
                            otpDialog.dismiss();
                            confirmLogin(password, phonenumber, otp_code, otpDialog);
                        }

                        @Override
                        protected void onResendOtp() {
                            otpDialogLoader.resendOtp(
                                    phonenumber,
                                    dialogLoader,
                                    binding.textForgotPin

                            );
                        }
                    };
                    otpDialogLoader.showOTPDialog();        //Call the OTP Dialog




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
                        Snackbar.make(binding.textForgotPin, response.body().getMessage(),Snackbar.LENGTH_LONG).show();
                        clearPin(binding);
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
                            Log.d(TAG, "onResponse: BUSINESSID= " + userDetails.getBusiness_id());
                            otpDialog.dismiss();
                            WalletHomeActivity.savePreferences(PREFERENCES_WALLET_BUSINESS_ID, userDetails.getBusiness_id(), context);
                            loginUser(userDetails, rawpassword);

                             //Log.w("WALLET_ID", WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context));

                            String accessToken = response.body().getAccess_token();
                            String accountRole = userDetails.getAccountRole();
                            Log.d(TAG, accessToken);
                            WalletHomeActivity.WALLET_ACCESS_TOKEN = accessToken;
                            WalletHomeActivity.savePreferences(PREFERENCES_WALLET_ACCOUNT_ROLE, accountRole, context);

                            Log.d(TAG, "onResponse: business_id"+userDetails.getBusiness_id());
                            if (dialogLoader != null)
                                dialogLoader.hideProgressDialog();
                            WalletHomeActivity.startHome(context);


                        }
                        catch (Exception e) {
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
                    clearPin(binding);
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
        editor.putString(WalletHomeActivity.PREFERENCES_USER_PASSWORD, password);

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
        String category = "Default";
        String action_id = "storeUser";
        dialogLoader.showProgressDialog();

        String countryCode = getResources().getString(R.string.ugandan_code);
        Call<WalletAuthentication> call = APIClient.getWalletInstance(getContext())
                .processRegistration(
                        userFirstname,userLastname, userPassword,
                        countryCode, phoneNumber, village, subCounty,
                        district,idType,idNo,
                        firstSecurityQn,secondSecurityQn,thirdSecurityQn,
                        firstQnAnswer,secondQnAnswer,thirdQnAnswer,
                        request_id,category,action_id);

        call.enqueue(new Callback<WalletAuthentication>() {
            @Override
            public void onResponse(@NotNull Call<WalletAuthentication> call, @NotNull retrofit2.Response<WalletAuthentication> response) {
                dialogLoader.hideProgressDialog();
                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getStatus()==1) {

                        WalletAuthentication.UserData userDetails = response.body().getData();
                        Log.d(TAG, "onResponse: Email = " + userDetails.getEmail());
                        Log.d(TAG, "onResponse: First Name = " + userDetails.getFirstname());
                        Log.d(TAG, "onResponse: Last Name = " + userDetails.getLastname());
                        Log.d(TAG, "onResponse: Username = " + userDetails.getEmail());
                        Log.d(TAG, "onResponse: addressStreet = " + userDetails.getAddressStreet());
                        Log.d(TAG, "onResponse: addressCityOrTown = " + userDetails.getAddressCityOrTown());
                        Log.d(TAG, "onResponse: address_district = " + userDetails.getAddressCityOrTown());
                        //otpDialog.dismiss();
                        loginUser(userDetails, userPassword);

                    } else if (response.body().getStatus()==0) {
                        // Get the Error Message from Response
                        String message = response.body().getMessage();
                        Snackbar.make(binding.textForgotPin, message, Snackbar.LENGTH_SHORT).show();

                    } else {
                        // Unable to get Success status
                        Toast.makeText(context, getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    // Show the Error Message
                    String Str = response.message();
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<WalletAuthentication> call, @NotNull Throwable t) {
                dialogLoader.hideProgressDialog();
                String Str = "" + t;
                Toast.makeText(context, "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateSecurityQns() {
        //load answered security Qns(locally or from an endpoint)
        return true;
    }

    public void RequestSecurityQns(){
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);
        /******************RETROFIT IMPLEMENTATION***********************/
        Call<SecurityQnsResponse> call = APIClient.getWalletInstance(getContext()).getSecurityQns(request_id,category,"getSecurityQns");
        call.enqueue(new Callback<SecurityQnsResponse>() {
            @Override
            public void onResponse(Call<SecurityQnsResponse> call, Response<SecurityQnsResponse> response) {
                if(response.isSuccessful()){

                    try {
                        securityQnsList = response.body().getSecurity_qnsList();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        Log.d(TAG,securityQnsList.size()+"**********");

                        //set security qns adapter
                        for(int i=0;i<securityQnsList.size();i++){
                            String security_Qn_name = securityQnsList.get(i).getSecurity_qn_name();
                            securityQns.add(security_Qn_name);
                        }
                        for(int i=0;i<3;i++) {
                            securityQnsSubList1.add(securityQns.get(i));
                        }for(int i=3;i<6;i++){
                            securityQnsSubList2.add(securityQns.get(i));
                        }for(int i=6;i<9;i++){
                            securityQnsSubList3.add(securityQns.get(i));
                        }

                        //set list in beneficiary spinner
                        ArrayAdapter<String> beneficiariesAdapter1 = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, securityQnsSubList1);
                        ArrayAdapter<String> beneficiariesAdapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, securityQnsSubList2);
                        ArrayAdapter<String> beneficiariesAdapter3 = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, securityQnsSubList3);
                        spFirstSecurityQn.setAdapter(beneficiariesAdapter1);
                        spSecondSecurityQn.setAdapter(beneficiariesAdapter2);
                        spThirdSecurityQn.setAdapter(beneficiariesAdapter3);

                        //set in the specific spinners

                    }

                }else if (response.code() == 401) {
                    Log.e("info", "Something got very very wrong");
                }

            }

            @Override
            public void onFailure(Call<SecurityQnsResponse> call, Throwable t){
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        otpDialogLoader.onActivityResult(requestCode, resultCode, data);
    }

}
