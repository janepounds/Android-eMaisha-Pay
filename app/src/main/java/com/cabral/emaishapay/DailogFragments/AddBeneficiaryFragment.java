package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.fragments.BeneficiariesListFragment;
import com.cabral.emaishapay.fragments.CardListFragment;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.utils.CryptoUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBeneficiaryFragment extends DialogFragment {
    LinearLayout bankLayout, mobileMoneyLayout;
    Spinner transactionTypeSp;
    Button submit;
    Context context;
    EditText beneficiary_name_mm,account_name,beneficiary_no,account_number;
    String beneficiary_name,beneficiary_number;
    public AddBeneficiaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_add_beneficiary, null);
        submit = view.findViewById(R.id.button_add_money);
        beneficiary_name_mm = view.findViewById(R.id.et_beneficiary_name);
        beneficiary_no = view.findViewById(R.id.et_beneficiary_mobile);
        account_name = view.findViewById(R.id.et_account_name);
        account_number = view.findViewById(R.id.et_account_number);
        bankLayout = view.findViewById(R.id.layout_bank);
        mobileMoneyLayout = view.findViewById(R.id.layout_mobile_money);
        transactionTypeSp = view.findViewById(R.id.sp_transaction_type);

        if(getArguments()!=null){
            //fill views and call update beneficiary
            String beneficiary_type = getArguments().getString("beneficiary_type");
            String beneficiary_name = getArguments().getString("beneficiary_name");
            String initials = getArguments().getString("initials");
            String beneficiary_number = getArguments().getString("beneficiary_no");
            if(beneficiary_type.equalsIgnoreCase("bank")){

                mobileMoneyLayout.setVisibility(View.GONE);
                bankLayout.setVisibility(View.VISIBLE);
                account_name.setText(beneficiary_name);
                account_number.setText(beneficiary_number);


            }else{
                mobileMoneyLayout.setVisibility(View.VISIBLE);
                bankLayout.setVisibility(View.GONE);
                beneficiary_name_mm.setText(beneficiary_name);
                beneficiary_no.setText(beneficiary_number);


            }
            WalletHomeActivity.selectSpinnerItemByValue(transactionTypeSp,beneficiary_type);


            submit.setText("UPDATE");
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProgressDialog dialog;
                    dialog = new ProgressDialog(context);
                    dialog.setIndeterminate(true);
                    dialog.setMessage("Please Wait..");
                    dialog.setCancelable(false);
                    dialog.show();

                    //call retrofit method for deleting card
                    String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
                    String request_id = WalletHomeActivity.generateRequestId();
                    String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
                    /*************RETROFIT IMPLEMENTATION**************/
                    Call<CardResponse> call = APIClient.getWalletInstance().updateBeneficiary(access_token,transactionTypeSp.getSelectedItem().toString(),beneficiary_name,beneficiary_number,request_id,category,"");
                    call.enqueue(new Callback<CardResponse>() {
                        @Override
                        public void onResponse(Call<CardResponse> call, Response<CardResponse> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getStatus() == 0) {
                                    dialog.dismiss();
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();

                                } else {
                                    String message = response.body().getMessage();
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                                    getActivity().getSupportFragmentManager().popBackStack();

                                    Fragment fragment = new CardListFragment();
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .hide(((WalletHomeActivity) getActivity()).currentFragment)
                                            .replace(R.id.wallet_home_container, fragment)
                                            .addToBackStack(null).commit();

                                    dialog.dismiss();
                                }

                            }else if(response.code()==401){
                                dialog.dismiss();
                                Toast.makeText(context, "session expired", Toast.LENGTH_LONG).show();

                                //redirect to auth
                                TokenAuthActivity.startAuth(getActivity(), true);
                                getActivity().finishAffinity();
                            }
                        }


                        @Override
                        public void onFailure(Call<CardResponse> call, Throwable t) {
                            dialog.dismiss();

                        }
                    });
                }
            });

        }

        transactionTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position==0){
                    mobileMoneyLayout.setVisibility(View.GONE);
                    bankLayout.setVisibility(View.GONE);


                }
                else if(position==1){
                    mobileMoneyLayout.setVisibility(View.VISIBLE);
                    bankLayout.setVisibility(View.GONE);

                }
                else if(position==2){
                    mobileMoneyLayout.setVisibility(View.GONE);
                    bankLayout.setVisibility(View.VISIBLE);

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (validateEntries()) {

                    ProgressDialog dialog;
                    dialog = new ProgressDialog(context);
                    dialog.setIndeterminate(true);
                    dialog.setMessage("Please Wait..");
                    dialog.setCancelable(false);
                    dialog.show();

                    String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
                    String request_id = WalletHomeActivity.generateRequestId();
                    String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
                    String beneficary_type = transactionTypeSp.getSelectedItem().toString().trim();
                    if(beneficary_type.equalsIgnoreCase("mobile money")){
                        beneficiary_name = beneficiary_name_mm.getText().toString();
                        beneficiary_number ="0"+beneficiary_no.getText().toString();

                    }else{
                        beneficiary_name = account_name.getText().toString();
                        beneficiary_number = account_number.getText().toString();

                    }

                        /*************RETROFIT IMPLEMENTATION**************/
                        Call<CardResponse> call = APIClient.getWalletInstance().saveBeneficiary(access_token,beneficary_type,beneficiary_name,beneficiary_number,request_id,category,"");
                        call.enqueue(new Callback<CardResponse>() {
                            @Override
                            public void onResponse(Call<CardResponse> call, Response<CardResponse> response) {
                                if (response.isSuccessful()) {
                                    String message = response.body().getMessage();
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                                    //redirect to card list fragment
                                    getActivity().getSupportFragmentManager().popBackStack();
                                    Fragment fragment = new BeneficiariesListFragment();
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                                    if (((WalletHomeActivity) getActivity()).currentFragment != null)
                                        fragmentManager.beginTransaction()
                                                .hide(((WalletHomeActivity) getActivity()).currentFragment)
                                                .replace(R.id.wallet_home_container, fragment)
                                                .addToBackStack(null).commit();


                                    dialog.dismiss();
                                } else if (response.code() == 401) {
                                    dialog.dismiss();
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();

                                    //redirect to auth
                                    TokenAuthActivity.startAuth(getActivity(), true);
                                    getActivity().finishAffinity();
                                }
                            }

                            @Override
                            public void onFailure(Call<CardResponse> call, Throwable t) {
                                dialog.dismiss();

                            }
                        });



                }
            }
        });



        builder.setView(view);
        Dialog dialog = builder.create();
//        dialog.setCanceledOnTouchOutside(false);
//        setCancelable(false);

        ImageView close = view.findViewById(R.id.img_beneficiary_close);
        close.setOnClickListener(v -> dismiss());

        return dialog;

    }
    public boolean validateEntries(){


        boolean check = true;
        if(transactionTypeSp.getSelectedItemPosition()==0){
            check = false;
            Toast.makeText(context,"please select transction type",Toast.LENGTH_LONG);
        }


        if (transactionTypeSp.getSelectedItem().toString().trim().equalsIgnoreCase("bank") && account_number.getText().toString().trim().isEmpty()) {
            check = false;
            account_number.setError("Please enter account number");


        } else   if (transactionTypeSp.getSelectedItem().toString().trim().equalsIgnoreCase("bank") && account_name.getText().toString().trim().isEmpty()) {
            check = false;
            account_name.setError("Please enter account name");


        }

        else   if (transactionTypeSp.getSelectedItem().toString().trim().equalsIgnoreCase("mobile money") && beneficiary_name_mm.getText().toString().trim().isEmpty()) {
            check = false;
            beneficiary_name_mm.setError("Please enter beneficiary name");


        }

        else   if (transactionTypeSp.getSelectedItem().toString().trim().equalsIgnoreCase("mobile money") && beneficiary_no.getText().toString().trim().isEmpty()) {
            check = false;
            beneficiary_no.setError("Please enter beneficiary number");


        }

        return check;

    }
}