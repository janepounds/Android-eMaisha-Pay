package com.cabral.emaishapay.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.adapters.BeneficiariesListAdapter;
import com.cabral.emaishapay.databinding.SignupBinding;
import com.cabral.emaishapay.fragments.TokenAuthFragment;
import com.cabral.emaishapay.models.BeneficiaryResponse;
import com.cabral.emaishapay.models.SecurityQnsResponse;
import com.cabral.emaishapay.network.APIClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.database.DbHandlerSingleton;
import com.cabral.emaishapay.models.CropSpinnerItem;
import com.cabral.emaishapay.models.address_model.RegionDetails;
import com.cabral.emaishapay.utils.CheckPermissions;
import com.cabral.emaishapay.utils.ImagePicker;
import com.cabral.emaishapay.utils.LocaleHelper;
import com.cabral.emaishapay.utils.ValidateInputs;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import am.appwise.components.ni.NoInternetDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.cabral.emaishapay.app.EmaishaPayApp.getContext;

/**
 * SignUp activity handles User's Registration
 **/

public class SignUp extends AppCompatActivity {
    private static final String TAG = "SignUp";
    private SignupBinding binding;

    private File profileImage;
    private static final int PICK_IMAGE_ID = 360;
    private DbHandlerSingleton dbHandler;
    private Context context;
    // the number doesn't matter

    DialogLoader dialogLoader;

    //Custom Dialog Vies
    private Dialog dialog;
    private EditText code1,code2,code3,code4,code5,code6;

    // Verification id that will be sent to the user
    private String mVerificationId;
    // Firebase auth object
    private FirebaseAuth mAuth;
    private int pickedDistrictId;
    private int pickedSubCountyId;
    private ArrayList<CropSpinnerItem> subCountyList = new ArrayList<>();
    private ArrayList<String> villageList = new ArrayList<>();
    private List<SecurityQnsResponse.SecurityQns> securityQnsList = new ArrayList();
    ArrayList<String> securityQns = new ArrayList<>();
    ArrayList<String> securityQnsSubList1 = new ArrayList<>();
    ArrayList<String> securityQnsSubList2 = new ArrayList<>();
    ArrayList<String> securityQnsSubList3 = new ArrayList<>();

    public SignUp(Context context) {
        this.context = context;
    }

    public SignUp() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        binding = DataBindingUtil.setContentView(this, R.layout.signup);

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(this).build();
        //noInternetDialog.show();
        FirebaseApp.initializeApp(context);
        //initializing objects
        mAuth = FirebaseAuth.getInstance();

        ArrayList<CropSpinnerItem> districtList = new ArrayList<>();
        dbHandler = DbHandlerSingleton.getHandlerInstance(getApplicationContext());
        try {
            for (RegionDetails x : dbHandler.getRegionDetails("district")) {
                districtList.add(new CropSpinnerItem() {
                    @Override
                    public String getId() {
                        return String.valueOf(x.getId());
                    }

                    @Override
                    public String getUnits() {
                        return null;
                    }

                    @NonNull
                    @Override
                    public String toString() {
                        return x.getRegion();
                    }
                });

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "onCreate: " + districtList + districtList.size());
        ArrayAdapter<CropSpinnerItem> districtListAdapter = new ArrayAdapter<CropSpinnerItem>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, districtList);
        binding.districtSpinner.setThreshold(1);
        binding.districtSpinner.setAdapter(districtListAdapter);
        binding.districtSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.districtSpinner.showDropDown();
                for (int i = 0; i < districtList.size(); i++) {

                    if (districtList.get(i).toString().equals(binding.districtSpinner.getText().toString())) {
                        pickedDistrictId = Integer.parseInt(districtList.get(i).getId());

                        Log.d(TAG, "onCreate: " + pickedDistrictId);

                        subCountyList.clear();
                        try {
                            for (RegionDetails x : dbHandler.getSubcountyDetails(String.valueOf(pickedDistrictId), "subcounty")) {
                                subCountyList.add(new CropSpinnerItem() {
                                    @Override
                                    public String getId() {
                                        return String.valueOf(x.getId());
                                    }

                                    @Override
                                    public String getUnits() {
                                        return null;
                                    }

                                    @NonNull
                                    @Override
                                    public String toString() {
                                        return x.getRegion();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "onCreate: " + subCountyList);
                        ArrayAdapter<CropSpinnerItem> subCountyListAdapter = new ArrayAdapter<CropSpinnerItem>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, subCountyList);
                        binding.subCountySpinner.setThreshold(1);
                        binding.subCountySpinner.setAdapter(subCountyListAdapter);
                    }

                }
            }
        });

        binding.subCountySpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.subCountySpinner.showDropDown();

                for (int i = 0; i < subCountyList.size(); i++) {

                    if (subCountyList.get(i).toString().equals(binding.subCountySpinner.getText().toString())) {
                        pickedSubCountyId = Integer.parseInt(subCountyList.get(i).getId());

                        Log.d(TAG, "onCreate: " + pickedSubCountyId);

                        villageList.clear();
                        try {
                            for (RegionDetails x : dbHandler.getVillageDetails(String.valueOf(pickedSubCountyId), "village")) {
                                villageList.add(x.getRegion());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "onCreate: " + villageList);
                        ArrayAdapter<String> villageListAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, villageList);
                        binding.villageSpinner.setThreshold(1);
                        binding.villageSpinner.setAdapter(villageListAdapter);
                    }


                }
            }
        });

        binding.villageSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dialogLoader = new DialogLoader(this);

        binding.textPrivacyPolicy.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
            dialog.setView(dialogView);
            dialog.setCancelable(true);

            final ImageButton dialog_button = dialogView.findViewById(R.id.dialog_button);
            final TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
            final WebView dialog_webView = dialogView.findViewById(R.id.dialog_webView);

            dialog_title.setText(getString(R.string.privacy_policy));


            String description = ConstantValues.PRIVACY_POLICY;
            String styleSheet = "<style> " +
                    "body{background:#eeeeee; margin:10; padding:10} " +
                    "p{color:#757575;} " +
                    "img{display:inline; height:auto; max-width:100%;}" +
                    "</style>";

            dialog_webView.setVerticalScrollBarEnabled(true);
            dialog_webView.setHorizontalScrollBarEnabled(false);
            dialog_webView.setBackgroundColor(Color.TRANSPARENT);
            dialog_webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            dialog_webView.loadDataWithBaseURL(null, styleSheet + description, "text/html", "utf-8", null);


            final AlertDialog alertDialog = dialog.create();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                alertDialog.getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
            }

            dialog_button.setOnClickListener(v1 -> alertDialog.dismiss());

            alertDialog.show();

        });

        binding.textTermsOfService.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
            dialog.setView(dialogView);
            dialog.setCancelable(true);

            final ImageButton dialog_button = dialogView.findViewById(R.id.dialog_button);
            final TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
            final WebView dialog_webView = dialogView.findViewById(R.id.dialog_webView);

            dialog_title.setText(getString(R.string.service_terms));


            String description = ConstantValues.TERMS_SERVICES;
            String styleSheet = "<style> " +
                    "body{background:#eeeeee; margin:10; padding:10} " +
                    "p{color:#757575;} " +
                    "img{display:inline; height:auto; max-width:100%;}" +
                    "</style>";

            dialog_webView.setVerticalScrollBarEnabled(true);
            dialog_webView.setHorizontalScrollBarEnabled(false);
            dialog_webView.setBackgroundColor(Color.TRANSPARENT);
            dialog_webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            dialog_webView.loadDataWithBaseURL(null, styleSheet + description, "text/html", "utf-8", null);


            final AlertDialog alertDialog = dialog.create();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                alertDialog.getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
            }

            dialog_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.show();
        });

        // Handle Click event of signUpBtn Button
        binding.signUpBtn.setOnClickListener(v -> {
            // Validate Login Form Inputs
            boolean isValidData = validateForm();
//
//            if (isValidData) {
//                // Proceed User Registration
//
//                sendVerificationCode(getResources().getString(R.string.ugandan_code) + binding.userMobile.getText().toString().trim());
//            }

                    if (isValidData) {
                // Proceed User Registration
                        binding.selectedSignUp.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_cornor_bg, null));
                        binding.selectedSecurityQns.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_corners_button, null));
                        binding.layoutSignUp.setVisibility(View.GONE);
                        binding.layoutSecurityQns.setVisibility(View.VISIBLE);
                        //get security qns
                        RequestSecurityQns();

            }

        });

        binding.btnSubmitSecurityQn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                boolean isValidData = validateSecurityQns();
                if (isValidData) {
//                // Proceed User Registration
//
                sendVerificationCode(getResources().getString(R.string.ugandan_code) + binding.userMobile.getText().toString().trim());
            }

            }
        });





        binding.btnBack.setOnClickListener(v -> {


            binding.selectedSecurityQns.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_cornor_bg, null));
            binding.selectedSignUp.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_corners_button, null));
            binding.layoutSecurityQns.setVisibility(View.GONE);
            binding.layoutSignUp.setVisibility(View.VISIBLE);
        });
    }

    //*********** Picks User Profile Image from Gallery or Camera ********//

    private void pickImage() {
        // Intent with Image Picker Apps from the static method of ImagePicker class
        Intent chooseImageIntent = ImagePicker.getImagePickerIntent(com.cabral.emaishapay.activities.SignUp.this);
        chooseImageIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        chooseImageIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        // Start Activity with Image Picker Intent
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }

    //*********** Receives the result from a previous call of startActivityForResult(Intent, int) ********//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // Handle Activity Result
            if (requestCode == PICK_IMAGE_ID) {

                // Get the User Selected Image as Bitmap from the static method of ImagePicker class
                Bitmap bitmap = ImagePicker.getImageFromResult(com.cabral.emaishapay.activities.SignUp.this, resultCode, data);

                // Upload the Bitmap to ImageView
                binding.userPhoto.setImageBitmap(bitmap);

                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(getApplicationContext(), bitmap);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                profileImage = new File(getRealPathFromURI(tempUri));
    
                /*File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                
                // Get the converted Bitmap as Base64ImageString from the static method of Helper class
                profileImage = Utilities.getBase64ImageStringFromBitmap(bitmap);*/

            }
        }
    }

    // Getting image URI
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    // Get absolute image path
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    /// Custom dialog for OTP
    public void showOTPDialog(Activity activity, String msg) {

        //call success dialog
        dialog  = new Dialog(activity);
        dialog.setContentView(R.layout.login_dialog_otp);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        code1= dialog.findViewById(R.id.otp_code1_et);
        code2= dialog.findViewById(R.id.otp_code2_et);
        code3= dialog.findViewById(R.id.otp_code3_et);
        code4= dialog.findViewById(R.id.otp_code4_et);
        code5=dialog.findViewById(R.id.otp_code5_et);
        code6= dialog.findViewById(R.id.otp_code6_et);
              code1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                code2.requestFocus();
            }
        });


        code2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                code3.requestFocus();
            }
        });

        code3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                code4.requestFocus();
            }
        });

        code4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                code5.requestFocus();
            }
        });

        code5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                code6.requestFocus();
            }
        });


        code6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                verifyVerificationCode(code1.getText().toString()+code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString());
            }
        });
        dialog.findViewById(R.id.login_otp_resend_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode(binding.userMobile.getText().toString().trim());
            }
        });
        dialog.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String code = code1.getText().toString() + code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString();

                verifyVerificationCode(code);
            }
        });
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

    }

    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(String mobile) {

        showOTPDialog(this, "");

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(mobile)                        // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS)      // Timeout and unit
                        .setActivity(this)                             // Activity (for callback binding)
                        .setCallbacks(mCallbacks)                      // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                mobile,
//                60,
//                TimeUnit.SECONDS,
//                TaskExecutors.MAIN_THREAD,
//                mCallbacks);
    }

    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                //split code

//                ed_otp.setText(code);

                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(), "Verification failed : " + e.getMessage(), Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }

        @Override
        public void onCodeSent(@NotNull String s, @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    //*********** This method is invoked for every call on requestPermissions(Activity, String[], int) ********//

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
//                            dialogOTP.dismiss();

                            //Final Registration Call to API
                            ConfirmActivity.processFurtherRegistration(SignUp.this, "0"+binding.userMobile.getText().toString(),
                                    binding.userFirstname.getText().toString(),
                                    binding.userLastname.getText().toString(),
                                    binding.villageSpinner.getText().toString(),
                                    binding.subCountySpinner.getText().toString(),
                                    binding.districtSpinner.getText().toString(),
                                    binding.idType.getSelectedItem().toString(),
                                    binding.idNumber.getText().toString(),
                                    binding.spFirstSecurityQn.getSelectedItem().toString(),
                                    binding.spSecondSecurityQn.getSelectedItem().toString(),
                                    binding.spThirdSecurityQn.getSelectedItem().toString(),
                                    binding.etxtFirstSecurityQn.getText().toString(),
                                    binding.etxtSecondSecurityQn.getText().toString(),
                                    binding.etxtThirdSecurityQn.getText().toString(),
                                    2);

                        } else {
                            //verification unsuccessful.. display an error message
                            String message = "Something is wrong, we will fix it soon...";
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.credential_container), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", v -> snackbar.dismiss());
                            snackbar.show();
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CheckPermissions.PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // The Camera and Storage Permission is granted
                pickImage();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(com.cabral.emaishapay.activities.SignUp.this, Manifest.permission.CAMERA)) {
                    // Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(com.cabral.emaishapay.activities.SignUp.this);
                    builder.setTitle(getString(R.string.permission_camera_storage));
                    builder.setMessage(getString(R.string.permission_camera_storage_needed));
                    builder.setPositiveButton(getString(R.string.grant), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions
                                    (
                                            com.cabral.emaishapay.activities.SignUp.this,
                                            new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            CheckPermissions.PERMISSIONS_REQUEST_CAMERA
                                    );
                        }
                    });
                    builder.setNegativeButton(getString(R.string.not_now), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(com.cabral.emaishapay.activities.SignUp.this, getString(R.string.permission_rejected), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //*********** Proceed User Registration Request ********//


    //*********** Validate SignUp Form Inputs ********//

    private boolean validateForm() {
        if (!ValidateInputs.isValidName(binding.userFirstname.getText().toString().trim())) {
            binding.userFirstname.setError(getString(R.string.invalid_first_name));
            return false;
        } else if (!ValidateInputs.isValidName(binding.userLastname.getText().toString().trim())) {
            binding.userLastname.setError(getString(R.string.invalid_last_name));
            return false;
        } else if (!ValidateInputs.isValidNumber(binding.userMobile.getText().toString().trim())) {
            binding.userMobile.setError(getString(R.string.invalid_contact));
            return false;
        } else if (binding.districtSpinner.getText().toString().equals("District")) {
            Toast.makeText(this, "Please select District", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.subCountySpinner.getText().toString().equals("Sub County")) {
            Toast.makeText(this, "Please select Sub County", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.villageSpinner.getText().toString().equals("Village")) {
            Toast.makeText(this, "Please select Village", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.idType.getSelectedItem().toString().equalsIgnoreCase("select")) {
            Toast.makeText(this, "Please select IdType", Toast.LENGTH_SHORT).show();
            return false;
        }else if (binding.idNumber.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter Id no", Toast.LENGTH_SHORT).show();
            return false;
        }else if (binding.idType.getSelectedItem().toString().equalsIgnoreCase("national id") && binding.idNumber.getText().toString().length()<15) {
            Toast.makeText(this, "Please enter valid id", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }

    //*********** Validate Security Qns Form Inputs ********//
    private boolean validateSecurityQns() {

       if (binding.etxtFirstSecurityQn.getText().toString().isEmpty()) {
          Toast.makeText(this, "Please enter 1st security question", Toast.LENGTH_SHORT).show();
          return false;
      }else if (binding.etxtSecondSecurityQn.getText().toString().isEmpty()) {
          Toast.makeText(this, "Please enter 2nd security question", Toast.LENGTH_SHORT).show();
          return false;
      }else{
          return  true;

      }
    }


    //*********** Set the Base Context for the ContextWrapper ********//

    @Override
    protected void attachBaseContext(Context newBase) {

        String languageCode = ConstantValues.LANGUAGE_CODE;
        if ("".equalsIgnoreCase(languageCode))
            languageCode = ConstantValues.LANGUAGE_CODE = "en";

        super.attachBaseContext(LocaleHelper.wrapLocale(newBase, languageCode));
    }
    //*********** Called when the Activity has detected the User pressed the Back key ********//

    @Override
    public void onBackPressed() {
        // Finish SignUpActivity to goto the LoginActivity
        finish();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
        }

        return super.onOptionsItemSelected(item);
    }

    public void RequestSecurityQns(){

        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        /******************RETROFIT IMPLEMENTATION***********************/
        Call<SecurityQnsResponse> call = APIClient.getWalletInstance(getContext()).getSecurityQns(access_token,request_id);
        call.enqueue(new Callback<SecurityQnsResponse>() {
            @Override
            public void onResponse(Call<SecurityQnsResponse> call, Response<SecurityQnsResponse> response) {
                if(response.isSuccessful()){

                    try {

                        securityQnsList = response.body().getSecurity_qnsList();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        Log.d(TAG,securityQnsList.size()+"**********");

                        //set security qns adapter
                        for(int i=0;i<securityQnsList.size();i++){
                            String security_Qn_name = securityQnsList.get(i).getSecurity_qn_name();
                            securityQns.add(security_Qn_name);


                        }
                        for(int i=0;i<3;i++) {
                            securityQnsSubList1.add(securityQns.get(i));

                        }for(int i=3;i<6;i++){
                            securityQnsSubList2.add(securityQns.get(i));


                        }for(int i=6;i<9;i++){

                            securityQnsSubList3.add(securityQns.get(i));



                        }




                        //set list in beneficiary spinner
                        ArrayAdapter<String> beneficiariesAdapter1 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, securityQnsSubList1);
                        ArrayAdapter<String> beneficiariesAdapter2 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, securityQnsSubList2);
                        ArrayAdapter<String> beneficiariesAdapter3 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, securityQnsSubList3);
                        binding.spFirstSecurityQn.setAdapter(beneficiariesAdapter1);
                        binding.spSecondSecurityQn.setAdapter(beneficiariesAdapter2);
                        binding.spThirdSecurityQn.setAdapter(beneficiariesAdapter3);

                    }

                }else if (response.code() == 401) {

//                    TokenAuthActivity.startAuth(, true);
//                    finishAffinity();
//                    if (response.errorBody() != null) {
//                        Log.e("info", new String(String.valueOf(response.errorBody())));
//                    } else {
//                        Log.e("info", "Something got very very wrong");
//                    }
                }

            }

            @Override
            public void onFailure(Call<SecurityQnsResponse> call, Throwable t){
            }
        });





    }
}

