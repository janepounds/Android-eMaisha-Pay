package com.cabral.emaishapay.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.databinding.FragmentBusinessAccountBinding;
import com.cabral.emaishapay.models.AccountResponse;
import com.cabral.emaishapay.network.APIClient;

import java.io.ByteArrayOutputStream;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessAccountFragment extends Fragment {
    private FragmentBusinessAccountBinding binding;
    private String role;
    private String encodedIdFront;
    private String encodedIdBack;
    private ImageView imageView;
    private String encodedIdreg_cert;
    private String encodedIdtradelicense;
    private ProgressDialog progressDialog;
    private Context context;
    Bundle localBundle;

    public BusinessAccountFragment(Bundle bundle) {
        // Required empty public constructor
        this.localBundle=bundle;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
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

        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.toolbar);
        //binding.toolbar.setTitle("Merchant Details");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        initializeViews();
        return binding.getRoot();
    }



    public void initializeViews(){
        if(localBundle!=null) {

            if (localBundle.getString("Agent")!=null) {
                binding.toolbar.setTitle(localBundle.getString("Agent"));
                role = "AGENT";

            }else if(localBundle.getString("Merchant")!=null){
                binding.toolbar.setTitle(localBundle.getString("Merchant"));
                role = "MERCHANT";

            }else if(localBundle.getString("AgentMerchant")!=null) {
                binding.toolbar.setTitle(localBundle.getString("AgentMerchant"));
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
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();
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
                if(response.isSuccessful()){
                    String message = response.body().getMessage();

                    //call success dialog
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_successful_message);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(false);
                    TextView text = dialog.findViewById(R.id.dialog_success_txt_message);
                    text.setText(message);

                    dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            //go to home activity fragment
                            Intent goToWallet = new Intent(context, WalletHomeActivity.class);
                            startActivity(goToWallet);
                        }
                    });
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();

                }else{
                    Toast.makeText(context,response.message(),Toast.LENGTH_LONG);
                }
                progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_LONG);

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