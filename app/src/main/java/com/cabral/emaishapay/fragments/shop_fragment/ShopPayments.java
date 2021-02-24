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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.SupportedCardTypesView;
import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.Shop.CartAdapter;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.database.DbHandlerSingleton;
import com.cabral.emaishapay.database.User_Cart_BuyInputsDB;
import com.cabral.emaishapay.models.WalletTransaction;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.ugmobilemoney.UgandaMobileMoneyPaymentCallback;
import com.flutterwave.raveandroid.rave_presentation.ugmobilemoney.UgandaMobileMoneyPaymentManager;
import com.flutterwave.raveutils.verification.RaveVerificationUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopPayments extends Fragment {

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
               // proceedOrder();


            } else if (MobileMoney.isChecked()) {
                selectedPaymentMethod = "Mobile Money";
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
                .setPhoneNumber("0" + phoneNumber)
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
                Log.e("MobileMoneypaymentError", errorMessage);
            }

            @Override
            public void onSuccessful(String flwRef) {
                dialogLoader.hideProgressDialog();
                Log.e("Success code :", flwRef);
                Toast.makeText(context, "Transaction Successful", Toast.LENGTH_LONG).show();
                creditAfterDeposit(flwRef,amount);
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


    public void creditAfterDeposit(String txRef, double amount) {
        /******************RETROFIT IMPLEMENTATION**************************/
        dialogLoader.showProgressDialog();

        String referenceNumber = txRef;
        String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;

        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<WalletTransaction> call = apiRequests.creditUser(access_token,userId,amount,referenceNumber);
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

                        Log.e("info", "Something got very very wrong, code: " + response.code());
                    }
                    Log.e("info 500", String.valueOf(response.errorBody()) + ", code: " + response.code());
                } else if (response.code() == 400) {
                    if (response.errorBody() != null) {
                        Toast.makeText(context, response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    } else {

                        Log.e("info", "Something got very very wrong, code: " + response.code());
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

}