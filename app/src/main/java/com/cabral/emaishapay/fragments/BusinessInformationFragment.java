package com.cabral.emaishapay.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.databinding.FragmentBusinessInformationBinding;
import com.cabral.emaishapay.models.AccountResponse;
import com.cabral.emaishapay.network.APIClient;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessInformationFragment extends Fragment {
    private static final String TAG = "BusinessInformation";
    private FragmentBusinessInformationBinding binding;
    private NavController navController = null;

    // image picker code
    private static final int IMAGE_PICK_CODE = 0;
    //permission code
    private static final int PERMISSION_CODE = 1;

    Dialog dialog;
    private String encodedRegistrationCertificate;
    private String encodedTradeLicence;
    private ImageView imageView;
    private String business_name,location,regno,license_no,reg_certificate,trade_license;

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
            //check runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    //permission denied
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    //permission granted
                    chooseImage();
                }
            } else {
                //version is less than marshmallow
                chooseImage();
            }
        });

        binding.tradeLicense.setOnClickListener(v -> {
            imageView = binding.tradeLicense;
            //check runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    //permission denied
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    //permission granted
                    chooseImage();
                }
            } else {
                //version is less than marshmallow
                chooseImage();
            }
        });
        saveInfo();
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseImage();
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            Uri imageUri = null;
            Bitmap imageBitmap;

            assert data != null;
            if (data.getData() != null) {
                imageUri = data.getData();

                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] b = byteArrayOutputStream.toByteArray();

                    if (imageView == binding.registrationCertificate) {
                        encodedRegistrationCertificate = Base64.encodeToString(b, Base64.DEFAULT);
                    } else if (imageView == binding.tradeLicense) {
                        encodedTradeLicence = Base64.encodeToString(b, Base64.DEFAULT);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Glide.with(requireContext()).load(imageUri).into(imageView);
        }
    }

    public void saveInfo(){
        business_name = binding.businessName.getText().toString();
        location = binding.businessLocation.getText().toString();
        regno = binding.registrationNumber.getText().toString();
        license_no = binding.licenceNumber.getText().toString();

        Call<AccountResponse> call = APIClient.getWalletInstance()
                .storeBusinessInfo(business_name,location,regno,license_no,encodedRegistrationCertificate,encodedTradeLicence);
        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                if (response.isSuccessful()) {

                    Log.d(TAG, "onResponse: successful");
                } else {

                    Log.d(TAG, "onResponse: failed" + response.errorBody());
                }

            }

            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: failed" + t.getMessage());

            }
        });




    }

    public boolean validateEntries(){
        if(binding.businessName.getText().toString().isEmpty()){

            return false;
        } else if(binding.businessLocation.getText().toString().isEmpty()){
            return false;


        }else if(binding.licenceNumber.getText().toString().isEmpty()){

            return false;
        }

        else {
            return true;
        }
    }
}