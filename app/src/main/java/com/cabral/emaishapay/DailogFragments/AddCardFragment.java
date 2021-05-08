package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.databinding.WalletAddMoneyVisaBinding;
import com.cabral.emaishapay.fragments.wallet_fragments.TokenAuthFragment;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.network.api_helpers.APIClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddCardFragment extends DialogFragment {

    private Context context;
    private String id;
    DialogLoader dialogLoader;
    WalletAddMoneyVisaBinding binding;

    public AddCardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
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
        binding= DataBindingUtil.inflate(inflater,R.layout.wallet_add_money_visa,null,false);

        binding.addMoneyAmountLayout .setVisibility(View.GONE);
        binding.layoutPurpose .setVisibility(View.GONE);

        dialogLoader=new DialogLoader(context);



        if(getArguments()!=null){
            String account_name = getArguments().getString("account_name");
            String card_number = getArguments().getString("account_number");
            String cvv = getArguments().getString("cvv");
            String expiry_date = getArguments().getString("expiry");
             id = getArguments().getString("id");

            setDataToUpdate(account_name,card_number,cvv,expiry_date);

            binding.deleteCard.setOnClickListener(v -> {
              deleteCardRetrofitCall();
            });
        }else {
            binding.addMoneyTitle.setText("ADD CARD");
            binding.buttonAddMoney.setText("SAVE CARD");
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
                if (filterLongEnough() && !binding.addMoneyCardExpiry.getText().toString().contains("/")) {
                    binding.addMoneyCardExpiry.setText(binding.addMoneyCardExpiry.getText().toString()+"/");
                    int pos = binding.addMoneyCardExpiry.getText().length();
                    binding.addMoneyCardExpiry.setSelection(pos);
                }
            }

            private boolean filterLongEnough() {
                return binding.addMoneyCardExpiry.getText().toString().length() == 2;
            }
        };

        binding.addMoneyCardExpiry.addTextChangedListener(fieldValidatorTextWatcher);

        binding.buttonAddMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCard();
            }
        });


        builder.setView(binding.getRoot());
        Dialog dialog = builder.create();

        ImageView close = binding.getRoot().findViewById(R.id.wallet_deposit_close);
        close.setOnClickListener(v -> dismiss());

        return dialog;

    }

    private void setDataToUpdate(String account_name, String card_number, String cvv, String expiry_date) {
        binding.addMoneyCreditCardNumber.setTransformationMethod(PasswordTransformationMethod.getInstance());
        binding.addMoneyCardCvv.setTransformationMethod(PasswordTransformationMethod.getInstance());

        binding.addMoneyHolderName.setText(account_name);
        binding.addMoneyCreditCardNumber.setText(card_number);
        binding.addMoneyCardCvv.setText(cvv);
        binding.addMoneyCardExpiry.setText(expiry_date);
        binding.addMoneyTitle.setText("EDIT CARD");
        binding.buttonAddMoney.setText("UPDATE");
        binding.deleteCard.setVisibility(View.VISIBLE);
    }

    private void saveCard() {
        if (validateEntries()) {
            dialogLoader.showProgressDialog();

            String identifier = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
            String card_number =  binding.addMoneyCreditCardNumber.getText().toString().trim();
            String cvv = binding.addMoneyCardCvv.getText().toString().trim();
            String expiry = binding.addMoneyCardExpiry.getText().toString();
            String account_name = binding.addMoneyHolderName.getText().toString();
            Call<CardResponse> call;


            //check if the button text is save card
            if(binding.buttonAddMoney.getText().toString().equalsIgnoreCase("SAVE CARD")) {
                String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
                String request_id = WalletHomeActivity.generateRequestId();
                String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());

                call = APIClient.getWalletInstance(getContext()).
                        saveCardInfo(access_token,identifier, card_number, cvv, expiry, account_name, getString(R.string.currency), request_id,category,"saveCard");


            }else{
                //call update card endpoint
                String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
                String request_id = WalletHomeActivity.generateRequestId();
                String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
                call = APIClient.getWalletInstance(getContext()).updateCardInfo(access_token,id,identifier, card_number, cvv, expiry, account_name,request_id,category,"updateCard");
            }

           getSaveCardRetrofitResponse(call);

        }
    }

    private void getSaveCardRetrofitResponse(Call<CardResponse> call) {
        call.enqueue(new Callback<CardResponse>() {
            @Override
            public void onResponse(Call<CardResponse> call, Response<CardResponse> response) {
                dialogLoader.hideProgressDialog();

                if (response.isSuccessful()) {
                    AddCardFragment.this.dismiss();
                    String message = response.body().getMessage();
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                    //To CardListFragment
                    WalletHomeActivity.navController.popBackStack(R.id.walletHomeFragment2,false);
                    WalletHomeActivity.navController.navigate(R.id.action_walletHomeFragment2_to_cardListFragment);
                } else if (response.code() == 401) {
                    TokenAuthFragment.startAuth(true);
                }
            }

            @Override
            public void onFailure(Call<CardResponse> call, Throwable t) {
                dialogLoader.hideProgressDialog();

            }
        });
    }

    private void deleteCardRetrofitCall() {
        dialogLoader.showProgressDialog();

        //call retrofit method for deleting card
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        /*************RETROFIT IMPLEMENTATION**************/

        Call<CardResponse> call = APIClient.getWalletInstance(getContext()).deleteCard(id,access_token,request_id,category,"deleteCard");

        call.enqueue(new Callback<CardResponse>() {
            @Override
            public void onResponse(Call<CardResponse> call, Response<CardResponse> response) {
                dialogLoader.hideProgressDialog();
                if (response.isSuccessful()) {

                    if (response.body().getStatus() == 0) {
                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        String message = response.body().getMessage();
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        AddCardFragment.this.dismiss();
                        //To CardListFragment
                        WalletHomeActivity.navController.popBackStack(R.id.walletHomeFragment2,false);
                        WalletHomeActivity.navController.navigate(R.id.action_walletHomeFragment2_to_cardListFragment);
                    }

                }else if(response.code()==401){
                    Toast.makeText(context, "session expired", Toast.LENGTH_LONG).show();
                    //redirect to auth
                    TokenAuthFragment.startAuth( true);

                }
            }


            @Override
            public void onFailure(Call<CardResponse> call, Throwable t) {
                dialogLoader.hideProgressDialog();

            }
        });
    }

    public boolean validateEntries(){

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MM");
        DateFormat yearDateFormat = new SimpleDateFormat("yy");

        int mm = Integer.parseInt(dateFormat.format(date));
        int yy = Integer.parseInt(yearDateFormat.format(date));
        String expMonth = binding.addMoneyCardExpiry.getText().toString().substring(0,2);
        String expYear = binding.addMoneyCardExpiry.getText().toString().substring(binding.addMoneyCardExpiry.getText().toString().length() - 2);


        boolean check = true;


        if (binding.addMoneyHolderName.getText().toString().trim() == null || binding.addMoneyHolderName.getText().toString().trim().isEmpty()) {
            check = false;
            binding.addMoneyHolderName.setError("Please enter valid value");


        } else if (binding.addMoneyCreditCardNumber.getText().toString().trim() == null || binding.addMoneyCreditCardNumber.getText().toString().trim().isEmpty()
                || binding.addMoneyCreditCardNumber.getText().toString().trim().length()<13 ){
            check = false;
            binding.addMoneyCreditCardNumber.setError("Please enter valid value");

        }

        else if (binding.addMoneyCardExpiry.getText().toString().trim() == null || binding.addMoneyCardExpiry.getText().toString().trim().isEmpty() ||
                binding.addMoneyCardExpiry.getText().toString().length()<5){
            check = false;
            binding.addMoneyCardExpiry.setError("Please enter valid value");

        }

        else if (binding.addMoneyCardCvv.getText().toString().trim() == null || binding.addMoneyCardCvv.getText().toString().trim().isEmpty()
                || binding.addMoneyCardCvv.getText().toString().trim().length()<3 ) {
            check = false;
            binding.addMoneyCreditCardNumber.setError("Please enter valid value");

        } else if(binding.addMoneyCardExpiry.getText().toString().length()>4 && Integer.parseInt(expYear) < yy) {

                check = false;
                 binding.addMoneyCardExpiry.setError("Card is expired");
                Toasty.error(context, "Card is expired", Toast.LENGTH_LONG).show();
                Log.d("CARD IS EXPIRED","DATE *"+Integer.parseInt(expMonth)+"/"+Integer.parseInt(expYear)+"* IS SAME OR GREATER THAN *"+mm+"*/*"+yy+"*");

        }

        else if(binding.addMoneyCardExpiry.getText().toString().length()>4 && (Integer.parseInt(expYear) == yy && Integer.parseInt(expMonth) <= mm )){
                check = false;
                binding.addMoneyCardExpiry.setError("Card is expired");
                Toasty.error(context, "Card is expired", Toast.LENGTH_LONG).show();
                Log.d("CARD IS EXPIRED","DATE *"+Integer.parseInt(expMonth)+"/"+Integer.parseInt(expYear)+"* IS SAME OR GREATER THAN *"+mm+"*/*"+yy+"*");

        }

        return check;

    }


}