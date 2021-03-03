package com.cabral.emaishapay.DailogFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.models.InitiateTransferResponse;
import com.cabral.emaishapay.models.InitiateWithdrawResponse;
import com.cabral.emaishapay.models.WalletTransactionResponse;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;


public class AgentCustomerConfirmDetails extends DialogFragment {
    TextView textTitleLabel,textTitleName,textName,textReceiverAccount,textTitlePhoneNumber,textPhoneNumber,textTitleAmount,textAmount;
    TextView textTitleCharge,textCharge,textTitleTotalAmount,textTotalAmount,textSenderName,textSenderMobile;
    CardView layoutReceiverAccount,totalAmount,charge,depositAmount,layoutSenderName,layoutSenderMobile;
    Button txtSubmit,btnCancel;
    String key = "",customerNo;
    DialogLoader dialogLoader;

    private Dialog otpDialog;
    private EditText code1,code2,code3,code4,code5,code6;
    TextView resendtxtview;
    private  String otp_code, sms_code;
    double transferAmount;

    public AgentCustomerConfirmDetails() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_agent_customer_confirm_details, null);
        dialogLoader = new DialogLoader(getContext());
        textTitleLabel = view.findViewById(R.id.agent_confirm_details_title_label);
        textTitleName = view.findViewById(R.id.text_title_name);
        textName = view.findViewById(R.id.text_name);
        textReceiverAccount = view.findViewById(R.id.text_receiver_account);
        textTitlePhoneNumber = view.findViewById(R.id.text_title_phone_number);
        textPhoneNumber = view.findViewById(R.id.text_phone_number);
        textTitleAmount = view.findViewById(R.id.text_title_amount);
        textAmount = view.findViewById(R.id.text_amount);
        textTitleCharge = view.findViewById(R.id.text_title_charge);
        textCharge = view.findViewById(R.id.text_charge);
        textTitleTotalAmount = view.findViewById(R.id.text_title_total_amount);
        textTotalAmount = view.findViewById(R.id.txtTotalAmount);
        layoutReceiverAccount = view.findViewById(R.id.layout_receiver_account);
        totalAmount = view.findViewById(R.id.card_total_amount);
        charge = view.findViewById(R.id.card_transaction_charge);
        depositAmount = view.findViewById(R.id.card_deposit_amount);
        txtSubmit = view.findViewById(R.id.txt_card_confirm);
        textSenderName = view.findViewById(R.id.text_sender_name);
        textSenderMobile = view.findViewById(R.id.text_sender_phone_number);
        layoutSenderMobile = view.findViewById(R.id.card_layout_sender_phone);
        layoutSenderName = view.findViewById(R.id.layout_sender_name);
        btnCancel = view.findViewById(R.id.cancel);


        if(getArguments()!=null){
            /**************WITHDRAW********************/
            key = getArguments().getString("key");
            this.transferAmount=Double.parseDouble(getArguments().getString("amount"));
            if(key.equalsIgnoreCase("withdraw")){
                textTitleLabel.setText(getArguments().getString("title"));
                textTitleAmount.setText("Amount Received");
                textName.setText(getArguments().getString("customer_name"));
                textReceiverAccount.setText("Customer");
                textPhoneNumber.setText("0"+getArguments().getString("phone_number"));
                textAmount.setText("UGX "+getArguments().getString("amount"));
                textTotalAmount.setText("UGX "+getArguments().getString("amount"));

            }else if(key.equalsIgnoreCase("deposit")){
                /*****************DEPOSIT*************/

                textName.setText(getArguments().getString("customer_name"));
                textPhoneNumber.setText("0"+getArguments().getString("phone_number"));
                textAmount.setText("UGX "+getArguments().getString("amount"));
                textTotalAmount.setText("UGX "+getArguments().getString("amount"));


            }else if(key.equalsIgnoreCase("transfer")){
                /*****************TRANSFER*************/
                textTitleLabel.setText(getArguments().getString("title"));
                textReceiverAccount.setText("Receiver");
                textName.setText(getArguments().getString("receiver_name"));
                textTitlePhoneNumber.setText("Receiver Mobile");
                textPhoneNumber.setText("0"+getArguments().getString("receipient_no"));
                textAmount.setText("UGX "+getArguments().getString("amount"));
                textTotalAmount.setText("UGX "+getArguments().getString("amount"));
                customerNo = "0"+getArguments().getString("customer_no");
                textTitleCharge.setText("Transfer Charge");
                textTitleAmount.setText("Transfer Amount");
                textTitleName.setText("Receiver");
                layoutSenderName.setVisibility(View.VISIBLE);
                layoutSenderMobile.setVisibility(View.VISIBLE);
                textSenderName.setText(getArguments().getString("customer_name"));
                textSenderMobile.setText("0"+getArguments().getString("customer_no"));


            }else{
                /*****************BALANCE INQUIRY*************/
                textTitleLabel.setText(getArguments().getString("title"));
                textName.setText(getArguments().getString("customer_name"));
                textPhoneNumber.setText("0"+getArguments().getString("customer_no"));
                totalAmount.setVisibility(View.GONE);
                charge.setVisibility(View.GONE);
                depositAmount.setVisibility(View.GONE);
            }



        }



        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //call pin dialog
                           // Create and show the dialog.
//                DialogFragment depositDialog = new EnterPin();
//                depositDialog.setArguments(bundle);
//                depositDialog.show(ft, "dialog");

                initiateFundsTransfer(customerNo,transferAmount );
            }


        });



        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        btnCancel.setOnClickListener(v -> dismiss());
        return dialog;

    }


    public void initiateFundsTransfer(final String customerPhoneNumber, final double amount) {

        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        dialogLoader.showProgressDialog();

        /*****RETROFIT IMPLEMENTATION*****/
        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<InitiateTransferResponse> call = apiRequests.initiateAgentTransaction(access_token, amount,customerPhoneNumber,"Agent Transfer");
        call.enqueue(new Callback<InitiateTransferResponse>() {
            @Override
            public void onResponse(Call<InitiateTransferResponse> call, Response<InitiateTransferResponse> response) {
                if(response.code() ==200){
                    if( response.body().getStatus().equalsIgnoreCase("0") ){
                        Toast.makeText(getContext(),response.body().getMessage()+"",Toast.LENGTH_LONG).show();
                    }else{
                        getOTPFromUser(customerPhoneNumber,amount);
                    }
                    dialogLoader.hideProgressDialog();

                }
                else if(response.code() == 401) {
                    TokenAuthActivity.startAuth(getActivity(), true);
                    getActivity().finish();
                }
                else if (response.code() == 500) {
                    Log.e("info 500", new String(String.valueOf(response.errorBody())) + ", code: " + response.code());
                } else if (response.code() == 400) {
                    Log.e("info 400", new String(String.valueOf(response.errorBody()) + ", code: " + response.code()));
                } else if (response.code() == 406) {

                    Log.e("info 406", new String(String.valueOf(response.errorBody())) + ", code: " + response.code());
                } else {
                    Log.e("info 406", new String("Error Occurred Try again later"));

                }
                dialogLoader.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<InitiateTransferResponse> call, Throwable t) {
                Log.e("info : " , new String(String.valueOf(t.getMessage())));

                dialogLoader.hideProgressDialog();
            }
        });

    }


    private void getOTPFromUser(final String customerNumber, final double amount) {
        otpDialog  = new Dialog(getContext());
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
                    comfirmAgentFundsTransfer(otp_code,customerNumber,amount);
                }

            }
        });

        otpDialog.findViewById(R.id.login_otp_resend_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // processLogin(password,ConfirmActivity.phonenumber);
            }
        });

        otpDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams params = otpDialog.getWindow().getAttributes(); // change this to your otpDialog.

        params.x = 100; // Here is the param to set your dialog position. Same with params.x
        otpDialog.getWindow().setAttributes(params);
        otpDialog.show();
    }

    private  void comfirmAgentFundsTransfer(String otp_code, String customerNumber, double amount){
        String receiverPhoneNumber=textPhoneNumber.getText().toString();
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        dialogLoader.showProgressDialog();

        Call<InitiateWithdrawResponse> call = APIClient.getWalletInstance().
                confirmAgentTransfer(access_token, amount, otp_code,customerNumber, receiverPhoneNumber);
        call.enqueue(new Callback<InitiateWithdrawResponse>() {
            @Override
            public void onResponse(Call<InitiateWithdrawResponse> call, Response<InitiateWithdrawResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("1")) {
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        //success message
                        Intent intent = new Intent(getContext(), WalletHomeActivity.class);
                        startActivity(intent);
                        dialogLoader.hideProgressDialog();

                    } else {
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        //redirect to home;
                        Intent intent = new Intent(getContext(), WalletHomeActivity.class);
                        startActivity(intent);

                        dialogLoader.hideProgressDialog();

                    }
                }


            }

            @Override
            public void onFailure(Call<InitiateWithdrawResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getContext(), WalletHomeActivity.class);
                startActivity(intent);
                dialogLoader.hideProgressDialog();
            }
        });
    }

}