package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.fragments.wallet_fragments.ScanAndPayStep1;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import eu.livotov.labs.android.camview.ScannerLiveView;

public class ScanAndPayDialog extends DialogFragment {
   private ScannerLiveView camera;
   private TextView text_merchant_id;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog

        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.new_scan_pay_floating_button, null);
        FloatingActionButton scan = view.findViewById(R.id.fab_scan);

        scan.setOnClickListener(v -> {
            ScanAndPayDialog.this.dismiss();


             //call scan merchant code fragment

            WalletHomeActivity.navController.navigate(R.id.action_scanAndPayDialog_to_scanAndPayStep1);

        });


        ImageView cancel = view.findViewById(R.id.img_scan_cancel);


        builder.setView(view);
        Dialog dialog = builder.create();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

}