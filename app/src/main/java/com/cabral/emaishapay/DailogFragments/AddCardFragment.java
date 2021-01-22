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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.utils.CryptoUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddCardFragment extends DialogFragment {

    EditText etName, etCardNumber, etCvv;
    TextView txtExpiryDate;
    Button btnSaveCard;
    private Context context;
    private NavController navController;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_add_card, null);
        etName = view.findViewById(R.id.txt_card_holder_name);
        etCardNumber = view.findViewById(R.id.txt_card_number);
        etCvv = view.findViewById(R.id.card_cvv);
        txtExpiryDate = view.findViewById(R.id.card_expiry);
        btnSaveCard = view.findViewById(R.id.save_card_button);

        btnSaveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog dialog;
                dialog = new ProgressDialog(context);
                dialog.setIndeterminate(true);
                dialog.setMessage("Please Wait..");
                dialog.setCancelable(false);
                dialog.show();
                if(validateEntries()){


                    String identifier = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
                    String card_number = etCardNumber.getText().toString().trim();
                    String cvv = etCvv.getText().toString().trim();
                    String expiry =txtExpiryDate.getText().toString();
                    String account_name = etName.getText().toString();

                    /**********ENCRPT CARD DETAILS****************/
                    CryptoUtil encrypter =new CryptoUtil(BuildConfig.ENCRYPTION_KEY,getString(R.string.iv));
                    String hash_card_number=encrypter.encrypt(card_number);
                    String hash_cvv=encrypter.encrypt(cvv);
                    String hash_expiry=encrypter.encrypt(expiry);
                    String hash_account_name=encrypter.encrypt(account_name);

                    /*************RETROFIT IMPLEMENTATION**************/
                    Call<CardResponse> call = APIClient.getWalletInstance().saveCardInfo(identifier,hash_card_number,hash_cvv,hash_expiry,hash_account_name);
                    call.enqueue(new Callback<CardResponse>() {
                        @Override
                        public void onResponse(Call<CardResponse> call, Response<CardResponse> response) {
                            if(response.isSuccessful()){
                                String message = response.body().getMessage();
                                Toast.makeText(context,message,Toast.LENGTH_LONG).show();

                                //call card list fragment
                                navController.navigate(R.id.action_addCardFragment_to_cardListFragment);



                               
                            dialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<CardResponse> call, Throwable t) {
                            dialog.dismiss();

                        }
                    });

                }
            }
        });


        builder.setView(view);
        Dialog dialog = builder.create();
//        dialog.setCanceledOnTouchOutside(false);
//        setCancelable(false);

        ImageView close = view.findViewById(R.id.add_card_close);
        close.setOnClickListener(v -> dismiss());

        return dialog;

    }

    public boolean validateEntries(){

        if (etName.getText().toString().trim() == null || etName.getText().toString().trim().isEmpty()) {
            etName.setError("Please enter valid value");
            return false;

        } else if (etCardNumber.getText().toString().trim() == null || etCardNumber.getText().toString().trim().isEmpty()
                || etCardNumber.getText().toString().trim().length()<13 ){
            etCardNumber.setError("Please enter valid value");
            return false;
        }

        else if (txtExpiryDate.getText().toString().trim() == null || txtExpiryDate.getText().toString().trim().isEmpty()){
            txtExpiryDate.setError("Please select valid value");
            return false;
        }

        else if (etCvv.getText().toString().trim() == null || etCvv.getText().toString().trim().isEmpty()
                || etCvv.getText().toString().trim().length()<3 ){
            etCardNumber.setError("Please enter valid value");
            return false;
        }else {

            return true;
        }
    }
}