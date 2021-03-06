package com.cabral.emaishapay.fragments.wallet_fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cabral.emaishapay.DailogFragments.PurchasePreview;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.models.CardSpinnerItem;
import com.cabral.emaishapay.models.ConfirmationDataResponse;
import com.cabral.emaishapay.models.WalletTransactionInitiation;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;
import com.cabral.emaishapay.utils.ValidateInputs;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayFragment extends Fragment {
    private static final String TAG = "PayFragment";
    TextView mechantIdEdt, text_coupon,text_pay_to_title;
    EditText totalAmountEdt, couponAmout, cardNumberEdt, expiryEdt, cvvEdt, mobileNumberEdt;
    Spinner spinner_select_card;

    DialogLoader dialog;
    private List<CardResponse.Cards> cardlists = new ArrayList();
    ArrayList<CardSpinnerItem> cardItems = new ArrayList<>();
    String card_number,decripted_expiryDate;

    private String cardNo,cvv,expiry,mobileNo,methodOfPayment,key=null;

    LinearLayout card_details_layout;
    CheckBox checkbox_save_card;
    LinearLayout layout_coupon,layoutMobileMoney,layoutBankCards,layoutAmount,layoutMerchantID,layoutPaymentMethod;
    Spinner spPaymentMethod;
    Button saveBtn;
    FragmentManager fm;
    Toolbar toolbar;
    private Context context;
    DialogLoader dialogLoader;


    public PayFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.fragment_wallet_pay, container, false);
        WalletHomeActivity.bottomNavigationView.setVisibility(View.GONE);
        WalletHomeActivity.scanCoordinatorLayout.setVisibility(View.GONE);
        WalletHomeActivity.bottom_navigation_shop.setVisibility(View.GONE);
        this.context=getActivity();
        toolbar=view.findViewById(R.id.toolbar_wallet_pay_merchant);
        dialog = new DialogLoader(getContext());

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
      //  toolbar.setTitle("Pay Merchant");

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.fm=getParentFragmentManager();
        dialogLoader=new DialogLoader(context);
        initializeForm(view);
        return view;
    }


    public void initializeForm(View view) {
        totalAmountEdt = view.findViewById(R.id.txt_bill_total);

        cardNumberEdt = view.findViewById(R.id.pay_bank_CardNumber);
        expiryEdt = view.findViewById(R.id.pay_bank_card_expiry);
        cvvEdt = view.findViewById(R.id.pay_bank_card_cvv);
        mobileNumberEdt = view.findViewById(R.id.pay_mobile_no);

        couponAmout= view.findViewById(R.id.txt_wallet_bill_coupon);
        layout_coupon= view.findViewById(R.id.layout_coupon);
        mechantIdEdt = view.findViewById(R.id.edt_purchase_mechant_id);
        saveBtn = view.findViewById(R.id.btn_save_pay_merchant);
        text_coupon= view.findViewById(R.id.txt_bill_by_coupon);
        layoutMobileMoney = view.findViewById(R.id.layout_mobile_number);
        layoutBankCards = view.findViewById(R.id.layout_bank_cards);
        spPaymentMethod = view.findViewById(R.id.sp_payment_method);
        layoutPaymentMethod = view.findViewById(R.id.payment_method_layout);

        spinner_select_card = view.findViewById(R.id.spinner_select_card_wallet_pay);
        card_details_layout = view.findViewById(R.id.card_details_layout);
        checkbox_save_card = view.findViewById(R.id.checkbox_save_card);

        layoutMerchantID = view.findViewById(R.id.layout_pay_merchant_id);
        layoutAmount = view.findViewById(R.id.layout_pay_merchant_amount);

        text_pay_to_title = view.findViewById(R.id.text_pay_to_title);


        if(getArguments()!=null){
            //from scan and pay
            if(getArguments().getString("scan_pay").equalsIgnoreCase("scan_pay")) {
                spPaymentMethod.setEnabled(false);
                layoutPaymentMethod.setVisibility(View.GONE);
                layoutAmount.setVisibility(View.VISIBLE);
                key = getArguments().getString("scan_pay");
//                WalletHomeActivity.selectSpinnerItemByValue(spPaymentMethod, "eMaisha Pay");
                layoutMerchantID.setVisibility(View.VISIBLE);
            }



        }

        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (filterLongEnough() && !expiryEdt.getText().toString().contains("/")) {
                    expiryEdt.setText(expiryEdt.getText().toString()+"/");
                    int pos = expiryEdt.getText().length();
                    expiryEdt.setSelection(pos);
                }
            }

            private boolean filterLongEnough() {
                return expiryEdt.getText().toString().length() == 2;
            }
        };
        expiryEdt.addTextChangedListener(fieldValidatorTextWatcher);

        spPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Change selected text color
                    ((TextView) view).setTextColor(ContextCompat.getColor(context,R.color.white));
                    //((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);//Change selected text size

                String selectedItem=spPaymentMethod.getSelectedItem().toString();

                if(selectedItem.equalsIgnoreCase("select")){
                    layoutMobileMoney.setVisibility(View.GONE);
                    layoutBankCards.setVisibility(View.GONE);
                    layoutAmount.setVisibility(View.GONE);
                    layoutMerchantID.setVisibility(View.GONE);

                }

                else if(selectedItem.equalsIgnoreCase("eMaisha Pay")){
                    layoutMobileMoney.setVisibility(View.GONE);
                    layoutBankCards.setVisibility(View.GONE);
                    layoutAmount.setVisibility(View.VISIBLE);
                    layoutMerchantID.setVisibility(View.VISIBLE);

                }
                else if(selectedItem.equalsIgnoreCase("Mobile Money")){
                    layoutMobileMoney.setVisibility(View.VISIBLE);
                    layoutBankCards.setVisibility(View.GONE);
                    layoutAmount.setVisibility(View.VISIBLE);
                    layoutMerchantID.setVisibility(View.VISIBLE);

                }
//                else if(selectedItem.equalsIgnoreCase("Bank Cards") || selectedItem.equalsIgnoreCase("eMaisha Card")){
//                    layoutMobileMoney.setVisibility(View.GONE);
//                    layoutBankCards.setVisibility(View.VISIBLE);
//                    layoutAmount.setVisibility(View.VISIBLE);
//                    layoutMerchantID.setVisibility(View.VISIBLE);
//                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    //Change selected text color
                    ((TextView) view).setTextColor(ContextCompat.getColor(context,R.color.white));
                    //((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);//Change selected text size


                if (spinner_select_card.getSelectedItem().toString().equalsIgnoreCase("Add New")){
                    //call add card
                    card_details_layout.setVisibility(View.VISIBLE);

                }
                else {
                    for(int i = 0; i<cardItems.size();i++){
                        if(cardItems.get(i).toString().equalsIgnoreCase(spinner_select_card.getSelectedItem().toString())){
                            expiry =  cardItems.get(i).getExpiryDate();
                            cardNo = cardItems.get(i).getCardNumber();
                            cvv = cardItems.get(i).getCvv();

                        }
                    }
                   // Log.d(TAG, "onItemSelected: expiry"+expiryDate+"card_no"+card_no+"cvv"+cvv);
                    card_details_layout.setVisibility(View.GONE);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        spinner_select_card.setOnItemSelectedListener(onItemSelectedListener);

        getCards();

        text_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layout_coupon.getVisibility()== View.VISIBLE){
                    layout_coupon.setVisibility(View.GONE);
                    couponAmout.setText(null);
                }
                else{
                    layout_coupon.setVisibility(View.VISIBLE);
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm()){
                    processPayment();
                }


            }
        });
    }

    public void processPayment(){
        float amount = Float.parseFloat(totalAmountEdt.getText().toString());
        if( layoutPaymentMethod.getVisibility() == View.VISIBLE) {
            methodOfPayment = spPaymentMethod.getSelectedItem().toString();
        }else{
            methodOfPayment = "eMaisha Pay";
        }
        if(methodOfPayment.equalsIgnoreCase("eMaisha Pay") && validateWalletPurchase()){
            getMechantName(amount);
        }
        else if(methodOfPayment.equals("Mobile Money") && validateMobileMoneyPurchase()) {
            getMechantName(amount);
        }

    }

    private void navigateToPreviewDialog(float amount, String businessName) {
        WalletTransactionInitiation.getInstance().setMechantId(mechantIdEdt.getText().toString());
        WalletTransactionInitiation.getInstance().setMobileNumber( mobileNumberEdt.getText().toString());
        WalletTransactionInitiation.getInstance().setMethodOfPayment(methodOfPayment);
        WalletTransactionInitiation.getInstance().setCoupon(couponAmout.getText().toString());
        WalletTransactionInitiation.getInstance().setAmount(amount);
//        WalletTransactionInitiation.getInstance().setPayTo(sp_payment_pay_to.getSelectedItem().toString());
        FragmentTransaction ft = this.fm.beginTransaction();
        Fragment prev =this.fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment PreviewDailog =new PurchasePreview(businessName);
        PreviewDailog.show( ft, "dialog");
    }

    public void getMechantName(float amount){
        dialogLoader.showProgressDialog();

        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        String merchantId = mechantIdEdt.getText().toString();
        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<ConfirmationDataResponse> call = apiRequests.
                getMerchant(access_token,merchantId,request_id,category,"getMerchantForUser");

        call.enqueue(new Callback<ConfirmationDataResponse>() {
            @Override
            public void onResponse(Call<ConfirmationDataResponse> call, Response<ConfirmationDataResponse> response) {

                dialogLoader.hideProgressDialog();
                if(response.isSuccessful() && response.body().getStatus().equalsIgnoreCase("1")){
                    navigateToPreviewDialog( amount,response.body().getData().getBusinessName());
                }else if(response.isSuccessful()){
                    Snackbar snack=Snackbar.make(saveBtn,response.body().getMessage(), Snackbar.LENGTH_LONG);
                    View view = snack.getView();
                    FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                    params.gravity = Gravity.CENTER;
                    params.topMargin=150;
                    view.setLayoutParams(params);
                    snack.show();
                }else if(response.code()==401){
                    TokenAuthFragment.startAuth( true);
                }else{
                    Log.e("info : ", "Something got very wrong");
                }

            }

            @Override
            public void onFailure(Call<ConfirmationDataResponse> call, Throwable t) {
                Log.e("info : ", t.getMessage());
                Log.e("info : ", "Something got very very wrong");
                dialogLoader.hideProgressDialog();

            }
        });


    }


    private boolean validateWalletPurchase() {
        if ( TextUtils.isEmpty(mechantIdEdt.getText()) ) {
            mechantIdEdt.setError("Enter merchant id");
            return false;
        }  else if( TextUtils.isEmpty(totalAmountEdt.getText()) ){
            totalAmountEdt.setError(getString(R.string.invalid_number));
            return false;
        }  else {
            return true;
        }
    }

    private boolean validateMobileMoneyPurchase() {
        if (!ValidateInputs.isValidPhoneNo(mobileNumberEdt.getText().toString().trim())) {
            mobileNumberEdt.setError(getString(R.string.invalid_phone));
            return false;
        } else if (mechantIdEdt.getText().toString().trim().isEmpty()) {
            mechantIdEdt.setError("Required!");
            return false;
        }  else if (Integer.parseInt(totalAmountEdt.getText().toString().trim())<0) {
            totalAmountEdt.setError(getString(R.string.invalid_number));
            return false;
        }  else {
            return true;
        }
    }

    public boolean validateForm(){
        if( TextUtils.isEmpty(totalAmountEdt.getText())){
            totalAmountEdt.setError("Invalid Amount");
            return false;
        }else if( layoutPaymentMethod.getVisibility() == View.VISIBLE) {
            if (spPaymentMethod.getSelectedItem().toString().equalsIgnoreCase("select")) {
                Toast.makeText(context, "Please select mode of payment", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        }else{
            return  true;
        }

    }

    public void getCards(){
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        /******************RETROFIT IMPLEMENTATION***********************/
        dialog.showProgressDialog();
        Call<CardResponse> call = APIClient.getWalletInstance(getContext()).getCards(access_token,request_id,category,"getCards");
        call.enqueue(new Callback<CardResponse>() {
            @Override
            public void onResponse(Call<CardResponse> call, Response<CardResponse> response) {
                if(response.isSuccessful()){
                    dialog.hideProgressDialog();

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
                        for(int i =0; i<cardlists.size();i++) {
                             card_number =  cardlists.get(i).getCard_number();
                            decripted_expiryDate =  cardlists.get(i).getExpiry();
                            String id =  cardlists.get(i).getId();
                            String cvv =  cardlists.get(i).getCvv();
                            Log.d(TAG, "onResponse: decripter_card_no" + card_number);

                            if (card_number.length() > 4) {

                                String first_four_digits = (card_number.substring(0, 4));
                                String last_four_digits = (card_number.substring(card_number.length() - 4));
                                final String decripted_card_number = first_four_digits + "*******" + last_four_digits;

                                Log.d(TAG, "onResponse: masked " + decripted_card_number);
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
                        Log.d(TAG, "onResponse: cards"+cardItems);

                        ArrayAdapter<CardSpinnerItem> cardListAdapter = new ArrayAdapter<CardSpinnerItem>(getContext(),  android.R.layout.simple_dropdown_item_1line, cardItems);
//                        cardListAdapter = new CardSpinnerAdapter(cardItems, "New", getContext());
                        spinner_select_card.setAdapter(cardListAdapter);
                        dialog.hideProgressDialog();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }



                }else if (response.code() == 401) {

                    TokenAuthFragment.startAuth( true);

                    if (response.errorBody() != null) {
                        Log.e("info", String.valueOf(response.errorBody()));
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

}

