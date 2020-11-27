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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.models.WalletPurchase;

public class Buy extends DialogFragment {
    TextView mechantIdTextView, text_coupon;
    EditText totalAmountTxt, couponAmout;
    LinearLayout layout_coupon;     Button saveBtn;
    Context activity;     FragmentManager fm;

    public Buy(Context context, FragmentManager supportFragmentManager){
        this.activity=context;
        this.fm=supportFragmentManager;
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
        LayoutInflater inflater = getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
         View view =inflater.inflate(R.layout.wallet_buy, null);
         builder.setView(view);
         initializeForm( view);

        ImageView close = view.findViewById(R.id.wallet_buy_close);
        close.setOnClickListener(v -> dismiss());

         return builder.create();
    }

    public void initializeForm(View view) {
            totalAmountTxt = view.findViewById(R.id.txt_crop_bill_total);
            couponAmout= view.findViewById(R.id.txt_wallet_bill_coupon);
            layout_coupon= view.findViewById(R.id.layout_coupon);
            mechantIdTextView = view.findViewById(R.id.txt_wallet_purchase_mechant_id);
            saveBtn = view.findViewById(R.id.btn_save);
            text_coupon= view.findViewById(R.id.txt_bill_by_coupon);

            text_coupon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(layout_coupon.getVisibility()== View.VISIBLE){
                        layout_coupon.setVisibility(View.GONE);
                        couponAmout.setText(null);
                    }
                    else{
                        layout_coupon.setVisibility(View.VISIBLE);
                    }
                }
            });
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processPayment();
                }
            });
    }

    public void processPayment(){
        float amount = Float.parseFloat(totalAmountTxt.getText().toString());
        if(amount>0 && !mechantIdTextView.getText().toString().isEmpty()){
            WalletPurchase.getInstance().setMechantId(mechantIdTextView.getText().toString());
            WalletPurchase.getInstance().setAmount(amount);
            WalletPurchase.getInstance().setCoupon(couponAmout.getText().toString());
            FragmentTransaction ft = this.fm.beginTransaction();
            Fragment prev =this.fm.findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            // Create and show the dialog.
            DialogFragment buyfoodPreviewDailog =new PurchasePreview(activity);
            buyfoodPreviewDailog.show( ft, "dialog");
        }
        else{
            Log.d("ITEMS ", "NO ITEMS");
        }
    }
}

