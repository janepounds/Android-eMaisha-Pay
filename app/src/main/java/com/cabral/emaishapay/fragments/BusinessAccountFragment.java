package com.cabral.emaishapay.fragments;

import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.databinding.FragmentBusinessAccountBinding;
import com.cabral.emaishapay.databinding.FragmentBusinessInformationBinding;
import com.cabral.emaishapay.models.AccountResponse;
import com.cabral.emaishapay.network.APIClient;

import java.io.ByteArrayOutputStream;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;

public class BusinessAccountFragment extends Fragment {
    private FragmentBusinessAccountBinding binding;
    private NavController navController = null;
    private String role;
    private String encodedIdFront;
    private String encodedIdBack;
    private ImageView imageView;
    private String encodedIdreg_cert;
    private String encodedIdtradelicense;

    public BusinessAccountFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_business_account, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);
        initializeViews();



    }

    public void initializeViews(){
        if(getArguments()!=null) {

            if (getArguments().getString("Agent")!=null) {
                binding.toolbar.setTitle(getArguments().getString("Agent"));
                role = "AGENT";

            }else if(getArguments().getString("Merchant")!=null){
                binding.toolbar.setTitle(getArguments().getString("Merchant"));
                role = "MERCHANT";

            }else if(getArguments().getString("AgentMerchant")!=null) {
                binding.toolbar.setTitle(getArguments().getString("AgentMerchant"));
                role = "AGENT_MERCHANT";

            }


        }
        binding.imgNidFront.setOnClickListener(v -> {
            imageView = binding.imgNidFront;
            chooseImage();
        });
        binding.imgNidBack.setOnClickListener(v -> {
            imageView = binding.imgNidBack;
            chooseImage();
        });
        binding.registrationCertificate.setOnClickListener(v -> {
                imageView = binding.registrationCertificate;
                chooseImage();
        });
        binding.tradeLicense.setOnClickListener(v -> {
                    imageView = binding.tradeLicense;
                    chooseImage();
        });

        binding.btnSubmit.setOnClickListener(v -> {
            if (validateEntries()) {
                saveInfo();
            }
        });



    }

    public void saveInfo(){
        String user_Id = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
        String business_name = binding.businessName.getText().toString();
        String registration_no = binding.registrationNumber.getText().toString();
        String proprietor_name = binding.proprietorName.getText().toString();
        String proprietor_nin = binding.proprietorNin.getText().toString();


        //**************RETROFIT IMPLEMENTATION******************//
        Call<AccountResponse> call = APIClient.getWalletInstance()
                .applyForBusiness(user_Id,business_name,registration_no,encodedIdreg_cert,encodedIdtradelicense,proprietor_name,proprietor_nin,encodedIdFront,encodedIdBack,role);
        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {

            }

            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {

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

            if (imageView == binding.imgNidFront) {
                encodedIdFront = Base64.encodeToString(b, Base64.DEFAULT);
                Glide.with(requireContext()).asBitmap().load(Base64.decode(encodedIdFront, Base64.DEFAULT)).placeholder(R.drawable.add_default_image).into(binding.imgNidFront);
            } else if (imageView == binding.imgNidBack) {
                encodedIdBack = Base64.encodeToString(b, Base64.DEFAULT);
                Glide.with(requireContext()).asBitmap().load(Base64.decode(encodedIdBack, Base64.DEFAULT)).placeholder(R.drawable.add_default_image).into(binding.imgNidBack);
            }else if(imageView == binding.registrationCertificate){
                encodedIdreg_cert = Base64.encodeToString(b, Base64.DEFAULT);
                Glide.with(requireContext()).asBitmap().load(Base64.decode(encodedIdreg_cert, Base64.DEFAULT)).placeholder(R.drawable.add_default_image).into(binding.registrationCertificate);
            }else if(imageView == binding.tradeLicense){
                encodedIdtradelicense = Base64.encodeToString(b, Base64.DEFAULT);
                Glide.with(requireContext()).asBitmap().load(Base64.decode(encodedIdtradelicense, Base64.DEFAULT)).placeholder(R.drawable.add_default_image).into(binding.tradeLicense);

            }
        }
    }

    public boolean validateEntries(){

        return true;
    }
}