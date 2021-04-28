package com.cabral.emaishapay.fragments.wallet_fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.AuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.databinding.LayoutScanAndPayProcessStep3Binding;
import com.cabral.emaishapay.models.WalletPurchaseResponse;
import com.cabral.emaishapay.models.WalletTransactionInitiation;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanAndPayStep3 extends Fragment implements View.OnClickListener {
    private Context context;
    private LayoutScanAndPayProcessStep3Binding binding;
    private SparseArray<String> keyValues = new SparseArray<>();
    private static InputConnection inputConnection;
    private String pin,merchant_id;
    private double Amount,Balance;
    DialogLoader dialogLoader;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.layout_scan_and_pay_process_step_3,container,false);

        dialogLoader = new DialogLoader(context);
        if(getArguments()!=null){
            binding.textMerchantName.setText(getArguments().getString("merchant_name"));
            binding.amount.setText("UGX "+getArguments().getString("amount"));
            merchant_id = getArguments().getString("merchant_id");
            Amount = Double.parseDouble(getArguments().getString("amount"));
            Balance = Double.parseDouble(getArguments().getString("balance"));

        }
        setKeyValues();

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
        super.onViewCreated(view, savedInstanceState);

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
        binding.textForgotPin.setOnClickListener(this);

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

                processPayment("12"+pin);


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


//        else if(v.getId() == R.id.text_forgot_pin){
//            AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.DialogFullscreen);
//            View dialogView = getLayoutInflater().inflate(R.layout.buy_inputs_dialog_input, null);
//            dialog.setView(dialogView);
//            dialog.setCancelable(true);
//
////            RequestUserQns(user_id);
//            //display security qns and
//            Button submit = dialogView.findViewById(R.id.btn_submit_security_qn);
//            submit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    boolean validSecurityQns = validateSecurityQns();
//                    if(validSecurityQns){
//                        //enter new pin
//                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//                        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
//                        dialog.setView(dialogView);
//                        dialog.setCancelable(true);
//                        final EditText current_pin = dialogView.findViewById(R.id.current_pin);
//                        current_pin.setVisibility(View.GONE);
//                        final EditText new_pin = dialogView.findViewById(R.id.new_pin);
//                        final EditText confirm_new_pin = dialogView.findViewById(R.id.confirm_new_pin);
//                        final TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
//                        final Button submit = dialogView.findViewById(R.id.update);
//                        final Button cancel = dialogView.findViewById(R.id.cancel);
//                        dialog_title.setText("Create New Pin");
//
//
//                        final AlertDialog alertDialog = dialog.create();
//                        alertDialog.show();
//                        cancel.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                alertDialog.dismiss();
//                            }
//                        });
//                    }
//
//                }
//            });
//
//            //calling security qns form
//            spFirstSecurityQn = dialogView.findViewById(R.id.sp_first_security_qn);
//            spSecondSecurityQn = dialogView.findViewById(R.id.sp_second_security_qn);
//            spThirdSecurityQn = dialogView.findViewById(R.id.sp_third_security_qn);
//            etFirstQnAnswer = dialogView.findViewById(R.id.etxt_first_security_qn);
//            etSecondQnAnswer = dialogView.findViewById(R.id.etxt_second_security_qn);
//            etThirdQnAnswer = dialogView.findViewById(R.id.etxt_third_security_qn);
//            RequestSecurityQns();
//
//            final AlertDialog alertDialog = dialog.create();
//            alertDialog.show();
//
//        }

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

    public void setInputConnection(EditText editText) {
        InputConnection ic = editText.onCreateInputConnection(new EditorInfo());
        inputConnection = ic;
    }

    private void processPayment(String service_code) {
        dialogLoader.showProgressDialog();

        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());

        Call<WalletPurchaseResponse> call = apiRequests.makeTransaction(access_token,merchant_id,Amount,"",request_id,category,"payMerchant",service_code);

        call.enqueue(new Callback<WalletPurchaseResponse>() {
            @Override
            public void onResponse(Call<WalletPurchaseResponse> call, Response<WalletPurchaseResponse> response) {
                if(response.code()== 200){
                    dialogLoader.hideProgressDialog();
                    if(response.body().getStatus().equals("0")){

                        try {
                            Toasty.error(context, ""+response.body().getMessage(), Toast.LENGTH_LONG).show();
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.e("Error", e.getMessage());
                        }
                    }else {

                        //got to step 4
                        Bundle bundle = new Bundle();
                        bundle.putString("amount", binding.amount.getText().toString());
                        bundle.putString("merchant_name", binding.textMerchantName.getText().toString());
                        bundle.putString("merchant_id", merchant_id);
                        bundle.putString("trans_id", response.body().getData().getReferenceNumber());
                        bundle.putString("Date", new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
                        bundle.putString("balance", Balance+"");
                        WalletHomeActivity.navController.navigate(R.id.action_scanAndPayStep3_to_scanAndPayStep4,bundle);

                    }


                }else{
                    binding.errorMessageTxt.setText(response.errorBody().toString());
                    binding.errorMessageTxt.setVisibility(View.VISIBLE);
                    if(response.errorBody() != null){
                        Log.e("BACKUP RESPONSE 1A"+response.code(),response.errorBody().toString());
                    }

                    dialogLoader.hideProgressDialog();

                }
            }

            @Override
            public void onFailure(Call<WalletPurchaseResponse> call, Throwable t) {

                Log.e("info 1A: ", t.getMessage());
                Log.e("info 1A: ", "Something got very very wrong");

                binding.errorMessageTxt.setText("Error occured! Try again later");
                binding.errorMessageTxt.setVisibility(View.VISIBLE);
                binding.errorMessageTxt.setVisibility(View.VISIBLE);
                dialogLoader.hideProgressDialog();

            }

        });
    }
}
