package com.cabral.emaishapay.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.cabral.emaishapay.R;
import com.kofigyan.stateprogressbar.StateProgressBar;

public class ContactDetailsFragment extends Fragment {
    String[] descriptionData = {"Personal\n Details", "Contact\n Details", "Identity\n Proof" , "Card\n Details"};
    String[] districts = {"Kampala", "Mukono", "Jinja", "Wakiso"};
    String[] sub_counties = {"Kasubi", "Nakulabye", "Bukoto"};
    String[] villages = {"Makerere", "Busabala", "Bukoto"};
    String firstname, lastname, middlename, customer_gender, date_of_birth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        firstname = getArguments().getString("firstname");
        lastname = getArguments().getString("lastname");
        middlename = getArguments().getString("middlename");
        customer_gender = getArguments().getString("customer_gender");
        date_of_birth = getArguments().getString("date_of_birth");

        return inflater.inflate(R.layout.fragment_contact_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StateProgressBar stateProgressBar = view.findViewById(R.id.your_state_progress_bar_contact_details);
        stateProgressBar.setStateDescriptionData(descriptionData);
        stateProgressBar.setStateDescriptionTypeface("fonts/JosefinSans-SemiBold.ttf");

        Button next = view.findViewById(R.id.txt_next_three);
        AutoCompleteTextView act_districts = view.findViewById(R.id.act_district);
        AutoCompleteTextView act_sub_counties = view.findViewById(R.id.act_sub_county);
        AutoCompleteTextView act_villages = view.findViewById(R.id.act_village);

        EditText etxt_land_mark = view.findViewById(R.id.etxt_landmark);
        EditText etxt_phonenumber = view.findViewById(R.id.etxt_phone_number);
        EditText etxt_email = view.findViewById(R.id.etxt_email);
        EditText etxt_next_of_kin_name = view.findViewById(R.id.etxt_next_of_kin_name);
        EditText etxt_next_of_kin_second_name = view.findViewById(R.id.etxt_next_of_kin_second_name);
        EditText etxt_next_of_kin_relationship = view.findViewById(R.id.etxt_kin_relationship);
        EditText etxt_next_of_kin_contact = view.findViewById(R.id.etxt_kin_contact);


        act_districts.setThreshold(2);
        act_sub_counties.setThreshold(2);
        act_villages.setThreshold(2);

        ArrayAdapter district_adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_row, districts);
        act_districts.setAdapter(district_adapter);

        ArrayAdapter sub_county_adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_row, sub_counties);
        act_sub_counties.setAdapter(sub_county_adapter);

        ArrayAdapter village_adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_row, villages);
        act_villages.setAdapter(village_adapter);

        act_districts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act_districts.showDropDown();
            }
        });

        act_sub_counties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act_sub_counties.showDropDown();
            }
        });

        act_villages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act_villages.showDropDown();
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String district = act_districts.getText().toString().trim();
                String sub_county = act_sub_counties.getText().toString().trim();
                String village = act_villages.getText().toString().trim();
                String landmark = etxt_land_mark.getText().toString().trim();
                String email = etxt_email.getText().toString().trim();
                String phonenumber = "0"+etxt_phonenumber.getText().toString().trim();
                String next_of_kin_name = etxt_next_of_kin_name.getText().toString().trim();
                String next_of_kin_second_name = etxt_next_of_kin_second_name.getText().toString().trim();
                String next_of_kin_relationship = etxt_next_of_kin_relationship.getText().toString().trim();
                String next_of_kin_contact = "0"+etxt_next_of_kin_contact.getText().toString().trim();
                if (district.equals("")) {
                    act_districts.setError("District is required");
                    act_districts.requestFocus();
                    return;
                }
                if (sub_county.equals("")) {
                    act_sub_counties.setError("Sub county is required");
                    act_sub_counties.requestFocus();
                    return;
                }
                if (village.equals("")) {
                    act_villages.setError("Village is required");
                    act_villages.requestFocus();
                    return;
                }
                if (landmark.equals("")) {
                    etxt_land_mark.setError("Landmark is required");
                    etxt_land_mark.requestFocus();
                    return;
                }
                if (phonenumber.equals("")) {
                    etxt_phonenumber.setError("Phone number is required");
                    etxt_phonenumber.requestFocus();
                    return;
                }
                if (email.equals("")) {
                    etxt_email.setError("Email is required");
                    etxt_email.requestFocus();
                    return;
                }
                if (next_of_kin_name.equals("")) {
                    etxt_next_of_kin_name.setError("Next of kin name is required");
                    etxt_next_of_kin_name.requestFocus();
                    return;
                }
                if (next_of_kin_second_name.equals("")) {
                    etxt_next_of_kin_name.setError("Next of kin second name is required");
                    etxt_next_of_kin_name.requestFocus();
                    return;
                }
                if (next_of_kin_relationship.equals("")) {
                    etxt_next_of_kin_relationship.setError("Next of kin relationship is required");
                    etxt_next_of_kin_relationship.requestFocus();
                    return;
                }
                if (next_of_kin_contact.equals("")) {
                    etxt_next_of_kin_contact.setError("Next of kin contact is required");
                    etxt_next_of_kin_contact.requestFocus();
                    return;
                }
                Log.d("Kin Name",next_of_kin_name);
                Log.d("Kin Second Name",next_of_kin_second_name);
                Log.d("Kin Relationship",next_of_kin_relationship);
                Log.d("Kin Contact",next_of_kin_contact);

                Log.d("First name", firstname);
                IdentityProofFragment identityProof = new IdentityProofFragment();
                Bundle bundle = new Bundle();
                bundle.putString("firstname", firstname);
                bundle.putString("lastname", lastname);
                bundle.putString("middlename", middlename);
                bundle.putString("customer_gender", customer_gender);
                bundle.putString("date_of_birth", date_of_birth);
                bundle.putString("district", district);
                bundle.putString("sub_county", sub_county);
                bundle.putString("village", village);
                bundle.putString("landmark", landmark);
                bundle.putString("email", email);
                bundle.putString("phone_number", phonenumber);
                bundle.putString("next_of_kin_name", next_of_kin_name);
                bundle.putString("next_of_kin_second_name", next_of_kin_second_name);
                bundle.putString("next_of_kin_relationship", next_of_kin_relationship);
                bundle.putString("next_of_kin_contact", next_of_kin_contact);
                identityProof.setArguments(bundle);


                openFragment(identityProof);
            }
        });

    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.open_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}