package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.fragments.wallet_fragments.TokenAuthFragment;
import com.cabral.emaishapay.models.InitiateTransferResponse;
import com.cabral.emaishapay.models.ConfirmationDataResponse;
import com.cabral.emaishapay.models.WalletTransaction;
import com.cabral.emaishapay.models.WalletTransactionInitiation;
import com.cabral.emaishapay.models.external_transfer_model.BankTransferResponse;
import com.cabral.emaishapay.models.external_transfer_model.SettlementResponse;
import com.cabral.emaishapay.models.external_transfer_model.TransferFeeResponse;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;
import com.cabral.emaishapay.network.api_helpers.ExternalAPIRequests;
import com.cabral.emaishapay.network.api_helpers.FlutterwaveV3APIClient;
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

import static android.content.ContentValues.TAG;

public class ConfirmTransfer extends DialogFragment {
    private static final String TAG = "ConfirmTransfer";

    Button confirmBtn;
    TextView serviceTextView, datetimeTextView, totalTextView, receiverPhoneNumber,
            receiverNameTextView, errorTextView, transactionLabelTextView, receiverLabel,chargesTextView;
    String businessName = "";
    Context activity;
    LinearLayout charges_layount, discount_layount;
    DialogLoader dialogLoader;
    Dialog dialog;

    public ConfirmTransfer(Context context) {
        this.activity = context;
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

        String phoneNumber= getArguments().getString("phoneNumber");
        String methodOfTransfer=getArguments().getString("methodOfPayment");
        double amount=getArguments().getDouble("amount");
        String beneficiary_name = getArguments().getString("beneficiary_name");
        String account_name = getArguments().getString("account_name");
        String account_number = getArguments().getString("account_number");
        String branch=getArguments().getString("bankBranch");
        String bankCode=getArguments().getString("bankCode");
        String beneficiary_id = getArguments().getString("beneficiary_id");
        String beneficiary_bank_phone_number = getArguments().getString("beneficiary_bank_phone_number");

        totalTextView.setText(getString(R.string.currency)+" "+NumberFormat.getInstance().format(amount));

        SimpleDateFormat localFormat = new SimpleDateFormat(getString(R.string.date_format_preffered), Locale.ENGLISH);
        localFormat.setTimeZone(TimeZone.getDefault());
        String currentDateandTime = null;
        SimpleDateFormat incomingFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        incomingFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        currentDateandTime = localFormat.format(new Date());

        datetimeTextView.setText(currentDateandTime);
        receiverPhoneNumber.setText(phoneNumber);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
                //Inner Dialog to enter PIN
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity, R.style.CustomAlertDialog);
                //LayoutInflater inflater = requireActivity().getLayoutInflater();
                View pinDialog = View.inflate(activity,R.layout.dialog_enter_pin,null);


                builder.setView(pinDialog);
                dialog = builder.create();
                builder.setCancelable(false);

                EditText pinEdittext =pinDialog.findViewById(R.id.etxt_create_agent_pin);
                TextView dialog_title = pinDialog.findViewById(R.id.dialog_title);
                if(category.equalsIgnoreCase("merchant")){
                    dialog_title.setText("ENTER MERCHANT PIN");

                }else if(category.equalsIgnoreCase("agent")){
                    dialog_title.setText("ENTER AGENT PIN");
                }

                pinDialog.findViewById(R.id.txt_custom_add_agent_submit_pin).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if( !TextUtils.isEmpty(pinEdittext.getText()) && pinEdittext.getText().length()==4){

                            if(methodOfTransfer.equalsIgnoreCase("eMaisha Account")){
                                initiateWalletTransfer(phoneNumber, amount,pinEdittext.getText().toString());
                            }else if(methodOfTransfer.equalsIgnoreCase("Mobile Money")){
                                String beneficiary_name = WalletTransactionInitiation.getInstance().getAccount_name();
                                mobileMoneyTransfer(amount, phoneNumber,pinEdittext.getText().toString());
                            }else if(methodOfTransfer.equalsIgnoreCase("Bank")){

//                                queueBankTransfer(account_name,""+amount,account_number,branch,bankCode,pinEdittext.getText().toString());
                                transferToBank(account_name,""+amount,account_number,branch,bankCode,pinEdittext.getText().toString(),beneficiary_id);
                            }else if(methodOfTransfer.equalsIgnoreCase("eMaisha Card")){

                            }

                        }else {
                            pinEdittext.setError("Invalid Pin Entered");
                        }

                    }
                });

                dialog.show();


            }
        });

        discount_layount.setVisibility(View.GONE);
        charges_layount.setVisibility(View.GONE);

        if(methodOfTransfer.equalsIgnoreCase("eMaisha Account")) {
            getReceiverName(phoneNumber);
        }else if(methodOfTransfer.equalsIgnoreCase("Mobile Money")) {
            receiverNameTextView.setText(beneficiary_name);
        }else if(methodOfTransfer.equalsIgnoreCase("Bank")) {
            receiverNameTextView.setText(beneficiary_name);
            receiverPhoneNumber.setText(beneficiary_bank_phone_number);
//            receiverPhoneNumber.setVisibility(View.GONE);
        }

    }

    private void transferToBank(String account_name, String s, String account_number, String branch, String bankCode, String toString, String beneficiary_id) {
        dialogLoader.showProgressDialog();
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        String currency_code = getString(R.string.currency);
        String service_code = WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION+toString;
        Call<ConfirmationDataResponse> call;

        if(category.equalsIgnoreCase("merchant")){
            APIRequests apiRequests = APIClient.getWalletInstance(requireContext());
            call = apiRequests.merchantTransferToBank(access_token, Double.parseDouble(s), beneficiary_id, category, request_id, "merchantTransferToBank", service_code, currency_code);


        }else  if(category.equalsIgnoreCase("agent")){
            Log.d(TAG, "transferToBank: beneficiary_id"+beneficiary_id);
            APIRequests apiRequests = APIClient.getWalletInstance(requireContext());
            call = apiRequests.agentTransferToBank(access_token, Double.parseDouble(s), beneficiary_id, category, request_id, "agentTransferToBank", service_code, currency_code);

        }else {

            APIRequests apiRequests = APIClient.getWalletInstance(requireContext());
            call = apiRequests.customerTransferToBank(access_token, Double.parseDouble(s), beneficiary_id, category, request_id, "customerTransferToBank", service_code, currency_code);

        }


        call.enqueue(new Callback<ConfirmationDataResponse>() {
            @Override
            public void onResponse(Call<ConfirmationDataResponse> call, Response<ConfirmationDataResponse> response) {
                if (response.code() == 200) {
                    if(response.body().getStatus().equalsIgnoreCase("1")) {
                        dialog.dismiss();
                        //success message
                        dialogLoader.hideProgressDialog();
                        final Dialog dialog = new Dialog(getContext());
                        dialog.setContentView(R.layout.dialog_successful_message);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.setCancelable(false);
                        TextView text = dialog.findViewById(R.id.dialog_success_txt_message);
                        text.setText( response.body().getMessage() );


                        dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                                Intent goToWallet = new Intent(getActivity(), WalletHomeActivity.class);
                                startActivity(goToWallet);
                            }
                        });
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.show();


                    } else {
                        dialogLoader.hideProgressDialog();
                        ConfirmTransfer.this.dismiss();
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

                } else if (response.code() == 412) {

                } else if (response.code() == 401) {
                    TokenAuthFragment.startAuth(true);

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

                receiverNameTextView.setText("Unknown Receiver");

                errorTextView.setText("Error while checking for merchant occured");
                errorTextView.setVisibility(View.VISIBLE);
                dialog.dismiss();
                dialogLoader.hideProgressDialog();
            }
        });
    }


    public void getReceiverName(String receiverPhoneNumber){
        /***************RETROFIT IMPLEMENTATION***********************/
        ProgressDialog dialog;
        dialog = new ProgressDialog(activity);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());



            APIRequests apiRequests = APIClient.getWalletInstance(requireContext());
            Call<ConfirmationDataResponse> call = apiRequests.getUserBusinessName(access_token, receiverPhoneNumber, "CustomersTransfer", request_id, "getReceiverForUser", category);
            call.enqueue(new Callback<ConfirmationDataResponse>() {
                @Override
                public void onResponse(Call<ConfirmationDataResponse> call, Response<ConfirmationDataResponse> response) {
                    if (response.code() == 200) {
                        businessName = response.body().getData().getBusinessName();
                        receiverNameTextView.setText(businessName);
                        confirmBtn.setVisibility(View.VISIBLE);

                        dialog.dismiss();
                    } else if (response.code() == 412) {
                        String businessName = null;
                        businessName = response.body().getMessage();
                        receiverNameTextView.setText(businessName);
                        errorTextView.setText(businessName);
                        errorTextView.setVisibility(View.VISIBLE);
                        // confirmBtn.setEnabled(true);
                    } else if (response.code() == 401) {
                        TokenAuthFragment.startAuth(true);

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

    public void mobileMoneyTransfer(double amount,String phoneNumber, String user_pin){
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        String service_code = WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION+user_pin;

        if(category.equalsIgnoreCase("merchant")){

            //********************* RETROFIT IMPLEMENTATION ********************************//
            APIRequests apiRequests = APIClient.getWalletInstance(getContext());
            Call<WalletTransaction> call = apiRequests.withdrawMobileMoneyMerchant(
                    access_token,
                    amount,
                    phoneNumber,
                    request_id,
                    category,
                    "merchantMobileMoneyWithdraw",
                    service_code

            );

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




        }else if(category.equalsIgnoreCase("agent")){
            //********************* RETROFIT IMPLEMENTATION ********************************//
            APIRequests apiRequests = APIClient.getWalletInstance(getContext());
            Call<WalletTransaction> call = apiRequests.withdrawMobileMoneyAgent(
                    access_token,
                    amount,
                    phoneNumber,
                    request_id,
                    category,
                    "agentMobileMoneyWithdraw",
                    service_code

            );

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




        }else {

            //********************* RETROFIT IMPLEMENTATION ********************************//
            APIRequests apiRequests = APIClient.getWalletInstance(getContext());
            Call<WalletTransaction> call = apiRequests.withdrawMobileMoneyCustomer(
                    access_token,
                    amount,
                    phoneNumber,
                    request_id,
                    category,
                    "customerMobileMoneyWithdraw",
                    service_code

            );

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


    private void mobileMoneyTransfer(String beneficiary_name, String transfer_amount, String account_number, String user_pin) {
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
                        String service_code =WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION+ user_pin;
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

    private void queueBankTransfer(String account_name, String transfer_amount, String account_number, String branch_code, String bank_code, String user_pin){

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
                        String service_code =WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION+ user_pin;
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

    public void initiateWalletTransfer(final String phoneNumber, final double amount, String user_pin) {
        ProgressDialog dialog;
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        Toast.makeText(activity, phoneNumber, Toast.LENGTH_LONG).show();
        dialog = new ProgressDialog(activity);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();
        /*****RETROFIT IMPLEMENTATION*****/
        String service_code =WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION+ user_pin;

        Log.d(TAG, "initiateWalletTransfer: encripted_service_code"+service_code);
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());

        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<InitiateTransferResponse> call = apiRequests.initiateTransfer(access_token, amount,phoneNumber,request_id,category,service_code,"customerInitiateFundsTransfer");
        call.enqueue(new Callback<InitiateTransferResponse>() {
            @Override
            public void onResponse(Call<InitiateTransferResponse> call, Response<InitiateTransferResponse> response) {
                if(response.code() ==200 && response.body().getStatus().equalsIgnoreCase("1")){
                    dialog.dismiss();
                    final Dialog dialog = new Dialog(activity);
                    dialog.setContentView(R.layout.dialog_successful_message);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(false);
                    TextView text = dialog.findViewById(R.id.dialog_success_txt_message);
                    text.setText("You have transferred UGX " + NumberFormat.getInstance().format(amount) + " to " + businessName);


                    dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                            Intent goToWallet = new Intent(activity, WalletHomeActivity.class);
                            startActivity(goToWallet);
                        }
                    });
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();

                } else if(response.code() ==200 && response.body().getStatus().equalsIgnoreCase("0")){
                    dialog.dismiss();
                    Snackbar.make(serviceTextView, response.body().getMessage(), Snackbar.LENGTH_SHORT).show();
                }
                else if(response.code() == 401) {
                    TokenAuthFragment.startAuth( true);

                }
                    else if (response.code() == 500) {
                        errorTextView.setText("Error Occurred Try again later");
                        Log.e("info 500", new String(String.valueOf(response.errorBody())) + ", code: " + response.code());
                    } else if (response.code() == 400) {
                        errorTextView.setText("Check your input details");
                        Log.e("info 500", new String(String.valueOf(response.errorBody()) + ", code: " + response.code()));
                    } else if (response.code() == 406) {
                        errorTextView.setText(response.body().getMessage());

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
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        Double amount =Double.parseDouble(transferResponse.getAmount());
        String thirdparty="flutterwave";
        Double third_party_fee =Double.parseDouble(transferResponse.getFee());
        String destination_account_no=transferResponse.getAccount_number();
        String beneficiary_name=transferResponse.getFull_name();
        String destination_name=transferResponse.getBank_name();
        String reference=transferResponse.getReference();
        String third_party_id=transferResponse.getId();
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());

        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
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
                third_party_id,request_id,category,"transferOutCallback");

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
