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
import android.widget.Toast;

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

import com.cabral.emaishapay.R;
import java.text.NumberFormat;

public class TransferMoney extends Fragment {
    LinearLayout layoutMobileNumber, layoutEmaishaCard,layoutBank;
    Button addMoneyImg;
    TextView mobile_numberTxt, addMoneyTxt;
    Spinner spTransferTo, spSelectBank;
    EditText cardNumberTxt,  cardexpiryTxt,  cardccvTxt, cardHolderNameTxt, etBankBranch, etAccountName, etAccountNumber;
    private double balance;
    FragmentManager fm;
    EditText phoneNumberTxt;
    private Context context;
    AppBarConfiguration appBarConfiguration;
    private Toolbar toolbar;


    public TransferMoney() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
       // getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transfer_money, container, false);

        initializeForm(view);
        return view;
    }

    public void initializeForm(View view) {
        toolbar = view.findViewById(R.id.toolbar_wallet_add_money);
        phoneNumberTxt = view.findViewById(R.id.crop_add_money_mobile_no);
        addMoneyImg = view.findViewById(R.id.button_add_money);
        addMoneyTxt = view.findViewById(R.id.crop_add_money_amount);
        mobile_numberTxt = view.findViewById(R.id.text_mobile_number);
        spTransferTo = view.findViewById(R.id.sp_transfer_to);
        spSelectBank = view.findViewById(R.id.sp_bank);
        cardNumberTxt=view.findViewById(R.id.add_money_creditCardNumber);
        cardHolderNameTxt=view.findViewById(R.id.add_money_holder_name);
        cardexpiryTxt=view.findViewById(R.id.add_money_card_expiry);
        cardccvTxt=view.findViewById(R.id.add_money_card_cvv);
        etBankBranch=view.findViewById(R.id.et_bank_branch);
        etAccountName=view.findViewById(R.id.et_account_name);
        etAccountNumber=view.findViewById(R.id.et_account_number);
        layoutMobileNumber=view.findViewById(R.id.layout_mobile_number);
        layoutEmaishaCard=view.findViewById(R.id.layout_emaisha_card);
        layoutBank=view.findViewById(R.id.layout_bank);

        this.fm=getActivity().getSupportFragmentManager();

        spTransferTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //Change selected text color
                    ((TextView) view).setTextColor(getResources().getColor(R.color.textColor));
                } catch (Exception e) {

                }
                if(position==0){
                    layoutMobileNumber.setVisibility(View.GONE);
                    layoutEmaishaCard.setVisibility(View.GONE);
                    layoutBank.setVisibility(View.GONE);
                }
                else if(position==1){
                    layoutMobileNumber.setVisibility(View.VISIBLE);
                    layoutEmaishaCard.setVisibility(View.GONE);
                    layoutBank.setVisibility(View.GONE);
                }
                else if(position==2){
                    layoutMobileNumber.setVisibility(View.GONE);
                    layoutEmaishaCard.setVisibility(View.VISIBLE);
                    layoutBank.setVisibility(View.GONE);
                }
                else if(position==3){
                    layoutMobileNumber.setVisibility(View.VISIBLE);
                    layoutEmaishaCard.setVisibility(View.GONE);
                    layoutBank.setVisibility(View.GONE);
                }
                else if(position==4){
                    loadTransferBanks();
                    layoutMobileNumber.setVisibility(View.GONE);
                    layoutEmaishaCard.setVisibility(View.GONE);
                    layoutBank.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addMoneyImg.setOnClickListener(v -> {
            String phoneNumber = "0"+phoneNumberTxt.getText().toString();
            String amountEntered = addMoneyTxt.getText().toString();
            float amount = Float.parseFloat(amountEntered);
            float charges = (float) 100; //Transfer Charges

            if (balance >= (amount + charges)) {
                FragmentTransaction ft = fm.beginTransaction();
                Fragment prev = fm.findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                DialogFragment transferPreviewDailog = new com.cabral.emaishapay.DailogFragments.ConfirmTransfer(context, phoneNumber, amount);
                transferPreviewDailog.show(ft, "dialog");
            } else {
                Toast.makeText(getActivity(), "Insufficient Account balance!", Toast.LENGTH_LONG).show();
                Log.e("Error", "Insufficient Account balance!");
            }

        });

    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        if(getArguments()!=null) {
            this.balance = getArguments().getDouble("balance");
        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void  loadTransferBanks(){

    }
}
