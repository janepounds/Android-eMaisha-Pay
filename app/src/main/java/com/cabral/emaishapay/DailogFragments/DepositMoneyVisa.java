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
import androidx.fragment.app.FragmentManager;

import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.fragments.wallet_fragments.TokenAuthFragment;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.models.CardSpinnerItem;
import com.flutterwave.raveandroid.rave_presentation.data.AddressDetails;
import com.flutterwave.raveutils.verification.AVSVBVFragment;
import com.flutterwave.raveutils.verification.OTPFragment;
import com.flutterwave.raveutils.verification.PinFragment;
import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.models.WalletTransaction;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepositMoneyVisa extends DialogFragment  {
    private static final String TAG = "DepositMoneyVisa";

    Button addMoneyImg;
    TextView addMoneyTxt ,errorMsgTxt;
    EditText cardNumberTxt,  cardexpiryTxt,  cardccvTxt, cardHolderNameTxt;
    Spinner spinner_select_card;
    LinearLayout card_details_layout;
    CheckBox checkbox_save_card;
    private final FragmentManager fm;
    private String txRef;
    private List<CardResponse.Cards> cardlists = new ArrayList();
    ArrayList<CardSpinnerItem> cardItems = new ArrayList<>();
    private String expiryDate,cvv,card_no,card_id;

    double balance;
    DialogLoader dialog;
    Context activity;
    public DepositMoneyVisa(Context context, double balance, FragmentManager fm){
        this.activity=context;
        this.balance=balance;
        this.fm = fm;
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

    public void initializeForm(View view) {

        dialog = new DialogLoader(getContext());
        addMoneyImg = view.findViewById(R.id.button_add_money);
        addMoneyTxt = view.findViewById(R.id.wallet_add_money_amount);
        cardNumberTxt=view.findViewById(R.id.add_money_creditCardNumber);
        cardHolderNameTxt=view.findViewById(R.id.add_money_holder_name);
        cardexpiryTxt=view.findViewById(R.id.add_money_card_expiry);
        cardccvTxt=view.findViewById(R.id.add_money_card_cvv);
        errorMsgTxt = view.findViewById(R.id.text_view_error_message);
        spinner_select_card = view.findViewById(R.id.spinner_select_card);
        card_details_layout = view.findViewById(R.id.card_details_layout);
        checkbox_save_card = view.findViewById(R.id.checkbox_save_card);
        addMoneyImg.setText("Top Up");

        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (filterLongEnough() && !cardexpiryTxt.getText().toString().contains("/")) {
                    cardexpiryTxt.setText(cardexpiryTxt.getText().toString()+"/");
                    int pos = cardexpiryTxt.getText().length();
                    cardexpiryTxt.setSelection(pos);
                }
            }

            private boolean filterLongEnough() {
                return cardexpiryTxt.getText().toString().length() == 2;
            }
        };
        cardexpiryTxt.addTextChangedListener(fieldValidatorTextWatcher);

        addMoneyImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner_select_card.getSelectedItem().toString().equalsIgnoreCase("Select Card")){
                    Snackbar.make(addMoneyImg, getString(R.string.invalid_payment_token), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                else if(spinner_select_card.getSelectedItem().toString().equalsIgnoreCase("Add New")){

                    card_no = cardNumberTxt.getText().toString();
                    cvv = cardccvTxt.getText().toString();
                    expiryDate = cardexpiryTxt.getText().toString();
                }

                if( spinner_select_card.getSelectedItem().toString().equalsIgnoreCase("Add New") ){
                        //save card
                        if (validateEntries()) {
                            saveNewCard();
                        }
                }
                else{
                    if(!addMoneyTxt.getText().toString().isEmpty() )
                        initiateDepositWithExistingCard(card_id);

                }

            }
        });
        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //Change selected text color
                    ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                    //((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);//Change selected text size
                } catch (Exception e) {

                }

                if (spinner_select_card.getSelectedItem().toString().equalsIgnoreCase("Add New")){
                    //call add card
                    card_details_layout.setVisibility(View.VISIBLE);
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

        getCards();

    }

    private void saveNewCard() {

        String identifier = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
        String cvv = cardccvTxt.getText().toString().trim();
        String expiry = cardexpiryTxt.getText().toString();
        String account_name = cardHolderNameTxt.getText().toString();
        String card_number = cardNumberTxt.getText().toString();


        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        /*************RETROFIT IMPLEMENTATION**************/
        Call<CardResponse> call = APIClient.getWalletInstance(getContext())
                .saveCardInfo(access_token,
                        identifier,
                        card_number,
                        cvv,
                        expiry,
                        account_name,
                        getString(R.string.currency),
                        request_id,
                        category,
                        "saveCard");

        call.enqueue(new Callback<CardResponse>() {
            @Override
            public void onResponse(Call<CardResponse> call, Response<CardResponse> response) {
                if (response.isSuccessful()) {
                    String message = response.body().getMessage();
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<CardResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();


            }
        });

    }

    public void getCards(){
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        /******************RETROFIT IMPLEMENTATION***********************/
        Call<CardResponse> call = APIClient.getWalletInstance(getContext()).getCards(access_token,request_id,category,"getCards");
        call.enqueue(new Callback<CardResponse>() {
            @Override
            public void onResponse(Call<CardResponse> call, Response<CardResponse> response) {
                if(response.isSuccessful()){

                    try {

                        cardlists = response.body().getCardsList();
                        cardItems.add(new CardSpinnerItem() {
                            @Override
                            public String getId() {
                                return null;
                            }

                            @Override
                            public String getCardNumber() {
                                return null;
                            }

                            @Override
                            public String getExpiryDate() {
                                return null;
                            }

                            @Override
                            public String getCvv() {
                                return null;
                            }

                            @Override
                            public String toString() {
                                return "Select Card";
                            }
                        });

                        for(int i =0; i<cardlists.size();i++){

                            //decript card number
                            if( cardlists.get(i).getCard_number().length()>4){
                                final  String card_number = cardlists.get(i).getCard_number();
                                final  String  decripted_expiryDate = cardlists.get(i).getExpiry();
                                final  String cvv  = cardlists.get(i).getCvv();
                                final  String id  = cardlists.get(i).getId();

                                String first_four_digits = (card_number.substring(0,  4));
                                String last_four_digits = (card_number.substring(card_number.length() - 4));
                                final String decripted_card_number = first_four_digits + "*******"+last_four_digits;
                                //  Log.w("CardNumber","**********>>>>"+decripted_card_number);
                                cardItems.add(new CardSpinnerItem() {
                                    @Override
                                    public String getId() {
                                        return id;
                                    }

                                    @Override
                                    public String getCardNumber() {
                                        return card_number;
                                    }

                                    @Override
                                    public String getExpiryDate() {
                                        return decripted_expiryDate;
                                    }

                                    @Override
                                    public String getCvv() {
                                        return cvv;
                                    }

                                    @NonNull
                                    @Override
                                    public String toString() {
                                        return decripted_card_number;
                                    }
                                });
                            }
                        }


                        cardItems.add(new CardSpinnerItem() {
                            @Override
                            public String getId() {
                                return null;
                            }

                            @Override
                            public String getCardNumber() {
                                return null;
                            }

                            @Override
                            public String getExpiryDate() {
                                return null;
                            }

                            @Override
                            public String getCvv() {
                                return null;
                            }

                            @Override
                            public String toString() {
                                return "Add New";
                            }
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        ArrayAdapter<CardSpinnerItem> cardListAdapter = new ArrayAdapter(getContext(),  android.R.layout.simple_dropdown_item_1line, cardItems);
//                        cardListAdapter = new CardSpinnerAdapter(cardItems, "New", getContext());
                        spinner_select_card.setAdapter(cardListAdapter);
                        dialog.hideProgressDialog();

                    }

                }else if (response.code() == 401) {

                    TokenAuthFragment.startAuth( true);
                    if (response.errorBody() != null) {
                        Log.e("info", new String(String.valueOf(response.errorBody())));
                    } else {
                        Log.e("info", "Something got very very wrong");
                    }
                    dialog.hideProgressDialog();
                }

            }

            @Override
            public void onFailure(Call<CardResponse> call, Throwable t) {
                dialog.hideProgressDialog();
            }
        });

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
                }
                else {

                    if (response.errorBody() != null) {

                        Log.e("info", String.valueOf(response.errorBody()) + ", code: " + response.code());
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



    public boolean validateEntries(){

        if (cardHolderNameTxt.getText().toString().trim() == null || cardHolderNameTxt.getText().toString().trim().isEmpty()) {
            cardHolderNameTxt.setError("Please enter valid value");
            return false;

        } else if (cardNumberTxt.getText().toString().trim() == null || cardNumberTxt.getText().toString().trim().isEmpty()
                || cardNumberTxt.getText().toString().trim().length()<13 ){
            cardNumberTxt.setError("Please enter valid value");
            return false;
        }

        else if (cardexpiryTxt.getText().toString().trim() == null || cardexpiryTxt.getText().toString().trim().isEmpty()){
            cardexpiryTxt.setError("Please select valid value");
            return false;
        }

        else if (cardccvTxt.getText().toString().trim() == null || cardccvTxt.getText().toString().trim().isEmpty()
                || cardccvTxt.getText().toString().trim().length()<3 ){
            cardccvTxt.setError("Please enter valid value");
            return false;
        }else {

            return true;
        }





    }

}

