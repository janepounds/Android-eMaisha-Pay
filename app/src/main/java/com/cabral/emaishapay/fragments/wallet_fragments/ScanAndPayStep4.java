package com.cabral.emaishapay.fragments.wallet_fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.databinding.LayoutScanAndPayProcessStep4Binding;

import java.text.NumberFormat;


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
            try{
                String balance=getArguments().getString("balance");
                double balanceDouble=Double.parseDouble(balance);
                balance=getString(R.string.currency)+" "+ NumberFormat.getInstance().format(balanceDouble);
                binding.textUpdatedWalletBalanceAmount.setText(balance);
            }catch (NumberFormatException exception){
                exception.printStackTrace();
                Log.e("ScanAndPay4Error",exception.getMessage());
            }


            binding.textMerchantName2.setText(getArguments().getString("merchant_name"));

        }

        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarScanPayProcess2);
        binding.toolbarScanPayProcess2.setTitle("Scan and Pay");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.rateMerchant.setOnClickListener(v -> {
            rateMerchant();
        });
        binding.btnFinish.setOnClickListener(v -> {
          //navigate to wallet home fragment
            WalletHomeActivity.navController.navigate(R.id.action_scanAndPayStep4_to_walletHomeFragment2);

        });
    }

    private void rateMerchant() {
    }

}
