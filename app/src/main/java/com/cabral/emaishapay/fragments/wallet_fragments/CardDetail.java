package com.cabral.emaishapay.fragments.wallet_fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cabral.emaishapay.BuildConfig;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.models.AccountCreation;
import com.cabral.emaishapay.models.InitiateWithdrawResponse;
import com.cabral.emaishapay.network.api_helpers.APIClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishapay.R;
import com.kofigyan.stateprogressbar.StateProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardDetail extends Fragment {
    private static final String TAG = "CardDetail";

    String firstname, lastname, middlename, gender, date_of_birth, district, village, sub_county, landmark, phone_number, email, next_of_kin_name, next_of_kin_second_name, next_of_kin_relationship, next_of_kin_contact,
    idtype,nin,valid_upto,encodedImageID,encodedImageCustomerPhoto,encodedImagePhotoWithID,new_gender,account_number,expiry_Date,cvvv,card_number, account_name;
    EditText account_no,card_no,cvv,card_enter_pin,card_confirm_pin,expiry;

    Button next;
    private Context context;
    AccountCreation accountCreation;
    String[] descriptionData = {"Personal\n Details", "Contact\n Details", "Identity\n Proof" , "Card\n Details"};

    DialogLoader dialogLoader;
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
        idtype = getArguments().getString("idtype");
        nin = getArguments().getString("nin");
        valid_upto = getArguments().getString("national_id_valid_upto");
        encodedImageID = getArguments().getString("national_id_photo");
        encodedImageCustomerPhoto = getArguments().getString("customer_photo");
        encodedImagePhotoWithID = getArguments().getString("customer_photo_with_id");
        getArguments().clear();
        accountCreation=new AccountCreation();

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
        accountCreation.setIdtype(idtype);
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
        stateProgressBar.setStateDescriptionTypeface("font/nunito.ttf");
        WalletHomeActivity.bottom_navigation_shop.setVisibility(View.GONE);

         account_no = view.findViewById(R.id.etxt_card_account_number);
         card_no = view.findViewById(R.id.etxt_card_number);
         expiry = view.findViewById(R.id.etxt_card_expiry_date);
         cvv = view.findViewById(R.id.etxt_card_cvv);
         card_enter_pin= view.findViewById(R.id.etxt_card_enter_pin);
         card_confirm_pin= view.findViewById(R.id.etxt_card_confirm_pin);
         next  = view.findViewById(R.id.txt_card_submit);

        Toolbar toolbar = view.findViewById(R.id.toolbar_account_opening);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Account Opening");

        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (filterLongEnough() && !expiry.getText().toString().contains("/")) {
                    expiry.setText(expiry.getText().toString()+"/");
                    int pos = expiry.getText().length();
                    expiry.setSelection(pos);
                }
            }

            private boolean filterLongEnough() {
                return expiry.getText().toString().length() == 2;
            }
        };
        expiry.addTextChangedListener(fieldValidatorTextWatcher);


        Log.d(TAG, "onViewCreated: "+"firstname\n"+firstname+"lastname\n"+lastname+"middlename\n"+
                middlename+"gender\n" +gender+"dob\n"+date_of_birth+"district\n"+district+"village\n" +village+"sub_county\n"+sub_county+"landmark\n"+landmark
                +"phone\n" +phone_number+"email\n"+email+"nok_f\n"+next_of_kin_name+"nok_l\n"+next_of_kin_second_name+"rlsp\n"+next_of_kin_relationship+"nok_contact\n"+ next_of_kin_contact
                +"nin\n"+nin+"valid\n"+valid_upto+"nationid\n"+encodedImageID+"customer_pic\n"+encodedImageCustomerPhoto+"cust_pic_id\n"+encodedImagePhotoWithID);


        expiry.setOnClickListener(view3 ->  addDatePicker2(expiry, getActivity()));

        Button previous = view.findViewById(R.id.previous_button);
        previous.setOnClickListener(view2 -> getFragmentManager().popBackStack());



        dialogLoader = new DialogLoader(getContext());

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEntries()) {

                    dialogLoader.showProgressDialog();
                    //call pin creation Activity
                    account_number = account_no.getText().toString();
                    card_number = card_no.getText().toString();
                    expiry_Date = expiry.getText().toString();
                    cvvv = cvv.getText().toString();


                    //submit registration details to server
                    /***************RETROFIT IMPLEMENTATION FOR TRANSFER FUNDS************************/
                    accountCreation.setCard_number(card_number);
                    accountCreation.setCvv(cvvv);
                    accountCreation.setExpiry(expiry_Date);
                    accountCreation.setAccount_name(account_name);
                    accountCreation.setPin(WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION+card_enter_pin.getText().toString());

                    /***************RETROFIT IMPLEMENTATION FOR ACCOUNT CREATION************************/
                    JSONObject requestObject = new JSONObject();
                    try {
                        requestObject.put("accountParams", accountCreation);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
                    String request_id = WalletHomeActivity.generateRequestId();
                    String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
                    Call<InitiateWithdrawResponse> call = APIClient.getWalletInstance(getContext())
                            .openAccount(access_token, requestObject,request_id,category,"merchantAccountOpening");
                    call.enqueue(new Callback<InitiateWithdrawResponse>() {
                        @Override
                        public void onResponse(Call<InitiateWithdrawResponse> call, Response<InitiateWithdrawResponse> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getStatus().equalsIgnoreCase("1")) {
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    //success message
                                    Intent intent = new Intent(context, WalletHomeActivity.class);
                                    startActivity(intent);
                                    dialogLoader.hideProgressDialog();

                                } else {
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    //redirect to home;
                                    Intent intent = new Intent(context, WalletHomeActivity.class);
                                    startActivity(intent);
                                    dialogLoader.hideProgressDialog();

                                }
                            }


                        }

                        @Override
                        public void onFailure(Call<InitiateWithdrawResponse> call, Throwable t) {
                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(context, WalletHomeActivity.class);
                            startActivity(intent);
                            dialogLoader.hideProgressDialog();
                        }
                    });


                }
            }
        });



    }
    public void addDatePicker2(final TextView ed_, final Context context) {
        ed_.setOnClickListener(view -> {
            Calendar mCurrentDate = Calendar.getInstance();
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
    public boolean validateEntries(){

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MM");
        DateFormat yearDateFormat = new SimpleDateFormat("yy");

        int mm = Integer.parseInt(dateFormat.format(date));
        int yy = Integer.parseInt(yearDateFormat.format(date));
        String expMonth = expiry.getText().toString().substring(0,2);
        String expYear = expiry.getText().toString().substring(expiry.getText().toString().length() - 2);


        boolean check = true;


        if (account_no.getText().toString().trim() == null || account_no.getText().toString().trim().isEmpty()) {

            account_no.setError("Please enter valid account number");
            account_no.requestFocus();
            check = false;

        } else if (card_no.getText().toString().trim() == null || card_no.getText().toString().trim().isEmpty()
                || card_no.getText().toString().trim().length()<13 ){

            card_no.setError("Please enter valid value");
            card_no.requestFocus();
            check= false;
        }

        else if (expiry.getText().toString().trim() == null || expiry.getText().toString().trim().isEmpty() ||
                expiry.getText().toString().length()<5){

            expiry.setError("Please enter valid value");
            expiry.requestFocus();
            check= false;
        } else if(expiry.getText().toString().length()>4 && Integer.parseInt(expYear) < yy) {


            expiry.setError("Card is expired");
            Toasty.error(context, "Card is expired", Toast.LENGTH_LONG).show();
          //  Log.d("CARD IS EXPIRED","DATE *"+Integer.parseInt(expMonth)+"/"+Integer.parseInt(expYear)+"* IS SAME OR GREATER THAN *"+mm+"*/*"+yy+"*");
            expiry.requestFocus();
            check= false;
        }

        else if(expiry.getText().toString().length()>4 && (Integer.parseInt(expYear) == yy && Integer.parseInt(expMonth) <= mm )){

            expiry.setError("Card is expired");
            Toasty.error(context, "Card is expired", Toast.LENGTH_LONG).show();
          //  Log.d("CARD IS EXPIRED","DATE *"+Integer.parseInt(expMonth)+"/"+Integer.parseInt(expYear)+"* IS SAME OR GREATER THAN *"+mm+"*/*"+yy+"*");
            expiry.requestFocus();
            check= false;
        }

        else if (cvv.getText().toString().trim() == null || cvv.getText().toString().trim().isEmpty()
                || cvv.getText().toString().trim().length()<3 ) {
            cvv.setError("Please enter valid value");
            cvv.requestFocus();
            check= false;
        }
        else if(!card_enter_pin.getText().toString().equalsIgnoreCase(card_confirm_pin.getText().toString())){
            card_enter_pin.setError("PIN Mismatch!");
            card_confirm_pin.setError("PIN Mismatch!");
            card_enter_pin.requestFocus();
            check= false;
        }

        return check;

    }

}