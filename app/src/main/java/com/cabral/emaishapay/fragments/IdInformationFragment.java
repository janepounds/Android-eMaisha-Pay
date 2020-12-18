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
import androidx.core.content.ContextCompat;
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
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.databinding.FragmentIdInformationBinding;
import com.cabral.emaishapay.models.AccountResponse;
import com.cabral.emaishapay.network.APIClient;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.cabral.emaishapay.fragments.PersonalInformationFragment.selectSpinnerItemByValue;

public class IdInformationFragment extends Fragment {
    private static final String TAG = "IdInformationFragment";
    private FragmentIdInformationBinding binding;
    private NavController navController = null;

    private String encodedIdFront;
    private String encodedIdBack;
    private ImageView imageView;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_id_information, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);

        if(getArguments()!=null){
            String idtype = getArguments().getString("idtype");
            String idNumber =getArguments().getString("idNumber");
            String expiryDate =getArguments().getString("expiryDate");


            //set edit textviews
            selectSpinnerItemByValue(binding.idType, idtype);
            binding.idNumber.setText(idNumber);
            binding.expiryDate.setText(expiryDate);


        }

        binding.expiryDatePicker.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            final DatePickerDialog mDatePicker = new DatePickerDialog(requireContext(), (datePicker, selectedYear, selectedMonth, selectedDay) -> {

                NumberFormat formatter = new DecimalFormat("00");
                String birthDate = selectedYear + "-" + formatter.format(selectedMonth + 1) + "-" + formatter.format(selectedDay);
                binding.expiryDate.setText(birthDate);
            }, year, month, day);
            mDatePicker.show();
        });

        binding.idFront.setOnClickListener(v -> {
            imageView = binding.idFront;
            // Choose Image from camera or gallery
            chooseImage();
        });

        binding.idBack.setOnClickListener(v -> {
            imageView = binding.idBack;
            // Choose Image from camera or gallery
            chooseImage();
        });

        binding.submitButton.setOnClickListener(v -> {
            if (validateEntries())
                saveInfo();
        });

        binding.cancelButton.setOnClickListener(view1 -> navController.popBackStack());

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);
    }

    public void saveInfo() {
        progressDialog.show();
        String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());

        Call<AccountResponse> call = APIClient.getWalletInstance()
                .storeIdInfo(userId, binding.idType.getSelectedItem().toString(), binding.idNumber.getText().toString(),
                        binding.expiryDate.getText().toString(), encodedIdFront, encodedIdBack);
        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(@NotNull Call<AccountResponse> call, @NotNull Response<AccountResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: successful");
                    navController.navigate(R.id.action_idInformationFragment_to_walletAccountFragment);
                } else {
                    Log.d(TAG, "onResponse: failed" + response.errorBody());
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NotNull Call<AccountResponse> call, @NotNull Throwable t) {
                Log.d(TAG, "onFailure: failed" + t.getMessage());
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent(getActivity(), ImageSelectActivity.class);
        intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true); // Default is true
        intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);   // Default is true
        intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);  // Default is true
        startActivityForResult(intent, 1);
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

            if (imageView == binding.idFront) {
                encodedIdFront = Base64.encodeToString(b, Base64.DEFAULT);
                Glide.with(requireContext()).asBitmap().load(Base64.decode(encodedIdFront, Base64.DEFAULT)).placeholder(R.drawable.user).into(binding.idFront);
            } else if (imageView == binding.idBack) {
                encodedIdBack = Base64.encodeToString(b, Base64.DEFAULT);
                Glide.with(requireContext()).asBitmap().load(Base64.decode(encodedIdBack, Base64.DEFAULT)).placeholder(R.drawable.user).into(binding.idBack);
            }
        }
    }

    public boolean validateEntries() {
        if (binding.idType.getSelectedItem().toString().equals("Select")) {
            binding.idTypeLayout.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.edittext_error));
            binding.idTypeLayout.requestFocus();
            Snackbar.make(requireActivity().findViewById(android.R.id.content), "ID type is required", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (binding.idNumber.getText().toString().isEmpty()) {
            binding.idNumber.setError("Required");
            binding.idNumber.requestFocus();
            Snackbar.make(requireActivity().findViewById(android.R.id.content), "ID number is required", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (binding.expiryDate.getText().toString().isEmpty()) {
            binding.expiryDateLayout.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.edittext_error));
            binding.expiryDate.requestFocus();
            Snackbar.make(requireActivity().findViewById(android.R.id.content), "Expiry date is required", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (encodedIdFront == null || encodedIdFront.isEmpty()) {
            binding.idFront.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.edittext_error));
            binding.idFront.requestFocus();
            Snackbar.make(requireActivity().findViewById(android.R.id.content), "ID image is required", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (encodedIdBack == null || encodedIdBack.isEmpty()) {
            binding.idBack.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.edittext_error));
            binding.idBack.requestFocus();
            Snackbar.make(requireActivity().findViewById(android.R.id.content), "ID image is required", Snackbar.LENGTH_LONG).show();
            return false;
        } else {
            binding.idNumber.setError(null);
            binding.idTypeLayout.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.edittext_corner));
            binding.expiryDateLayout.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.edittext_corner));
            binding.idFront.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.edittext_corner));
            binding.idBack.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.edittext_corner));
            return true;
        }
    }
}