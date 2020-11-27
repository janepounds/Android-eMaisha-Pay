package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cabral.emaishapay.R;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;

public class TransferMoney extends DialogFragment {
    LinearLayout layoutAddMoney, layoutTransfer;
    Button addMoneyImg;
    TextView mobile_numberTxt, addMoneyTxt,  errorMsgTxt;
    TextView balanceTextView, titleTextView;
    private double balance;
    Context activity;
    FragmentManager fm;
    EditText phoneNumberTxt;

    public TransferMoney(Context context, double balance, FragmentManager supportFragmentManager) {
        this.activity = context;
        this.balance = balance;
        this.fm = supportFragmentManager;

        Log.e("Balance", this.balance + "");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
        View view = inflater.inflate(R.layout.wallet_add_money, null);
        builder.setView(view);

        ImageView close = view.findViewById(R.id.wallet_transfer_money_close);
        close.setOnClickListener(v -> dismiss());

        initializeForm(view);
        return builder.create();
    }

    public void initializeForm(View view) {
        phoneNumberTxt = view.findViewById(R.id.crop_add_money_mobile_no);
        addMoneyImg = view.findViewById(R.id.button_add_money);
        addMoneyTxt = view.findViewById(R.id.crop_add_money_amount);
        balanceTextView = view.findViewById(R.id.crop_add_money_balance);
        titleTextView = view.findViewById(R.id.digital_wallet_title_label);
        mobile_numberTxt = view.findViewById(R.id.text_mobile_number);
        errorMsgTxt = view.findViewById(R.id.text_view_error_message);

        balanceTextView.setText(NumberFormat.getInstance().format(balance));
        titleTextView.setText("Transfer Money");
        addMoneyImg.setText("Transfer");
        mobile_numberTxt.setText("Receiver No");

        addMoneyImg.setOnClickListener(v -> {

            String phoneNumber = "0"+phoneNumberTxt.getText().toString();
            String amountEntered = addMoneyTxt.getText().toString();
            float amount = Float.parseFloat(amountEntered);
            float charges = (float) 100; //Transfer Charges

            if (balance >= (amount + charges)) {
                FragmentTransaction ft = fm.beginTransaction();
                Fragment prev = fm.findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                DialogFragment transferPreviewDailog = new com.cabral.emaishapay.DailogFragments.ConfirmTransfer(activity, phoneNumber, amount);
                transferPreviewDailog.show(ft, "dialog");
            } else {
                Toast.makeText(getActivity(), "Insufficient Account balance!", Toast.LENGTH_LONG).show();
                Log.e("Error", "Insufficient Account balance!");
            }

        });

    }
}
