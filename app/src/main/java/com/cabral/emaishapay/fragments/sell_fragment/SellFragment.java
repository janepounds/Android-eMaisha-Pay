package com.cabral.emaishapay.fragments.sell_fragment;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletBuySellActivity;
import com.cabral.emaishapay.adapters.sell.SellProduceViewPagerAdapter;
import com.cabral.emaishapay.database.User_Cart_BuyInputsDB;
import com.cabral.emaishapay.models.cart_model.CartProduct;
import com.google.android.material.tabs.TabLayout;

import java.util.List;


public class SellFragment extends Fragment {




    public SellFragment() {
        // Required empty public constructor
    }

    private Context context;

    private SellProduceViewPagerAdapter sellProduceViewPagerAdapter;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    Toolbar toolbar;

    public SellFragment(WalletBuySellActivity walletBuySellActivity, FragmentManager supportFragmentManager) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sell, container, false);
        toolbar = view.findViewById(R.id.toolbar_sell_home);

        tabLayout = view.findViewById(R.id.tabLayout_sell_produce_fragment);
        viewPager = view.findViewById(R.id.viewPager_sell_produce_fragment);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.actionproducemarket));
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sellProduceViewPagerAdapter = new SellProduceViewPagerAdapter(requireActivity().getSupportFragmentManager());

        viewPager.setAdapter(sellProduceViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onResume() {
        super.onResume();
        sellProduceViewPagerAdapter = new SellProduceViewPagerAdapter(getChildFragmentManager());

        viewPager.setAdapter(sellProduceViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }




}