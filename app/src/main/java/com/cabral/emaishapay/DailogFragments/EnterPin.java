package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.fragments.wallet_fragments.TokenAuthFragment;
import com.cabral.emaishapay.models.InitiateWithdrawResponse;
import com.cabral.emaishapay.models.WalletTransaction;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.NumberFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnterPin extends DialogFragment {
    private String totalAmount,phoneNumber;
    private Button submit;
    private EditText confirm_pin;
    private double balance;  String key = "";
    Dialog agentPinDialog, balancePreviewDialog;
    DialogLoader dialogLoader;
    String role;
    TextView dialog_title;



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
        View view = inflater.inflate(R.layout.dialog_enter_pin, null);
        confirm_pin = view.findViewById(R.id.etxt_create_agent_pin);
        dialog_title =view.findViewById(R.id.dialog_title);
        submit = view.findViewById(R.id.txt_custom_add_agent_submit_pin);
        role = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE, getContext());
        if(getArguments()!=null){
            totalAmount = getArguments().getString("amount");
            phoneNumber = getArguments().getString("phone_number");
            key = getArguments().getString("key");
        }
        if(key.equalsIgnoreCase("deposit")){
            dialog_title.setText("ENTER AGENT PIN");
        }


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(confirm_pin.getText().toString()) && confirm_pin.getText().toString().length() == 4) {


                    String request_id = WalletHomeActivity.generateRequestId();
                    String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
                    String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
                    String service_code =WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION+  confirm_pin.getText().toString();

                    dialogLoader=new DialogLoader(getActivity());

                    if(getArguments().getString("key").equalsIgnoreCase("deposit")) {
                        String[] amount = totalAmount.split("\\s+");
                        String amount_only = amount[1];
                        dialogLoader.showProgressDialog();
                        /***************RETROFIT IMPLEMENTATION FOR DEPOSIT************************/
                        Call<InitiateWithdrawResponse> call= APIClient.getWalletInstance(getContext()).confrmAgentDeposit(access_token, Double.parseDouble(amount_only), phoneNumber, request_id, category,"merchantInitiateDeposit",service_code);


                        call.enqueue(new Callback<InitiateWithdrawResponse>() {
                            @Override
                            public void onResponse(Call<InitiateWithdrawResponse> call, Response<InitiateWithdrawResponse> response) {
                                if (response.isSuccessful()) {
                                    dialogLoader.hideProgressDialog();
                                    if (response.body().getStatus().equalsIgnoreCase("1")) {
                                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                                        //success message
                                        Intent intent = new Intent(getContext(), WalletHomeActivity.class);
                                        startActivity(intent);

                                    } else {
                                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                                       EnterPin.this.dismiss();

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
                    else{
                        prepareBalanceRequest(request_id,category,access_token,getArguments().getString("customer_name"));
                    }


                }
            }
            });

        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(true);

        return dialog;

    }

    private void prepareBalanceRequest(String request_id, String category, String access_token, String customer_name) {
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

        pinDialog.findViewById(R.id.txt_custom_add_agent_submit_pin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !TextUtils.isEmpty(pinEdittext.getText()) && pinEdittext.getText().length()==4){
                    String service_code= WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION+ pinEdittext.getText().toString();
                    String customerPin = WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION+confirm_pin.getText().toString();

                    requestBalanceInquiry(customer_name,access_token,customerPin,request_id,category,service_code);

                }else {
                    pinEdittext.setError("Invalid Pin Entered");
                }

            }
        });

        agentPinDialog.show();
    }

    private void requestBalanceInquiry(String customer_name, String access_token, String customerPin, String request_id, String category, String service_code) {

        dialogLoader.showProgressDialog();

        /***************RETROFIT IMPLEMENTATION FOR BALANCE INQUIRY************************/
        Call<InitiateWithdrawResponse> call = APIClient.getWalletInstance(getContext()).balanceInquiry(access_token,customerPin , this.phoneNumber,request_id,category,"merchantBalanceInquiry",service_code);
        call.enqueue(new Callback<InitiateWithdrawResponse>() {
            @Override
            public void onResponse(Call<InitiateWithdrawResponse> call, Response<InitiateWithdrawResponse> response) {

                dialogLoader.hideProgressDialog();
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("1")) {
                        balance = response.body().getData().getBalance();
                        //Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                        //call balance dialog
                        FragmentManager fragmentManager = getChildFragmentManager();

                        FragmentTransaction ft = fragmentManager.beginTransaction();
                        Fragment prev = fragmentManager.findFragmentByTag("dialog");
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);
                        Bundle bundle = new Bundle();
                        bundle.putString("balance",balance+"");
                        bundle.putString("customer_name",customer_name);

                        // Create and show the dialog.
                        DialogFragment balanceDialog = new BalanceDialog();
                        balanceDialog.setArguments(bundle);

                        balanceDialog.show(ft, "dialog");

                    } else {
                        EnterPin.this.dismiss();
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                        Log.w("BalanceError",response.body().getMessage());
                    }
                }


            }

            @Override
            public void onFailure(Call<InitiateWithdrawResponse> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Log.e("BalanceError","something went wrong");
                EnterPin.this.dismiss();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getContext(), WalletHomeActivity.class);
                startActivity(intent);

            }
        });
    }


}