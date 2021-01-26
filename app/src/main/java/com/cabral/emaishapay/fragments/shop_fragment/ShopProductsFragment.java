package com.cabral.emaishapay.fragments.shop_fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.ShopActivity;

public class ShopProductsFragment extends Fragment {


    public ShopProductsFragment(ShopActivity shopActivity, FragmentManager supportFragmentManager) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop_products, container, false);
    }
}