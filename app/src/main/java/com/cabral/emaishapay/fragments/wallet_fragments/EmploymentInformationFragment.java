package com.cabral.emaishapay.fragments.wallet_fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.databinding.FragmentEmploymentInformationBinding;
import com.cabral.emaishapay.models.AccountResponse;
import com.cabral.emaishapay.network.api_helpers.APIClient;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmploymentInformationFragment extends Fragment {
    private static final String TAG = "EmploymentInformation";
    private FragmentEmploymentInformationBinding binding;
    private ProgressDialog progressDialog;

    public EmploymentInformationFragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_employment_information, container, false);

        WalletHomeActivity.bottomNavigationView.setVisibility(View.GONE);
        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle("Employment Information");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(getArguments()!=null){
            String employer = getArguments().getString("employer");
            String designation =getArguments().getString("designation");
            String location =getArguments().getString("location");
            String employee_id =getArguments().getString("employee_id");

            //set edit textviews
            binding.employer.setText(employer);
            binding.designaion.setText(designation);
            binding.location.setText(location);
            binding.employerId.setText(employee_id);

        }

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);
        binding.submitButton.setOnClickListener(v -> {
            progressDialog.show();
            String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
            String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
            String request_id = WalletHomeActivity.generateRequestId();
            String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
            Call<AccountResponse> call = APIClient.getWalletInstance(getContext())
                    .storeEmploymentInfo(access_token,userId, binding.employer.getText().toString(), binding.designaion.getText().toString(), binding.location.getText().toString(),
                            "+256 " + binding.contact.getText().toString(), binding.employerId.getText().toString(),request_id,category,"storeUserEmployementInfo");
            call.enqueue(new Callback<AccountResponse>() {
                @Override
                public void onResponse(@NotNull Call<AccountResponse> call, @NotNull Response<AccountResponse> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "onResponse: successful");

                        WalletHomeActivity.navController.popBackStack(R.id.walletHomeFragment2,false);
                        WalletHomeActivity.navController.navigate(R.id.action_walletHomeFragment2_to_walletAccountFragment2);
                    } else {
                        Log.d(TAG, "onResponse: failed" + response.errorBody());
                        Toast.makeText(getContext(), "Network Failure!", Toast.LENGTH_LONG).show();
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(@NotNull Call<AccountResponse> call, @NotNull Throwable t) {
                    Log.d(TAG, "onFailure: failed" + t.getMessage());
                }
            });
        });

        binding.cancelButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return binding.getRoot();
    }


}