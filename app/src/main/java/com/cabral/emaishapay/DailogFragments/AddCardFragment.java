package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cabral.emaishapay.R;


public class AddCardFragment extends DialogFragment {

    EditText etName, etCardNumber, etCvv;
    TextView txtExpiryDate;
    Button btnSaveCard;

    public AddCardFragment() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.dialog_add_card, null);
        etName = view.findViewById(R.id.txt_card_holder_name);
        etCardNumber = view.findViewById(R.id.txt_card_number);
        etCvv = view.findViewById(R.id.card_cvv);
        txtExpiryDate = view.findViewById(R.id.card_expiry);
        btnSaveCard = view.findViewById(R.id.save_card_button);

        btnSaveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etName.getText().toString().trim() == null || etName.getText().toString().trim().isEmpty()) {
                    etName.setError("Please enter valid value");

                } else if (etCardNumber.getText().toString().trim() == null || etCardNumber.getText().toString().trim().isEmpty()
                        || etCardNumber.getText().toString().trim().length()<13 ){
                    etCardNumber.setError("Please enter valid value");
                }

                else if (txtExpiryDate.getText().toString().trim() == null || txtExpiryDate.getText().toString().trim().isEmpty()){
                    txtExpiryDate.setError("Please select valid value");
                }

                else if (etCvv.getText().toString().trim() == null || etCvv.getText().toString().trim().isEmpty()
                        || etCvv.getText().toString().trim().length()<3 ){
                    etCardNumber.setError("Please enter valid value");
                }else {

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
}