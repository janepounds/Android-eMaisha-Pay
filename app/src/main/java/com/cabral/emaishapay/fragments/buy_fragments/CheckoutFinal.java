package com.cabral.emaishapay.fragments.buy_fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Bundle;


import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.Card;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.exceptions.BraintreeError;
import com.braintreepayments.api.exceptions.ErrorWithResponse;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.ConfigurationListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.BraintreeRequestCodes;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.api.models.Configuration;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletBuySellActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.buyInputsAdapters.CheckoutItemsAdapter;
import com.cabral.emaishapay.adapters.buyInputsAdapters.CouponsAdapter;
import com.cabral.emaishapay.adapters.buyInputsAdapters.DemoCouponsListAdapter;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.database.User_Cart_BuyInputsDB;
import com.cabral.emaishapay.database.User_Info_BuyInputsDB;
import com.cabral.emaishapay.databinding.BuyInputsCheckoutBinding;
import com.cabral.emaishapay.models.address_model.AddressDetails;
import com.cabral.emaishapay.models.cart_model.CartProduct;
import com.cabral.emaishapay.models.cart_model.CartProductAttributes;
import com.cabral.emaishapay.models.coupons_model.CouponsData;
import com.cabral.emaishapay.models.coupons_model.CouponsInfo;
import com.cabral.emaishapay.models.order_model.OrderData;
import com.cabral.emaishapay.models.order_model.PostOrder;
import com.cabral.emaishapay.models.order_model.PostProducts;
import com.cabral.emaishapay.models.order_model.PostProductsAttributes;
import com.cabral.emaishapay.models.payment_model.GetBrainTreeToken;
import com.cabral.emaishapay.models.payment_model.PaymentMethodsInfo;
import com.cabral.emaishapay.models.product_model.Option;
import com.cabral.emaishapay.models.product_model.Value;
import com.cabral.emaishapay.models.user_model.UserDetails;
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient;
import com.cabral.emaishapay.utils.NotificationHelper;
import com.cabral.emaishapay.utils.Utilities;
import com.cabral.emaishapay.utils.ValidateInputs;
import com.google.android.material.snackbar.Snackbar;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;


import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;



import am.appwise.components.ni.NoInternetDialog;

import retrofit2.Callback;
import retrofit2.Call;

public class CheckoutFinal extends Fragment {
    private static final String TAG = "CheckoutFinal";

    AlertDialog demoCouponsDialog;
    boolean disableOtherCoupons = false;

    private PostOrder paymentMethodsResult;

    String tax;
    String braintreeToken;
    String selectedPaymentMethod;
    String paymentNonceToken = "";
    double checkoutSubtotal, checkoutTax, packingCharges, checkoutShipping, checkoutShippingCost, checkoutDiscount, checkoutTotal = 0;
    EditText payment_name, payment_email, payment_phone, checkout_coupon_code, checkout_comments, checkout_card_number, checkout_card_cvv, checkout_card_expiry;
    TextView demo_coupons_text;

    List<CouponsInfo> couponsList;
    List<CartProduct> checkoutItemsList;
    List<PaymentMethodsInfo> paymentMethodsList;

    UserDetails userInfo;

    AddressDetails shippingAddress;
    CouponsAdapter couponsAdapter;
    CheckoutItemsAdapter checkoutItemsAdapter;

    User_Info_BuyInputsDB user_info_BuyInputs_db = new User_Info_BuyInputsDB();
    User_Cart_BuyInputsDB user_cart_BuyInputs_db;

    CardBuilder braintreeCard;
    BraintreeFragment braintreeFragment;

    private String PAYMENT_CURRENCY = "USD";
    private Context context;


    List<PostProducts> orderProductList;
    //Add order products name to String array
    List<String> productsName;

    //Add order id for payment methods
    String orderID, shop_id,merchant_wallet_id;
    My_Cart my_cart;
    PostOrder PaymentOrderDetails;
    DialogLoader dialogLoader;
    BuyInputsCheckoutBinding binding;


   public CheckoutFinal(){
       //no-args constructor
   }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.buy_inputs_checkout, container, false);


        // Set the Title of Toolbar
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarCheckout);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.checkout));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(getArguments()!=null){
            my_cart= (My_Cart) getArguments().getSerializable("my_cart");
            user_cart_BuyInputs_db= (User_Cart_BuyInputsDB) getArguments().getSerializable("db");
            shop_id = getArguments().getString("merchant_id");
            merchant_wallet_id = getArguments().getString("wallet_id");
        }

        // Get selectedShippingMethod, billingAddress and shippingAddress from ApplicationContext
        tax = ((EmaishaPayApp) getContext().getApplicationContext()).getTax();

        shippingAddress = ((EmaishaPayApp) getContext().getApplicationContext()).getShippingAddress();
        ((EmaishaPayApp) context.getApplicationContext()).getShippingService();

        // Get userInfo from Local Databases User_Info_DB
        userInfo = user_info_BuyInputs_db.getUserData(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext()));


        Log.d(TAG, "onCreateView: Payment Method = " + binding.paymentMethod.getText().toString());

        PAYMENT_CURRENCY = ConstantValues.CURRENCY_CODE;
        binding.checkoutItemsRecycler.setNestedScrollingEnabled(false);

        dialogLoader = new DialogLoader(getContext());

        couponsList = new ArrayList<>();
        paymentMethodsList = new ArrayList<>();
        // Get checkoutItems from Local Databases User_Cart_DB
        checkoutItemsList = user_cart_BuyInputs_db.getCartItems();


        productsName = new ArrayList<>();

        // Add orders to orderProductList
        addProductsToList();
        // Adding products name to array
        for (int i = 0; i < orderProductList.size(); i++) {
            productsName.add(orderProductList.get(i).getProductsName());
        }


        // Initialize the CheckoutItemsAdapter for RecyclerView
        checkoutItemsAdapter = new CheckoutItemsAdapter(context, checkoutItemsList);

        // Set the Adapter, LayoutManager and ItemDecoration to the RecyclerView
        binding.checkoutItemsRecycler.setAdapter(checkoutItemsAdapter);
        binding.checkoutItemsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.checkoutItemsRecycler.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        // Initialize the CouponsAdapter for RecyclerView
        couponsAdapter = new CouponsAdapter(getContext(), couponsList, true, CheckoutFinal.this);

        // Set the Adapter, LayoutManager and ItemDecoration to the RecyclerView
        binding.checkoutCouponsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.checkoutCouponsRecycler.setAdapter(couponsAdapter);

        couponsAdapter.notifyDataSetChanged();

        checkoutTax = Double.parseDouble(tax);
        if(ConstantValues.PACKING_CHARGE!=null)
            packingCharges = Double.parseDouble("" + ConstantValues.PACKING_CHARGE);
        else
            packingCharges=0;

        if( (shippingAddress.getLastname()==null || shippingAddress.getLastname().isEmpty() )  ){
            binding.shippingName.setText( shippingAddress.getFirstname());
        }else if( (shippingAddress.getFirstname()==null || shippingAddress.getFirstname().equalsIgnoreCase("null") )  ){
            binding.shippingName.setText( shippingAddress.getLastname());
        }else {
            binding.shippingName.setText(shippingAddress.getFirstname() + " " + shippingAddress.getLastname());
        }

        if( (shippingAddress.getCity()==null || shippingAddress.getCity().equalsIgnoreCase("null") )  ){
            binding.shippingAddress.setText( shippingAddress.getCountryName());
        }else{
            binding.shippingAddress.setText( shippingAddress.getCity()+", "+shippingAddress.getCountryName());
        }

        binding.shippingStreet.setText(shippingAddress.getStreet());

        // Set CheckoutFinal Total
        setCheckoutTotal();

        binding.paymentMethod.setOnClickListener(v -> {
            if(WalletBuySellActivity.navController.getCurrentDestination().getId()!=R.id.checkOutFinal)
            Log.w("CurrentDestination", WalletBuySellActivity.navController.getCurrentDestination().getLabel()+"" );

            //go to payment method
            Bundle bundle = new Bundle();
            bundle.putSerializable("my_cart",my_cart);
            bundle.putString("wallet_id",merchant_wallet_id);
            bundle.putString("shipping",binding.checkoutShippingCharge.getText().toString());
            bundle.putDouble("tax",checkoutTax);
            bundle.putDouble("shipping_cost",checkoutShipping);
            bundle.putDouble("discount",checkoutDiscount);
            bundle.putDouble("sub_total",checkoutSubtotal);
            bundle.putDouble("total",checkoutTotal);
            bundle.putSerializable("coupons", (ArrayList) couponsList);
            bundle.putSerializable("order_product_list", (ArrayList) orderProductList);
            bundle.putString("order_id",orderID);
            WalletBuySellActivity.navController.navigate(R.id.action_checkOutFinal_to_paymentMethods,bundle);

//            Fragment fragment = new PaymentMethodsFragment(my_cart, merchant_wallet_id, binding.checkoutShippingCharge.getText().toString(), checkoutTax, checkoutShipping,
//                    checkoutDiscount, couponsList, checkoutSubtotal, checkoutTotal, orderProductList, orderID);
//
//            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.nav_host_fragment2, fragment)
//                    .addToBackStack(getString(R.string.checkout)).commit();
        });


        binding.checkoutEditShipping.setOnClickListener(view -> {

            // Navigate to Shipping_Address Fragment to Edit ShippingAddress
            Bundle args = new Bundle();
            args.putBoolean("isUpdate", true);
            args.putSerializable("my_cart", my_cart);
            args.putSerializable("my_address", null);
            WalletBuySellActivity.navController.navigate(R.id.action_checkOutFinal_to_shippingAddress,args);

        });

        if (!ConstantValues.IS_CLIENT_ACTIVE) {
            setupDemoCoupons();
        } else {
//            demo_coupons_text.setVisibility(View.GONE);
        }

        // Handle the Click event of checkout_coupon_btn Button
        binding.checkoutCouponBtn.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(binding.checkoutCouponCode.getText().toString())) {
                GetCouponInfo(binding.checkoutCouponCode.getText().toString());
                dialogLoader.showProgressDialog();
            }
        });

        // Handle the Click event of checkout_cancel_btn Button
        binding.checkoutCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //got to home fragment
                WalletBuySellActivity.navController.navigate(R.id.action_checkOutFinal_to_walletBuyFragment);

            }
        });

        // Handle the Click event of checkout_order_btn Button
        binding.checkoutOrderBtn.setOnClickListener(view -> {

            if (binding.paymentMethod.getText().toString().equals("Payment Method")) {
                binding.paymentMethod.setError("Required");
            } else {
                if(PaymentOrderDetails.getPaymentMethod().equalsIgnoreCase("eMaisha Card") || PaymentOrderDetails.getPaymentMethod().equalsIgnoreCase("Visa")  ){
                    //check whether payment is made
                    if(PaymentOrderDetails.getPaymentMade()){
                        proceedOrder();
                    }
                }else{
                    //No payment Needed
                    proceedOrder();
                }

            }


        });

        return binding.getRoot();
    }

    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte[] messageDigest = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }

    private String getOrderID() {

        if ("".equalsIgnoreCase(orderID)) {
            Calendar c = Calendar.getInstance();
            double mseconds = c.get(Calendar.MILLISECOND);
            orderID = String.valueOf(mseconds);
        }
        return orderID;
    }

    //*********** Called when the fragment is no longer in use ********//

    @Override
    public void onDestroy() {
//        getContext().stopService(new Intent(getContext(), PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        PaymentOrderDetails = WalletBuySellActivity.postOrder;
        selectedPaymentMethod=PaymentOrderDetails.getPaymentMethod();
        Log.d(TAG, "onResume: Method = " + selectedPaymentMethod);
        Log.d(TAG, "onResume: Is Payment made "+PaymentOrderDetails.getPaymentMade());
        if (PaymentOrderDetails.getPaymentMethod() != null) {

            binding.paymentMethod.setText(PaymentOrderDetails.getPaymentMethod());
        }
        WalletBuySellActivity.bottomNavigationView.setVisibility(View.GONE);
    }

    //*********** Receives the result from a previous call of startActivityForResult(Intent, int) ********//

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
            if (requestCode == BraintreeRequestCodes.PAYPAL) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                // use the result to update your UI and send the payment method nonce to your server
                if (!TextUtils.isEmpty(result.getPaymentMethodNonce().getNonce())) {
                    selectedPaymentMethod = "braintree_paypal";
                    paymentNonceToken = result.getPaymentMethodNonce().getNonce();
                }

            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("VC_Shop", "[paypal] > The user canceled.");
        }

    }

    //*********** Validate Payment method Details according to the selectedPaymentMethod ********//

    private void validateSelectedPaymentMethod() {

        if (selectedPaymentMethod.equalsIgnoreCase("braintree_card")) {

            // Initialize BraintreeCard
            braintreeCard = new CardBuilder()
                    .cardNumber(checkout_card_number.getText().toString().trim())
                    .expirationDate(checkout_card_expiry.getText().toString().trim())
                    .cvv(checkout_card_cvv.getText().toString().trim());

            // Tokenize BraintreeCard
            Card.tokenize(braintreeFragment, braintreeCard);


            // Add PaymentMethodNonceCreatedListener to BraintreeFragment
            braintreeFragment.addListener(new PaymentMethodNonceCreatedListener() {
                @Override
                public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {

                    // Get Payment Nonce
                    paymentNonceToken = paymentMethodNonce.getNonce();
                }
            });


            // Add BraintreeErrorListener to BraintreeFragment
            braintreeFragment.addListener(new BraintreeErrorListener() {
                @Override
                public void onError(Exception error) {

                    // Check if there was a Validation Error of provided Data
                    if (error instanceof ErrorWithResponse) {
                        ErrorWithResponse errorWithResponse = (ErrorWithResponse) error;

                        BraintreeError cardNumberError = errorWithResponse.errorFor("number");
                        BraintreeError cardCVVErrors = errorWithResponse.errorFor("creditCard");
                        BraintreeError expirationMonthError = errorWithResponse.errorFor("expirationMonth");
                        BraintreeError expirationYearError = errorWithResponse.errorFor("expirationYear");

                        // Check if there is an Issue with the Credit Card
                        if (cardNumberError != null) {
                            checkout_card_number.setError(cardNumberError.getMessage());
                        } else if (expirationMonthError != null) {
                            checkout_card_expiry.setError(expirationMonthError.getMessage());
                        } else if (expirationYearError != null) {
                            checkout_card_expiry.setError(expirationYearError.getMessage());
                        } else if (cardCVVErrors != null) {
                            checkout_card_cvv.setError(cardCVVErrors.getMessage());
                        } else {
                            Toast.makeText(getContext(), errorWithResponse.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });


            // Add ConfigurationListener to BraintreeFragment
            braintreeFragment.addListener(new ConfigurationListener() {
                @Override
                public void onConfigurationFetched(Configuration configuration) {
                }
            });

            // Add BraintreeCancelListener to BraintreeFragment
            braintreeFragment.addListener(new BraintreeCancelListener() {
                @Override
                public void onCancel(int requestCode) {
                }
            });


        } else if (selectedPaymentMethod.equalsIgnoreCase("braintree_paypal")) {

            // Add PaymentMethodNonceCreatedListener to BraintreeFragment
            braintreeFragment.addListener(new PaymentMethodNonceCreatedListener() {
                @Override
                public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {

                    // Get Payment Nonce
                    paymentNonceToken = paymentMethodNonce.getNonce();
                    selectedPaymentMethod = "braintree_paypal";
                }
            });

            // Add BraintreeErrorListener to BraintreeFragment
            braintreeFragment.addListener(new BraintreeErrorListener() {
                @Override
                public void onError(Exception error) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            // Add BraintreeCancelListener to BraintreeFragment
            braintreeFragment.addListener(new BraintreeCancelListener() {
                @Override
                public void onCancel(int requestCode) {
                }
            });

        } else if (selectedPaymentMethod.equalsIgnoreCase("paypal")) {
            return;

        } else if (selectedPaymentMethod.equalsIgnoreCase("instamojo")) {
            return;

        } else {
            return;
        }

    }

    //*********** Returns Final Price of User's Cart ********//

    private double getProductsSubTotal() {

        double finalPrice = 0;

        for (int i = 0; i < this.checkoutItemsList.size(); i++) {
            // Add the Price of each Cart Product to finalPrice
            finalPrice += Double.parseDouble(this.checkoutItemsList.get(i).getCustomersBasketProduct().getTotalPrice());
        }

        return finalPrice;
    }

    //*********** Set CheckoutFinal's Subtotal, Tax, ShippingCost, Discount and Total Prices ********//

    private void setCheckoutTotal() {
        // Get Cart Total
        checkoutSubtotal = getProductsSubTotal();
        // Calculate CheckoutFinal Total
        checkoutTotal = checkoutSubtotal + checkoutTax + packingCharges + checkoutShipping - checkoutDiscount;

        // Set CheckoutFinal Details
//        checkout_tax.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutTax));
        binding.checkoutShippingCharge.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutShippingCost));
        binding.checkoutDiscount.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutDiscount));

        binding.checkoutPackingCharges.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(packingCharges));
        binding.checkoutSubtotal.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutSubtotal));
        binding.checkoutTotal.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutTotal));

    }

    private void addProductsToList() {
        orderProductList = new ArrayList<>();
        for (int i = 0; i < checkoutItemsList.size(); i++) {

            PostProducts orderProduct = new PostProducts();

            // Get current Product Details
            orderProduct.setProductsId(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsId());
            orderProduct.setProductsName(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsName());
            orderProduct.setModel(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsModel());
            orderProduct.setImage(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsImage());
            orderProduct.setWeight(checkoutItemsList.get(i).getCustomersBasketProduct().getSelectedProductsWeight());
            orderProduct.setUnit(checkoutItemsList.get(i).getCustomersBasketProduct().getSelectedProductsWeightUnit());
            orderProduct.setManufacture(checkoutItemsList.get(i).getCustomersBasketProduct().getManufacturersName());
            orderProduct.setCategoriesId(checkoutItemsList.get(i).getCustomersBasketProduct().getCategoryIDs());
            orderProduct.setCategoriesName(checkoutItemsList.get(i).getCustomersBasketProduct().getCategoryNames());
            orderProduct.setPrice(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsPrice());
            orderProduct.setFinalPrice(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsFinalPrice());
            orderProduct.setSubtotal(checkoutItemsList.get(i).getCustomersBasketProduct().getTotalPrice());
            orderProduct.setTotal(checkoutItemsList.get(i).getCustomersBasketProduct().getTotalPrice());
            orderProduct.setCustomersBasketQuantity(checkoutItemsList.get(i).getCustomersBasketProduct().getCustomersBasketQuantity());

            orderProduct.setOnSale(checkoutItemsList.get(i).getCustomersBasketProduct().getIsSaleProduct().equalsIgnoreCase("1"));


            List<PostProductsAttributes> productAttributes = new ArrayList<>();

            for (int j = 0; j < checkoutItemsList.get(i).getCustomersBasketProductAttributes().size(); j++) {
                CartProductAttributes cartProductAttributes = checkoutItemsList.get(i).getCustomersBasketProductAttributes().get(j);
                Option attributeOption = cartProductAttributes.getOption();
                Value attributeValue = cartProductAttributes.getValues().get(0);

                PostProductsAttributes attribute = new PostProductsAttributes();
                attribute.setProductsOptionsId(String.valueOf(attributeOption.getId()));
                attribute.setProductsOptions(attributeOption.getName());
                attribute.setProductsOptionsValuesId(String.valueOf(attributeValue.getId()));
                attribute.setProductsOptionsValues(attributeValue.getValue());
                attribute.setOptionsValuesPrice(attributeValue.getPrice());
                attribute.setPricePrefix(attributeValue.getPricePrefix());
                attribute.setAttributeName(attributeValue.getValue() + " " + attributeValue.getPricePrefix() + attributeValue.getPrice());

                productAttributes.add(attribute);
            }

            orderProduct.setAttributes(productAttributes);


            // Add current Product to orderProductList
            orderProductList.add(orderProduct);
        }
    }

    //*********** Set Order Details to proceed CheckoutFinal ********//

    private void proceedOrder() {
        dialogLoader.showProgressDialog();
        PostOrder orderDetails = new PostOrder();

        // Set Customer Info
        orderDetails.setCustomersId(Integer.parseInt(userInfo.getId()));
        orderDetails.setCustomersName(userInfo.getFirstName());
        orderDetails.setCustomersTelephone( PaymentOrderDetails.getCustomersTelephone() );
        orderDetails.setEmail(userInfo.getEmail());
        orderDetails.setShopId(this.shop_id);

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
        orderDetails.setDeliveryCost("" + checkoutShipping);
        orderDetails.setPackingChargeTax(ConstantValues.PACKING_CHARGE);

        // LatLang
        orderDetails.setLatitude(String.valueOf(shippingAddress.getLatitude()));
        orderDetails.setLongitude(String.valueOf(shippingAddress.getLongitude()));
        orderDetails.setLanguageId(ConstantValues.LANGUAGE_ID);
        orderDetails.setTaxZoneId(shippingAddress.getZoneId());
        orderDetails.setTotalTax(checkoutTax);
        orderDetails.setShippingMethod(getString(R.string.default_shipping_method));
        orderDetails.setShippingCost(checkoutShipping);

//        orderDetails.setComments(checkout_comments.getText().toString().trim());

        if (couponsList.size() > 0) {
            orderDetails.setIsCouponApplied(1);
        } else {
            orderDetails.setIsCouponApplied(0);
        }
        orderDetails.setCouponAmount(checkoutDiscount);
        orderDetails.setCoupons(couponsList);

        // Set PaymentNonceToken and PaymentMethod
        orderDetails.setNonce(paymentNonceToken);
        orderDetails.setPaymentMethod(selectedPaymentMethod);

        // Set CheckoutFinal Price and Products
        orderDetails.setProductsTotal(checkoutSubtotal);
        orderDetails.setTotalPrice(checkoutTotal);
        orderDetails.setProducts(orderProductList);
        orderDetails.setOrder_payment_id(getOrderID());

        orderDetails.setCurrency(PAYMENT_CURRENCY);
        String service_code = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_USER_PASSWORD,requireContext());
        orderDetails.setService_code(service_code);

        PlaceOrderNow(orderDetails);
    }


    //*********** Request the Server to Generate BrainTreeToken ********//

    private void GetCouponInfo(String coupon_code) {
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        Call<CouponsData> call = BuyInputsAPIClient.getInstance()
                .getCouponInfo
                        (access_token,
                                coupon_code
                        );


        call.enqueue(new Callback<CouponsData>() {
            @Override
            public void onResponse(Call<CouponsData> call, retrofit2.Response<CouponsData> response) {

                dialogLoader.hideProgressDialog();

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        final CouponsInfo couponsInfo = response.body().getData().get(0);

                        if (couponsList.size() != 0 && couponsInfo.getIndividualUse().equalsIgnoreCase("1")) {

                            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

                            dialog.setTitle(getString(R.string.add_coupon));
                            dialog.setMessage(getString(R.string.coupon_removes_other_coupons));

                            dialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    if (couponsInfo.getDiscountType().equalsIgnoreCase("fixed_cart")
                                            || couponsInfo.getDiscountType().equalsIgnoreCase("percent")) {
                                        if (validateCouponCart(couponsInfo))
                                            applyCoupon(couponsInfo);

                                    } else if (couponsInfo.getDiscountType().equalsIgnoreCase("fixed_product")
                                            || couponsInfo.getDiscountType().equalsIgnoreCase("percent_product")) {
                                        if (validateCouponProduct(couponsInfo))
                                            applyCoupon(couponsInfo);
                                    }
                                }
                            });

                            dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();

                        } else {
                            if (couponsInfo.getDiscountType().equalsIgnoreCase("fixed_cart")
                                    || couponsInfo.getDiscountType().equalsIgnoreCase("percent")) {
                                if (validateCouponCart(couponsInfo))
                                    applyCoupon(couponsInfo);

                            } else if (couponsInfo.getDiscountType().equalsIgnoreCase("fixed_product")
                                    || couponsInfo.getDiscountType().equalsIgnoreCase("percent_product")) {
                                if (validateCouponProduct(couponsInfo))
                                    applyCoupon(couponsInfo);
                            }
                        }

                    } else if (checkout_coupon_code!=null && response.body().getSuccess().equalsIgnoreCase("0")) {
                        checkout_coupon_code.setError(response.body().getMessage());

                    } else {
                        // Unexpected Response from Server
                        Toast.makeText(getContext(), getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "" + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CouponsData> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(getContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    //*********** Request the Server to Place User's Order ********//

    private void PlaceOrderNow(PostOrder postOrder) {
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;

        String str = new Gson().toJson(postOrder);

        Call<OrderData> call = BuyInputsAPIClient.getInstance()
                .addToOrder
                        (access_token,
                         postOrder
                        );

        call.enqueue(new Callback<OrderData>() {
            @Override
            public void onResponse(Call<OrderData> call, retrofit2.Response<OrderData> response) {
                dialogLoader.hideProgressDialog();

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Response is successful");

                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        Intent notificationIntent = new Intent(getContext(), WalletHomeActivity.class);
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        // Order has been placed Successfully
                        NotificationHelper.showNewNotification(getContext(), notificationIntent, getString(R.string.thank_you), response.body().getMessage(), null);

                        // Clear User's Cart
                        My_Cart.ClearCart();

                        // Clear User's Shipping and Billing info from AppContext
                        ((EmaishaPayApp) requireContext().getApplicationContext()).setShippingAddress(new AddressDetails());
                        ((EmaishaPayApp) requireContext().getApplicationContext()).setBillingAddress(new AddressDetails());

                        // Navigate to Thank_You Fragment

                        String orderNumber = response.body().getOrder_id();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("my_cart",my_cart);
                        bundle.putString("order_number",orderNumber);
                        WalletBuySellActivity.navController.navigate(R.id.action_checkOutFinal_to_thankYou, bundle);
                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        Snackbar.make(binding.paymentMethod, response.body().getMessage(), Snackbar.LENGTH_LONG).show();
                        if(  response.body().getMessage().equalsIgnoreCase("token_expired")){
                            getActivity().finish();
                        }

                    } else {
                        // Unable to get Success status
                        Snackbar.make(binding.paymentMethod, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("Error:", response.message());
                }
            }

            @Override
            public void onFailure(Call<OrderData> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(getContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    //*********** Apply given Coupon to buy_inputs_checkout ********//

    public void applyCoupon(CouponsInfo coupon) {

        double discount = 0.0;

        if (coupon.getDiscountType().equalsIgnoreCase("fixed_cart")) {
            discount = Double.parseDouble(coupon.getAmount());

        } else if (coupon.getDiscountType().equalsIgnoreCase("percent")) {
            discount = (checkoutSubtotal * Double.parseDouble(coupon.getAmount())) / 100;

        } else if (coupon.getDiscountType().equalsIgnoreCase("fixed_product")) {

            for (int i = 0; i < checkoutItemsList.size(); i++) {

                int productID = checkoutItemsList.get(i).getCustomersBasketProduct().getProductsId();
                int categoryID = Integer.parseInt(checkoutItemsList.get(i).getCustomersBasketProduct().getCategoryIDs());


                if (!checkoutItemsList.get(i).getCustomersBasketProduct().getIsSaleProduct().equalsIgnoreCase("1") || !coupon.getExcludeSaleItems().equalsIgnoreCase("1")) {
                    if (!isStringExistsInList(String.valueOf(categoryID), coupon.getExcludedProductCategories()) || coupon.getExcludedProductCategories().size() == 0) {
                        if (!isStringExistsInList(String.valueOf(productID), coupon.getExcludeProductIds()) || coupon.getExcludeProductIds().size() == 0) {
                            if (isStringExistsInList(String.valueOf(categoryID), coupon.getProductCategories()) || coupon.getProductCategories().size() == 0) {
                                if (isStringExistsInList(String.valueOf(productID), coupon.getProductIds()) || coupon.getProductIds().size() == 0) {

                                    discount += (Double.parseDouble(coupon.getAmount()) * checkoutItemsList.get(i).getCustomersBasketProduct().getCustomersBasketQuantity());
                                }
                            }
                        }
                    }
                }


            }

        } else if (coupon.getDiscountType().equalsIgnoreCase("percent_product")) {

            for (int i = 0; i < checkoutItemsList.size(); i++) {

                int productID = checkoutItemsList.get(i).getCustomersBasketProduct().getProductsId();
                int categoryID = Integer.parseInt(checkoutItemsList.get(i).getCustomersBasketProduct().getCategoryIDs());


                if (!checkoutItemsList.get(i).getCustomersBasketProduct().getIsSaleProduct().equalsIgnoreCase("1") || !coupon.getExcludeSaleItems().equalsIgnoreCase("1")) {
                    if (!isStringExistsInList(String.valueOf(categoryID), coupon.getExcludedProductCategories()) || coupon.getExcludedProductCategories().size() == 0) {
                        if (!isStringExistsInList(String.valueOf(productID), coupon.getExcludeProductIds()) || coupon.getExcludeProductIds().size() == 0) {
                            if (isStringExistsInList(String.valueOf(categoryID), coupon.getProductCategories()) || coupon.getProductCategories().size() == 0) {
                                if (isStringExistsInList(String.valueOf(productID), coupon.getProductIds()) || coupon.getProductIds().size() == 0) {

                                    double discountOnPrice = (Double.parseDouble(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsFinalPrice()) * Double.parseDouble(coupon.getAmount())) / 100;
                                    discount += (discountOnPrice * checkoutItemsList.get(i).getCustomersBasketProduct().getCustomersBasketQuantity());
                                }
                            }
                        }
                    }
                }

            }
        }


        if ((checkoutDiscount + discount) >= getProductsSubTotal()) {
            showSnackBarForCoupon(getString(R.string.coupon_cannot_be_applied));
        } else {
            if (coupon.getIndividualUse().equalsIgnoreCase("1")) {
                couponsList.clear();
                checkoutDiscount = 0.0;
                checkoutShipping = checkoutShippingCost;
                disableOtherCoupons = true;
                setCheckoutTotal();
            }

            if (coupon.getFreeShipping().equalsIgnoreCase("1")) {
                checkoutShipping = 0.0;
            }


            checkoutDiscount += discount;
            coupon.setDiscount(String.valueOf(discount));


            couponsList.add(coupon);
            checkout_coupon_code.setText("");
            couponsAdapter.notifyDataSetChanged();


            setCheckoutTotal();
        }

    }

    //*********** Remove given Coupon from buy_inputs_checkout ********//

    public void removeCoupon(CouponsInfo coupon) {

        if (coupon.getIndividualUse().equalsIgnoreCase("1")) {
            disableOtherCoupons = false;
        }


        for (int i = 0; i < couponsList.size(); i++) {
            if (coupon.getCode().equalsIgnoreCase(couponsList.get(i).getCode())) {
                couponsList.remove(i);
                couponsAdapter.notifyDataSetChanged();
            }
        }


        checkoutShipping = checkoutShippingCost;

        for (int i = 0; i < couponsList.size(); i++) {
            if (couponsList.get(i).getFreeShipping().equalsIgnoreCase("1")) {
                checkoutShipping = 0.0;
            }
        }


        double discount = Double.parseDouble(coupon.getDiscount());
        checkoutDiscount -= discount;


        setCheckoutTotal();

    }

    //*********** Validate Cart type Coupon ********//

    private boolean validateCouponCart(CouponsInfo coupon) {

        int user_used_this_coupon_counter = 0;

        boolean coupon_already_applied = false;

        boolean valid_user_email_for_coupon = false;
        boolean valid_sale_items_in_for_coupon = true;

        boolean valid_items_in_cart = false;
        boolean valid_category_items_in_cart = false;

        boolean no_excluded_item_in_cart = true;
        boolean no_excluded_category_item_in_cart = true;


        if (couponsList.size() != 0) {
            for (int i = 0; i < couponsList.size(); i++) {
                if (coupon.getCode().equalsIgnoreCase(couponsList.get(i).getCode())) {
                    coupon_already_applied = true;
                }
            }
        }


        for (int i = 0; i < coupon.getUsedBy().size(); i++) {
            if (userInfo.getId().equalsIgnoreCase(coupon.getUsedBy().get(i))) {
                user_used_this_coupon_counter += 1;
            }
        }


        if (coupon.getEmailRestrictions().size() != 0) {
            if (isStringExistsInList(userInfo.getEmail(), coupon.getEmailRestrictions())) {
                valid_user_email_for_coupon = true;
            }
        } else {
            valid_user_email_for_coupon = true;
        }


        for (int i = 0; i < checkoutItemsList.size(); i++) {

            int productID = checkoutItemsList.get(i).getCustomersBasketProduct().getProductsId();
            int categoryID = Integer.parseInt(checkoutItemsList.get(i).getCustomersBasketProduct().getCategoryIDs());


            if (coupon.getExcludeSaleItems().equalsIgnoreCase("1") && checkoutItemsList.get(i).getCustomersBasketProduct().getIsSaleProduct().equalsIgnoreCase("1")) {
                valid_sale_items_in_for_coupon = false;
            }


            if (coupon.getExcludedProductCategories().size() != 0) {
                if (isStringExistsInList(String.valueOf(categoryID), coupon.getExcludedProductCategories())) {
                    no_excluded_category_item_in_cart = false;
                }
            }

            if (coupon.getExcludeProductIds().size() != 0) {
                if (isStringExistsInList(String.valueOf(productID), coupon.getExcludeProductIds())) {
                    no_excluded_item_in_cart = false;
                }
            }

            if (coupon.getProductCategories().size() != 0) {
                if (isStringExistsInList(String.valueOf(categoryID), coupon.getProductCategories())) {
                    valid_category_items_in_cart = true;
                }
            } else {
                valid_category_items_in_cart = true;
            }


            if (coupon.getProductIds().size() != 0) {
                if (isStringExistsInList(String.valueOf(productID), coupon.getProductIds())) {
                    valid_items_in_cart = true;
                }
            } else {
                valid_items_in_cart = true;
            }
        }

        /////////////////////////////////////////////////////

        if (!disableOtherCoupons) {
            if (!coupon_already_applied) {
                if (!Utilities.checkIsDatePassed(coupon.getExpiryDate())) {
                    //coupon.getUsageLimit()==null UNLIMITED
                    if (coupon.getUsageLimit() == null || Integer.parseInt(coupon.getUsageCount()) <= Integer.parseInt(coupon.getUsageLimit())) {
                        if (user_used_this_coupon_counter <= Integer.parseInt(coupon.getUsageLimitPerUser())) {
                            if (valid_user_email_for_coupon) {
                                if (Double.parseDouble(coupon.getMinimumAmount()) <= checkoutTotal) {
                                    if (Double.parseDouble(coupon.getMaximumAmount()) == 0.0 || checkoutTotal <= Double.parseDouble(coupon.getMaximumAmount())) {
                                        if (valid_sale_items_in_for_coupon) {
                                            if (no_excluded_category_item_in_cart) {
                                                if (no_excluded_item_in_cart) {
                                                    if (valid_category_items_in_cart) {
                                                        if (valid_items_in_cart) {

                                                            return true;

                                                        } else {
                                                            showSnackBarForCoupon(getString(R.string.coupon_is_not_for_these_products));
                                                            return false;
                                                        }
                                                    } else {
                                                        showSnackBarForCoupon(getString(R.string.coupon_is_not_for_these_categories));
                                                        return false;
                                                    }
                                                } else {
                                                    showSnackBarForCoupon(getString(R.string.coupon_is_not_for_excluded_products));
                                                    return false;
                                                }
                                            } else {
                                                showSnackBarForCoupon(getString(R.string.coupon_is_not_for_excluded_categories));
                                                return false;
                                            }
                                        } else {
                                            showSnackBarForCoupon(getString(R.string.coupon_is_not_for_sale_items));
                                            return false;
                                        }
                                    } else {
                                        showSnackBarForCoupon(getString(R.string.coupon_max_amount_is_less_than_order_total));
                                        return false;
                                    }
                                } else {
                                    showSnackBarForCoupon(getString(R.string.coupon_min_amount_is_greater_than_order_total));
                                    return false;
                                }
                            } else {
                                showSnackBarForCoupon(getString(R.string.coupon_is_not_for_you));
                                return false;
                            }
                        } else {
                            showSnackBarForCoupon(getString(R.string.coupon_used_by_you));
                            return false;
                        }
                    } else {
                        showSnackBarForCoupon(getString(R.string.coupon_used_by_all));
                        return false;
                    }
                } else {
                    checkout_coupon_code.setError(getString(R.string.coupon_expired));
                    return false;
                }
            } else {
                showSnackBarForCoupon(getString(R.string.coupon_applied));
                return false;
            }
        } else {
            showSnackBarForCoupon(getString(R.string.coupon_cannot_used_with_existing));
            return false;
        }
    }

    //*********** Validate Product type Coupon ********//

    private boolean validateCouponProduct(CouponsInfo coupon) {

        int user_used_this_coupon_counter = 0;

        boolean coupon_already_applied = false;

        boolean valid_user_email_for_coupon = false;
        boolean valid_sale_items_in_for_coupon = false;

        boolean any_valid_item_in_cart = false;
        boolean any_valid_category_item_in_cart = false;

        boolean any_non_excluded_item_in_cart = false;
        boolean any_non_excluded_category_item_in_cart = false;

        if (couponsList.size() != 0) {
            for (int i = 0; i < couponsList.size(); i++) {
                if (coupon.getCode().equalsIgnoreCase(couponsList.get(i).getCode())) {
                    coupon_already_applied = true;
                }
            }
        }

        for (int i = 0; i < coupon.getUsedBy().size(); i++) {
            if (userInfo.getId().equalsIgnoreCase(coupon.getUsedBy().get(i))) {
                user_used_this_coupon_counter += 1;
            }
        }

        if (coupon.getEmailRestrictions().size() != 0) {
            if (isStringExistsInList(userInfo.getEmail(), coupon.getEmailRestrictions())) {
                valid_user_email_for_coupon = true;
            }
        } else {
            valid_user_email_for_coupon = true;
        }

        for (int i = 0; i < checkoutItemsList.size(); i++) {
            int productID = checkoutItemsList.get(i).getCustomersBasketProduct().getProductsId();
            int categoryID = checkoutItemsList.get(i).getCustomersBasketProduct().getLanguageId();

            if (!coupon.getExcludeSaleItems().equalsIgnoreCase("1") || !checkoutItemsList.get(i).getCustomersBasketProduct().getIsSaleProduct().equalsIgnoreCase("1")) {
                valid_sale_items_in_for_coupon = true;
            }

            if (coupon.getExcludedProductCategories().size() != 0) {
                if (isStringExistsInList(String.valueOf(categoryID), coupon.getExcludedProductCategories())) {
                    any_non_excluded_category_item_in_cart = true;
                }
            } else {
                any_non_excluded_category_item_in_cart = true;
            }

            if (coupon.getExcludeProductIds().size() != 0) {
                if (isStringExistsInList(String.valueOf(productID), coupon.getExcludeProductIds())) {
                    any_non_excluded_item_in_cart = true;
                }
            } else {
                any_non_excluded_item_in_cart = true;
            }

            if (coupon.getProductCategories().size() != 0) {
                if (isStringExistsInList(String.valueOf(categoryID), coupon.getProductCategories())) {
                    any_valid_category_item_in_cart = true;
                }
            } else {
                any_valid_category_item_in_cart = true;
            }

            if (coupon.getProductIds().size() != 0) {
                if (isStringExistsInList(String.valueOf(productID), coupon.getProductIds())) {
                    any_valid_item_in_cart = true;
                }
            } else {
                any_valid_item_in_cart = true;
            }
        }

        /////////////////////////////////////////////////////

        if (!disableOtherCoupons) {
            if (!coupon_already_applied) {
                if (!Utilities.checkIsDatePassed(coupon.getExpiryDate())) {
                    if (Integer.parseInt(coupon.getUsageCount()) <= Integer.parseInt(coupon.getUsageLimit())) {
                        if (user_used_this_coupon_counter <= Integer.parseInt(coupon.getUsageLimitPerUser())) {
                            if (valid_user_email_for_coupon) {
                                if (Double.parseDouble(coupon.getMinimumAmount()) <= checkoutTotal) {
                                    if (Double.parseDouble(coupon.getMaximumAmount()) == 0.0 || checkoutTotal <= Double.parseDouble(coupon.getMaximumAmount())) {
                                        if (valid_sale_items_in_for_coupon) {
                                            if (any_non_excluded_category_item_in_cart) {
                                                if (any_non_excluded_item_in_cart) {
                                                    if (any_valid_category_item_in_cart) {
                                                        if (any_valid_item_in_cart) {

                                                            return true;
                                                        } else {
                                                            showSnackBarForCoupon(getString(R.string.coupon_is_not_for_these_products));
                                                            return false;
                                                        }
                                                    } else {
                                                        showSnackBarForCoupon(getString(R.string.coupon_is_not_for_these_categories));
                                                        return false;
                                                    }
                                                } else {
                                                    showSnackBarForCoupon(getString(R.string.coupon_is_not_for_excluded_products));
                                                    return false;
                                                }
                                            } else {
                                                showSnackBarForCoupon(getString(R.string.coupon_is_not_for_excluded_categories));
                                                return false;
                                            }
                                        } else {
                                            showSnackBarForCoupon(getString(R.string.coupon_is_not_for_sale_items));
                                            return false;
                                        }
                                    } else {
                                        showSnackBarForCoupon(getString(R.string.coupon_max_amount_is_less_than_order_total));
                                        return false;
                                    }
                                } else {
                                    showSnackBarForCoupon(getString(R.string.coupon_min_amount_is_greater_than_order_total));
                                    return false;
                                }
                            } else {
                                showSnackBarForCoupon(getString(R.string.coupon_is_not_for_you));
                                return false;
                            }
                        } else {
                            showSnackBarForCoupon(getString(R.string.coupon_used_by_you));
                            return false;
                        }
                    } else {
                        showSnackBarForCoupon(getString(R.string.coupon_used_by_all));
                        return false;
                    }
                } else {
                    checkout_coupon_code.setError(getString(R.string.coupon_expired));
                    return false;
                }
            } else {
                showSnackBarForCoupon(getString(R.string.coupon_applied));
                return false;
            }
        } else {
            showSnackBarForCoupon(getString(R.string.coupon_cannot_used_with_existing));
            return false;
        }
    }

    //*********** Show SnackBar with given Message  ********//

    private void showSnackBarForCoupon(String msg) {
        final Snackbar snackbar = Snackbar.make(binding.paymentMethod, msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    //*********** Check if the given String exists in the given List ********//

    private boolean isStringExistsInList(String str, List<String> stringList) {
        boolean isExists = false;

        for (int i = 0; i < stringList.size(); i++) {
            if (stringList.get(i).equalsIgnoreCase(str)) {
                isExists = true;
            }
        }

        return isExists;
    }

    //*********** Setup Demo Coupons Dialog ********//

    private void setupDemoCoupons() {
        demo_coupons_text.setVisibility(View.VISIBLE);
        demo_coupons_text.setPaintFlags(demo_coupons_text.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        demo_coupons_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final List<CouponsInfo> couponsList = demoCouponsList();
                DemoCouponsListAdapter couponsListAdapter = new DemoCouponsListAdapter(getContext(), couponsList, CheckoutFinal.this);

                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.buy_inputs_dialog_list, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialog_button = dialogView.findViewById(R.id.dialog_button);
                TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = dialogView.findViewById(R.id.dialog_list);

                dialog_title.setText(getString(R.string.search) + " " + getString(R.string.coupon));
                dialog_list.setVerticalScrollBarEnabled(true);
                dialog_list.setAdapter(couponsListAdapter);

                demoCouponsDialog = dialog.create();

                dialog_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        demoCouponsDialog.dismiss();
                    }
                });

                demoCouponsDialog.show();
            }
        });
    }

    //*********** Sets selected Coupon code from the Dialog ********//

    public void setCouponCode(String code) {
        checkout_coupon_code.setText(code);
        demoCouponsDialog.dismiss();
    }

    //*********** Demo Coupons List ********//

    private List<CouponsInfo> demoCouponsList() {
        List<CouponsInfo> couponsList = new ArrayList<>();

        CouponsInfo coupon1 = new CouponsInfo();
        coupon1.setCode("PercentProduct_10");
        coupon1.setAmount("10");
        coupon1.setDiscountType("Percent Product");
        coupon1.setDescription("For All Products");

        CouponsInfo coupon2 = new CouponsInfo();
        coupon2.setCode("FixedProduct_10");
        coupon2.setAmount("10");
        coupon2.setDiscountType("Fixed Product");
        coupon2.setDescription("For All Products");

        CouponsInfo coupon3 = new CouponsInfo();
        coupon3.setCode("PercentCart_10");
        coupon3.setAmount("10");
        coupon3.setDiscountType("Percent Cart");
        coupon3.setDescription("For All Products");

        CouponsInfo coupon4 = new CouponsInfo();
        coupon4.setCode("FixedCart_10");
        coupon4.setAmount("10");
        coupon4.setDiscountType("Fixed Cart");
        coupon4.setDescription("For All Products");

        CouponsInfo coupon5 = new CouponsInfo();
        coupon5.setCode("SingleCoupon_50");
        coupon5.setAmount("50");
        coupon5.setDiscountType("Fixed Cart");
        coupon5.setDescription("Individual Use");

        CouponsInfo coupon6 = new CouponsInfo();
        coupon6.setCode("FreeShipping_20");
        coupon6.setAmount("20");
        coupon6.setDiscountType("Fixed Cart");
        coupon6.setDescription("Free Shipping");

        CouponsInfo coupon7 = new CouponsInfo();
        coupon7.setCode("ExcludeSale_15");
        coupon7.setAmount("15");
        coupon7.setDiscountType("Fixed Cart");
        coupon7.setDescription("Not for Sale Items");

        CouponsInfo coupon8 = new CouponsInfo();
        coupon8.setCode("Exclude_Shoes_25");
        coupon8.setAmount("25");
        coupon8.setDiscountType("Fixed Cart");
        coupon8.setDescription("Not For Men Shoes");

        CouponsInfo coupon9 = new CouponsInfo();
        coupon9.setCode("Polo_Shirts_10");
        coupon9.setAmount("10");
        coupon9.setDiscountType("Percent Product");
        coupon9.setDescription("For Men Polo Shirts");

        CouponsInfo coupon10 = new CouponsInfo();
        coupon10.setCode("Jeans_10");
        coupon10.setAmount("10");
        coupon10.setDiscountType("Percent Cart");
        coupon10.setDescription("For Men Jeans");

        couponsList.add(coupon1);
        couponsList.add(coupon2);
        couponsList.add(coupon3);
        couponsList.add(coupon4);
        couponsList.add(coupon5);
        couponsList.add(coupon6);
        couponsList.add(coupon7);
        couponsList.add(coupon8);
        couponsList.add(coupon9);
        couponsList.add(coupon10);

        return couponsList;
    }

    //*********** Validate Payment Card Inputs ********//

    private boolean validatePaymentCard() {
        if (!ValidateInputs.isValidNumber(checkout_card_number.getText().toString().trim())) {
            checkout_card_number.setError(getString(R.string.invalid_credit_card));
            return false;
        } else if (!ValidateInputs.isValidNumber(checkout_card_cvv.getText().toString().trim())) {
            checkout_card_cvv.setError(getString(R.string.invalid_card_cvv));
            return false;
        } else if (TextUtils.isEmpty(checkout_card_expiry.getText().toString().trim())) {
            checkout_card_expiry.setError(getString(R.string.select_card_expiry));
            return false;
        } else {
            return true;
        }
    }

    //*********** Validate Payment Info Inputs ********//

    private boolean validatePaymentInfo() {
        if (!ValidateInputs.isValidName(payment_name.getText().toString().trim())) {
            payment_name.setError(getString(R.string.invalid_first_name));
            return false;
        } else if (!ValidateInputs.isValidEmail(payment_email.getText().toString().trim())) {
            payment_email.setError(getString(R.string.invalid_email));
            return false;
        } else if (!ValidateInputs.isValidPhoneNo(payment_phone.getText().toString().trim())) {
            payment_phone.setError(getString(R.string.invalid_contact));
            return false;
        } else {
            return true;
        }
    }
}

