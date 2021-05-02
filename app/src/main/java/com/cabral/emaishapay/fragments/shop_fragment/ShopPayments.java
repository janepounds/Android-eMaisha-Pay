package com.cabral.emaishapay.fragments.shop_fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.SupportedCardTypesView;
import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.DailogFragments.AddCardFragment;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.AuthActivity;
import com.cabral.emaishapay.activities.ShopActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.customs.OtpDialogLoader;
import com.cabral.emaishapay.fragments.wallet_fragments.TokenAuthFragment;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.models.CardSpinnerItem;
import com.cabral.emaishapay.models.InitiateTransferResponse;
import com.cabral.emaishapay.models.InitiateWithdrawResponse;
import com.cabral.emaishapay.models.WalletTransaction;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;
import com.cabral.emaishapay.utils.CryptoUtil;
import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.card.Card;
import com.flutterwave.raveandroid.rave_presentation.card.CardPaymentCallback;
import com.flutterwave.raveandroid.rave_presentation.card.CardPaymentManager;
import com.flutterwave.raveandroid.rave_presentation.card.SavedCardsListener;
import com.flutterwave.raveandroid.rave_presentation.data.AddressDetails;
import com.flutterwave.raveandroid.rave_presentation.ugmobilemoney.UgandaMobileMoneyPaymentCallback;
import com.flutterwave.raveandroid.rave_presentation.ugmobilemoney.UgandaMobileMoneyPaymentManager;
import com.flutterwave.raveutils.verification.AVSVBVFragment;
import com.flutterwave.raveutils.verification.OTPFragment;
import com.flutterwave.raveutils.verification.PinFragment;
import com.flutterwave.raveutils.verification.RaveVerificationUtils;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopPayments extends Fragment implements

        SavedCardsListener, CardPaymentCallback {
    private static final String TAG = "ShopPayments";

    private Context context;
    ConstraintLayout layoutCod;

    private RadioButton eMaishaWallet, eMaishaCard, Visa, MobileMoney;
    private LinearLayout merchantCard, VisaCard, MobileM,eMaishaPayLayout,cardDetails;
    private EditText cardNumber, cardExpiry, cvv, monileMoneyPhoneEdtx,emaishapay_phone_number,account_name;
    Button continuePayment,cancel;
    TextView  resendtxtview, tvTimer;
    private static final CardType[] SUPPORTED_CARD_TYPES = {CardType.VISA, CardType.MASTERCARD,
            CardType.UNIONPAY};//,  CardType.MAESTRO,CardType.AMEX
    CardType cardType;
   // private BraintreeFragment braintreeFragment;
    private String selectedPaymentMethod,cardNo,cvv_,expiry,mobileNo;
    SupportedCardTypesView brainTreeSupportedCards;
    private Toolbar toolbar;
    private DialogLoader dialogLoader;
    String txRef;
    double chargeAmount;
    private CardPaymentManager cardPayManager;
    private OtpDialogLoader otpDialogLoader;

    private RaveVerificationUtils verificationUtils;
    private Dialog dialog;
    private Spinner spinner_select_card;
    private List<CardResponse.Cards> cardlists = new ArrayList();
    String card_number,decripted_expiryDate,acc_name;
    ArrayList<CardSpinnerItem> cardItems = new ArrayList<>();
    private CheckBox checkbox_save_card;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View rootView = inflater.inflate(R.layout.fragment_payment_methods, container, false);
        ShopActivity.bottomNavigationView.setVisibility(View.GONE);
        this.chargeAmount=getArguments().getDouble("Charge");
        toolbar = rootView.findViewById(R.id.toolbar_payment_methods);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(R.string.payment_method);
        dialogLoader = new DialogLoader(getContext());

        eMaishaWallet = rootView.findViewById(R.id.radio_btn_emaisha_wallet);
        layoutCod= rootView.findViewById(R.id.layout_cod);
        layoutCod.setVisibility(View.GONE);
        eMaishaCard = rootView.findViewById(R.id.radio_btn_merchant_card);
        Visa = rootView.findViewById(R.id.radio_btn_visa_card);
        MobileMoney = rootView.findViewById(R.id.radio_btn_mobile_money);
        merchantCard = rootView.findViewById(R.id.layout_merchant_card_details);
        VisaCard = rootView.findViewById(R.id.layout_visa_card_details);
        MobileM = rootView.findViewById(R.id.layout_mobile_money_number);
        eMaishaPayLayout = rootView.findViewById(R.id.layout_emaishapay_account);
        emaishapay_phone_number = rootView.findViewById(R.id.emaishapay_phone_number);
        monileMoneyPhoneEdtx = rootView.findViewById(R.id.mobile_money_phone_number);
        continuePayment = rootView.findViewById(R.id.btn_payment_methods);
        cardExpiry = rootView.findViewById(R.id.visa_card_expiry);
        cardNumber = rootView.findViewById(R.id.visa_card_number);
        cvv = rootView.findViewById(R.id.visa_card_cvv);
        spinner_select_card = rootView.findViewById(R.id.spinner_select_card);
        cancel = rootView.findViewById(R.id.payments_cancel_btn);
        cardDetails = rootView.findViewById(R.id.card_details_layout);
        checkbox_save_card = rootView.findViewById(R.id.checkbox_save_card);
        account_name = rootView.findViewById(R.id.visa_holder_name);

        brainTreeSupportedCards = rootView.findViewById(R.id.supported_card_types);
        dialogLoader = new DialogLoader(context);

        eMaishaWallet.setOnClickListener(v -> {

            MobileMoney.setChecked(false);
            eMaishaCard.setChecked(false);
            Visa.setChecked(false);
            MobileM.setVisibility(v.GONE);
            VisaCard.setVisibility(v.GONE);
            merchantCard.setVisibility(v.GONE);
            eMaishaPayLayout.setVisibility(v.VISIBLE);
        });

        eMaishaCard.setOnClickListener(v -> {
            merchantCard.setVisibility(v.VISIBLE);
            eMaishaWallet.setChecked(false);
            MobileMoney.setChecked(false);
            Visa.setChecked(false);
            MobileM.setVisibility(v.GONE);
            VisaCard.setVisibility(v.GONE);
            eMaishaPayLayout.setVisibility(v.GONE);
        });

        Visa.setOnClickListener(v -> {
            VisaCard.setVisibility(v.VISIBLE);
            eMaishaWallet.setChecked(false);
            eMaishaCard.setChecked(false);
            MobileMoney.setChecked(false);
            MobileM.setVisibility(v.GONE);
            merchantCard.setVisibility(v.GONE);
            eMaishaPayLayout.setVisibility(v.GONE);
            getCards();
        });

        MobileMoney.setOnClickListener(v -> {
            MobileM.setVisibility(v.VISIBLE);
            eMaishaWallet.setChecked(false);
            eMaishaCard.setChecked(false);
            Visa.setChecked(false);
            VisaCard.setVisibility(v.GONE);
            merchantCard.setVisibility(v.GONE);
            eMaishaPayLayout.setVisibility(v.GONE);
        });
        cancel.setOnClickListener(v -> {
            ShopActivity.navController.popBackStack();});


        spinner_select_card.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinner_select_card.getSelectedItem().toString().equalsIgnoreCase("Add New")){
                    //call add card
                    cardDetails.setVisibility(View.VISIBLE);

                }
                else {
                    for(int i = 0; i<cardItems.size();i++){
                        if(cardItems.get(i).toString().equalsIgnoreCase(spinner_select_card.getSelectedItem().toString())){
                            expiry =  cardItems.get(i).getExpiryDate();
                            cardNo = cardItems.get(i).getCardNumber();
                            cvv_ = cardItems.get(i).getCvv();

                        }
                    }
                    // Log.d(TAG, "onItemSelected: expiry"+expiryDate+"card_no"+card_no+"cvv"+cvv);
                    cardDetails.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        brainTreeSupportedCards.setSupportedCardTypes(SUPPORTED_CARD_TYPES);

        cardExpiry.setKeyListener(null);

        cardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(cardNumber.getText().toString().trim())) {
                    CardType type = CardType.forCardNumber(cardNumber.getText().toString());
                    if (cardType != type) {
                        cardType = type;

                        InputFilter[] filters = {new InputFilter.LengthFilter(cardType.getMaxCardLength())};
                        cardNumber.setFilters(filters);
                        cardNumber.invalidate();

                        brainTreeSupportedCards.setSelected(cardType);
                    }
                } else {
                    brainTreeSupportedCards.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        cardExpiry.setOnTouchListener((v, event) -> {

            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Get Calendar instance
                final Calendar calendar = Calendar.getInstance();

                // Initialize DateSetListener of DatePickerDialog
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        // Set the selected Date Info to Calendar instance
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);

                        // Set Date Format
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy", Locale.US);

                        // Set Date in input_dob EditText
                        cardExpiry.setText(dateFormat.format(calendar.getTime()));
                    }
                };


                // Initialize DatePickerDialog
                DatePickerDialog datePicker = new DatePickerDialog
                        (
                                getContext(),
                                date,
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                        );

                // Show datePicker Dialog
                datePicker.show();
            }

            return false;
        });

        this.txRef = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, this.context) + (new Date().getTime());
        verificationUtils = new RaveVerificationUtils(this, false, BuildConfig.PUBLIC_KEY);

        continuePayment.setOnClickListener(v -> {
            if (eMaishaWallet.isChecked()) {
                selectedPaymentMethod = "eMaisha Wallet";
                if(validateEmaishaPay()) {

                    preparecomfirmAcceptPayment(
                            getString(R.string.phone_number_code) + emaishapay_phone_number.getText().toString(),
                            chargeAmount);
                }

//              GenerateBrainTreeToken();
            } else if (eMaishaCard.isChecked()) {
                selectedPaymentMethod = "eMaisha Card";
                //proceedOrder();
                // validateSelectedPaymentMethod();
            } else if (Visa.isChecked()) {
                selectedPaymentMethod = "Visa";
                if(spinner_select_card.getSelectedItem().toString().equalsIgnoreCase("Select Card")){
                    Snackbar.make(continuePayment, getString(R.string.invalid_payment_token), Snackbar.LENGTH_SHORT).show();
                    return ;
                }else if( spinner_select_card.getSelectedItem().toString().equalsIgnoreCase("Add New")) {
                    if (validateEntries()) {
                        //save card info
                        cardNo = cardNumber.getText().toString();
                        cvv_ = cvv.getText().toString();
                        expiry = cardExpiry.getText().toString();
                        acc_name = account_name.getText().toString();
                        if(checkbox_save_card.isChecked()){
                            saveCard(cardNo,cvv_,expiry,acc_name);
                        }
//                        mobileNo = m.getText().toString();
                        initiateCardCharge(this.chargeAmount,expiry);
                    }
                }else {

                        initiateCardCharge(this.chargeAmount,expiry);
                }


            } else if (MobileMoney.isChecked()) {
                if(validateMobileMoney()) {
                    selectedPaymentMethod = "Mobile Money";
                    Log.w("eMaishaMM", chargeAmount + " " + getString(R.string.phone_number_code) + monileMoneyPhoneEdtx.getText().toString());
                    initiateMobileMoneyCharge(getString(R.string.phone_number_code) + monileMoneyPhoneEdtx.getText().toString(), chargeAmount);
                    //proceedOrder();
                }
            }
        });

        return rootView;
    }

    private void saveCard(String cardNumber, String cvv_, String expiry, String account_name) {
        dialogLoader.showProgressDialog();
        CryptoUtil encrypter = new CryptoUtil(BuildConfig.ENCRYPTION_KEY, getString(R.string.iv));
        String hash_card_number = encrypter.encrypt(cardNumber);
        String hash_cvv = encrypter.encrypt(cvv_);
        String hash_expiry = encrypter.encrypt(expiry);
        String identifier = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
        String hash_account_name = encrypter.encrypt(account_name);
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
            /*************RETROFIT IMPLEMENTATION**************/
            Call<CardResponse> call = APIClient.getWalletInstance(getContext()).saveCardInfo(access_token,identifier, hash_card_number, hash_cvv, hash_expiry, hash_account_name,request_id,category,"saveCard");
            call.enqueue(new Callback<CardResponse>() {
                @Override
                public void onResponse(Call<CardResponse> call, Response<CardResponse> response) {
                    if (response.isSuccessful()) {
                        dialogLoader.hideProgressDialog();
                        if(response.body().getStatus()==1) {
                            String message = response.body().getMessage();
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();

                        }

                    }
                }

                @Override
                public void onFailure(Call<CardResponse> call, Throwable t) {
                    dialogLoader.hideProgressDialog();

                }
            });


    }

    public void getCards(){
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        /******************RETROFIT IMPLEMENTATION***********************/
        dialogLoader.showProgressDialog();
        Call<CardResponse> call = APIClient.getWalletInstance(getContext()).getCards(access_token,request_id,category,"getCards");
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
                        for(int i =0; i<cardlists.size();i++) {
                            //decript card number
                            CryptoUtil encrypter = new CryptoUtil(BuildConfig.ENCRYPTION_KEY, getContext().getString(R.string.iv));


                            card_number = encrypter.decrypt(cardlists.get(i).getCard_number());
                            decripted_expiryDate = encrypter.decrypt(cardlists.get(i).getExpiry());
                            String cvv = encrypter.decrypt(cardlists.get(i).getCvv());
                            Log.d(TAG, "onResponse: decripter_card_no" + card_number);

                            if (card_number.length() > 4) {

                                String first_four_digits = (card_number.substring(0, 4));
                                String last_four_digits = (card_number.substring(card_number.length() - 4));
                                final String decripted_card_number = first_four_digits + "*******" + last_four_digits;

                                Log.d(TAG, "onResponse: masked " + decripted_card_number);
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
                        Log.d(TAG, "onResponse: cards"+cardItems);

                        ArrayAdapter<CardSpinnerItem> cardListAdapter = new ArrayAdapter<CardSpinnerItem>(getContext(),  android.R.layout.simple_dropdown_item_1line, cardItems);
//                        cardListAdapter = new CardSpinnerAdapter(cardItems, "New", getContext());
                        spinner_select_card.setAdapter(cardListAdapter);
                        dialogLoader.hideProgressDialog();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }



                }else if (response.code() == 401) {

                    TokenAuthFragment.startAuth( true);

                    if (response.errorBody() != null) {
                        Log.e("info", new String(String.valueOf(response.errorBody())));
                    } else {
                        Log.e("info", "Something got very very wrong");
                    }
                    dialogLoader.hideProgressDialog();
                }

            }

            @Override
            public void onFailure(Call<CardResponse> call, Throwable t) {
                dialogLoader.hideProgressDialog();
            }
        });

    }

    private boolean validateMobileMoney() {
        if(monileMoneyPhoneEdtx.getText().toString().isEmpty()){
            monileMoneyPhoneEdtx.setText("Required!");
            return false;

        }else {
            return true;
        }

    }

    private boolean validateEmaishaPay() {
        if(emaishapay_phone_number.getText().toString().isEmpty()){
            emaishapay_phone_number.setText("Required!");
            return false;

        }else {
            return true;
        }


    }


    public void initiateMobileMoneyCharge(String phoneNumber,double amount) {

        dialogLoader.showProgressDialog();

        txRef = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, this.context) + (new Date().getTime());
        String eMaishaPayServiceMail="info@cabraltech.com";

        RaveNonUIManager raveNonUIManager = new RaveNonUIManager().setAmount(amount)
                .setCurrency("UGX")
                .setEmail(eMaishaPayServiceMail)
                .setfName(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME, this.context))
                .setlName(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_LAST_NAME, this.context))
                .setPhoneNumber(phoneNumber)
                .setNarration("eMaisha Pay")
                .setPublicKey(BuildConfig.PUBLIC_KEY)
                .setEncryptionKey(BuildConfig.ENCRYPTION_KEY)
                .setTxRef(txRef)
                .onStagingEnv(false)
                .isPreAuth(true)
                .initialize();

        UgandaMobileMoneyPaymentCallback mobileMoneyPaymentCallback = new UgandaMobileMoneyPaymentCallback() {
            @Override
            public void showProgressIndicator(boolean active) {
                try {

                    if (dialogLoader == null) {
                        dialogLoader = new DialogLoader(getContext());
                        dialogLoader.showProgressDialog();
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage, @Nullable String flwRef) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                Log.e("MobileMoneypaymentError", errorMessage);
            }

            @Override
            public void onSuccessful(String flwRef) {
                dialogLoader.hideProgressDialog();
                Log.e("Success code :", flwRef);
                Toast.makeText(context, "Transaction Successful", Toast.LENGTH_LONG).show();
                creditAfterSale(flwRef,amount,phoneNumber);
            }

            @Override
            public void showAuthenticationWebPage(String authenticationUrl) {
                Log.e("Loading auth web page: ", authenticationUrl);
                verificationUtils.showWebpageVerificationScreen(authenticationUrl);
            }
        };
        UgandaMobileMoneyPaymentManager mobilePayManager = new UgandaMobileMoneyPaymentManager(raveNonUIManager, (UgandaMobileMoneyPaymentCallback) mobileMoneyPaymentCallback);

        mobilePayManager.charge();
    }

    public void initiateCardCharge(double amount, String expiryDate){

//        String expiryDate=cardExpiry.getText().toString();
        Log.d(TAG, "initiateCardCharge: expiry"+expiryDate);

        txRef= WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID,this.context)+(new Date().getTime());



        String eMaishaPayServiceMail="info@cabraltech.com";
        RaveNonUIManager raveNonUIManager = new RaveNonUIManager().setAmount(amount)
                .setCurrency("UGX")
                .setEmail( eMaishaPayServiceMail )
                .setfName( WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME,this.context) )
                .setlName( WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_LAST_NAME,this.context) )
                .setPhoneNumber("+256"+ WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_PHONE_NUMBER,this.context).substring(1))
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
                cardNumber.getText().toString(),
                expiryDate.substring(0,2),
                expiryDate.substring(3,5),
                cvv.getText().toString()
        );

        cardPayManager.chargeCard(card);
        //cardPayManager.onWebpageAuthenticationComplete();

    }

    public void creditAfterSale(String txRef, double amount, String thirdParty_id) {
        /******************RETROFIT IMPLEMENTATION**************************/
        dialogLoader.showProgressDialog();

        String referenceNumber = txRef;
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());

        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<WalletTransaction> call = apiRequests.creditUser(access_token,null,amount,referenceNumber,"External Purchase","flutterwave",thirdParty_id,false,request_id,category,"creditUserAfterTransaction" );
        call.enqueue(new Callback<WalletTransaction>() {
            @Override
            public void onResponse(Call<WalletTransaction> call, Response<WalletTransaction> response) {
                if(response.code() == 200){

                    dialogLoader.hideProgressDialog();

                    refreshActivity();
                }else if(response.code() == 401){

                    TokenAuthFragment.startAuth( true);

                } else if (response.code() == 500) {
                    if (response.errorBody() != null) {
                        Toast.makeText(context,response.body().getRecepient(), Toast.LENGTH_LONG).show();
                    } else {

                        Log.e("info", "Something got very wrong, code: " + response.code());
                    }
                    Log.e("info 500", String.valueOf(response.errorBody()) + ", code: " + response.code());
                } else if (response.code() == 400) {
                    if (response.errorBody() != null) {
                        Toast.makeText(context, response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    } else {

                        Log.e("info", "Something got very wrong, code: " + response.code());
                    }
                    Log.e("info 500", String.valueOf(response.errorBody()) + ", code: " + response.code());
                } else if (response.code() == 406) {
                    if (response.errorBody() != null) {

                        Toast.makeText(context, response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    } else {

                        Log.e("info", "Something got very very wrong, code: " + response.code());
                    }
                    Log.e("info 406", String.valueOf(response.errorBody()) + ", code: " + response.code());
                } else {

                    if (response.errorBody() != null) {

                        Toast.makeText(context, response.errorBody().toString(), Toast.LENGTH_LONG).show();
                        Log.e("info", String.valueOf(response.errorBody()) + ", code: " + response.code());
                    } else {

                        Log.e("info", "Something got very very wrong, code: " + response.code());
                    }
                }
                dialogLoader.hideProgressDialog();
            }


            @Override
            public void onFailure(Call<WalletTransaction> call, Throwable t) {

            }
        });


    }


    public void refreshActivity() {
        Intent goToWallet = new Intent(getActivity(), WalletHomeActivity.class);
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
            otpDialogLoader.onActivityResult(requestCode, resultCode, data);
        }


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
        Toast.makeText(context, "Card Saved Successfully", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onCardSaveFailed(String message) {
        Log.e("Error: ", message);
    }
    //add card payment listeners
    @Override
    public void showProgressIndicator(boolean active) {
        try {

           dialogLoader.showProgressDialog();
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
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(String errorMessage, @Nullable String flwRef) {
        Log.e("VisapaymentError",errorMessage);
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccessful(String flwRef) {
        Log.e("Success code :",flwRef);
        Toast.makeText(context, "Transaction Successful", Toast.LENGTH_LONG).show();
        creditAfterSale(flwRef,this.chargeAmount,cardNumber.getText().toString());
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

         if (cardNumber.getText().toString().trim() == null || cardNumber.getText().toString().trim().isEmpty()  || cardNumber.getText().toString().trim().length()<13 ){
            cardNumber.setError("Please enter valid value");
            return false;
        }

        else if (cardExpiry.getText().toString().trim() == null || cardExpiry.getText().toString().trim().isEmpty()){
            cardExpiry.setError("Please select valid value");
            return false;
        }

        else if (cvv.getText().toString().trim() == null || cvv.getText().toString().trim().isEmpty()
                || cvv.getText().toString().trim().length()<3 ){
            cvv.setError("Please enter valid value");
            return false;
        }else {
            return true;
        }

    }

    public void initiateAcceptPayment(final String phoneNumber, final double amount,String service_code) {

        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        dialogLoader.showProgressDialog();


        String type;
        if(category.equalsIgnoreCase("merchant")){
            type="Merchant Accept Payment";
        }else{
            type = "Agent Transfer";

        }

        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<InitiateTransferResponse> call = apiRequests.initiateAgentTransaction(access_token, amount,phoneNumber,type,request_id,category,"customerTransactionOTP",service_code);
        call.enqueue(new Callback<InitiateTransferResponse>() {
            @Override
            public void onResponse(Call<InitiateTransferResponse> call, Response<InitiateTransferResponse> response) {
                if(response.code() ==200){
                    dialogLoader.hideProgressDialog();

                   otpDialogLoader=new OtpDialogLoader(ShopPayments.this) {
                       @Override
                       protected void onConfirmOtp(String otp_code, Dialog otpDialog) {
                           otpDialog.dismiss();
                           comfirmAcceptPayment(
                                   otp_code,
                                   phoneNumber,
                                   amount,
                                   service_code);
                       }

                       @Override
                       protected void onResendOtp() {
                           otpDialogLoader.resendOtp(
                                   phoneNumber,
                                   dialogLoader,
                                   continuePayment

                           );
                       }
                   };

                    otpDialogLoader.showOTPDialog();


                }
                else if(response.code() == 401) {
                    TokenAuthFragment.startAuth( true);
                    getActivity().finish();
                }
                else if(response.code() == 401) {
                    TokenAuthFragment.startAuth( true);

                }
                else if (response.code() == 500) {
                    Log.e("info 500", new String(String.valueOf(response.errorBody())) + ", code: " + response.code());
                } else if (response.code() == 400) {
                    Log.e("info 400", new String(String.valueOf(response.errorBody()) + ", code: " + response.code()));
                } else if (response.code() == 406) {

                    Log.e("info 406", new String(String.valueOf(response.errorBody())) + ", code: " + response.code());
                } else {
                    Log.e("info 406", new String("Error Occurred Try again later"));

                }
                dialogLoader.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<InitiateTransferResponse> call, Throwable t) {
                Log.e("info : " , new String(String.valueOf(t.getMessage())));

                dialogLoader.hideProgressDialog();
            }
        });

    }

    public void preparecomfirmAcceptPayment(final String customerNumber, final double amount) {

        //Inner Dialog to enter PIN
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
        //LayoutInflater inflater = requireActivity().getLayoutInflater();
        View pinDialog = View.inflate(context,R.layout.dialog_enter_pin,null);




        EditText pinEdittext =pinDialog.findViewById(R.id.etxt_create_agent_pin);
        TextView dialog_title = pinDialog.findViewById(R.id.dialog_title);
        dialog_title.setText("ENTER MERCHANT PIN");

        pinDialog.findViewById(R.id.txt_custom_add_agent_submit_pin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !TextUtils.isEmpty(pinEdittext.getText()) && pinEdittext.getText().length()==4){
                    String service_code =WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION+ pinEdittext.getText().toString() ;
                    initiateAcceptPayment(
                            getString(R.string.phone_number_code)+emaishapay_phone_number.getText().toString(),
                            chargeAmount,
                            service_code
                            );


                }else {
                    pinEdittext.setError("Invalid Pin Entered");
                }

            }
        });
        builder.setView(pinDialog);
        Dialog dialog = builder.create();
        builder.setCancelable(false);
        dialog.show();


    }

    private void comfirmAcceptPayment(final String OTPCode,final String customerNumber, final double amount,String service_code){

        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        dialogLoader.showProgressDialog();
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        String receiverPhoneNumber = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_PHONE_NUMBER,requireContext());

        /*****RETROFIT IMPLEMENTATION*****/
        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<InitiateWithdrawResponse> call = apiRequests.confirmAcceptPayment(access_token, amount,customerNumber,receiverPhoneNumber,OTPCode,request_id,category,"confirmAgentPayment",service_code);
        call.enqueue(new Callback<InitiateWithdrawResponse>() {
            @Override
            public void onResponse(Call<InitiateWithdrawResponse> call, Response<InitiateWithdrawResponse> response) {
                if(response.code() ==200){
                    if(response.body().getStatus().equalsIgnoreCase("1")) {
                        dialogLoader.hideProgressDialog();
                        Toast.makeText(context,response.body().getMessage(),Toast.LENGTH_LONG).show();
                        refreshActivity();
                    }else{
                        Toast.makeText(context,response.body().getMessage(),Toast.LENGTH_LONG).show();
                    }

                }
                else if(response.code() == 401) {
                    TokenAuthFragment.startAuth( true);

                }
                else if (response.code() == 500) {
                    Log.e("info 500", new String(String.valueOf(response.errorBody())) + ", code: " + response.code());
                } else if (response.code() == 400) {
                    Log.e("info 400", new String(String.valueOf(response.errorBody()) + ", code: " + response.code()));
                } else if (response.code() == 406) {

                    Log.e("info 406", new String(String.valueOf(response.errorBody())) + ", code: " + response.code());
                } else {
                    Log.e("info 406", new String("Error Occurred Try again later"));

                }
                dialogLoader.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<InitiateWithdrawResponse> call, Throwable t) {
                Log.e("info 406", new String("Error Occurred Try again later"));
                dialogLoader.hideProgressDialog();
            }
        });

    }

}