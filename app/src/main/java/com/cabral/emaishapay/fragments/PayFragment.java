package com.cabral.emaishapay.fragments;

import android.content.Context;
import android.os.Bundle;
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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.cabral.emaishapay.DailogFragments.PurchasePreview;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.models.WalletPurchase;

public class PayFragment extends Fragment {
    TextView mechantIdTextView, text_coupon;
    EditText totalAmountTxt, couponAmout;
    LinearLayout layout_coupon,layoutEmaishaCard,layoutMobileMoney,layoutBankCards;
    Spinner spPaymentMethod;
    Button saveBtn;
    FragmentManager fm;
    private Context context;
    AppBarConfiguration appBarConfiguration;
    private Toolbar toolbar;

   public PayFragment(Context context, FragmentManager supportFragmentManager){
      this.context=context;
      this.fm=supportFragmentManager;
    }

    public PayFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.fragment_wallet_pay, container, false);

        initializeForm(view);
        return view;
    }

//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // Get the layout inflater
//        LayoutInflater inflater = getLayoutInflater();
//        // Inflate and set the layout for the dialog
//        // Pass null as the parent view because its going in the dialog layout
//         View view =inflater.inflate(R.layout.fragment_wallet_pay, null);
//         builder.setView(view);
//         initializeForm( view);
//
//        ImageView close = view.findViewById(R.id.wallet_buy_close);
//        close.setOnClickListener(v -> dismiss());
//
//         return builder.create();
//    }

    public void initializeForm(View view) {
            totalAmountTxt = view.findViewById(R.id.txt_crop_bill_total);
            couponAmout= view.findViewById(R.id.txt_wallet_bill_coupon);
            layout_coupon= view.findViewById(R.id.layout_coupon);
            mechantIdTextView = view.findViewById(R.id.txt_wallet_purchase_mechant_id);
            saveBtn = view.findViewById(R.id.btn_save);
            text_coupon= view.findViewById(R.id.txt_bill_by_coupon);

            toolbar = view.findViewById(R.id.toolbar_wallet_pay_merchant);
            layoutEmaishaCard = view.findViewById(R.id.layout_emaisha_card);
            layoutMobileMoney = view.findViewById(R.id.layout_mobile_number);
            layoutBankCards = view.findViewById(R.id.layout_bank_cards);
            spPaymentMethod = view.findViewById(R.id.sp_payment_method);


        spPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //Change selected text color
                    ((TextView) view).setTextColor(getResources().getColor(R.color.textColor));
                    //((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);//Change selected text size
                } catch (Exception e) {

                }
                if(position==0){
                    layoutMobileMoney.setVisibility(View.GONE);
                    layoutEmaishaCard.setVisibility(View.GONE);
                    layoutBankCards.setVisibility(View.GONE);
                }
                else if(position==1){
                    layoutMobileMoney.setVisibility(View.GONE);
                    layoutEmaishaCard.setVisibility(View.GONE);
                    layoutBankCards.setVisibility(View.GONE);
                }
                else if(position==2){
                    layoutMobileMoney.setVisibility(View.GONE);
                    layoutEmaishaCard.setVisibility(View.VISIBLE);
                    layoutBankCards.setVisibility(View.GONE);
                }
                else if(position==3){
                    layoutMobileMoney.setVisibility(View.VISIBLE);
                    layoutEmaishaCard.setVisibility(View.GONE);
                    layoutBankCards.setVisibility(View.GONE);
                }
                else if(position==4){
                    layoutMobileMoney.setVisibility(View.GONE);
                    layoutEmaishaCard.setVisibility(View.GONE);
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
        float amount = Float.parseFloat(totalAmountTxt.getText().toString());
        if(amount>0 && !mechantIdTextView.getText().toString().isEmpty()){
            WalletPurchase.getInstance().setMechantId(mechantIdTextView.getText().toString());
            WalletPurchase.getInstance().setAmount(amount);
            WalletPurchase.getInstance().setCoupon(couponAmout.getText().toString());
            FragmentTransaction ft = this.fm.beginTransaction();
            Fragment prev =this.fm.findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            // Create and show the dialog.
            DialogFragment buyfoodPreviewDailog =new PurchasePreview(context);
            buyfoodPreviewDailog.show( ft, "dialog");
        }
        else{
            Log.d("ITEMS ", "NO ITEMS");
        }
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}

