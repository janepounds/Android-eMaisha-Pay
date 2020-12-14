package com.cabral.emaishapay.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
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

import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.databinding.FragmentIdInformationBinding;
import com.cabral.emaishapay.models.AccountResponse;
import com.cabral.emaishapay.network.APIClient;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IdInformationFragment extends Fragment {
    private static final String TAG = "IdInformationFragment";
    private FragmentIdInformationBinding binding;
    private NavController navController = null;
    private Context context;
    private String idType,idNumber,expiryDate,frontId,backId;
    String mediaPath, encodedImageID = "N/A";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_id_information, container, false);

        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);
        initializeViews(view);
    }

    public void initializeViews(View view){
        binding.expiryDatePicker.setOnClickListener(v -> addDatePickerImageView(binding.expiryDatePicker,binding.expiryDateTv,context));

        binding.imgIdFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                startActivityForResult(intent, 1213);
            }
        });

        binding.imgIdBack.setOnClickListener(new View.OnClickListener() {
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
        binding.submitButton.setOnClickListener(v -> {
            idType = binding.idType.getSelectedItem().toString();
            idNumber = binding.idNumber.getText().toString();
            expiryDate = binding.expiryDateTv.getText().toString();
            frontId = encodedImageID;
            backId = encodedImageID;

            Call<AccountResponse> call = APIClient.getWalletInstance()
                    .storeIdInfo(idType,idNumber,expiryDate,frontId,backId);
            call.enqueue(new Callback<AccountResponse>() {
                @Override
                public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                    if(response.isSuccessful()){

                        Log.d(TAG, "onResponse: successful");
                    }else{

                        Log.d(TAG, "onResponse: failed"+ response.errorBody());
                    }

                }

                @Override
                public void onFailure(Call<AccountResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure: failed"+t.getMessage());

                }
            });

        });



    }


    public static void addDatePickerImageView(final ImageView imageView, final TextView ed_, final Context context) {
        imageView.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1213 && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
            Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
            encodedImageID = encodeImage(selectedImage);
            binding.imgIdFront.setImageBitmap(selectedImage);
            binding.imgIdBack.setImageBitmap(selectedImage);
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