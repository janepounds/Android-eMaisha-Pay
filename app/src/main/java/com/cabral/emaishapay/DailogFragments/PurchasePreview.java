package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.models.ConfirmationDataResponse;
import com.cabral.emaishapay.models.WalletPurchaseResponse;
import com.cabral.emaishapay.models.WalletTransaction;
import com.cabral.emaishapay.models.WalletTransactionInitiation;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.cabral.emaishapay.singletons.WalletSettingsSingleton;
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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchasePreview extends DialogFragment implements
        SavedCardsListener, CardPaymentCallback {

    Button confirmBtn;
    LinearLayout error_message_layout, discount_layout;
    TextView purchase_date_label_TextView,datetimeTextView, totalTextView,mechantIdTextView,
            mechantNameTextView,errorTextView, discountTextView;

    String businessName ="", cardNumber;
    Context activity;

    ProgressDialog dialog;

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

        totalTextView.setText( NumberFormat.getInstance().format(WalletTransactionInitiation.getInstance().getAmount()));

        SimpleDateFormat localFormat = new SimpleDateFormat(WalletSettingsSingleton.getInstance().getDateFormat(), Locale.ENGLISH);
        localFormat.setTimeZone(TimeZone.getDefault());
        String currentDateandTime = null;
        SimpleDateFormat incomingFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        incomingFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            currentDateandTime = localFormat.format(new Date());
        purchase_date_label_TextView.setText(getString(R.string.purchase_date));
        datetimeTextView.setText(currentDateandTime);

        mechantIdTextView.setText(WalletTransactionInitiation.getInstance().getMechantId());
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double balance = Double.parseDouble(WalletHomeActivity.getPreferences(String.valueOf(WalletHomeActivity.PREFERENCE_WALLET_BALANCE),getContext()));
                Float PurchaseCharges=0F;
                if(balance >= Double.parseDouble( ""+(WalletTransactionInitiation.getInstance().getAmount())+PurchaseCharges) ){
                    processPayment();
                }
            }
        });

        verificationUtils = new RaveVerificationUtils(PurchasePreview.this, false, BuildConfig.PUBLIC_KEY);

        getMechantName();


    }

    public void getMechantName(){

        /***************RETROFIT IMPLEMENTATION***********************/
        ProgressDialog dialog;
        dialog = new ProgressDialog(activity);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        int merchantId = Integer.parseInt(WalletTransactionInitiation.getInstance().getMechantId());
        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<ConfirmationDataResponse> call = apiRequests.getMerchant(access_token,merchantId);
        call.enqueue(new Callback<ConfirmationDataResponse>() {
            @Override
            public void onResponse(Call<ConfirmationDataResponse> call, Response<ConfirmationDataResponse> response) {
                if(response.code()==200){
                    businessName = response.body().getData().getBusinessName();
                    mechantNameTextView.setText(businessName);
                    confirmBtn.setVisibility(View.VISIBLE);

                    dialog.dismiss();
                }else if(response.code()==412) {
                    String businessName = null;
                    businessName = response.body().getMessage();
                    mechantNameTextView.setText(businessName);
                    errorTextView.setText(businessName);
                    errorTextView.setVisibility(View.VISIBLE);
                    // confirmBtn.setEnabled(true);
                }
                else if(response.code()==401){
                    TokenAuthActivity.startAuth(getActivity(), true);
                    getActivity().finishAffinity();
                }


                dialog.dismiss();
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
        if(methodOfPayment.equalsIgnoreCase("wallet")){
          processWalletPayment();
        }
        else if(methodOfPayment.equalsIgnoreCase("eMaisha Card") || methodOfPayment.equalsIgnoreCase("Bank Cards") ){

            processCardPayment();
        }
        else if(methodOfPayment.equalsIgnoreCase("Mobile Money")){

            processMobileMoneyPayment();
        }

    }

    private void processMobileMoneyPayment() {
        String mobileNumber= WalletTransactionInitiation.getInstance().getMobileNumber();
        double amount = WalletTransactionInitiation.getInstance().getAmount();
        String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
        String txRef= "MMP"+userId+ System.currentTimeMillis();
        String eMaishaPayServiceMail="info@cabraltech.com";


        dialog = new ProgressDialog(this.activity);
        dialog.setIndeterminate(true);
        dialog.setMessage("Processing the transaction..");

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

                    if (dialog == null) {
                        dialog = new ProgressDialog(activity);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setMessage("Please wait...");
                    }

                    if (active && !dialog.isShowing()) {
                        dialog.show();
                    } else {
                        //dialog.dismiss();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage, @Nullable String flwRef) {
                dialog.dismiss();
                Log.e("MobileMoneypaymentError", errorMessage);
            }

            @Override
            public void onSuccessful(String flwRef) {
                dialog.dismiss();
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

    private void processCardPayment() {

        String expiryDate= WalletTransactionInitiation.getInstance().getCardExpiry();
        cardNumber= WalletTransactionInitiation.getInstance().getCardNumber();
        String cardccv= WalletTransactionInitiation.getInstance().getCvv();
        double amount = WalletTransactionInitiation.getInstance().getAmount();
        String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
        String txRef= "BCP"+userId+ System.currentTimeMillis();
        String eMaishaPayServiceMail="info@cabraltech.com";


        dialog = new ProgressDialog(this.activity);
        dialog.setIndeterminate(true);
        dialog.setMessage("Processing the transaction..");

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

    private void processWalletPayment() {
        int merchantId = Integer.parseInt(WalletTransactionInitiation.getInstance().getMechantId());
        double amount = WalletTransactionInitiation.getInstance().getAmount();
        String coupon  = WalletTransactionInitiation.getInstance().getCoupon();
        APIRequests apiRequests = APIClient.getWalletInstance();
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;

        ProgressDialog dialog;
        dialog = new ProgressDialog(activity);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();

        Call<WalletPurchaseResponse> call = apiRequests.makeTransaction(access_token,merchantId,amount,coupon);

        call.enqueue(new Callback<WalletPurchaseResponse>() {
            @Override
            public void onResponse(Call<WalletPurchaseResponse> call, Response<WalletPurchaseResponse> response) {
                if(response.code()== 200){
                    dialog.dismiss();
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

                    dialog.dismiss();

                }
            }

            @Override
            public void onFailure(Call<WalletPurchaseResponse> call, Throwable t) {

                Log.e("info 1A: ", t.getMessage());
                Log.e("info 1A: ", "Something got very very wrong");

                errorTextView.setText("Error occured! Try again later");
                error_message_layout.setVisibility(View.VISIBLE);
                errorTextView.setVisibility(View.VISIBLE);
                dialog.dismiss();

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

            if (dialog == null) {
                dialog = new ProgressDialog(activity);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setMessage("Please wait...");
            }

            if (active && !dialog.isShowing()) {
                dialog.show();
            } else {
                dialog.dismiss();
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
        APIRequests apiRequests = APIClient.getWalletInstance();
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;



        Call<WalletTransaction> call = apiRequests.creditUser(access_token,merchantId+"",amount,flwRef,"External Purchase","flutterwave",thirdparty_id);

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
