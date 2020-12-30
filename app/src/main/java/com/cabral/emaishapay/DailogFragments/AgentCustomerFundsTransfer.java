package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cabral.emaishapay.R;
import com.flutterwave.raveutils.verification.RaveVerificationUtils;

public class AgentCustomerFundsTransfer extends DialogFragment {
    LinearLayout layoutAddMoney;
    Button addMoneyImg;
    TextView addMoneyTxt, phoneNumberTxt, errorMsgTxt;
    static String PENDING_DEPOSIT_REFERENCE_NUMBER;
    TextView balanceTextView;
    double balance;
    private String txRef;
    ProgressDialog dialog;
    Context activity;
    private RaveVerificationUtils verificationUtils;

    public AgentCustomerFundsTransfer() {


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
        View view = inflater.inflate(R.layout.dialog_agent_customer_funds_transfer, null);

        builder.setView(view);


        ImageView close = view.findViewById(R.id.agent_deposit_money_close);
        close.setOnClickListener(v -> dismiss());

        return builder.create();

    }

}