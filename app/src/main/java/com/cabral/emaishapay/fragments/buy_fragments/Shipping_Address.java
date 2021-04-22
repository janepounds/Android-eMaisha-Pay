package com.cabral.emaishapay.fragments.buy_fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletBuySellActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.models.address_model.AddressData;
import com.cabral.emaishapay.models.address_model.AddressDetails;
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient;
import com.cabral.emaishapay.utils.ValidateInputs;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class Shipping_Address extends Fragment implements GoogleApiClient.OnConnectionFailedListener , OnMapReadyCallback {

    private final My_Addresses parentFrag;
    private String ADDRESS_ID;
    View rootView;
    Boolean isUpdate = false;
    String customerID, defaultAddressID, customerPhone;
    int selectedZoneID, selectedCountryID;
    TextView proceed_checkout_btn;
    LinearLayout save_address_layout;
    EditText input_name,  input_phone;
    DialogLoader dialogLoader;

    Toolbar toolbar;

    //LocationPicker.
    /*Edited  28-Dec-18*/
    int PLACE_PICKER_REQUEST = 1463;
    String latitude, longitude;
    SharedPreferences pref;


    static String deliveryCost;
    My_Cart my_cart;


    private static final String TAG ="Maps Error";
    public static GoogleMap map;
    private CameraPosition cameraPosition;
    // The entry point to the Places API.
    private PlacesClient placesClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(0.347596, 32.582520);//kampala
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    public static Location lastKnownLocation;
    // [START maps_current_place_state_keys]
    public static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    // [END maps_current_place_state_keys]
    private LatLng mCenterLatLong;



    public Shipping_Address(My_Cart my_cart,My_Addresses parentFrag) {
        this.my_cart = my_cart;
        this.parentFrag = parentFrag;
    }

    public Shipping_Address(My_Addresses parentFrag) {
        this.parentFrag = parentFrag;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.buy_inputs_address, container, false);

        toolbar = rootView.findViewById(R.id.add_address_Toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Add New Address");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // noInternetDialog.show();
        WalletBuySellActivity.bottomNavigationView.setVisibility(View.GONE);
        if (getArguments() != null) {
            if (getArguments().containsKey("isUpdate")) {
                isUpdate = getArguments().getBoolean("isUpdate", false);
            }
        }


        /*Edited  05/02/2021*/
        if (!Places.isInitialized()) {
            Places.initialize(getContext(), getString(R.string.place_picker_id));
        }

        // Set the Title of Toolbar
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(getString(R.string.shipping_address));

        // Get the customersID and defaultAddressID from SharedPreferences

        pref = requireContext().getSharedPreferences("UserInfo", requireContext().MODE_PRIVATE);
        customerID = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());


        defaultAddressID = pref.getString("userDefaultAddressID", "");
        customerPhone = pref.getString("userTelephone", "");


        // Binding Layout Views
        input_name = rootView.findViewById(R.id.name);
        input_phone = rootView.findViewById(R.id.contact);

        proceed_checkout_btn = rootView.findViewById(R.id.save_address_btn);

        save_address_layout= rootView.findViewById(R.id.save_address_layout);
        // Set the text of Button
        //proceed_checkout_btn.setText(getContext().getString(R.string.next));

        dialogLoader = new DialogLoader(getContext());


        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // If an existing Address is being Edited
        if (isUpdate) {
            // Get the Shipping AddressDetails from AppContext that is being Edited
            AddressDetails shippingAddress = ((EmaishaPayApp) getContext().getApplicationContext()).getShippingAddress();

            // Set the Address details
            this.ADDRESS_ID=shippingAddress.getAddressId()+"";
            selectedZoneID = shippingAddress.getZoneId();
            selectedCountryID = shippingAddress.getCountriesId();
            input_name.setText(shippingAddress.getLastname()!=null? shippingAddress.getFirstname()+" "+shippingAddress.getLastname() : shippingAddress.getFirstname());
            input_phone.setText(shippingAddress.getPhone());

            mCenterLatLong= new LatLng(shippingAddress.getLatitude(),shippingAddress.getLongitude());

        }
        else {
            // Request All Addresses of the User
            input_phone.setText(customerPhone);
        }

        // [START_EXCLUDE silent]
        // Construct a PlacesClient
        Places.initialize(getContext(), getString(R.string.maps_api_key));
        placesClient = Places.createClient(getContext());

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        // [START maps_current_place_map_fragment]
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        //MapView mapView = (MapView) rootView.findViewById(R.id.map);

        mapFragment.getMapAsync(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
        autocompleteFragment.setCountries("UG");
        autocompleteFragment.setHint(getString(R.string.searchShippingAddress));
        // ((Button)autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_button)).setTextColor(getResources().getColor(R.color.colorWhite));
//        ((EditText)autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input)).setTextColor(getResources().getColor(R.color.colorWhite));
//        ((EditText)autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input)).setTextSize(10.5f);
//       ImageView searchIcon = (ImageView)((LinearLayout) autocompleteFragment.getView()).getChildAt(0);
     //   ImageView cancelIcon = (ImageView)((LinearLayout) autocompleteFragment.getView()).getChildAt(2);
      //  cancelIcon.setColorFilter(getResources().getColor(R.color.colorWhite));
      //  searchIcon.setColorFilter(getResources().getColor(R.color.colorWhite));
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                Log.w(TAG, "Place: " + place.getName() + ", " +place.getAddress() + ", " + place.getId());
                LatLng selectedLocation= place.getLatLng();
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(selectedLocation.latitude,
                                selectedLocation.longitude), DEFAULT_ZOOM));
                mCenterLatLong = selectedLocation;
                WalletBuySellActivity.bottomNavigationView.setVisibility(View.GONE);
            }


            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.e(TAG, "An error occurred: " + status);

                WalletBuySellActivity.bottomNavigationView.setVisibility(View.GONE);
            }
        });

        proceed_checkout_btn.setOnClickListener(v -> {
            processCheckout(v);
        });
        save_address_layout.setOnClickListener(v -> {
            processCheckout(v);
        });

        return rootView;
    }

    private void processCheckout(View v) {
        // Validate Address Form Inputs
        boolean isValidData = validateAddressForm();
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        if(mCenterLatLong==null && map!=null){
            mCenterLatLong=map.getCameraPosition().target;
        }

        try {
            if(mCenterLatLong!=null)
                addresses = geocoder.getFromLocation(mCenterLatLong.latitude, mCenterLatLong.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }


        if (isValidData && addresses!=null ) {
            dialogLoader.showProgressDialog();

            final String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            final String city = addresses.get(0).getLocality();
            //String state = addresses.get(0).getAdminArea();
            //String country = addresses.get(0).getCountryName();
            final String postalCode = "UG 102";
            final String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
            //final String streetname = addresses.get(0).getThoroughfare()+", "+addresses.get(0).getSubThoroughfare();

            // New Instance of AddressDetails
            AddressDetails shippingAddress = new AddressDetails();
            String[] names = input_name.getText().toString().trim().split(" ");

            shippingAddress.setFirstname(names[0]);
            shippingAddress.setLastname(JoinStrings(Arrays.copyOfRange(names, 1, names.length)));
            shippingAddress.setCountryName("Uganda");
            shippingAddress.setZoneName(knownName);
            shippingAddress.setCity(city);
            shippingAddress.setStreet(address);
            shippingAddress.setPostcode(postalCode);
            shippingAddress.setPhone(input_phone.getText().toString().trim());
            shippingAddress.setZoneId(selectedZoneID);
            shippingAddress.setCountriesId(selectedCountryID);
            shippingAddress.setLatitude(mCenterLatLong.latitude);
            shippingAddress.setLongitude(mCenterLatLong.longitude);
            shippingAddress.setDelivery_cost(deliveryCost);
            shippingAddress.setPacking_charge_tax("" + ConstantValues.PACKING_CHARGE);

            // Save the AddressDetails
            ((EmaishaPayApp) getContext().getApplicationContext()).setShippingAddress(shippingAddress);

            // Check if an Address is being Edited
            if (isUpdate) {
                // Navigate to CheckoutFinal Fragment
                updateUserAddress(ADDRESS_ID,shippingAddress);
                //((MainActivity) getContext()).getSupportFragmentManager().popBackStack();
            }
            else {
                addUserAddress(shippingAddress,v);

            }
        }
    }


    //*********** Validate Address Form Inputs ********//

    private boolean validateAddressForm() {
        if (!ValidateInputs.isValidName(input_name.getText().toString().trim())) {
            input_name.setError(getString(R.string.invalid_first_name));
            return false;
        } else if (!ValidateInputs.isValidInput(input_phone.getText().toString().trim())) {
            input_phone.setError(getString(R.string.contact_format));
            return false;
        }

        else {
            return true;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                String lat = data.getStringExtra("lat");
                String lon = data.getStringExtra("lon");
                latitude = lat;
                longitude = lon;

            }
            else {
                Log.e("ShippingAddress","Error in data fetching");
            }

            WalletBuySellActivity.bottomNavigationView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), connectionResult.getErrorMessage() + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        //mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        //   mGoogleApiClient.disconnect();
    }

    @Override
    public void onPause() {
        //  mGoogleApiClient.stopAutoManage(getActivity());
        //   mGoogleApiClient.disconnect();
        super.onPause();
    }


    /**
     * Saves the state of the map when the activity is paused.
     */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }
    // [END maps_current_place_on_save_instance_state]


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

        if(isUpdate && mCenterLatLong!=null )
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCenterLatLong.latitude,
                            mCenterLatLong.longitude), DEFAULT_ZOOM));
        else // Get the current location of the device and set the position of the map.
            getDeviceLocation();

    }
    // [END maps_current_place_on_map_ready]

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    // [START maps_current_place_get_device_location]
    private void getDeviceLocation() {
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
    // [END maps_current_place_get_device_location]

    /**
     * Prompts the user for permission to use the device location.
     */
    // [START maps_current_place_location_permission]
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
    // [END maps_current_place_location_permission]

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

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

    private String JoinStrings( String stringArray[]){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < stringArray.length; i++) {
            sb.append(stringArray[i]);
        }

        return sb.toString();
    }


    //*********** Proceed the Request of New Address ********//

    public void addUserAddress(AddressDetails addressDetails, View v) {


        final String customers_default_address_id = getActivity().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userDefaultAddressID", "");
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;

        String[] names = input_name.getText().toString().trim().split(" ");

        Call<AddressData> call = BuyInputsAPIClient.getInstance()
                .addUserAddress
                        (       access_token,
                                customerID,
                                names[0],
                                JoinStrings(Arrays.copyOfRange(names, 1, names.length)),
                                //input_contact.getText().toString().trim(),
                                addressDetails.getStreet(),
                                addressDetails.getPostcode(),
                                addressDetails.getCity(),
                                "219",
                                addressDetails.getLatitude()+"",
                                addressDetails.getLongitude()+"",
                                addressDetails.getPhone()+"",
                                customers_default_address_id

                        );


        call.enqueue(new Callback<AddressData>() {
            @Override
            public void onResponse(Call<AddressData> call, Response<AddressData> response) {

                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        // Address has been added to User's Addresses
                        // Navigate to Addresses fragment
                        ((WalletBuySellActivity) getContext()).getSupportFragmentManager().popBackStack();
                        if( my_cart!=null){

                            // Navigate to Shipping_Methods Fragment
                            Fragment fragment = new Nearby_Merchants(my_cart);
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().add(R.id.nav_host_fragment2, fragment)
                                    .addToBackStack(null).commit();
                        }else
                            parentFrag.RequestAllAddresses(v);
                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        Snackbar.make(v, response.body().getMessage(), Snackbar.LENGTH_LONG).show();

                    }
                    else {
                        // Unable to get Success status
                        Snackbar.make(v, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
                dialogLoader.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<AddressData> call, Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
                dialogLoader.hideProgressDialog();
            }
        });
    }



    //*********** Proceed the Request of Update Address ********//

    public void updateUserAddress(String addressID, AddressDetails addressDetails) {

        final String customers_default_address_id = getActivity().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userDefaultAddressID", "");
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;

        String[] names = input_name.getText().toString().trim().split(" ");
        Call<AddressData> call = BuyInputsAPIClient.getInstance()
                .updateUserAddress
                        (       access_token,
                                customerID,
                                addressID,
                                names[0],
                                JoinStrings(Arrays.copyOfRange(names, 1, names.length)),
                                addressDetails.getStreet(),
                                addressDetails.getPostcode(),
                                addressDetails.getCity(),
                                "219",
                                addressDetails.getLatitude()+"",
                                addressDetails.getLongitude()+"",
                                addressDetails.getPhone()+"",
                                customers_default_address_id
                        );

        call.enqueue(new Callback<AddressData>() {
            @Override
            public void onResponse(Call<AddressData> call, Response<AddressData> response) {

                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        // Address has been Edited
                        // Navigate to Addresses fragment
                        ((WalletBuySellActivity) getContext()).getSupportFragmentManager().popBackStack();
                        parentFrag.RequestAllAddresses(rootView);

                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        // Address has not been Edited
                        // Show the Message to the User
                        Toast.makeText(getContext(), ""+response.body().toString(), Toast.LENGTH_LONG).show();
                    }
                }
                dialogLoader.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<AddressData> call, Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
                dialogLoader.hideProgressDialog();
            }
        });
    }
}
