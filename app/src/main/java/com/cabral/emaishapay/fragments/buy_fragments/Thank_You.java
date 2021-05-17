package com.cabral.emaishapay.fragments.buy_fragments;

import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletBuySellActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;

import org.jetbrains.annotations.NotNull;

import am.appwise.components.ni.NoInternetDialog;

public class Thank_You extends Fragment {
    private TextView order_number;
    private Button order_status_btn, continue_shopping_btn;

    My_Cart my_cart;String orderNumber;

    public Thank_You(My_Cart my_cart, String orderNumber) {
        this.my_cart = my_cart;
        this.orderNumber=orderNumber;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.buy_inputs_thank_you, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.order_confirmed));

        order_number = rootView.findViewById(R.id.order_number);
        order_status_btn = rootView.findViewById(R.id.order_status_btn);
        continue_shopping_btn = rootView.findViewById(R.id.continue_shopping_btn);

        order_number.setText("Order number: #"+this.orderNumber);
        // Binding Layout Views
        order_status_btn.setOnClickListener(view -> {
            // Navigate to My_Orders Fragment
            Fragment fragment = new My_Orders();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            while(fragmentManager.getBackStackEntryCount()>0)//pop all fragements in back stack till there none
                fragmentManager.popBackStackImmediate();


            fragmentManager.beginTransaction()
                    .add(R.id.nav_host_fragment2, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(null)
                    .commit();
        });

        // Binding Layout Views
        continue_shopping_btn.setOnClickListener(view -> {
            // Navigate to HomePage Fragment
            refreshActivity();

        });

        return rootView;
    }

    public void refreshActivity() {
        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();

        while(fragmentManager.getBackStackEntryCount()>0)//pop all fragements in back stack till there none
            fragmentManager.popBackStackImmediate();

        Fragment fragment =new WalletBuyFragment(getContext(),fragmentManager);
        fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment2, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null).commit();
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

