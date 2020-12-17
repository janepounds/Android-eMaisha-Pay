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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.databinding.FragmentPersonalInformationBinding;
import com.cabral.emaishapay.models.AccountResponse;
import com.cabral.emaishapay.network.APIClient;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Objects;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalInformationFragment extends Fragment {
    private static final String TAG = "PersonalInformation";
    private FragmentPersonalInformationBinding binding;
    private NavController navController = null;
    private ProgressDialog progressDialog;
    String encodedImageID = "N/A";
    private String selectedGender;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_personal_information, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);

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

        binding.cancelButton.setOnClickListener(v -> navController.popBackStack());

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
        Call<AccountResponse> call = APIClient.getWalletInstance()
                .storePersonalInfo(userId, binding.dob.getText().toString(), selectedGender,
                        binding.nextOfKinFirst.getText().toString() + " " + binding.nextOfKinLast.getText().toString(), "+256" + binding.nextOfKinContact.getText().toString(), encodedImageID);
        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(@NotNull Call<AccountResponse> call, @NotNull Response<AccountResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: successful");
                    navController.navigate(R.id.action_personalInformationFragment_to_walletAccountFragment);
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



}