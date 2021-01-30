package com.cabral.emaishapay.activities;

import android.content.Intent;
import android.os.Bundle;

import com.braintreepayments.api.Card;
import com.cabral.emaishapay.fragments.FingerPrintAuthenticationFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cabral.emaishapay.R;
import com.kofigyan.stateprogressbar.StateProgressBar;

public class CardDetail extends Fragment {

    String firstname, lastname, middlename, gender, date_of_birth, district, village, sub_county, landmark, phone_number, email, next_of_kin_name, next_of_kin_second_name, next_of_kin_relationship, next_of_kin_contact,
    nin,valid_upto,encodedImageID,encodedImageCustomerPhoto,encodedImagePhotoWithID;
    EditText account_no,card_no,expiry,cvv;
    Button next;
    String[] descriptionData = {"Personal\n Details", "Contact\n Details", "Identity\n Proof" , "Card\n Details"};
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        firstname = getArguments().getString("firstname");
        lastname = getArguments().getString("lastname");
        middlename = getArguments().getString("middlename");
        gender = getArguments().getString("customer_gender");
        date_of_birth = getArguments().getString("date_of_birth");
        district = getArguments().getString("district");
        sub_county = getArguments().getString("sub_county");
        village = getArguments().getString("village");
        landmark = getArguments().getString("landmark");
        phone_number = getArguments().getString("phone_number");
        next_of_kin_name = getArguments().getString("next_of_kin_name");
        next_of_kin_second_name = getArguments().getString("next_of_kin_second_name");
        next_of_kin_relationship = getArguments().getString("next_of_kin_relationship");
        next_of_kin_contact = getArguments().getString("next_of_kin_contact");
        nin = getArguments().getString("nin");
        valid_upto = getArguments().getString("national_id_valid_upto");
        encodedImageID = getArguments().getString("national_id_photo");
        encodedImageCustomerPhoto = getArguments().getString("customer_photo");
        encodedImagePhotoWithID = getArguments().getString("customer_photo_with_id");
        return inflater.inflate(R.layout.activity_card_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StateProgressBar stateProgressBar = view.findViewById(R.id.your_state_progress_bar_card_details);
        stateProgressBar.setStateDescriptionData(descriptionData);
        stateProgressBar.setStateDescriptionTypeface("fonts/JosefinSans-Bold.ttf");

        Button next = view.findViewById(R.id.txt_next_submit);
        TextView finger_print = view.findViewById(R.id.txt_next_finger_print);
        TextView txt_upload_national_id = view.findViewById(R.id.txt_browse_national_id_photo);
        TextView txt_upload_customer_photo = view.findViewById(R.id.txt_browse_customer_photo);
        TextView txt_upload_photo_with_id = view.findViewById(R.id.txt_browse_photo_with_id);


         account_no = view.findViewById(R.id.etxt_card_account_number);
         card_no = view.findViewById(R.id.etxt_card_number);
         expiry = view.findViewById(R.id.etxt_card_expiry_date);
         cvv = view.findViewById(R.id.etxt_card_cvv);
         next  = view.findViewById(R.id.txt_card_submit);




        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call pin creation Activity
                String account_number = account_no.getText().toString();
                String card_number = card_no.getText().toString();
                String expiry_Date = expiry.getText().toString();
                String cvvv = cvv.getText().toString();
                Intent intent = new Intent(getContext(),AccountOpeningPinCreationActivity.class);
                intent.putExtra("firstname", firstname);
                intent.putExtra("lastname", lastname);
                intent.putExtra("middlename", middlename);
                intent.putExtra("date_of_birth", date_of_birth);
                intent.putExtra("gender", gender);
                intent.putExtra("district", district);
                intent.putExtra("sub_county", sub_county);
                intent.putExtra("village", village);
                intent.putExtra("landmark", landmark);
                intent.putExtra("phone_number", phone_number);
                intent.putExtra("email", email);
                intent.putExtra("next_of_kin_name", next_of_kin_name);
                intent.putExtra("next_of_kin_second_name", next_of_kin_second_name);
                intent.putExtra("next_of_kin_relationship", next_of_kin_relationship);
                intent.putExtra("next_of_kin_contact", next_of_kin_contact);
                intent.putExtra("nin", nin);
                intent.putExtra("national_id_valid_upto", valid_upto);
                intent.putExtra("national_id_photo", encodedImageID);
                intent.putExtra("customer_photo", encodedImageCustomerPhoto);
                intent.putExtra("customer_photo_with_id", encodedImagePhotoWithID);
                intent.putExtra("account_no", account_number);
                intent.putExtra("card_no", card_number);
                intent.putExtra("expiry", expiry_Date);
                intent.putExtra("cvv", cvvv);
                startActivity(intent);

            }
        });



    }
}