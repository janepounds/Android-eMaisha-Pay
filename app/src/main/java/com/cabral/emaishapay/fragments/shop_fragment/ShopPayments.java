package com.cabral.emaishapay.fragments.shop_fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.SupportedCardTypesView;
import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.models.WalletTransaction;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopPayments extends Fragment implements

        SavedCardsListener, CardPaymentCallback {

    List<String> customerNames, orderTypeNames, paymentMethodNames;
    ArrayAdapter<String> customerAdapter, orderTypeAdapter, paymentMethodAdapter;
    private Context context;
    AlertDialog alertDialog;
    String order_type, order_payment_method, customer_name;

    private RadioButton eMaishaWallet, eMaishaCard, Visa, MobileMoney;
    private LinearLayout merchantCard, VisaCard, MobileM;
    private EditText cardNumber, cardExpiry, cvv, monileMoneyPhoneEdtx;
    Button continuePayment;
    private static final CardType[] SUPPORTED_CARD_TYPES = {CardType.VISA, CardType.MASTERCARD,
            CardType.UNIONPAY};//,  CardType.MAESTRO,CardType.AMEX
    CardType cardType;
    private BraintreeFragment braintreeFragment;
    private String selectedPaymentMethod;
    SupportedCardTypesView brainTreeSupportedCards;
    private Toolbar toolbar;
    private CardBuilder brainTreeCard;
    private String brainTreeToken;
    private DialogLoader dialogLoader;
    String txRef;
    double chargeAmount;
    private CardPaymentManager cardPayManager;


    private RaveVerificationUtils verificationUtils;

    public ShopPayments(double chargeAmount) {
        this.chargeAmount=chargeAmount;
    }

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

        toolbar = rootView.findViewById(R.id.toolbar_payment_methods);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(R.string.payment_method);
        dialogLoader = new DialogLoader(getContext());

        eMaishaWallet = rootView.findViewById(R.id.radio_btn_emaisha_wallet);
        eMaishaCard = rootView.findViewById(R.id.radio_btn_merchant_card);
        Visa = rootView.findViewById(R.id.radio_btn_visa_card);
        MobileMoney = rootView.findViewById(R.id.radio_btn_mobile_money);
        merchantCard = rootView.findViewById(R.id.layout_merchant_card_details);
        VisaCard = rootView.findViewById(R.id.layout_visa_card_details);
        MobileM = rootView.findViewById(R.id.layout_mobile_money_number);
        monileMoneyPhoneEdtx = rootView.findViewById(R.id.mobile_money_phone_number);
        continuePayment = rootView.findViewById(R.id.btn_payment_methods);
        cardExpiry = rootView.findViewById(R.id.visa_card_expiry);
        cardNumber = rootView.findViewById(R.id.txt_visa_card_number);
        cvv = rootView.findViewById(R.id.visa_card_cvv);
        brainTreeSupportedCards = rootView.findViewById(R.id.supported_card_types);


        eMaishaWallet.setOnClickListener(v -> {

            MobileMoney.setChecked(false);
            eMaishaCard.setChecked(false);
            Visa.setChecked(false);
            MobileM.setVisibility(v.GONE);
            VisaCard.setVisibility(v.GONE);
            merchantCard.setVisibility(v.GONE);
        });

        eMaishaCard.setOnClickListener(v -> {
            merchantCard.setVisibility(v.VISIBLE);
            eMaishaWallet.setChecked(false);
            MobileMoney.setChecked(false);
            Visa.setChecked(false);
            MobileM.setVisibility(v.GONE);
            VisaCard.setVisibility(v.GONE);
        });

        Visa.setOnClickListener(v -> {
            VisaCard.setVisibility(v.VISIBLE);
            eMaishaWallet.setChecked(false);
            eMaishaCard.setChecked(false);
            MobileMoney.setChecked(false);
            MobileM.setVisibility(v.GONE);
            merchantCard.setVisibility(v.GONE);
        });

        MobileMoney.setOnClickListener(v -> {
            MobileM.setVisibility(v.VISIBLE);
            eMaishaWallet.setChecked(false);
            eMaishaCard.setChecked(false);
            Visa.setChecked(false);
            VisaCard.setVisibility(v.GONE);
            merchantCard.setVisibility(v.GONE);
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
                //proceedOrder();
//              GenerateBrainTreeToken();
            } else if (eMaishaCard.isChecked()) {
                selectedPaymentMethod = "eMaisha Card";
                //proceedOrder();
                // validateSelectedPaymentMethod();
            } else if (Visa.isChecked()) {
                //save card info
                selectedPaymentMethod = "Visa";
                if(validateEntries())
                initiateCardCharge(this.chargeAmount);


            } else if (MobileMoney.isChecked()) {
                selectedPaymentMethod = "Mobile Money";
                Log.w("eMaishaMM",chargeAmount+" "+"0"+monileMoneyPhoneEdtx.getText().toString());
                initiateMobileMoneyCharge( "0"+monileMoneyPhoneEdtx.getText().toString(),chargeAmount);
                //proceedOrder();
            }
        });


        return rootView;
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

    public void initiateCardCharge(double amount){

        String expiryDate=cardExpiry.getText().toString();

        txRef= WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID,this.context)+(new Date().getTime());
        Log.e("PUBK : ", BuildConfig.PUBLIC_KEY+" : "+expiryDate.substring(0,2)+" : "+expiryDate.substring(3,5));


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
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;

        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<WalletTransaction> call = apiRequests.creditUser(access_token,null,amount,referenceNumber,"External Purchase","flutterwave",thirdParty_id);
        call.enqueue(new Callback<WalletTransaction>() {
            @Override
            public void onResponse(Call<WalletTransaction> call, Response<WalletTransaction> response) {
                if(response.code() == 200){

                    dialogLoader.hideProgressDialog();

                    refreshActivity();
                }else if(response.code() == 401){

                    TokenAuthActivity.startAuth(getActivity(), true);
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

         if (cardNumber.getText().toString().trim() == null || cardNumber.getText().toString().trim().isEmpty()
                || cardNumber.getText().toString().trim().length()<13 ){
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

}