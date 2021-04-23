package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnterPin extends DialogFragment {
    private String totalAmount,phoneNumber;
    private Button submit;
    private EditText confirm_pin;
    private double balance;  String key = "";



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
            key = getArguments().getString("key");
        }
        if(key.equalsIgnoreCase("deposit")){
            TextView dialog_title=view.findViewById(R.id.dialog_title);
            dialog_title.setText("ENTER AGENT PIN");
        }


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String request_id = WalletHomeActivity.generateRequestId();
                String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());

                ProgressDialog dialog;
                dialog = new ProgressDialog(v.getContext());
                dialog.setIndeterminate(true);
                dialog.setMessage("Please Wait..");
                dialog.setCancelable(false);
                dialog.show();
                String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;



                if(getArguments().getString("key").equalsIgnoreCase("deposit")) {
                    String[] amount = totalAmount.split("\\s+");
                    String amount_only = amount[1];
                    /***************RETROFIT IMPLEMENTATION FOR DEPOSIT************************/
                    Call<InitiateWithdrawResponse> call = APIClient.getWalletInstance(getContext()).confrmDeposit(access_token, Double.parseDouble(amount_only), "12" + confirm_pin.getText().toString(), phoneNumber, request_id, category, "merchantInitiateDeposit");
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

                }else if(key.equalsIgnoreCase("mobile_deposit")){

                    double amount_entered = Float.parseFloat(totalAmount);

                    //********************* RETROFIT IMPLEMENTATION ********************************//
                    APIRequests apiRequests = APIClient.getWalletInstance(getContext());
                    Call<WalletTransaction> call = apiRequests.depositMobileMoney(access_token,amount_entered,phoneNumber,request_id,category,"customerMobileMoneyDeposit","12"+confirm_pin.getText().toString());
                    call.enqueue(new Callback<WalletTransaction>() {
                        @Override
                        public void onResponse(Call<WalletTransaction> call, Response<WalletTransaction> response) {
                            if(response.code() == 200){
                                if(response.body().getStatus().equalsIgnoreCase("1")){
                                    dialog.dismiss();
                                    Toast.makeText(getContext(),response.body().getMessage(),Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(getContext(), WalletHomeActivity.class);
                                    startActivity(intent);

                                }else {
                                    dialog.dismiss();
                                    Toast.makeText(getContext(),response.body().getMessage(),Toast.LENGTH_LONG).show();

                                }


                            }else if(response.code() == 401){

                                TokenAuthFragment.startAuth( true);

                            } else if (response.code() == 500) {
                                if (response.errorBody() != null) {
                                    Toast.makeText(getContext(),response.body().getMessage(), Toast.LENGTH_LONG).show();
                                } else {

                                    Log.e("info", "Something got very very wrong, code: " + response.code());
                                }
                                Log.e("info 500", String.valueOf(response.errorBody()) + ", code: " + response.code());
                            } else if (response.code() == 400) {
                                if (response.errorBody() != null) {
                                    Toast.makeText(getContext(), response.errorBody().toString(), Toast.LENGTH_LONG).show();
                                } else {

                                    Log.e("info", "Something got very very wrong, code: " + response.code());
                                }
                                Log.e("info 500", String.valueOf(response.errorBody()) + ", code: " + response.code());
                            } else if (response.code() == 406) {
                                if (response.errorBody() != null) {

                                    Toast.makeText(getContext(), response.errorBody().toString(), Toast.LENGTH_LONG).show();
                                } else {

                                    Log.e("info", "Something got very very wrong, code: " + response.code());
                                }
                                Log.e("info 406", String.valueOf(response.errorBody()) + ", code: " + response.code());
                            } else {

                                if (response.errorBody() != null) {

                                    Toast.makeText(getContext(), response.errorBody().toString(), Toast.LENGTH_LONG).show();
                                    Log.e("info", String.valueOf(response.errorBody()) + ", code: " + response.code());
                                } else {

                                    Log.e("info", "Something got very very wrong, code: " + response.code());
                                }
                            }
                            dialog.dismiss();
                        }


                        @Override
                        public void onFailure(Call<WalletTransaction> call, Throwable t) {

                        }
                    });



                }else{
                    /***************RETROFIT IMPLEMENTATION FOR BALANCE INQUIRY************************/
                    Call<InitiateWithdrawResponse> call = APIClient.getWalletInstance(getContext()).balanceInquiry(access_token, "12"+confirm_pin.getText().toString(), phoneNumber,request_id,category,"merchantBalanceInquiry");
                    call.enqueue(new Callback<InitiateWithdrawResponse>() {
                        @Override
                        public void onResponse(Call<InitiateWithdrawResponse> call, Response<InitiateWithdrawResponse> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getStatus().equalsIgnoreCase("1")) {
                                    balance = response.body().getData().getBalance();
                                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

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



                                    // Create and show the dialog.
                                    DialogFragment balanceDialog = new BalanceDialog();
                                    balanceDialog.setArguments(bundle);

                                    balanceDialog.show(ft, "dialog");
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

    @Override
    public void dismiss() {
        super.dismiss();
        dismiss();
    }
}
