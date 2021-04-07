package com.cabral.emaishapay.fragments.auth_fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import androidx.fragment.app.Fragment;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.AuthActivity;
import com.cabral.emaishapay.activities.ConfirmActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.databinding.LoginFragmentBinding;
import com.cabral.emaishapay.models.SecurityQnsResponse;
import com.cabral.emaishapay.models.user_model.UserData;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
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
    private EditText firstQnAnswer,secondQnAnswer,thirdQnAnswer;

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
            startActivity(new Intent(getActivity(), com.cabral.emaishapay.activities.AuthActivity.class));
            getActivity().overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
        });

        binding.loginBtn.setOnClickListener(v -> {
            // Validate Login Form Inputs
            boolean isValidData = validateLogin();

            if (isValidData) {
                // Proceed User Login
                String phone ="0"+binding.userPhone.getText().toString();
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

    private boolean validateSecurityQns() {
        //load answered security Qns(locally or from an endpoint)

        return true;

    }

    public void RequestSecurityQns(){

        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);
        /******************RETROFIT IMPLEMENTATION***********************/
        Call<SecurityQnsResponse> call = APIClient.getWalletInstance(getContext()).getSecurityQns(access_token,request_id,category,"getSecurityQns");
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
