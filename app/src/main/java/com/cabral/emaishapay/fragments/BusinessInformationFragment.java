package com.cabral.emaishapay.fragments;

import android.app.Activity;
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
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.databinding.FragmentBusinessInformationBinding;
import com.cabral.emaishapay.models.AccountResponse;
import com.cabral.emaishapay.network.APIClient;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessInformationFragment extends Fragment {
    private static final String TAG = "BusinessInformation";
    private FragmentBusinessInformationBinding binding;
    private NavController navController = null;

    private String encodedRegistrationCertificate;
    private String encodedTradeLicence;
    private ImageView imageView;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_business_information, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);

        binding.registrationCertificate.setOnClickListener(v -> {
            imageView = binding.registrationCertificate;
            // Choose Image from camera or gallery
            chooseImage();
        });

        binding.tradeLicense.setOnClickListener(v -> {
            imageView = binding.tradeLicense;
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

            if (imageView == binding.registrationCertificate) {
                encodedRegistrationCertificate = Base64.encodeToString(b, Base64.DEFAULT);
                Glide.with(requireContext()).asBitmap().load(Base64.decode(encodedRegistrationCertificate, Base64.DEFAULT)).placeholder(R.drawable.user).into(binding.registrationCertificate);
            } else if (imageView == binding.tradeLicense) {
                encodedTradeLicence = Base64.encodeToString(b, Base64.DEFAULT);
                Glide.with(requireContext()).asBitmap().load(Base64.decode(encodedTradeLicence, Base64.DEFAULT)).placeholder(R.drawable.user).into(binding.tradeLicense);
            }
        }
    }

    public void saveInfo() {
        progressDialog.show();
        String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());

        Call<AccountResponse> call = APIClient.getWalletInstance()
                .storeBusinessInfo(userId, binding.businessName.getText().toString(), binding.businessLocation.getText().toString(), binding.registrationNumber.getText().toString(),
                        binding.licenceNumber.getText().toString(), encodedRegistrationCertificate, encodedTradeLicence);
        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(@NotNull Call<AccountResponse> call, @NotNull Response<AccountResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: successful");
                    navController.navigate(R.id.action_businessInformationFragment_to_walletAccountFragment);
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

    public boolean validateEntries() {
        if (binding.businessName.getText().toString().isEmpty()) {
            binding.businessName.setError("Required");
            binding.businessName.requestFocus();
            Snackbar.make(requireActivity().findViewById(android.R.id.content), "Business name is required", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (binding.businessLocation.getText().toString().isEmpty()) {
            binding.businessLocation.setError("Required");
            binding.businessLocation.requestFocus();
            Snackbar.make(requireActivity().findViewById(android.R.id.content), "Business location is required", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (binding.licenceNumber.getText().toString().isEmpty()) {
            binding.licenceNumber.setError("Required");
            binding.licenceNumber.requestFocus();
            Snackbar.make(requireActivity().findViewById(android.R.id.content), "Licence number is required", Snackbar.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
}