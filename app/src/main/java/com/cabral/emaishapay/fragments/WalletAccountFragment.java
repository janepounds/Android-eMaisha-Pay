package com.cabral.emaishapay.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.Login;
import com.cabral.emaishapay.activities.WalletHomeActivity;
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

        binding.userName.setText(ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_LAST_NAME, requireContext())) + " " + ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME, requireContext())));
        binding.userEmail.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_USER_EMAIL, requireContext()));
        binding.userPhone.setText(ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_PHONE_NUMBER, requireContext())));

        binding.personalInformationLayout.setOnClickListener(view12 -> {

            if (binding.layoutIdInfo.getVisibility() == View.VISIBLE) {
                binding.idTypeNumber.setVisibility(View.VISIBLE);
                binding.layoutIdInfo.setVisibility(View.GONE);
                binding.chevronIdInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutEmploymentInfo.getVisibility() == View.VISIBLE) {
                binding.employeeDesignation.setVisibility(View.VISIBLE);
                binding.layoutEmploymentInfo.setVisibility(View.GONE);
                binding.chevronEmploymentInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutBusinessInfo.getVisibility() == View.VISIBLE) {
                binding.businessNameTinLicence.setVisibility(View.VISIBLE);
                binding.layoutBusinessInfo.setVisibility(View.GONE);
                binding.chevronBusinessInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutPersonalInfo.getVisibility() == View.VISIBLE) {
                binding.genderDobStatus.setVisibility(View.VISIBLE);
                binding.layoutPersonalInfo.setVisibility(View.GONE);
                binding.chevronPersonalInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            } else {
                binding.chevronPersonalInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_down));
                binding.layoutPersonalInfo.setVisibility(View.VISIBLE);
                binding.genderDobStatus.setVisibility(View.GONE);
            }

        });

        binding.editPersonalInfo.setOnClickListener(view1 -> navController.navigate(R.id.action_walletAccountFragment_to_personalInformationFragment));

        binding.idInformationLayout.setOnClickListener(view12 -> {

            if (binding.layoutEmploymentInfo.getVisibility() == View.VISIBLE) {
                binding.employeeDesignation.setVisibility(View.VISIBLE);
                binding.layoutEmploymentInfo.setVisibility(View.GONE);
                binding.chevronEmploymentInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutBusinessInfo.getVisibility() == View.VISIBLE) {
                binding.businessNameTinLicence.setVisibility(View.VISIBLE);
                binding.layoutBusinessInfo.setVisibility(View.GONE);
                binding.chevronBusinessInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutPersonalInfo.getVisibility() == View.VISIBLE) {
                binding.genderDobStatus.setVisibility(View.VISIBLE);
                binding.layoutPersonalInfo.setVisibility(View.GONE);
                binding.chevronPersonalInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutIdInfo.getVisibility() == View.VISIBLE) {
                binding.idTypeNumber.setVisibility(View.VISIBLE);
                binding.layoutIdInfo.setVisibility(View.GONE);
                binding.chevronIdInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            } else {
                binding.chevronIdInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_down));
                binding.layoutIdInfo.setVisibility(View.VISIBLE);
                binding.idTypeNumber.setVisibility(View.GONE);
            }

        });

        binding.editIdInfo.setOnClickListener(view1 -> navController.navigate(R.id.action_walletAccountFragment_to_idInformationFragment));

        binding.employmentInformationLayout.setOnClickListener(view12 -> {

            if (binding.layoutBusinessInfo.getVisibility() == View.VISIBLE) {
                binding.businessNameTinLicence.setVisibility(View.VISIBLE);
                binding.layoutBusinessInfo.setVisibility(View.GONE);
                binding.chevronBusinessInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutPersonalInfo.getVisibility() == View.VISIBLE) {
                binding.genderDobStatus.setVisibility(View.VISIBLE);
                binding.layoutPersonalInfo.setVisibility(View.GONE);
                binding.chevronPersonalInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutIdInfo.getVisibility() == View.VISIBLE) {
                binding.idTypeNumber.setVisibility(View.VISIBLE);
                binding.layoutIdInfo.setVisibility(View.GONE);
                binding.chevronIdInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutEmploymentInfo.getVisibility() == View.VISIBLE) {
                binding.employeeDesignation.setVisibility(View.VISIBLE);
                binding.layoutEmploymentInfo.setVisibility(View.GONE);
                binding.chevronEmploymentInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            } else {
                binding.chevronEmploymentInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_down));
                binding.layoutEmploymentInfo.setVisibility(View.VISIBLE);
                binding.employeeDesignation.setVisibility(View.GONE);
            }

        });

        binding.editEmploymentInfo.setOnClickListener(view1 -> navController.navigate(R.id.action_walletAccountFragment_to_employmentInformationFragment));

        binding.businessInformationLayout.setOnClickListener(view12 -> {

            if (binding.layoutPersonalInfo.getVisibility() == View.VISIBLE) {
                binding.genderDobStatus.setVisibility(View.VISIBLE);
                binding.layoutPersonalInfo.setVisibility(View.GONE);
                binding.chevronPersonalInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutIdInfo.getVisibility() == View.VISIBLE) {
                binding.idTypeNumber.setVisibility(View.VISIBLE);
                binding.layoutIdInfo.setVisibility(View.GONE);
                binding.chevronIdInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutEmploymentInfo.getVisibility() == View.VISIBLE) {
                binding.employeeDesignation.setVisibility(View.VISIBLE);
                binding.layoutEmploymentInfo.setVisibility(View.GONE);
                binding.chevronEmploymentInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutBusinessInfo.getVisibility() == View.VISIBLE) {
                binding.businessNameTinLicence.setVisibility(View.VISIBLE);
                binding.layoutBusinessInfo.setVisibility(View.GONE);
                binding.chevronBusinessInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            } else {
                binding.chevronBusinessInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_down));
                binding.layoutBusinessInfo.setVisibility(View.VISIBLE);
                binding.businessNameTinLicence.setVisibility(View.GONE);
            }

        });

        binding.editBusinessInfo.setOnClickListener(view1 -> navController.navigate(R.id.action_walletAccountFragment_to_businessInformationFragment));

        binding.shareApp.setOnClickListener(view13 -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        });

        binding.rateApp.setOnClickListener(view13 -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)));
        });

        binding.logout.setOnClickListener(v -> logoutUser());
    }

    private String ucf(String str) {
        if (str == null || str.length() < 2)
            return str;

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private void logoutUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());

        alertDialogBuilder.setTitle("Logout");
        alertDialogBuilder.setMessage("Are you sure you want to logout?");
        alertDialogBuilder.setPositiveButton("Yes", (dialogInterface, i) -> {
            // Initialise SharedPreference manager class
            MyAppPrefsManager prefsManager = new MyAppPrefsManager(requireContext());

            // change the login status to false
            prefsManager.logOutUser();

            // check if has been changed to false
            if (!prefsManager.isUserLoggedIn()) {
                Log.d(TAG, "onCreate: Login Status = " + prefsManager.isUserLoggedIn());

                // Open login
                startActivity(new Intent(requireActivity(), Login.class));
                requireActivity().finish();
            }
        }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}