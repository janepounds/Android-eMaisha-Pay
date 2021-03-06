package com.cabral.emaishapay.fragments.wallet_fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.kofigyan.stateprogressbar.StateProgressBar;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

import static android.app.Activity.RESULT_OK;


public class IdentityProofFragment extends Fragment {
    String[] descriptionData = {"Personal\n Details", "Contact\n Details", "Identity\n Proof" , "Card\n Details"};
    String mediaPathNationalID, encodedImageID = "N/A", mediaPathCustomerPhoto, encodedImageCustomerPhoto = "N/A", mediaPathPhotoWithID, encodedImagePhotoWithID = "N/A";
    TextView etxt_national_id, etxt_customer_photo, etxt_photo_with_id,etxt_nin_expiry_date;
    String firstname, lastname, middlename, gender, date_of_birth, district, village, sub_county, landmark, phone_number, email, next_of_kin_name, next_of_kin_second_name, next_of_kin_relationship, next_of_kin_contact;
    EditText etxt_nin;
    Spinner id_type;
    ActivityResultLauncher<Intent> nationalIdActivityResultLauncher, customerPhotoActivityResultLauncher, customerWithPhotoActivityResultLauncher;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        nationalIdActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // There are no request codes
                showActivityResult(result,1231);
            });
        customerPhotoActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // There are no request codes
                    showActivityResult(result,1232);
                });

        customerWithPhotoActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // There are no request codes
                    showActivityResult(result,1233);
                });
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
        village = getArguments().getString("village");
        landmark = getArguments().getString("landmark");
        email = getArguments().getString("email");
        phone_number = getArguments().getString("phone_number");
        next_of_kin_name = getArguments().getString("next_of_kin_name");
        next_of_kin_second_name = getArguments().getString("next_of_kin_second_name");
        next_of_kin_relationship = getArguments().getString("next_of_kin_relationship");
        next_of_kin_contact = getArguments().getString("next_of_kin_contact");
        getArguments().clear();
        return inflater.inflate(R.layout.fragment_identity_proof, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StateProgressBar stateProgressBar = view.findViewById(R.id.your_state_progress_bar_identity_proof);
        stateProgressBar.setStateDescriptionData(descriptionData);
        stateProgressBar.setStateDescriptionTypeface("font/nunito.ttf");
        WalletHomeActivity.bottom_navigation_shop.setVisibility(View.GONE);

        Button next = view.findViewById(R.id.txt_next_submit);
        TextView finger_print = view.findViewById(R.id.txt_next_finger_print);
        TextView txt_upload_national_id = view.findViewById(R.id.txt_browse_national_id_photo);
        TextView txt_upload_customer_photo = view.findViewById(R.id.txt_browse_customer_photo);
        TextView txt_upload_photo_with_id = view.findViewById(R.id.txt_browse_photo_with_id);


        etxt_nin = view.findViewById(R.id.etxt_nin);
        etxt_nin_expiry_date = view.findViewById(R.id.etxt_id_expiry_date);
        etxt_national_id = view.findViewById(R.id.etxt_national_id);
        etxt_customer_photo = view.findViewById(R.id.etxt_customer_photo);
        etxt_photo_with_id = view.findViewById(R.id.etxt_photo_with_id);
        id_type = view.findViewById(R.id.id_type);

        Toolbar toolbar = view.findViewById(R.id.toolbar_account_opening);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Account Opening");
        etxt_nin_expiry_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDatePicker(etxt_nin_expiry_date, getActivity());
            }
        });

        txt_upload_national_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                nationalIdActivityResultLauncher.launch(intent);
            }
        });
        etxt_national_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                nationalIdActivityResultLauncher.launch(intent);
            }
        });
        txt_upload_customer_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                customerPhotoActivityResultLauncher.launch(intent);
            }
        });
        etxt_customer_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                customerPhotoActivityResultLauncher.launch(intent);
            }
        });

        txt_upload_photo_with_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                customerWithPhotoActivityResultLauncher.launch(intent);
            }
        });
        etxt_photo_with_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                customerWithPhotoActivityResultLauncher.launch(intent);
            }
        });


        finger_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //openFragment(new FingerPrintAuthenticationFragment());

            }
        });
        id_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //Change selected text color
                    ((TextView) view).setTextColor(ContextCompat.getColor(getContext(),R.color.white));
                    ((TextView) view).setTextSize(14);

                } catch (Exception e) {

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button previous = view.findViewById(R.id.previous_button_two);
        previous.setOnClickListener(view2 -> getActivity().getSupportFragmentManager().popBackStack());

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateEntries()) {
                    String nin = etxt_nin.getText().toString().trim();
                    String valid_upto = etxt_nin_expiry_date.getText().toString().trim();
                    String idtype = id_type.getSelectedItem().toString().trim();

                    Log.d("Kin Name", next_of_kin_name);
                    Log.d("Kin Second Name", next_of_kin_second_name);
                    Log.d("Kin Relationship", next_of_kin_relationship);
                    Log.d("Kin Contact", next_of_kin_contact);

                    //call card details fragment CardDetail
                    Bundle bundle = new Bundle();
                    bundle.putString("firstname", firstname);
                    bundle.putString("lastname", lastname);
                    bundle.putString("middlename", middlename);
                    bundle.putString("date_of_birth", date_of_birth);
                    bundle.putString("customer_gender", gender);
                    bundle.putString("district", district);
                    bundle.putString("sub_county", sub_county);
                    bundle.putString("village", village);
                    bundle.putString("landmark", landmark);
                    bundle.putString("phone_number", phone_number);
                    bundle.putString("email", email);
                    bundle.putString("next_of_kin_name", next_of_kin_name);
                    bundle.putString("next_of_kin_second_name", next_of_kin_second_name);
                    bundle.putString("next_of_kin_relationship", next_of_kin_relationship);
                    bundle.putString("next_of_kin_contact", next_of_kin_contact);
                    bundle.putString("idtype",idtype);
                    bundle.putString("nin", nin);
                    bundle.putString("national_id_valid_upto", valid_upto);
                    bundle.putString("national_id_photo", encodedImageID);
                    bundle.putString("customer_photo", encodedImageCustomerPhoto);
                    bundle.putString("customer_photo_with_id", encodedImagePhotoWithID);

                    WalletHomeActivity.navController.navigate(R.id.action_identityProofFragment_to_cardDetail,bundle);
                }
            }
        });


    }


    private void showActivityResult(ActivityResult result, int requestCode) {
        try {
            // When an Image is picked
            int resultCode= result.getResultCode();
            Intent data = result.getData();

            if (resultCode == RESULT_OK && null != data) {
                if (requestCode == 1231) {


                    mediaPathNationalID = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                    Bitmap selectedImage = BitmapFactory.decodeFile(mediaPathNationalID);
                    encodedImageID = encodeImage(selectedImage);
                    etxt_national_id.setText(mediaPathNationalID);


                }
                if (requestCode == 1232) {


                    mediaPathCustomerPhoto = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                    Bitmap selectedImage = BitmapFactory.decodeFile(mediaPathCustomerPhoto);
                    encodedImageCustomerPhoto = encodeImage(selectedImage);
                    etxt_customer_photo.setText(mediaPathCustomerPhoto);


                }
                if (requestCode == 1233) {


                    mediaPathPhotoWithID = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                    Bitmap selectedImage = BitmapFactory.decodeFile(mediaPathPhotoWithID);
                    encodedImagePhotoWithID = encodeImage(selectedImage);
                    etxt_photo_with_id.setText(mediaPathPhotoWithID);


                }

            }


        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.something_went_wrong, Toast.LENGTH_LONG).show();
        }
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }



    public static void addDatePicker(final TextView ed_, final Context context) {
        ed_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                final DatePickerDialog mDatePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        int month = selectedmonth + 1;
                        NumberFormat formatter = new DecimalFormat("00");
                        ed_.setText(selectedyear + "-" + formatter.format(month) + "-" + formatter.format(selectedday));
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.show();

            }
        });
        ed_.setInputType(InputType.TYPE_NULL);
    }

    public boolean validateEntries(){
        boolean check = true;

        if (id_type.getSelectedItem().toString().trim().equalsIgnoreCase("Select")) {

            Toasty.error(requireContext(), "Please select ID type", Toast.LENGTH_LONG).show();
            id_type.requestFocus();
            check = false;

        }

        else if (etxt_nin.getText().toString().trim() == null || etxt_nin.getText().toString().trim().isEmpty() || etxt_nin.getText().toString().length() < 14 ) {

            etxt_nin.setError("Please enter valid NIN");
            etxt_nin.requestFocus();
            check = false;

         }

        else if (etxt_nin_expiry_date.getText().toString().trim() == null || etxt_nin_expiry_date.getText().toString().trim().isEmpty()){

            etxt_nin_expiry_date.setError("Please select valid date");
            etxt_nin_expiry_date.requestFocus();
            check = false;
        }

        else if (etxt_national_id.getText().toString().trim() == null || etxt_national_id.getText().toString().trim().isEmpty()) {

            etxt_national_id.setError("Please upload national ID photo");
            etxt_national_id.requestFocus();
            check = false;
        }
        else if (etxt_customer_photo.getText().toString().trim() == null || etxt_customer_photo.getText().toString().trim().isEmpty()) {

            etxt_customer_photo.setError("Please upload customer photo");
            etxt_customer_photo.requestFocus();
            check = false;
        }
        else if (etxt_photo_with_id.getText().toString().trim() == null || etxt_photo_with_id.getText().toString().trim().isEmpty()) {

            etxt_photo_with_id.setError("Please upload customer photo with ID");
            etxt_photo_with_id.requestFocus();
            check = false;
        }
        return check;
    }
}