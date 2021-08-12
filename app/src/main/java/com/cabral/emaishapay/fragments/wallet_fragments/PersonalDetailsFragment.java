package com.cabral.emaishapay.fragments.wallet_fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.kofigyan.stateprogressbar.StateProgressBar;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;


public class PersonalDetailsFragment extends Fragment {
    String[] descriptionData = {"Personal\n Details", "Contact\n Details", "Identity\n Proof" , "Card\n Details"};
    String[] arrayForSpinner = {"Male", "Female"};



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StateProgressBar stateProgressBar = view.findViewById(R.id.your_state_progress_bar_personal_details);
        stateProgressBar.setStateDescriptionData(descriptionData);
        stateProgressBar.setStateDescriptionTypeface("font/nunito.ttf");
        WalletHomeActivity.bottomNavigationShop.setVisibility(View.GONE);

        EditText first_name = view.findViewById(R.id.etxt_fullname);
        EditText middle_name = view.findViewById(R.id.etxt_middlename);
        EditText last_name = view.findViewById(R.id.etxt_lastname);
        EditText date_of_birth = view.findViewById(R.id.etxt_date_of_birth);
        AutoCompleteTextView act_gender = view.findViewById(R.id.act_gender);

        Toolbar toolbar = view.findViewById(R.id.toolbar_account_opening);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Account Opening");

        act_gender.setFocusableInTouchMode(false);
        act_gender.setFocusable(false);

        date_of_birth.setFocusableInTouchMode(false);
        date_of_birth.setFocusable(false);


        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_row, arrayForSpinner);
        act_gender.setAdapter(adapter);


        date_of_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDatePicker2(date_of_birth, getActivity());
            }
        });

        act_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act_gender.showDropDown();
            }
        });


        Button next = view.findViewById(R.id.txt_next_two);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstname = first_name.getText().toString().trim();
                String lastname = last_name.getText().toString().trim();
                String middlename = middle_name.getText().toString().trim();
                String customer_gender = act_gender.getText().toString().trim();
                String date = date_of_birth.getText().toString().trim();

                if (firstname.equals("")) {
                    first_name.setError("First Name is required");
                    first_name.requestFocus();
                    return;
                }

                if (lastname.equals("")) {
                    last_name.setError("Last Name is required");
                    last_name.requestFocus();
                    return;
                }

                if (customer_gender.equals("")) {
                    act_gender.setError("Gender is required");
                    act_gender.requestFocus();
                    return;
                }
                if (date.equals("")) {
                    date_of_birth.setError("Date of birth is required");
                    date_of_birth.requestFocus();
                    return;
                }

                act_gender.setError(null);
                date_of_birth.setError(null);

                //To ContactDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("firstname", firstname);
                bundle.putString("lastname", lastname);
                if (middle_name!=null) {
                    bundle.putString("middlename", middlename);
                }
                bundle.putString("customer_gender", customer_gender);
                bundle.putString("date_of_birth", date);

                WalletHomeActivity.navController.navigate(R.id.action_personalDetailsFragment_to_contactDetailsFragment,bundle);

            }
        });

    }


    public void addDatePicker2(final TextView ed_, final Context context) {
        ed_.setOnClickListener(view -> {
            Calendar mCurrentDate = Calendar.getInstance();
            mCurrentDate.add(Calendar.YEAR, -18);
            int mYear = mCurrentDate.get(Calendar.YEAR);
            int mMonth = mCurrentDate.get(Calendar.MONTH);
            int mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);

            final DatePickerDialog mDatePicker = new DatePickerDialog(context, (datePicker, selectedYear, selectedMonth, selectedDay) -> {

                int month = selectedMonth + 1;
                NumberFormat formatter = new DecimalFormat("00");
                ed_.setText(selectedYear + "-" + formatter.format(month) + "-" + formatter.format(selectedDay));


            }, mYear, mMonth, mDay);

            mDatePicker.show();
        });
        ed_.setInputType(InputType.TYPE_NULL);
    }


}