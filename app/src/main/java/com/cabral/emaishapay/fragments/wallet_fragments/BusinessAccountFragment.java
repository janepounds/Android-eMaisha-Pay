package com.cabral.emaishapay.fragments.wallet_fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.databinding.FragmentBusinessAccountBinding;
import com.cabral.emaishapay.models.AccountResponse;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class BusinessAccountFragment extends Fragment implements  OnMapReadyCallback  {
    private FragmentBusinessAccountBinding binding;
    private String role;
    private String encodedIdFront;
    private String encodedIdBack;
    private ImageView imageView;
    private String encodedIdreg_cert;
    private String encodedIdtradelicense;
    private ProgressDialog progressDialog;


    private static final String TAG ="Maps Error";
    public static GoogleMap map;
    private CameraPosition cameraPosition;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(0.347596, 32.582520);//kampala
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private static int AUTOCOMPLETE_REQUEST_CODE=12;

    public static Location lastKnownLocation;
    // [START maps_current_place_state_keys]
    public static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private LatLng mCenterLatLong;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_business_account, container, false);
        WalletHomeActivity.bottomNavigationView.setVisibility(View.GONE);
        WalletHomeActivity.scanCoordinatorLayout.setVisibility(View.GONE);
        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.toolbar);
        //binding.toolbar.setTitle("Merchant Details");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        initializeViews();
        /*Edited  05/02/2021*/
        if (!Places.isInitialized()) {
            Places.initialize(getContext(), getString(R.string.maps_api_key));
        }
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_fragment);
        //MapView mapView = (MapView) rootView.findViewById(R.id.map);

        mapFragment.getMapAsync(this);

//        AutocompleteSessionToken autocompleteSessionToken; autocompleteSessionToken= AutocompleteSessionToken.newInstance();
//        PlacesClient placesClient;
//        placesClient= Places.createClient(getActivity().getApplicationContext());
//        mAdapter = new PlaceAutocompleteAdapter(getContext(), placesClient,autocompleteSessionToken);
//        autoCompleteTextView=binding.shopLocation;

        binding.shopLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .setHint(getString(R.string.search_shop_location)).setCountry("UG") //UGANDA
                        .build(getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });


        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    public void initializeViews(){
        if(getArguments()!=null) {

            if (getArguments().getString(getString(R.string.role_master_agent))!=null) {
                binding.toolbar.setTitle(getArguments().getString("Agent"));
                role = "AGENT";

            }else if(getArguments().getString("Merchant")!=null){
                binding.toolbar.setTitle(getArguments().getString("Merchant"));
                role = "MERCHANT";

            }


        }else{
            role="DEFAULT";
        }
        //set business info
        String business_name = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_BUSINESS_NAME,getContext());
        String business_location =WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_BUSINESS_LOCATION,getContext());
        String reg_no =WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_REG_NO,getContext());
        String license_no =WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_LICENSE_NUMBER,getContext());
        String reg_cert =WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_REG_CERTIFICATE,getContext());
        String trade_license =WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_TRADE_LICENSE,getContext());
        String id_front = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_FRONT,getContext());
        String id_back = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_ACCOUNT_ID_BACK,getContext());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.add_default_image)
                .error(R.drawable.add_default_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);
        if(business_name!=null){

            binding.businessName.setText(business_name);
        } if(reg_no!=null){
            binding.registrationNumber.setText(reg_no);
        } if(business_location!=null){
            binding.shopLocation.setText(business_location);
        } if(trade_license!=null){
            Glide.with(requireContext()).load(ConstantValues.WALLET_DOMAIN+trade_license).apply(options).into(binding.tradeLicense);
//            //encode trade lisence
//            Bitmap imageBitmap1;
//
//            imageBitmap1 = BitmapFactory.decodeFile(ConstantValues.WALLET_DOMAIN+trade_license);
//            ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
//            imageBitmap1.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream1);
//            byte[] c = byteArrayOutputStream1.toByteArray();
//            encodedIdtradelicense = Base64.encodeToString(c, Base64.DEFAULT);
        } if(reg_cert!=null){
            Glide.with(requireContext()).load(ConstantValues.WALLET_DOMAIN+reg_cert).apply(options).into(binding.registrationCertificate);
            //encode registration certificate
//            Bitmap imageBitmap;
//
//            imageBitmap = BitmapFactory.decodeFile(ConstantValues.WALLET_DOMAIN+reg_cert);
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//            byte[] b = byteArrayOutputStream.toByteArray();
//            encodedIdreg_cert = Base64.encodeToString(b, Base64.DEFAULT);
        } if(id_front!=null){

            Glide.with(requireContext()).load(ConstantValues.WALLET_DOMAIN+id_front).apply(options).into(binding.imgNidFront);
            //encode id front
//            Bitmap imageBitmap2;
//
//            imageBitmap2 = BitmapFactory.decodeFile(ConstantValues.WALLET_DOMAIN+id_front);
//            ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
//            imageBitmap2.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream2);
//            byte[] d = byteArrayOutputStream2.toByteArray();
//            encodedIdFront = Base64.encodeToString(d, Base64.DEFAULT);
        } if(id_back!=null){

            Glide.with(requireContext()).load(ConstantValues.WALLET_DOMAIN+id_back).apply(options).into(binding.imgNidBack);
            //encode id back
//            Bitmap imageBitmap3;
//
//            imageBitmap3 = BitmapFactory.decodeFile(ConstantValues.WALLET_DOMAIN+id_back);
//            ByteArrayOutputStream byteArrayOutputStream3 = new ByteArrayOutputStream();
//            imageBitmap3.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream3);
//            byte[] e = byteArrayOutputStream3.toByteArray();
//            encodedIdBack = Base64.encodeToString(e, Base64.DEFAULT);
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
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());

        //**************RETROFIT IMPLEMENTATION******************//
        Call<AccountResponse> call = APIClient.getWalletInstance(getContext())
                .applyForBusiness(access_token,
                        role,
                        user_Id,
                        business_name,
                        registration_no,
                        encodedIdreg_cert,
                        encodedIdtradelicense,
                        proprietor_name,
                        proprietor_nin,
                        encodedIdFront,
                        encodedIdBack,
                        mCenterLatLong.latitude,
                        mCenterLatLong.longitude,
                        request_id,
                        category,
                        "applyForBusinessAccount");

        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                if(response.isSuccessful()){
                    String message = response.body().getMessage();

                    //call success dialog
                    final Dialog dialog = new Dialog(getContext());
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
                            Intent goToWallet = new Intent(getContext(), WalletHomeActivity.class);
                            startActivity(goToWallet);
                        }
                    });
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();

                }else{
                    Toast.makeText(getContext(),response.message(),Toast.LENGTH_LONG);
                }
                progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_LONG);

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

        if (resultCode == RESULT_OK && requestCode == 1) {
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
        }else if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.w(TAG, "Place: " + place.getName() + ", " +place.getAddress() + ", " + place.getId());
                binding.shopLocation.setText(place.getName());
                LatLng selectedLocation= place.getLatLng();
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(selectedLocation.latitude,
                                selectedLocation.longitude), DEFAULT_ZOOM));
                mCenterLatLong = selectedLocation;
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getContext(), "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public boolean validateEntries(){

        if(TextUtils.isEmpty(binding.businessName.getText())){
            binding.businessName.setError("Business name required");
            return false;
        }else if(binding.businessName.getText().length()<3){
            binding.businessName.setError("Business name invalid");
            return false;
        }else if(TextUtils.isEmpty(binding.registrationNumber.getText())){
            binding.registrationNumber.setError("Registration Number required");
            return false;
        }else if(TextUtils.isEmpty(binding.proprietorName.getText())){
            binding.proprietorName.setError("Proprietor Name required");
            return false;
        }else if(TextUtils.isEmpty(binding.proprietorNin.getText())){
            binding.proprietorNin.setError("Proprietor Name required");
            return false;
        }
        else if(TextUtils.isEmpty(binding.proprietorNin.getText())){
            binding.proprietorNin.setError("Proprietor Name required");
            return false;
        }
        else if(TextUtils.isEmpty(encodedIdreg_cert)){
            Toast.makeText(getContext(),"registration Certificate required",Toast.LENGTH_LONG).show();
            return false;
        }
//        else if(TextUtils.isEmpty(encodedIdFront)){
//            Toast.makeText(getContext(),"registration Certificate required",Toast.LENGTH_LONG).show();
//            return false;
//        }

        return true;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;//pass map to global event listeners

        final GoogleMap mGoogleMap=this.map;
        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                cameraPosition= mGoogleMap.getCameraPosition();
                Log.w("Camera postion change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;

                //mGoogleMap.clear();

            }
        });

        // Prompt the user for permission.
        getLocationPermission();
        // [END_EXCLUDE]

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        if( mCenterLatLong!=null )
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCenterLatLong.latitude,
                            mCenterLatLong.longitude), DEFAULT_ZOOM));
        else // Get the current location of the device and set the position of the map.
            getDeviceLocation();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(false);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}