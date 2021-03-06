package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
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

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.fragments.wallet_fragments.TokenAuthFragment;
import com.cabral.emaishapay.models.InitiateTransferResponse;
import com.cabral.emaishapay.models.InitiateWithdrawResponse;
import com.cabral.emaishapay.models.WalletTransactionInitiation;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;

import java.text.NumberFormat;


public class AgentCustomerConfirmDetails extends DialogFragment {
    TextView textTitleLabel,textTitleName,textName,textReceiverAccount,textTitlePhoneNumber,textPhoneNumber,textTitleAmount,textAmount;
    TextView textTitleCharge,textCharge,textTitleTotalAmount,textTotalAmount,textSenderName,textSenderMobile,tvTimer,tvChangeNumber;
    CardView layoutReceiverAccount,totalAmount,charge,depositAmount,layoutSenderName,layoutSenderMobile;
    Button txtSubmit,btnCancel;
    String key = "",customerNo;
    DialogLoader dialogLoader;
    RelativeLayout layoutResendCode;

    private Dialog otpDialog,dialog;
    private EditText code1,code2,code3,code4,code5,code6;
    TextView resendtxtview;
    private  String otp_code, sms_code;
    double transferAmount;
    Dialog agentPinDialog;

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

            if(key.equalsIgnoreCase("withdraw")){
                this.transferAmount=Double.parseDouble(getArguments().getString("amount"));
                textTitleLabel.setText(getArguments().getString("title"));
                textTitleAmount.setText("Amount Received");
                textName.setText(getArguments().getString("customer_name"));
                textReceiverAccount.setText("Customer");
                customerNo = getString(R.string.phone_number_code)+getArguments().getString("customer_no");
                textPhoneNumber.setText(getString(R.string.phone_number_code)+getArguments().getString("customer_no"));
                textAmount.setText("UGX "+getArguments().getString("amount"));
                textTotalAmount.setText("UGX "+getArguments().getString("amount"));

            }
            else if(key.equalsIgnoreCase("deposit")){
                /*****************DEPOSIT*************/
                this.transferAmount=Double.parseDouble(getArguments().getString("amount"));
                textName.setText(getArguments().getString("customer_name"));
                textPhoneNumber.setText(getString(R.string.phone_number_code)+getArguments().getString("phone_number"));
                textAmount.setText("UGX "+this.transferAmount);
                textTotalAmount.setText("UGX "+this.transferAmount);


            }
            else if(key.equalsIgnoreCase("transfer")){
                /*****************TRANSFER*************/
                this.transferAmount=Double.parseDouble(getArguments().getString("amount"));
                textTitleLabel.setText(getArguments().getString("title"));
                textReceiverAccount.setText("Receiver");
                textName.setText(getArguments().getString("receiver_name"));
                textTitlePhoneNumber.setText("Receiver Mobile");
                textPhoneNumber.setText(getString(R.string.phone_number_code)+getArguments().getString("receipient_no"));
                textAmount.setText("UGX "+getArguments().getString("amount"));
                textTotalAmount.setText("UGX "+getArguments().getString("amount"));
                customerNo = getString(R.string.phone_number_code)+getArguments().getString("customer_no");
                textTitleCharge.setText("Transfer Charge");
                textTitleAmount.setText("Transfer Amount");
                textTitleName.setText("Receiver");
                layoutSenderName.setVisibility(View.VISIBLE);
                layoutSenderMobile.setVisibility(View.VISIBLE);
                textSenderName.setText(getArguments().getString("customer_name"));
                textSenderMobile.setText(getString(R.string.phone_number_code)+getArguments().getString("customer_no"));


            }
            else{
                /*****************BALANCE INQUIRY*************/
                textTitleLabel.setText(getArguments().getString("title"));
                textName.setText(getArguments().getString("customer_name"));
                textPhoneNumber.setText(getString(R.string.phone_number_code)+getArguments().getString("customer_no"));
                totalAmount.setVisibility(View.GONE);
                charge.setVisibility(View.GONE);
                depositAmount.setVisibility(View.GONE);
            }

        }



        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(key.equalsIgnoreCase("deposit")){
                    //call pin dialog
                    // Create and show the dialog.
                    FragmentManager fragmentManager = getChildFragmentManager();

                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    Fragment prev = fragmentManager.findFragmentByTag("dialog");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);
                    Bundle bundle = new Bundle();
                    if(key.equalsIgnoreCase("transfer")){
                        bundle.putString("customer_no",customerNo);
                    }
                    bundle.putString("key",key);
                    bundle.putString("amount",textTotalAmount.getText().toString());
                    bundle.putString("phone_number",textPhoneNumber.getText().toString());
                    bundle.putString("customer_name",textName.getText().toString());

                    // Create and show the dialog.
                    DialogFragment depositDialog = new EnterPin();
                    depositDialog.setArguments(bundle);
                    depositDialog.show(ft, "dialog");

                }
                else if(key.equalsIgnoreCase("withdraw") || key.equalsIgnoreCase("transfer")){
                    String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE, requireContext());
                    String type="Agent Withdraw";
                    if(key.equalsIgnoreCase("transfer")){
                        type="Agent Transfer";
                    }

                    //Inner Dialog to enter PIN
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                    //LayoutInflater inflater = requireActivity().getLayoutInflater();
                    View pinDialog = View.inflate(getContext(),R.layout.dialog_enter_pin,null);


                    builder.setView(pinDialog);
                    agentPinDialog= builder.create();
                    builder.setCancelable(false);

                    EditText pinEdittext =pinDialog.findViewById(R.id.etxt_create_agent_pin);
                    TextView titleTxt = pinDialog.findViewById(R.id.dialog_title);
                    titleTxt.setText("ENTER AGENT PIN");

                    String finalType = type;
                    pinDialog.findViewById(R.id.txt_custom_add_agent_submit_pin).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if( !TextUtils.isEmpty(pinEdittext.getText()) && pinEdittext.getText().length()==4){
                                String service_code= WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION+ pinEdittext.getText().toString();


                                initiateFundsTransfer(customerNo, transferAmount, finalType,service_code );

                            }else {
                                pinEdittext.setError("Invalid Pin Entered");
                            }

                        }
                    });

                    agentPinDialog.show();




                }else{
                    FragmentManager fragmentManager = getChildFragmentManager();

                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    Fragment prev = fragmentManager.findFragmentByTag("dialog");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);
                    Bundle bundle = new Bundle();
                    bundle.putString("key",key);
                    bundle.putString("amount",null);
                    bundle.putString("phone_number",textPhoneNumber.getText().toString());


                    // Create and show the dialog.
                    DialogFragment depositDialog = new EnterPin();
                    depositDialog.setArguments(bundle);
                    depositDialog.show(ft, "dialog");
                }

            }


        });

        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        btnCancel.setOnClickListener(v -> dismiss());
        return dialog;

    }


    public void initiateFundsTransfer(final String customerPhoneNumber, final double amount, String type, String service_code ){

        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        dialogLoader.showProgressDialog();
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());


        /*****RETROFIT IMPLEMENTATION*****/
        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<InitiateTransferResponse> call = apiRequests.initiateAgentTransaction(access_token, amount,customerPhoneNumber,type,request_id,category,"customerTransactionOTP",service_code);
        call.enqueue(new Callback<InitiateTransferResponse>() {
            @Override
            public void onResponse(Call<InitiateTransferResponse> call, Response<InitiateTransferResponse> response) {
                if(response.code() ==200){
                    if( response.body().getStatus().equalsIgnoreCase("0") ){
                        Toast.makeText(getContext(),response.body().getMessage()+"",Toast.LENGTH_LONG).show();
                    }else{
                        getOTPFromUser(customerPhoneNumber,amount,service_code);
                    }
                    dialogLoader.hideProgressDialog();

                }
                else if(response.code() == 401) {
                    TokenAuthFragment.startAuth( true);
                    getActivity().finish();
                }
                else if (response.code() == 500) {
                    Log.e("info 500", String.valueOf(response.errorBody()) + ", code: " + response.code());
                } else if (response.code() == 400) {
                    Log.e("info 400", response.errorBody() + ", code: " + response.code());
                } else if (response.code() == 406) {

                    Log.e("info 406", String.valueOf(response.errorBody()) + ", code: " + response.code());
                } else {
                    Log.e("info 406", "Error Occurred Try again later");

                }
                dialogLoader.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<InitiateTransferResponse> call, Throwable t) {
                Log.e("info : " , String.valueOf(t.getMessage()));

                dialogLoader.hideProgressDialog();
            }
        });

    }


    private void getOTPFromUser(final String customerNumber, final double amount, String service_code) {
        otpDialog  = new Dialog(getContext());
        otpDialog.setContentView(R.layout.login_dialog_otp);
        otpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        otpDialog.setCancelable(false);

        code1= otpDialog.findViewById(R.id.otp_code1_et);
        code2= otpDialog.findViewById(R.id.otp_code2_et);
        code3= otpDialog.findViewById(R.id.otp_code3_et);
        code4= otpDialog.findViewById(R.id.otp_code4_et);
        code5=otpDialog.findViewById(R.id.otp_code5_et);
        code6= otpDialog.findViewById(R.id.otp_code6_et);
        resendtxtview= otpDialog.findViewById(R.id.login_otp_resend_code);
        tvTimer= otpDialog.findViewById(R.id.tv_timer);
        layoutResendCode= otpDialog.findViewById(R.id.layout_resend_code);
        tvChangeNumber = otpDialog.findViewById(R.id.text_view_change_number);

        CountDownTimer timer = new  CountDownTimer(30000, 1000) {

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
                    if(key.equalsIgnoreCase("transfer")){
                        comfirmAgentFundsTransfer(otp_code,customerNumber,amount,service_code);
                    }else if(key.equalsIgnoreCase("withdraw")){

                        comfirmAgentWithdraw(otp_code, customerNumber, amount ,service_code);

                    }
                }

            }
        });

        otpDialog.findViewById(R.id.login_otp_resend_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // processLogin(password,ConfirmActivity.phonenumber);
                layoutResendCode.setVisibility(View.GONE);
            }
        });

        otpDialog.findViewById(R.id.text_view_change_number).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpDialog.dismiss();
            }
        });

        otpDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams params = otpDialog.getWindow().getAttributes(); // change this to your otpDialog.

        params.x = 100; // Here is the param to set your dialog position. Same with params.x
        otpDialog.getWindow().setAttributes(params);
        otpDialog.show();
    }

    private  void comfirmAgentFundsTransfer(String otp_code, String customerNumber, double amount, String service_code){
        String receiverPhoneNumber=textPhoneNumber.getText().toString();
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        dialogLoader.showProgressDialog();

        Call<InitiateWithdrawResponse> call = APIClient.getWalletInstance(getContext()).
                confirmAgentTransfer(
                        access_token,
                        amount,
                        otp_code,
                        customerNumber,
                        receiverPhoneNumber,
                        request_id,
                        category,
                        "completeAgentCustomerTransfer",
                        service_code);


        call.enqueue(new Callback<InitiateWithdrawResponse>() {
            @Override
            public void onResponse(Call<InitiateWithdrawResponse> call, Response<InitiateWithdrawResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("1")) {

                        //success message
                        dialogLoader.hideProgressDialog();
                        final Dialog dialog = new Dialog(getContext());
                        dialog.setContentView(R.layout.dialog_successful_message);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.setCancelable(false);
                        TextView text = dialog.findViewById(R.id.dialog_success_txt_message);
                        text.setText( response.body().getMessage() );


                        dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                                Intent goToWallet = new Intent(getActivity(), WalletHomeActivity.class);
                                startActivity(goToWallet);
                            }
                        });
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.show();


                    } else if(response.code()==401){
                        Toast.makeText(getContext(), "session expired", Toast.LENGTH_LONG).show();
                        TokenAuthFragment.startAuth( true);
                    }else {

                        dialogLoader.hideProgressDialog();
                        final Dialog dialog = new Dialog(getContext());
                        dialog.setContentView(R.layout.dialog_failure_message);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.setCancelable(false);
                        TextView text = dialog.findViewById(R.id.dialog_success_txt_message);
                        text.setText(response.body().getMessage());


                        dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                Intent goToWallet = new Intent(getActivity(), WalletHomeActivity.class);
                                startActivity(goToWallet);
                            }
                        });
                        dialog.show();

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

    private  void comfirmAgentWithdraw(String otp_code, String customerNumber, double amount, String service_code){
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        dialogLoader.showProgressDialog();

        Call<InitiateWithdrawResponse>  call = APIClient.getWalletInstance(getContext()).
                confirmAgentWithdraw(access_token,
                        amount,
                        otp_code,
                        customerNumber,
                        request_id,
                        category,
                        "completeAgentCustomerWithdraw",
                        service_code);


        call.enqueue(new Callback<InitiateWithdrawResponse>() {
            @Override
            public void onResponse(Call<InitiateWithdrawResponse> call, Response<InitiateWithdrawResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("1")) {

                        dialogLoader.hideProgressDialog();
                        final Dialog dialog = new Dialog(getContext());
                        dialog.setContentView(R.layout.dialog_successful_message);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.setCancelable(false);
                        TextView text = dialog.findViewById(R.id.dialog_success_txt_message);
                        text.setText( response.body().getMessage() );


                        dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                                Intent goToWallet = new Intent(getActivity(), WalletHomeActivity.class);
                                startActivity(goToWallet);
                            }
                        });
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.show();


                    }else if(response.code()==401){
                        Toast.makeText(getContext(), "session expired", Toast.LENGTH_LONG).show();
                        TokenAuthFragment.startAuth( true);
                    } else {

                        dialogLoader.hideProgressDialog();
                        final Dialog dialog = new Dialog(getContext());
                        dialog.setContentView(R.layout.dialog_failure_message);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.setCancelable(false);
                        TextView text = dialog.findViewById(R.id.dialog_success_txt_message);
                        text.setText(response.body().getMessage());


                        dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                Intent goToWallet = new Intent(getActivity(), WalletHomeActivity.class);
                                startActivity(goToWallet);
                            }
                        });
                        dialog.show();

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