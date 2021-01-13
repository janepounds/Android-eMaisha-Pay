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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.cabral.emaishapay.R;

import org.jetbrains.annotations.NotNull;

import am.appwise.components.ni.NoInternetDialog;

public class Thank_You extends Fragment {
    //    FrameLayout banner_adView;
    private TextView order_number;
    private Button order_status_btn, continue_shopping_btn;

    My_Cart my_cart;

    public Thank_You(My_Cart my_cart) {
        this.my_cart = my_cart;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.buy_inputs_thank_you, container, false);

        // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
        //MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.order_confirmed));
        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
        // noInternetDialog.show();

        // Binding Layout Views
//        banner_adView = rootView.findViewById(R.id.banner_adView);
        order_number = rootView.findViewById(R.id.order_number);
        order_status_btn = rootView.findViewById(R.id.order_status_btn);
        continue_shopping_btn = rootView.findViewById(R.id.continue_shopping_btn);

        // Binding Layout Views
        order_status_btn.setOnClickListener(view -> {
            // Navigate to My_Orders Fragment
            Fragment fragment = new My_Orders();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.main_fragment_container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(null)
                    .commit();
        });

        // Binding Layout Views
        continue_shopping_btn.setOnClickListener(view -> {
            // Navigate to HomePage Fragment

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment buyinputshome = new WalletBuyFragment(getContext(),fragmentManager);

            fragmentManager.beginTransaction()
                    //.hide(currentFragment)
                    .add(R.id.nav_host_fragment, buyinputshome)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(getString(R.string.actionHome)).commit();


        });

        return rootView;
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

