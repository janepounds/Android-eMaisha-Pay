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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.fragments.wallet_fragments.TokenAuthFragment;
import com.cabral.emaishapay.models.ConfirmationDataResponse;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;
import com.google.android.material.snackbar.Snackbar;


public class AgentCustomerWithdraw extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "AgentCustomerWithdraw";
    LinearLayout layoutCustomerNumber,layoutAccountNumber,layoutPhoneNumber;
    FrameLayout layoutSubmit;
    private Button txt_withdraw_submit;
    private EditText customerPhoneNumber,amount;
    DialogLoader dialogLoader;
    private String  business_name = "";


    public AgentCustomerWithdraw() {}

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

        layoutAccountNumber = view.findViewById(R.id.layout_withdraw_card_account_number);
        layoutCustomerNumber = view.findViewById(R.id.layout_customer_number);
        layoutPhoneNumber = view.findViewById(R.id.layout_withdraw_card_phonenumber);
        txt_withdraw_submit = view.findViewById(R.id.txt_withdraw_submit);
        customerPhoneNumber = view.findViewById(R.id.txt_withdraw_customer_no);
        amount = view.findViewById(R.id.etxt_customer_withdraw_amount);

        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);


        ImageView close = view.findViewById(R.id.agent_deposit_money_close);
        close.setOnClickListener(v -> dismiss());
        txt_withdraw_submit.setOnClickListener(this);
        layoutSubmit = view.findViewById(R.id.layout_withdraw_submit);
        layoutSubmit.setOnClickListener(this);

        dialogLoader=new DialogLoader(getContext());
        return dialog;

    }

    public void getReceiverName(String receiverPhoneNumber){
        /***************RETROFIT IMPLEMENTATION***********************/
        dialogLoader.showProgressDialog();
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());

        APIRequests apiRequests = APIClient.getWalletInstance(requireContext());
        Call<ConfirmationDataResponse> call = apiRequests.getUserBusinessName(access_token,receiverPhoneNumber,"MerchantWithdraw",request_id,"getReceiverForUser",category);
        call.enqueue(new Callback<ConfirmationDataResponse>() {
            @Override
            public void onResponse(Call<ConfirmationDataResponse> call, Response<ConfirmationDataResponse> response) {
                dialogLoader.hideProgressDialog();

                if(response.isSuccessful() && response.body().getStatus().equalsIgnoreCase("1")){
                    business_name = response.body().getData().getBusinessName();

                    //call confirm withdraw  details
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Bundle bundle = new Bundle();

                    bundle.putString("key","withdraw");
                    bundle.putString("title","Confirm Withdraw Details");
                    bundle.putString("customer_no",customerPhoneNumber.getText().toString());
                    bundle.putString("amount",amount.getText().toString());
                    bundle.putString("customer_name",business_name);
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
                else if( response.isSuccessful() ) {
                    //Toast.makeText(getContext(),response.body().getStatus(),Toast.LENGTH_LONG).show();
                    Snackbar.make(layoutSubmit,response.body().getMessage(),Snackbar.LENGTH_LONG).show();
                }
                else if(response.code()==401){
                    TokenAuthFragment.startAuth( true);
                }

            }

            @Override
            public void onFailure(Call<ConfirmationDataResponse> call, Throwable t) {
                Log.e("info : ", t.getMessage());
                Log.e("info : ", "Something got very wrong");

            }
        });


    }

    @Override
    public void onClick(View v) {
        //getMethod to fetch customer name
        if(TextUtils.isEmpty(customerPhoneNumber.getText())) {
            customerPhoneNumber.setError("Customer number required");
            return;
        } else if( customerPhoneNumber.getText().length()!=9 ) {
            customerPhoneNumber.setError("Invalid customer number");
            return;
        }else if(TextUtils.isEmpty(amount.getText())) {
            amount.setError("Amount required");
            return;
        }else if( Integer.parseInt(amount.getText().toString()) <= 0 ) {
            amount.setError("Invalid amount");
            return;
        }else {
            getReceiverName("0" + customerPhoneNumber.getText().toString());
        }
    }
}