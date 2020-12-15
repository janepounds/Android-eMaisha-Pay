package com.cabral.emaishapay.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.databinding.FragmentPersonalInformationBinding;
import com.cabral.emaishapay.models.AccountResponse;
import com.cabral.emaishapay.network.APIClient;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalInformationFragment extends Fragment {
    private static final String TAG = "PersonalInformation";
    private FragmentPersonalInformationBinding binding;
    private NavController navController = null;
    String encodedImageID = "N/A";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
    }

    public void saveInfo() {
        Call<AccountResponse> call = APIClient.getWalletInstance().storePersonalInfo(binding.dob.getText().toString(), binding.gender.getSelectedItem().toString(),
                binding.nextOfKinFirst + " " + binding.nextOfKinLast, "+256 " + binding.nextOfKinContact.getText().toString(), encodedImageID);
        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(@NotNull Call<AccountResponse> call, @NotNull Response<AccountResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: successful");
                } else {
                    Log.d(TAG, "onResponse: failed" + response.errorBody());
                }
            }

            @Override
            public void onFailure(@NotNull Call<AccountResponse> call, @NotNull Throwable t) {
                Log.d(TAG, "onFailure: failed" + t.getMessage());
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