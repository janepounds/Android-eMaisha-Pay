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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.fragments.wallet_fragments.TokenAuthFragment;
import com.cabral.emaishapay.models.ConfirmationDataResponse;
import com.cabral.emaishapay.models.WalletPurchaseResponse;
import com.cabral.emaishapay.models.WalletTransactionInitiation;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;
import com.google.android.material.snackbar.Snackbar;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchasePreview extends DialogFragment  {

    Button confirmBtn;
    LinearLayout error_message_layout, discount_layout;
    TextView purchase_date_label_TextView,datetimeTextView, totalTextView,mechantIdTextView,
            mechantNameTextView,errorTextView, discountTextView,merchant_label;

    String businessName;
    Context activity;
    Dialog dialog;
    DialogLoader dialogLoader;

    public PurchasePreview(String businessName){
        this.businessName=businessName;
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

        View view =inflater.inflate(R.layout.wallet_purchase_preview, null);

        builder.setView(view);
        initializeView(view);
        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity=context;
    }

    public void initializeView(View view){

        purchase_date_label_TextView = view.findViewById(R.id.purchase_date_label);
        totalTextView = view.findViewById(R.id.txt_view_crop_bill_preview_total);
        errorTextView = view.findViewById(R.id.Comfirmation_Error_textview);
        discountTextView= view.findViewById(R.id.txt_view_discount_preview_total);
        discount_layout= view.findViewById(R.id.discount_layount);
        error_message_layout= view.findViewById(R.id.linearlayount_error);
        datetimeTextView = view.findViewById(R.id.text_view_purchase_date_time);

        mechantNameTextView = view.findViewById(R.id.text_view_purchase_preview_name);
        mechantIdTextView = view.findViewById(R.id.text_view_purchase_preview_mechant_id);
        confirmBtn = view.findViewById(R.id.btn_purchase_preview_confirm);
        merchant_label = view.findViewById(R.id.txt_wallet_purchase_mechant_label);
        if( !TextUtils.isEmpty(businessName))
            mechantNameTextView.setText(businessName);
        totalTextView.setText( NumberFormat.getInstance().format(WalletTransactionInitiation.getInstance().getAmount()));

        SimpleDateFormat localFormat = new SimpleDateFormat(getString(R.string.date_format_preffered), Locale.ENGLISH);
        localFormat.setTimeZone(TimeZone.getDefault());
        String currentDateandTime = null;
        SimpleDateFormat incomingFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        incomingFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        currentDateandTime = localFormat.format(new Date());
        purchase_date_label_TextView.setText(getString(R.string.purchase_date));
        datetimeTextView.setText(currentDateandTime);


        String key = WalletTransactionInitiation.getInstance().getPayTo();
        if(key!=null && key.equalsIgnoreCase("agent")){
            merchant_label.setText("AgentID");

        }
        mechantIdTextView.setText(WalletTransactionInitiation.getInstance().getMechantId());

        dialogLoader = new DialogLoader(activity);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPayment();

            }
        });


    }



    public void processPayment(){
        String methodOfPayment= WalletTransactionInitiation.getInstance().getMethodOfPayment();
        //Inner Dialog to enter PIN
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialog);
        //LayoutInflater inflater = requireActivity().getLayoutInflater();
        View pinDialog = View.inflate(activity,R.layout.dialog_enter_pin,null);


        builder.setView(pinDialog);
        dialog = builder.create();
        builder.setCancelable(false);

        EditText pinEdittext =pinDialog.findViewById(R.id.etxt_create_agent_pin);



        pinDialog.findViewById(R.id.txt_custom_add_agent_submit_pin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !TextUtils.isEmpty(pinEdittext.getText()) && pinEdittext.getText().length()==4){
                    String service_code =WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION+ pinEdittext.getText().toString() ;
                    dialogLoader.showProgressDialog();
                    if( methodOfPayment.equalsIgnoreCase("wallet") || methodOfPayment.equalsIgnoreCase("eMaisha Pay") ){
                        processWalletPayment(service_code );
                    }
                    else if(methodOfPayment.equalsIgnoreCase("eMaisha Card") || methodOfPayment.equalsIgnoreCase("Bank Cards") ){

                       // processCardPayment(service_code);
                    }
                    else if(methodOfPayment.equalsIgnoreCase("Mobile Money")){

                        payMerchantMobileMoney(service_code);
                    }

                }else {
                    pinEdittext.setError("Invalid Pin Entered");
                }

            }
        });

        dialog.show();

    }

    private void payMerchantMobileMoney(String service_code) {

        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE, requireContext());
        String mobileNumber=getString(R.string.phone_number_code)+ WalletTransactionInitiation.getInstance().getMobileNumber();
        String merchant_id= WalletTransactionInitiation.getInstance().getMechantId();
        double amount = WalletTransactionInitiation.getInstance().getAmount();
        String key = WalletTransactionInitiation.getInstance().getPayTo();

        if(key.equalsIgnoreCase("agent")){
            //call agent endpoint
            /******************RETROFIT IMPLEMENTATION***********************/
            Call<WalletPurchaseResponse> call = APIClient.getWalletInstance(activity).customerPayAgentMobile(
                    access_token, amount, mobileNumber,
                    request_id,
                    category, "customerPayAgentViaMobileMoney", service_code, merchant_id);
            call.enqueue(new Callback<WalletPurchaseResponse>() {
                @Override
                public void onResponse(Call<WalletPurchaseResponse> call, Response<WalletPurchaseResponse> response) {
                    if (response.isSuccessful()) {
                        dialogLoader.hideProgressDialog();
                        if (response.body().getStatus().equalsIgnoreCase("1")) {
                            //call success dialog
                            final Dialog dialog = new Dialog(activity);
                            dialog.setContentView(R.layout.dialog_successful_message);
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

                        TokenAuthFragment.startAuth(true);

                        if (response.errorBody() != null) {
                            Log.e("info", new String(String.valueOf(response.errorBody())));
                        } else {
                            Log.e("info", "Something got very very wrong");
                        }
                    }

                }

                @Override
                public void onFailure(Call<WalletPurchaseResponse> call, Throwable t) {
                    dialogLoader.hideProgressDialog();
                }
            });

        }else {


            /******************RETROFIT IMPLEMENTATION***********************/
            Call<WalletPurchaseResponse> call = APIClient.getWalletInstance(activity).customerPayMerchantMobile(
                    access_token, amount, mobileNumber,
                    request_id,
                    category, "customerPayMerchantViaMobileMoney", service_code, merchant_id);
            call.enqueue(new Callback<WalletPurchaseResponse>() {
                @Override
                public void onResponse(Call<WalletPurchaseResponse> call, Response<WalletPurchaseResponse> response) {
                    if (response.isSuccessful()) {
                        dialogLoader.hideProgressDialog();
                        if (response.body().getStatus().equalsIgnoreCase("1")) {
                            //call success dialog
                            final Dialog dialog = new Dialog(activity);
                            dialog.setContentView(R.layout.dialog_successful_message);
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

                        TokenAuthFragment.startAuth(true);

                        if (response.errorBody() != null) {
                            Log.e("info", new String(String.valueOf(response.errorBody())));
                        } else {
                            Log.e("info", "Something got very very wrong");
                        }
                    }

                }

                @Override
                public void onFailure(Call<WalletPurchaseResponse> call, Throwable t) {
                    dialogLoader.hideProgressDialog();
                }
            });

        }
        }

    private void processWalletPayment(String service_code) {
        String merchantId =WalletTransactionInitiation.getInstance().getMechantId();
        double amount = WalletTransactionInitiation.getInstance().getAmount();
        String coupon  = WalletTransactionInitiation.getInstance().getCoupon();
        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());


        Call<WalletPurchaseResponse> call = apiRequests.makeTransaction(access_token,merchantId,amount,coupon,request_id,category,"payMerchant",service_code);

        call.enqueue(new Callback<WalletPurchaseResponse>() {
            @Override
            public void onResponse(Call<WalletPurchaseResponse> call, Response<WalletPurchaseResponse> response) {
                if(response.code()== 200){
                    dialog.dismiss();
                    dialogLoader.hideProgressDialog();
                    if(response.body().getStatus().equals("0")){

                        try {
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
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.e("Error", e.getMessage());
                        }
                    }else {

                        final Dialog dialog = new Dialog(activity);
                        dialog.setContentView(R.layout.dialog_successful_message);
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


                }else{
                    errorTextView.setText(response.errorBody().toString());
                    error_message_layout.setVisibility(View.VISIBLE);
                    errorTextView.setVisibility(View.VISIBLE);
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

                errorTextView.setText("Error occured! Try again later");
                error_message_layout.setVisibility(View.VISIBLE);
                errorTextView.setVisibility(View.VISIBLE);
                dialogLoader.hideProgressDialog();

            }

        });
    }

}
