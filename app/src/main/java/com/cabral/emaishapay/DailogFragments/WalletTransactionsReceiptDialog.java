package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
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
import com.cabral.emaishapay.models.WalletTransactionResponse;
import com.cabral.emaishapay.singletons.WalletSettingsSingleton;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class WalletTransactionsReceiptDialog extends DialogFragment {

    TextView serviceTextView, receiptNumberTextView, statusTextView,totalTextView,
            merchantNameTextView,errorTextView,dateTextView,referenceNoTextView;
    private Context context;
    private WalletTransactionResponse.TransactionData.Transactions  transaction;

   public WalletTransactionsReceiptDialog(WalletTransactionResponse.TransactionData.Transactions transaction){
       this.transaction =transaction;
   }
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

        totalTextView.setText( NumberFormat.getInstance().format(this.transaction.getAmount()));
        dateTextView.setText(this.transaction.getDate());
        referenceNoTextView.setText(this.transaction.getReferenceNumber());
        actualStatementData(this.transaction);
    }

    private void actualStatementData(WalletTransactionResponse.TransactionData.Transactions transaction) {
        try {

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
            serviceTextView.setText(" "+transaction.getType());


        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("Info2: ","-"+e.getMessage());
        }
    }


}
