package com.cabral.emaishapay.DailogFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.models.InitiateWithdrawResponse;
import com.cabral.emaishapay.models.WalletTransactionResponse;
import com.cabral.emaishapay.network.APIClient;


public class AgentCustomerConfirmDetails extends DialogFragment {
    TextView textTitleLabel,textTitleName,textName,textReceiverAccount,textTitlePhoneNumber,textPhoneNumber,textTitleAmount,textAmount;
    TextView textTitleCharge,textCharge,textTitleTotalAmount,textTotalAmount;
    CardView layoutReceiverAccount;
    Button txtSubmit;


    public AgentCustomerConfirmDetails() {


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
        View view = inflater.inflate(R.layout.dialog_agent_customer_confirm_details, null);

        textTitleLabel = view.findViewById(R.id.agent_confirm_details_title_label);
        textTitleName = view.findViewById(R.id.text_title_name);
        textName = view.findViewById(R.id.text_name);
        textReceiverAccount = view.findViewById(R.id.text_receiver_account);
        textTitlePhoneNumber = view.findViewById(R.id.text_title_phone_number);
        textPhoneNumber = view.findViewById(R.id.text_phone_number);
        textTitleAmount = view.findViewById(R.id.text_title_amount);
        textAmount = view.findViewById(R.id.text_amount);
        textTitleCharge = view.findViewById(R.id.text_title_charge);
        textCharge = view.findViewById(R.id.text_charge);
        textTitleTotalAmount = view.findViewById(R.id.text_title_total_amount);
        textTotalAmount = view.findViewById(R.id.txtTotalAmount);
        layoutReceiverAccount = view.findViewById(R.id.layout_receiver_account);
        txtSubmit = view.findViewById(R.id.txt_card_confirm);

        if(getArguments()!=null){
            /**************WITHDRAW********************/
            String key = getArguments().getString("key");
            if(key.equalsIgnoreCase("withdraw")){
                textTitleLabel.setText(getArguments().getString("title"));
                textTitleAmount.setText("Amount Received");
                textName.setText(getArguments().getString("customer_name"));
                textReceiverAccount.setText("Customer");
                textPhoneNumber.setText("0"+getArguments().getString("phone_number"));
                textAmount.setText("UGX "+getArguments().getString("amount"));
                textTotalAmount.setText("UGX "+getArguments().getString("amount"));

            }



        }


        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call pin dialog
                FragmentManager fragmentManager = getChildFragmentManager();

                FragmentTransaction ft = fragmentManager.beginTransaction();
                Fragment prev = fragmentManager.findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                Bundle bundle = new Bundle();
                bundle.putString("amount",textTotalAmount.getText().toString());
                bundle.putString("phone_number",textPhoneNumber.getText().toString());

                // Create and show the dialog.
                DialogFragment depositDialog = new EnterPin();
                depositDialog.setArguments(bundle);

                depositDialog.show(ft, "dialog");
            }


        });



        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        return dialog;

    }




}