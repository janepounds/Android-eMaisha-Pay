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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.fragments.wallet_fragments.TokenAuthFragment;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.ugmobilemoney.UgandaMobileMoneyPaymentCallback;
import com.flutterwave.raveandroid.rave_presentation.ugmobilemoney.UgandaMobileMoneyPaymentManager;
import com.flutterwave.raveutils.verification.RaveVerificationUtils;
import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.models.WalletTransaction;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;
import com.google.android.material.snackbar.Snackbar;

import java.text.NumberFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DepositMoneyMobile extends DialogFragment {
    LinearLayout layoutAddMoney;
    Button addMoneyImg;
    TextView addMoneyTxt, phoneNumberTxt, errorMsgTxt;
    TextView balanceTextView,dialog_title;;
    double balance;
    private String txRef,role;
    private Button submit;
    private EditText confirm_pin;

    private DialogLoader dialogLoader;;
    Context activity;
    private RaveVerificationUtils verificationUtils;

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
        addMoneyImg.setText("Top Up");
        balanceTextView.setText(NumberFormat.getInstance().format(balance));
        this.txRef = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, this.activity) + (new Date().getTime());
        addMoneyImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterPin();
            }
        });

//        verificationUtils = new RaveVerificationUtils(this, false, BuildConfig.PUBLIC_KEY);

    }
    public void enterPin(){

        //call inner dialog
        final Dialog dialog1 = new Dialog(getContext());
        dialog1.setContentView(R.layout.dialog_enter_pin);
        confirm_pin = dialog1.findViewById(R.id.etxt_create_agent_pin);
        dialog_title =dialog1.findViewById(R.id.dialog_title);
        submit = dialog1.findViewById(R.id.txt_custom_add_agent_submit_pin);
    if( role.equalsIgnoreCase("merchant")){

        dialog_title.setText("ENTER MERCHANT PIN");
    }else if(role.equalsIgnoreCase(getString(R.string.role_master_agent) )){

        dialog_title.setText("ENTER AGENT PIN");
    }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(confirm_pin.getText().toString()) && confirm_pin.getText().toString().length() == 4) {
                    initiateDeposit();
                }
            }
        });
        dialog1.show();




    }

    public void initiateDeposit(){
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String service_code =WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION+  confirm_pin.getText().toString();

        dialogLoader=new DialogLoader(getActivity());

        if (role.equalsIgnoreCase("merchant")) {
            //call merchant endpoint

            dialogLoader.showProgressDialog();
            double amount_entered = Float.parseFloat(addMoneyTxt.getText().toString());

            //********************* RETROFIT IMPLEMENTATION ********************************//
            APIRequests apiRequests = APIClient.getWalletInstance(getContext());
            Call<WalletTransaction> call = apiRequests.depositMobileMoneyMerchant(access_token, amount_entered, "0"+phoneNumberTxt.getText().toString(), request_id, category, "merchantMobileMoneyDeposit", "12" + confirm_pin.getText().toString());
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

        }else if (role.equalsIgnoreCase(getString(R.string.role_master_agent)) ) {
            //call agent merchant endpoint

            dialogLoader.showProgressDialog();
            double amount_entered = Float.parseFloat(addMoneyTxt.getText().toString());

            //********************* RETROFIT IMPLEMENTATION ********************************//
            APIRequests apiRequests = APIClient.getWalletInstance(getContext());
            Call<WalletTransaction> call = apiRequests.depositMobileMoneyAgentMerchant(access_token, amount_entered, "0"+phoneNumberTxt.getText().toString(), request_id, category, "merchantAgentMobileMoneyDeposit", "12" + confirm_pin.getText().toString());
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
            double amount_entered = Float.parseFloat(addMoneyTxt.getText().toString());

            //********************* RETROFIT IMPLEMENTATION ********************************//
            APIRequests apiRequests = APIClient.getWalletInstance(getContext());
            Call<WalletTransaction> call = apiRequests.depositMobileMoney(access_token, amount_entered, "0"+phoneNumberTxt.getText().toString(), request_id, category, "customerMobileMoneyDeposit", "12" + confirm_pin.getText().toString());

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

//    public void initiateDeposit(){
//        dialogLoader.showProgressDialog();
//        String phoneNumber = "0"+phoneNumberTxt.getText().toString();
//        String amountEntered = addMoneyTxt.getText().toString();
//        double amount = Float.parseFloat(amountEntered);
//        String request_id = WalletHomeActivity.generateRequestId();
//        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
//        String access_token =  WalletHomeActivity.WALLET_ACCESS_TOKEN;
//
//        //********************* RETROFIT IMPLEMENTATION ********************************//
//        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
//        Call<WalletTransaction> call = apiRequests.depositMobileMoney(access_token,amount,phoneNumber,request_id,category,"customerMobileMoneyDeposit","120224");
//        call.enqueue(new Callback<WalletTransaction>() {
//            @Override
//            public void onResponse(Call<WalletTransaction> call, Response<WalletTransaction> response) {
//                if(response.code() == 200){
//                    if(response.body().getStatus().equalsIgnoreCase("1")){
//                        dialogLoader.hideProgressDialog();
//                        Toast.makeText(getContext(),response.body().getMessage(),Toast.LENGTH_LONG).show();
////                        Snackbar.make(getContext(),getView(),"",Snackbar.LENGTH_SHORT).show();
//
//                        refreshActivity();
//
//                    }else {
//                        dialogLoader.hideProgressDialog();
//                        Toast.makeText(getContext(),response.body().getMessage(),Toast.LENGTH_LONG).show();
//
//                    }
//
//
//                }else if(response.code() == 401){
//
//                    TokenAuthFragment.startAuth( true);
//
//                } else if (response.code() == 500) {
//                    if (response.errorBody() != null) {
//                        Toast.makeText(activity,response.body().getMessage(), Toast.LENGTH_LONG).show();
//                    } else {
//
//                        Log.e("info", "Something got very very wrong, code: " + response.code());
//                    }
//                    Log.e("info 500", String.valueOf(response.errorBody()) + ", code: " + response.code());
//                } else if (response.code() == 400) {
//                    if (response.errorBody() != null) {
//                        Toast.makeText(activity, response.errorBody().toString(), Toast.LENGTH_LONG).show();
//                    } else {
//
//                        Log.e("info", "Something got very very wrong, code: " + response.code());
//                    }
//                    Log.e("info 500", String.valueOf(response.errorBody()) + ", code: " + response.code());
//                } else if (response.code() == 406) {
//                    if (response.errorBody() != null) {
//
//                        Toast.makeText(activity, response.errorBody().toString(), Toast.LENGTH_LONG).show();
//                    } else {
//
//                        Log.e("info", "Something got very very wrong, code: " + response.code());
//                    }
//                    Log.e("info 406", String.valueOf(response.errorBody()) + ", code: " + response.code());
//                } else {
//
//                    if (response.errorBody() != null) {
//
//                        Toast.makeText(activity, response.errorBody().toString(), Toast.LENGTH_LONG).show();
//                        Log.e("info", String.valueOf(response.errorBody()) + ", code: " + response.code());
//                    } else {
//
//                        Log.e("info", "Something got very very wrong, code: " + response.code());
//                    }
//                }
//                dialogLoader.hideProgressDialog();
//            }
//
//
//            @Override
//            public void onFailure(Call<WalletTransaction> call, Throwable t) {
//
//            }
//        });
//
//
//
//    }


//    public void initiateDeposit() {
//
//        dialogLoader.showProgressDialog();
//        String phoneNumber = phoneNumberTxt.getText().toString();
//        String amountEntered = addMoneyTxt.getText().toString();
//
//        double amount = Float.parseFloat(amountEntered);
//        txRef = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, this.activity) + (new Date().getTime());
//        String eMaishaPayServiceMail="info@cabraltech.com";
//        RaveNonUIManager raveNonUIManager = new RaveNonUIManager().setAmount(amount)
//                .setCurrency("UGX")
//                .setEmail(eMaishaPayServiceMail)
//                .setfName(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME, this.activity))
//                .setlName(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_LAST_NAME, this.activity))
//                .setPhoneNumber("0" + phoneNumber)
//                .setNarration("eMaisha Pay")
//                .setPublicKey(BuildConfig.PUBLIC_KEY)
//                .setEncryptionKey(BuildConfig.ENCRYPTION_KEY)
//                .setTxRef(txRef)
//                .onStagingEnv(false)
//                .isPreAuth(true)
//                .initialize();
//
//        UgandaMobileMoneyPaymentCallback mobileMoneyPaymentCallback = new UgandaMobileMoneyPaymentCallback() {
//            @Override
//            public void showProgressIndicator(boolean active) {
//                try {
//
//                    if (dialogLoader == null) {
//                        dialogLoader = new DialogLoader(getContext());
//                        dialogLoader.showProgressDialog();
//                    }
//
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(String errorMessage, @Nullable String flwRef) {
//                dialogLoader.hideProgressDialog();
//                Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show();
//                Log.e("MobileMoneypaymentError", errorMessage);
//            }
//
//            @Override
//            public void onSuccessful(String flwRef) {
//                dialogLoader.hideProgressDialog();
//                Log.e("Success code :", flwRef);
//                Toast.makeText(activity, "Transaction Successful", Toast.LENGTH_LONG).show();
//                creditAfterDeposit(flwRef,"0" + phoneNumber);
//            }
//
//            @Override
//            public void showAuthenticationWebPage(String authenticationUrl) {
//                Log.e("Loading auth web page: ", authenticationUrl);
//                verificationUtils.showWebpageVerificationScreen(authenticationUrl);
//            }
//        };
//        UgandaMobileMoneyPaymentManager mobilePayManager = new UgandaMobileMoneyPaymentManager(raveNonUIManager, (UgandaMobileMoneyPaymentCallback) mobileMoneyPaymentCallback);
//
//        mobilePayManager.charge();
//    }


    public void creditAfterDeposit(String txRef, String phonumber) {
        /******************RETROFIT IMPLEMENTATION**************************/
     
        
        dialogLoader.showProgressDialog();

        String amountEntered = addMoneyTxt.getText().toString();
        double amount = Float.parseFloat(amountEntered);
        String referenceNumber = txRef;
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());

        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<WalletTransaction> call = apiRequests.creditUser(access_token,null,amount,referenceNumber,"Deposit","flutterwave",phonumber,false,request_id,category,"creditUserAfterTransaction");
        call.enqueue(new Callback<WalletTransaction>() {
            @Override
            public void onResponse(Call<WalletTransaction> call, Response<WalletTransaction> response) {
                if(response.code() == 200){

                    dialogLoader.hideProgressDialog();

                    refreshActivity();
                }else if(response.code() == 401){

                        TokenAuthFragment.startAuth( true);

                    } else if (response.code() == 500) {
                        if (response.errorBody() != null) {
                            Toast.makeText(activity,response.body().getRecepient(), Toast.LENGTH_LONG).show();
                        } else {

                            Log.e("info", "Something got very very wrong, code: " + response.code());
                        }
                        Log.e("info 500", String.valueOf(response.errorBody()) + ", code: " + response.code());
                    } else if (response.code() == 400) {
                        if (response.errorBody() != null) {
                            Toast.makeText(activity, response.errorBody().toString(), Toast.LENGTH_LONG).show();
                        } else {

                            Log.e("info", "Something got very very wrong, code: " + response.code());
                        }
                        Log.e("info 500", String.valueOf(response.errorBody()) + ", code: " + response.code());
                    } else if (response.code() == 406) {
                        if (response.errorBody() != null) {

                            Toast.makeText(activity, response.errorBody().toString(), Toast.LENGTH_LONG).show();
                        } else {

                            Log.e("info", "Something got very very wrong, code: " + response.code());
                        }
                        Log.e("info 406", String.valueOf(response.errorBody()) + ", code: " + response.code());
                    } else {

                        if (response.errorBody() != null) {

                            Toast.makeText(activity, response.errorBody().toString(), Toast.LENGTH_LONG).show();
                            Log.e("info", String.valueOf(response.errorBody()) + ", code: " + response.code());
                        } else {

                            Log.e("info", "Something got very very wrong, code: " + response.code());
                        }
                    }
                    dialogLoader.hideProgressDialog();
                }


            @Override
            public void onFailure(Call<WalletTransaction> call, Throwable t) {

            }
        });


    }


    public void refreshActivity() {
        Intent goToWallet = new Intent(activity, WalletHomeActivity.class);
        startActivity(goToWallet);
    }


}
