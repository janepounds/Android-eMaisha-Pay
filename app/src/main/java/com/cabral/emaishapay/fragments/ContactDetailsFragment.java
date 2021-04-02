package com.cabral.emaishapay.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.database.DbHandlerSingleton;
import com.cabral.emaishapay.models.CropSpinnerItem;
import com.cabral.emaishapay.models.address_model.RegionDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.kofigyan.stateprogressbar.StateProgressBar;

import org.json.JSONException;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

import static android.content.ContentValues.TAG;

public class ContactDetailsFragment extends Fragment {
    String[] descriptionData = {"Personal\n Details", "Contact\n Details", "Identity\n Proof" , "Card\n Details"};

    String firstname, lastname, middlename, customer_gender, date_of_birth;
    private FirebaseAuth mAuth;
    private int pickedDistrictId;
    private int pickedSubCountyId;
    private ArrayList<CropSpinnerItem> subCountyList = new ArrayList<>();
    private ArrayList<String> villageList = new ArrayList<>();
    private DbHandlerSingleton dbHandler;

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
        Spinner sp_next_of_kin_relationship = view.findViewById(R.id.sp_nok_relationship_spn1);
        EditText etxt_next_of_kin_contact = view.findViewById(R.id.etxt_kin_contact);


        Toolbar toolbar = view.findViewById(R.id.toolbar_account_opening);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Account Opening");


        //initializing objects
        mAuth = FirebaseAuth.getInstance();

        ArrayList<CropSpinnerItem> districtList = new ArrayList<>();
        dbHandler = DbHandlerSingleton.getHandlerInstance(getContext());
        try {
            for (RegionDetails x : dbHandler.getRegionDetails("district")) {
                districtList.add(new CropSpinnerItem() {
                    @Override
                    public String getId() {
                        return String.valueOf(x.getId());
                    }

                    @Override
                    public String getUnits() {
                        return null;
                    }

                    @NonNull
                    @Override
                    public String toString() {
                        return x.getRegion();
                    }
                });

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.d(TAG, "onCreate: " + districtList + districtList.size());
        ArrayAdapter<CropSpinnerItem> districtListAdapter = new ArrayAdapter<CropSpinnerItem>(getContext(), android.R.layout.simple_dropdown_item_1line, districtList);
        act_districts.setThreshold(1);
        act_districts.setAdapter(districtListAdapter);
        act_districts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                act_districts.showDropDown();
                for (int i = 0; i < districtList.size(); i++) {

                    if (districtList.get(i).toString().equals(act_districts.getText().toString())) {
                        pickedDistrictId = Integer.parseInt(districtList.get(i).getId());

                        Log.d(TAG, "onCreate: " + pickedDistrictId);

                        subCountyList.clear();
                        try {
                            for (RegionDetails x : dbHandler.getSubcountyDetails(String.valueOf(pickedDistrictId), "subcounty")) {
                                subCountyList.add(new CropSpinnerItem() {
                                    @Override
                                    public String getId() {
                                        return String.valueOf(x.getId());
                                    }

                                    @Override
                                    public String getUnits() {
                                        return null;
                                    }

                                    @NonNull
                                    @Override
                                    public String toString() {
                                        return x.getRegion();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "onCreate: " + subCountyList);
                        ArrayAdapter<CropSpinnerItem> subCountyListAdapter = new ArrayAdapter<CropSpinnerItem>(getContext(), android.R.layout.simple_dropdown_item_1line, subCountyList);
                        act_sub_counties.setThreshold(1);
                        act_sub_counties.setAdapter(subCountyListAdapter);
                    }

                }
            }
        });

        act_sub_counties.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                act_sub_counties.showDropDown();

                for (int i = 0; i < subCountyList.size(); i++) {

                    if (subCountyList.get(i).toString().equals(act_sub_counties.getText().toString())) {
                        pickedSubCountyId = Integer.parseInt(subCountyList.get(i).getId());

                        Log.d(TAG, "onCreate: " + pickedSubCountyId);

                        villageList.clear();
                        try {
                            for (RegionDetails x : dbHandler.getVillageDetails(String.valueOf(pickedSubCountyId), "village")) {
                                villageList.add(x.getRegion());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "onCreate: " + villageList);
                        ArrayAdapter<String> villageListAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, villageList);
                        act_villages.setThreshold(1);
                        act_villages.setAdapter(villageListAdapter);
                    }


                }
            }
        });

        act_villages.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });





        Button previous = view.findViewById(R.id.previous_button);
        previous.setOnClickListener(view2 -> getFragmentManager().popBackStack());

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
                String next_of_kin_relationship = sp_next_of_kin_relationship.getSelectedItem().toString().trim();
                String next_of_kin_contact = "0"+etxt_next_of_kin_contact.getText().toString().trim();
                if (district.equals("") || district.length()<3){
                    act_districts.setError("Enter valid district name");
                    act_districts.requestFocus();
                    return;
                }
                if (sub_county.equals("") || sub_county.length()<3){
                    act_sub_counties.setError("Enter valid sub-county name");
                    act_sub_counties.requestFocus();
                    return;
                }
                if (village.equals("") || village.length()<3){
                    act_villages.setError("Enter valid village name");
                    act_villages.requestFocus();
                    return;
                }
                if (landmark.equals("")) {
                    etxt_land_mark.setError("Landmark is required");
                    etxt_land_mark.requestFocus();
                    return;
                }
                if (phonenumber.equals("") || phonenumber.length()<9){
                    etxt_phonenumber.setError("Enter valid number");
                    etxt_phonenumber.requestFocus();
                    return;
                }

                if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){
                    etxt_email.setError("Enter valid email");
                    etxt_email.requestFocus();
                    return;
                }

                if (next_of_kin_name.equals("")) {
                    etxt_next_of_kin_name.setError("Next of kin name is required");
                    etxt_next_of_kin_name.requestFocus();
                    return;
                }
                if (next_of_kin_second_name.equals("")) {
                    etxt_next_of_kin_second_name.setError("Next of kin second name is required");
                    etxt_next_of_kin_second_name.requestFocus();
                    return;
                }
                if (next_of_kin_relationship.equalsIgnoreCase("Select")) {
                    //Toast.makeText(getContext(), "Next of kin relationship is required", Toast.LENGTH_LONG).show();
                    Toasty.error(requireContext(), "Next of kin relationship is required", Toast.LENGTH_LONG).show();
                    sp_next_of_kin_relationship.requestFocus();
                    return;
                }
                if (next_of_kin_contact.equals("") || next_of_kin_contact.length()<9){
                    etxt_next_of_kin_contact.setError("Enter valid next of kin contact");
                    etxt_next_of_kin_contact.requestFocus();
                    return;
                }



                Log.d("Kin Name",next_of_kin_name);
                Log.d("Kin Second Name",next_of_kin_second_name);
                Log.d("Kin Relationship",next_of_kin_relationship);
                Log.d("Kin Contact",next_of_kin_contact);

                Log.d("First name", firstname);

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

                //To IdentityProofFragment
                WalletHomeActivity.navController.navigate(R.id.action_personalDetailsFragment_to_contactDetailsFragment,bundle);
            }
        });

    }


}