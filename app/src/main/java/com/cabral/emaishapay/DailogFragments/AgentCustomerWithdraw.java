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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.models.MerchantInfoResponse;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.flutterwave.raveutils.verification.RaveVerificationUtils;


public class AgentCustomerWithdraw extends DialogFragment {
    private static final String TAG = "AgentCustomerWithdraw";
    LinearLayout layoutCustomerNumber,layoutAccountNumber,layoutPhoneNumber;
    Spinner spWithdrawFrom;
    Button addMoneyImg;
    TextView addMoneyTxt, phoneNumberTxt, errorMsgTxt;
    static String PENDING_DEPOSIT_REFERENCE_NUMBER;
    TextView balanceTextView;
    double balance;
    private String txRef;
    ProgressDialog dialog;
    Context activity;
    private RaveVerificationUtils verificationUtils;
    private Button txt_withdraw_submit;
    private EditText customerPhoneNumber,amount,customer;
    private String  businessName;
    private String phone_no;
    Context context;


    public AgentCustomerWithdraw() {


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
        View view = inflater.inflate(R.layout.dialog_agent_customer_withdraw, null);
        spWithdrawFrom = view.findViewById(R.id.sp_withdraw_from);
        layoutAccountNumber = view.findViewById(R.id.layout_withdraw_card_account_number);
        layoutCustomerNumber = view.findViewById(R.id.layout_customer_number);
        layoutPhoneNumber = view.findViewById(R.id.layout_withdraw_card_phonenumber);
        txt_withdraw_submit = view.findViewById(R.id.txt_withdraw_submit);
        customerPhoneNumber = view.findViewById(R.id.txt_withdraw_customer_no);
         amount = view.findViewById(R.id.etxt_customer_withdraw_amount);

        spWithdrawFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //Change selected text color
                    ((TextView) view).setTextColor(getResources().getColor(R.color.textColor));
                } catch (Exception e) {

                }
                if(position==0){
                    layoutCustomerNumber.setVisibility(View.GONE);
                    layoutAccountNumber.setVisibility(View.GONE);
                    layoutPhoneNumber.setVisibility(View.GONE);
                }
                else if(position==1){
                    layoutCustomerNumber.setVisibility(View.VISIBLE);
                    layoutAccountNumber.setVisibility(View.GONE);
                    layoutPhoneNumber.setVisibility(View.GONE);
                }
                else if(position==2) {
                    layoutCustomerNumber.setVisibility(View.GONE);
                    layoutAccountNumber.setVisibility(View.VISIBLE);
                    layoutPhoneNumber.setVisibility(View.VISIBLE);


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);


        ImageView close = view.findViewById(R.id.agent_deposit_money_close);
        close.setOnClickListener(v -> dismiss());
        txt_withdraw_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //call confirm withdraw  details
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Bundle bundle = new Bundle();
                    if(spWithdrawFrom.getSelectedItem().toString().equalsIgnoreCase("withdraw")) {
                        //getMethod to fetch customer name
                        getReceiverName("0" + customerPhoneNumber.getText().toString());
                        bundle.putString("customer", businessName);


                    }
                bundle.putString("key","withdraw");
                bundle.putString("title","Confirm Withdraw Details");
                bundle.putString("phone_number",customerPhoneNumber.getText().toString());
                bundle.putString("amount",amount.getText().toString());
                FragmentTransaction ft = fm.beginTransaction();
                Fragment prev =fm.findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                // Create and show the dialog.
                DialogFragment addCardDialog =new AgentCustomerConfirmDetails();
                addCardDialog.setArguments(bundle);
                addCardDialog.show( ft, "dialog");



            }
        });

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
                if(response.code()==200){
                   businessName = response.body().getData().getBusinessName();

                }else if(response.code()==412) {
                    String businessName = null;
                    businessName = response.body().getMessage();
                    // confirmBtn.setEnabled(true);
                }
                else if(response.code()==401){
                    TokenAuthActivity.startAuth(activity, true);
                }
                if (response.errorBody() != null) {
                    Log.e("info", String.valueOf(response.errorBody()));
                } else {
                    Log.e("info", "Something got very very wrong");
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