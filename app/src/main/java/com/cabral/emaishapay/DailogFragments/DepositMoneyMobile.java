package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.fragments.wallet_fragments.TokenAuthFragment;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.models.MomoTransactionResponse;
import com.cabral.emaishapay.models.TransactionStatusResponse;
import com.cabral.emaishapay.models.WalletTransaction;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;

import java.text.NumberFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DepositMoneyMobile extends DialogFragment {
    LinearLayout layoutAddMoney;
    Button addMoneyImg;
    TextView addMoneyTxt, phoneNumberTxt, errorMsgTxt,title;
    TextView balanceTextView;
    double balance;
    private String role,phoneNumber;

    private DialogLoader dialogLoader;;
    Context activity;

    public DepositMoneyMobile(Context context, double balance) {
        this.activity = context;
        this.balance = balance;

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
        View view = inflater.inflate(R.layout.wallet_add_money, null);
        role = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE, getContext());

        builder.setView(view);

        initializeForm(view);

        ImageView close = view.findViewById(R.id.wallet_transfer_money_close);
        close.setOnClickListener(v -> dismiss());
        dialogLoader = new DialogLoader(getContext());
        return builder.create();

    }

    public void initializeForm(View view) {
        layoutAddMoney = view.findViewById(R.id.layout_add_money);
        phoneNumberTxt = view.findViewById(R.id.crop_add_money_mobile_no);
        addMoneyImg = view.findViewById(R.id.button_add_money);
        addMoneyTxt = view.findViewById(R.id.crop_add_money_amount);
        balanceTextView = view.findViewById(R.id.crop_add_money_balance);
        errorMsgTxt = view.findViewById(R.id.text_view_error_message);
        title = view.findViewById(R.id.digital_wallet_title_label);
        title.setText("Mobile Money");
        addMoneyImg.setText("Top Up");
        balanceTextView.setText(NumberFormat.getInstance().format(balance));

        addMoneyImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateDeposit();
            }
        });


    }

    public void initiateDeposit(){
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());

        String service_code = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_USER_PASSWORD,requireContext());
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        phoneNumber =getString(R.string.phone_number_code)+phoneNumberTxt.getText().toString();

        dialogLoader=new DialogLoader(getActivity());

        dialogLoader.showProgressDialog();
        double amount_entered = Float.parseFloat(addMoneyTxt.getText().toString());
        phoneNumber =getString(R.string.phone_number_code)+phoneNumberTxt.getText().toString();

        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<MomoTransactionResponse> call;

        if (role.equalsIgnoreCase("merchant")) {
            call = apiRequests.depositMobileMoneyMerchant(access_token, amount_entered, phoneNumber, request_id, category, "merchantMobileMoneyDeposit", service_code);

        }
        else if (role.equalsIgnoreCase(getString(R.string.role_master_agent)) ) {
            //call agent merchant endpoint
             call = apiRequests.depositMobileMoneyAgent(access_token, amount_entered,
                    phoneNumber, request_id, category, "merchantAgentMobileMoneyDeposit", service_code );
        }
        else {

            call = apiRequests.depositMobileMoney(
                    access_token,
                    amount_entered,
                    phoneNumber,
                    request_id, 
                    category,
                    "customerMobileMoneyDeposit",
                     service_code
                     );
        }


        call.enqueue(new Callback<MomoTransactionResponse>() {
            @Override
            public void onResponse(Call<MomoTransactionResponse> call, Response<MomoTransactionResponse> response) {


                if (response.code() == 200) {
                    if (response.body().getStatus()==1) {
                        String ref=response.body().getData().getReferenceNumber();
                        checkTransactionStatus( ref );
                    } else {
                        dialogLoader.hideProgressDialog();
                        
                        final Dialog dialog = new Dialog(activity);
                        dialog.setContentView(R.layout.dialog_failure_message);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.setCancelable(false);
                        TextView text = dialog.findViewById(R.id.dialog_success_txt_message);
                        text.setText(response.body().getMessage());

                        dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                Intent goToWallet = new Intent(activity, WalletHomeActivity.class);
                                startActivity(goToWallet);
                            }
                        });
                        dialog.show();

                    }


                } else if (response.code() == 401) {
                    dialogLoader.hideProgressDialog();
                    TokenAuthFragment.startAuth(true);
                    DepositMoneyMobile.this.dismiss();
                }  else {
                    dialogLoader.hideProgressDialog();
                    if (response.errorBody() != null) {

                        Toast.makeText(getContext(), response.errorBody().toString(), Toast.LENGTH_LONG).show();
                        Log.e("info", String.valueOf(response.errorBody()) + ", code: " + response.code());
                    } else {

                        Log.e("info", "Something got very very wrong, code: " + response.code());
                    }
                }


            }

            @Override
            public void onFailure(Call<MomoTransactionResponse> call, Throwable t) {
                dialogLoader.hideProgressDialog();
            }
        });


    }



    public void checkTransactionStatus(String transaction_ref){
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        String service_code = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_USER_PASSWORD,requireContext());
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;

        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<TransactionStatusResponse> call = apiRequests.checkTransactionStatus(
                    access_token,
                    transaction_ref,
                    request_id,
                "initCheckStatus",
                    service_code,
                    category
                    );

        call.enqueue(new Callback<TransactionStatusResponse>() {
            @Override
            public void onResponse(Call<TransactionStatusResponse> call, Response<TransactionStatusResponse> response) {

                handleCheckStatusResponse(response,transaction_ref);

            }

            @Override
            public void onFailure(Call<TransactionStatusResponse> call, Throwable t) {
                dialogLoader.hideProgressDialog();
            }
        });


    }
    int responseCheckCounter=0;
    private void handleCheckStatusResponse(Response<TransactionStatusResponse> response, String transaction_ref) {
        if (response.code() == 200) {
            final Dialog dialog = new Dialog(getContext());

            if(response.body().getStatus()==1) {
                if(response.body().getCode().equalsIgnoreCase("201")){
                    if(responseCheckCounter==4){
                        dialog.setContentView(R.layout.dialog_failure_message);
                        response.body().setMessage("Transaction timeout, please try again later!");
                    }else{
                        try {
                            Thread.sleep(15000);
                            responseCheckCounter++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        checkTransactionStatus(transaction_ref);
                        return;
                    }

                }else if( response.body().getCode().equalsIgnoreCase("200") ){
                    dialog.setContentView(R.layout.dialog_successful_message);
                }else {
                    dialog.setContentView(R.layout.dialog_failure_message);
                }

            } else {
                dialog.setContentView(R.layout.dialog_failure_message);
            }
            dialogLoader.hideProgressDialog();

            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
            TextView text = dialog.findViewById(R.id.dialog_success_txt_message);
            text.setText(response.body().getMessage());

            dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                    Intent goToWallet = new Intent(getContext(), WalletHomeActivity.class);
                    startActivity(goToWallet);
                }
            });
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();

        } else if (response.code() == 401) {
            dialogLoader.hideProgressDialog();
            TokenAuthFragment.startAuth(true);
        }  else {

            if (response.errorBody() != null) {
                Toast.makeText(getContext(), response.errorBody().toString(), Toast.LENGTH_LONG).show();
                Log.e("info", String.valueOf(response.errorBody()) + ", code: " + response.code());
            } else {
                Log.e("info", "Something got very wrong, code: " + response.code());
            }
        }
    }


}
