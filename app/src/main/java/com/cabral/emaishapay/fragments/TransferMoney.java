package com.cabral.emaishapay.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.models.InitiateTransferResponse;
import com.cabral.emaishapay.models.external_transfer_model.Bank;
import com.cabral.emaishapay.models.external_transfer_model.BankBranch;
import com.cabral.emaishapay.models.external_transfer_model.BankBranchInfoResponse;
import com.cabral.emaishapay.models.external_transfer_model.BankTransferResponse;
import com.cabral.emaishapay.models.external_transfer_model.BanksInfoResponse;
import com.cabral.emaishapay.models.external_transfer_model.SettlementResponse;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.cabral.emaishapay.network.ExternalAPIRequests;
import com.cabral.emaishapay.network.FlutterwaveV3APIClient;
import com.cabral.emaishapay.network.RaveV2APIClient;
import com.cabral.emaishapay.utils.ValidateInputs;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferMoney extends Fragment {
    LinearLayout layoutMobileNumber, layoutEmaishaCard,layoutBank,layout_beneficiary_name;
    Button addMoneyImg;
    TextView mobile_numberTxt, addMoneyTxt;
    Spinner spTransferTo, spSelectBank,spSelectBankBranch;
    EditText cardNumberTxt,  cardexpiryTxt,  cardccvTxt, cardHolderNameTxt, etAccountName, etAccountNumber,etAmount;
    private double balance;
    FragmentManager fm;
    EditText etMobileMoneyNumber,etBeneficiaryName;
    private Context context;
    AppBarConfiguration appBarConfiguration;
    private Toolbar toolbar;
    DialogLoader dialogLoader;
    Bank[] BankList; BankBranch[] bankBranches;
    String selected_bank_code,selected_branch_code;
    NavController navController;
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

        initializeForm(view);
        return view;
    }

    public void initializeForm(View view) {
        toolbar = view.findViewById(R.id.toolbar_wallet_add_money);
        etMobileMoneyNumber = view.findViewById(R.id.money_mobile_no);
        addMoneyImg = view.findViewById(R.id.button_add_money);
        addMoneyTxt = view.findViewById(R.id.crop_add_money_amount);
        mobile_numberTxt = view.findViewById(R.id.text_mobile_number);
        spTransferTo = view.findViewById(R.id.sp_transfer_to);
        spSelectBank = view.findViewById(R.id.sp_bank);
        spSelectBankBranch = view.findViewById(R.id.sp_bank_branch);
        cardNumberTxt=view.findViewById(R.id.add_money_creditCardNumber);
        cardHolderNameTxt=view.findViewById(R.id.add_money_holder_name);
        cardexpiryTxt=view.findViewById(R.id.add_money_card_expiry);
        cardccvTxt=view.findViewById(R.id.add_money_card_cvv);
        etAccountName=view.findViewById(R.id.et_account_name);
        etAccountNumber=view.findViewById(R.id.et_account_number);
        etAmount=view.findViewById(R.id.money_amount);
        etBeneficiaryName=view.findViewById(R.id.et_beneficiary_name);
        layoutMobileNumber=view.findViewById(R.id.layout_mobile_number);
        layoutEmaishaCard=view.findViewById(R.id.layout_emaisha_card);
        layoutBank=view.findViewById(R.id.layout_bank);
        layout_beneficiary_name=view.findViewById(R.id.layout_beneficiary_name);

        this.fm=getActivity().getSupportFragmentManager();

            TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (filterLongEnough() && !cardexpiryTxt.getText().toString().contains("/")) {
                    cardexpiryTxt.setText(cardexpiryTxt.getText().toString()+"/");
                    int pos = cardexpiryTxt.getText().length();
                    cardexpiryTxt.setSelection(pos);
                }
            }

            private boolean filterLongEnough() {
                return cardexpiryTxt.getText().toString().length() == 2;
            }
        };
        cardexpiryTxt.addTextChangedListener(fieldValidatorTextWatcher);


        spTransferTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //Change selected text color
                    ((TextView) view).setTextColor(getResources().getColor(R.color.textColor));
                } catch (Exception e) {

                }
                if(position==0){
                    layoutMobileNumber.setVisibility(View.GONE);
                    layoutEmaishaCard.setVisibility(View.GONE);
                    layoutBank.setVisibility(View.GONE);
                    layout_beneficiary_name.setVisibility(View.GONE);
                }
                else if(position==1){
                    layoutMobileNumber.setVisibility(View.VISIBLE);
                    layoutEmaishaCard.setVisibility(View.GONE);
                    layoutBank.setVisibility(View.GONE);
                    layout_beneficiary_name.setVisibility(View.GONE);
                }
                else if(position==2){
                    layoutMobileNumber.setVisibility(View.GONE);
                    layoutEmaishaCard.setVisibility(View.VISIBLE);
                    layoutBank.setVisibility(View.GONE);
                }
                else if(position==3){
                    layoutMobileNumber.setVisibility(View.VISIBLE);
                    layout_beneficiary_name.setVisibility(View.VISIBLE);
                    layoutEmaishaCard.setVisibility(View.GONE);
                    layoutBank.setVisibility(View.GONE);
                }
                else if(position==4){
                    loadTransferBanks();
                    layoutMobileNumber.setVisibility(View.GONE);
                    layoutEmaishaCard.setVisibility(View.GONE);
                    layoutBank.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spSelectBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(BankList!=null)
                    for (Bank bank: BankList) {
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
                if(BankList!=null)
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
            double balance = Double.parseDouble(WalletHomeActivity.getPreferences(String.valueOf(WalletHomeActivity.PREFERENCE_WALLET_BALANCE),context));
            //fetch Transfer Charges

            if(spTransferTo.getSelectedItem().toString().equalsIgnoreCase("Bank")){
                queueBankTransfer();
            }
            else if(spTransferTo.getSelectedItem().toString().equalsIgnoreCase("eMaisha Account")){
                transferToInternalWallet();
            }else if(spTransferTo.getSelectedItem().toString().equalsIgnoreCase("eMaisha Card")){

            }
            else if(spTransferTo.getSelectedItem().toString().equalsIgnoreCase("Mobile Money")){
                mobileMoneyTransfer();
            }else{
                Toast.makeText(context,"Select Transfer To",Toast.LENGTH_LONG).show();
            }


        });

    }

    private void transferToInternalWallet() {
        String phoneNumber = "0"+etMobileMoneyNumber.getText().toString();
        String amountEntered = etAmount.getText().toString();
        float amount = Float.parseFloat(amountEntered);
        float charges = (float) 100; //Transfer Charges

        if (balance >= (amount + charges)) {
            FragmentTransaction ft = fm.beginTransaction();
            Fragment prev = fm.findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            // Create and show the dialog.
            DialogFragment transferPreviewDailog = new com.cabral.emaishapay.DailogFragments.ConfirmTransfer(context, phoneNumber, amount);
            transferPreviewDailog.show(ft, "dialog");
        } else {
            Toast.makeText(getActivity(), "Insufficient Account balance!", Toast.LENGTH_LONG).show();
            Log.e("Error", "Insufficient Account balance!");
        }
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        if(getArguments()!=null) {
            this.balance = getArguments().getDouble("balance");
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
                        BanksInfoResponse.InfoData bankInfo = response.body().getData();
                        BankList=bankInfo.getBanks();
                        Log.w("Banks_NumberFetched",BankList.length+" #############");
                        List<String> Banknames = new ArrayList<>();
                        Banknames.add("Select");
                        for (Bank bank: BankList) {
                            if( bank.getMobileVerified()==null && !bank.getName().equals("MTN") && !bank.getName().equals("Bank of Uganda"))
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
                    Snackbar.make(layoutBank, message, Snackbar.LENGTH_SHORT).show();
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

                        Log.w("Banks_NumberFetched",bankBranches.length+"****************");
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
                    Snackbar.make(layoutBank, message, Snackbar.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<BankBranchInfoResponse> call, Throwable t) {
                Log.e("info2 : ", t.getMessage());
                dialogLoader.hideProgressDialog();
            }
        });

    }

    private void queueBankTransfer(){

        String account_name = etAccountName.getText().toString();
        String transfer_amount = etAmount.getText().toString();
        String account_number = etAccountNumber.getText().toString();
        String currency=getString(R.string.currency);
        String narration="eMaisha Pay Bank Transfer outs";
        String reference= "TrO"+UUID.randomUUID().toString();

        if( !spSelectBank.getSelectedItem().toString().equalsIgnoreCase("Select") && validateBankTransFerForm() && selected_bank_code!=null ){

            ExternalAPIRequests apiRequests = FlutterwaveV3APIClient.getFlutterwaveV3Instance();
            Call<BankTransferResponse> call = apiRequests.bankTransferOuts( BuildConfig.SECRET_KEY,
                    reference,
                    account_name,
                    currency,
                    narration,
                    transfer_amount,
                    account_number,
                    selected_branch_code,
                    selected_bank_code);

            dialogLoader.showProgressDialog();
            call.enqueue(new Callback<BankTransferResponse>() {
                @Override
                public void onResponse(@NotNull Call<BankTransferResponse> call, @NotNull Response<BankTransferResponse> response) {
                    if (response.code() == 200 && response.body().getMessage().equals("Transfer Queued Successfully")) {
                        try {
                            com.cabral.emaishapay.models.external_transfer_model.BankTransferResponse.InfoData TransferResponse =
                                    response.body().getData();
                            recordTransferSettlement("pending","Bank",TransferResponse,dialogLoader);

                        } catch (Exception e) {
                            Log.e("response", response.toString());
                            e.printStackTrace();
                        }

                    } else {
                        dialogLoader.hideProgressDialog();
                        //Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        String message = response.body().getMessage();
                        Snackbar.make(layoutBank, message, Snackbar.LENGTH_SHORT).show();
                    }


                }

                @Override
                public void onFailure(Call<BankTransferResponse> call, Throwable t) {
                    Log.e("info2 : ", t.getMessage());
                    dialogLoader.hideProgressDialog();
                }
            });

        }
    }

    private void mobileMoneyTransfer() {
        String account_bank = "MPS";
        String beneficiary_name =etBeneficiaryName.getText().toString();
        String transfer_amount = etAmount.getText().toString();
        String account_number = "0"+etMobileMoneyNumber.getText().toString();
        String currency=getString(R.string.currency);
        String narration="eMaisha Pay Mobile Money Transfer outs";
        String reference= UUID.randomUUID().toString();

        if(validateMobileMoneyTransFerForm()){
            ExternalAPIRequests apiRequests = FlutterwaveV3APIClient.getFlutterwaveV3Instance();
            Call<BankTransferResponse> call = apiRequests.bankTransferOuts( BuildConfig.SECRET_KEY,
                    reference,
                    beneficiary_name,
                    currency,
                    narration,
                    transfer_amount,
                    account_number,
                    null,
                    account_bank);

            dialogLoader.showProgressDialog();
            call.enqueue(new Callback<BankTransferResponse>() {
                @Override
                public void onResponse(@NotNull Call<BankTransferResponse> call, @NotNull Response<BankTransferResponse> response) {
                    if (response.code() == 200 && response.body().getMessage().equals("Transfer Queued Successfully")) {
                        try {
                            com.cabral.emaishapay.models.external_transfer_model.BankTransferResponse.InfoData TransferResponse =
                                    response.body().getData();
                            recordTransferSettlement("completed","Mobile Money",TransferResponse,dialogLoader);

                        } catch (Exception e) {
                            Log.e("response", response.toString());
                            e.printStackTrace();
                        }

                    } else {
                        dialogLoader.hideProgressDialog();
                        //Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        String message = response.body().getMessage();
                        Snackbar.make(layoutBank, message, Snackbar.LENGTH_SHORT).show();
                    }


                }

                @Override
                public void onFailure(Call<BankTransferResponse> call, Throwable t) {
                    Log.e("info2 : ", t.getMessage());
                    dialogLoader.hideProgressDialog();
                }
            });
        }

    }

    private void recordTransferSettlement(String third_party_status,String destination_type, BankTransferResponse.InfoData transferResponse, DialogLoader dialogLoader) {
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        Double amount =Double.parseDouble(transferResponse.getAmount());
        String thirdparty="flutterwave";
        Double third_party_fee =Double.parseDouble(transferResponse.getFee());
        String destination_account_no=transferResponse.getAccount_number();
        String beneficiary_name=transferResponse.getFull_name();
        String destination_name=transferResponse.getBank_name();
        String reference=transferResponse.getReference();
        String third_party_id=transferResponse.getId();

        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<SettlementResponse> call = apiRequests.recordSettlementTransfer(
                access_token,
                amount,
                thirdparty,
                third_party_fee,
                destination_type,
                destination_account_no,
                beneficiary_name,
                destination_name,
                reference,
                third_party_status,
                third_party_id);

        call.enqueue(new Callback<SettlementResponse>() {
            @Override
            public void onResponse(Call<SettlementResponse> call, Response<SettlementResponse> response) {
                if (response.code() == 200 && response.body().getStatus().equals("1")) {
                    dialogLoader.hideProgressDialog();
                    navController.navigate(R.id.action_transferMoney_to_walletHomeFragment);
                } else {
                    dialogLoader.hideProgressDialog();
                    //Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    String message = response.body().getMessage();
                    Snackbar.make(layoutBank, message, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SettlementResponse> call, Throwable t) {
                Log.e("info : " , new String(String.valueOf(t.getMessage())));
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
