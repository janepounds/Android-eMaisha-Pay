package com.cabral.emaishapay.fragments.wallet_fragments;

import android.app.Activity;
import android.app.DatePickerDialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.databinding.FragmentIdInformationBinding;
import com.cabral.emaishapay.models.AccountResponse;
import com.cabral.emaishapay.network.api_helpers.APIClient;
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

import static com.cabral.emaishapay.fragments.wallet_fragments.AccountPersonalInformationFragment.selectSpinnerItemByValue;

public class IdInformationFragment extends Fragment {
    private static final String TAG = "IdInformationFragment";
    private FragmentIdInformationBinding binding;

    private String encodedIdFront;
    private String encodedIdBack;
    private ImageView imageView;
    DialogLoader dialogLoader;
    ActivityResultLauncher<Intent> imageChooserActivityResultLauncher;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        imageChooserActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // There are no request codes
                    showActivityResult(result,1);
                });
    }



    public IdInformationFragment() {
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_id_information, container, false);

        WalletHomeActivity.bottomNavigationView.setVisibility(View.GONE);
        WalletHomeActivity.scanCoordinatorLayout.setVisibility(View.GONE);
        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle("ID Information");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        if(getArguments()!=null){
            String idtype = getArguments().getString("idtype");
            String idNumber =getArguments().getString("idNumber");
            String expiryDate =getArguments().getString("expiryDate");
            String front =getArguments().getString("front");
            String back =getArguments().getString("back");

            Log.d(TAG, "onViewCreated: "+ ConstantValues.WALLET_DOMAIN+front);
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.add_default_image)
                    .error(R.drawable.add_default_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);

            //set edit textviews
            selectSpinnerItemByValue(binding.idType, idtype);

            binding.idType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        //Change selected text color
                        ((TextView) view).setTextColor(ContextCompat.getColor(getContext(),R.color.white));
                        //((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);//Change selected text size
                    } catch (Exception e) { }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });



            binding.idNumber.setText(idNumber);
            binding.expiryDate.setText(expiryDate);
            Glide.with(requireContext()).load(ConstantValues.WALLET_DOMAIN+front).apply(options).into(binding.idFront);
            Glide.with(requireContext()).load(ConstantValues.WALLET_DOMAIN+back).apply(options).into(binding.idBack);


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

        binding.cancelButton.setOnClickListener(view1 -> getParentFragmentManager().popBackStack());

        dialogLoader = new DialogLoader(getContext());

        return binding.getRoot();
    }

    public void saveInfo() {
        dialogLoader.showProgressDialog();
        String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        Call<AccountResponse> call = APIClient.getWalletInstance(getContext())
                .storeIdInfo(access_token,userId, binding.idType.getSelectedItem().toString(), binding.idNumber.getText().toString(),
                        binding.expiryDate.getText().toString(), encodedIdFront, encodedIdBack,request_id,category,"storeUserIDInfo");
        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(@NotNull Call<AccountResponse> call, @NotNull Response<AccountResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: successful");

                    WalletHomeActivity.navController.popBackStack(R.id.walletHomeFragment2,false);
                    WalletHomeActivity.navController.navigate(R.id.action_walletHomeFragment2_to_walletAccountFragment2);

                } else {
                    Log.d(TAG, "onResponse: failed" + response.errorBody());
                }

                dialogLoader.hideProgressDialog();
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
        imageChooserActivityResultLauncher.launch(intent);
    }


    private void showActivityResult(ActivityResult result, int requestCode) {

        int resultCode = result.getResultCode();
        Intent data = result.getData();

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
        } else if (binding.idNumber.getText().toString().isEmpty() || binding.idNumber.getText().toString().length() < 14) {
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
            binding.idTypeLayout.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.outline_primary));
            binding.expiryDateLayout.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.outline_primary));
            binding.idFront.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.outline_primary));
            binding.idBack.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.outline_primary));
            return true;
        }
    }
}