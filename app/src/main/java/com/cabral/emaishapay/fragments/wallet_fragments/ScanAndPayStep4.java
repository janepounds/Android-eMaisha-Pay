package com.cabral.emaishapay.fragments.wallet_fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.databinding.LayoutScanAndPayProcessStep4Binding;

public class ScanAndPayStep4 extends Fragment {
    private Context context;
    private LayoutScanAndPayProcessStep4Binding binding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.layout_scan_and_pay_process_step_4,container,false);


        if(getArguments()!=null){
            binding.amount.setText(getArguments().getString("amount"));
            binding.textMerchantName.setText(getArguments().getString("merchant_name"));
            binding.textMerchantId.setText(getArguments().getString("merchant_id"));
            binding.textDateTime.setText(getArguments().getString("Date"));
            binding.textTxnId.setText(getArguments().getString("trans_id"));
            binding.textUpdatedWalletBalanceAmount.setText(getArguments().getString("balance"));
            binding.textMerchantName2.setText(getArguments().getString("merchant_name"));

        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.rateMerchant.setOnClickListener(v -> {
            rateMerchant();
        });
        binding.textSkip.setOnClickListener(v -> {
          //navigate to wallet home fragment
            WalletHomeActivity.navController.navigate(R.id.action_scanAndPayStep4_to_walletHomeFragment2);

        });
    }

    private void rateMerchant() {
    }
}
