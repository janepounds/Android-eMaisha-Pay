package com.cabral.emaishapay.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.databinding.FragmentBusinessAccountBinding;
import com.cabral.emaishapay.databinding.FragmentBusinessInformationBinding;

public class BusinessAccountFragment extends Fragment {
    private FragmentBusinessAccountBinding binding;
    private NavController navController = null;
    public BusinessAccountFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_business_account, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);
        initializeViews();



    }

    public void initializeViews(){
        if(getArguments().getString("Agent").equalsIgnoreCase("Agent")){
            binding.toolbar.setTitle(getArguments().getString("Agent"));

        }else if(getArguments().getString("Merchant").equalsIgnoreCase("Merchant")){
            binding.toolbar.setTitle(getArguments().getString("Merchant"));
        }else if(getArguments().getString("AgentMerchant").equalsIgnoreCase("AgentMerchant")) {
            binding.toolbar.setTitle(getArguments().getString("AgentMerchant"));
        }
    }
}