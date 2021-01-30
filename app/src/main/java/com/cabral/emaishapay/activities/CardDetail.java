package com.cabral.emaishapay.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.braintreepayments.api.Card;
import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.fragments.FingerPrintAuthenticationFragment;
import com.cabral.emaishapay.models.AccountCreation;
import com.cabral.emaishapay.utils.CryptoUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cabral.emaishapay.R;
import com.kofigyan.stateprogressbar.StateProgressBar;

import org.json.JSONObject;

public class CardDetail extends Fragment {
    private static final String TAG = "CardDetail";

    String firstname, lastname, middlename, gender, date_of_birth, district, village, sub_county, landmark, phone_number, email, next_of_kin_name, next_of_kin_second_name, next_of_kin_relationship, next_of_kin_contact,
    nin,valid_upto,encodedImageID,encodedImageCustomerPhoto,encodedImagePhotoWithID,new_gender,account_number,expiry_Date,cvvv,card_number, account_name;
    EditText account_no,card_no,expiry,cvv;
    Button next;
    private AccountCreation accountCreation;
    private Context context;
    String[] descriptionData = {"Personal\n Details", "Contact\n Details", "Identity\n Proof" , "Card\n Details"};

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

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
        email = getArguments().getString("email");
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

        accountCreation = new AccountCreation();


        String next_of_kin = next_of_kin_name + next_of_kin_second_name;
         account_name = firstname + lastname;
        if(gender.equalsIgnoreCase("female")){
            new_gender = "F";
        }else{
            new_gender = "M";
        }
        //set account creation values
        accountCreation.setDob(date_of_birth);
        accountCreation.setFirstname(firstname );
        accountCreation.setLastname(lastname);
        accountCreation.setMiddlename(middlename );
        accountCreation.setGender(new_gender );
        accountCreation.setNext_of_kin(next_of_kin );
        accountCreation.setNext_of_kin_contact(next_of_kin_contact);
        accountCreation.setDistrict(district );
        accountCreation.setSub_county(sub_county);
        accountCreation.setVillage(village );
        accountCreation.setLandmark(landmark);
        accountCreation.setPhone_number(phone_number);
        accountCreation.setEmail(email );
        accountCreation.setNext_of_kin_relationship(next_of_kin_relationship );
        accountCreation.setNin(nin );
        accountCreation.setNational_id_valid_upto(valid_upto );
        accountCreation.setNational_id_photo(encodedImageID );
        accountCreation.setCustomer_photo(encodedImageCustomerPhoto);
        accountCreation.setCustomer_photo_with_id(encodedImagePhotoWithID);

        return inflater.inflate(R.layout.activity_card_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StateProgressBar stateProgressBar = view.findViewById(R.id.your_state_progress_bar_card_details);
        stateProgressBar.setStateDescriptionData(descriptionData);
        stateProgressBar.setStateDescriptionTypeface("fonts/JosefinSans-Bold.ttf");


         account_no = view.findViewById(R.id.etxt_card_account_number);
         card_no = view.findViewById(R.id.etxt_card_number);
         expiry = view.findViewById(R.id.etxt_card_expiry_date);
         cvv = view.findViewById(R.id.etxt_card_cvv);
         next  = view.findViewById(R.id.txt_card_submit);

        Log.d(TAG, "onViewCreated: "+"firstname\n"+firstname+"lastname\n"+lastname+"middlename\n"+
                middlename+"gender\n" +gender+"dob\n"+date_of_birth+"district\n"+district+"village\n" +village+"sub_county\n"+sub_county+"landmark\n"+landmark
                +"phone\n" +phone_number+"email\n"+email+"nok_f\n"+next_of_kin_name+"nok_l\n"+next_of_kin_second_name+"rlsp\n"+next_of_kin_relationship+"nok_contact\n"+ next_of_kin_contact
                +"nin\n"+nin+"valid\n"+valid_upto+"nationid\n"+encodedImageID+"customer_pic\n"+encodedImageCustomerPhoto+"cust_pic_id\n"+encodedImagePhotoWithID);




        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //call pin creation Activity
                 account_number = account_no.getText().toString();
                 card_number = card_no.getText().toString();
                 expiry_Date = expiry.getText().toString();
                 cvvv = cvv.getText().toString();
                Intent intent = new Intent(getContext(),AccountOpeningPinCreationActivity.class);


                /**********ENCRIPT CARD DETAILS************/
                CryptoUtil encrypter =new CryptoUtil(BuildConfig.ENCRYPTION_KEY,context.getString(R.string.iv));

                String card_number_encripted = encrypter.encrypt(card_number);
                String  expiry_encripted = encrypter.encrypt(expiry_Date);
                String  account_name_encripted = encrypter.encrypt(account_name);
                String cvv_encripted  = encrypter.encrypt(cvvv);

                //submit registration details to server
                /***************RETROFIT IMPLEMENTATION FOR TRANSFER FUNDS************************/


                accountCreation.setCard_number(card_number_encripted );
                accountCreation.setCvv(cvv_encripted );
                accountCreation.setExpiry(expiry_encripted);
                accountCreation.setAccount_name(account_name_encripted );
                startActivity(intent);



            }
        });



    }


}