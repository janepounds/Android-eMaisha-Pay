package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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

import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.fragments.wallet_fragments.TokenAuthFragment;
import com.cabral.emaishapay.fragments.wallet_fragments.WalletHomeFragment;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.models.CardSpinnerItem;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepositMoneyVisa extends DialogFragment  {
    private static final String TAG = "DepositMoneyVisa";

    Button addMoneyImg;
    TextView addMoneyTxt ,errorMsgTxt;
    Spinner spinner_select_card;
    LinearLayout card_details_layout;
    CheckBox checkbox_save_card;

    ArrayList<CardSpinnerItem> cardItems;
    private String expiryDate,cvv,card_no,card_id;

    double balance;
    Context activity;
    public DepositMoneyVisa(ArrayList<CardSpinnerItem> cardItems, double balance){
        this.cardItems=cardItems;
        this.balance=balance;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity=context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view =inflater.inflate(R.layout.wallet_add_money_visa_card, null);
        builder.setView(view);
        initializeForm( view);
        ImageView close = view.findViewById(R.id.wallet_deposit_close);
        close.setOnClickListener(v -> dismiss());

        return builder.create();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    public void initializeForm(View view) {
        addMoneyImg = view.findViewById(R.id.button_add_money);
        addMoneyTxt = view.findViewById(R.id.wallet_add_money_amount);
        errorMsgTxt = view.findViewById(R.id.text_view_error_message);
        spinner_select_card = view.findViewById(R.id.spinner_select_card);
        card_details_layout = view.findViewById(R.id.card_details_layout);
        checkbox_save_card = view.findViewById(R.id.checkbox_save_card);
        addMoneyImg.setText("Top Up");

        addMoneyImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner_select_card.getSelectedItem().toString().equalsIgnoreCase("Select Card")){
                    Snackbar.make(addMoneyImg, getString(R.string.invalid_payment_token), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if( spinner_select_card.getSelectedItem().toString().equalsIgnoreCase("Add New") ){


                }
                else{
                    if(!addMoneyTxt.getText().toString().isEmpty() )
                        initiateDepositWithExistingCard(card_id);

                }

            }
        });
        ArrayAdapter<CardSpinnerItem> cardListAdapter = new ArrayAdapter(activity,  android.R.layout.simple_dropdown_item_1line, cardItems);
        spinner_select_card.setAdapter(cardListAdapter);
        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (spinner_select_card.getSelectedItem().toString().equalsIgnoreCase("Add New")){
                    navigateToAddCard();
                }
                else {
                    for(int i = 0; i<cardItems.size();i++){
                        if(cardItems.get(i).toString().equalsIgnoreCase(spinner_select_card.getSelectedItem().toString())){
                            card_id=cardItems.get(i).getId();
                            expiryDate =  cardItems.get(i).getExpiryDate();
                            card_no = cardItems.get(i).getCardNumber();
                            cvv = cardItems.get(i).getCvv();

                        }
                    }
                    Log.d(TAG, "onItemSelected: expiry"+expiryDate+"card_no"+card_no+"cvv"+cvv);
                    card_details_layout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        spinner_select_card.setOnItemSelectedListener(onItemSelectedListener);


    }

    private void navigateToAddCard() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev =fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment addCardDialog =new AddCardFragment();
        addCardDialog.show( ft, "dialog");
    }



    public void initiateDepositWithExistingCard(String card_id){
        DialogLoader dialogLoader = new DialogLoader(getContext());
        dialogLoader.showProgressDialog();

        String amountEntered = addMoneyTxt.getText().toString();
        double amount = Float.parseFloat(amountEntered);

        /************RETROFIT IMPLEMENTATION*******************/
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        String service_code = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_USER_PASSWORD,requireContext());

        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<CardResponse> call;

        if(category.equalsIgnoreCase("Merchant")){
            call = apiRequests.cardTopUpMerchant(
                    access_token,
                    amount,
                    card_id,
                    request_id,
                    category,
                    service_code,
                    "merchantEmaishaCardTopup"
            );
        }else if(category.equalsIgnoreCase("Agent")){
            call = apiRequests.cardTopUpAgent(
                    access_token,
                    amount,
                    card_id,
                    request_id,
                    category,
                    service_code,
                    "agentEmaishaCardTopup"
            );
        }else{
            call = apiRequests.cardTopUp(
                    access_token,
                    amount,
                    card_id,
                    request_id,
                    category,
                    service_code,
                    "customerEmaishaCardTopup"
            );
        }


        call.enqueue(new Callback<CardResponse>() {
            @Override
            public void onResponse(Call<CardResponse> call, Response<CardResponse> response) {
                dialogLoader.hideProgressDialog();

                if(response.isSuccessful() && response.body().getStatus()==1){
                    //success message
                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.dialog_successful_message);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(false);
                    TextView text = dialog.findViewById(R.id.dialog_success_txt_message);
                    text.setText( response.body().getMessage() );


                    dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            refreshActivity();
                        }
                    });
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();


                }
                else if(response.isSuccessful()){
                    Snackbar.make(addMoneyTxt,response.body().getMessage(), Snackbar.LENGTH_LONG).show();
                }else if (response.code() == 401) {
                    dialogLoader.hideProgressDialog();
                    TokenAuthFragment.startAuth(true);
                    DepositMoneyVisa.this.dismiss();
                    WalletHomeFragment.depositPaymentsDialog.dismiss();
                }
                else {

                    if (response.errorBody() != null) {

                        Log.e("info", response.errorBody() + ", code: " + response.code());
                    } else {

                        Log.e("info", "Something got very wrong, code: " + response.code());
                    }
                }

            }


            @Override
            public void onFailure(Call<CardResponse> call, Throwable t) {
                Log.e("info", "Something got very very wrong, code: ");
                dialogLoader.hideProgressDialog();
            }
        });


    }


    public void refreshActivity(){
        Intent goToWallet = new Intent(activity, WalletHomeActivity.class);
        startActivity(goToWallet);
    }



}

