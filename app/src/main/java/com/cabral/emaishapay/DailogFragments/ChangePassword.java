package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.Login;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.app.MyAppPrefsManager;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.fragments.CardListFragment;
import com.cabral.emaishapay.fragments.TokenAuthFragment;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.models.ChangePinResponse;
import com.cabral.emaishapay.models.SecurityQnsResponse;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.utils.CryptoUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChangePassword extends DialogFragment {
    private static final String TAG = "ChangePassword";
    private Context context;
    private EditText curent_pin,new_pin,confirm_new_pin;
    private Button cancel,update;


    public ChangePassword() {
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
        View view = inflater.inflate(R.layout.dialog_change_password, null);
        curent_pin = view.findViewById(R.id.current_pin);
        new_pin = view.findViewById(R.id.new_pin);
        confirm_new_pin = view.findViewById(R.id.confirm_new_pin);
        update = view.findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validate_form = validateForm();
                if(validate_form){
                    DialogLoader dialog;
                    dialog = new DialogLoader(context);
                    dialog.showProgressDialog();
                    dialog.showProgressDialog();
                    //call endpoint for change password
                    String phone_number = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_PHONE_NUMBER,context);
                    String access_token = TokenAuthFragment.WALLET_ACCESS_TOKEN;
                    /******************RETROFIT IMPLEMENTATION***********************/
                    Call<ChangePinResponse> call = APIClient.getWalletInstance(getContext()).changePassword(access_token,phone_number,WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION+curent_pin.getText().toString(),WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION+new_pin.getText().toString(),WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION+confirm_new_pin.getText().toString());
                    call.enqueue(new Callback<ChangePinResponse>() {
                        @Override
                        public void onResponse(Call<ChangePinResponse> call, Response<ChangePinResponse> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                //redirect to account/home
                                dismiss();
                                dialog.hideProgressDialog();
                                // Initialise SharedPreference manager class
                                MyAppPrefsManager prefsManager = new MyAppPrefsManager(requireContext());

                                // change the login status to false
                                prefsManager.logOutUser();
                                // check if has been changed to false
                                if (!prefsManager.isUserLoggedIn()) {
                                    Log.d(TAG, "onCreate: Login Status = " + prefsManager.isUserLoggedIn());

                                    requireActivity().finish();
                                    // Open login
                                    startActivity(new Intent(requireActivity(), Login.class));
                                }


                            } else if (response.body().getStatus().equalsIgnoreCase("0")) {
                                Toast.makeText(context,response.body().getMessage(),Toast.LENGTH_LONG).show();
                                dialog.hideProgressDialog();

                            }
                        }



                        @Override
                        public void onFailure(Call<ChangePinResponse> call, Throwable t){
                            dialog.hideProgressDialog();
                        }
                    });

                }
            }
        });





        builder.setView(view);
        Dialog dialog = builder.create();
//        dialog.setCanceledOnTouchOutside(false);
//        setCancelable(false);

        Button cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> dismiss());

        return dialog;

    }

    public  boolean validateForm(){
        String new_pin_ = WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION +new_pin.getText().toString();
        String confirm_new_pin_ = WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION +confirm_new_pin.getText().toString();
        if(!new_pin_.equalsIgnoreCase(confirm_new_pin_)){
            Toast.makeText(context,"New pin does not match",Toast.LENGTH_LONG).show();
            return false;

        }else {
            return true;
        }
    }
}