package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletAuthActivity;
import com.cabral.emaishapay.models.WalletTransactionReceiptResponse;
import com.cabral.emaishapay.models.WalletTransaction;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.cabral.emaishapay.singletons.WalletSettingsSingleton;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletTransactionsReceiptDialog extends DialogFragment {

    TextView serviceTextView, receiptNumberTextView, statusTextView,totalTextView,
            merchantNameTextView,errorTextView,dateTextView,referenceNoTextView;
    private Context context;

   public WalletTransactionsReceiptDialog(){}
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.wallet_purchase_card_preview, null);
        builder.setView(view);




        initializeView(view);
        return builder.create();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context =context;
    }
    public void initializeView(View view){
        serviceTextView = view.findViewById(R.id.text_view_purchase_service);
        receiptNumberTextView = view.findViewById(R.id.text_view_expiry_date);
        statusTextView = view.findViewById(R.id.text_view_id_type);
        totalTextView = view.findViewById(R.id.txt_view_bill_preview_total);
        errorTextView = view.findViewById(R.id.text_view_purchase_preview_error);
        merchantNameTextView = view.findViewById(R.id.text_view_purchase_preview_name);
        dateTextView = view.findViewById(R.id.text_view_purchase_date_time);
        referenceNoTextView = view.findViewById(R.id.text_view_id_number);

        totalTextView.setText( NumberFormat.getInstance().format(WalletTransaction.getInstance().getAmount()));
        dateTextView.setText(WalletTransaction.getInstance().getDate());
        referenceNoTextView.setText(WalletTransaction.getInstance().getReferenceNumber());
        actualStatementData();
    }

    private void actualStatementData() {

        if(!getActivity().getIntent().hasExtra("referenceNumber")){
            getActivity().finish();
        }
        ProgressDialog dialog;
        dialog = new ProgressDialog(getContext());
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();
        /******************RETROFIT IMPLEMENTATION************************/
        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<WalletTransactionReceiptResponse> call = apiRequests.
                getReceipt(WalletAuthActivity.WALLET_ACCESS_TOKEN,getActivity().getIntent().getStringExtra("referenceNumber"));

        call.enqueue(new Callback<WalletTransactionReceiptResponse>() {
            @Override
            public void onResponse(Call<WalletTransactionReceiptResponse> call, Response<WalletTransactionReceiptResponse> response) {
                if(response.code()== 200){
                    try {
                        WalletTransactionReceiptResponse.ReceiptData.TransactionsData transaction = response.body().getData().getTransaction();

                        totalTextView.setText("UGX "+ NumberFormat.getInstance().format(transaction.getAmount()));
                        referenceNoTextView.setText(transaction.getReferenceNumber());
                        merchantNameTextView.setText(transaction.getReceiver());
                        SimpleDateFormat localFormat = new SimpleDateFormat(WalletSettingsSingleton.getInstance().getDateFormat()+" '|' HH:mm:ss a", Locale.ENGLISH);
                        localFormat.setTimeZone(TimeZone.getDefault());
                        String currentDateandTime = null;
                        SimpleDateFormat incomingFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        incomingFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                        currentDateandTime = localFormat.format(incomingFormat.parse(transaction.getDate()));
                        dateTextView.setText(currentDateandTime);

                        if(transaction.getStatus().equals("Completed"))
                            statusTextView.setText("Successful");
                        else
                            statusTextView.setText(transaction.getStatus());

                        receiptNumberTextView.setText(transaction.getReceiptNumber());

                        if(transaction.getType().equals("FoodPurchase"))
                            serviceTextView.setText("Food Purchase");
                        else
                            serviceTextView.setText("Money "+transaction.getType());

                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.e("Info2: ","-"+e.getMessage());
                    }


                    dialog.dismiss();
                }else
                if(response.code()==401){
                    WalletAuthActivity.startAuth(getContext(), true);
                }
                else{
                    errorTextView.setText("Error while loading receipt");
                    errorTextView.setVisibility(View.VISIBLE);
                }
                if (response.errorBody() != null) {
                    Log.e("info", new String(String.valueOf(response.errorBody())));
                } else {
                    Log.e("info", "Something got very very wrong");
                }
                dialog.dismiss();


            }

            @Override
            public void onFailure(Call<WalletTransactionReceiptResponse> call, Throwable t) {

                Log.e("info : ", new String(String.valueOf(t.getMessage())));

                Log.e("info : " ,"Something got very very wrong");


                errorTextView.setText("Error while loading receipt");
                errorTextView.setVisibility(View.VISIBLE);
            }
        });


    }


}
