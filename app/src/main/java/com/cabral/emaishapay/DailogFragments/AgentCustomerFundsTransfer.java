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
import androidx.fragment.app.FragmentTransaction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.models.MerchantInfoResponse;
import com.cabral.emaishapay.models.external_transfer_model.Bank;
import com.cabral.emaishapay.models.external_transfer_model.BankBranch;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.flutterwave.raveutils.verification.RaveVerificationUtils;

import java.util.ArrayList;

public class AgentCustomerFundsTransfer extends DialogFragment {
    LinearLayout layoutMobileNumber, layoutEmaishaCard,layoutBank,layout_beneficiary_nam,walletLayout;
    Button addMoney;
    Spinner spTransferTo, spSelectBank,spSelectBankBranch;
    String key;
    EditText receipentNo,customerNo,amount;
    String business_name = "";

    public AgentCustomerFundsTransfer(String key) {
        this.key = key;


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
        View view = inflater.inflate(R.layout.dialog_agent_customer_funds_transfer, null);
        layoutMobileNumber=view.findViewById(R.id.layout_mobile_number);
        walletLayout = view.findViewById(R.id.layout_wallet);
        layoutEmaishaCard=view.findViewById(R.id.layout_emaisha_card);
        layoutBank=view.findViewById(R.id.layout_bank);
        spTransferTo = view.findViewById(R.id.sp_transfer_to);
        spSelectBank = view.findViewById(R.id.sp_bank);
        spSelectBankBranch = view.findViewById(R.id.sp_bank_branch);
        addMoney =view.findViewById(R.id.button_add_money);
        receipentNo = view.findViewById(R.id.mobile_money_recipients_no);
        customerNo = view.findViewById(R.id.customer_no);
        amount = view.findViewById(R.id.money_amount);

        if(key.equalsIgnoreCase("Customer Fund Transfer")){
            String[] transfer_to = {"Select","Wallet","eMaisha Card"};
            ArrayAdapter transfers = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, transfer_to);
            spTransferTo.setAdapter(transfers);
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
                        walletLayout.setVisibility(View.GONE);

                    }
                    else if(position==1){
                        walletLayout.setVisibility(View.VISIBLE);
                        layoutMobileNumber.setVisibility(View.VISIBLE);
                        layoutEmaishaCard.setVisibility(View.GONE);
                        layoutBank.setVisibility(View.GONE);
                    }
                    else if(position==2){
                        layoutMobileNumber.setVisibility(View.GONE);
                        layoutEmaishaCard.setVisibility(View.VISIBLE);
                        layoutBank.setVisibility(View.GONE);
                        walletLayout.setVisibility(View.GONE);
                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            addMoney.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //call customer details dialog
                    if(spTransferTo.getSelectedItem().toString().equalsIgnoreCase("wallet")) {
                        getReceiverName("0" + receipentNo.getText().toString());
                    }
                    FragmentManager fragmentManager = getChildFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    Fragment prev = fragmentManager.findFragmentByTag("dialog");
                    Bundle bundle = new Bundle();
                    bundle.putString("key","transfer");
                    bundle.putString("title","Confirm Transfer Details");
                    bundle.putString("receipient_no",receipentNo.getText().toString());
                    bundle.putString("customer_no",customerNo.getText().toString());
                    bundle.putString("amount",amount.getText().toString());
                    bundle.putString("customer_name",business_name);
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);

                    // Create and show the dialog.
                    DialogFragment depositDialog = new AgentCustomerConfirmDetails();
                    depositDialog.setArguments(bundle);
                    depositDialog.show(ft, "dialog");
                }
            });



        }else {


            spTransferTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        //Change selected text color
                        ((TextView) view).setTextColor(getResources().getColor(R.color.textColor));
                    } catch (Exception e) {

                    }
                    if (position == 0) {
                        walletLayout.setVisibility(View.GONE);
                        layoutMobileNumber.setVisibility(View.GONE);
                        layoutEmaishaCard.setVisibility(View.GONE);
                        layoutBank.setVisibility(View.GONE);

                    } else if (position == 1) {
                        layoutMobileNumber.setVisibility(View.VISIBLE);
                        layoutEmaishaCard.setVisibility(View.GONE);
                        layoutBank.setVisibility(View.GONE);
                        walletLayout.setVisibility(View.GONE);
                    } else if (position == 2) {
                        layoutMobileNumber.setVisibility(View.GONE);
                        layoutEmaishaCard.setVisibility(View.VISIBLE);
                        layoutBank.setVisibility(View.GONE);
                    } else if (position == 3) {
                        layoutMobileNumber.setVisibility(View.VISIBLE);
                        layoutEmaishaCard.setVisibility(View.GONE);
                        layoutBank.setVisibility(View.GONE);
                        walletLayout.setVisibility(View.GONE);
                    } else if (position == 4) {
                        layoutMobileNumber.setVisibility(View.GONE);
                        layoutEmaishaCard.setVisibility(View.GONE);
                        layoutBank.setVisibility(View.VISIBLE);
                        walletLayout.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });




        }

        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);



        ImageView close = view.findViewById(R.id.agent_deposit_money_close);
        close.setOnClickListener(v -> dismiss());

        return dialog;

    }
    public void getReceiverName(String receiverPhoneNumber){

        /***************RETROFIT IMPLEMENTATION***********************/

        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;

        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<MerchantInfoResponse> call = apiRequests.getUserBusinessName(access_token,receiverPhoneNumber);
        call.enqueue(new Callback<MerchantInfoResponse>() {
            @Override
            public void onResponse(Call<MerchantInfoResponse> call, Response<MerchantInfoResponse> response) {
                if(response.isSuccessful()){
                    business_name = response.body().getData().getBusinessName();

                }else if(response.code()==412) {
                    business_name = response.body().getMessage();
                    // confirmBtn.setEnabled(true);
                }
                else if(response.code()==401){
                    TokenAuthActivity.startAuth(getContext(), true);
                }

            }

            @Override
            public void onFailure(Call<MerchantInfoResponse> call, Throwable t) {

                Log.e("info : ", t.getMessage());
                Log.e("info : ", "Something got very very wrong");



            }
        });


    }
}