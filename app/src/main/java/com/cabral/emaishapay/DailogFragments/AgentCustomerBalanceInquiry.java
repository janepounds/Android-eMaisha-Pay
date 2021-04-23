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
import android.widget.Toast;

import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.fragments.wallet_fragments.TokenAuthFragment;
import com.cabral.emaishapay.models.ConfirmationDataResponse;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;
import com.flutterwave.raveutils.verification.RaveVerificationUtils;


public class AgentCustomerBalanceInquiry extends DialogFragment {
    LinearLayout layoutWalletNumber,layoutAccountNumber,layoutPhoneNumber;
    Spinner spAccountType;
    Button addMoney;
    private String business_name = " ";
    EditText walletNo;


    public AgentCustomerBalanceInquiry() {
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
        View view = inflater.inflate(R.layout.dialog_agent_customer_balance_inquiry, null);
        spAccountType = view.findViewById(R.id.sp_account_type);
        layoutWalletNumber = view.findViewById(R.id.layout_balance_inquiry_wallet_no);
        layoutAccountNumber = view.findViewById(R.id.layout_balance_inquiry_card_account_no);
        layoutPhoneNumber = view.findViewById(R.id.layout_balance_inquiry_card_phone_no);
        addMoney = view.findViewById(R.id.button_add_money);
        walletNo = view.findViewById(R.id.agent_balance_inquiry_mobile_no);

        spAccountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    layoutPhoneNumber.setVisibility(View.GONE);
                }
                else if(position==1){
                    layoutWalletNumber.setVisibility(View.VISIBLE);
                    layoutAccountNumber.setVisibility(View.GONE);
                    layoutPhoneNumber.setVisibility(View.GONE);
                }
                else if(position==2) {
                    layoutWalletNumber.setVisibility(View.GONE);
                    layoutAccountNumber.setVisibility(View.VISIBLE);
                    layoutPhoneNumber.setVisibility(View.VISIBLE);


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call confirm details layout
                //call customer details dialog
                if(spAccountType.getSelectedItem().toString().equalsIgnoreCase("wallet")) {
                    getReceiverName("0" + walletNo.getText().toString());
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

        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();

        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<ConfirmationDataResponse> call = apiRequests.getUserBusinessName(access_token,receiverPhoneNumber,"MerchantBalanceInquiry",request_id,"getReceiverForUser");
        call.enqueue(new Callback<ConfirmationDataResponse>() {
            @Override
            public void onResponse(Call<ConfirmationDataResponse> call, Response<ConfirmationDataResponse> response) {
                if(response.isSuccessful()){
                    business_name = response.body().getData().getBusinessName();
                    FragmentManager fragmentManager = getChildFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    Fragment prev = fragmentManager.findFragmentByTag("dialog");
                    Bundle bundle = new Bundle();
                    bundle.putString("key","balance inquiry");
                    bundle.putString("title","Confirm Balance Inquiry Details");
                    bundle.putString("customer_no",walletNo.getText().toString());
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
                    TokenAuthFragment.startAuth( true);

                }

            }

            @Override
            public void onFailure(Call<ConfirmationDataResponse> call, Throwable t) {

                Log.e("info : ", t.getMessage());
                Log.e("info : ", "Something got very very wrong");

            }
        });


    }


}