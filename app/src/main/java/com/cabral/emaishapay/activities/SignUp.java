package com.cabral.emaishapay.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
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
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cabral.emaishapay.DailogFragments.LoginOtpDialog;
import com.cabral.emaishapay.DailogFragments.SignUpOtpDialog;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.databinding.SignupBinding;
import com.cabral.emaishapay.network.APIClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.CircularImageView;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.database.DbHandlerSingleton;
import com.cabral.emaishapay.models.CropSpinnerItem;
import com.cabral.emaishapay.models.address_model.RegionDetails;
import com.cabral.emaishapay.models.user_model.UserData;
import com.cabral.emaishapay.utils.CheckPermissions;
import com.cabral.emaishapay.utils.ImagePicker;
import com.cabral.emaishapay.utils.LocaleHelper;
import com.cabral.emaishapay.utils.ValidateInputs;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import am.appwise.components.ni.NoInternetDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

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
    private Dialog dialogOTP;
    private EditText ed_otp;

    // Verification id that will be sent to the user
    private String mVerificationId;
    // Firebase auth object
    private FirebaseAuth mAuth;
    private int pickedDistrictId;
    private int pickedSubCountyId;
    private ArrayList<CropSpinnerItem> subCountyList = new ArrayList<>();
    private ArrayList<String> villageList = new ArrayList<>();

    public SignUp(Context context) {
        this.context = context;
    }

    public SignUp() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.signup);

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(this).build();
        //noInternetDialog.show();

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

            if (isValidData) {
                // Proceed User Registration

                sendVerificationCode(getResources().getString(R.string.ugandan_code) + binding.userMobile.getText().toString().trim());




            }
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
        dialogOTP = new Dialog(activity);
        dialogOTP.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogOTP.setCancelable(false);
        dialogOTP.setContentView(R.layout.dialog_otp);

        ed_otp = dialogOTP.findViewById(R.id.ed_otp);
        AppCompatButton btn_resend, btn_submit;
        btn_resend = dialogOTP.findViewById(R.id.btn_resend);
        btn_submit = dialogOTP.findViewById(R.id.btn_submit);

        btn_resend.setOnClickListener(view -> sendVerificationCode(binding.userMobile.getText().toString().trim()));

        btn_submit.setOnClickListener(view -> {
            if (!ed_otp.getText().toString().trim().isEmpty()) {
                verifyVerificationCode(ed_otp.getText().toString().trim());
            }
        });

        dialogOTP.show();
    }

    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(String mobile) {

//        showOTPDialog(this, "");

        //call otp dialog
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);
//        Bundle bundle = new Bundle();
//        bundle.putString("sms_code",response.body().getData().getSms_code());
        // Create and show the dialog.
        DialogFragment payLoandialog = new SignUpOtpDialog(SignUp.this,fm,mobile);
//        payLoandialog.setArguments(bundle);

        payLoandialog.show(ft, "dialog");

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

//                ed_otp.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(), "Verification failed : " + e.getMessage(), Toast.LENGTH_LONG).show();
            dialogOTP.dismiss();
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
                            Auth2Activity.processFurtherRegistration(SignUp.this, "0"+binding.userMobile.getText().toString(),
                                    binding.userFirstname.getText().toString(),
                                    binding.userLastname.getText().toString(),
                                    binding.villageSpinner.getText().toString(),
                                    binding.subCountySpinner.getText().toString(), binding.districtSpinner.getText().toString(),2);

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
        } else {
            return true;
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
}

