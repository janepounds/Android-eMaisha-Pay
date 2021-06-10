package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.fragments.wallet_fragments.TokenAuthFragment;
import com.cabral.emaishapay.fragments.wallet_fragments.WalletHomeFragment;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.models.CardSpinnerItem;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.models.WalletTransaction;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;
import com.flutterwave.raveandroid.RaveUiManager;
import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_presentation.card.Card;
import com.flutterwave.raveandroid.rave_presentation.card.CardPaymentCallback;
import com.flutterwave.raveandroid.rave_presentation.card.CardPaymentManager;
import com.flutterwave.raveandroid.rave_presentation.card.SavedCardsListener;
import com.flutterwave.raveandroid.rave_presentation.data.AddressDetails;
import com.flutterwave.raveutils.verification.AVSVBVFragment;
import com.flutterwave.raveutils.verification.OTPFragment;
import com.flutterwave.raveutils.verification.PinFragment;
import com.flutterwave.raveutils.verification.RaveVerificationUtils;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepositMoneyVisa extends DialogFragment   implements  SavedCardsListener, CardPaymentCallback
{
    private static final String TAG = "DepositMoneyVisa";

    Button addMoneyImg;
    TextView addMoneyTxt ,errorMsgTxt;
    Spinner spinner_select_card;
    LinearLayout card_details_layout;
    CheckBox checkbox_save_card;
    private RaveVerificationUtils verificationUtils;
    private CardPaymentManager cardPayManager;

    ArrayList<CardSpinnerItem> cardItems;
    private String expiryDate,cvv,card_no,card_id;
    private String txRef;
    double balance;
    Context activity;
    DialogLoader dialogLoader;
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
        dialogLoader=new DialogLoader(getContext());
        verificationUtils = new RaveVerificationUtils( this, false, BuildConfig.PUBLIC_KEY);
        addMoneyImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateEntries()){
                    //initiateDepositWithExistingCard(card_id);
                    initiateDepositWithExistingCard();

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

    private boolean validateEntries() {

        if(spinner_select_card.getSelectedItem().toString().equalsIgnoreCase("Select Card")){
            Snackbar.make(addMoneyImg, "Please Select card", Snackbar.LENGTH_SHORT).show();
            return  false;
        }

        else if(addMoneyTxt.getText().toString().isEmpty()){
            Snackbar.make(addMoneyImg, "Please Enter amount", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        else {
            return  true;
        }



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


    public void initiateDepositWithExistingCard(){

        String amountEntered = addMoneyTxt.getText().toString();

        double amount = Double.parseDouble(amountEntered);

        txRef= "EPT"+WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID,this.activity)+(new Date().getTime());
        Log.e("REFFERROR : ", txRef+" : "+expiryDate.substring(0,2)+" : "+expiryDate.substring(3,5)+" : "+card_no);


        String eMaishaPayServiceMail="info@cabraltech.com";
        RaveUiManager raveNonUIManager = new RaveUiManager(requireActivity()).
                 setAmount(amount)
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




//        cardPayManager = new CardPaymentManager(
//                raveNonUIManager, this, this);

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
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<WalletTransaction> call = apiRequests.creditUser(access_token,null,amount,txRef,"Deposit","flutterwave",thirdparty_id, false,request_id,category,"creditUserAfterTransaction");
        call.enqueue(new Callback<WalletTransaction>() {
            @Override
            public void onResponse(Call<WalletTransaction> call, Response<WalletTransaction> response) {
                dialogLoader.hideProgressDialog();

                if(response.code() == 200){
                    refreshActivity();
                }else if(response.code() == 401){

                    TokenAuthFragment.startAuth( true);
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
                dialogLoader.hideProgressDialog();
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
            Log.w("UnknownResult",".......Unknown Result ");
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
                dialogLoader.showProgressDialog();
            } else {
                dialogLoader.hideProgressDialog();
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



}

