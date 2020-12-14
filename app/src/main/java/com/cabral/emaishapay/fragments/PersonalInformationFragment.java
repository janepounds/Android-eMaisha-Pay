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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.databinding.FragmentPersonalInformationBinding;
import com.cabral.emaishapay.databinding.FragmentWalletAccountBinding;
import com.cabral.emaishapay.models.AccountResponse;
import com.cabral.emaishapay.network.APIClient;

import java.io.ByteArrayOutputStream;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalInformationFragment extends Fragment {
    private static final String TAG = "PersonalInformation";
    private FragmentPersonalInformationBinding binding;
    private NavController navController = null;
    private String dob, gender, next_of_kin,next_of_kin_contact,picture;
    String mediaPath, encodedImageID = "N/A";


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
        saveInfo(view);
    }

    public void saveInfo(View view){
        binding.userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                startActivityForResult(intent, 1213);
            }
        });

        /******************RETROFIT IMPLEMENTATION****************************/
        dob = binding.dob.getText().toString();
        gender = binding.gender.getSelectedItem().toString();
        next_of_kin = binding.nextOfKinFirst + " " + binding.nextOfKinLast;
        next_of_kin_contact = "+256 " +binding.nextOfKinContact.getText().toString();
        picture = encodedImageID;
        Call<AccountResponse> call = APIClient.getWalletInstance()
                                .storePersonalInfo(dob,gender,next_of_kin,next_of_kin_contact,picture);
                                call.enqueue(new Callback<AccountResponse>() {
                                    @Override
                                    public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                                        if(response.isSuccessful()){
                                            Log.d(TAG, "onResponse: successful");
                                        }else {
                                            Log.d(TAG, "onResponse: failed"+response.errorBody());
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<AccountResponse> call, Throwable t) {
                                        Log.d(TAG, "onFailure: failed"+t.getMessage());

                                    }
                                });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1213 && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
            Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
            encodedImageID = encodeImage(selectedImage);
            binding.userPic.setImageBitmap(selectedImage);

        }
    }


    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }
}