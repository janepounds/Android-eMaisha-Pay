package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.models.MerchantInfoResponse;
import com.cabral.emaishapay.models.WalletPurchaseResponse;
import com.cabral.emaishapay.models.WalletPurchase;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.cabral.emaishapay.singletons.WalletSettingsSingleton;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchasePreview extends DialogFragment {
    ScrollView summaryScrollView;
    Button confirmBtn;
    LinearLayout error_message_layout, discount_layout;
    TextView datetimeTextView, totalTextView,mechantIdTextView,
            mechantNameTextView,errorTextView, discountTextView;

    String businessName ="";
    Context activity;
    public PurchasePreview(Context context){
        this.activity=context;

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
        View view =inflater.inflate(R.layout.wallet_purchase_preview, null);

        builder.setView(view);

        initializeView(view);
        return builder.create();

    }
    public void initializeView(View view){

        totalTextView = view.findViewById(R.id.txt_view_crop_bill_preview_total);
        errorTextView = view.findViewById(R.id.Comfirmation_Error_textview);
        discountTextView= view.findViewById(R.id.txt_view_discount_preview_total);
        discount_layout= view.findViewById(R.id.discount_layount);
        error_message_layout= view.findViewById(R.id.linearlayount_error);
        datetimeTextView = view.findViewById(R.id.text_view_purchase_date_time);

        mechantNameTextView = view.findViewById(R.id.text_view_purchase_preview_name);
        mechantIdTextView = view.findViewById(R.id.text_view_purchase_preview_mechant_id);
        confirmBtn = view.findViewById(R.id.btn_purchase_preview_confirm);

        totalTextView.setText( NumberFormat.getInstance().format(WalletPurchase.getInstance().getAmount()));

        SimpleDateFormat localFormat = new SimpleDateFormat(WalletSettingsSingleton.getInstance().getDateFormat(), Locale.ENGLISH);
        localFormat.setTimeZone(TimeZone.getDefault());
        String currentDateandTime = null;
        SimpleDateFormat incomingFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        incomingFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            currentDateandTime = localFormat.format(new Date());

        datetimeTextView.setText(currentDateandTime);

        mechantIdTextView.setText(WalletPurchase.getInstance().getMechantId());
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPayment();
            }
        });
        //confirmBtn.setVisibility(View.GONE);

        getMechantName();
    }

    public void getMechantName(){

        /***************RETROFIT IMPLEMENTATION***********************/
        ProgressDialog dialog;
        dialog = new ProgressDialog(activity);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        int merchantId = Integer.parseInt(WalletPurchase.getInstance().getMechantId());
        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<MerchantInfoResponse> call = apiRequests.getMerchant(access_token,merchantId);
        call.enqueue(new Callback<MerchantInfoResponse>() {
            @Override
            public void onResponse(Call<MerchantInfoResponse> call, Response<MerchantInfoResponse> response) {
                if(response.code()==200){
                    businessName = response.body().getData().getBusinessName();
                    mechantNameTextView.setText(businessName);
                    confirmBtn.setVisibility(View.VISIBLE);

                    dialog.dismiss();
                }else if(response.code()==412) {
                    String businessName = null;
                    businessName = response.body().getMessage();
                    mechantNameTextView.setText(businessName);
                    errorTextView.setText(businessName);
                    errorTextView.setVisibility(View.VISIBLE);
                    // confirmBtn.setEnabled(true);
                }
                else if(response.code()==401){
                    TokenAuthActivity.startAuth(activity, true);
                }
                if (response.errorBody() != null) {
                    Log.e("info", String.valueOf(response.errorBody()));
                } else {
                    Log.e("info", "Something got very very wrong");
                }

                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<MerchantInfoResponse> call, Throwable t) {

                    Log.e("info : ", t.getMessage());
                    Log.e("info : ", "Something got very very wrong");

                mechantNameTextView.setText("Unknown Merchant");

                errorTextView.setText("Error while checking for mechant occured");
                errorTextView.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });


    }

    public void processPayment(){

        /*****RETROFIT IMPLEMENTATION******/
        int merchantId = Integer.parseInt(WalletPurchase.getInstance().getMechantId());
        double amount = WalletPurchase.getInstance().getAmount();
        String coupon  = WalletPurchase.getInstance().getCoupon();
        APIRequests apiRequests = APIClient.getWalletInstance();
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        ProgressDialog dialog;
        dialog = new ProgressDialog(activity);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();
        Call<WalletPurchaseResponse> call = apiRequests.makeTransaction(access_token,merchantId,amount,coupon);

        call.enqueue(new Callback<WalletPurchaseResponse>() {
            @Override
            public void onResponse(Call<WalletPurchaseResponse> call, Response<WalletPurchaseResponse> response) {
                if(response.code()== 200){
                    dialog.dismiss();
                    final Dialog dialog = new Dialog(activity);
                    dialog.setContentView(R.layout.wallet_dialog_one_button);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(false);
                    TextView text = dialog.findViewById(R.id.dlg_one_button_tv_message);
                    text.setText("Processed Purchase worth UGX "+ NumberFormat.getInstance().format(WalletPurchase.getInstance().getAmount())+" from "+businessName);
                    TextView title = dialog.findViewById(R.id.dlg_one_button_tv_title);
                    title.setText("SUCCESS!");
                    Button dialogButton = (Button) dialog.findViewById(R.id.dlg_one_button_btn_ok);
                    dialogButton.setText("OK");
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Intent goToWallet = new Intent(activity, WalletHomeActivity.class);
                            startActivity(goToWallet);
                        }
                    });
                    dialog.show();

                }else{
                    errorTextView.setText(response.errorBody().toString());
                    error_message_layout.setVisibility(View.VISIBLE);
                    errorTextView.setVisibility(View.VISIBLE);
                    if(response.errorBody() != null){
                        Log.e("BACKUP RESPONSE 1A"+response.code(),response.errorBody().toString());
                    }

                    dialog.dismiss();

                }
            }

            @Override
            public void onFailure(Call<WalletPurchaseResponse> call, Throwable t) {

                    Log.e("info 1A: ", t.getMessage());
                    Log.e("info 1A: ", "Something got very very wrong");

                errorTextView.setText("Error occured! Try again later");
                error_message_layout.setVisibility(View.VISIBLE);
                errorTextView.setVisibility(View.VISIBLE);
                dialog.dismiss();

            }

        });


    }
}
