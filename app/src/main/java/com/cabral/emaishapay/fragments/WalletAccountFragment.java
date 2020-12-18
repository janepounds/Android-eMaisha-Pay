package com.cabral.emaishapay.fragments;

import android.content.Context;
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
import com.cabral.emaishapay.models.AccountResponse;
import com.cabral.emaishapay.network.APIClient;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletAccountFragment extends Fragment {
    private static final String TAG = "WalletAccountFragment";
    private FragmentWalletAccountBinding binding;
    private NavController navController = null;
    private Context context;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wallet_account, container, false);

        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        binding.userName.setText(ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_LAST_NAME, requireContext())) + " " + ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME, requireContext())));
        binding.userEmail.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_USER_EMAIL, requireContext()));
        binding.userPhone.setText(ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_PHONE_NUMBER, requireContext())));

        //get account info
        retrieveAccountInfo();

        binding.personalInformationLayout.setOnClickListener(view12 -> {

            if (binding.layoutIdInfo.getVisibility() == View.VISIBLE) {
                binding.idTypeNumber.setVisibility(View.VISIBLE);
                binding.viewIdInfo.setVisibility(View.VISIBLE);
                binding.layoutIdInfo.setVisibility(View.GONE);
                binding.chevronIdInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutEmploymentInfo.getVisibility() == View.VISIBLE) {
                binding.employeeDesignation.setVisibility(View.VISIBLE);
                binding.viewEmploymentId.setVisibility(View.VISIBLE);
                binding.layoutEmploymentInfo.setVisibility(View.GONE);
                binding.chevronEmploymentInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutBusinessInfo.getVisibility() == View.VISIBLE) {
                binding.businessNameTinLicence.setVisibility(View.VISIBLE);
                binding.viewBusinessInfo.setVisibility(View.VISIBLE);
                binding.layoutBusinessInfo.setVisibility(View.GONE);
                binding.chevronBusinessInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutPersonalInfo.getVisibility() == View.VISIBLE) {
                binding.genderDobStatus.setVisibility(View.VISIBLE);
                binding.viewPersonalInfo.setVisibility(View.VISIBLE);
                binding.layoutPersonalInfo.setVisibility(View.GONE);
                binding.chevronPersonalInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            } else {
                binding.chevronPersonalInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_down));
                binding.layoutPersonalInfo.setVisibility(View.VISIBLE);
                binding.genderDobStatus.setVisibility(View.GONE);
                binding.viewPersonalInfo.setVisibility(View.GONE);


                //set id info textviews
                binding.textViewDob.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_DOB, context));
                binding.textViewGender.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_GENDER, context));
                binding.textViewNok.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_NOK, context));
                binding.textViewNokContact.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_NOK_CONTACT, context));


            }

        });

        binding.editPersonalInfo.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            bundle.putString("dob",WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_DOB, context));
            bundle.putString("gender",WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_GENDER, context));
            bundle.putString("nok",WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_NOK, context));
            bundle.putString("nok_contact",WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_NOK_CONTACT, context));
            navController.navigate(R.id.action_walletAccountFragment_to_personalInformationFragment,bundle);
                });

        binding.idInformationLayout.setOnClickListener(view12 -> {

            if (binding.layoutEmploymentInfo.getVisibility() == View.VISIBLE) {
                binding.employeeDesignation.setVisibility(View.VISIBLE);
                binding.viewEmploymentId.setVisibility(View.VISIBLE);
                binding.layoutEmploymentInfo.setVisibility(View.GONE);
                binding.chevronEmploymentInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutBusinessInfo.getVisibility() == View.VISIBLE) {
                binding.businessNameTinLicence.setVisibility(View.VISIBLE);
                binding.viewBusinessInfo.setVisibility(View.VISIBLE);
                binding.layoutBusinessInfo.setVisibility(View.GONE);
                binding.chevronBusinessInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutPersonalInfo.getVisibility() == View.VISIBLE) {
                binding.genderDobStatus.setVisibility(View.VISIBLE);
                binding.viewPersonalInfo.setVisibility(View.VISIBLE);
                binding.layoutPersonalInfo.setVisibility(View.GONE);
                binding.chevronPersonalInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutIdInfo.getVisibility() == View.VISIBLE) {
                binding.idTypeNumber.setVisibility(View.VISIBLE);
                binding.viewIdInfo.setVisibility(View.VISIBLE);
                binding.layoutIdInfo.setVisibility(View.GONE);
                binding.chevronIdInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            } else {
                binding.chevronIdInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_down));
                binding.layoutIdInfo.setVisibility(View.VISIBLE);
                binding.viewIdInfo.setVisibility(View.GONE);
                binding.idTypeNumber.setVisibility(View.GONE);


                //set id info textviews
                binding.textViewIdType.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_TYPE, context));
                binding.textViewIdNumber.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_NUMBER, context));
                binding.textViewExpiryDate.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_EXPIRY_DATE, context));
            }

        });

        binding.editIdInfo.setOnClickListener(view1 -> {

                    Bundle bundle = new Bundle();
                    bundle.putString("idtype",WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_TYPE, context));
                    bundle.putString("idNumber",WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_NUMBER, context));
                    bundle.putString("expiryDate",WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_EXPIRY_DATE, context));

                    navController.navigate(R.id.action_walletAccountFragment_to_idInformationFragment,bundle);
                });

        binding.employmentInformationLayout.setOnClickListener(view12 -> {

            if (binding.layoutBusinessInfo.getVisibility() == View.VISIBLE) {
                binding.businessNameTinLicence.setVisibility(View.VISIBLE);
                binding.viewBusinessInfo.setVisibility(View.VISIBLE);
                binding.layoutBusinessInfo.setVisibility(View.GONE);
                binding.chevronBusinessInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutPersonalInfo.getVisibility() == View.VISIBLE) {
                binding.genderDobStatus.setVisibility(View.VISIBLE);
                binding.viewPersonalInfo.setVisibility(View.VISIBLE);
                binding.layoutPersonalInfo.setVisibility(View.GONE);
                binding.chevronPersonalInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutIdInfo.getVisibility() == View.VISIBLE) {
                binding.idTypeNumber.setVisibility(View.VISIBLE);
                binding.viewIdInfo.setVisibility(View.VISIBLE);
                binding.layoutIdInfo.setVisibility(View.GONE);
                binding.chevronIdInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));


            }

            if (binding.layoutEmploymentInfo.getVisibility() == View.VISIBLE) {
                binding.employeeDesignation.setVisibility(View.VISIBLE);
                binding.viewEmploymentId.setVisibility(View.VISIBLE);
                binding.layoutEmploymentInfo.setVisibility(View.GONE);
                binding.chevronEmploymentInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));




            } else {
                binding.chevronEmploymentInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_down));
                binding.layoutEmploymentInfo.setVisibility(View.VISIBLE);
                binding.viewEmploymentId.setVisibility(View.GONE);
                binding.employeeDesignation.setVisibility(View.GONE);

                //set employment info textviews
                binding.textViewEmployer.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_EMPLOYER, context));
                binding.textViewDesignation.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_DESIGNATION, context));
                binding.textViewLocation.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_LOCATION, context));
                binding.textViewEmployeeId.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_EMPLOYEE_ID, context));

            }

        });

        binding.editEmploymentInfo.setOnClickListener(view1 ->

                {
                    Bundle bundle = new Bundle();
                    bundle.putString("employer",WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_EMPLOYER, context));
                    bundle.putString("designation",WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_DESIGNATION, context));
                    bundle.putString("location",WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_LOCATION, context));
                    bundle.putString("employee_id",WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_EMPLOYEE_ID, context));
                    navController.navigate(R.id.action_walletAccountFragment_to_employmentInformationFragment,bundle);
                });

        binding.businessInformationLayout.setOnClickListener(view12 -> {

            if (binding.layoutPersonalInfo.getVisibility() == View.VISIBLE) {
                binding.genderDobStatus.setVisibility(View.VISIBLE);
                binding.viewPersonalInfo.setVisibility(View.VISIBLE);
                binding.layoutPersonalInfo.setVisibility(View.GONE);
                binding.chevronPersonalInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutIdInfo.getVisibility() == View.VISIBLE) {
                binding.idTypeNumber.setVisibility(View.VISIBLE);
                binding.viewIdInfo.setVisibility(View.VISIBLE);
                binding.layoutIdInfo.setVisibility(View.GONE);
                binding.chevronIdInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutEmploymentInfo.getVisibility() == View.VISIBLE) {
                binding.employeeDesignation.setVisibility(View.VISIBLE);
                binding.viewEmploymentId.setVisibility(View.VISIBLE);
                binding.layoutEmploymentInfo.setVisibility(View.GONE);
                binding.chevronEmploymentInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));
            }

            if (binding.layoutBusinessInfo.getVisibility() == View.VISIBLE) {
                binding.businessNameTinLicence.setVisibility(View.VISIBLE);
                binding.viewBusinessInfo.setVisibility(View.VISIBLE);
                binding.layoutBusinessInfo.setVisibility(View.GONE);
                binding.chevronBusinessInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_right));


            } else {
                binding.chevronBusinessInformation.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_chevron_down));
                binding.layoutBusinessInfo.setVisibility(View.VISIBLE);
                binding.viewBusinessInfo.setVisibility(View.GONE);
                binding.businessNameTinLicence.setVisibility(View.GONE);


                //set business info textviews
                binding.textViewBusinessName.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_BUSINESS_NAME, context));
                binding.textViewBusinessLocation.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_BUSINESS_LOCATION, context));
                binding.textViewRegNo.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_REG_NO, context));
                binding.textViewLicenseNo.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_LICENSE_NUMBER, context));

            }

        });

        binding.editBusinessInfo.setOnClickListener(view1 ->
                {
                    Bundle bundle = new Bundle();
                    bundle.putString("biz_name",WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_BUSINESS_NAME, context));
                    bundle.putString("biz_location",WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_BUSINESS_LOCATION, context));
                    bundle.putString("reg_no",WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_REG_NO, context));
                    bundle.putString("license_no",WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_LICENSE_NUMBER, context));
                    navController.navigate(R.id.action_walletAccountFragment_to_businessInformationFragment,bundle);
                });

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
    public void retrieveAccountInfo(){
        String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
        Call<AccountResponse> call = APIClient.getWalletInstance()
                .getAccountInfo(userId);
        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                if(response.isSuccessful()){
                    if(response.body().getBusinessInfo()!=null){
                        WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_BUSINESS_NAME, response.body().getBusinessInfo().getBusiness_name(), context);
                        WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_BUSINESS_LOCATION, response.body().getBusinessInfo().getBusiness_location(), context);
                        WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_REG_NO, response.body().getBusinessInfo().getRegistration_no(), context);
                        WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_LICENSE_NUMBER, response.body().getBusinessInfo().getLicense_no(), context);


                    }else if(response.body().getEmployeeInfo()!=null){
                        WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_EMPLOYER, response.body().getEmployeeInfo().getEmployer(), context);
                        WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_DESIGNATION, response.body().getEmployeeInfo().getDesignation(), context);
                        WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_LOCATION, response.body().getEmployeeInfo().getLocation(), context);
                        WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_EMPLOYEE_ID, response.body().getEmployeeInfo().getEmployee_id(), context);


                    }else if(response.body().getProfile()!=null){
                        //save in shared preferences
                        WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_DOB, response.body().getProfile().getDob(), context);
                        WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_GENDER, response.body().getProfile().getGender(), context);
                        WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_NOK, response.body().getProfile().getNext_of_kin(), context);
                        WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_NOK_CONTACT, response.body().getProfile().getNext_of_kin_contact(), context);

                    }else if(response.body().getUserIdInfo()!=null){
                        WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_TYPE, response.body().getUserIdInfo().getId_type(), context);
                        WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_NUMBER, response.body().getUserIdInfo().getId_number(), context);
                        WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_EXPIRY_DATE, response.body().getUserIdInfo().getExpiry_date(), context);
                    }


                }

            }

            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {

            }
        });



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