package com.cabral.emaishapay.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cabral.emaishapay.R;


public class AcceptPaymentFragment extends Fragment {

    TextView  txtPaymentMethod;
    EditText totalAmountEdt, couponAmout, cardNumberEdt, expiryEdt, cvvEdt, mobileNumberEdt;

    LinearLayout layoutMobileMoney;
    Spinner spPaymentMethod;
    Button saveBtn;
    FragmentManager fm;
    private Context context;

    public AcceptPaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_accept_payment, container, false);

        Toolbar toolbar=view.findViewById(R.id.toolbar_accept_payment);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Accept Payment");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);

        txtPaymentMethod=view.findViewById(R.id.text_mobile_number);
        layoutMobileMoney=view.findViewById(R.id.layout_mobile_number);
        spPaymentMethod=view.findViewById(R.id.sp_payment_method);

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
                if(selectedItem.equalsIgnoreCase("emaisha pay")){
                    layoutMobileMoney.setVisibility(View.VISIBLE);
                    txtPaymentMethod.setText("eMaisha Account");
                }
                else if(selectedItem.equalsIgnoreCase("Mobile Money")){
                    layoutMobileMoney.setVisibility(View.VISIBLE);
                    txtPaymentMethod.setText("Mobile Number");
                }
                else {
                    layoutMobileMoney.setVisibility(View.GONE);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        return view;
    }
}