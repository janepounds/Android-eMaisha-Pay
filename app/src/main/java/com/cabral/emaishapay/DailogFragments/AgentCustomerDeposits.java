package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.models.MerchantInfoResponse;
import com.cabral.emaishapay.models.WalletTransaction;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.ugmobilemoney.UgandaMobileMoneyPaymentCallback;
import com.flutterwave.raveandroid.rave_presentation.ugmobilemoney.UgandaMobileMoneyPaymentManager;
import com.flutterwave.raveutils.verification.RaveVerificationUtils;

import java.text.NumberFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgentCustomerDeposits extends DialogFragment {
    LinearLayout layoutMobileNumber, layoutWalletNumber,layoutAccountNumber;
    Spinner spDepositTo;
    Button addMoneyBtn;
    TextView addMoneyTxt, phoneNumberTxt, errorMsgTxt;
    static String PENDING_DEPOSIT_REFERENCE_NUMBER;
    TextView balanceTextView;
    double balance;
    private String txRef, business_name= "";
    ProgressDialog dialog;
    Context activity;
    private RaveVerificationUtils verificationUtils;
    FragmentManager fm;
    private EditText walletNumber,depositAmount,phoneNumber;

    public AgentCustomerDeposits() {


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
        View view = inflater.inflate(R.layout.dialog_agent_customer_deposit, null);

        spDepositTo = view.findViewById(R.id.sp_deposit_to);
        layoutAccountNumber = view.findViewById(R.id.layout_account_number);
        layoutMobileNumber = view.findViewById(R.id.layout_mobile_number);
        layoutWalletNumber = view.findViewById(R.id.layout_wallet_number);
        addMoneyBtn = view.findViewById(R.id.button_add_money);
        phoneNumber = view.findViewById(R.id.agent_deposit_mobile_no);
        walletNumber = view.findViewById(R.id.txt_deposit_wallet_no);
        depositAmount = view.findViewById(R.id.deposit_amount);

        spDepositTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //Change selected text color
                    ((TextView) view).setTextColor(getResources().getColor(R.color.textColor));
                } catch (Exception e) {

                }
                if(position==0){
                    layoutWalletNumber.setVisibility(View.GONE);
                    layoutAccountNumber.setVisibility(View.GONE);
                    layoutMobileNumber.setVisibility(View.GONE);
                }
                else if(position==1){
                    layoutWalletNumber.setVisibility(View.VISIBLE);
                    layoutAccountNumber.setVisibility(View.GONE);
                    layoutMobileNumber.setVisibility(View.GONE);
                }
                else if(position==2){
                    layoutWalletNumber.setVisibility(View.GONE);
                    layoutAccountNumber.setVisibility(View.VISIBLE);
                    layoutMobileNumber.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        addMoneyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spDepositTo.getSelectedItem().toString().equalsIgnoreCase("wallet")) {
                    getReceiverName("0" + walletNumber.getText().toString());
                }

            }
        });

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
        Call<MerchantInfoResponse> call = apiRequests.getUserBusinessName(access_token,receiverPhoneNumber,"MerchantDeposit");
        call.enqueue(new Callback<MerchantInfoResponse>() {
            @Override
            public void onResponse(Call<MerchantInfoResponse> call, Response<MerchantInfoResponse> response) {
                if(response.isSuccessful()){
                    business_name = response.body().getData().getBusinessName();

                    FragmentManager fragmentManager = getChildFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    Fragment prev = fragmentManager.findFragmentByTag("dialog");
                    Bundle bundle = new Bundle();
                    bundle.putString("key","deposit");
                    bundle.putString("phone_number",walletNumber.getText().toString());
                    bundle.putString("amount",depositAmount.getText().toString());
                    bundle.putString("customer_name",business_name);
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);

                    // Create and show the dialog.
                    DialogFragment depositDialog = new AgentCustomerConfirmDetails();
                    depositDialog.setArguments(bundle);
                    depositDialog.show(ft, "dialog");

                }else if(response.code()==412) {
                    Toast.makeText(getContext(),"Unknown Merchant",Toast.LENGTH_LONG).show();
                    //redirect to previous step
                    getActivity().getSupportFragmentManager().popBackStack();
                    // confirmBtn.setEnabled(true);
                }
                else if(response.code()==401){
                    TokenAuthActivity.startAuth(getActivity(), true);
                    getActivity().finishAffinity();
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
