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

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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


public class ChangePassword extends DialogFragment {

    private Context context;

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




        builder.setView(view);
        Dialog dialog = builder.create();
//        dialog.setCanceledOnTouchOutside(false);
//        setCancelable(false);

        Button cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> dismiss());

        return dialog;

    }
}