package com.cabral.emaishapay.fragments.buy_fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;


import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.Card;
import com.braintreepayments.api.exceptions.BraintreeError;
import com.braintreepayments.api.exceptions.ErrorWithResponse;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.ConfigurationListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.SupportedCardTypesView;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.database.User_Cart_BuyInputsDB;
import com.cabral.emaishapay.database.User_Info_BuyInputsDB;
import com.cabral.emaishapay.models.address_model.AddressDetails;
import com.cabral.emaishapay.models.order_model.OrderData;
import com.cabral.emaishapay.models.order_model.PostOrder;
import com.cabral.emaishapay.models.payment_model.GetBrainTreeToken;
import com.cabral.emaishapay.models.user_model.UserDetails;
import com.cabral.emaishapay.network.BuyInputsAPIClient;
import com.cabral.emaishapay.utils.NotificationHelper;
import com.cabral.emaishapay.utils.ValidateInputs;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class PaymentMethodsFragment extends Fragment {
    private static final String TAG = "PaymentMethodsFragment";
    private View rootView;
    private RadioButton cashOnDelivery, eMaishaWallet, eMaishaCard, Visa, MobileMoney;
    private LinearLayout merchantCard, VisaCard, MobileM;
    private EditText cardNumber, cardExpiry, cvv;
    Button continuePayment;
    private static final CardType[] SUPPORTED_CARD_TYPES = {CardType.VISA, CardType.MASTERCARD, CardType.MAESTRO,
            CardType.UNIONPAY, CardType.AMEX};
    CardType cardType;
    private BraintreeFragment braintreeFragment;
    SupportedCardTypesView brainTreeSupportedCards;
    private String selectedPaymentMethod, shop_id, shipping, orderId;
    private CardBuilder brainTreeCard;
    private List couponList, productList;
    private Double subtotal, total, tax, shipping_cost, discount;
    private String paymentNonceToken = "";
    private ProgressDialog progressDialog;
    private UserDetails userInfo;
    private My_Cart my_cart;
    final User_Cart_BuyInputsDB user_cart_BuyInputs_db;
    private String brainTreeToken;
    private DialogLoader dialogLoader;

    private AddressDetails shippingAddress;
    User_Info_BuyInputsDB user_info_BuyInputs_db = new User_Info_BuyInputsDB();

    public PaymentMethodsFragment(My_Cart my_cart, User_Cart_BuyInputsDB user_cart_BuyInputs_db, String merchantId, String shipping, Double tax, Double shipping_cost,
                                  Double discount, List couponList, Double subtotal, Double total, List productList, String orderId) {
        // Required empty public constructor
        this.my_cart = my_cart;
        this.user_cart_BuyInputs_db = user_cart_BuyInputs_db;
        this.shop_id = merchantId;
        this.shipping = shipping;
        this.tax = tax;
        this.shipping_cost = shipping_cost;
        this.discount = discount;
        this.couponList = couponList;
        this.subtotal = subtotal;
        this.total = total;
        this.productList = productList;
        this.orderId = orderId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_payment_methods, container, false);
        // Inflate the layout for this fragment
        shippingAddress = ((EmaishaPayApp) requireContext().getApplicationContext()).getShippingAddress();
        userInfo = user_info_BuyInputs_db.getUserData(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext()));
        dialogLoader = new DialogLoader(getContext());
        cashOnDelivery = rootView.findViewById(R.id.radio_btn_cash_on_delivery);
        eMaishaWallet = rootView.findViewById(R.id.radio_btn_emaisha_wallet);
        eMaishaCard = rootView.findViewById(R.id.radio_btn_merchant_card);
        Visa = rootView.findViewById(R.id.radio_btn_visa_card);
        MobileMoney = rootView.findViewById(R.id.radio_btn_mobile_money);
        merchantCard = rootView.findViewById(R.id.layout_merchant_card_details);
        VisaCard = rootView.findViewById(R.id.layout_visa_card_details);
        MobileM = rootView.findViewById(R.id.layout_mobile_money_number);
        continuePayment = rootView.findViewById(R.id.btn_payment_methods);
        cardExpiry = rootView.findViewById(R.id.visa_card_expiry);
        cardNumber = rootView.findViewById(R.id.txt_visa_card_number);
        cvv = rootView.findViewById(R.id.visa_card_cvv);
        brainTreeSupportedCards = rootView.findViewById(R.id.supported_card_types);

        cashOnDelivery.setOnClickListener(v -> {
            MobileMoney.setChecked(false);
            eMaishaWallet.setChecked(false);
            eMaishaCard.setChecked(false);
            Visa.setChecked(false);
            MobileM.setVisibility(v.GONE);
            VisaCard.setVisibility(v.GONE);
            merchantCard.setVisibility(v.GONE);
        });

        eMaishaWallet.setOnClickListener(v -> {
            cashOnDelivery.setChecked(false);
            MobileMoney.setChecked(false);
            eMaishaCard.setChecked(false);
            Visa.setChecked(false);
            MobileM.setVisibility(v.GONE);
            VisaCard.setVisibility(v.GONE);
            merchantCard.setVisibility(v.GONE);
        });

        eMaishaCard.setOnClickListener(v -> {
            merchantCard.setVisibility(v.VISIBLE);
            cashOnDelivery.setChecked(false);
            eMaishaWallet.setChecked(false);
            MobileMoney.setChecked(false);
            Visa.setChecked(false);
            MobileM.setVisibility(v.GONE);
            VisaCard.setVisibility(v.GONE);
        });

        Visa.setOnClickListener(v -> {
            VisaCard.setVisibility(v.VISIBLE);
            cashOnDelivery.setChecked(false);
            eMaishaWallet.setChecked(false);
            eMaishaCard.setChecked(false);
            MobileMoney.setChecked(false);
            MobileM.setVisibility(v.GONE);
            merchantCard.setVisibility(v.GONE);
        });

        MobileMoney.setOnClickListener(v -> {
            MobileM.setVisibility(v.VISIBLE);
            cashOnDelivery.setChecked(false);
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

//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setTitle(getString(R.string.processing));
//        progressDialog.setMessage(getString(R.string.please_wait));
//        progressDialog.setCancelable(false);

        continuePayment.setOnClickListener(v -> {
            if (cashOnDelivery.isChecked()) {
                //navigate to thank you
                selectedPaymentMethod = "cod";
                proceedOrder();

            } else if (eMaishaWallet.isChecked()) {
                //
                selectedPaymentMethod = "eMaisha Wallet";
                proceedOrder();
//                GenerateBrainTreeToken();
            } else if (eMaishaCard.isChecked()) {
                selectedPaymentMethod = "eMaisha Card";
                proceedOrder();
                // validateSelectedPaymentMethod();
            } else if (Visa.isChecked()) {
                //save card info
                selectedPaymentMethod = "Visa";
                proceedOrder();

//                if (validatePaymentCard()) {
//                    // Setup Payment Method
//                    validateSelectedPaymentMethod();
//
//                    // Delay of 2 seconds
//                    new Handler().postDelayed(() -> {
//                        if (!"".equalsIgnoreCase(paymentNonceToken)) {
//                            // Proceed Order
//                            proceedOrder();
//                        } else {
//                            Snackbar.make(v, getString(R.string.invalid_payment_token), Snackbar.LENGTH_SHORT).show();
//                        }
//                    }, 2000);
//                }

            } else if (MobileMoney.isChecked()) {
                selectedPaymentMethod = "Mobile Money";
                proceedOrder();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void validateSelectedPaymentMethod() {
        if (selectedPaymentMethod.equalsIgnoreCase("Visa")) {
            // Initialize BrainTreeCard
            brainTreeCard = new CardBuilder()
                    .cardNumber(cardNumber.getText().toString().trim())
                    .expirationDate(cardExpiry.getText().toString().trim())
                    .cvv(cvv.getText().toString().trim());

            // Tokenize BrainTreeCard
            Card.tokenize(braintreeFragment, brainTreeCard);

            // Add PaymentMethodNonceCreatedListener to BrainTreeFragment
            braintreeFragment.addListener((PaymentMethodNonceCreatedListener) paymentMethodNonce -> {
                // Get Payment Nonce
                paymentNonceToken = paymentMethodNonce.getNonce();
            });

            // Add BrainTreeErrorListener to BrainTreeFragment
            braintreeFragment.addListener((BraintreeErrorListener) error -> {
                // Check if there was a Validation Error of provided Data
                if (error instanceof ErrorWithResponse) {
                    ErrorWithResponse errorWithResponse = (ErrorWithResponse) error;

                    BraintreeError cardNumberError = errorWithResponse.errorFor("number");
                    BraintreeError cardCVVErrors = errorWithResponse.errorFor("creditCard");
                    BraintreeError expirationMonthError = errorWithResponse.errorFor("expirationMonth");
                    BraintreeError expirationYearError = errorWithResponse.errorFor("expirationYear");

                    // Check if there is an Issue with the Credit Card
                    if (cardNumberError != null) {
                        cardNumber.setError(cardNumberError.getMessage());
                    } else if (expirationMonthError != null) {
                        cardExpiry.setError(expirationMonthError.getMessage());
                    } else if (expirationYearError != null) {
                        cardExpiry.setError(expirationYearError.getMessage());
                    } else if (cardCVVErrors != null) {
                        cvv.setError(cardCVVErrors.getMessage());
                    } else {
                        Toast.makeText(getContext(), errorWithResponse.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Add ConfigurationListener to BrainTreeFragment
            braintreeFragment.addListener((ConfigurationListener) configuration -> {
            });

            // Add BrainTreeCancelListener to BrainTreeFragment
            braintreeFragment.addListener((BraintreeCancelListener) requestCode -> {
            });

        } else if (selectedPaymentMethod.equalsIgnoreCase("payPal")) {
            return;

        } else if (selectedPaymentMethod.equalsIgnoreCase("instamojo")) {
            return;

        } else {
            return;
        }
    }

    private void proceedOrder() {
        PostOrder orderDetails = new PostOrder();

        // Set Customer Info
        orderDetails.setCustomersId(Integer.parseInt(userInfo.getId()));
        orderDetails.setCustomersName(userInfo.getFirstName());
        orderDetails.setCustomersTelephone(shippingAddress.getPhone());
        orderDetails.setEmail(userInfo.getEmail());
        orderDetails.setShopId(shop_id);

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
        WalletHomeActivity.postOrder = orderDetails;
        requireActivity().getSupportFragmentManager().popBackStack();
//        requireActivity().onBackPressed();
    }

    private void PlaceOrderNow(PostOrder postOrder) {

        String str = new Gson().toJson(postOrder);

        Call<OrderData> call = BuyInputsAPIClient.getInstance()
                .addToOrder
                        (
                                postOrder
                        );

        call.enqueue(new Callback<OrderData>() {
            @Override
            public void onResponse(Call<OrderData> call, retrofit2.Response<OrderData> response) {

//                progressDialog.dismiss();

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        Intent notificationIntent = new Intent(getContext(), WalletHomeActivity.class);
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        // Order has been placed Successfully
                        NotificationHelper.showNewNotification(getContext(), notificationIntent, getString(R.string.thank_you), response.body().getMessage(), null);

                        // Clear User's Cart
                        My_Cart.ClearCart();

                        // Clear User's Shipping and Billing info from AppContext
                        ((EmaishaPayApp) getContext().getApplicationContext()).setShippingAddress(new AddressDetails());
                        ((EmaishaPayApp) getContext().getApplicationContext()).setBillingAddress(new AddressDetails());


                        // Navigate to Thank_You Fragment
                        Fragment fragment = new Thank_You(my_cart);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.popBackStack(getString(R.string.actionHome), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fragmentManager.beginTransaction()
                                .add(R.id.main_fragment_container, fragment)
                                .addToBackStack(null)
                                .commit();


                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_LONG).show();

                    } else {
                        // Unable to get Success status
                        Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("Error:", response.message());
                }
            }

            @Override
            public void onFailure(Call<OrderData> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validatePaymentCard() {
        if (!ValidateInputs.isValidNumber(cardNumber.getText().toString().trim())) {
            cardNumber.setError(getString(R.string.invalid_credit_card));
            return false;
        } else if (!ValidateInputs.isValidNumber(cvv.getText().toString().trim())) {
            cvv.setError(getString(R.string.invalid_card_cvv));
            return false;
        } else if (TextUtils.isEmpty(cardExpiry.getText().toString().trim())) {
            cardExpiry.setError(getString(R.string.select_card_expiry));
            return false;
        } else {
            return true;
        }
    }

    private void GenerateBrainTreeToken() {
        Call<GetBrainTreeToken> call = BuyInputsAPIClient.getInstance()
                .generateBraintreeToken();

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
}