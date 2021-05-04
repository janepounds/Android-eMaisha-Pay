package com.cabral.emaishapay.fragments.auth_fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.AuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.customs.OtpDialogLoader;
import com.cabral.emaishapay.databinding.FragmentGetStartedSignUpBinding;
import com.cabral.emaishapay.models.WalletAuthentication;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.services.SmsBroadcastReceiver;
import com.cabral.emaishapay.utils.ValidateInputs;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class GetStartedSignUpFragment extends Fragment {
    FragmentGetStartedSignUpBinding binding;
    Context context;

    private DialogLoader dialogLoader;
    String phone_no,authPhone;
    OtpDialogLoader otpDialogLoader;

    @Override
    public void onAttach(@NonNull Context context) {
        this.context=context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_get_started_sign_up,container,false);
        dialogLoader=new DialogLoader(this.context);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        View decorView = getActivity().getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        binding.checkboxTcs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (binding.checkboxTcs.isChecked()){
                    binding.getStartedBtn.setEnabled(true);
                    binding.getStartedBtn.setClickable(true);

                   // binding.getStartedBtn.setAlpha(1);
                    binding.getStartedBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Validate Login Form Inputs
                            boolean isValidData = validateForm();
                            if (isValidData) {
                                //authenticate phone
                                phone_no = getString(R.string.phone_number_code) + binding.userMobile.getText().toString().trim();
                                authPhone = "256" + binding.userMobile.getText().toString().trim();
                                authenticatePhoneNo(phone_no,authPhone);

                            }
                            //navigate to Sign Up Fragment


                        }
                    });
                }else{
                    binding.getStartedBtn.setEnabled(false);
                    binding.getStartedBtn.setClickable(false);


                    Toast.makeText(context, "Please agree to the Terms of Service and Privacy Policy", Toast.LENGTH_LONG).show();




                   // binding.getStartedBtn.setAlpha((float) 0.4);
                }

            }
        });
        binding.textTcsDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                View dialogView = getLayoutInflater().inflate(R.layout.layout_terms_and_conditions, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);

                Button btn_agree = dialogView.findViewById(R.id.btn_agree);

                Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);

                TextView full_app_terms_services =dialogView.findViewById(R.id.full_app_terms_services);






                final AlertDialog alertDialog = dialog.create();

                btn_cancel.setOnClickListener(view13->{
                    alertDialog.dismiss();
                });
                full_app_terms_services.setOnClickListener(view13->{
                    Uri uri = Uri.parse("https://forms.zohopublic.com/virtualoffice20750/form/PrivacyPolicy/formperma/cMB0eFNpmuo5BfUYcYjm-56lXcYWvOL55IodE5BtBpI"); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                });
                btn_agree.setOnClickListener(vv->{
                    binding.checkboxTcs.setChecked(true);
                    alertDialog.dismiss();
                });
                alertDialog.show();





                final AlertDialog alertDialog = dialog.create();

                btn_cancel.setOnClickListener(view13->{
                    alertDialog.dismiss();
                });
                full_app_terms_services.setOnClickListener(view13->{
                    Uri uri = Uri.parse("https://forms.zohopublic.com/virtualoffice20750/form/PrivacyPolicy/formperma/cMB0eFNpmuo5BfUYcYjm-56lXcYWvOL55IodE5BtBpI"); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                });
                btn_agree.setOnClickListener(vv->{
                    binding.checkboxTcs.setChecked(true);
                    alertDialog.dismiss();
                });
                alertDialog.show();

            }
        });


        binding.layoutSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to Sign Up Fragment
                AuthActivity.navController.navigate(R.id.action_getStartedSignUpFragment_to_loginFragment);

            }
        });
    }


    private boolean validateForm() {
        if (!ValidateInputs.isValidNumber(binding.userMobile.getText().toString().trim())) {
            binding.userMobile.setError(getString(R.string.invalid_contact));
            return false;
        }
        else {
            return true;
        }
    }

    public void authenticatePhoneNo(String phoneNumber, String authPhone){
        dialogLoader.showProgressDialog();
        String request_id = WalletHomeActivity.generateRequestId();
        String service_code = "120224";
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);
        /******************RETROFIT IMPLEMENTATION***********************/
        Call<WalletAuthentication> call = APIClient.getWalletInstance(context).initiatePhoneAuth(authPhone,request_id,category,service_code,"initiatePhoneAuth");
        call.enqueue(new Callback<WalletAuthentication>() {
            @Override
            public void onResponse(Call<WalletAuthentication> call, Response<WalletAuthentication> response) {
                if(response.isSuccessful()){
                    dialogLoader.hideProgressDialog();

                    if(response.body().getStatus()==1){
                        if(response.body().getMessage().equalsIgnoreCase("Phone was already Verified")){
                            //navigate to signup
                            navigateToSignUp(phoneNumber);

                        }else {

                            otpDialogLoader=new OtpDialogLoader(GetStartedSignUpFragment.this) {
                                @Override
                                protected void onConfirmOtp(String otp_code, Dialog otpDialog) {
                                    otpDialog.dismiss();

                                    verifyVerificationCode(phoneNumber,authPhone,otp_code,otpDialog,otpDialogLoader);
                                }

                                @Override
                                protected void onResendOtp() {
                                    otpDialogLoader.resendOtp(
                                            phoneNumber,
                                            dialogLoader,
                                            binding.getStartedBtn

                                    );
                                }
                            };
                            otpDialogLoader.showOTPDialog();        //Call the OTP Dialog
                        }
                    }
                    else if( response.isSuccessful() ){
                        otpDialogLoader.clearOTPDialog();
                        String message = response.body().getMessage();
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }else{

                        Snackbar.make(context,getView(),response.body().getMessage(),Snackbar.LENGTH_SHORT).show();
                    }


                }else if (response.code() == 401) {
                    Toast.makeText(context,response.body().getMessage(),Toast.LENGTH_LONG).show();
                    dialogLoader.hideProgressDialog();

                }

            }

            @Override
            public void onFailure(Call<WalletAuthentication> call, Throwable t){
                dialogLoader.hideProgressDialog();
            }
        });

    }

    public void verifyVerificationCode(String phone_number, String authPhone, String code, Dialog otpDialog, OtpDialogLoader otpDialogLoader){
        //dialogLoader.showProgressDialog();

        String request_id = WalletHomeActivity.generateRequestId();
        String service_code = "120224";
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);
        /******************RETROFIT IMPLEMENTATION***********************/
        Call<WalletAuthentication> call = APIClient.getWalletInstance(context).validatePhoneNo(authPhone,request_id,category,service_code,"validatePhoneNumber",code);
        call.enqueue(new Callback<WalletAuthentication>() {
            @Override
            public void onResponse(Call<WalletAuthentication> call, Response<WalletAuthentication> response) {
                dialogLoader.hideProgressDialog();
                if(response.isSuccessful() && response.body().getStatus()==1){

                    navigateToSignUp(phone_number);
                    otpDialog.dismiss();
                }else if (response.code() == 401) {
                    Snackbar.make(context,binding.getStartedBtn,response.body().getMessage(),Snackbar.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<WalletAuthentication> call, Throwable t){
                dialogLoader.hideProgressDialog();
            }
        });

    }

    private void navigateToSignUp(String phoneNumber) {
        Bundle bundle = new Bundle();
        bundle.putString("phone",phoneNumber);
        AuthActivity.navController.navigate(R.id.action_getStartedSignUpFragment_to_signUpFragment,bundle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        otpDialogLoader.onActivityResult(requestCode, resultCode, data);
    }



}
