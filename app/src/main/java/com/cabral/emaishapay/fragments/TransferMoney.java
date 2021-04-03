package com.cabral.emaishapay.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.DailogFragments.ConfirmTransfer;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.BeneficiariesListAdapter;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.models.BeneficiaryResponse;
import com.cabral.emaishapay.models.CropSpinnerItem;
import com.cabral.emaishapay.models.WalletTransactionInitiation;
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
import com.cabral.emaishapay.utils.CryptoUtil;
import com.cabral.emaishapay.utils.ValidateInputs;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.internal.bind.ArrayTypeAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferMoney extends Fragment {
    private static final String TAG = "TransferMoney";
    LinearLayout layoutMobileNumber, layoutEmaishaCard,layoutBank,layout_beneficiary_name,layoutBeneficiary;
    Button addMoneyImg;
    TextView mobile_numberTxt, addMoneyTxt,transferTotxt;
    Spinner spTransferTo, spSelectBank,spSelectBankBranch,spBeneficiary;
    EditText cardNumberTxt,  cardexpiryTxt,  cardccvTxt, cardHolderNameTxt, etAccountName, etAccountNumber,etAmount;

    FragmentManager fm;
    EditText etMobileMoneyNumber;
    AutoCompleteTextView etBeneficiaryName;
    private Context context;
    private Toolbar toolbar;
    DialogLoader dialogLoader;
    Bank[] BankList; BankBranch[] bankBranches;
    String selected_bank_code,selected_branch_code;
    String action, beneficiary_name;
    private List<BeneficiaryResponse.Beneficiaries> beneficiariesList = new ArrayList();
    ArrayList<String> beneficiaries = new ArrayList<>();

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
        layoutBeneficiary=view.findViewById(R.id.layout_beneficiary);
        spBeneficiary = view.findViewById(R.id.sp_beneficiary);

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
                    layoutBeneficiary.setVisibility(View.GONE);
                }
                else if(position==1){
                    layoutMobileNumber.setVisibility(View.VISIBLE);
                    layoutEmaishaCard.setVisibility(View.GONE);
                    layoutBank.setVisibility(View.GONE);
                    layout_beneficiary_name.setVisibility(View.GONE);
                    layoutBeneficiary.setVisibility(View.GONE);

                }
                else if(position==2){
                    layoutMobileNumber.setVisibility(View.GONE);
                    layoutEmaishaCard.setVisibility(View.VISIBLE);
                    layoutBank.setVisibility(View.GONE);
                    layoutBeneficiary.setVisibility(View.GONE);
                }
                else if(position==3){
                    layoutMobileNumber.setVisibility(View.GONE);
                    layout_beneficiary_name.setVisibility(View.GONE);
                    layoutEmaishaCard.setVisibility(View.GONE);
                    layoutBank.setVisibility(View.GONE);
                    layoutBeneficiary.setVisibility(View.VISIBLE);
                    requestFilteredBeneficiaries();
                }
                else if(position==4){
                   // loadTransferBanks();
                    layoutMobileNumber.setVisibility(View.GONE);
                    layoutEmaishaCard.setVisibility(View.GONE);
                    layoutBank.setVisibility(View.GONE);
                    layoutBeneficiary.setVisibility(View.VISIBLE);
                    requestFilteredBeneficiaries();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        if(this.action.equalsIgnoreCase(getString(R.string.settlements))){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.settle_to));
            spTransferTo.setAdapter(adapter);

        }else{
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.transfer_to));
            spTransferTo.setAdapter(adapter);
        }
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
           // double balance = Double.parseDouble(WalletHomeActivity.getPreferences(String.valueOf(WalletHomeActivity.PREFERENCE_WALLET_BALANCE),context));

            String phoneNumber = "0"+etMobileMoneyNumber.getText().toString();
            String amountEntered = etAmount.getText().toString();
            float amount = Float.parseFloat(amountEntered);
            String beneficiary_name =etBeneficiaryName.getText().toString();//required for Mobile Money
            String account_name = etAccountName.getText().toString();//required for Bank
            String account_number = etAccountNumber.getText().toString();//required for Bank

            if(spTransferTo.getSelectedItem().toString().equalsIgnoreCase("Bank") && !spSelectBank.getSelectedItem().toString().equalsIgnoreCase("Select") && validateBankTransFerForm() && selected_bank_code!=null){

            }
            else if(spTransferTo.getSelectedItem().toString().equalsIgnoreCase("eMaisha Account") &&  validateMobileMoneyTransFerForm()){
            }else if(spTransferTo.getSelectedItem().toString().equalsIgnoreCase("eMaisha Card")){
                WalletTransactionInitiation.getInstance().setMethodOfPayment("eMaisha Card");
            }
            else if(spTransferTo.getSelectedItem().toString().equalsIgnoreCase("Mobile Money") &&  validateMobileMoneyTransFerForm()){


            }else{
                Toast.makeText(context,"Select Transfer To",Toast.LENGTH_LONG).show();
            }

           // if (balance >= amount ) {
                FragmentTransaction ft = fm.beginTransaction();
                Fragment prev = fm.findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);


                // Create and show the dialog.
                DialogFragment transferPreviewDailog = new ConfirmTransfer(context);

                Bundle args=new Bundle();
                args.putString("methodOfPayment",spTransferTo.getSelectedItem().toString());
                args.putString("phoneNumber",phoneNumber);
                args.putDouble("amount",amount);

                args.putString("beneficiary_name",beneficiary_name);
                args.putString("account_name",account_name);
                args.putString("account_number",account_number);

                args.putString("bankCode",selected_bank_code);
                args.putString("bankBranch",selected_branch_code);

                transferPreviewDailog.setArguments(args);
                transferPreviewDailog.show(ft, "dialog");
//            } else {
//                Toast.makeText(getActivity(), "Insufficient Account balance!", Toast.LENGTH_LONG).show();
//                Log.e("Error", "Insufficient Account balance!");
//            }


        });

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

    public void requestFilteredBeneficiaries(){
        String user_id = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
        String access_token = TokenAuthFragment.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        /******************RETROFIT IMPLEMENTATION***********************/
        Call<BeneficiaryResponse> call = APIClient.getWalletInstance(getContext()).getBeneficiaries(access_token,spTransferTo.getSelectedItem().toString(),request_id);
        call.enqueue(new Callback<BeneficiaryResponse>() {
            @Override
            public void onResponse(Call<BeneficiaryResponse> call, Response<BeneficiaryResponse> response) {
                if(response.isSuccessful()){

                    try {

                        beneficiariesList = response.body().getBeneficiariesList();



                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        Log.d(TAG,beneficiariesList.size()+"**********");
                        for(int i=0;i<beneficiariesList.size();i++) {

                            //decript
                            CryptoUtil encrypter = new CryptoUtil(BuildConfig.ENCRYPTION_KEY, context.getString(R.string.iv));
                            beneficiary_name = encrypter.decrypt(beneficiariesList.get(i).getAccount_name());
                            beneficiaries.add(beneficiary_name);

                        }

                        //set list in beneficiary spinner
                        ArrayAdapter<String> beneficiariesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, beneficiaries);
                        spBeneficiary.setAdapter(beneficiariesAdapter);



                    }
                  
                }else if (response.code() == 401) {

                    TokenAuthFragment.startAuth( true);

                    if (response.errorBody() != null) {
                        Log.e("info", new String(String.valueOf(response.errorBody())));
                    } else {
                        Log.e("info", "Something got very very wrong");
                    }
                }

            }

            @Override
            public void onFailure(Call<BeneficiaryResponse> call, Throwable t){
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
