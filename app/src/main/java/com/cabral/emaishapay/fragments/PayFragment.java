package com.cabral.emaishapay.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cabral.emaishapay.DailogFragments.PurchasePreview;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.models.WalletTransactionInitiation;
import com.cabral.emaishapay.utils.ValidateInputs;

public class PayFragment extends Fragment {
    TextView mechantIdEdt, text_coupon;
    EditText totalAmountEdt, couponAmout, cardNumberEdt, expiryEdt, cvvEdt, mobileNumberEdt;

    LinearLayout layout_coupon,layoutMobileMoney,layoutBankCards;
    Spinner spPaymentMethod;
    Button saveBtn;
    FragmentManager fm;
    private Context context;


    public PayFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.fragment_wallet_pay, container, false);
        this.context=getActivity();
        Toolbar toolbar=view.findViewById(R.id.toolbar_wallet_pay_merchant);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Pay Merchant");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.fm=getParentFragmentManager();
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
            saveBtn = view.findViewById(R.id.btn_save);
            text_coupon= view.findViewById(R.id.txt_bill_by_coupon);
            layoutMobileMoney = view.findViewById(R.id.layout_mobile_number);
            layoutBankCards = view.findViewById(R.id.layout_bank_cards);
            spPaymentMethod = view.findViewById(R.id.sp_payment_method);

            
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
                try {
                    //Change selected text color
                    ((TextView) view).setTextColor(getResources().getColor(R.color.textColor));
                    //((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);//Change selected text size
                } catch (Exception e) {

                }
                String selectedItem=spPaymentMethod.getSelectedItem().toString();
                if(selectedItem.equalsIgnoreCase("wallet")){
                    layoutMobileMoney.setVisibility(View.GONE);
                    layoutBankCards.setVisibility(View.GONE);
                }
                else if(selectedItem.equalsIgnoreCase("Mobile Money")){
                    layoutMobileMoney.setVisibility(View.VISIBLE);
                    layoutBankCards.setVisibility(View.GONE);
                }
                else if(selectedItem.equalsIgnoreCase("Bank Cards") || selectedItem.equalsIgnoreCase("eMaisha Card")){
                    layoutMobileMoney.setVisibility(View.GONE);
                    layoutBankCards.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



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
                    processPayment();

                }
            });
    }

    public void processPayment(){
        float amount = Float.parseFloat(totalAmountEdt.getText().toString());
        String cardNo = cardNumberEdt.getText().toString();
        String cvv = cvvEdt.getText().toString();
        String expiry = expiryEdt.getText().toString();
        String mobileNo = mobileNumberEdt.getText().toString();
        String methodOfPayment= spPaymentMethod.getSelectedItem().toString();

        if(methodOfPayment.equals("Wallet"))
            validateWalletPurchase();
        else if(methodOfPayment.equals("Bank Cards") || methodOfPayment.equals("eMaisha Card") )
            validateBankCardPurchase();
        else if(methodOfPayment.equals("Mobile Money"))
            validateMobileMoneyPurchase();


        if(amount>0 && !mechantIdEdt.getText().toString().isEmpty()){
            WalletTransactionInitiation.getInstance().setMechantId(mechantIdEdt.getText().toString());
            WalletTransactionInitiation.getInstance().setAmount(amount);
            WalletTransactionInitiation.getInstance().setCardNumber(cardNo);
            WalletTransactionInitiation.getInstance().setCardExpiry(expiry);
            WalletTransactionInitiation.getInstance().setCvv(cvv);
            WalletTransactionInitiation.getInstance().setMobileNumber(mobileNo);
            WalletTransactionInitiation.getInstance().setMethodOfPayment(methodOfPayment);
            WalletTransactionInitiation.getInstance().setCoupon(couponAmout.getText().toString());

            
            FragmentTransaction ft = this.fm.beginTransaction();
            Fragment prev =this.fm.findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            // Create and show the dialog.
            DialogFragment PreviewDailog =new PurchasePreview(context);
            PreviewDailog.show( ft, "dialog");
        }
        else{
            Log.d("ITEMS ", "NO ITEMS");
        }
    }

    private boolean validateBankCardPurchase() {
        if (!ValidateInputs.isValidAccountNo(cardNumberEdt.getText().toString().trim())) {
            cardNumberEdt.setError(getString(R.string.invalid_credit_card));
            return false;
        } else if (!ValidateInputs.isValidCvv(cvvEdt.getText().toString().trim())) {
            cvvEdt.setError(getString(R.string.invalid_card_cvv));
            return false;
        } else if (Integer.parseInt(totalAmountEdt.getText().toString().trim())<0) {
            totalAmountEdt.setError(getString(R.string.invalid_number));
            return false;
        } else if (!ValidateInputs.isValidCardExpiry(expiryEdt.getText().toString().trim())){
            expiryEdt.setError(getString(R.string.invalid_expiry));
            return false;
        } else if (Integer.parseInt(totalAmountEdt.getText().toString().trim())<0) {
            totalAmountEdt.setError(getString(R.string.invalid_number));
            return false;
        }  else {
            return true;
        }
    }

    private boolean validateWalletPurchase() {
        if (Integer.parseInt(mechantIdEdt.getText().toString().trim())<0) {
            totalAmountEdt.setError(getString(R.string.invalid_number));
            return false;
        }  else if (Integer.parseInt(totalAmountEdt.getText().toString().trim())<0) {
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
        } else if (Integer.parseInt(mechantIdEdt.getText().toString().trim())<0) {
            totalAmountEdt.setError(getString(R.string.invalid_number));
            return false;
        }  else if (Integer.parseInt(totalAmountEdt.getText().toString().trim())<0) {
            totalAmountEdt.setError(getString(R.string.invalid_number));
            return false;
        }  else {
            return true;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

}

