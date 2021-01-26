package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.fragments.CardListFragment;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.utils.CryptoUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddCardFragment extends DialogFragment {

    EditText etName, etCardNumber, etCvv, etExpiryDate;
    TextView txtTitle;
    LinearLayout amountLayout, purporseLayout;

    Button btnSaveCard,delete_card;
    private Context context;

    private String id;

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
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.wallet_add_money_visa, null);
        etName = view.findViewById(R.id.add_money_holder_name);
        etCardNumber = view.findViewById(R.id.add_money_creditCardNumber);
        etCvv = view.findViewById(R.id.add_money_card_cvv);
        etExpiryDate = view.findViewById(R.id.add_money_card_expiry);
        btnSaveCard = view.findViewById(R.id.button_add_money);
        amountLayout = view.findViewById(R.id.add_money_amount_layout);
        purporseLayout = view.findViewById(R.id.layout_purpose);
        txtTitle = view.findViewById(R.id.add_money_title);
        delete_card = view.findViewById(R.id.delete_card);

        amountLayout.setVisibility(View.GONE);
        purporseLayout.setVisibility(View.GONE);
        if(getArguments()!=null){
            String account_name = getArguments().getString("account_name");
            String card_number = getArguments().getString("account_number");
            String cvv = getArguments().getString("cvv");
            String expiry_date = getArguments().getString("expiry");
             id = getArguments().getString("id");

            //set corresponsding edit texts;
            etName.setText(account_name);
            etCardNumber.setText(card_number);
            etCvv.setText(cvv);
            etExpiryDate.setText(expiry_date);
            txtTitle.setText("EDIT CARD");
            btnSaveCard.setText("UPDATE");
            delete_card.setVisibility(View.VISIBLE);

            delete_card.setOnClickListener(v -> {
                ProgressDialog dialog;
                dialog = new ProgressDialog(context);
                dialog.setIndeterminate(true);
                dialog.setMessage("Please Wait..");
                dialog.setCancelable(false);
                dialog.show();

                //call retrofit method for deleting card
                String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
                /*************RETROFIT IMPLEMENTATION**************/
                Call<CardResponse> call = APIClient.getWalletInstance().deleteCard(id,access_token);
                call.enqueue(new Callback<CardResponse>() {
                    @Override
                    public void onResponse(Call<CardResponse> call, Response<CardResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getStatus() == 0) {
                                dialog.dismiss();
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();

                            } else {
                                String message = response.body().getMessage();
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                                getActivity().getSupportFragmentManager().popBackStack();

                                Fragment fragment = new CardListFragment();
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .hide(((WalletHomeActivity) getActivity()).currentFragment)
                                        .replace(R.id.wallet_home_container, fragment)
                                        .addToBackStack(null).commit();

                                dialog.dismiss();
                            }

                        }else if(response.code()==401){
                            dialog.dismiss();
                            Toast.makeText(context, "session expired", Toast.LENGTH_LONG).show();

                            //redirect to auth
                            TokenAuthActivity.startAuth(context, true);
                        }
                    }


                    @Override
                    public void onFailure(Call<CardResponse> call, Throwable t) {
                        dialog.dismiss();

                    }
                });
            });
        }else {
            txtTitle.setText("ADD CARD");
            btnSaveCard.setText("SAVE CARD");
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
                if (filterLongEnough() && !etExpiryDate.getText().toString().contains("/")) {
                    etExpiryDate.setText(etExpiryDate.getText().toString()+"/");
                    int pos = etExpiryDate.getText().length();
                    etExpiryDate.setSelection(pos);
                }
            }

            private boolean filterLongEnough() {
                return etExpiryDate.getText().toString().length() == 2;
            }
        };
        etExpiryDate.addTextChangedListener(fieldValidatorTextWatcher);



        btnSaveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                    if (validateEntries()) {
                        ProgressDialog dialog;
                        dialog = new ProgressDialog(context);
                        dialog.setIndeterminate(true);
                        dialog.setMessage("Please Wait..");
                        dialog.setCancelable(false);
                        dialog.show();

                        String identifier = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
                        String card_number = etCardNumber.getText().toString().trim();
                        String cvv = etCvv.getText().toString().trim();
                        String expiry = etExpiryDate.getText().toString();
                        String account_name = etName.getText().toString();

                        /**********ENCRPT CARD DETAILS****************/
                        CryptoUtil encrypter = new CryptoUtil(BuildConfig.ENCRYPTION_KEY, getString(R.string.iv));
                        String hash_card_number = encrypter.encrypt(card_number);
                        String hash_cvv = encrypter.encrypt(cvv);
                        String hash_expiry = encrypter.encrypt(expiry);
                        String hash_account_name = encrypter.encrypt(account_name);

                        //check if the button text is save card
                        if(btnSaveCard.getText().toString().equalsIgnoreCase("SAVE CARD")) {

                        /*************RETROFIT IMPLEMENTATION**************/
                        Call<CardResponse> call = APIClient.getWalletInstance().saveCardInfo(identifier, hash_card_number, hash_cvv, hash_expiry, hash_account_name);
                        call.enqueue(new Callback<CardResponse>() {
                            @Override
                            public void onResponse(Call<CardResponse> call, Response<CardResponse> response) {
                                if (response.isSuccessful()) {
                                    String message = response.body().getMessage();
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                    getActivity().getSupportFragmentManager().popBackStack();

                                    Fragment fragment = new CardListFragment();
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .hide(((WalletHomeActivity) getActivity()).currentFragment)
                                            .replace(R.id.wallet_home_container, fragment)
                                            .addToBackStack(null).commit();

                                    //call card list fragment
//                                navController.navigate(R.id.action_addCardFragment_to_cardListFragment);


                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(Call<CardResponse> call, Throwable t) {
                                dialog.dismiss();

                            }
                        });

                    }else{
                            //call update card endpoint
                            String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
                            /*************RETROFIT IMPLEMENTATION**************/
                            Call<CardResponse> call = APIClient.getWalletInstance().updateCardInfo(access_token,id,identifier, hash_card_number, hash_cvv, hash_expiry, hash_account_name);
                            call.enqueue(new Callback<CardResponse>() {
                                @Override
                                public void onResponse(Call<CardResponse> call, Response<CardResponse> response) {
                                    if (response.isSuccessful()) {
                                        String message = response.body().getMessage();
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                                        //redirect to card list fragment
                                        getActivity().getSupportFragmentManager().popBackStack();
                                        Fragment fragment = new CardListFragment();
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                                        if (((WalletHomeActivity) getActivity()).currentFragment != null)
                                            fragmentManager.beginTransaction()
                                                    .hide(((WalletHomeActivity) getActivity()).currentFragment)
                                                    .replace(R.id.wallet_home_container, fragment)
                                                    .addToBackStack(null).commit();


                                        dialog.dismiss();
                                    } else if (response.code() == 401) {
                                        dialog.dismiss();
                                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();

                                        //redirect to auth
                                        TokenAuthActivity.startAuth(context, true);
                                    }
                                }

                                @Override
                                public void onFailure(Call<CardResponse> call, Throwable t) {
                                    dialog.dismiss();

                                }
                            });


                        }
                }
            }
        });


        builder.setView(view);
        Dialog dialog = builder.create();
//        dialog.setCanceledOnTouchOutside(false);
//        setCancelable(false);

        ImageView close = view.findViewById(R.id.wallet_deposit_close);
        close.setOnClickListener(v -> dismiss());

        return dialog;

    }

    public boolean validateEntries(){

        boolean check = true;
        if (etName.getText().toString().trim() == null || etName.getText().toString().trim().isEmpty()) {
            check = false;
            etName.setError("Please enter valid value");


        } else if (etCardNumber.getText().toString().trim() == null || etCardNumber.getText().toString().trim().isEmpty()
                || etCardNumber.getText().toString().trim().length()<13 ){
            check = false;
            etCardNumber.setError("Please enter valid value");

        }

        else if (etExpiryDate.getText().toString().trim() == null || etExpiryDate.getText().toString().trim().isEmpty()){
            check = false;
            etExpiryDate.setError("Please select valid value");

        }

        else if (etCvv.getText().toString().trim() == null || etCvv.getText().toString().trim().isEmpty()
                || etCvv.getText().toString().trim().length()<3 ) {
            check = false;
            etCardNumber.setError("Please enter valid value");

        }
        return check;



    }
}