package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.models.InitiateWithdrawResponse;
import com.cabral.emaishapay.network.APIClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnterPin extends DialogFragment {
    private String totalAmount,phoneNumber;
    private Button submit;
    private EditText confirm_pin;

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
        submit = view.findViewById(R.id.txt_custom_add_agent_submit_pin);
        if(getArguments()!=null){
            totalAmount = getArguments().getString("amount");
            phoneNumber = getArguments().getString("phone_number");


        }



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ProgressDialog dialog;
                dialog = new ProgressDialog(v.getContext());
                dialog.setIndeterminate(true);
                dialog.setMessage("Please Wait..");
                dialog.setCancelable(false);
                dialog.show();
                String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;

                String[] amount = totalAmount.split("\\s+");
                String amount_only = amount[1];

                if (getArguments().getString("key").equalsIgnoreCase("withdraw")) {
                    //submit details
                    /***************RETROFIT IMPLEMENTATION FOR WITHDRAW************************/
                    Call<InitiateWithdrawResponse> call = APIClient.getWalletInstance().initiateWithdraw(access_token, Integer.parseInt(amount_only), "12"+confirm_pin.getText().toString(), phoneNumber);
                    call.enqueue(new Callback<InitiateWithdrawResponse>() {
                        @Override
                        public void onResponse(Call<InitiateWithdrawResponse> call, Response<InitiateWithdrawResponse> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getStatus().equalsIgnoreCase("1")) {
                                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    //success message
                                    Intent intent = new Intent(getContext(), WalletHomeActivity.class);
                                    startActivity(intent);
                                    dialog.dismiss();

                                } else {
                                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    //redirect to home;
                                    Intent intent = new Intent(getContext(), WalletHomeActivity.class);
                                    startActivity(intent);


                                    dialog.dismiss();

                                }
                            }


                        }

                        @Override
                        public void onFailure(Call<InitiateWithdrawResponse> call, Throwable t) {
                            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getContext(), WalletHomeActivity.class);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });
//
//
                }else if(getArguments().getString("key").equalsIgnoreCase("deposit")) {
                    /***************RETROFIT IMPLEMENTATION FOR DEPOSIT************************/
                    Call<InitiateWithdrawResponse> call = APIClient.getWalletInstance().initiateDeposit(access_token, Integer.parseInt(amount_only), "12"+confirm_pin.getText().toString(), phoneNumber);
                    call.enqueue(new Callback<InitiateWithdrawResponse>() {
                        @Override
                        public void onResponse(Call<InitiateWithdrawResponse> call, Response<InitiateWithdrawResponse> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getStatus().equalsIgnoreCase("1")) {
                                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    //success message
                                    Intent intent = new Intent(getContext(), WalletHomeActivity.class);
                                    startActivity(intent);
                                    dialog.dismiss();

                                } else {
                                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    //redirect to home;
                                    Intent intent = new Intent(getContext(), WalletHomeActivity.class);
                                    startActivity(intent);


                                    dialog.dismiss();

                                }
                            }


                        }

                        @Override
                        public void onFailure(Call<InitiateWithdrawResponse> call, Throwable t) {
                            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getContext(), WalletHomeActivity.class);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });
                }else{

                    /***************RETROFIT IMPLEMENTATION FOR TRANSFER FUNDS************************/
                    String customer_no ="0"+ getArguments().getString("customer_no");
                    Call<InitiateWithdrawResponse> call = APIClient.getWalletInstance().initiateTransfer(access_token, Integer.parseInt(amount_only), "12"+confirm_pin.getText().toString(),customer_no, phoneNumber);
                    call.enqueue(new Callback<InitiateWithdrawResponse>() {
                        @Override
                        public void onResponse(Call<InitiateWithdrawResponse> call, Response<InitiateWithdrawResponse> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getStatus().equalsIgnoreCase("1")) {
                                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    //success message
                                    Intent intent = new Intent(getContext(), WalletHomeActivity.class);
                                    startActivity(intent);
                                    dialog.dismiss();

                                } else {
                                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    //redirect to home;
                                    Intent intent = new Intent(getContext(), WalletHomeActivity.class);
                                    startActivity(intent);


                                    dialog.dismiss();

                                }
                            }


                        }

                        @Override
                        public void onFailure(Call<InitiateWithdrawResponse> call, Throwable t) {
                            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getContext(), WalletHomeActivity.class);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });

                }
            }

        });

        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        return dialog;

    }
}
