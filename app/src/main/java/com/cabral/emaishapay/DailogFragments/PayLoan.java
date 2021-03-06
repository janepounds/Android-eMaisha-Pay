package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.fragments.wallet_fragments.WalletLoansListFragment;
import com.cabral.emaishapay.models.LoanPayResponse;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PayLoan extends DialogFragment {

    EditText totalAmountTxt;
    TextView errorTextView;
    Button saveBtn;
    Context activity;
    FragmentManager fm;
    DialogLoader dialogLoader;

    public PayLoan(Context context, FragmentManager supportFragmentManager){
        this.activity=context;
        this.fm=supportFragmentManager;
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
        LayoutInflater inflater = getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view =inflater.inflate(R.layout.wallet_pay_loan, null);

        builder.setView(view);
        dialogLoader = new DialogLoader(getContext());

        initializeForm( view);

        ImageView close = view.findViewById(R.id.wallet_transfer_money_close);
        close.setOnClickListener(v -> dismiss());

        return builder.create();

    }

    public void initializeForm(View view) {

        totalAmountTxt = view.findViewById(R.id.txt_loan_bill_total);
        errorTextView = view.findViewById(R.id.error_message_txt);
        saveBtn = view.findViewById(R.id.btn_pay);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPayment();
            }
        });
    }

    public void processPayment(){
        dialogLoader.showProgressDialog();

            float amount = Float.parseFloat(totalAmountTxt.getText().toString());

            String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
            String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());

            String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, activity);
            

            /*********RETROFIT IMPLEMENTATION*************/
            APIRequests apiRequests = APIClient.getWalletInstance(getContext());
            Call<LoanPayResponse> call = apiRequests.loanPay(access_token,amount,userId,request_id,category,"payLoan");
            call.enqueue(new Callback<LoanPayResponse>() {
                @Override
                public void onResponse(Call<LoanPayResponse> call, Response<LoanPayResponse> response) {
                    if(response.code()== 200){
                        dialogLoader.hideProgressDialog();
                        FragmentManager fragmentManager = getParentFragmentManager();
                        Fragment fragment = new WalletLoansListFragment();

                        fragmentManager.beginTransaction()
                                .add(R.id.mainView, fragment)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();
                    }else if(response.errorBody()!=null){

                            errorTextView.setText("Error occured! Try again later");
                        }
                        else{
                            errorTextView.setText(response.body().getMessage());}
                            errorTextView.setVisibility(View.VISIBLE);
                    dialogLoader.hideProgressDialog();


                }

                @Override
                public void onFailure(Call<LoanPayResponse> call, Throwable t) {
                    Log.e("info 1A: ", String.valueOf(t.getMessage()));

                    Log.e("info 1A: ", "Something got very wrong");
                }
            });



    }


}