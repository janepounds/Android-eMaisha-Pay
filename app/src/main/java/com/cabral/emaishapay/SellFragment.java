package com.cabral.emaishapay;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabral.emaishapay.adapters.sell.SellProduceViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;


public class SellFragment extends Fragment {




    public SellFragment() {
        // Required empty public constructor
    }

    private Context context;

    private SellProduceViewPagerAdapter sellProduceViewPagerAdapter;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sell, container, false);

        tabLayout = view.findViewById(R.id.tabLayout_sell_produce_fragment);
        viewPager = view.findViewById(R.id.viewPager_sell_produce_fragment);

        setHasOptionsMenu(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(getString(R.string.actionproducemarket));

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