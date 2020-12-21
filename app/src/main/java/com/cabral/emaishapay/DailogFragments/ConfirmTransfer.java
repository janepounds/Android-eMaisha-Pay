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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.models.InitiateTransferResponse;
import com.cabral.emaishapay.models.MerchantInfoResponse;
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

public class ConfirmTransfer extends DialogFragment {

    Button confirmBtn;
    TextView serviceTextView, datetimeTextView, totalTextView, receiverPhoneNumber,
            receiverNameTextView, errorTextView, transactionLabelTextView, receiverLabel;
    String phoneNumber, businessName = "";
    Context activity;
    float amount;

    public ConfirmTransfer(Context context, String phoneNumber, float amount) {
        this.activity = context;
        this.phoneNumber = phoneNumber;
        this.amount = amount;

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
        View view = inflater.inflate(R.layout.wallet_purchase_preview, null);

        builder.setView(view);

        initializeView(view);
        return builder.create();

    }

    public void initializeView(View view) {

        totalTextView = view.findViewById(R.id.txt_view_crop_bill_preview_total);
        errorTextView = view.findViewById(R.id.text_view_purchase_preview_error);
        datetimeTextView = view.findViewById(R.id.text_view_purchase_date_time);
        transactionLabelTextView = view.findViewById(R.id.transaction_receiver_label);
        transactionLabelTextView.setText("Transferring to");

        receiverLabel = view.findViewById(R.id.txt_wallet_purchase_mechant_label);
        receiverLabel.setText("Receiver's Phone Number");

        receiverNameTextView = view.findViewById(R.id.text_view_purchase_preview_name);
        receiverPhoneNumber = view.findViewById(R.id.text_view_purchase_preview_mechant_id);
        confirmBtn = view.findViewById(R.id.btn_purchase_preview_confirm);
        confirmBtn.setText("Confirm Transfer");
        serviceTextView = view.findViewById(R.id.text_view_purchase_service);
        serviceTextView.setText("Money Transfer");

        totalTextView.setText(NumberFormat.getInstance().format(this.amount));

        SimpleDateFormat localFormat = new SimpleDateFormat(WalletSettingsSingleton.getInstance().getDateFormat(), Locale.ENGLISH);
        localFormat.setTimeZone(TimeZone.getDefault());
        String currentDateandTime = null;
        SimpleDateFormat incomingFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        incomingFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        currentDateandTime = localFormat.format(new Date());

        datetimeTextView.setText(currentDateandTime);

        receiverPhoneNumber.setText(this.phoneNumber);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateTransfer(phoneNumber, amount);
            }
        });

        getReceiverName(phoneNumber);
    }
    public void getReceiverName(String receiverPhoneNumber){

        /***************RETROFIT IMPLEMENTATION***********************/
        ProgressDialog dialog;
        dialog = new ProgressDialog(activity);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;

        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<MerchantInfoResponse> call = apiRequests.getUserBusinessName(access_token,receiverPhoneNumber);
        call.enqueue(new Callback<MerchantInfoResponse>() {
            @Override
            public void onResponse(Call<MerchantInfoResponse> call, Response<MerchantInfoResponse> response) {
                if(response.code()==200){
                    businessName = response.body().getData().getBusinessName();
                    receiverNameTextView.setText(businessName);
                    confirmBtn.setVisibility(View.VISIBLE);

                    dialog.dismiss();
                }else if(response.code()==412) {
                    String businessName = null;
                    businessName = response.body().getMessage();
                    receiverNameTextView.setText(businessName);
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

                receiverNameTextView.setText("Unknown Merchant");

                errorTextView.setText("Error while checking for merchant occured");
                errorTextView.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });


    }


    public void initiateTransfer(final String phoneNumber, final float amount) {
        ProgressDialog dialog;
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        Toast.makeText(activity, phoneNumber, Toast.LENGTH_LONG).show();
        dialog = new ProgressDialog(activity);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();
        /*****RETROFIT IMPLEMENTATION*****/
        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<InitiateTransferResponse> call = apiRequests.initiateTransfer(access_token, Double.parseDouble(new Float(amount).toString()),phoneNumber);
        call.enqueue(new Callback<InitiateTransferResponse>() {
            @Override
            public void onResponse(Call<InitiateTransferResponse> call, Response<InitiateTransferResponse> response) {
                if(response.code() ==200){
                    dialog.dismiss();
                    final Dialog dialog = new Dialog(activity);
                    dialog.setContentView(R.layout.wallet_dialog_one_button);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(false);
                    TextView text = dialog.findViewById(R.id.dlg_one_button_tv_message);
                    text.setText("You have transferred UGX " + NumberFormat.getInstance().format(amount) + " to " + businessName);
                    TextView title = dialog.findViewById(R.id.dlg_one_button_tv_title);
                    title.setText("SUCCESS!");
                    Button dialogButton = (Button) dialog.findViewById(R.id.dlg_one_button_btn_ok);
                    dialogButton.setText("OK");
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                            Intent goToWallet = new Intent(activity, WalletHomeActivity.class);
                            startActivity(goToWallet);
                        }
                    });
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();

                }
                else if(response.code() == 401) {
                    TokenAuthActivity.startAuth(activity, true);
                }
                    else if (response.code() == 500) {
                        errorTextView.setText("Error Occurred Try again later");
                        Log.e("info 500", new String(String.valueOf(response.errorBody())) + ", code: " + response.code());
                    } else if (response.code() == 400) {
                        errorTextView.setText("Check your input details");
                        Log.e("info 500", new String(String.valueOf(response.errorBody()) + ", code: " + response.code()));
                    } else if (response.code() == 406) {

                        Log.e("info 406", new String(String.valueOf(response.errorBody())) + ", code: " + response.code());
                    } else {
                        errorTextView.setText("Error Occurred Try again later");

                    }
                    errorTextView.setVisibility(View.VISIBLE);
                    dialog.dismiss();
            }

            @Override
            public void onFailure(Call<InitiateTransferResponse> call, Throwable t) {
                Log.e("info : " , new String(String.valueOf(t.getMessage())));
                errorTextView.setText("Error Occurred Try again later");
                errorTextView.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });

    }
}
