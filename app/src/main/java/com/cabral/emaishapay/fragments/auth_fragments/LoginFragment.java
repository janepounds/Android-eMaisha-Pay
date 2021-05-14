package com.cabral.emaishapay.fragments.auth_fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cabral.emaishapay.DailogFragments.AddCardFragment;
import com.cabral.emaishapay.DailogFragments.ChangePassword;
import com.cabral.emaishapay.DailogFragments.DepositPayments;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.AuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.databinding.LoginFragmentBinding;
import com.cabral.emaishapay.fragments.wallet_fragments.WalletHomeFragment;
import com.cabral.emaishapay.models.SecurityQnsResponse;
import com.cabral.emaishapay.models.user_model.UserData;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment  extends Fragment {
    private static final String TAG = "Login";
    private LoginFragmentBinding binding;

    DialogLoader dialogLoader;
    APIRequests apiRequests;
    private Context context;
    private List<SecurityQnsResponse.SecurityQns> securityQnsList = new ArrayList();
    ArrayList<String> securityQns = new ArrayList<>();
    ArrayList<String> securityQnsSubList1 = new ArrayList<>();
    ArrayList<String> securityQnsSubList2 = new ArrayList<>();
    ArrayList<String> securityQnsSubList3 = new ArrayList<>();
    private Spinner firstSecurityQn,secondSecurityQn,thirdSecurityQn;
    private EditText firstQnAnswer,secondQnAnswer,thirdQnAnswer,phone_number;

    @Override
    public void onAttach(@NonNull Context context) {
        this.context=context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View decorView = getActivity().getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        binding = DataBindingUtil.inflate(inflater,R.layout.login_fragment,container,false);
        apiRequests = APIClient.getWalletInstance(getContext());
        // Binding Layout Views
        dialogLoader = new DialogLoader(context);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        binding.forgotPasswordText.setOnClickListener(view0 -> {
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

            //calling security qns form
            phone_number =dialogView.findViewById(R.id.phone_no);
            firstSecurityQn = dialogView.findViewById(R.id.sp_first_security_qn);
            secondSecurityQn = dialogView.findViewById(R.id.sp_second_security_qn);
            thirdSecurityQn = dialogView.findViewById(R.id.sp_third_security_qn);
            firstQnAnswer = dialogView.findViewById(R.id.etxt_first_security_qn);
            secondQnAnswer = dialogView.findViewById(R.id.etxt_second_security_qn);
            thirdQnAnswer = dialogView.findViewById(R.id.etxt_third_security_qn);
            RequestSecurityQns();

            final AlertDialog alertDialog = dialog.create();
            alertDialog.show();


        });

        binding.loginSignupText.setOnClickListener(v -> {
            // Navigate to SignUp Activity
            AuthActivity.navController.navigate(R.id.action_loginFragment_to_getStartedSignUpFragment);
        });

        binding.loginBtn.setOnClickListener(v -> {
            // Validate Login Form Inputs
            boolean isValidData = validateLogin();

            if (isValidData) {
                // Proceed User Login
                String phone =getString(R.string.phone_number_code)+binding.userPhone.getText().toString();
                Bundle args= new Bundle();
                args.putInt("action",1);
                args.putString("phone",phone);
                AuthActivity.navController.navigate(R.id.action_loginFragment_to_PINManagerFragment,args);
            }



        });

    }


    private void processForgotPassword(String email) {
        dialogLoader.showProgressDialog();
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
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
                    Snackbar.make(binding.container, message, Snackbar.LENGTH_LONG).show();

                } else {
                    // Show the Error Message
                    Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserData> call, @NotNull Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(context, "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
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

    private void validateSecurityQns() {
        dialogLoader.showProgressDialog();
        String access_token =  WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();


        Call<SecurityQnsResponse> call = APIClient.getWalletInstance(getContext()).
                validateSecurityQns(access_token,
                        getString(R.string.phone_number_code)+phone_number.getText().toString(),
                        firstSecurityQn.getSelectedItem().toString(),
                        secondSecurityQn.getSelectedItem().toString(),
                        thirdSecurityQn.getSelectedItem().toString(),
                        firstQnAnswer.getText().toString(),
                        secondQnAnswer.getText().toString(),
                        thirdQnAnswer.getText().toString(),
                        request_id,
                        "validateSecurityQns");

        call.enqueue(new Callback<SecurityQnsResponse>() {
            @Override
            public void onResponse(Call<SecurityQnsResponse> call, Response<SecurityQnsResponse> response) {
                if(response.isSuccessful()){

                    if(response.body().getStatus().equalsIgnoreCase("1")){
                        //call change password dialog
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        Fragment prev =fm.findFragmentByTag("dialog");
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);
                        // Create and show the dialog.
                        DialogFragment changePassword =new ChangePassword(getString(R.string.forgot_pin));
                        changePassword.show( ft, "dialog");
                        Toast.makeText(context,response.body().getMessage(),Toast.LENGTH_LONG).show();


                    }else{
                        Toast.makeText(context,response.body().getMessage(),Toast.LENGTH_LONG).show();

                    }
                    dialogLoader.hideProgressDialog();


                }else if (response.code() == 401) {
                    Toast.makeText(context,response.body().getMessage(),Toast.LENGTH_LONG).show();
                    dialogLoader.hideProgressDialog();

                }

            }

            @Override
            public void onFailure(Call<SecurityQnsResponse> call, Throwable t){
                dialogLoader.hideProgressDialog();
            }
        });


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
                        firstSecurityQn.setAdapter(beneficiariesAdapter1);
                        secondSecurityQn.setAdapter(beneficiariesAdapter2);
                        thirdSecurityQn.setAdapter(beneficiariesAdapter3);

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

}
