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

import com.cabral.emaishapay.R;
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
    private View parentView;
    private File profileImage;
    private static final int PICK_IMAGE_ID = 360;
    private DbHandlerSingleton dbHandler;
    private Context context;
    // the number doesn't matter

    ActionBar actionBar;
    DialogLoader dialogLoader;

    //AdView mAdView;
    Button signupBtn;
    FrameLayout banner_adView;
    //  TextView signup_loginText;
    TextView termsOfService, privacyPolicy, and_text;
    CircularImageView user_photo;
    //FloatingActionButton user_photo_edit_fab;
    EditText firstName, lastName, phoneNumber, user_email;
    private AutoCompleteTextView district, subCounty, village;

    //Custom Dialog Vies
    Dialog dialogOTP;
    EditText ed_otp, userPassword, userConfirmPassword;

    // Verification id that will be sent to the user
    private String mVerificationId;
    // Firebase auth object
    private FirebaseAuth mAuth;
    private int pickedDistrictId;
    private int pickedSubcountyId;
    private ArrayList<CropSpinnerItem> subcountyList = new ArrayList<>();
    private ArrayList<String> villageList = new ArrayList<>();

    public SignUp(Context context){
        this.context = context;
    }
    public SignUp(){
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        //MobileAds.initialize(this, ConstantValues.ADMOBE_ID);
        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(com.cabral.emaishapay.activities.SignUp.this).build();
        //noInternetDialog.show();

        //initializing objects
        mAuth = FirebaseAuth.getInstance();

        // Binding Layout Views
        firstName = (EditText) findViewById(R.id.user_firstname);
        lastName = (EditText) findViewById(R.id.user_lastname);
        phoneNumber = (EditText) findViewById(R.id.user_mobile);
        district = findViewById(R.id.district_spinner);
        subCounty = findViewById(R.id.sub_county_spinner);
        village = findViewById(R.id.village_spinner);

        user_photo = (CircularImageView) findViewById(R.id.user_photo);
        user_email = (EditText) findViewById(R.id.user_email);
        signupBtn = (Button) findViewById(R.id.signUpBtn);
        userPassword = (EditText) findViewById(R.id.user_password);
        userConfirmPassword = (EditText) findViewById(R.id.user_confirm_password);
        termsOfService = (TextView) findViewById(R.id.text_terms_of_service);
        privacyPolicy = (TextView) findViewById(R.id.text_privacy_policy);

/*
        if (ConstantValues.IS_ADMOBE_ENABLED) {
            // Initialize Admobe
            mAdView = new AdView(SignUp.this);
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId(ConstantValues.AD_UNIT_ID_BANNER);
            AdRequest adRequest = new AdRequest.Builder().build();
            banner_adView.addView(mAdView);
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    banner_adView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    banner_adView.setVisibility(View.GONE);
                }
            });
        }
        */
        ArrayList<CropSpinnerItem> districtList = new ArrayList<>();
        dbHandler = DbHandlerSingleton.getHandlerInstance(context);
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
        Log.d(TAG, "onCreate: "+ districtList + districtList.size());
        ArrayAdapter<CropSpinnerItem> districtListAdapter = new ArrayAdapter<CropSpinnerItem>(getApplicationContext(),  android.R.layout.simple_dropdown_item_1line, districtList);
        district.setThreshold(1);
        district.setAdapter(districtListAdapter);
        district.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                district.showDropDown();
                for (int i = 0; i < districtList.size(); i++) {

                    if (districtList.get(i).toString().equals(district.getText().toString())) {
                        pickedDistrictId = Integer.parseInt(districtList.get(i).getId());

                        Log.d(TAG, "onCreate: "+ pickedDistrictId);

                        subcountyList.clear();
                        try {
                            for (RegionDetails x : dbHandler.getSubcountyDetails(String.valueOf(pickedDistrictId),"subcounty")) {
                                subcountyList.add(new CropSpinnerItem() {
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
                        Log.d(TAG, "onCreate: "+ subcountyList);
                        ArrayAdapter<CropSpinnerItem> subcountryListAdapter = new ArrayAdapter<CropSpinnerItem>(getApplicationContext(),  android.R.layout.simple_dropdown_item_1line, subcountyList);
                        subCounty.setThreshold(1);
                        subCounty.setAdapter(subcountryListAdapter);
                    }


                }
            }
        });






        subCounty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                subCounty.showDropDown();

                for (int i = 0; i < subcountyList.size(); i++) {

                    if (subcountyList.get(i).toString().equals(subCounty.getText().toString())) {
                        pickedSubcountyId = Integer.parseInt(subcountyList.get(i).getId());

                        Log.d(TAG, "onCreate: "+ pickedSubcountyId);

                        villageList.clear();
                        try {
                            for (RegionDetails x : dbHandler.getVillageDetails(String.valueOf(pickedSubcountyId),"village")) {
                                villageList.add(x.getRegion());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "onCreate: "+ villageList);
                        ArrayAdapter<String> villageListAdapter = new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_dropdown_item_1line, villageList);
                        village.setThreshold(1);
                        village.setAdapter(villageListAdapter);
                    }


                }
            }
        });

        village.addTextChangedListener(new TextWatcher() {
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

        dialogLoader = new DialogLoader(com.cabral.emaishapay.activities.SignUp.this);

        privacyPolicy.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(com.cabral.emaishapay.activities.SignUp.this, android.R.style.Theme_NoTitleBar);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
            dialog.setView(dialogView);
            dialog.setCancelable(true);

            final ImageButton dialog_button = (ImageButton) dialogView.findViewById(R.id.dialog_button);
            final TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
            final WebView dialog_webView = (WebView) dialogView.findViewById(R.id.dialog_webView);

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
                alertDialog.getWindow().setStatusBarColor(ContextCompat.getColor(com.cabral.emaishapay.activities.SignUp.this, R.color.colorPrimaryDark));
            }

            dialog_button.setOnClickListener(v1 -> alertDialog.dismiss());

            alertDialog.show();

        });

//        refund_policy.setOnClickListener(v -> {
//            AlertDialog.Builder dialog = new AlertDialog.Builder(SignUp.this, android.R.style.Theme_NoTitleBar);
//            View dialogView = getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
//            dialog.setView(dialogView);
//            dialog.setCancelable(true);
//
//            final ImageButton dialog_button = (ImageButton) dialogView.findViewById(R.id.dialog_button);
//            final TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
//            final WebView dialog_webView = (WebView) dialogView.findViewById(R.id.dialog_webView);
//
//            dialog_title.setText(getString(R.string.refund_policy));
//
//
//            String description = ConstantValues.REFUND_POLICY;
//            String styleSheet = "<style> " +
//                    "body{background:#eeeeee; margin:10; padding:10} " +
//                    "p{color:#757575;} " +
//                    "img{display:inline; height:auto; max-width:100%;}" +
//                    "</style>";
//
//            dialog_webView.setVerticalScrollBarEnabled(true);
//            dialog_webView.setHorizontalScrollBarEnabled(false);
//            dialog_webView.setBackgroundColor(Color.TRANSPARENT);
//            dialog_webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
//            dialog_webView.loadDataWithBaseURL(null, styleSheet + description, "text/html", "utf-8", null);
//
//
//            final AlertDialog alertDialog = dialog.create();
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//                alertDialog.getWindow().setStatusBarColor(ContextCompat.getColor(SignUp.this, R.color.colorPrimaryDark));
//            }
//
//            dialog_button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    alertDialog.dismiss();
//                }
//            });
//
//            alertDialog.show();
//        });

        termsOfService.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(com.cabral.emaishapay.activities.SignUp.this, android.R.style.Theme_NoTitleBar);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
            dialog.setView(dialogView);
            dialog.setCancelable(true);

            final ImageButton dialog_button = (ImageButton) dialogView.findViewById(R.id.dialog_button);
            final TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
            final WebView dialog_webView = (WebView) dialogView.findViewById(R.id.dialog_webView);

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
                alertDialog.getWindow().setStatusBarColor(ContextCompat.getColor(com.cabral.emaishapay.activities.SignUp.this, R.color.colorPrimaryDark));
            }

            dialog_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.show();
        });

        // Handle Click event of signup_loginText TextView
        //   signup_loginText.setOnClickListener(v -> {
        // Finish SignUpActivity to goto the LoginActivity
        //  finish();
        // overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
        //   });

        // Handle Click event of signupBtn Button
        signupBtn.setOnClickListener(v -> {
            // Validate Login Form Inputs
            boolean isValidData = validateForm();

            if (isValidData) {
                parentView = v;
                // Proceed User Registration

                sendVerificationCode(getResources().getString(R.string.ugandan_code)+phoneNumber.getText().toString().trim());

            }
        });

//        binding.textTermsOfService.setOnClickListener(v -> {
////            Toast.makeText(SignUp.this, "Terms of service", Toast.LENGTH_SHORT).show();
//
//            Uri uri = Uri.parse("http://www.google.com"); // missing 'http://' will cause crashed
//            startActivity(new Intent(Intent.ACTION_VIEW, uri));
//        });

//        binding.textPrivacyPolicy.setOnClickListener(v -> {
////            Toast.makeText(SignUp.this, "Privacy Policy", Toast.LENGTH_SHORT).show();
//
//            Uri uri = Uri.parse("http://www.google.com"); // missing 'http://' will cause crashed
//            startActivity(new Intent(Intent.ACTION_VIEW, uri));
//        });
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
                user_photo.setImageBitmap(bitmap);

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

        btn_resend.setOnClickListener(view -> sendVerificationCode(phoneNumber.getText().toString().trim()));

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

        showOTPDialog(com.cabral.emaishapay.activities.SignUp.this, "");

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
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

                ed_otp.setText(code);
                //verifying the code
                verifyVerificationCode(code);
                processRegistration();
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(com.cabral.emaishapay.activities.SignUp.this, e.getMessage(), Toast.LENGTH_LONG).show();
            dialogOTP.dismiss();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
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
                .addOnCompleteListener(com.cabral.emaishapay.activities.SignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            dialogOTP.dismiss();

                            //Final Registration Call to API
                            processRegistration();
                        } else {
                            //verification unsuccessful.. display an error message
                            String message = "Somthing is wrong, we will fix it soon...";
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.credential_container), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
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

    private void processRegistration() {

        dialogLoader.showProgressDialog();

        RequestBody fName = RequestBody.create(MediaType.parse("text/plain"), firstName.getText().toString().trim());
        RequestBody lName = RequestBody.create(MediaType.parse("text/plain"), lastName.getText().toString().trim());
        RequestBody customersTelephone = RequestBody.create(MediaType.parse("text/plain"),  "0"+phoneNumber.getText().toString().trim());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), user_email.getText().toString().trim());
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), userPassword.getText().toString().trim());
        RequestBody countryCode = RequestBody.create(MediaType.parse("text/plain"), getResources().getString(R.string.ugandan_code));
        RequestBody addressStreet = RequestBody.create(MediaType.parse("text/plain"), village.getText().toString());
        RequestBody addressCityOrTown = RequestBody.create(MediaType.parse("text/plain"), subCounty.getText().toString());
        RequestBody addressDistrict = RequestBody.create(MediaType.parse("text/plain"), district.getText().toString());

        Call<UserData> call = APIClient.getWalletInstance()
                .processRegistration
                        (
                                fName, lName, email, password, countryCode, customersTelephone, addressStreet, addressCityOrTown, addressDistrict
                        );

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, retrofit2.Response<UserData> response) {

                dialogLoader.hideProgressDialog();

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        // Finish SignUpActivity to goto the LoginActivity
                        finish();
                        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);

                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        // Get the Error Message from Response
                        String message = response.body().getMessage();
                        Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT).show();

                    } else {
                        // Unable to get Success status
                        Toast.makeText(com.cabral.emaishapay.activities.SignUp.this, getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // Show the Error Message
                    String Str = response.message();
                    Toast.makeText(com.cabral.emaishapay.activities.SignUp.this, response.message(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                String Str = "" + t;
                Toast.makeText(com.cabral.emaishapay.activities.SignUp.this, "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();

            }
        });
    }

    //*********** Validate SignUp Form Inputs ********//

    private boolean validateForm() {
        if (!ValidateInputs.isValidName(firstName.getText().toString().trim())) {
            firstName.setError(getString(R.string.invalid_first_name));
            return false;
        } else if (!ValidateInputs.isValidName(lastName.getText().toString().trim())) {
            lastName.setError(getString(R.string.invalid_last_name));
            return false;
        } else if (!ValidateInputs.isValidNumber(phoneNumber.getText().toString().trim())) {
            phoneNumber.setError(getString(R.string.invalid_contact));
            return false;
        } else if (district.getText().toString().equals("District")) {
            Toast.makeText(this, "Please select District", Toast.LENGTH_SHORT).show();
            return false;
        } else if (subCounty.getText().toString().equals("Sub County")) {
            Toast.makeText(this, "Please select Sub County", Toast.LENGTH_SHORT).show();
            return false;
        } else if (village.getText().toString().equals("Village")) {
            Toast.makeText(this, "Please select Village", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!ValidateInputs.isValidEmail(user_email.getText().toString().trim())) {
            user_email.setError(getString(R.string.invalid_email));
            return false;
        } else if (userPassword.getText().toString().trim().length() < 6) {
            userPassword.setError(getString(R.string.invalid_password_length));
            return false;
        } else if (!ValidateInputs.isValidPassword(userPassword.getText().toString().trim())) {
            userPassword.setError(getString(R.string.invalid_password));
            return false;
        } else if (!ValidateInputs.isPasswordMatching(userPassword.getText().toString(), userConfirmPassword.getText().toString())) {
            userPassword.setError("Password does not match");
            userConfirmPassword.setError("Password does not match");
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

