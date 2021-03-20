package com.cabral.emaishapay.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
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
import com.cabral.emaishapay.models.SecurityQnsResponse;
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

import java.util.ArrayList;
import java.util.List;

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
    private List<SecurityQnsResponse.SecurityQns> securityQnsList = new ArrayList();
    ArrayList<String> securityQns = new ArrayList<>();
    ArrayList<String> securityQnsSubList1 = new ArrayList<>();
    ArrayList<String> securityQnsSubList2 = new ArrayList<>();
    ArrayList<String> securityQnsSubList3 = new ArrayList<>();

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
            RequestSecurityQns();
//            RequestUserQns(user_id);
            //display security qns and
            Button submit = dialogView.findViewById(R.id.btn_submit_security_qn);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean validSecurityQns = validateSecurityQns();
                    if(validSecurityQns){
                        //enter new pin
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.DialogFullscreen);
                        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
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
                    }

                }
            });


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

    private boolean validateSecurityQns() {
        return true;

    }

    public void RequestSecurityQns(){

        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);
        /******************RETROFIT IMPLEMENTATION***********************/
        Call<SecurityQnsResponse> call = APIClient.getWalletInstance().getSecurityQns(access_token,request_id,"");
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
                        for(int i=0;i<securityQns.size();i++){
                            securityQnsSubList1.add(securityQns.get(0));
                            securityQnsSubList1.add(securityQns.get(1));
                            securityQnsSubList1.add(securityQns.get(2));

                            securityQnsSubList2.add(securityQns.get(3));
                            securityQnsSubList2.add(securityQns.get(4));
                            securityQnsSubList2.add(securityQns.get(5));

                            securityQnsSubList3.add(securityQns.get(6));
                            securityQnsSubList3.add(securityQns.get(7));
                            securityQnsSubList3.add(securityQns.get(8));



                        }

                        //set list in beneficiary spinner
                        ArrayAdapter<String> beneficiariesAdapter1 = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, securityQnsSubList1);
                        ArrayAdapter<String> beneficiariesAdapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, securityQnsSubList2);
                        ArrayAdapter<String> beneficiariesAdapter3 = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, securityQnsSubList3);
//                        binding.spFirstSecurityQn.setAdapter(beneficiariesAdapter1);
//                        binding.spSecondSecurityQn.setAdapter(beneficiariesAdapter2);
//                        binding.spThirdSecurityQn.setAdapter(beneficiariesAdapter3);

                        //set in the specific spinners

                    }

                }else if (response.code() == 401) {

//                    TokenAuthActivity.startAuth(, true);
//                    finishAffinity();
//                    if (response.errorBody() != null) {
//                        Log.e("info", new String(String.valueOf(response.errorBody())));
//                    } else {
//                        Log.e("info", "Something got very very wrong");
//                    }
                }

            }

            @Override
            public void onFailure(Call<SecurityQnsResponse> call, Throwable t){
            }
        });





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

