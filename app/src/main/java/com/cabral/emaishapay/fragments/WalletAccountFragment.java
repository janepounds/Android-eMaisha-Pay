package com.cabral.emaishapay.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.app.MyAppPrefsManager;
import com.cabral.emaishapay.databinding.FragmentWalletAccountBinding;

import org.jetbrains.annotations.NotNull;

public class WalletAccountFragment extends Fragment {
    private static final String TAG = "WalletAccountFragment";
    private FragmentWalletAccountBinding binding;
    private NavController navController = null;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wallet_account, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        binding.logout.setOnClickListener(v -> logoutUser());
    }

    private void logoutUser() {
        // Initialise SharedPreference manager class
        MyAppPrefsManager prefsManager = new MyAppPrefsManager(requireContext());

        // change the login status to false
        prefsManager.logOutUser();

        // check if has been changed to false
        if (!prefsManager.isUserLoggedIn()) {
            Log.d(TAG, "onCreate: Login Status = " + prefsManager.isUserLoggedIn());

            // exit the app
            requireActivity().finishAffinity();
        }
    }
}