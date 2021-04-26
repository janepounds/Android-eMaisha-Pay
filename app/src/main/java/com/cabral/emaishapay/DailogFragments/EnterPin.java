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
        }else if(key.equalsIgnoreCase("mobile_deposit") && (role.equalsIgnoreCase(getString(R.string.role_agent))|| role.equalsIgnoreCase("merchant") ||role.equalsIgnoreCase(getString(R.string.role_master_agent) ) )){

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
                        Call<InitiateWithdrawResponse> call = null;
                         if(category.equalsIgnoreCase(getString(R.string.role_master_agent))  ){
                             call= APIClient.getWalletInstance(getContext()).confrmMasterAgentDeposit(access_token, Double.parseDouble(amount_only), phoneNumber, request_id, category,"merchantInitiateDeposit",service_code);

                         }else if( category.equalsIgnoreCase(getString(R.string.role_agent))  ){
                             call = APIClient.getWalletInstance(getContext()).confrmDeposit(access_token, Double.parseDouble(amount_only), phoneNumber, request_id, category,"masterAgentInitiateCustomerDeposit",service_code);

                         }
                        
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
                    else if (key.equalsIgnoreCase("mobile_deposit")) {

                        if (role.equalsIgnoreCase(getString(R.string.role_agent))) {
                            //call agent end point

                            dialogLoader.showProgressDialog();
                            double amount_entered = Float.parseFloat(totalAmount);

                            //********************* RETROFIT IMPLEMENTATION ********************************//
                            APIRequests apiRequests = APIClient.getWalletInstance(getContext());
                            Call<WalletTransaction> call = apiRequests.depositMobileMoneyAgent(access_token, amount_entered, phoneNumber, request_id, category, "agentMobileMoneyDeposit", "12" + confirm_pin.getText().toString());
                            call.enqueue(new Callback<WalletTransaction>() {
                                @Override
                                public void onResponse(Call<WalletTransaction> call, Response<WalletTransaction> response) {
                                    dialogLoader.hideProgressDialog();

                                    if (response.code() == 200) {
                                        if (response.body().getStatus().equalsIgnoreCase("1")) {

                                            //call the success dialog
                                            final Dialog dialog = new Dialog(getContext());
                                            dialog.setContentView(R.layout.dialog_successful_message);
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

                                        } else {
                                            Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                                        }


                                    } else if (response.code() == 401) {

                                        TokenAuthFragment.startAuth(true);

                                    } else if (response.code() == 500) {
                                        if (response.errorBody() != null) {
                                            Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
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

                                }

                                @Override
                                public void onFailure(Call<WalletTransaction> call, Throwable t) {
                                    dialogLoader.hideProgressDialog();
                                }
                            });


                        } else if (role.equalsIgnoreCase("merchant")) {
                            //call merchant endpoint

                            dialogLoader.showProgressDialog();
                            double amount_entered = Float.parseFloat(totalAmount);

                            //********************* RETROFIT IMPLEMENTATION ********************************//
                            APIRequests apiRequests = APIClient.getWalletInstance(getContext());
                            Call<WalletTransaction> call = apiRequests.depositMobileMoneyMerchant(access_token, amount_entered, phoneNumber, request_id, category, "merchantMobileMoneyDeposit", "12" + confirm_pin.getText().toString());
                            call.enqueue(new Callback<WalletTransaction>() {
                                @Override
                                public void onResponse(Call<WalletTransaction> call, Response<WalletTransaction> response) {
                                    dialogLoader.hideProgressDialog();

                                    if (response.code() == 200) {
                                        if (response.body().getStatus().equalsIgnoreCase("1")) {

                                            //call the success dialog
                                            final Dialog dialog = new Dialog(getContext());
                                            dialog.setContentView(R.layout.dialog_successful_message);
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

                                        } else {
                                            Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                                        }


                                    } else if (response.code() == 401) {

                                        TokenAuthFragment.startAuth(true);

                                    } else if (response.code() == 500) {
                                        if (response.errorBody() != null) {
                                            Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
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

                                }

                                @Override
                                public void onFailure(Call<WalletTransaction> call, Throwable t) {
                                    dialogLoader.hideProgressDialog();
                                }
                            });

                        } else if (role.equalsIgnoreCase(getString(R.string.role_master_agent)) ) {
                            //call agent merchant endpoint

                            dialogLoader.showProgressDialog();
                            double amount_entered = Float.parseFloat(totalAmount);

                            //********************* RETROFIT IMPLEMENTATION ********************************//
                            APIRequests apiRequests = APIClient.getWalletInstance(getContext());
                            Call<WalletTransaction> call = apiRequests.depositMobileMoneyAgentMerchant(access_token, amount_entered, phoneNumber, request_id, category, "merchantAgentMobileMoneyDeposit", "12" + confirm_pin.getText().toString());
                            call.enqueue(new Callback<WalletTransaction>() {
                                @Override
                                public void onResponse(Call<WalletTransaction> call, Response<WalletTransaction> response) {
                                    dialogLoader.hideProgressDialog();

                                    if (response.code() == 200) {
                                        if (response.body().getStatus().equalsIgnoreCase("1")) {

                                            //call the success dialog
                                            final Dialog dialog = new Dialog(getContext());
                                            dialog.setContentView(R.layout.dialog_successful_message);
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

                                        } else {
                                            Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                                        }


                                    } else if (response.code() == 401) {

                                        TokenAuthFragment.startAuth(true);

                                    } else if (response.code() == 500) {
                                        if (response.errorBody() != null) {
                                            Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
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

                                }

                                @Override
                                public void onFailure(Call<WalletTransaction> call, Throwable t) {
                                    dialogLoader.hideProgressDialog();
                                }
                            });


                        } else {

                        dialogLoader.showProgressDialog();
                        double amount_entered = Float.parseFloat(totalAmount);

                        //********************* RETROFIT IMPLEMENTATION ********************************//
                        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
                        Call<WalletTransaction> call = apiRequests.depositMobileMoney(access_token, amount_entered, phoneNumber, request_id, category, "customerMobileMoneyDeposit", "12" + confirm_pin.getText().toString());

                        call.enqueue(new Callback<WalletTransaction>() {
                            @Override
                            public void onResponse(Call<WalletTransaction> call, Response<WalletTransaction> response) {
                                dialogLoader.hideProgressDialog();

                                if (response.code() == 200) {
                                    if (response.body().getStatus().equalsIgnoreCase("1")) {

                                        //call the success dialog
                                        final Dialog dialog = new Dialog(getContext());
                                        dialog.setContentView(R.layout.dialog_successful_message);
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

                                    } else {
                                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                                    }


                                } else if (response.code() == 401) {

                                    TokenAuthFragment.startAuth(true);

                                } else if (response.code() == 500) {
                                    if (response.errorBody() != null) {
                                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
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

                            }

                            @Override
                            public void onFailure(Call<WalletTransaction> call, Throwable t) {
                                dialogLoader.hideProgressDialog();
                            }
                        });
                    }


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