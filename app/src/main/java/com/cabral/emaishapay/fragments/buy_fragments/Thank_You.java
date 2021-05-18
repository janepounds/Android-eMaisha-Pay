package com.cabral.emaishapay.fragments.buy_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletBuySellActivity;
import com.cabral.emaishapay.databinding.BuyInputsThankYouBinding;

import org.jetbrains.annotations.NotNull;


public class Thank_You extends Fragment {
    private TextView order_number;
    private Button order_status_btn, continue_shopping_btn;

    My_Cart my_cart;String orderNumber;
    BuyInputsThankYouBinding binding;

    public Thank_You() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.buy_inputs_thank_you, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.order_confirmed));
        if(getArguments()!=null){
            my_cart = (My_Cart) getArguments().getSerializable("my_cart");
            orderNumber = getArguments().getString("order_number");
        }
        binding.orderNumber.setText("Order number: #"+this.orderNumber);
        // Binding Layout Views
        binding.orderStatusBtn.setOnClickListener(view -> {
            // Navigate to My_Orders Fragment
            WalletBuySellActivity.navController.navigate(R.id.action_thankYou_to_walletOrdersFragment);

        });

        // Binding Layout Views
        binding.continueShoppingBtn.setOnClickListener(view -> {
            // Navigate to HomePage Fragment

            WalletBuySellActivity.navController.navigate(R.id.action_thankYou_to_walletBuyFragment);

        });

        return binding.getRoot();
    }


    @Override
    public void onPrepareOptionsMenu(@NotNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            getActivity().getSupportFragmentManager().popBackStack(getString(R.string.actionHome), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}

