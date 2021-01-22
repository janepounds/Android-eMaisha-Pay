package com.cabral.emaishapay.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.Toast;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.databinding.FragmentEmploymentInformationBinding;
import com.cabral.emaishapay.models.AccountResponse;
import com.cabral.emaishapay.network.APIClient;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmploymentInformationFragment extends Fragment {
    private static final String TAG = "EmploymentInformation";
    private FragmentEmploymentInformationBinding binding;
    private ProgressDialog progressDialog;
    Bundle localBundle;

    public EmploymentInformationFragment(Bundle bundle) {
        this.localBundle=bundle;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_employment_information, container, false);

        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.toolbar);
        //toolbar.setTitle(getString(R.string.actionOrders));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(localBundle!=null){
            String employer = localBundle.getString("employer");
            String designation =localBundle.getString("designation");
            String location =localBundle.getString("location");
            String employee_id =localBundle.getString("employee_id");

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

            Call<AccountResponse> call = APIClient.getWalletInstance()
                    .storeEmploymentInfo(userId, binding.employer.getText().toString(), binding.designaion.getText().toString(), binding.location.getText().toString(),
                            "+256 " + binding.contact.getText().toString(), binding.employerId.getText().toString());
            call.enqueue(new Callback<AccountResponse>() {
                @Override
                public void onResponse(@NotNull Call<AccountResponse> call, @NotNull Response<AccountResponse> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "onResponse: successful");

                        Fragment fragment= new WalletAccountFragment();
                        getParentFragmentManager().beginTransaction().replace(R.id.wallet_home_container, fragment).commit();
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