package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
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
import com.cabral.emaishapay.models.InitiateWithdrawResponse;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;
import com.flutterwave.raveutils.verification.RaveVerificationUtils;
import com.google.android.material.snackbar.Snackbar;


public class AgentCustomerBalanceInquiry extends DialogFragment {
    LinearLayout layoutWalletNumber,layoutAccountNumber,layoutPhoneNumber;
    Spinner spAccountType;
    Button addMoney;
    private String business_name = " ";
    EditText walletNo;
    DialogLoader dialogLoader;
    Dialog agentPinDialog,confirmBalanceDialog;


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
                    ((TextView) view).setTextColor(ContextCompat.getColor(getContext(),R.color.white));
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
                if( TextUtils.isEmpty(walletNo.getText()) ){
                    walletNo.setError( getString(R.string.account_number)+" required");
                    return;
                }else if( walletNo.getText().length() != 9 ){
                    walletNo.setError( getString(R.string.account_number)+" invalid");
                    return;
                }
                else {
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

        dialogLoader= new DialogLoader(getContext());
        return dialog;

    }

    public void getReceiverName(String receiverPhoneNumber){
        /***************RETROFIT IMPLEMENTATION***********************/
        dialogLoader.showProgressDialog();
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());

        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<ConfirmationDataResponse> call = apiRequests.getUserBusinessName(access_token,receiverPhoneNumber,"MerchantBalanceInquiry",request_id,"getReceiverForUser",category);

        call.enqueue(new Callback<ConfirmationDataResponse>() {
            @Override
            public void onResponse(Call<ConfirmationDataResponse> call, Response<ConfirmationDataResponse> response) {
                dialogLoader.hideProgressDialog();
                if(response.isSuccessful() && response.body().getStatus().equals("1")){
                    business_name = response.body().getData().getBusinessName();
                    String request_id2 = WalletHomeActivity.generateRequestId();

                    //Inner Dialog to enter PIN
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                    //LayoutInflater inflater = requireActivity().getLayoutInflater();
                    View confirmDialog = View.inflate(getContext(),R.layout.dialog_agent_customer_confirm_details,null);


                    builder.setView(confirmDialog);
                    confirmBalanceDialog= builder.create();
                    builder.setCancelable(false);

                    TextView textTitlelabel =confirmDialog.findViewById(R.id.agent_confirm_details_title_label);
                    TextView textPhoneNumber =confirmDialog.findViewById(R.id.text_phone_number);
                    TextView textName =confirmDialog.findViewById(R.id.text_name);

                    CardView layoutCharge =confirmDialog.findViewById(R.id.card_transaction_charge);
                    CardView depositAmount =confirmDialog.findViewById(R.id.card_deposit_amount);
                    CardView totalAmount =confirmDialog.findViewById(R.id.card_total_amount);

                    textPhoneNumber.setText(receiverPhoneNumber);
                    textName.setText(business_name);
                    textTitlelabel.setText(getString(R.string.confirm_customer_details));

                    totalAmount.setVisibility(View.GONE);
                    layoutCharge.setVisibility(View.GONE);
                    depositAmount.setVisibility(View.GONE);

                    confirmDialog.findViewById(R.id.txt_card_confirm).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            prepareBalanceRequest(request_id2,category,access_token, business_name);
                        }
                    });

                    confirmBalanceDialog.show();




                }else if(response.isSuccessful() ){
                    Snackbar.make(addMoney,response.body().getMessage(),Snackbar.LENGTH_LONG).show();
                }
                else if(response.code()==412) {
                    Log.e("info : ", "Something got very wrong");
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
    private void prepareBalanceRequest(String request_id, String category, String access_token, String customer_name) {
        //Inner Dialog to enter PIN
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        //LayoutInflater inflater = requireActivity().getLayoutInflater();
        View pinDialog = View.inflate(getContext(),R.layout.dialog_enter_pin,null);


        builder.setView(pinDialog);
        agentPinDialog= builder.create();
        builder.setCancelable(false);

        EditText pinEdittext =pinDialog.findViewById(R.id.etxt_create_agent_pin);
        TextView titleTxt = pinDialog.findViewById(R.id.dialog_title);
        titleTxt.setText("ENTER AGENT PIN");

        pinDialog.findViewById(R.id.txt_custom_add_agent_submit_pin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !TextUtils.isEmpty(pinEdittext.getText()) && pinEdittext.getText().length()==4){
                    String service_code= WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION+ pinEdittext.getText().toString();

                    requestBalanceInquiry(customer_name,access_token,request_id,category,service_code);

                }else {
                    pinEdittext.setError("Invalid Pin Entered");
                }

            }
        });

        agentPinDialog.show();
    }

    private void requestBalanceInquiry(String customer_name, String access_token, String request_id, String category, String service_code) {

        dialogLoader.showProgressDialog();

        /***************RETROFIT IMPLEMENTATION FOR BALANCE INQUIRY************************/
        Call<InitiateWithdrawResponse> call = APIClient.getWalletInstance(getContext()).balanceInquiry(access_token , getString(R.string.phone_number_code)+walletNo.getText().toString(),request_id,category,"merchantBalanceInquiry",service_code);
        call.enqueue(new Callback<InitiateWithdrawResponse>() {
            @Override
            public void onResponse(Call<InitiateWithdrawResponse> call, Response<InitiateWithdrawResponse> response) {

                dialogLoader.hideProgressDialog();
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("1")) {
                        double balance = response.body().getData().getBalance();
                        //Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        //call balance dialog
                        FragmentManager fragmentManager = getChildFragmentManager();

                        FragmentTransaction ft = fragmentManager.beginTransaction();
                        Fragment prev = fragmentManager.findFragmentByTag("dialog");
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);
                        Bundle bundle = new Bundle();
                        bundle.putString("balance",balance+"");
                        bundle.putString("customer_name",customer_name);

                        // Create and show the dialog.
                        DialogFragment balanceDialog = new BalanceDialog();
                        balanceDialog.setArguments(bundle);

                        balanceDialog.show(ft, "dialog");

                    } else {
                        //EnterPin.this.dismiss();
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                         //Log.w("BalanceError",response.body().getMessage());
                    }
                }


            }

            @Override
            public void onFailure(Call<InitiateWithdrawResponse> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Log.e("BalanceError","something went wrong");
                AgentCustomerBalanceInquiry.this.dismiss();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getContext(), WalletHomeActivity.class);
                startActivity(intent);

            }
        });
    }


}