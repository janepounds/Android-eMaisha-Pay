package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.fragments.wallet_fragments.TokenAuthFragment;
import com.cabral.emaishapay.models.ConfirmationDataResponse;
import com.cabral.emaishapay.models.WalletPurchaseConfirmResponse;
import com.cabral.emaishapay.models.WalletPurchaseResponse;
import com.cabral.emaishapay.models.WalletTransaction;
import com.cabral.emaishapay.models.WalletTransactionInitiation;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;
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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchasePreview extends DialogFragment implements
        SavedCardsListener, CardPaymentCallback {

    Button confirmBtn;
    LinearLayout error_message_layout, discount_layout;
    TextView purchase_date_label_TextView,datetimeTextView, totalTextView,mechantIdTextView,
            mechantNameTextView,errorTextView, discountTextView,merchant_label;

    String businessName ="", cardNumber;
    Context activity;
    Dialog dialog;
    DialogLoader dialogLoader;

    private RaveVerificationUtils verificationUtils;
    private CardPaymentManager cardPayManager;

    public PurchasePreview(Context context){
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
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view =inflater.inflate(R.layout.wallet_purchase_preview, null);

        builder.setView(view);
        initializeView(view);
        return builder.create();

    }
    public void initializeView(View view){

        purchase_date_label_TextView = view.findViewById(R.id.purchase_date_label);
        totalTextView = view.findViewById(R.id.txt_view_crop_bill_preview_total);
        errorTextView = view.findViewById(R.id.Comfirmation_Error_textview);
        discountTextView= view.findViewById(R.id.txt_view_discount_preview_total);
        discount_layout= view.findViewById(R.id.discount_layount);
        error_message_layout= view.findViewById(R.id.linearlayount_error);
        datetimeTextView = view.findViewById(R.id.text_view_purchase_date_time);

        mechantNameTextView = view.findViewById(R.id.text_view_purchase_preview_name);
        mechantIdTextView = view.findViewById(R.id.text_view_purchase_preview_mechant_id);
        confirmBtn = view.findViewById(R.id.btn_purchase_preview_confirm);
        merchant_label = view.findViewById(R.id.txt_wallet_purchase_mechant_label);

        totalTextView.setText( NumberFormat.getInstance().format(WalletTransactionInitiation.getInstance().getAmount()));

        SimpleDateFormat localFormat = new SimpleDateFormat(getString(R.string.date_format_preffered), Locale.ENGLISH);
        localFormat.setTimeZone(TimeZone.getDefault());
        String currentDateandTime = null;
        SimpleDateFormat incomingFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        incomingFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        currentDateandTime = localFormat.format(new Date());
        purchase_date_label_TextView.setText(getString(R.string.purchase_date));
        datetimeTextView.setText(currentDateandTime);


        String key = WalletTransactionInitiation.getInstance().getPayTo();
        if(key.equalsIgnoreCase("agent")){
            merchant_label.setText("AgentID");

        }
        mechantIdTextView.setText(WalletTransactionInitiation.getInstance().getMechantId());

        dialogLoader = new DialogLoader(activity);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mechantNameTextView.getText().toString().equalsIgnoreCase("unknown merchant")){

                    Snackbar.make(errorTextView,"Unknown Merchant",Snackbar.LENGTH_SHORT).show();
                    errorTextView.setVisibility(View.VISIBLE);

                }else {

                    double balance = Double.parseDouble(WalletHomeActivity.getPreferences(String.valueOf(WalletHomeActivity.PREFERENCE_WALLET_BALANCE), getContext()));
                    Float PurchaseCharges = 0F;
                    double amount = WalletTransactionInitiation.getInstance().getAmount() + PurchaseCharges;
                    if (balance >= amount) {
                        processPayment();

                    } else {

                        Snackbar.make(errorTextView,"Insufficient Funds",Snackbar.LENGTH_SHORT).show();
                        errorTextView.setVisibility(View.VISIBLE);
                    }
                }

            }
        });

        verificationUtils = new RaveVerificationUtils(PurchasePreview.this, false, BuildConfig.PUBLIC_KEY);

        getMechantName();


    }

    public void getMechantName(){

        /***************RETROFIT IMPLEMENTATION***********************/
        dialogLoader.showProgressDialog();

        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        String merchantId = WalletTransactionInitiation.getInstance().getMechantId();
        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<ConfirmationDataResponse> call = apiRequests.
                getMerchant(access_token,merchantId,request_id,category,"getMerchantForUser");
        call.enqueue(new Callback<ConfirmationDataResponse>() {
            @Override
            public void onResponse(Call<ConfirmationDataResponse> call, Response<ConfirmationDataResponse> response) {
                if(response.code()==200){
                    businessName = response.body().getData().getBusinessName();
                    mechantNameTextView.setText(businessName);
                    confirmBtn.setVisibility(View.VISIBLE);

                }else if(response.code()==412) {
//                    businessName = response.body().getMessage();
                    mechantNameTextView.setText("Unknown Merchant");
                    mechantNameTextView.setTextColor( getResources().getColor(R.color.textRed));
                    errorTextView.setText("Unknown Merchant");
                    errorTextView.setVisibility(View.VISIBLE);

                    // confirmBtn.setEnabled(true);
                }
                else if(response.code()==401){
                    TokenAuthFragment.startAuth( true);

                }

                dialogLoader.hideProgressDialog();

            }

            @Override
            public void onFailure(Call<ConfirmationDataResponse> call, Throwable t) {

                    Log.e("info : ", t.getMessage());
                    Log.e("info : ", "Something got very very wrong");

                mechantNameTextView.setText("Unknown Merchant");

                errorTextView.setText("Error while checking for mechant occured");
                errorTextView.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });


    }

    public void processPayment(){
        String methodOfPayment= WalletTransactionInitiation.getInstance().getMethodOfPayment();
        //Inner Dialog to enter PIN
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialog);
        //LayoutInflater inflater = requireActivity().getLayoutInflater();
        View pinDialog = View.inflate(activity,R.layout.dialog_enter_pin,null);


        builder.setView(pinDialog);
        dialog = builder.create();
        builder.setCancelable(false);

        EditText pinEdittext =pinDialog.findViewById(R.id.etxt_create_agent_pin);



        pinDialog.findViewById(R.id.txt_custom_add_agent_submit_pin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !TextUtils.isEmpty(pinEdittext.getText()) && pinEdittext.getText().length()==4){
                    String service_code =WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION+ pinEdittext.getText().toString() ;
                    dialogLoader.showProgressDialog();
                    if(methodOfPayment.equalsIgnoreCase("wallet")){
                        processWalletPayment(service_code );
                    }
                    else if(methodOfPayment.equalsIgnoreCase("eMaisha Card") || methodOfPayment.equalsIgnoreCase("Bank Cards") ){

                        processCardPayment(service_code);
                    }
                    else if(methodOfPayment.equalsIgnoreCase("Mobile Money")){

                        payMerchantMobileMoney(service_code);
                    }

                }else {
                    pinEdittext.setError("Invalid Pin Entered");
                }

            }
        });

        dialog.show();




    }

    private void payMerchantMobileMoney(String service_code) {

        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE, requireContext());
        String mobileNumber=getString(R.string.phone_number_code)+ WalletTransactionInitiation.getInstance().getMobileNumber();
        String merchant_id= WalletTransactionInitiation.getInstance().getMechantId();
        double amount = WalletTransactionInitiation.getInstance().getAmount();
        String key = WalletTransactionInitiation.getInstance().getPayTo();

        if(key.equalsIgnoreCase("agent")){
            //call agent endpoint
            /******************RETROFIT IMPLEMENTATION***********************/
            Call<WalletPurchaseResponse> call = APIClient.getWalletInstance(activity).customerPayAgentMobile(
                    access_token, amount, mobileNumber,
                    request_id,
                    category, "customerPayAgentViaMobileMoney", service_code, merchant_id);
            call.enqueue(new Callback<WalletPurchaseResponse>() {
                @Override
                public void onResponse(Call<WalletPurchaseResponse> call, Response<WalletPurchaseResponse> response) {
                    if (response.isSuccessful()) {
                        dialogLoader.hideProgressDialog();
                        if (response.body().getStatus().equalsIgnoreCase("1")) {
                            //call success dialog
                            final Dialog dialog = new Dialog(activity);
                            dialog.setContentView(R.layout.dialog_successful_message);
                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialog.setCancelable(false);
                            TextView text = dialog.findViewById(R.id.dialog_success_txt_message);
                            text.setText(response.body().getMessage());


                            dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    Intent goToWallet = new Intent(activity, WalletHomeActivity.class);
                                    startActivity(goToWallet);
                                }
                            });
                            dialog.show();

                        } else {

                            Toast.makeText(activity, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            dialogLoader.hideProgressDialog();
                        }

                    } else if (response.code() == 401) {

                        TokenAuthFragment.startAuth(true);

                        if (response.errorBody() != null) {
                            Log.e("info", new String(String.valueOf(response.errorBody())));
                        } else {
                            Log.e("info", "Something got very very wrong");
                        }
                    }

                }

                @Override
                public void onFailure(Call<WalletPurchaseResponse> call, Throwable t) {
                    dialogLoader.hideProgressDialog();
                }
            });

        }else {


            /******************RETROFIT IMPLEMENTATION***********************/
            Call<WalletPurchaseResponse> call = APIClient.getWalletInstance(activity).customerPayMerchantMobile(
                    access_token, amount, mobileNumber,
                    request_id,
                    category, "customerPayMerchantViaMobileMoney", service_code, merchant_id);
            call.enqueue(new Callback<WalletPurchaseResponse>() {
                @Override
                public void onResponse(Call<WalletPurchaseResponse> call, Response<WalletPurchaseResponse> response) {
                    if (response.isSuccessful()) {
                        dialogLoader.hideProgressDialog();
                        if (response.body().getStatus().equalsIgnoreCase("1")) {
                            //call success dialog
                            final Dialog dialog = new Dialog(activity);
                            dialog.setContentView(R.layout.dialog_successful_message);
                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialog.setCancelable(false);
                            TextView text = dialog.findViewById(R.id.dialog_success_txt_message);
                            text.setText(response.body().getMessage());


                            dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    Intent goToWallet = new Intent(activity, WalletHomeActivity.class);
                                    startActivity(goToWallet);
                                }
                            });
                            dialog.show();

                        } else {

                            Toast.makeText(activity, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            dialogLoader.hideProgressDialog();
                        }

                    } else if (response.code() == 401) {

                        TokenAuthFragment.startAuth(true);

                        if (response.errorBody() != null) {
                            Log.e("info", new String(String.valueOf(response.errorBody())));
                        } else {
                            Log.e("info", "Something got very very wrong");
                        }
                    }

                }

                @Override
                public void onFailure(Call<WalletPurchaseResponse> call, Throwable t) {
                    dialogLoader.hideProgressDialog();
                }
            });

        }
        }


    private void processMobileMoneyPayment(String service_code) {
        String mobileNumber= WalletTransactionInitiation.getInstance().getMobileNumber();
        double amount = WalletTransactionInitiation.getInstance().getAmount();
        String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
        String txRef= "MMP"+userId+ System.currentTimeMillis();
        String eMaishaPayServiceMail="info@cabraltech.com";


        RaveNonUIManager raveNonUIManager = new RaveNonUIManager().setAmount(amount)
                .setCurrency("UGX")
                .setEmail(eMaishaPayServiceMail)
                .setfName(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME, this.activity))
                .setlName(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_LAST_NAME, this.activity))
                .setPhoneNumber("0" + mobileNumber)
                .setNarration("eMaisha Pay Mobile Money Purchase")
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
                Toast.makeText(activity, "Transaction Successful", Toast.LENGTH_LONG).show();
                recordPurchase(flwRef,"0" + mobileNumber);
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

    private void processCardPayment(String service_code) {

        String expiryDate= WalletTransactionInitiation.getInstance().getCardExpiry();
        cardNumber= WalletTransactionInitiation.getInstance().getCardNumber();
        String cardccv= WalletTransactionInitiation.getInstance().getCvv();
        double amount = WalletTransactionInitiation.getInstance().getAmount();
        String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
        String txRef= "BCP"+userId+ System.currentTimeMillis();
        String eMaishaPayServiceMail="info@cabraltech.com";


        dialogLoader.showProgressDialog();

        RaveNonUIManager raveNonUIManager = new RaveNonUIManager().setAmount(amount)
                .setCurrency("UGX")
                .setEmail( eMaishaPayServiceMail )
                .setfName( WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME,this.activity) )
                .setlName( WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_LAST_NAME,this.activity) )
                .setPhoneNumber("+256"+ WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_PHONE_NUMBER,this.activity).substring(1))
                .setNarration("eMaisha Pay Bank Card Purchase")
                .setPublicKey(BuildConfig.PUBLIC_KEY)
                .setEncryptionKey(BuildConfig.ENCRYPTION_KEY)
                .setTxRef(txRef)
                .onStagingEnv(false)
                .isPreAuth(true)
                .initialize();




        cardPayManager = new CardPaymentManager(
                raveNonUIManager, PurchasePreview.this, this);

        Card card = new Card(
                cardNumber,
                expiryDate.substring(0,2),
                expiryDate.substring(3,5),
                cardccv
        );

        cardPayManager.chargeCard(card);

    }

    private void processWalletPayment(String service_code) {
        String merchantId =WalletTransactionInitiation.getInstance().getMechantId();
        double amount = WalletTransactionInitiation.getInstance().getAmount();
        String coupon  = WalletTransactionInitiation.getInstance().getCoupon();
        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());


        Call<WalletPurchaseResponse> call = apiRequests.makeTransaction(access_token,merchantId,amount,coupon,request_id,category,"payMerchant",service_code);

        call.enqueue(new Callback<WalletPurchaseResponse>() {
            @Override
            public void onResponse(Call<WalletPurchaseResponse> call, Response<WalletPurchaseResponse> response) {
                if(response.code()== 200){
                    dialog.dismiss();
                    dialogLoader.hideProgressDialog();
                    if(response.body().getStatus().equals("0")){

                        try {
                            Toasty.error(activity, ""+response.body().getMessage(), Toast.LENGTH_LONG).show();
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.e("Error", e.getMessage());
                        }
                    }else {

                        final Dialog dialog = new Dialog(activity);
                        dialog.setContentView(R.layout.dialog_successful_message);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.setCancelable(false);
                        TextView text = dialog.findViewById(R.id.dialog_success_txt_message);
                        text.setText(response.body().getMessage());


                        dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                Intent goToWallet = new Intent(activity, WalletHomeActivity.class);
                                startActivity(goToWallet);
                            }
                        });
                        dialog.show();
                    }


                }else{
                    errorTextView.setText(response.errorBody().toString());
                    error_message_layout.setVisibility(View.VISIBLE);
                    errorTextView.setVisibility(View.VISIBLE);
                    if(response.errorBody() != null){
                        Log.e("BACKUP RESPONSE 1A"+response.code(),response.errorBody().toString());
                    }

                    dialogLoader.hideProgressDialog();

                }
            }

            @Override
            public void onFailure(Call<WalletPurchaseResponse> call, Throwable t) {

                Log.e("info 1A: ", t.getMessage());
                Log.e("info 1A: ", "Something got very very wrong");

                errorTextView.setText("Error occured! Try again later");
                error_message_layout.setVisibility(View.VISIBLE);
                errorTextView.setVisibility(View.VISIBLE);
                dialogLoader.hideProgressDialog();

            }

        });
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

            if (dialogLoader == null) {
                dialogLoader.showProgressDialog();
            }

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
        recordPurchase(flwRef, cardNumber);
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

    private void recordPurchase(String flwRef, String thirdparty_id) {
        DialogLoader dialogLoader = new DialogLoader(getContext());
        dialogLoader.showProgressDialog();

        int merchantId = Integer.parseInt(WalletTransactionInitiation.getInstance().getMechantId());
        double amount = WalletTransactionInitiation.getInstance().getAmount();
        String coupon  = WalletTransactionInitiation.getInstance().getCoupon();
        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());



        Call<WalletTransaction> call = apiRequests.creditUser(access_token,merchantId+"",amount,flwRef,"External Purchase","flutterwave",thirdparty_id,false,request_id,category,"creditUserAfterTransaction");

        call.enqueue(new Callback<WalletTransaction>() {
            @Override
            public void onResponse(Call<WalletTransaction> call, Response<WalletTransaction> response) {
                if(response.code()== 200){
                    dialogLoader.hideProgressDialog();

                    final Dialog dialog = new Dialog(activity);
                    dialog.setContentView(R.layout.dialog_successful_message);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(false);
                    TextView text = dialog.findViewById(R.id.dialog_success_txt_message);
                    text.setText("Processed Purchase worth UGX "+ NumberFormat.getInstance().format(WalletTransactionInitiation.getInstance().getAmount())+" from "+businessName);

                    dialog.findViewById(R.id.dialog_success_txt_message).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Intent goToWallet = new Intent(activity, WalletHomeActivity.class);
                            startActivity(goToWallet);
                        }
                    });
                    dialog.show();

                }else{
                    errorTextView.setText(response.errorBody().toString());
                    error_message_layout.setVisibility(View.VISIBLE);
                    errorTextView.setVisibility(View.VISIBLE);
                    if(response.errorBody() != null){
                        Log.e("BACKUP RESPONSE 1A"+response.code(),response.errorBody().toString());
                    }

                    dialogLoader.hideProgressDialog();

                }
            }


            @Override
            public void onFailure(Call<WalletTransaction> call, Throwable t) {

                Log.e("info 1A: ", t.getMessage());
                Log.e("info 1A: ", "Something got very very wrong");

                errorTextView.setText("Error occured! Try again later");
                error_message_layout.setVisibility(View.VISIBLE);
                errorTextView.setVisibility(View.VISIBLE);
                dialogLoader.hideProgressDialog();

            }

        });
    }
}
