package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.customs.OtpDialogLoader;
import com.cabral.emaishapay.fragments.wallet_fragments.TokenAuthFragment;
import com.cabral.emaishapay.models.GeneralWalletResponse;
import com.cabral.emaishapay.models.external_transfer_model.Bank;
import com.cabral.emaishapay.models.external_transfer_model.BankBranch;
import com.cabral.emaishapay.models.external_transfer_model.BankBranchInfoResponse;
import com.cabral.emaishapay.models.external_transfer_model.BanksInfoResponse;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.ExternalAPIRequests;
import com.cabral.emaishapay.network.api_helpers.RaveV2APIClient;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBeneficiary extends DialogFragment {
    private static final String TAG = "AddBeneficiaryFragment";
    LinearLayout bankLayout, mobileMoneyLayout,beneficiaryNameLayout,beneficiaryMobileLayout;
    Spinner transactionTypeSp, bankSp,bank_branch,spCountry;
    Button submit;
    Context context;
    EditText beneficiary_name_mm,account_name,beneficiary_no,account_number,etCity;
    String beneficiary_name,beneficiary_number,city,country,street_address_1,street_address_2,beneficiary_bank_phone_number;
    Bank[] BankList; BankBranch[] bankBranches;
    String selected_bank_code,selected_branch_code,bankk,branch,id,sSpCountry;
    TextView title;
    OtpDialogLoader otpDialogLoader;
    DialogLoader dialogLoader;
    List<String> Banknames = new ArrayList<>();

    public AddBeneficiary() {
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
        bankSp = view.findViewById(R.id.sp_bank);
        bank_branch = view.findViewById(R.id.sp_bank_branch);
        title = view.findViewById(R.id.agent_bal_inquiry_title_label);
        etCity = view.findViewById(R.id.et_city);
        spCountry = view.findViewById(R.id.sp_country);
        spCountry.setSelection(1);
        spCountry.setEnabled(false);
        beneficiaryMobileLayout = view.findViewById(R.id.layout_beneficiary_mobile);
        beneficiaryNameLayout = view.findViewById(R.id.layout_beneficiary_name);

        dialogLoader=new DialogLoader(getContext());
        loadTransferBanks();

        if(getArguments()!=null){
            //fill views and call update beneficiary
            String beneficiary_type = getArguments().getString("beneficiary_type");
            String beneficiary_name_ = getArguments().getString("beneficiary_name");
            String beneficiary_number_ = getArguments().getString("beneficiary_no");
            String beneficiary_phone = getArguments().getString("beneficiary_phone");
             bankk = getArguments().getString("bank");
             branch = getArguments().getString("branch");
             id = getArguments().getString("id");
             city = getArguments().getString("city");
            street_address_1 = getArguments().getString("address1");
            street_address_2 = getArguments().getString("address2");
            sSpCountry = getArguments().getString("country");
            Log.w(TAG, "onCreateDialog: number"+city+"name"+branch+"id"+id);

            if(beneficiary_type.equalsIgnoreCase("bank")){
                beneficiaryNameLayout.setVisibility(View.GONE);
                beneficiaryMobileLayout.setVisibility(View.VISIBLE);
                bankLayout.setVisibility(View.VISIBLE);
                account_name.setText(beneficiary_name_);
                account_number.setText(beneficiary_number_);
                beneficiary_no.setText(beneficiary_phone);
                etCity.setText(city);
                try {
                    bankSp.setSelection(Banknames.indexOf(bankk));
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("IndexError",e.getMessage());
                }

                WalletHomeActivity.selectSpinnerItemByValue(spCountry,sSpCountry);
                WalletHomeActivity.selectSpinnerItemByValue(bank_branch,branch);

            }else{
                beneficiaryNameLayout.setVisibility(View.VISIBLE);
                beneficiaryMobileLayout.setVisibility(View.VISIBLE);
                bankLayout.setVisibility(View.GONE);

                if(beneficiary_name_!=null && beneficiary_number_!=null){
                    beneficiary_name_mm.setText(beneficiary_name_);
                    beneficiary_no.setText(beneficiary_number_.substring(1));
                }


            }
            WalletHomeActivity.selectSpinnerItemByValue(transactionTypeSp,beneficiary_type);
            transactionTypeSp.setEnabled(false);
            transactionTypeSp.setClickable(false);
            beneficiary_name_mm.setEnabled(false);
            beneficiary_no.setEnabled(false);
            account_name.setEnabled(false);
            account_number.setEnabled(false);
            title.setText("VIEW BENEFICIARY");
            submit.setVisibility(View.GONE);

        }
        else {

            transactionTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        //Change selected text color
                        ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                        //((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);//Change selected text size
                    } catch (Exception e) {

                    }

                    if(position==0){
                        beneficiaryNameLayout.setVisibility(View.GONE);
                        beneficiaryMobileLayout.setVisibility(View.GONE);
                        bankLayout.setVisibility(View.GONE);
                    }
                    else if(position==1){
                        beneficiaryNameLayout.setVisibility(View.VISIBLE);
                        beneficiaryMobileLayout.setVisibility(View.VISIBLE);
                        bankLayout.setVisibility(View.GONE);
                    }
                    else{
                        beneficiaryNameLayout.setVisibility(View.GONE);
                        beneficiaryMobileLayout.setVisibility(View.VISIBLE);
                        bankLayout.setVisibility(View.VISIBLE);
                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            branch=bank_branch.getSelectedItem().toString();
        }



        bankSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //Change selected text color
                    ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                    //((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);//Change selected text size
                } catch (Exception e) {

                }

                if(BankList!=null)
                    for (Bank bank_: BankList) {
                        if(bank_.getName().equalsIgnoreCase(bankSp.getSelectedItem().toString())){
                            selected_bank_code=bank_.getCode();
                            getTransferBankBranches(bank_.getId());

                        }
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

            });

        bank_branch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        //Change selected text color
                        ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                        //((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);//Change selected text size
                    } catch (Exception e) {

                    }

                    if(bankBranches!=null)
                        for (BankBranch branch: bankBranches) {
                            if(branch.getBranchName().equalsIgnoreCase(bank_branch.getSelectedItem().toString())){
                                selected_branch_code=branch.getBranchCode();

                            }
                        }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //Change selected text color
                    ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                    //((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);//Change selected text size
                } catch (Exception e) {

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
                    street_address_1 = getString(R.string.street_address_1);
                    street_address_2 = getString(R.string.street_address_2);

                    String beneficary_type = transactionTypeSp.getSelectedItem().toString().trim();
                    if(beneficary_type.equalsIgnoreCase("mobile money")){
                        //  encrypter = new CryptoUtil(BuildConfig.ENCRYPTION_KEY, context.getString(R.string.iv));
                        beneficiary_name = beneficiary_name_mm.getText().toString();
                        beneficiary_number = getString(R.string.phone_number_code)+beneficiary_no.getText().toString();
                        bankk = "Mobile Money Bank";
                        branch = "";
                        city ="";
                        country = "";
                        beneficiary_bank_phone_number = getString(R.string.phone_number_code)+beneficiary_no.getText().toString();


                    }else{
                        //encript account_name and number
                        //CryptoUtil encrypter = new CryptoUtil(BuildConfig.ENCRYPTION_KEY, context.getString(R.string.iv));
                         beneficiary_name = account_name.getText().toString();
                        beneficiary_number = account_number.getText().toString();
                        bankk = bankSp.getSelectedItem().toString();
                        branch = bank_branch.getSelectedItem().toString();
                        city =etCity.getText().toString();
                        country = spCountry.getSelectedItem().toString();
                        beneficiary_bank_phone_number =getString(R.string.phone_number_code)+beneficiary_no.getText().toString();


                    }
                    String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
                    String user_id = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
                    String request_id = WalletHomeActivity.generateRequestId();
                    String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());

                    if(submit.getText().toString().equalsIgnoreCase("update")){
                        dialogLoader.showProgressDialog();

                        /*************RETROFIT IMPLEMENTATION**************/
                        Call<GeneralWalletResponse> call = APIClient.getWalletInstance(getContext())
                                .updateBeneficiary(access_token, id, beneficary_type, bankk, branch, beneficiary_name, beneficiary_number, request_id,city,country,street_address_1,street_address_2,beneficiary_bank_phone_number);
                        call.enqueue(new Callback<GeneralWalletResponse>() {
                            @Override
                            public void onResponse(Call<GeneralWalletResponse> call, Response<GeneralWalletResponse> response) {
                                if (response.isSuccessful()) {
                                    dialogLoader.hideProgressDialog();
                                    if (response.body().getStatus().equalsIgnoreCase("0")) {
                                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();

                                    } else {
                                        String message = response.body().getMessage();
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();


                                        //To BeneficiariesListFragment();
                                        WalletHomeActivity.navController.popBackStack(R.id.beneficiariesListFragment,true);
                                        WalletHomeActivity.navController.navigate(R.id.action_walletHomeFragment2_to_beneficiariesListFragment);

                                    }

                                } else if (response.code() == 401) {
                                    Toast.makeText(context, "session expired", Toast.LENGTH_LONG).show();

                                    //redirect to auth
                                    TokenAuthFragment.startAuth( true);

                                }
                            }


                            @Override
                            public void onFailure(Call<GeneralWalletResponse> call, Throwable t) {
                                dialogLoader.hideProgressDialog();

                            }
                        });
                    }
                    else {
                        requestsaveBeneficiary(access_token,user_id, category,beneficary_type);

                    }


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

    private void saveBeneficiary(String access_token, String user_id, String category, String otp_code, String type, String beneficary_type, Dialog otpDialog, OtpDialogLoader otpDialogLoader) {
        Log.d(TAG, "saveBeneficiary: " + beneficiary_name);
        dialogLoader.showProgressDialog();

        String request_id = WalletHomeActivity.generateRequestId();
        /*************RETROFIT IMPLEMENTATION**************/
        Call<GeneralWalletResponse> call =null;

        if (beneficary_type.equalsIgnoreCase("Bank")) {
            call = APIClient.getWalletInstance(getContext()).saveBankBeneficiary(
                    access_token,  otp_code,
                    user_id,  type,  bankk, branch,   beneficiary_name,  beneficiary_number,
                    request_id,  category,   "registerBankBeneficiary",
                    beneficiary_bank_phone_number, city,  country,  street_address_1,   street_address_2);
        } else {

            call = APIClient.getWalletInstance(getContext()).saveBeneficiary(
                    access_token, otp_code,  user_id, type,
                    bankk, branch,  beneficiary_name,  beneficiary_number,
                    request_id,  category,  "saveBeneficiary",
                    beneficiary_bank_phone_number,   city,   country,  street_address_1, street_address_2);
        }





        call.enqueue(new Callback<GeneralWalletResponse>() {
            @Override
            public void onResponse(Call<GeneralWalletResponse> call, Response<GeneralWalletResponse> response) {
                dialogLoader.hideProgressDialog();
                if (response.isSuccessful() && response.body().getStatus().equalsIgnoreCase("1")) {
                    otpDialog.dismiss();
                    //success message
                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.dialog_successful_message);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(false);
                    TextView text = dialog.findViewById(R.id.dialog_success_txt_message);
                    text.setText( response.body().getMessage() );


                    dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            AddBeneficiary.this.dismiss();
                            //To BeneficiariesListFragment();
                            WalletHomeActivity.navController.popBackStack(R.id.beneficiariesListFragment,true);
                            if(WalletHomeActivity.navController.getCurrentDestination().getId() == R.id.walletHomeFragment2)
                            WalletHomeActivity.navController.navigate(R.id.action_walletHomeFragment2_to_beneficiariesListFragment);
                        }
                    });
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();



                }else if( response.isSuccessful() ){
                    otpDialogLoader.clearOTPDialog();
                    String message = response.body().getMessage();
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
                else if (response.code() == 401) {
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();

                    //redirect to auth
                    TokenAuthFragment.startAuth( true);

                }
            }

            @Override
            public void onFailure(Call<GeneralWalletResponse> call, Throwable t) {
                dialogLoader.hideProgressDialog();

            }
        });
    }

    public void requestsaveBeneficiary(String access_token, String user_id,  String category, String beneficary_type) {

        String request_id = WalletHomeActivity.generateRequestId();
        String type="";

        if(category.equalsIgnoreCase("default") && beneficary_type.equalsIgnoreCase("mobile money") ){
            type="defaultAddBeneficiaryMobileMoney";
        }
        else if(category.equalsIgnoreCase("agent") && beneficary_type.equalsIgnoreCase("mobile money") ){
            type="agentAddBeneficiaryMobileMoney";
        } else if(category.equalsIgnoreCase("merchant") && beneficary_type.equalsIgnoreCase("mobile money") ){
            type="merchantAddBeneficiaryMobileMoney";
        }else if(category.equalsIgnoreCase("default") && beneficary_type.equalsIgnoreCase("Bank") ){
            type="defaultAddBeneficiaryBank";
        }
        else if(category.equalsIgnoreCase("agent") && beneficary_type.equalsIgnoreCase("Bank") ){
            type="agentAddBeneficiaryBank";
        } else if(category.equalsIgnoreCase("merchant") && beneficary_type.equalsIgnoreCase("Bank") ){
            type="merchantAddBeneficiaryBank";
        }


        String customer_phone_number=WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_PHONE_NUMBER,getActivity());
        dialogLoader.showProgressDialog();

        /*************RETROFIT IMPLEMENTATION**************/
        Call<GeneralWalletResponse> call = APIClient.getWalletInstance(getContext())
                .requestSaveBeneficiary(
                        access_token,
                        user_id,
                        type,
                        beneficiary_name,
                        customer_phone_number,
                        request_id,
                        category,
                        "customerAddBeneficiaryTransactionOTP");

        String finalType = type;
        call.enqueue(new Callback<GeneralWalletResponse>() {
            @Override
            public void onResponse(Call<GeneralWalletResponse> call, Response<GeneralWalletResponse> response) {
                dialogLoader.hideProgressDialog();
                if (response.isSuccessful() && response.body().getStatus().equalsIgnoreCase("1")) {


                    Log.w("PhoneNumberError",customer_phone_number);

                    otpDialogLoader=new OtpDialogLoader( AddBeneficiary.this) {
                        @Override
                        protected void onConfirmOtp(String otp_code, Dialog otpDialog) {
                           // otpDialog.dismiss();
                            saveBeneficiary(access_token,user_id, category,otp_code, finalType,beneficary_type,otpDialog,otpDialogLoader);
                        }

                        @Override
                        protected void onResendOtp() {
                            otpDialogLoader.resendOtp(
                                    customer_phone_number,
                                    dialogLoader,
                                    submit

                            );
                        }
                    };
                    otpDialogLoader.showOTPDialog();


                }
                else if(response.isSuccessful() ){

                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
                else if (response.code() == 401) {
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();

                    //redirect to auth
                    TokenAuthFragment.startAuth( true);

                }
            }

            @Override
            public void onFailure(Call<GeneralWalletResponse> call, Throwable t) {
                dialogLoader.hideProgressDialog();

            }
        });
    }

    private void  loadTransferBanks(){

        ExternalAPIRequests apiRequests = RaveV2APIClient.getRavePayV2Instance();
        Call<BanksInfoResponse> call = apiRequests.getTransferBanks(getString(R.string.countryCode), BuildConfig.PUBLIC_KEY);

        dialogLoader.showProgressDialog();
        call.enqueue(new Callback<BanksInfoResponse>() {
            @Override
            public void onResponse(@NotNull Call<BanksInfoResponse> call, @NotNull Response<BanksInfoResponse> response) {
                if (response.code() == 200) {
                    Banknames.add("Select");

                    try {
                        BanksInfoResponse.InfoData bankInfo = response.body().getData();
                        BankList=bankInfo.getBanks();
                        Log.w("Banks_NumberFetched",BankList.length+" #############");

                        for (Bank bank: BankList) {
                            if( bank.getMobileVerified()==null && !bank.getName().equals("MTN") )
                                Banknames.add(bank.getName());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, Banknames);
                        bankSp.setAdapter(adapter);

                    } catch (Exception e) {
                        Log.e("response", response.toString());
                        e.printStackTrace();
                    } finally {

                        if(getArguments()!=null) {
                            //fill views and call update beneficiary
                            String beneficiary_type = getArguments().getString("beneficiary_type");
                            if(beneficiary_type.equalsIgnoreCase("bank")){
                                bankSp.setSelection(Banknames.indexOf(bankk));

                                for (Bank bank_: BankList) {
                                    if(bank_.getName().equalsIgnoreCase(bankSp.getSelectedItem().toString())){
                                        selected_bank_code=bank_.getCode();
                                        getTransferBankBranches(bank_.getId());

                                    }
                                }

                            }
                        }
                        dialogLoader.hideProgressDialog();
                    }
                } else {
                    dialogLoader.hideProgressDialog();
                    //Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    String message = response.body().getMessage();
                    Snackbar.make(bankLayout, message, Snackbar.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<BanksInfoResponse> call, Throwable t) {
                Log.e("info2 : ", t.getMessage());
                dialogLoader.hideProgressDialog();
            }
        });

    }

    private void  getTransferBankBranches(String bank_id){
        ExternalAPIRequests apiRequests = RaveV2APIClient.getRavePayV2Instance();
        Call<BankBranchInfoResponse> call = apiRequests.getTransferBankBranches(bank_id, BuildConfig.PUBLIC_KEY);
        List<String> BankBranchnames = new ArrayList<>();
        dialogLoader.showProgressDialog();

        call.enqueue(new Callback<BankBranchInfoResponse>() {
            @Override
            public void onResponse(@NotNull Call<BankBranchInfoResponse> call, @NotNull Response<BankBranchInfoResponse> response) {
                if (response.code() == 200) {
                    try {
                        bankBranches = response.body().getData().getBankBranches();

                        Log.w("Banks_NumberFetched",bankBranches.length+"****************");

                        BankBranchnames.add("Select");
                        for (BankBranch bank: bankBranches) {
                            BankBranchnames.add(bank.getBranchName());

                        }
                        BankBranchnames.add("Other");
                        ArrayAdapter<String> adapter = new ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, BankBranchnames);
                        bank_branch.setAdapter(adapter);
                    } catch (Exception e) {
                        Log.e("response", response.toString());
                        e.printStackTrace();
                    } finally {

                        int i=1;//sets Bank brank for preview/Update
                        for (BankBranch bank: bankBranches) {
                            BankBranchnames.add(bank.getBranchName());
                            if( branch.equalsIgnoreCase(bank.getBranchName() )) {
                                bank_branch.setSelection(i);
                            }
                            i++;

                        }

                        dialogLoader.hideProgressDialog();
                    }
                } else {
                    dialogLoader.hideProgressDialog();
                    //Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    if(response.body()!=null){
                        String message = response.body().getMessage();
                        Snackbar.make(bankLayout, message, Snackbar.LENGTH_SHORT).show();
                    }

                }


            }

            @Override
            public void onFailure(Call<BankBranchInfoResponse> call, Throwable t) {
                Log.e("info2 : ", t.getMessage());
                dialogLoader.hideProgressDialog();
            }
        });

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


        }else   if (transactionTypeSp.getSelectedItem().toString().trim().equalsIgnoreCase("bank") && etCity.getText().toString().trim().isEmpty()) {
            check = false;
            etCity.setError("Please enter city");


        }else   if (transactionTypeSp.getSelectedItem().toString().trim().equalsIgnoreCase("bank") && spCountry.getSelectedItem().toString().trim().equalsIgnoreCase("select")) {
            check = false;
            Toast.makeText(context,"Please select country",Toast.LENGTH_LONG).show();



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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        otpDialogLoader.onActivityResult(requestCode, resultCode, data);
    }

}