package com.cabral.emaishapay.fragments.wallet_fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.DailogFragments.AddBeneficiary;
import com.cabral.emaishapay.DailogFragments.AddCardFragment;
import com.cabral.emaishapay.DailogFragments.ConfirmTransfer;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.models.BeneficiaryResponse;
import com.cabral.emaishapay.models.ConfirmationDataResponse;
import com.cabral.emaishapay.models.WalletTransactionInitiation;
import com.cabral.emaishapay.models.external_transfer_model.Bank;
import com.cabral.emaishapay.models.external_transfer_model.BankBranch;
import com.cabral.emaishapay.models.external_transfer_model.BankBranchInfoResponse;
import com.cabral.emaishapay.models.external_transfer_model.BanksInfoResponse;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;
import com.cabral.emaishapay.network.api_helpers.ExternalAPIRequests;
import com.cabral.emaishapay.network.api_helpers.RaveV2APIClient;
import com.cabral.emaishapay.utils.ValidateInputs;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferMoney extends Fragment {
    private static final String TAG = "TransferMoney";
    LinearLayout layoutMobileNumber, layoutEmaishaCard,layoutBeneficiary,layoutAmount,layoutMobileMoneyBeneficiaries;
    Button addMoneyImg;
    TextView mobile_numberTxt, addMoneyTxt,transferTotxt;

    Spinner spTransferTo, spSelectBankBranch,spSelectBank,spBeneficiary;
    EditText  etAccountName, etAccountNumber,etAmount;

    FragmentManager fm;
    EditText etMobileMoneyNumber;
    EditText etBeneficiaryName;
    private Context context;
    private Toolbar toolbar;
    DialogLoader dialogLoader;
    Bank[] BankList; BankBranch[] bankBranches;
    String beneficiary_name, selected_bank_code, selected_branch_code;
    String action;
    private List<BeneficiaryResponse.Beneficiaries> beneficiariesList = new ArrayList();

    float amount;
    String beneficiary_number,bankk,branch,phoneNumber,account_name,account_number,beneficiary_bank_phone_number,beneficiary_id;

    public TransferMoney() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
       // getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        super.onCreate(savedInstanceState);
        dialogLoader = new DialogLoader(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transfer_money, container, false);

        if(getArguments()!=null)
            action=getArguments().getString("KEY_ACTION");

        WalletHomeActivity.bottomNavigationView.setVisibility(View.GONE);
        WalletHomeActivity.scanCoordinatorLayout.setVisibility(View.GONE);
        WalletHomeActivity.bottom_navigation_shop.setVisibility(View.GONE);
        initializeForm(view);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        toolbar.setTitle("Transfer Money");
        if(this.action.equalsIgnoreCase(getString(R.string.settlements))){
            toolbar.setTitle("Settle Money");
            transferTotxt.setText("Settle To");
            addMoneyImg.setText("SETTLE");
        }

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        return view;
    }

    public void initializeForm(View view) {
        toolbar = view.findViewById(R.id.toolbar_wallet_add_money);
        etMobileMoneyNumber = view.findViewById(R.id.money_mobile_no);
        addMoneyImg = view.findViewById(R.id.button_add_money);
        addMoneyTxt = view.findViewById(R.id.crop_add_money_amount);
        transferTotxt = view.findViewById(R.id.text_transfer_to);
        mobile_numberTxt = view.findViewById(R.id.text_mobile_number);
        spTransferTo = view.findViewById(R.id.sp_transfer_to);
        spSelectBank = view.findViewById(R.id.sp_bank);
        spSelectBankBranch = view.findViewById(R.id.sp_bank_branch);

        etAccountName=view.findViewById(R.id.et_account_name);
        etAccountNumber=view.findViewById(R.id.et_account_number);
        etAmount=view.findViewById(R.id.money_amount);
        etBeneficiaryName=view.findViewById(R.id.et_beneficiary_name);
        layoutMobileNumber=view.findViewById(R.id.layout_mobile_number);
        layoutEmaishaCard=view.findViewById(R.id.layout_emaisha_card);
        layoutBeneficiary=view.findViewById(R.id.layout_beneficiary);
        spBeneficiary = view.findViewById(R.id.sp_beneficiary);
        layoutAmount = view.findViewById(R.id.transfer_amount);
        layoutMobileMoneyBeneficiaries = view.findViewById(R.id.layout_mobile_money);


        this.fm=getActivity().getSupportFragmentManager();

        spTransferTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    //Change selected text color
                    ((TextView) view).setTextColor(ContextCompat.getColor(context,R.color.white));

                if(spTransferTo.getSelectedItem().toString().equalsIgnoreCase("select")){
                    layoutMobileNumber.setVisibility(View.GONE);
                    layoutEmaishaCard.setVisibility(View.GONE);
                    layoutBeneficiary.setVisibility(View.GONE);
                    layoutAmount.setVisibility(View.GONE);
                    layoutMobileMoneyBeneficiaries.setVisibility(View.GONE);
                }
                else if(spTransferTo.getSelectedItem().toString().equalsIgnoreCase("emaisha account")){
                    layoutMobileNumber.setVisibility(View.VISIBLE);
                    mobile_numberTxt.setText("Mobile Number");
                    layoutEmaishaCard.setVisibility(View.GONE);
                    layoutMobileMoneyBeneficiaries.setVisibility(View.GONE);
                    layoutBeneficiary.setVisibility(View.GONE);
                    layoutAmount.setVisibility(View.VISIBLE);

                }
                else if(spTransferTo.getSelectedItem().toString().equalsIgnoreCase("bank")){
                    layoutMobileNumber.setVisibility(View.GONE);
                    layoutEmaishaCard.setVisibility(View.GONE);
                    layoutMobileMoneyBeneficiaries.setVisibility(View.GONE);
                    layoutBeneficiary.setVisibility(View.VISIBLE);
                    layoutAmount.setVisibility(View.VISIBLE);
                    loadTransferBanks();
                    requestFilteredBeneficiaries();
                }
                else if(spTransferTo.getSelectedItem().toString().equalsIgnoreCase("mobile money")){

                    layoutEmaishaCard.setVisibility(View.GONE);
                    layoutMobileMoneyBeneficiaries.setVisibility(View.GONE);
                    layoutBeneficiary.setVisibility(View.VISIBLE);
                    layoutAmount.setVisibility(View.VISIBLE);
                    requestFilteredBeneficiaries();
                }
//                else if(position==4){
//                   // loadTransferBanks();
//                    layoutMobileNumber.setVisibility(View.GONE);
//                    layoutEmaishaCard.setVisibility(View.GONE);
//                    layoutBank.setVisibility(View.GONE);
//                    layoutBeneficiary.setVisibility(View.VISIBLE);
//                    layoutAmount.setVisibility(View.VISIBLE);
//                    requestFilteredBeneficiaries();
//                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        spBeneficiary.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    //Change selected text color
                    ((TextView) view).setTextColor(ContextCompat.getColor(context,R.color.white));
                    //((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);//Change selected text size

                if(spBeneficiary.getSelectedItem()==null || spTransferTo.getSelectedItem()==null){
                    return;
                }
                if (spBeneficiary.getSelectedItem().toString().equalsIgnoreCase("Add New") && spTransferTo.getSelectedItem().toString().equalsIgnoreCase("Bank")){
                    //show bank beneficiary
                    layoutMobileMoneyBeneficiaries.setVisibility(View.GONE);
                    layoutMobileNumber.setVisibility(View.GONE);
                    mobile_numberTxt.setText("Beneficiary Mobile");
                    popUpAddBeneficiary("Bank");

                }else if(spBeneficiary.getSelectedItem().toString().equalsIgnoreCase("Add New") && spTransferTo.getSelectedItem().toString().equalsIgnoreCase("Mobile Money")){
                    //show mobile money beneficiary
                    layoutMobileMoneyBeneficiaries.setVisibility(View.VISIBLE);
                    layoutMobileNumber.setVisibility(View.VISIBLE);
                    mobile_numberTxt.setText("Beneficiary Mobile");
                    popUpAddBeneficiary("Mobile Money");

                }else {
                    layoutMobileMoneyBeneficiaries.setVisibility(View.GONE);
                    layoutMobileNumber.setVisibility(View.GONE);
                    for(int i = 0; i<beneficiariesList.size();i++){
                        if(beneficiariesList.get(i).getAccount_name().equalsIgnoreCase(spBeneficiary.getSelectedItem().toString())){
                            beneficiary_id =  beneficiariesList.get(i).getId();
                            account_number =beneficiariesList.get(i).getAccount_number();
                            bankk =beneficiariesList.get(i).getBank();
                            branch =beneficiariesList.get(i).getBank_branch();
                            beneficiary_name=beneficiariesList.get(i).getAccount_name();
                            account_name=beneficiariesList.get(i).getAccount_name();

                            if(beneficiariesList.get(i).getTransaction_type().equalsIgnoreCase("mobile money")){
                                phoneNumber = beneficiariesList.get(i).getAccount_number();

                            }else{
                                phoneNumber = beneficiariesList.get(i).getBeneficiary_phone();
                            }


                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        if(this.action.equalsIgnoreCase(getString(R.string.settlements))){
            ArrayAdapter<String> adapter = new ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.settle_to));
            spTransferTo.setAdapter(adapter);

        }else{
            ArrayAdapter<String> adapter = new ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.transfer_to));
            spTransferTo.setAdapter(adapter);
        }
        spSelectBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(BankList!=null)
                    for (Bank bank: BankList) {

                            ((TextView) view).setTextColor(ContextCompat.getColor(context,R.color.white));
                            //((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);//Change selected text size



                       if(bank.getName().equalsIgnoreCase(spSelectBank.getSelectedItem().toString())){
                           selected_bank_code=bank.getCode();
                           getTransferBankBranches(bank.getId());
                       }
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spSelectBankBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    //Change selected text color
                    ((TextView) view).setTextColor(ContextCompat.getColor(context,R.color.white));
                    //((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);//Change selected text size

                if(bankBranches!=null)
                    for (BankBranch branch: bankBranches) {

                        if(branch.getBranchName().equalsIgnoreCase(spSelectBankBranch.getSelectedItem().toString())){
                            selected_branch_code=branch.getBranchCode();
                        }
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addMoneyImg.setOnClickListener(v -> {
            if(validateForm()){
            String amountEntered = etAmount.getText().toString();
            amount = Float.parseFloat(amountEntered);
            phoneNumber = getString(R.string.phone_number_code) + etMobileMoneyNumber.getText().toString();

            if(spTransferTo.getSelectedItem().toString().equalsIgnoreCase("Bank")){

                if(spBeneficiary.getSelectedItem().toString().equalsIgnoreCase("Add New")){
                    if(validateBankTransFerForm()) {
                        beneficiary_name = etBeneficiaryName.getText().toString();//required for Mobile Money
                        account_name = etAccountName.getText().toString();//required for Bank
                        account_number = etAccountNumber.getText().toString();//required for Bank
                        bankk = spSelectBank.getSelectedItem().toString();
                        branch = spSelectBankBranch.getSelectedItem().toString();
                        beneficiary_bank_phone_number = getString(R.string.phone_number_code) + etMobileMoneyNumber.getText().toString();

                        popUpAddBeneficiary("Mobile Money");
                    }


                }else{

                    Log.d(TAG, "initializeForm: "+beneficiary_name+"beneciary_number"+account_number);
                    navigateToComfirmDialog(null);


                }

            }
            else if(spTransferTo.getSelectedItem().toString().equalsIgnoreCase("emaisha account")){
                if( validateMaishaTransFerForm()) {

                    getReceiverName( phoneNumber);
                }

            }else if(spTransferTo.getSelectedItem().toString().equalsIgnoreCase("eMaisha Card")){

                WalletTransactionInitiation.getInstance().setMethodOfPayment("eMaisha Card");

            }
            else if(spTransferTo.getSelectedItem().toString().equalsIgnoreCase("Mobile Money")){

                bankk = "Mobile Money Bank";
                branch = "";
                beneficiary_bank_phone_number = getString(R.string.phone_number_code)+etMobileMoneyNumber.getText().toString();

                if(spBeneficiary.getSelectedItem().toString().equalsIgnoreCase("Add New")){
                    if( validateMobileMoneyTransFerForm()) {
                        beneficiary_name = etBeneficiaryName.getText().toString();
                        beneficiary_bank_phone_number = getString(R.string.phone_number_code) + etMobileMoneyNumber.getText().toString();
                        beneficiary_number = getString(R.string.phone_number_code) + etMobileMoneyNumber.getText().toString();
                        popUpAddBeneficiary("Mobile Money");
                    }

                }else{

                    navigateToComfirmDialog(null);

                }

            }else{
                Toast.makeText(context,"Select Transfer To",Toast.LENGTH_LONG).show();
            }
            }
        });


    }
    public void getReceiverName(String receiverPhoneNumber){
        /***************RETROFIT IMPLEMENTATION***********************/
        dialogLoader.showProgressDialog();
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());

        APIRequests apiRequests = APIClient.getWalletInstance(requireContext());
        Call<ConfirmationDataResponse> call = apiRequests.getUserBusinessName(access_token, receiverPhoneNumber, "CustomersTransfer", request_id, "getReceiverForUser", category);

        call.enqueue(new Callback<ConfirmationDataResponse>() {
            @Override
            public void onResponse(Call<ConfirmationDataResponse> call, Response<ConfirmationDataResponse> response) {
                dialogLoader.hideProgressDialog();
                if (response.code() == 200 && response.body().getStatus().equalsIgnoreCase("1")) {
                  navigateToComfirmDialog(response.body().getData().getBusinessName());

                } else if(response.body()!=null) {
                    Snackbar.make(context,addMoneyImg, response.body().getMessage(),Snackbar.LENGTH_LONG).show();
                }else if(response.code()==401){
                    Toast.makeText(context, "session expired", Toast.LENGTH_LONG).show();
                    TokenAuthFragment.startAuth( true);
                }

                if (response.errorBody() != null) {
                    Log.e("info", String.valueOf(response.errorBody()));
                } else {
                    Log.e("info", "Something got very very wrong");
                }

            }

            @Override
            public void onFailure(Call<ConfirmationDataResponse> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Log.e("info : ", t.getMessage());
                Log.e("info : ", "Something got very very wrong");

            }
        });

    }

    private void navigateToComfirmDialog(String businessName) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);


        // Create and show the dialog.
        DialogFragment transferPreviewDailog = new ConfirmTransfer(businessName);

        Bundle args = new Bundle();

        args.putString("methodOfPayment", spTransferTo.getSelectedItem().toString());
        args.putString("phoneNumber", phoneNumber);
        args.putDouble("amount", amount);

        args.putString("beneficiary_name", beneficiary_name);
        args.putString("account_name", account_name);
        args.putString("account_number", account_number);

        args.putString("bankCode", selected_bank_code);
        args.putString("bankBranch", selected_branch_code);
        args.putString("beneficiary_id", beneficiary_id);
        args.putString(" beneficiary_bank_phone_number",  beneficiary_bank_phone_number);

        transferPreviewDailog.setArguments(args);
        transferPreviewDailog.show(ft, "dialog");
    }


    private void popUpAddBeneficiary(String type) {
        //nvigate to add beneficiaries fragment
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev =fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment addBeneficiaryDialog =new AddBeneficiary(type);
        addBeneficiaryDialog.show( ft, "dialog");

    }

    private boolean validateMaishaTransFerForm() {
       if (!ValidateInputs.isValidPhoneNo(etMobileMoneyNumber.getText().toString().trim())) {
            etMobileMoneyNumber.setError(getString(R.string.invalid_phone));
            return false;
        } else if (Integer.parseInt(etAmount.getText().toString().trim())<0) {
            etAmount.setError(getString(R.string.invalid_number));
            return false;
        }  else {
            return true;
        }
    }

    private boolean validateForm() {
        if(spTransferTo.getSelectedItem().toString().equalsIgnoreCase("Select")){
            Toast.makeText(context,"Select transfer to",Toast.LENGTH_LONG).show();
            return false;

        }else if( spBeneficiary.getSelectedItem()!=null && spBeneficiary.getSelectedItem().toString().equalsIgnoreCase("Select")){
            Toast.makeText(context,"Select Beneficiary",Toast.LENGTH_LONG).show();
            return false;

        }else if(etAmount.getText().toString().isEmpty()){
            etAmount.setError("Required");
            return false;
        }else if( !spTransferTo.getSelectedItem().toString().equalsIgnoreCase("eMaisha Account") && spBeneficiary.getSelectedItem().toString().equalsIgnoreCase("Select")){
            Toast.makeText(context,"Select beneficiary",Toast.LENGTH_LONG).show();
            return false;

        }else{
            return true;
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void  loadTransferBanks(){

        ExternalAPIRequests apiRequests = RaveV2APIClient.getRavePayV2Instance();
        Call<BanksInfoResponse> call = apiRequests.getTransferBanks(getString(R.string.countryCode), BuildConfig.PUBLIC_KEY);

        dialogLoader.showProgressDialog();
        call.enqueue(new Callback<BanksInfoResponse>() {
            @Override
            public void onResponse(@NotNull Call<BanksInfoResponse> call, @NotNull Response<BanksInfoResponse> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        BanksInfoResponse.InfoData bankInfo = response.body().getData();
                        BankList=bankInfo.getBanks();
                         //Log.w("Banks_NumberFetched",BankList.length+" #############");
                        List<String> Banknames = new ArrayList<>();
                        Banknames.add("Select");
                        for (Bank bank: BankList) {
                            if( bank.getMobileVerified()==null && !bank.getName().equals("MTN") )
                            Banknames.add(bank.getName());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, Banknames);
                        spSelectBank.setAdapter(adapter);


                    } catch (Exception e) {
                        Log.e("response", response.toString());
                        e.printStackTrace();
                    } finally {
                        dialogLoader.hideProgressDialog();
                    }
                } else {
                    dialogLoader.hideProgressDialog();
                    //Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    String message = response.body().getMessage();
                    Snackbar.make(layoutMobileNumber, message, Snackbar.LENGTH_SHORT).show();
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

        dialogLoader.showProgressDialog();
        call.enqueue(new Callback<BankBranchInfoResponse>() {
            @Override
            public void onResponse(@NotNull Call<BankBranchInfoResponse> call, @NotNull Response<BankBranchInfoResponse> response) {
                if (response.code() == 200) {
                    try {
                        bankBranches = response.body().getData().getBankBranches();

                         //Log.w("Banks_NumberFetched",bankBranches.length+"****************");
                        List<String> BankBranchnames = new ArrayList<>();
                        BankBranchnames.add("Select");
                        for (BankBranch bank: bankBranches) {
                            BankBranchnames.add(bank.getBranchName());
                        }
                        BankBranchnames.add("Other");
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, BankBranchnames);
                        spSelectBankBranch.setAdapter(adapter);
                    } catch (Exception e) {
                        Log.e("response", response.toString());
                        e.printStackTrace();
                    } finally {
                        dialogLoader.hideProgressDialog();
                    }
                } else {
                    dialogLoader.hideProgressDialog();
                    //Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    String message = response.body().getMessage();
                    Snackbar.make(layoutMobileNumber, message, Snackbar.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<BankBranchInfoResponse> call, Throwable t) {
                Log.e("info2 : ", t.getMessage());
                dialogLoader.hideProgressDialog();
            }
        });

    }

    public void requestFilteredBeneficiaries(){
        dialogLoader.showProgressDialog();
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();


        Call<BeneficiaryResponse> call = APIClient.getWalletInstance(context).getBeneficiaries(
                access_token,
                spTransferTo.getSelectedItem().toString(),
                request_id,
                "getBeneficiaries");

        call.enqueue(new Callback<BeneficiaryResponse>() {
            @Override
            public void onResponse(Call<BeneficiaryResponse> call, Response<BeneficiaryResponse> response) {
                if(response.isSuccessful()){
                    dialogLoader.hideProgressDialog();
                    if(response.body().getStatus().equalsIgnoreCase("1")) {
                      final   ArrayList<String> beneficiaries = new ArrayList<>();
                        try {

                            beneficiariesList = response.body().getBeneficiariesList();
                            beneficiaries.add("Select");

                            Log.d(TAG, beneficiariesList.size() + "**********");
                            for (int i = 0; i < beneficiariesList.size(); i++) {

                                //decript

                                beneficiaries.add(beneficiariesList.get(i).getAccount_name());

                            }


                            beneficiaries.add("Add New");


                            //set list in beneficiary spinner

                            ArrayAdapter<String> beneficiariesAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, beneficiaries);

                            spBeneficiary.setAdapter(beneficiariesAdapter);


                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }else {

                        Toast.makeText(context,response.body().getMessage(),Toast.LENGTH_LONG).show();
                        dialogLoader.hideProgressDialog();
                    }
                  
                }else if (response.code() == 401) {

                    TokenAuthFragment.startAuth( true);

                    if (response.errorBody() != null) {
                        Log.e("info", String.valueOf(response.errorBody()));
                    } else {
                        Log.e("info", "Something got very very wrong");
                    }
                }

            }

            @Override
            public void onFailure(Call<BeneficiaryResponse> call, @NotNull Throwable t){
                dialogLoader.hideProgressDialog();
            }
        });



    }

    private boolean validateBankTransFerForm() {
        if (!ValidateInputs.isValidName(etAccountName.getText().toString().trim())) {
            etAccountName.setError(getString(R.string.invalid_name));
            return false;
        } else if (!ValidateInputs.isValidAccountNo(etAccountNumber.getText().toString().trim())) {
            etAccountNumber.setError(getString(R.string.invalid_account_number));
            return false;
        } else if (Integer.parseInt(etAmount.getText().toString().trim())<0) {
            etAmount.setError(getString(R.string.invalid_number));
            return false;
        }  else {
            return true;
        }
    }

    private boolean validateMobileMoneyTransFerForm() {
        if (!ValidateInputs.isValidName(etBeneficiaryName.getText().toString().trim())) {
            etAccountName.setError(getString(R.string.invalid_name));
            return false;
        } else if (!ValidateInputs.isValidPhoneNo(etMobileMoneyNumber.getText().toString().trim())) {
            etMobileMoneyNumber.setError(getString(R.string.invalid_phone));
            return false;
        } else if (Integer.parseInt(etAmount.getText().toString().trim())<0) {
            etAmount.setError(getString(R.string.invalid_number));
            return false;
        }  else {
            return true;
        }
    }
}
