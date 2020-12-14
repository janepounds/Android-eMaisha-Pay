package com.cabral.emaishapay.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.databinding.FragmentEmploymentInformationBinding;
import com.cabral.emaishapay.databinding.FragmentPersonalInformationBinding;
import com.cabral.emaishapay.models.AccountResponse;
import com.cabral.emaishapay.network.APIClient;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmploymentInformationFragment extends Fragment {
    private static final String TAG = "EmploymentInformation";
    private FragmentEmploymentInformationBinding binding;
    private NavController navController = null;
    private String employer,designation,location,phone,employee_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_employment_information, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);
        initializeViews(view);
    }

    public void initializeViews(View view) {

        /******************RETROFIT IMPLEMENTATION****************************/
        binding.submitButton.setOnClickListener(v -> {
            employer = binding.employer.getText().toString();
            designation = binding.designaion.getText().toString();
            location = binding.location.getText().toString();
            phone = "+256 "+ binding.contact.getText().toString();
            employee_id = binding.employerId.getText().toString();

            Call<AccountResponse> call = APIClient.getWalletInstance()
                    .storeEmploymentInfo(employer, designation, location, phone, employee_id);
            call.enqueue(new Callback<AccountResponse>() {
                @Override
                public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                    if (response.isSuccessful()) {

                        Log.d(TAG, "onResponse: successful");
                    } else {

                        Log.d(TAG, "onResponse: failed" + response.errorBody());
                    }

                }

                @Override
                public void onFailure(Call<AccountResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure: failed" + t.getMessage());

                }
            });

        });

    }
    }