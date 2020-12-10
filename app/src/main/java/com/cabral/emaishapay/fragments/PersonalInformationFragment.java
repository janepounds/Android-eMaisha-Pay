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
import com.cabral.emaishapay.databinding.FragmentPersonalInformationBinding;
import com.cabral.emaishapay.databinding.FragmentWalletAccountBinding;
import com.cabral.emaishapay.models.AccountResponse;
import com.cabral.emaishapay.network.APIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalInformationFragment extends Fragment {
    private static final String TAG = "PersonalInformation";
    private FragmentPersonalInformationBinding binding;
    private NavController navController = null;
    private String dob, gender, next_of_kin,next_of_kin_contact,picture;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_personal_information, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);
        saveInfo(view);
    }

    public void saveInfo(View view){
        /******************RETROFIT IMPLEMENTATION****************************/
        dob = binding.dob.getText().toString();
        gender = binding.gender.getSelectedItem().toString();
        next_of_kin = binding.nextOfKinFirst + " " + binding.nextOfKinLast;
        next_of_kin_contact = "+256 " +binding.nextOfKinContact.getText().toString();
        Call<AccountResponse> call = APIClient.getWalletInstance()
                                .storePersonalInfo(dob,gender,next_of_kin,next_of_kin_contact,picture);
                                call.enqueue(new Callback<AccountResponse>() {
                                    @Override
                                    public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {

                                    }

                                    @Override
                                    public void onFailure(Call<AccountResponse> call, Throwable t) {

                                    }
                                });


    }
}