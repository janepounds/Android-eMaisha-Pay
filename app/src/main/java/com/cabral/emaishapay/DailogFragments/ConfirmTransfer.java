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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.models.InitiateTransferResponse;
import com.cabral.emaishapay.models.ConfirmationDataResponse;
import com.cabral.emaishapay.models.WalletTransactionInitiation;
import com.cabral.emaishapay.models.external_transfer_model.BankTransferResponse;
import com.cabral.emaishapay.models.external_transfer_model.SettlementResponse;
import com.cabral.emaishapay.models.external_transfer_model.TransferFeeResponse;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.cabral.emaishapay.network.ExternalAPIRequests;
import com.cabral.emaishapay.network.FlutterwaveV3APIClient;
import com.cabral.emaishapay.singletons.WalletSettingsSingleton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

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
            receiverNameTextView, errorTextView, transactionLabelTextView, receiverLabel,chargesTextView;
    String businessName = "";
    Context activity;
    LinearLayout charges_layount, discount_layount;
    DialogLoader dialogLoader;

    public ConfirmTransfer(Context context, WalletTransactionInitiation instance) {
        this.activity = context;
        WalletTransactionInitiation.setPurchase(instance);
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

       // navController = Navigation.findNavController(view);
        initializeView(view);
        return builder.create();

    }

    public void initializeView(View view) {
        totalTextView = view.findViewById(R.id.txt_view_crop_bill_preview_total);
        errorTextView = view.findViewById(R.id.text_view_purchase_preview_error);
        datetimeTextView = view.findViewById(R.id.text_view_purchase_date_time);
        transactionLabelTextView = view.findViewById(R.id.transaction_receiver_label);
        transactionLabelTextView.setText("Transferring to");
        chargesTextView = view.findViewById(R.id.txt_view_charges);
        receiverLabel = view.findViewById(R.id.txt_wallet_purchase_mechant_label);
        receiverLabel.setText("Receiver's Phone Number");

        receiverNameTextView = view.findViewById(R.id.text_view_purchase_preview_name);
        receiverPhoneNumber = view.findViewById(R.id.text_view_purchase_preview_mechant_id);
        confirmBtn = view.findViewById(R.id.btn_purchase_preview_confirm);
        confirmBtn.setText("Confirm Transfer");
        serviceTextView = view.findViewById(R.id.text_view_purchase_service);
        serviceTextView.setText("Money Transfer");
        charges_layount= view.findViewById(R.id.charges_layount);
        discount_layount= view.findViewById(R.id.discount_layount);

        dialogLoader = new DialogLoader(activity);

        double amount=WalletTransactionInitiation.getInstance().getAmount();
        String phoneNumber= WalletTransactionInitiation.getInstance().getMobileNumber();

        totalTextView.setText(getString(R.string.currency)+" "+NumberFormat.getInstance().format(amount));

        SimpleDateFormat localFormat = new SimpleDateFormat(WalletSettingsSingleton.getInstance().getDateFormat(), Locale.ENGLISH);
        localFormat.setTimeZone(TimeZone.getDefault());
        String currentDateandTime = null;
        SimpleDateFormat incomingFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        incomingFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        currentDateandTime = localFormat.format(new Date());

        datetimeTextView.setText(currentDateandTime);
        receiverPhoneNumber.setText(phoneNumber);
        String methodOfTransfer=WalletTransactionInitiation.getInstance().getMethodOfPayment();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(methodOfTransfer.equalsIgnoreCase("eMaisha Account")){
                    initiateWalletTransfer(phoneNumber, amount);
                }else if(methodOfTransfer.equalsIgnoreCase("Mobile Money")){
                    String beneficiary_name = WalletTransactionInitiation.getInstance().getAccount_name();
                    mobileMoneyTransfer(beneficiary_name, "" + amount, phoneNumber);
                }else if(methodOfTransfer.equalsIgnoreCase("Bank")){
                    String account_name=WalletTransactionInitiation.getInstance().getAccount_name();
                    String account_number=WalletTransactionInitiation.getInstance().getAccountNumber();
                    String branch=WalletTransactionInitiation.getInstance().getBankBranch();
                    String bankCode=WalletTransactionInitiation.getInstance().getBankCode();
                    queueBankTransfer(account_name,""+amount,account_number,branch,bankCode);
                }else if(methodOfTransfer.equalsIgnoreCase("eMaisha Card")){

                }


            }
        });

        if(methodOfTransfer.equalsIgnoreCase("eMaisha Account")) {
            getReceiverName(phoneNumber);
            charges_layount.setVisibility(View.VISIBLE);
            discount_layount.setVisibility(View.GONE);
        }else if(methodOfTransfer.equalsIgnoreCase("Mobile Money")) {
            getFlutterwaveTransferFee(""+amount,"mobilemoney");
            String beneficiary_name = WalletTransactionInitiation.getInstance().getAccount_name();
            receiverNameTextView.setText(beneficiary_name);
            charges_layount.setVisibility(View.VISIBLE);
            discount_layount.setVisibility(View.GONE);
        }else if(methodOfTransfer.equalsIgnoreCase("Bank")) {
            getFlutterwaveTransferFee(""+amount,"account");
            String account_name = WalletTransactionInitiation.getInstance().getAccount_name();
            receiverNameTextView.setText(account_name);
            receiverPhoneNumber.setVisibility(View.GONE);
            charges_layount.setVisibility(View.VISIBLE);
            discount_layount.setVisibility(View.GONE);
        }
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
        String request_id = WalletHomeActivity.generateRequestId();

        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<ConfirmationDataResponse> call = apiRequests.getUserBusinessName(access_token,receiverPhoneNumber,"CustomersTransfer",request_id);
        call.enqueue(new Callback<ConfirmationDataResponse>() {
            @Override
            public void onResponse(Call<ConfirmationDataResponse> call, Response<ConfirmationDataResponse> response) {
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
                    TokenAuthActivity.startAuth(getActivity(), true);
                    getActivity().finishAffinity();
                }
                if (response.errorBody() != null) {
                    Log.e("info", String.valueOf(response.errorBody()));
                } else {
                    Log.e("info", "Something got very very wrong");
                }

                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ConfirmationDataResponse> call, Throwable t) {

                Log.e("info : ", t.getMessage());
                Log.e("info : ", "Something got very very wrong");

                receiverNameTextView.setText("Unknown Merchant");

                errorTextView.setText("Error while checking for merchant occured");
                errorTextView.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });


    }

    private void getFlutterwaveTransferFee( String amount, String type){
        if(type.equals("mobilemoney") || type.equals("account") ){
            String currency=getString(R.string.currency);
            dialogLoader.showProgressDialog();
            ExternalAPIRequests apiRequests = FlutterwaveV3APIClient.getFlutterwaveV3Instance();
            Call<TransferFeeResponse> call = apiRequests.getFlutterwaveTransferFee( BuildConfig.SECRET_KEY,
                    currency,
                    amount,
                    type);

            call.enqueue(new Callback<TransferFeeResponse>() {
                @Override
                public void onResponse(@NotNull Call<TransferFeeResponse> call, @NotNull Response<TransferFeeResponse> response) {


                    if (response.code() == 200 && response.body().getMessage().equals("Transfer fee fetched")) {
                        try {
                            double fees = response.body().calculateFees( Double.parseDouble(amount) );
                            chargesTextView.setText(getString(R.string.currency)+" "+NumberFormat.getInstance().format(fees));
                        } catch (Exception e) {
                            Log.e("response", response.toString());
                            e.printStackTrace();
                        }finally {
                            dialogLoader.hideProgressDialog();
                        }

                    } else {
                        dialogLoader.hideProgressDialog();
                        String message = response.body().getMessage();
                        Snackbar.make(serviceTextView, message, Snackbar.LENGTH_LONG).show();
                    }


                }

                @Override
                public void onFailure(Call<TransferFeeResponse> call, Throwable t) {
                    Log.e("info2 : ", t.getMessage());
                    dialogLoader.hideProgressDialog();
                }
            });

        }else {
            Toast.makeText(getContext(),"Wrong Type Value Supplied!!",Toast.LENGTH_LONG).show();
        }

    }


    private void mobileMoneyTransfer(String beneficiary_name, String transfer_amount, String account_number) {
        String account_bank = "MPS";
        String currency=getString(R.string.currency);
        String narration="eMaisha Pay Mobile Money Transfer outs";
        String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
        String reference= "TrO"+userId+System.currentTimeMillis();
        ExternalAPIRequests apiRequests = FlutterwaveV3APIClient.getFlutterwaveV3Instance();
        Call<BankTransferResponse> call = apiRequests.bankTransferOuts( BuildConfig.SECRET_KEY,
                reference,
                beneficiary_name,
                currency,
                narration,
                transfer_amount,
                account_number,
                null,
                account_bank);

        dialogLoader.showProgressDialog();
        call.enqueue(new Callback<BankTransferResponse>() {
            @Override
            public void onResponse(@NotNull Call<BankTransferResponse> call, @NotNull Response<BankTransferResponse> response) {
                if (response.code() == 200 && response.body().getMessage().equals("Transfer Queued Successfully")) {
                    try {
                        com.cabral.emaishapay.models.external_transfer_model.BankTransferResponse.InfoData TransferResponse =
                                response.body().getData();
                        recordTransferSettlement("completed","Mobile Money",TransferResponse,dialogLoader);

                    } catch (Exception e) {
                        Log.e("response", response.toString());
                        e.printStackTrace();
                    }

                } else {
                    dialogLoader.hideProgressDialog();
                    //Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    String message = response.body().getMessage();
                    Snackbar.make(serviceTextView, message, Snackbar.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<BankTransferResponse> call, Throwable t) {
                Log.e("info2 : ", t.getMessage());
                dialogLoader.hideProgressDialog();
            }
        });
    }

    private void queueBankTransfer(String account_name,String transfer_amount, String account_number, String branch_code, String bank_code){

        String currency=getString(R.string.currency);
        String narration="eMaisha Pay Bank Transfer outs";
        String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
        String reference= "TrO"+userId+System.currentTimeMillis();


        ExternalAPIRequests apiRequests = FlutterwaveV3APIClient.getFlutterwaveV3Instance();
        Call<BankTransferResponse> call = apiRequests.bankTransferOuts( BuildConfig.SECRET_KEY,
                reference,
                account_name,
                currency,
                narration,
                transfer_amount,
                account_number,
                branch_code,
                bank_code);

        dialogLoader.showProgressDialog();
        call.enqueue(new Callback<BankTransferResponse>() {
            @Override
            public void onResponse(@NotNull Call<BankTransferResponse> call, @NotNull Response<BankTransferResponse> response) {
                if (response.code() == 200 && response.body().getMessage().equals("Transfer Queued Successfully")) {
                    try {
                        com.cabral.emaishapay.models.external_transfer_model.BankTransferResponse.InfoData TransferResponse =
                                response.body().getData();
                        recordTransferSettlement("pending","Bank",TransferResponse,dialogLoader);

                    } catch (Exception e) {
                        Log.e("response", response.toString());
                        e.printStackTrace();
                    }

                } else {
                    dialogLoader.hideProgressDialog();
                    //Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    String message = response.body().getMessage();
                    Snackbar.make(serviceTextView, message, Snackbar.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<BankTransferResponse> call, Throwable t) {
                Log.e("info2 : ", t.getMessage());
                dialogLoader.hideProgressDialog();
            }
        });


    }

    public void initiateWalletTransfer(final String phoneNumber, final double amount) {
        ProgressDialog dialog;
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        Toast.makeText(activity, phoneNumber, Toast.LENGTH_LONG).show();
        dialog = new ProgressDialog(activity);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();
        /*****RETROFIT IMPLEMENTATION*****/
        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<InitiateTransferResponse> call = apiRequests.initiateTransfer(access_token, amount,phoneNumber,request_id);
        call.enqueue(new Callback<InitiateTransferResponse>() {
            @Override
            public void onResponse(Call<InitiateTransferResponse> call, Response<InitiateTransferResponse> response) {
                if(response.code() ==200){
                    dialog.dismiss();
                    final Dialog dialog = new Dialog(activity);
                    dialog.setContentView(R.layout.dialog_successful_message);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(false);
                    TextView text = dialog.findViewById(R.id.dialog_success_txt_message);
                    text.setText("You have transferred UGX " + NumberFormat.getInstance().format(amount) + " to " + businessName);


                    dialog.findViewById(R.id.dialog_success_txt_message).setOnClickListener(new View.OnClickListener() {
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
                    TokenAuthActivity.startAuth(getActivity(), true);
                    getActivity().finishAffinity();
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


    private void recordTransferSettlement(String third_party_status,String destination_type, BankTransferResponse.InfoData transferResponse, DialogLoader dialogLoader)
    {
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        Double amount =Double.parseDouble(transferResponse.getAmount());
        String thirdparty="flutterwave";
        Double third_party_fee =Double.parseDouble(transferResponse.getFee());
        String destination_account_no=transferResponse.getAccount_number();
        String beneficiary_name=transferResponse.getFull_name();
        String destination_name=transferResponse.getBank_name();
        String reference=transferResponse.getReference();
        String third_party_id=transferResponse.getId();
        String request_id = WalletHomeActivity.generateRequestId();

        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<SettlementResponse> call = apiRequests.recordSettlementTransfer(
                access_token,
                amount,
                thirdparty,
                third_party_fee,
                destination_type,
                destination_account_no,
                beneficiary_name,
                destination_name,
                reference,
                third_party_status,
                third_party_id,request_id);

        call.enqueue(new Callback<SettlementResponse>() {
            @Override
            public void onResponse(Call<SettlementResponse> call, Response<SettlementResponse> response) {
                if (response.code() == 200 && response.body().getStatus().equals("1")) {
                    dialogLoader.hideProgressDialog();

                    final Dialog dialog = new Dialog(activity);
                    dialog.setContentView(R.layout.dialog_successful_message);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(false);
                    TextView text = dialog.findViewById(R.id.dialog_success_txt_message);
                    text.setText("Transferred UGX "+ NumberFormat.getInstance().format(WalletTransactionInitiation.getInstance().getAmount())+" to "+WalletTransactionInitiation.getInstance().getAccount_name());


                    dialog.findViewById(R.id.dialog_success_txt_message).setOnClickListener(new View.OnClickListener() {
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
                    //Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    String message = response.body().getMessage();
                    Snackbar.make(serviceTextView, message, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SettlementResponse> call, Throwable t) {
                Log.e("info : " , new String(String.valueOf(t.getMessage())));
                dialogLoader.hideProgressDialog();

            }
        });

    }

}
