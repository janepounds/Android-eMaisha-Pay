package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cabral.emaishapay.R;
import com.flutterwave.raveutils.verification.RaveVerificationUtils;


public class AgentCustomerDepositDetails extends DialogFragment {
    TextView txtType,txtTypeNumber,txtTotal,txtAmount;
    Button txtSubmit;

    public AgentCustomerDepositDetails() {


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
        View view = inflater.inflate(R.layout.dialog_agent_customer_deposit_details, null);

        txtType = view.findViewById(R.id.txtType);
        txtTypeNumber = view.findViewById(R.id.txtType_number);
        txtTotal = view.findViewById(R.id.txtTotalAmount);
        txtAmount = view.findViewById(R.id.txtAmount);




        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        txtSubmit = view.findViewById(R.id.txt_card_confirm);
        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // Get the layout inflater
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                View view = inflater.inflate(R.layout.dialog_enter_agent_pin, null);

                TextView card_sumit = view.findViewById(R.id.txt_custom_add_agent_submit_pin);
                card_sumit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                    }
                });
                builder.setView(view);
                Dialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                setCancelable(false);
            }
        });



        return dialog;

    }
}