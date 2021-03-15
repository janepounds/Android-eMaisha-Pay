package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
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

import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.fragments.CardListFragment;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.models.CardSpinnerItem;
import com.cabral.emaishapay.models.CropSpinnerItem;
import com.cabral.emaishapay.utils.CryptoUtil;
import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.card.Card;
import com.flutterwave.raveandroid.rave_presentation.card.CardPaymentCallback;
import com.flutterwave.raveandroid.rave_presentation.card.CardPaymentManager;
import com.flutterwave.raveandroid.rave_presentation.card.SavedCardsListener;
import com.flutterwave.raveandroid.rave_presentation.data.AddressDetails;
import com.flutterwave.raveutils.verification.AVSVBVFragment;
import com.flutterwave.raveutils.verification.OTPFragment;
import com.flutterwave.raveutils.verification.PinFragment;
import com.flutterwave.raveutils.verification.RaveVerificationUtils;
import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.models.WalletTransaction;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepositMoneyVisa extends DialogFragment implements

        SavedCardsListener, CardPaymentCallback {
    private static final String TAG = "DepositMoneyVisa";

    Button addMoneyImg;
    TextView addMoneyTxt ,errorMsgTxt;
    EditText cardNumberTxt,  cardexpiryTxt,  cardccvTxt, cardHolderNameTxt;
    Spinner spinner_select_card;
    LinearLayout card_details_layout;
    CheckBox checkbox_save_card;
    private final FragmentManager fm;
    private String txRef;
    private RaveVerificationUtils verificationUtils;
    private CardPaymentManager cardPayManager;
    private List<CardResponse.Cards> cardlists = new ArrayList();
    ArrayList<CardSpinnerItem> cardItems = new ArrayList<>();
    String decripted_card_number = null;
    private String expiryDate,cvv,card_no;

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
        verificationUtils = new RaveVerificationUtils( this, false, BuildConfig.PUBLIC_KEY);


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

                if(checkbox_save_card.isChecked()){
                    //save card
                    if (validateEntries()) {

                        String identifier = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
                        String cvv = cardccvTxt.getText().toString().trim();
                        String expiry = cardexpiryTxt.getText().toString();
                        String account_name = cardHolderNameTxt.getText().toString();
                        String card_number = cardNumberTxt.getText().toString();

                        /**********ENCRPT CARD DETAILS****************/
                        CryptoUtil encrypter = new CryptoUtil(BuildConfig.ENCRYPTION_KEY, getString(R.string.iv));
                        String hash_card_number = encrypter.encrypt(card_number);
                        String hash_cvv = encrypter.encrypt(cvv);
                        String hash_expiry = encrypter.encrypt(expiry);
                        String hash_account_name = encrypter.encrypt(account_name);
                        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
                            /*************RETROFIT IMPLEMENTATION**************/
                            Call<CardResponse> call = APIClient.getWalletInstance().saveCardInfo(access_token,identifier, hash_card_number, hash_cvv, hash_expiry, hash_account_name);
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

                    if(!addMoneyTxt.getText().toString().isEmpty() )
                        initiateDeposit();
                }
                else{
                    if(!addMoneyTxt.getText().toString().isEmpty() )
                        initiateDepositWithExistingCard();
                }

            }
        });
        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (spinner_select_card.getSelectedItem().toString().equalsIgnoreCase("Add New")){
                    //call add card
                    card_details_layout.setVisibility(View.VISIBLE);

            }
                else {
                    for(int i = 0; i<cardItems.size();i++){
                        if(cardItems.get(i).toString().equalsIgnoreCase(spinner_select_card.getSelectedItem().toString())){
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

    public void getCards(){
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        /******************RETROFIT IMPLEMENTATION***********************/
        Call<CardResponse> call = APIClient.getWalletInstance().getCards(access_token);
        call.enqueue(new Callback<CardResponse>() {
            @Override
            public void onResponse(Call<CardResponse> call, Response<CardResponse> response) {
                if(response.isSuccessful()){

                    try {

                        cardlists = response.body().getCardsList();
                        cardItems.add(new CardSpinnerItem() {
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
                            CryptoUtil encrypter =new CryptoUtil(BuildConfig.ENCRYPTION_KEY,getContext().getString(R.string.iv));
                            if(encrypter.decrypt(cardlists.get(i).getCard_number()).length()>4){
                                final  String card_number = encrypter.decrypt(cardlists.get(i).getCard_number());
                                final  String  decripted_expiryDate = encrypter.decrypt(cardlists.get(i).getExpiry());
                                final  String cvv  = encrypter.decrypt(cardlists.get(i).getCvv());

                                String first_four_digits = (card_number.substring(0,  4));
                                String last_four_digits = (card_number.substring(card_number.length() - 4));
                                final String decripted_card_number = first_four_digits + "*******"+last_four_digits;
                                //  Log.w("CardNumber","**********>>>>"+decripted_card_number);
                                cardItems.add(new CardSpinnerItem() {
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
                        ArrayAdapter<CardSpinnerItem> cardListAdapter = new ArrayAdapter<CardSpinnerItem>(getContext(),  android.R.layout.simple_dropdown_item_1line, cardItems);
//                        cardListAdapter = new CardSpinnerAdapter(cardItems, "New", getContext());
                        spinner_select_card.setAdapter(cardListAdapter);
                        dialog.hideProgressDialog();

                    }

                }else if (response.code() == 401) {

                    TokenAuthActivity.startAuth(getActivity(), true);
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


    public void initiateDeposit(){

        String amountEntered = addMoneyTxt.getText().toString();
        String expiryDate=cardexpiryTxt.getText().toString();
        double amount = Double.parseDouble(amountEntered);

        txRef= WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID,this.activity)+(new Date().getTime());
        Log.e("PUBK : ", BuildConfig.PUBLIC_KEY+" : "+expiryDate.substring(0,2)+" : "+expiryDate.substring(3,5));


        String eMaishaPayServiceMail="info@cabraltech.com";
        RaveNonUIManager raveNonUIManager = new RaveNonUIManager().setAmount(amount)
                .setCurrency("UGX")
                .setEmail( eMaishaPayServiceMail )
                .setfName( WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME,this.activity) )
                .setlName( WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_LAST_NAME,this.activity) )
                .setPhoneNumber("+256"+ WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_PHONE_NUMBER,this.activity).substring(1))
                .setNarration("eMaisha Pay Deposit")
                .setPublicKey(BuildConfig.PUBLIC_KEY)
                .setEncryptionKey(BuildConfig.ENCRYPTION_KEY)
                .setTxRef(txRef)
                .onStagingEnv(false)
                .isPreAuth(true)
                .initialize();




         cardPayManager = new CardPaymentManager(
                raveNonUIManager, this, this);

        Card card = new Card(
                cardNumberTxt.getText().toString(),
                expiryDate.substring(0,2),
                expiryDate.substring(3,5),
                cardccvTxt.getText().toString()
        );

        cardPayManager.chargeCard(card);
        //cardPayManager.onWebpageAuthenticationComplete();

    }

    public void initiateDepositWithExistingCard(){

        String amountEntered = addMoneyTxt.getText().toString();

        double amount = Double.parseDouble(amountEntered);

        txRef= WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID,this.activity)+(new Date().getTime());
        Log.e("PUBK : ", BuildConfig.PUBLIC_KEY+" : "+expiryDate.substring(0,2)+" : "+expiryDate.substring(3,5));


        String eMaishaPayServiceMail="info@cabraltech.com";
        RaveNonUIManager raveNonUIManager = new RaveNonUIManager().setAmount(amount)
                .setCurrency("UGX")
                .setEmail( eMaishaPayServiceMail )
                .setfName( WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME,this.activity) )
                .setlName( WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_LAST_NAME,this.activity) )
                .setPhoneNumber("+256"+ WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_PHONE_NUMBER,this.activity).substring(1))
                .setNarration("eMaisha Pay Deposit")
                .setPublicKey(BuildConfig.PUBLIC_KEY)
                .setEncryptionKey(BuildConfig.ENCRYPTION_KEY)
                .setTxRef(txRef)
                .onStagingEnv(false)
                .isPreAuth(true)
                .initialize();




        cardPayManager = new CardPaymentManager(
                raveNonUIManager, this, this);

        Card card = new Card(
                card_no,
                expiryDate.substring(0,2),
                expiryDate.substring(3,5),
                cvv
        );

        cardPayManager.chargeCard(card);
        //cardPayManager.onWebpageAuthenticationComplete();

    }

    public void creditAfterDeposit(String txRef,String thirdparty_id){
        DialogLoader dialogLoader = new DialogLoader(getContext());
        dialogLoader.showProgressDialog();

        String amountEntered = addMoneyTxt.getText().toString();
        double amount = Float.parseFloat(amountEntered);

        /************RETROFIT IMPLEMENTATION*******************/
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<WalletTransaction> call = apiRequests.creditUser(access_token,null,amount,txRef,"Deposit","flutterwave",thirdparty_id, false);
        call.enqueue(new Callback<WalletTransaction>() {
            @Override
            public void onResponse(Call<WalletTransaction> call, Response<WalletTransaction> response) {
                dialogLoader.hideProgressDialog();

                if(response.code() == 200){
                    refreshActivity();
                }else if(response.code() == 401){

                    TokenAuthActivity.startAuth(getActivity(), true);
                } else if (response.code() == 500) {
                    if (response.errorBody() != null) {
                        Toast.makeText(activity,response.body().getRecepient(), Toast.LENGTH_LONG).show();
                    } else {

                        Log.e("info", "Something got very wrong, code: " + response.code());
                    }
                    Log.e("info 500", String.valueOf(response.errorBody()) + ", code: " + response.code());
                } else if (response.code() == 400) {
                    if (response.errorBody() != null) {
                        Toast.makeText(activity, response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    } else {

                        Log.e("info", "Something got very very wrong, code: " + response.code());
                    }
                    Log.e("info 500", String.valueOf(response.errorBody()) + ", code: " + response.code());
                } else if (response.code() == 406) {
                    if (response.errorBody() != null) {

                        Toast.makeText(activity, response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    } else {

                        Log.e("info", "Something got very very wrong, code: " + response.code());
                    }
                    Log.e("info 406", String.valueOf(response.errorBody()) + ", code: " + response.code());
                } else {

                    if (response.errorBody() != null) {

                        Toast.makeText(activity, response.errorBody().toString(), Toast.LENGTH_LONG).show();
                        Log.e("info", String.valueOf(response.errorBody()) + ", code: " + response.code());
                    } else {

                        Log.e("info", "Something got very very wrong, code: " + response.code());
                    }
                }
                dialog.hideProgressDialog();
            }


            @Override
            public void onFailure(Call<WalletTransaction> call, Throwable t) {
                Log.e("info", "Something got very very wrong, code: ");
                dialogLoader.hideProgressDialog();
            }
        });


    }


    public void refreshActivity(){
        Intent goToWallet = new Intent(activity, WalletHomeActivity.class);
        startActivity(goToWallet);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RaveConstants.RESULT_SUCCESS) {
            switch (requestCode) {
                case RaveConstants.PIN_REQUEST_CODE:
                    String pin = data.getStringExtra(PinFragment.EXTRA_PIN);
                    // Use the collected PIN
                    cardPayManager.submitPin(pin);
                    break;
                case RaveConstants.ADDRESS_DETAILS_REQUEST_CODE:
                    String streetAddress = data.getStringExtra(AVSVBVFragment.EXTRA_ADDRESS);
                    String state = data.getStringExtra(AVSVBVFragment.EXTRA_STATE);
                    String city = data.getStringExtra(AVSVBVFragment.EXTRA_CITY);
                    String zipCode = data.getStringExtra(AVSVBVFragment.EXTRA_ZIPCODE);
                    String country = data.getStringExtra(AVSVBVFragment.EXTRA_COUNTRY);
                    AddressDetails address = new AddressDetails(streetAddress, city, state, zipCode, country);

                    // Use the address details
                    cardPayManager.submitAddress(address);
                    break;
                case RaveConstants.WEB_VERIFICATION_REQUEST_CODE:
                    // Web authentication complete, proceed

                    Log.w("UnkownResult",".......Web authentication complete ");
                    //creditAfterDeposit();
                    cardPayManager.onWebpageAuthenticationComplete();
                    break;
                case RaveConstants.OTP_REQUEST_CODE:
                    String otp = data.getStringExtra(OTPFragment.EXTRA_OTP);
                    // Use OTP
                    cardPayManager.submitOtp(otp);
                    break;
            }
        } else {
            Log.w("UnkownResult",".......Unkown Result ");
            super.onActivityResult(requestCode, resultCode, data);
        }


    }
    void fetchUserSavedCards(){
        cardPayManager.fetchSavedCards(true);
    }
    @Override
    public void onSavedCardsLookupSuccessful(List<SavedCard> cards, String phoneNumber) {
        // Check that the list is not empty, show the user to select which they'd like to charge, then proceed to chargeSavedCard()
        if (cards.size() != 0) {

                   // cardPayManager.chargeSavedCard(cards.get(0));
        }
        else
            Toast.makeText(getContext(), "No saved cards found for " + phoneNumber, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSavedCardsLookupFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteSavedCardRequestSuccessful() {

    }

    @Override
    public void onDeleteSavedCardRequestFailed(String message) {

    }


    @Override
    public void collectOtpForSaveCardCharge() {
        //collectOtp("Otp for saved card");
    }

    @Override
    public void onCardSaveSuccessful(String phoneNumber) {
        Toast.makeText(activity, "Card Saved Successfully", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onCardSaveFailed(String message) {
        Log.e("Error: ", message);
    }
    //add card payment listeners
    @Override
    public void showProgressIndicator(boolean active) {
        try {

            if (active ) {
                dialog.showProgressDialog();
            } else {
                dialog.hideProgressDialog();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void collectCardPin() {
        verificationUtils.showPinScreen();
    }

    @Override
    public void collectOtp(String message) {
        verificationUtils.showOtpScreen(message);
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(String errorMessage, @Nullable String flwRef) {
        Log.e("VisapaymentError",errorMessage);
        Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccessful(String flwRef) {
        Log.e("Success code :",flwRef);
        Toast.makeText(activity, "Transaction Successful", Toast.LENGTH_LONG).show();
        creditAfterDeposit(flwRef,card_no);
    }

    @Override
    public void collectAddress() {
        Toast.makeText(getContext(), "Submitting address details", Toast.LENGTH_SHORT).show();
        verificationUtils.showAddressScreen();
    }

    @Override
    public void showAuthenticationWebPage(String authenticationUrl) {
        Log.w("Loading auth web page: ",authenticationUrl);
        verificationUtils.showWebpageVerificationScreen(authenticationUrl);
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

