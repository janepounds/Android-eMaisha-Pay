package com.cabral.emaishapay.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.databinding.FragmentPersonalInformationBinding;
import com.cabral.emaishapay.models.AccountResponse;
import com.cabral.emaishapay.network.APIClient;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Objects;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountPersonalInformationFragment extends Fragment {
    private static final String TAG = "PersonalInformation";
    private FragmentPersonalInformationBinding binding;
    private ProgressDialog progressDialog;
    String encodedImageID = "N/A";
    private String selectedGender,displayGender;

    public AccountPersonalInformationFragment() {

    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_personal_information, container, false);

        WalletHomeActivity.bottomNavigationView.setVisibility(View.GONE);
        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle(getString(R.string.AddPersonalInformation));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments()!=null) {
            String dob = getArguments().getString("dob");
            String gender = getArguments().getString("gender");
            String nok = getArguments().getString("nok");
            String nok_contact = getArguments().getString("nok_contact");
            String pic = getArguments().getString("picture") ;
            if(gender.equalsIgnoreCase("F")){
                displayGender = "Female";
            }else{
                displayGender ="Male";
            }

            String[] nok_split = nok.split(" ");


            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);



            //set edit textviews
            binding.dob.setText(dob);
            selectSpinnerItemByValue(binding.gender, displayGender);
            if(nok_split.length>0)
                binding.nextOfKinFirst.setText(nok_split[0]);
            if(nok_split.length>1)
                binding.nextOfKinLast.setText(nok_split[1]);
            if(!nok_contact.isEmpty())
                binding.nextOfKinContact.setText(nok_contact.substring(4));
            Glide.with(requireContext()).load(ConstantValues.WALLET_DOMAIN +pic).apply(options).into(binding.userPic);
            Log.d(TAG, "onViewCreated: "+pic +gender);

        }

        binding.datePicker.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, -18);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            final DatePickerDialog mDatePicker = new DatePickerDialog(requireContext(), (datePicker, selectedYear, selectedMonth, selectedDay) -> {

                NumberFormat formatter = new DecimalFormat("00");
                String birthDate = selectedYear + "-" + formatter.format(selectedMonth + 1) + "-" + formatter.format(selectedDay);
                binding.dob.setText(birthDate);
            }, year, month, day);
            mDatePicker.show();
        });

        binding.userPic.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ImageSelectActivity.class);
            intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true); // Default is true
            intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);   // Default is true
            intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);  // Default is true
            startActivityForResult(intent, 1);
        });

        binding.submitButton.setOnClickListener(v -> saveInfo());

        binding.cancelButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);
    }

    public void saveInfo() {
        progressDialog.show();
        String gender = binding.gender.getSelectedItem().toString();
        if(gender.equalsIgnoreCase("Male")){
            selectedGender = "M";

        }else{
            selectedGender ="F";
        }

        String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        Call<AccountResponse> call = APIClient.getWalletInstance(getContext())
                .storePersonalInfo(access_token,userId, binding.dob.getText().toString(), selectedGender,
                        binding.nextOfKinFirst.getText().toString() + " " + binding.nextOfKinLast.getText().toString(), "+256" + binding.nextOfKinContact.getText().toString(), encodedImageID,request_id,category,"storeUserPersonalInfo");
        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(@NotNull Call<AccountResponse> call, @NotNull Response<AccountResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: successful");

                    WalletHomeActivity.navController.popBackStack(R.id.walletHomeFragment2,false);
                    WalletHomeActivity.navController.navigate(R.id.action_walletHomeFragment2_to_walletAccountFragment2);

                } else {
                    Log.d(TAG, "onResponse: failed" + response.errorBody());
                    Toast.makeText(getContext(), "Network Failure!", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NotNull Call<AccountResponse> call, @NotNull Throwable t) {
                Log.d(TAG, "onFailure: failed" + t.getMessage());
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Network Failure!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            Bitmap imageBitmap;

            imageBitmap = BitmapFactory.decodeFile(data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] b = byteArrayOutputStream.toByteArray();

            encodedImageID = Base64.encodeToString(b, Base64.DEFAULT);

            Glide.with(requireContext()).asBitmap().load(Base64.decode(encodedImageID, Base64.DEFAULT)).placeholder(R.drawable.user).into(binding.userPic);
        }
    }

    public static void selectSpinnerItemByValue(Spinner spnr, String value) {

        if (value == null) {
            return;
        }

        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spnr.getAdapter();
        for (int position = 1; position < adapter.getCount(); position++) {

            String item = spnr.getAdapter().getItem(position) + "";
            if (item.toLowerCase().equals(value.toLowerCase())) {
                spnr.setSelection(position);
                return;
            }

        }
    }

}