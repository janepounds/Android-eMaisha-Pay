package com.cabral.emaishapay.fragments.wallet_fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.customs.DialogLoader;


public class WalletRewardsFragment extends Fragment {


    public WalletRewardsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.layout_coming_soon_rewards, container, false);
        Toolbar toolbar=view.findViewById(R.id.toolbar_wallet_rewards);


        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("My Rewards");

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);



        return view;
    }
}