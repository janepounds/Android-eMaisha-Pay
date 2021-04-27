package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
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

import android.text.TextUtils;
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

import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.fragments.wallet_fragments.TokenAuthFragment;
import com.cabral.emaishapay.models.ConfirmationDataResponse;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;

public class AgentCustomerFundsTransfer extends DialogFragment {
    LinearLayout layoutMobileNumber, layoutEmaishaCard,layoutBank,layout_beneficiary_nam,walletLayout;
    Button addMoney;
    Spinner spTransferTo, spSelectBank,spSelectBankBranch;
    String key;
    EditText receipentNo,customerNo,amountEdt;
    TextView customerNoTitle;
    String business_name = "";
    DialogLoader dialogLoader;

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
        dialogLoader = new DialogLoader(getContext());
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
        customerNoTitle = view.findViewById(R.id.text_customer_number);
        amountEdt = view.findViewById(R.id.money_amount);
        customerNoTitle.setText("Sender's Number");

        if(key.equalsIgnoreCase("Customer Fund Transfer")){

//            String[] transfer_to = {"Select","Wallet","eMaisha Card"};
//            ArrayAdapter transfers = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, transfer_to);
//            spTransferTo.setAdapter(transfers);
//            spTransferTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    try {
//                        //Change selected text color
//                        ((TextView) view).setTextColor(getResources().getColor(R.color.textColor));
//                    } catch (Exception e) {
//
//                    }
//                    if(position==0){
//                        layoutMobileNumber.setVisibility(View.GONE);
//                        layoutEmaishaCard.setVisibility(View.GONE);
//                        layoutBank.setVisibility(View.GONE);
//                        walletLayout.setVisibility(View.GONE);
//                    }
//                    else if(position==1){
//                        walletLayout.setVisibility(View.VISIBLE);
//                        layoutMobileNumber.setVisibility(View.VISIBLE);
//                        layoutEmaishaCard.setVisibility(View.GONE);
//                        layoutBank.setVisibility(View.GONE);
//                    }
//                    else if(position==2){
//                        layoutMobileNumber.setVisibility(View.GONE);
//                        layoutEmaishaCard.setVisibility(View.VISIBLE);
//                        layoutBank.setVisibility(View.GONE);
//                        walletLayout.setVisibility(View.GONE);
//                    }
//
//
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
            walletLayout.setVisibility(View.VISIBLE);
            layoutMobileNumber.setVisibility(View.VISIBLE);
            layoutEmaishaCard.setVisibility(View.GONE);
            layoutBank.setVisibility(View.GONE);

            addMoney.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //call customer details dialog
                    if( TextUtils.isEmpty(receipentNo.getText()) ) {
                        receipentNo.setError("Receiver Number required");
                        return;
                    }else if( TextUtils.isEmpty(customerNo.getText()) ) {
                        customerNo.setError("Customer Number required");
                        return;
                    }else if( customerNo.getText().length()!=9 ) {
                        customerNo.setError("Customer Number invalid");
                        return;
                    }else if( receipentNo.getText().length()!=9  ) {
                        receipentNo.setError("Receiver Number invalid");
                        return;
                    }
                    else{
                        getReceiverName(
                                getString(R.string.phone_number_code)+ receipentNo.getText().toString(),
                                getString(R.string.phone_number_code) + customerNo.getText().toString()
                        );
                    }

                }
            });



        }
        else {
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
    public void getReceiverName(String receiverPhoneNumber, String customerPhoneNumber){

        /***************RETROFIT IMPLEMENTATION***********************/
        dialogLoader.showProgressDialog();
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        double amount = Double.parseDouble(amountEdt.getText().toString());

        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<ConfirmationDataResponse> call = apiRequests.validateAgentFundsTransfer(access_token, customerPhoneNumber, receiverPhoneNumber,amount,request_id,category,"fundsTransferMetadata");
        call.enqueue(new Callback<ConfirmationDataResponse>() {
            @Override
            public void onResponse(Call<ConfirmationDataResponse> call, Response<ConfirmationDataResponse> response) {
                if(response.isSuccessful()){
                    if(response.body().getStatus().equalsIgnoreCase("1")){
                        String receiverBusinessName = response.body().getData().getReceiverBusinessName();
                        String sendBusinessName = response.body().getData().getSenderBusinessName();
                        FragmentManager fragmentManager = getChildFragmentManager();
                        FragmentTransaction ft = fragmentManager.beginTransaction();
                        Fragment prev = fragmentManager.findFragmentByTag("dialog");
                        Bundle bundle = new Bundle();
                        bundle.putString("key","transfer");
                        bundle.putString("title","Confirm Transfer Details");
                        bundle.putString("receipient_no",receipentNo.getText().toString());
                        bundle.putString("customer_no",customerNo.getText().toString());
                        bundle.putString("amount",amount+"");
                        bundle.putString("customer_name",sendBusinessName);
                        bundle.putString("receiver_name",receiverBusinessName);
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);

                        // Create and show the dialog.
                        DialogFragment depositDialog = new AgentCustomerConfirmDetails();
                        depositDialog.setArguments(bundle);
                        depositDialog.show(ft, "dialog");
                    }else{
                        Toast.makeText(getContext(),response.body().getData().getMessage(),Toast.LENGTH_LONG).show();
                    }

                    dialogLoader.hideProgressDialog();

                }else if(response.code()==412) {
                    Log.e("Error",response.errorBody()+"");
                    //redirect to previous step
                    getActivity().getSupportFragmentManager().popBackStack();
                    dialogLoader.hideProgressDialog();
                }
                else if(response.code()==401){
                    dialogLoader.hideProgressDialog();
                    TokenAuthFragment.startAuth( true);

                }

            }

            @Override
            public void onFailure(Call<ConfirmationDataResponse> call, Throwable t) {
                Log.e("info : ", t.getMessage());
                Log.e("info : ", "Something got very very wrong");

                dialogLoader.hideProgressDialog();

            }
        });


    }
}