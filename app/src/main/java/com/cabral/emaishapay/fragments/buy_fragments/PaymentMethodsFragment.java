package com.cabral.emaishapay.fragments.buy_fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;


import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.SupportedCardTypesView;
import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletBuySellActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.database.User_Info_BuyInputsDB;
import com.cabral.emaishapay.fragments.wallet_fragments.TokenAuthFragment;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.models.CardSpinnerItem;
import com.cabral.emaishapay.models.WalletTransaction;
import com.cabral.emaishapay.models.address_model.AddressDetails;
import com.cabral.emaishapay.models.order_model.PostOrder;
import com.cabral.emaishapay.models.payment_model.GetBrainTreeToken;
import com.cabral.emaishapay.models.user_model.UserDetails;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.card.Card;
import com.flutterwave.raveandroid.rave_presentation.card.CardPaymentCallback;
import com.flutterwave.raveandroid.rave_presentation.card.CardPaymentManager;
import com.flutterwave.raveandroid.rave_presentation.ugmobilemoney.UgandaMobileMoneyPaymentCallback;
import com.flutterwave.raveandroid.rave_presentation.ugmobilemoney.UgandaMobileMoneyPaymentManager;
import com.flutterwave.raveutils.verification.RaveVerificationUtils;
import com.google.android.material.snackbar.Snackbar;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentMethodsFragment extends Fragment implements CardPaymentCallback {
    private static final String TAG = "PaymentMethodsFragment";
    private View rootView;
    private RadioButton  eMaishaWallet, eMaishaCard, Visa, MobileMoney,COD;
    private LinearLayout merchantCard, VisaCard, MobileM;
    private EditText cardNumber, cardExpiry, cvv,cardHolderName,mobilePhonenumber;
    Button continuePayment, cancel;
    private static final CardType[] SUPPORTED_CARD_TYPES = {CardType.VISA, CardType.MASTERCARD,
            CardType.UNIONPAY};//,  CardType.MAESTRO,CardType.AMEX
    CardType cardType;
    private BraintreeFragment braintreeFragment;
    SupportedCardTypesView brainTreeSupportedCards;
    private String selectedPaymentMethod, merchantWalletId, shipping, orderId;
    private CardBuilder brainTreeCard;
    private List couponList, productList;
    private Double subtotal, total, tax, shipping_cost, discount;
    private String paymentNonceToken = "";
    private UserDetails userInfo;
    private My_Cart my_cart;
    private String brainTreeToken;
    private DialogLoader dialogLoader;
    private Toolbar toolbar;
    private ConstraintLayout codLayout,ePayLayout,eCardLayout,bankLayout,mmLayout;

    private AddressDetails shippingAddress;
    User_Info_BuyInputsDB user_info_BuyInputs_db = new User_Info_BuyInputsDB();


    String txRef;
    Context context;
    double chargeAmount;
    Spinner spinner_select_card;
    LinearLayout card_details_layout;
    CheckBox checkbox_save_card;
    private CardPaymentManager cardPayManager;
    private RaveVerificationUtils verificationUtils;
    private List<CardResponse.Cards> cardlists = new ArrayList();
    ArrayList<CardSpinnerItem> cardItems = new ArrayList<>();

    private String expiryDate,cvv_,card_no;

    public PaymentMethodsFragment(My_Cart my_cart, String merchantWalletId, String shipping, Double tax, Double shipping_cost,
                                  Double discount, List couponList, Double subtotal, Double total, List productList, String orderId) {
        // Required empty public constructor
        this.my_cart = my_cart;
        this.merchantWalletId = merchantWalletId;
        this.shipping = shipping;
        this.tax = tax;
        this.shipping_cost = shipping_cost;
        this.discount = discount;
        this.couponList = couponList;
        this.subtotal = subtotal;
        this.total = total;
        this.productList = productList;
        this.orderId = orderId;
        this.chargeAmount=this.total;
    }
    public PaymentMethodsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_payment_methods, container, false);
        // Inflate the layout for this fragment
        shippingAddress = ((EmaishaPayApp) requireContext().getApplicationContext()).getShippingAddress();
        userInfo = user_info_BuyInputs_db.getUserData(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext()));
        dialogLoader = new DialogLoader(getContext());
        eMaishaWallet = rootView.findViewById(R.id.radio_btn_emaisha_wallet);
        eMaishaCard = rootView.findViewById(R.id.radio_btn_merchant_card);
        Visa = rootView.findViewById(R.id.radio_btn_visa_card);
        MobileMoney = rootView.findViewById(R.id.radio_btn_mobile_money);
        merchantCard = rootView.findViewById(R.id.layout_merchant_card_details);
        VisaCard = rootView.findViewById(R.id.layout_visa_card_details);
        MobileM = rootView.findViewById(R.id.layout_mobile_money_number);
        continuePayment = rootView.findViewById(R.id.btn_payment_methods);
        cardExpiry = rootView.findViewById(R.id.visa_card_expiry);
        cardNumber = rootView.findViewById(R.id.visa_card_number);
        cvv = rootView.findViewById(R.id.visa_card_cvv);
        cardHolderName = rootView.findViewById(R.id.visa_holder_name);
        mobilePhonenumber = rootView.findViewById(R.id.mobile_money_phone_number);
        brainTreeSupportedCards = rootView.findViewById(R.id.supported_card_types);
        checkbox_save_card = rootView.findViewById(R.id.checkbox_save_card);
        spinner_select_card = rootView.findViewById(R.id.spinner_select_card);
        card_details_layout = rootView.findViewById(R.id.card_details_layout);
        verificationUtils = new RaveVerificationUtils( this, false, BuildConfig.PUBLIC_KEY);

        codLayout = rootView.findViewById(R.id.layout_cod);
        COD = rootView.findViewById(R.id.radio_btn_cash_on_delivery);
        ePayLayout = rootView.findViewById(R.id.layout_emaisha_pay);
        eCardLayout = rootView.findViewById(R.id.layout_merchant_card);
        mmLayout = rootView.findViewById(R.id.layout_mobile_money);
        bankLayout = rootView.findViewById(R.id.layout_bank_card);

        toolbar = rootView.findViewById(R.id.toolbar_payment_methods);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        toolbar.setTitle(getString(R.string.payment_methods));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        cancel = rootView.findViewById(R.id.payments_cancel_btn);
        // Handle the Click event of checkout_cancel_btn Button
        cancel.setOnClickListener(view -> requireActivity().getSupportFragmentManager().popBackStack());

        codLayout.setOnClickListener(v -> {
            COD.setChecked(true);
            eMaishaWallet.setChecked(false);
            MobileMoney.setChecked(false);
            eMaishaCard.setChecked(false);
            Visa.setChecked(false);
            MobileM.setVisibility(v.GONE);
            VisaCard.setVisibility(v.GONE);
            merchantCard.setVisibility(v.GONE);
        });

        ePayLayout.setOnClickListener(v -> {
            COD.setChecked(false);
            eMaishaWallet.setChecked(true);
            MobileMoney.setChecked(false);
            eMaishaCard.setChecked(false);
            Visa.setChecked(false);
            MobileM.setVisibility(v.GONE);
            VisaCard.setVisibility(v.GONE);
            merchantCard.setVisibility(v.GONE);
        });

        eCardLayout.setOnClickListener(v -> {
            merchantCard.setVisibility(v.VISIBLE);
            eMaishaCard.setChecked(true);
            eMaishaWallet.setChecked(false);
            MobileMoney.setChecked(false);
            Visa.setChecked(false);
            MobileM.setVisibility(v.GONE);
            VisaCard.setVisibility(v.GONE);
            COD.setChecked(false);

        });

        bankLayout.setOnClickListener(v -> {
            VisaCard.setVisibility(v.VISIBLE);
            eMaishaWallet.setChecked(false);
            eMaishaCard.setChecked(false);
            MobileMoney.setChecked(false);
            Visa.setChecked(true);
            MobileM.setVisibility(v.GONE);
            merchantCard.setVisibility(v.GONE);
            COD.setChecked(false);

        });

        mmLayout.setOnClickListener(v -> {

            MobileM.setVisibility(v.VISIBLE);
            eMaishaWallet.setChecked(false);
            eMaishaCard.setChecked(false);
            Visa.setChecked(false);
            MobileMoney.setChecked(true);
            VisaCard.setVisibility(v.GONE);
            merchantCard.setVisibility(v.GONE);
            COD.setChecked(false);

        });
        //radiobuttons

        COD.setOnClickListener(v -> {
           // COD.setChecked(true);
            eMaishaWallet.setChecked(false);
            MobileMoney.setChecked(false);
            eMaishaCard.setChecked(false);
            Visa.setChecked(false);
            MobileM.setVisibility(v.GONE);
            VisaCard.setVisibility(v.GONE);
            merchantCard.setVisibility(v.GONE);
        });

        eMaishaWallet.setOnClickListener(v -> {
            COD.setChecked(false);
           // eMaishaWallet.setChecked(true);
            MobileMoney.setChecked(false);
            eMaishaCard.setChecked(false);
            Visa.setChecked(false);
            MobileM.setVisibility(v.GONE);
            VisaCard.setVisibility(v.GONE);
            merchantCard.setVisibility(v.GONE);
        });

        eMaishaCard.setOnClickListener(v -> {
            merchantCard.setVisibility(v.VISIBLE);
           // eMaishaCard.setChecked(true);
            eMaishaWallet.setChecked(false);
            MobileMoney.setChecked(false);
            Visa.setChecked(false);
            MobileM.setVisibility(v.GONE);
            VisaCard.setVisibility(v.GONE);
            COD.setChecked(false);

        });

        Visa.setOnClickListener(v -> {
            VisaCard.setVisibility(v.VISIBLE);
            eMaishaWallet.setChecked(false);
            eMaishaCard.setChecked(false);
            MobileMoney.setChecked(false);
            //Visa.setChecked(true);
            MobileM.setVisibility(v.GONE);
            merchantCard.setVisibility(v.GONE);
            COD.setChecked(false);

        });

        MobileMoney.setOnClickListener(v -> {

            MobileM.setVisibility(v.VISIBLE);
            eMaishaWallet.setChecked(false);
            eMaishaCard.setChecked(false);
            Visa.setChecked(false);
           // MobileMoney.setChecked(true);
            VisaCard.setVisibility(v.GONE);
            merchantCard.setVisibility(v.GONE);
            COD.setChecked(false);

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

        this.context=getContext();
        txRef= WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID,this.context)+(new Date().getTime());

        continuePayment.setOnClickListener(v -> {
            if (eMaishaWallet.isChecked()) {
                double balance= Double.parseDouble( WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_USER_BALANCE, context) );
                if(balance>=this.total){
                    selectedPaymentMethod = "eMaisha Wallet";
                    recordEmaishaPayPurchase(txRef,this.chargeAmount);
                }else {
                    Toasty.error(context, "Insufficient Balance", Toast.LENGTH_LONG).show();
                }
            }else if (COD.isChecked()) {
                //
                selectedPaymentMethod = "cod";
                proceedOrder(false);
//                GenerateBrainTreeToken();
            } else if (eMaishaCard.isChecked()) {
                selectedPaymentMethod = "eMaisha Card";

                //if(validateSelectedPaymentMethod())
                    makeCardPayment();

                // validateSelectedPaymentMethod();
            }
            else if (Visa.isChecked()) {
                //save card info
                selectedPaymentMethod = "Visa";
                if(spinner_select_card.getSelectedItem().toString().equalsIgnoreCase("Select Card")){
                    Snackbar.make(continuePayment, getString(R.string.invalid_payment_token), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                else if(spinner_select_card.getSelectedItem().toString().equalsIgnoreCase("Add New")){

                    card_no = cardNumber.getText().toString();
                    cvv_ = cvv.getText().toString();
                    expiryDate = cardExpiry.getText().toString();
                }

                if(checkbox_save_card.isChecked()){
                    //save card
                    if (validateEntries()) {

                        String identifier = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
                        String cvv_ = cvv.getText().toString().trim();
                        String expiry = cardExpiry.getText().toString();
                        String account_name = cardHolderName.getText().toString();
                        String card_number = cardNumber.getText().toString();


                        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
                        String request_id = WalletHomeActivity.generateRequestId();
                        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
                        /*************RETROFIT IMPLEMENTATION**************/
                        Call<CardResponse> call = APIClient.getWalletInstance(getContext()).
                                saveCardInfo(access_token,
                                        identifier,
                                        card_number,
                                        cvv_,
                                        expiry,
                                        account_name,getString(R.string.currency),
                                        request_id,category,"saveCard");

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

                    makeCardPayment();
                }
                else{
                    makeCardPaymentWithExistingCard();
                }

            }
            else if (MobileMoney.isChecked()) {
                selectedPaymentMethod = "Mobile Money";

                String mobileNumber=getString(R.string.phone_number_code)+mobilePhonenumber.getText().toString();
                if(mobileNumber.length()>9){
                    mobileMoneyChargePayment(mobileNumber);
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
                            cvv_ = cardItems.get(i).getCvv();

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
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public boolean validateEntries(){

        if (cardHolderName.getText().toString().trim() == null || cardHolderName.getText().toString().trim().isEmpty()) {
            cardHolderName.setError("Please enter valid value");
            return false;

        } else if (cardNumber.getText().toString().trim() == null || cardNumber.getText().toString().trim().isEmpty()
                || cardNumber.getText().toString().trim().length()<13 ){
            cardNumber.setError("Please enter valid value");
            return false;
        }

        else if (cardExpiry.getText().toString().trim() == null || cardExpiry.getText().toString().trim().isEmpty()){
            cardExpiry.setError("Please select valid value");
            return false;
        }

        else if (cvv.getText().toString().trim() == null || cvv.getText().toString().trim().isEmpty() || cvv.getText().toString().trim().length()<3 ){
            cvv.setError("Please enter valid value");
            return false;
        }else {

            return true;
        }





    }

    private void proceedOrder(boolean isPaymentMade) {
        PostOrder orderDetails = new PostOrder();

        // Set Customer Info
        orderDetails.setPaymentMade(isPaymentMade);
        orderDetails.setCustomersId(Integer.parseInt(userInfo.getId()));
        orderDetails.setCustomersName(userInfo.getFirstName());
        orderDetails.setCustomersTelephone(shippingAddress.getPhone());
        orderDetails.setEmail(userInfo.getEmail());

        // Set Shipping  Info
        orderDetails.setDeliveryFirstname(shippingAddress.getFirstname());
        orderDetails.setDeliveryLastname(shippingAddress.getLastname());
        orderDetails.setDeliveryStreetAddress(shippingAddress.getStreet());
        orderDetails.setDeliveryPostcode(shippingAddress.getPostcode());
        orderDetails.setDeliveryPhone(shippingAddress.getPhone());
        orderDetails.setDeliverySuburb(shippingAddress.getSuburb());
        orderDetails.setDeliveryCity(shippingAddress.getCity());
        orderDetails.setDeliveryZone(shippingAddress.getZoneName());
        orderDetails.setDeliveryState(shippingAddress.getZoneName());
        orderDetails.setDeliverySuburb(shippingAddress.getZoneName());
        orderDetails.setDeliveryCountry(shippingAddress.getCountryName());
        orderDetails.setDeliveryZoneId(String.valueOf(shippingAddress.getZoneId()));
        orderDetails.setDeliveryCountryId(String.valueOf(shippingAddress.getCountriesId()));
        orderDetails.setDeliveryTime("" + shippingAddress.getDelivery_time());
        orderDetails.setDeliveryCost("" + shipping);
        orderDetails.setPackingChargeTax(ConstantValues.PACKING_CHARGE);

        // LatLang
        orderDetails.setLatitude(String.valueOf(shippingAddress.getLatitude()));
        orderDetails.setLongitude(String.valueOf(shippingAddress.getLongitude()));

        orderDetails.setLanguageId(ConstantValues.LANGUAGE_ID);

        orderDetails.setTaxZoneId(shippingAddress.getZoneId());
        orderDetails.setTotalTax(tax);
        orderDetails.setShippingMethod(getString(R.string.default_shipping_method));
        orderDetails.setShippingCost(shipping_cost);


//        orderDetails.setComments(comments);

        if (couponList.size() > 0) {
            orderDetails.setIsCouponApplied(1);
        } else {
            orderDetails.setIsCouponApplied(0);
        }
        orderDetails.setCouponAmount(discount);
        orderDetails.setCoupons(couponList);

        // Set PaymentNonceToken and PaymentMethod
        orderDetails.setNonce(paymentNonceToken);
        orderDetails.setPaymentMethod(selectedPaymentMethod);

        // Set CheckoutFinal Price and Products
        orderDetails.setProductsTotal(subtotal);
        orderDetails.setTotalPrice(total);
        orderDetails.setProducts(productList);
        orderDetails.setOrder_payment_id(orderId);

        orderDetails.setCurrency("UGX");

//        PlaceOrderNow(orderDetails);
        WalletBuySellActivity.postOrder = orderDetails;
        requireActivity().getSupportFragmentManager().popBackStack();

    }

    private void GenerateBrainTreeToken() {
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        Call<GetBrainTreeToken> call = BuyInputsAPIClient.getInstance()
                .generateBraintreeToken(access_token);

        call.enqueue(new Callback<GetBrainTreeToken>() {
            @Override
            public void onResponse(Call<GetBrainTreeToken> call, retrofit2.Response<GetBrainTreeToken> response) {

                dialogLoader.hideProgressDialog();

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        brainTreeToken = response.body().getToken();

                        // Initialize BrainTreeFragment with BrainTreeToken
                        try {
                            braintreeFragment = BraintreeFragment.newInstance(getActivity(), brainTreeToken);
                        } catch (InvalidArgumentException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Snackbar.make(rootView, getString(R.string.cannot_initialize_braintree), Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("BRAINTREE TOKEN CALL", "onFailure: \"NetworkCallFailure : \"" + R.string.cannot_initialize_braintree);
                }
            }

            @Override
            public void onFailure(Call<GetBrainTreeToken> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Log.d("BRAINTREE TOKEN CALL", "onFailure: \"NetworkCallFailure : \"+t");
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Hide Cart Icon in the Toolbar

//        MenuItem searchItem = menu.findItem(R.id.toolbar_ic_search);
        MenuItem cartItem = menu.findItem(R.id.ic_cart_item);

//        searchItem.setVisible(false);
        cartItem.setVisible(false);
    }


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
        recordPurchase(flwRef,this.chargeAmount,cardNumber.getText().toString());

    }

    @Override
    public void collectAddress() {
        Toast.makeText(getContext(), "Submitting address details", Toast.LENGTH_SHORT).show();
        verificationUtils.showAddressScreen();
    }

    @Override
    public void showAuthenticationWebPage(String authenticationUrl) {
         //Log.w("Loading auth web page: ",authenticationUrl);
        verificationUtils.showWebpageVerificationScreen(authenticationUrl);
    }

    public void mobileMoneyChargePayment(String phoneNumber) {

        dialogLoader.showProgressDialog();

        txRef = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, this.context) + (new Date().getTime());
        String eMaishaPayServiceMail="info@cabraltech.com";

        RaveNonUIManager raveNonUIManager = new RaveNonUIManager().setAmount(this.chargeAmount)
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
                Log.e("Success code :", "Mobile Money Payment "+flwRef);
                Toast.makeText(context, "Transaction Successful", Toast.LENGTH_LONG).show();

                recordPurchase(flwRef,chargeAmount,cardNumber.getText().toString());

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

    public void recordPurchase(String txRef, double amount, String thirdParty_id) {
        /******************RETROFIT IMPLEMENTATION**************************/
        dialogLoader.showProgressDialog();

        String referenceNumber = txRef;
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());

        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<WalletTransaction> call = apiRequests.thirdpartyCreditUser(access_token,merchantWalletId,amount,referenceNumber,"flutterwave",thirdParty_id,true,request_id,category,"creditUserAfterTransaction");
        call.enqueue(new Callback<WalletTransaction>() {
            @Override
            public void onResponse(Call<WalletTransaction> call, Response<WalletTransaction> response) {
                if(response.code() == 200){

                    dialogLoader.hideProgressDialog();

                    proceedOrder(true);
                } else if(response.code() == 401){

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

    public void recordEmaishaPayPurchase(String txRef, double amount) {
        /******************RETROFIT IMPLEMENTATION**************************/
        dialogLoader.showProgressDialog();

        String referenceNumber = txRef;
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());

        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<WalletTransaction> call = apiRequests.eMaishaPayUserPayment(access_token,merchantWalletId,amount,referenceNumber,true,request_id,category,"userEmaishaPayPaymentment");
        call.enqueue(new Callback<WalletTransaction>() {
            @Override
            public void onResponse(Call<WalletTransaction> call, Response<WalletTransaction> response) {
                if(response.code() == 200){

                    dialogLoader.hideProgressDialog();
                    proceedOrder(true);
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


    public void makeCardPayment(){
        String expiryDate=cardExpiry.getText().toString();
        double amount = Double.parseDouble(this.chargeAmount+"");

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
                raveNonUIManager, this, null);

        com.flutterwave.raveandroid.rave_presentation.card.Card card = new com.flutterwave.raveandroid.rave_presentation.card.Card(
                cardNumber.getText().toString(),
                expiryDate.substring(0,2),
                expiryDate.substring(3,5),
                cvv.getText().toString()
        );

        cardPayManager.chargeCard(card);
        //cardPayManager.onWebpageAuthenticationComplete();

    }

    public void makeCardPaymentWithExistingCard(){
        double amount = Double.parseDouble(this.chargeAmount+"");

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
                raveNonUIManager, this, null);

        com.flutterwave.raveandroid.rave_presentation.card.Card card = new Card(
                card_no,
                expiryDate.substring(0,2),
                expiryDate.substring(3,5),
                cvv_
        );

        cardPayManager.chargeCard(card);
        //cardPayManager.onWebpageAuthenticationComplete();

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

                            if( cardlists.get(i).getCard_number().length()>4){
                                final  String card_number = cardlists.get(i).getCard_number();
                                final  String  decripted_expiryDate = cardlists.get(i).getExpiry();
                                final  String cvv  = cardlists.get(i).getCvv();
                                final  String id  = cardlists.get(i).getId();

                                String first_four_digits = (card_number.substring(0,  4));
                                String last_four_digits = (card_number.substring(card_number.length() - 4));
                                final String decripted_card_number = first_four_digits + "*******"+last_four_digits;
                                //   //Log.w("CardNumber","**********>>>>"+decripted_card_number);
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
                        if(getContext()!=null){
                            ArrayAdapter<CardSpinnerItem> cardListAdapter = new ArrayAdapter<CardSpinnerItem>(getContext(),  android.R.layout.simple_dropdown_item_1line, cardItems);
//                        cardListAdapter = new CardSpinnerAdapter(cardItems, "New", getContext());
                            spinner_select_card.setAdapter(cardListAdapter);
                            dialogLoader.hideProgressDialog();
                        }

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
}