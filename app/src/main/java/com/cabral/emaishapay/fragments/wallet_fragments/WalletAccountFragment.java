package com.cabral.emaishapay.fragments.wallet_fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.DailogFragments.ChangePassword;
import com.cabral.emaishapay.DailogFragments.MerchantQrCode;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.AuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.app.MyAppPrefsManager;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.databinding.NewFragmentWalletAccountBinding;
import com.cabral.emaishapay.models.AccountResponse;
import com.cabral.emaishapay.modelviews.SignUpModelView;
import com.cabral.emaishapay.network.api_helpers.APIClient;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletAccountFragment extends Fragment {
    private static final String TAG = "WalletAccountFragment";
    private NewFragmentWalletAccountBinding binding;
    private Context context;
    private String label_details;
    private Spinner firstSecurityQn,secondSecurityQn,thirdSecurityQn;
    private EditText firstQnAnswer,secondQnAnswer,thirdQnAnswer,phone_number;
    DialogLoader dialogLoader;


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.new_fragment_wallet_account, container, false);

        retrieveAccountInfo();
        binding.userName.setText(ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_LAST_NAME, requireContext())) + " " + ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME, requireContext())));
        binding.userPhone.setText(ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_PHONE_NUMBER, requireContext())));
        String user_pic =WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_PIC, context);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_rectangle_profile_place_holder)
                .error(R.drawable.ic_rectangle_profile_place_holder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);
        Glide.with(requireContext()).load(ConstantValues.WALLET_DOMAIN +user_pic).apply(options).into(binding.userImage);


        binding.qrCodeTitle.setText("MERCHANT ID : "+ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_BUSINESS_ID, requireContext())));


        //view user profile details for view more
        binding.layoutViewMoreUserProfile.setOnClickListener(view12->{
           // dialogLoader = new DialogLoader(context);
           AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            View dialogView = getLayoutInflater().inflate(R.layout.user_summary_details, null);


            dialog.setView(dialogView);
            dialog.setCancelable(true);

            Button btn_edit = dialogView.findViewById(R.id.btn_edit);
            Button btn_ok = dialogView.findViewById(R.id.btn_ok);
            TextView textViewDob = dialogView.findViewById(R.id.text_view_dob);
            TextView textViewGender = dialogView.findViewById(R.id.text_view_gender);
            TextView textViewNok = dialogView.findViewById(R.id.text_view_nok);
            TextView textViewNokContact = dialogView.findViewById(R.id.text_view_nok_contact);

            //set user profile info textviews
            textViewDob.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_DOB, context));
            textViewGender.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_GENDER, context));
            textViewNok.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_NOK, context));
            textViewNokContact.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_NOK_CONTACT, context));


            final AlertDialog alertDialog = dialog.create();
            alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
            btn_edit.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            bundle.putString("dob",WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_DOB, context));
            bundle.putString("gender",WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_GENDER, context));
            bundle.putString("nok",WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_NOK, context));
            bundle.putString("nok_contact",WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_NOK_CONTACT, context));
            bundle.putString("picture",WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_PIC, context));

            WalletHomeActivity.navController.navigate(R.id.action_walletAccountFragment2_to_personalInformationFragment,bundle);

            alertDialog.dismiss();

        });
            btn_ok.setOnClickListener(view13->{
                alertDialog.dismiss();
            });
            alertDialog.show();

        });
        //initializing the view model
        final SignUpModelView viewModel = new ViewModelProvider(this).get(SignUpModelView.class);

        //show Qr code dialog
        binding.imgQrCode.setOnClickListener(v -> {

            if (binding.layoutEmploymentInfo.getVisibility() == View.VISIBLE) {
                binding.chevronEmploymentDetails.setRotation(0);
                binding.layoutEmploymentInfo.setVisibility(View.GONE);
            }
            if (binding.layoutBusinessInfo.getVisibility() == View.VISIBLE) {
                binding.chevronBusinessInformation.setRotation(0);
                binding.layoutBusinessInfo.setVisibility(View.GONE);
            }

            if (binding.layoutEmploymentBusinessInfoDetails.getVisibility() == View.VISIBLE) {
                binding.chevronEmploymentBusinessInfo.setRotation(0);
                binding.layoutEmploymentBusinessInfoDetails.setVisibility(View.GONE);
            }
            if(binding.layoutSecurityInfoDetails.getVisibility() == View.VISIBLE){
                binding.chevronSecurity.setRotation(0);
                binding.layoutSecurityInfoDetails.setVisibility(View.GONE);

            }
            if (binding.layoutIdInfoDetails.getVisibility() == View.VISIBLE) {
                binding.chevronIdInformation.setRotation(0);
                binding.layoutIdInfoDetails.setVisibility(View.GONE);
            }

            Bundle bundle = new Bundle();
            bundle.putString("merchant_id",WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_BUSINESS_ID,context));
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment prev = fm.findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            // Create and show the dialog.
            DialogFragment qrDialog = new MerchantQrCode();
            qrDialog.setArguments(bundle);
            qrDialog.show(ft, "dialog");
            Log.d(TAG, "onCreateView: merchant_id"+WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_BUSINESS_ID,context));

        });

        //navigate to register emaisha card
        binding.layoutEmaishaCards.setOnClickListener(view12->{


            if (binding.layoutEmploymentInfo.getVisibility() == View.VISIBLE) {
                binding.chevronEmploymentDetails.setRotation(0);
                binding.layoutEmploymentInfo.setVisibility(View.GONE);
            }
            if (binding.layoutBusinessInfo.getVisibility() == View.VISIBLE) {
                binding.chevronBusinessInformation.setRotation(0);
                binding.layoutBusinessInfo.setVisibility(View.GONE);
            }

            if (binding.layoutEmploymentBusinessInfoDetails.getVisibility() == View.VISIBLE) {
                binding.chevronEmploymentBusinessInfo.setRotation(0);
                binding.layoutEmploymentBusinessInfoDetails.setVisibility(View.GONE);
            }
            if(binding.layoutSecurityInfoDetails.getVisibility() == View.VISIBLE){
                binding.chevronSecurity.setRotation(0);
                binding.layoutSecurityInfoDetails.setVisibility(View.GONE);

            }
            if (binding.layoutIdInfoDetails.getVisibility() == View.VISIBLE) {
                binding.chevronIdInformation.setRotation(0);
                binding.layoutIdInfoDetails.setVisibility(View.GONE);
            }

            //Go to coming soon
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
            View dialogView = getLayoutInflater().inflate(R.layout.layout_coming_soon, null);
            dialog.setView(dialogView);
            dialog.setCancelable(true);

            ImageView close = dialogView.findViewById(R.id.coming_soon_close);
            Button ok = dialogView.findViewById(R.id.button_submit);
            TextView text = dialogView.findViewById(R.id.text);
            TextView textTitle = dialogView.findViewById(R.id.text_instant_loans);

            textTitle.setText("PREPAID CARD");
            text.setText("Buy smart with eMaisha Card to enjoy discounts, rewards and security free loans");



            final android.app.AlertDialog alertDialog = dialog.create();

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });


            alertDialog.show();
        });


        //navigate to cards list

        binding.layoutCards.setOnClickListener(view12->{

            if (binding.layoutEmploymentInfo.getVisibility() == View.VISIBLE) {
                binding.chevronEmploymentDetails.setRotation(0);
                binding.layoutEmploymentInfo.setVisibility(View.GONE);
            }
            if (binding.layoutBusinessInfo.getVisibility() == View.VISIBLE) {
                binding.chevronBusinessInformation.setRotation(0);
                binding.layoutBusinessInfo.setVisibility(View.GONE);
            }

            if (binding.layoutEmploymentBusinessInfoDetails.getVisibility() == View.VISIBLE) {
                binding.chevronEmploymentBusinessInfo.setRotation(0);
                binding.layoutEmploymentBusinessInfoDetails.setVisibility(View.GONE);
            }
            if(binding.layoutSecurityInfoDetails.getVisibility() == View.VISIBLE){
                binding.chevronSecurity.setRotation(0);
                binding.layoutSecurityInfoDetails.setVisibility(View.GONE);

            }
            if (binding.layoutIdInfoDetails.getVisibility() == View.VISIBLE) {
                binding.chevronIdInformation.setRotation(0);
                binding.layoutIdInfoDetails.setVisibility(View.GONE);
            }
            else {
                WalletHomeActivity.navController.navigate(R.id.action_walletAccountFragment2_to_cardListFragment);


            }

        });


        binding.layoutEmploymentBusinessInfo.setOnClickListener(view12 -> {


            if (binding.layoutEmploymentInfo.getVisibility() == View.VISIBLE) {
                binding.chevronEmploymentDetails.setRotation(0);
                binding.layoutEmploymentInfo.setVisibility(View.GONE);

            }
            if (binding.layoutBusinessInfo.getVisibility() == View.VISIBLE) {
                binding.chevronBusinessInformation.setRotation(0);
                binding.layoutBusinessInfo.setVisibility(View.GONE);

            }

            if (binding.layoutIdInfoDetails.getVisibility() == View.VISIBLE) {
                binding.chevronIdInformation.setRotation(0);
                binding.layoutIdInfoDetails.setVisibility(View.GONE);


            }
            if(binding.layoutSecurityInfoDetails.getVisibility() == View.VISIBLE){
                binding.chevronSecurity.setRotation(0);
                binding.layoutSecurityInfoDetails.setVisibility(View.GONE);

            }
            if (binding.layoutEmploymentBusinessInfoDetails.getVisibility() == View.VISIBLE) {
                binding.chevronEmploymentBusinessInfo.setRotation(0);
                binding.layoutEmploymentBusinessInfoDetails.setVisibility(View.GONE);
            }


            else {
                binding.chevronEmploymentBusinessInfo.setRotation(90);
                binding.layoutEmploymentBusinessInfoDetails.setVisibility(View.VISIBLE);
//                binding.viewEmploymentId.setVisibility(View.GONE);
//                binding.employeeDesignation.setVisibility(View.GONE);
//
            }


        });


        binding.layoutIdInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (binding.layoutBusinessInfo.getVisibility() == View.VISIBLE) {
                    binding.chevronBusinessInformation.setRotation(0);
                    binding.layoutBusinessInfo.setVisibility(View.GONE);
                }

                if (binding.layoutEmploymentInfo.getVisibility() == View.VISIBLE) {
                    binding.chevronEmploymentDetails.setRotation(0);
                    binding.layoutEmploymentInfo.setVisibility(View.GONE);
                }
                if (binding.layoutIdInfoDetails.getVisibility() == View.VISIBLE) {
                    binding.chevronIdInformation.setRotation(0);
                    binding.layoutIdInfoDetails.setVisibility(View.GONE);
                }


                else {
                    binding.chevronIdInformation.setRotation(90);
                    binding.layoutIdInfoDetails.setVisibility(View.VISIBLE);


                    //set id info textviews
                binding.textViewIdType.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_TYPE, context));
                binding.textViewIdNumber.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_NUMBER, context));
                binding.textViewExpiryDate.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_EXPIRY_DATE, context));

                }

            }
        });

        binding.editIdInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                    bundle.putString("idtype", WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_TYPE, context));
                    bundle.putString("idNumber", WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_NUMBER, context));
                    bundle.putString("expiryDate", WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_EXPIRY_DATE, context));
//                    bundle.putString("front", WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_FRONT, context));
//                    bundle.putString("back", WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_BACK, context));
                 //To IdInformationFragment
                   WalletHomeActivity.navController.navigate(R.id.action_walletAccountFragment2_to_idInformationFragment,bundle);
            }
        });



        binding.layoutEmploymentInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (binding.layoutBusinessInfo.getVisibility() == View.VISIBLE) {
                    binding.chevronBusinessInformation.setRotation(0);
                    binding.layoutBusinessInfo.setVisibility(View.GONE);
                }
                if (binding.layoutIdInfoDetails.getVisibility() == View.VISIBLE) {
                    binding.chevronIdInformation.setRotation(0);
                    binding.layoutIdInfoDetails.setVisibility(View.GONE);
                }

                if (binding.layoutEmploymentInfo.getVisibility() == View.VISIBLE) {
                    binding.chevronEmploymentDetails.setRotation(0);
                    binding.layoutEmploymentInfo.setVisibility(View.GONE);
                }


                else {
                    binding.chevronEmploymentDetails.setRotation(90);
                    binding.layoutEmploymentInfo.setVisibility(View.VISIBLE);
                    //set employment info textviews
                    binding.textViewEmployer.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_EMPLOYER, context));
                    binding.textViewDesignation.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_DESIGNATION, context));
                    binding.textViewLocation.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_LOCATION, context));
                    binding.textViewEmployeeId.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_EMPLOYEE_ID, context));
                }

            }
        });

        binding.editEmploymentInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To EmploymentInformationFragment
                Bundle bundle = new Bundle();
                bundle.putString("employer", WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_EMPLOYER, context));
                bundle.putString("designation", WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_DESIGNATION, context));
                bundle.putString("location", WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_LOCATION, context));
                bundle.putString("employee_id", WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_EMPLOYEE_ID, context));
                bundle.putString("contact", WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_CONTACT, context));

                WalletHomeActivity.navController.navigate(R.id.action_walletAccountFragment2_to_employmentInformationFragment,bundle);
            }
        });

        binding.layoutBusinessInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.layoutEmploymentInfo.getVisibility() == View.VISIBLE) {
                    binding.chevronEmploymentDetails.setRotation(0);
                    binding.layoutEmploymentInfo.setVisibility(View.GONE);

                }
                if (binding.layoutIdInfoDetails.getVisibility() == View.VISIBLE) {
                    binding.chevronIdInformation.setRotation(0);
                    binding.layoutIdInfoDetails.setVisibility(View.GONE);


                }
                if (binding.layoutBusinessInfo.getVisibility() == View.VISIBLE) {
                    binding.chevronBusinessInformation.setRotation(0);
                    binding.layoutBusinessInfo.setVisibility(View.GONE);

                }

                else {
                    binding.chevronBusinessInformation.setRotation(90);
                    binding.layoutBusinessInfo.setVisibility(View.VISIBLE);


                    //set business info textviews
                binding.textViewBusinessName.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_BUSINESS_NAME, context));
                binding.textViewBusinessLocation.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_BUSINESS_LOCATION, context));
                binding.textViewRegNo.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_REG_NO, context));
                binding.textViewLicenseNo.setText(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_LICENSE_NUMBER, context));
                }

            }
        });


        binding.editBusinessInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WalletHomeActivity.navController.navigate(R.id.action_walletAccountFragment2_to_businessAccountFragment);
            }
        });

        binding.textChangeAccountType.setOnClickListener(view12 -> {

            //reset the visibilty of the other views
            if (binding.layoutEmploymentInfo.getVisibility() == View.VISIBLE) {
                binding.chevronEmploymentDetails.setRotation(0);
                binding.layoutEmploymentInfo.setVisibility(View.GONE);
            }
            if (binding.layoutBusinessInfo.getVisibility() == View.VISIBLE) {
                binding.chevronBusinessInformation.setRotation(0);
                binding.layoutBusinessInfo.setVisibility(View.GONE);
            }

            if (binding.layoutEmploymentBusinessInfoDetails.getVisibility() == View.VISIBLE) {
                binding.chevronEmploymentBusinessInfo.setRotation(0);
                binding.layoutEmploymentBusinessInfoDetails.setVisibility(View.GONE);
            }
            if (binding.layoutIdInfoDetails.getVisibility() == View.VISIBLE) {
                binding.chevronIdInformation.setRotation(0);
                binding.layoutIdInfoDetails.setVisibility(View.GONE);

            }
            if(binding.layoutSecurityInfoDetails.getVisibility() == View.VISIBLE){
                binding.chevronSecurity.setRotation(0);
                binding.layoutSecurityInfoDetails.setVisibility(View.GONE);

            }



            //inflate the change business account dialog

            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
            View dialogView = getLayoutInflater().inflate(R.layout.new_business_account_dialog, null);
            dialog.setView(dialogView);
            dialog.setCancelable(true);

            final android.app.AlertDialog alertDialog = dialog.create();

            LinearLayout layout_agent = dialogView.findViewById(R.id.layout_agent);
            LinearLayout layout_merchant = dialogView.findViewById(R.id.layout_merchant);
            View view4 = dialogView.findViewById(R.id.view4);
            View view5 = dialogView.findViewById(R.id.view5);


            layout_agent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    Bundle bundle = new Bundle();
                    label_details = "Agent Details";
                    bundle.putString("Agent", label_details);
                    WalletHomeActivity.navController.navigate(R.id.action_walletAccountFragment2_to_businessAccountFragment,bundle);

                }
            });
            layout_merchant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    alertDialog.dismiss();
                    Bundle bundle = new Bundle();
                    label_details = "Merchant Details";
                    bundle.putString("Merchant", label_details);


                    WalletHomeActivity.navController.navigate(R.id.action_walletAccountFragment2_to_businessAccountFragment,bundle);
                }
            });



            alertDialog.show();

        });

        //Setting the account type
        String role = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);
        if(role.equalsIgnoreCase(getString(R.string.role_master_agent)) ){
            binding.textAccountType.setText("Agent");
            binding.textChangeAccountType.setText(ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_BUSINESS_ID, requireContext())));
            binding.textChangeAccountType.setClickable(false);
            binding.cardViewQrCode.setVisibility(View.GONE);
        }

        else if(role.equalsIgnoreCase("merchant")){
            binding.textAccountType.setText("Merchant");
            binding.textChangeAccountType.setVisibility(View.GONE);
            binding.cardViewQrCode.setVisibility(View.VISIBLE);
        } else{
            binding.textAccountType.setText("Default User");
            binding.textChangeAccountType.setVisibility(View.VISIBLE);
            binding.cardViewQrCode.setVisibility(View.GONE);
        }



            binding.layoutSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.layoutEmploymentInfo.getVisibility() == View.VISIBLE) {
                    binding.chevronEmploymentDetails.setRotation(0);
                    binding.layoutEmploymentInfo.setVisibility(View.GONE);

                }
                if (binding.layoutBusinessInfo.getVisibility() == View.VISIBLE) {
                    binding.chevronBusinessInformation.setRotation(0);
                    binding.layoutBusinessInfo.setVisibility(View.GONE);

                }

                if (binding.layoutEmploymentBusinessInfoDetails.getVisibility() == View.VISIBLE) {
                    binding.chevronEmploymentBusinessInfo.setRotation(0);
                    binding.layoutEmploymentBusinessInfoDetails.setVisibility(View.GONE);


                }
                if (binding.layoutIdInfoDetails.getVisibility() == View.VISIBLE) {
                    binding.chevronIdInformation.setRotation(0);
                    binding.layoutIdInfoDetails.setVisibility(View.GONE);


                }
                if(binding.layoutSecurityInfoDetails.getVisibility() == View.VISIBLE){
                    binding.chevronSecurity.setRotation(0);
                    binding.layoutSecurityInfoDetails.setVisibility(View.GONE);

                }else {


                    binding.chevronSecurity.setRotation(90);
                    binding.layoutSecurityInfoDetails.setVisibility(View.VISIBLE);

                }




            }
        });

        binding.layoutChangePassword.setOnClickListener(view13 -> {
            // Create and show the dialog.
            DialogFragment depositDialog = new ChangePassword(getString(R.string.change_pin));
            depositDialog.show(getFragmentManager(), "dialog");

        });

        binding.layoutUpdateSecurityQns.setOnClickListener(view13 ->{
            dialogLoader = new DialogLoader(context);
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context, R.style.DialogFullscreen);
            View dialogView = getLayoutInflater().inflate(R.layout.new_forgot_pin, null);
            dialog.setView(dialogView);
            dialog.setCancelable(true);
            //calling security qns form
            phone_number =dialogView.findViewById(R.id.phone_no);
            firstSecurityQn = dialogView.findViewById(R.id.sp_first_security_qn);
            secondSecurityQn = dialogView.findViewById(R.id.sp_second_security_qn);
            thirdSecurityQn = dialogView.findViewById(R.id.sp_third_security_qn);
            firstQnAnswer = dialogView.findViewById(R.id.etxt_first_security_qn);
            secondQnAnswer = dialogView.findViewById(R.id.etxt_second_security_qn);
            thirdQnAnswer = dialogView.findViewById(R.id.etxt_third_security_qn);

            subscribeToSecurityQns(viewModel.getSecurityQuestions());
            //display security qns and
            Button submit = dialogView.findViewById(R.id.btn_submit_security_qn);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogLoader.hideProgressDialog();
                   // update security questions based on user_id
                    String access_token =  WalletHomeActivity.WALLET_ACCESS_TOKEN;
                    String request_id = WalletHomeActivity.generateRequestId();

                    //load answered security Qns(locally or from an endpoint)
                    /******************RETROFIT IMPLEMENTATION***********************/
//                    Call<SecurityQnsResponse> call = APIClient.getWalletInstance(getContext()).
//                            updateSecurityQns(access_token,
//                                    getString(R.string.phone_number_code)+phone_number.getText().toString(),
//                                    firstSecurityQn.getSelectedItem().toString(),
//                                    secondSecurityQn.getSelectedItem().toString(),
//                                    thirdSecurityQn.getSelectedItem().toString(),
//                                    firstQnAnswer.getText().toString(),
//                                    secondQnAnswer.getText().toString(),
//                                    thirdQnAnswer.getText().toString(),
//                                    request_id,
//                                    "updateSecurityQns");
//                    call.enqueue(new Callback<SecurityQnsResponse>() {
//                        @Override
//                        public void onResponse(Call<SecurityQnsResponse> call, Response<SecurityQnsResponse> response) {
//                            if(response.isSuccessful()){
//                                String status = response.body().getStatus();
//                                if(status.equalsIgnoreCase("1")){
//                                    //call change password dialog
//
//                                    Toast.makeText(context,response.body().getMessage(),Toast.LENGTH_LONG).show();
//
//
//                                }else{
//                                    Toast.makeText(context,response.body().getMessage(),Toast.LENGTH_LONG).show();
//
//                                }
//                                dialogLoader.hideProgressDialog();
//
//
//                            }else if (response.code() == 401) {
//                                Toast.makeText(context,response.body().getMessage(),Toast.LENGTH_LONG).show();
//                                dialogLoader.hideProgressDialog();
//
//                            }
//
//                        }
//
//                        @Override
//                        public void onFailure(Call<SecurityQnsResponse> call, Throwable t){
//                            dialogLoader.hideProgressDialog();
//                        }
//                    });

                }
            });

            Button cancel = dialogView.findViewById(R.id.btn_cancel_security_qn);



            final android.app.AlertDialog alertDialog = dialog.create();
            cancel.setOnClickListener(v->{alertDialog.dismiss();});
            alertDialog.show();


        });


            binding.textShareApp.setOnClickListener(view13 -> {

                if (binding.layoutEmploymentInfo.getVisibility() == View.VISIBLE) {
                    binding.chevronEmploymentDetails.setRotation(0);
                    binding.layoutEmploymentInfo.setVisibility(View.GONE);

                }
                if (binding.layoutBusinessInfo.getVisibility() == View.VISIBLE) {
                    binding.chevronBusinessInformation.setRotation(0);
                    binding.layoutBusinessInfo.setVisibility(View.GONE);

                }

                if (binding.layoutEmploymentBusinessInfoDetails.getVisibility() == View.VISIBLE) {
                    binding.chevronEmploymentBusinessInfo.setRotation(0);
                    binding.layoutEmploymentBusinessInfoDetails.setVisibility(View.GONE);


                }
                if (binding.layoutIdInfoDetails.getVisibility() == View.VISIBLE) {
                    binding.chevronIdInformation.setRotation(0);
                    binding.layoutIdInfoDetails.setVisibility(View.GONE);


                }
                if(binding.layoutSecurityInfoDetails.getVisibility() == View.VISIBLE){
                    binding.chevronSecurity.setRotation(0);
                    binding.layoutSecurityInfoDetails.setVisibility(View.GONE);

                }

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            });

            binding.textRateApp.setOnClickListener(view13 -> {

                if (binding.layoutEmploymentInfo.getVisibility() == View.VISIBLE) {
                    binding.chevronEmploymentDetails.setRotation(0);
                    binding.layoutEmploymentInfo.setVisibility(View.GONE);

                }
                if (binding.layoutBusinessInfo.getVisibility() == View.VISIBLE) {
                    binding.chevronBusinessInformation.setRotation(0);
                    binding.layoutBusinessInfo.setVisibility(View.GONE);

                }

                if (binding.layoutEmploymentBusinessInfoDetails.getVisibility() == View.VISIBLE) {
                    binding.chevronEmploymentBusinessInfo.setRotation(0);
                    binding.layoutEmploymentBusinessInfoDetails.setVisibility(View.GONE);


                }

                if (binding.layoutIdInfoDetails.getVisibility() == View.VISIBLE) {
                    binding.chevronIdInformation.setRotation(0);
                    binding.layoutIdInfoDetails.setVisibility(View.GONE);


                }
                if(binding.layoutSecurityInfoDetails.getVisibility() == View.VISIBLE){
                    binding.chevronSecurity.setRotation(0);
                    binding.layoutSecurityInfoDetails.setVisibility(View.GONE);

                }
                //rate app
               // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)));
            });

            binding.btnLogout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (binding.layoutEmploymentInfo.getVisibility() == View.VISIBLE) {
                        binding.chevronEmploymentDetails.setRotation(0);
                        binding.layoutEmploymentInfo.setVisibility(View.GONE);

                    }

                    if (binding.layoutBusinessInfo.getVisibility() == View.VISIBLE) {
                        binding.chevronBusinessInformation.setRotation(0);
                        binding.layoutBusinessInfo.setVisibility(View.GONE);

                    }

                    if (binding.layoutEmploymentBusinessInfoDetails.getVisibility() == View.VISIBLE) {
                        binding.chevronEmploymentBusinessInfo.setRotation(0);
                        binding.layoutEmploymentBusinessInfoDetails.setVisibility(View.GONE);
                    }

                    if (binding.layoutIdInfoDetails.getVisibility() == View.VISIBLE) {
                        binding.chevronIdInformation.setRotation(0);
                        binding.layoutIdInfoDetails.setVisibility(View.GONE);


                    }
                    if(binding.layoutSecurityInfoDetails.getVisibility() == View.VISIBLE){
                        binding.chevronSecurity.setRotation(0);
                        binding.layoutSecurityInfoDetails.setVisibility(View.GONE);

                    }

                    logoutUser();
                }
            });

            binding.layoutCustomerSupport.setOnClickListener(v -> {

                if (binding.layoutEmploymentInfo.getVisibility() == View.VISIBLE) {
                    binding.chevronEmploymentDetails.setRotation(0);
                    binding.layoutEmploymentInfo.setVisibility(View.GONE);

                }
                if (binding.layoutBusinessInfo.getVisibility() == View.VISIBLE) {
                    binding.chevronBusinessInformation.setRotation(0);
                    binding.layoutBusinessInfo.setVisibility(View.GONE);

                }

                if (binding.layoutEmploymentBusinessInfoDetails.getVisibility() == View.VISIBLE) {
                    binding.chevronEmploymentBusinessInfo.setRotation(0);
                    binding.layoutEmploymentBusinessInfoDetails.setVisibility(View.GONE);


                }
                if (binding.layoutIdInfoDetails.getVisibility() == View.VISIBLE) {
                    binding.chevronIdInformation.setRotation(0);
                    binding.layoutIdInfoDetails.setVisibility(View.GONE);


                }
                if(binding.layoutSecurityInfoDetails.getVisibility() == View.VISIBLE){
                    binding.chevronSecurity.setRotation(0);
                    binding.layoutSecurityInfoDetails.setVisibility(View.GONE);

                }

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:0800399399"));
                startActivity(intent);
            });

            binding.cardViewTermsConditions.setOnClickListener(v->{

                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                View dialogView = getLayoutInflater().inflate(R.layout.layout_terms_and_conditions, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);

                Button btn_agree = dialogView.findViewById(R.id.btn_agree);

                Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);

                TextView full_app_terms_services =dialogView.findViewById(R.id.full_app_terms_services);

                btn_agree.setVisibility(View.GONE);





                final AlertDialog alertDialog = dialog.create();

                btn_cancel.setOnClickListener(view13->{
                    alertDialog.dismiss();
                });
                full_app_terms_services.setOnClickListener(view13->{
                    Uri uri = Uri.parse("https://forms.zohopublic.com/virtualoffice20750/form/PrivacyPolicy/formperma/cMB0eFNpmuo5BfUYcYjm-56lXcYWvOL55IodE5BtBpI"); // missing 'https://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                });

                alertDialog.show();



            });

            String versionCode =  String.valueOf(BuildConfig.VERSION_CODE);
            binding.textAppVersion.setText(versionCode);

        return binding.getRoot();
    }

    private void subscribeToSecurityQns(LiveData<List<ArrayList<String>>> securityQuestions) {
            securityQuestions.observe(getViewLifecycleOwner(), myQns->{
                //set list in beneficiary spinner
                if(myQns!=null){
                    ArrayAdapter<String> beneficiariesAdapter1 = new ArrayAdapter(context, android.R.layout.simple_spinner_item, myQns.get(0));
                    ArrayAdapter<String> beneficiariesAdapter2 = new ArrayAdapter(context, android.R.layout.simple_spinner_item, myQns.get(1));
                    ArrayAdapter<String> beneficiariesAdapter3 = new ArrayAdapter(context, android.R.layout.simple_spinner_item, myQns.get(2));
                    firstSecurityQn.setAdapter(beneficiariesAdapter1);
                    secondSecurityQn.setAdapter(beneficiariesAdapter2);
                    thirdSecurityQn.setAdapter(beneficiariesAdapter3);
                }

            });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


    private String ucf(String str) {
        if (str == null || str.length() < 2)
            return str;

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public void retrieveAccountInfo(){
        String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        Call<AccountResponse> call = APIClient.getWalletInstance(getContext())
                .getAccountInfo(access_token,userId,request_id,category,"getAllUserData");
        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                if(response.isSuccessful()){
                        if(response.body().getBusinessInfo()!=null){
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_BUSINESS_NAME, response.body().getBusinessInfo().getBusiness_name(), context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_BUSINESS_LOCATION, response.body().getBusinessInfo().getBusiness_location(), context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_REG_NO, response.body().getBusinessInfo().getRegistration_no(), context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_LICENSE_NUMBER, response.body().getBusinessInfo().getLicense_no(), context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_REG_CERTIFICATE, response.body().getBusinessInfo().getRegistration_cert(), context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_TRADE_LICENSE, response.body().getBusinessInfo().getTrade_license(), context);

                        }else{
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_BUSINESS_NAME, null, context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_BUSINESS_LOCATION, null, context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_REG_NO, null, context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_LICENSE_NUMBER, null, context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_REG_CERTIFICATE, null, context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_TRADE_LICENSE, null, context);
                        }

                        if(response.body().getEmployeeInfo()!=null){
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_EMPLOYER, response.body().getEmployeeInfo().getEmployer(), context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_DESIGNATION, response.body().getEmployeeInfo().getDesignation(), context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_LOCATION, response.body().getEmployeeInfo().getLocation(), context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_EMPLOYEE_ID, response.body().getEmployeeInfo().getEmployee_id(), context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_CONTACT, response.body().getEmployeeInfo().getEmployment_contact(), context);

                        }else{
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_EMPLOYER, null, context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_DESIGNATION, null, context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_LOCATION, null, context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_EMPLOYEE_ID, null, context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_CONTACT, null, context);

                        }

                        if(response.body().getProfile()!=null){
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_DOB, response.body().getProfile().getDob(), context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_GENDER, response.body().getProfile().getGender(), context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_NOK, response.body().getProfile().getNext_of_kin(), context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_NOK_CONTACT, response.body().getProfile().getNext_of_kin_contact(), context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_PIC, response.body().getProfile().getPic(), context);
                        }else{
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_DOB, null, context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_GENDER, null, context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_NOK, null, context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_NOK_CONTACT, null, context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_PERSONAL_PIC, null, context);

                        }

                        if(response.body().getUserIdInfo()!=null){
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_TYPE, response.body().getUserIdInfo().getId_type(), context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_NUMBER, response.body().getUserIdInfo().getId_number(), context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_EXPIRY_DATE, response.body().getUserIdInfo().getExpiry_date(), context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_FRONT, response.body().getUserIdInfo().getFront(), context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_BACK, response.body().getUserIdInfo().getBack(), context);
                        }else{
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_TYPE, null, context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_NUMBER, null, context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_EXPIRY_DATE, null, context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_FRONT, null, context);
                            WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_BACK, null, context);
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

                requireActivity().finish();
                // Open login
                Intent authIntent=new Intent(requireActivity(), AuthActivity.class);
                authIntent.putExtra("flash",false);
                startActivity(authIntent);
            }
        }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


}