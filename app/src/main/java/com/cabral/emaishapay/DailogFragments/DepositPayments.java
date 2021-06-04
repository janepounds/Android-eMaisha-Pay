package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cabral.emaishapay.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DepositPayments extends DialogFragment {
    private static final String TAG = "DepositPayments";
    private Context context;
    private final double balance;

    public DepositPayments(double balance){
        this.balance=balance;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.wallet_payment_methods, null);
        builder.setView(view);


     //  RelativeLayout layout_visa = view.findViewById(R.id.layout_visa);
//        layout_visa.setOnClickListener(v->{
//
//            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(context);
//            View dialogView = getLayoutInflater().inflate(R.layout.layout_coming_soon, null);
//            dialog.setView(dialogView);
//            dialog.setCancelable(true);
//
//            ImageView close = dialogView.findViewById(R.id.coming_soon_close);
//            Button ok = dialogView.findViewById(R.id.button_submit);
//            TextView text = dialogView.findViewById(R.id.text);
//            TextView textTitle = dialogView.findViewById(R.id.text_instant_loans);
//
//            textTitle.setText("VISA TOPUP");
//
//            text.setText("Top up your eMaisha Account using your Visa Card.");
//
//
//
//            final androidx.appcompat.app.AlertDialog alertDialog = dialog.create();
//
//            ok.setOnClickListener(view13->{
//                alertDialog.dismiss();
//            });
//            close.setOnClickListener(view13->{
//                alertDialog.dismiss();
//            });
//            alertDialog.show();
//        });

        ImageView close = view.findViewById(R.id.wallet_deposit_close);
        close.setOnClickListener(v -> dismiss());


        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
