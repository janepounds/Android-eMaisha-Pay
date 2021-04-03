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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.fragments.TokenAuthFragment;
import com.cabral.emaishapay.models.coupons_model.CouponsData;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DepositMoneyVoucher extends DialogFragment {

    Button addMoneyImg;
    TextView errorMsgTxt;
    EditText voucherTxt;

    double balance;
    ProgressDialog dialog;
    Context activity;
    public DepositMoneyVoucher(Context context, double balance){
        this.activity=context;   this.balance=balance;

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
        View view =inflater.inflate(R.layout.wallet_add_money_voucher, null);
        ImageView close = view.findViewById(R.id.wallet_deposit_close);
        close.setOnClickListener(v -> dismiss());

        builder.setView(view);

        initializeForm( view);

        return builder.create();

    }
    public void initializeForm(View view) {
        addMoneyImg = view.findViewById(R.id.button_add_money);

        voucherTxt= view.findViewById(R.id.wallet_add_money_amount);

        errorMsgTxt = view.findViewById(R.id.text_view_error_message);


        addMoneyImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateDeposit();
            }
        });

        dialog = new ProgressDialog(this.activity);
        dialog.setIndeterminate(true);
        dialog.setMessage("Processing the transaction..");
        dialog.setCancelable(false);
    }

    public void initiateDeposit(){
        dialog.show();
        /************RETROFIT IMPLEMENTATION*************/
        String access_token = TokenAuthFragment.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String codeEntered = voucherTxt.getText().toString();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());

        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<CouponsData> call = apiRequests.voucherDeposit(access_token,codeEntered,request_id,category,"initiateVoucherPay");
        call.enqueue(new Callback<CouponsData>() {
            @Override
            public void onResponse(Call<CouponsData> call, Response<CouponsData> response) {
                if(response.code()== 200){
                    refreshActivity();
                }else if(response.code()==401){
                    TokenAuthFragment.startAuth( true);

                }else if(response.code()==500){
                    if (response.errorBody() != null) {
                        errorMsgTxt.setText(response.body().getMessage());
                    } else {

                        Log.e("info", "Something got very very wrong, code: "+response.code());
                    }
                    Log.e("info 500", new String(String.valueOf(response.errorBody()))+", code: "+response.code());

                }else if(response.code() ==400){
                    if (response.errorBody() != null) {
                        Toast.makeText(getContext(), "Wrong Voucher code!", Toast.LENGTH_LONG).show();
                    } else {

                        Log.e("info", "Something got very very wrong, code: "+response.code());
                    }
                    Log.e("info 500", new String(String.valueOf(response.errorBody()))+", code: "+response.code());
                    dialog.dismiss();
                }else if(response.code() ==406){
                    if (response.errorBody() != null) {

                            errorMsgTxt.setText(response.errorBody().toString());
                            errorMsgTxt.setText("Error Occurred Please Check and Try again");

                    }else {

                        Log.e("info", "Something got very very wrong, code: "+response.code());
                    }
                    Log.e("info 406", new String(String.valueOf(response.errorBody()))+", code: "+response.code());
                    dialog.dismiss();
                }
                else{
                    errorMsgTxt.setText("Error Occurred Try again later");
                    if (response.errorBody() != null) {
                        errorMsgTxt.setText(response.errorBody().toString());
                        Log.e("info", new String(String.valueOf(response.errorBody()))+", code: "+response.code());
                    } else {

                        Log.e("info", "Something got very very wrong, code: "+response.code());
                    }
                }
                dialog.dismiss();
                }




            @Override
            public void onFailure(Call<CouponsData> call, Throwable t) {

                    errorMsgTxt.setText(t.getMessage());


                    Log.e("info : ", "Something got very wrong");

                errorMsgTxt.setText("Error Occurred Try again later");
                errorMsgTxt.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });


    }

    public void refreshActivity(){
        this.dismiss();
        Intent goToWallet = new Intent(activity, WalletHomeActivity.class);
        startActivity(goToWallet);
    }

}
