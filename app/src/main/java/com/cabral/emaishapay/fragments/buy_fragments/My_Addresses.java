package com.cabral.emaishapay.fragments.buy_fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletBuySellActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.buyInputsAdapters.AddressListAdapter;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.database.DbHandlerSingleton;
import com.cabral.emaishapay.fragments.TokenAuthFragment;
import com.cabral.emaishapay.models.address_model.AddressData;
import com.cabral.emaishapay.models.address_model.AddressDetails;
import com.cabral.emaishapay.network.BuyInputsAPIClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import am.appwise.components.ni.NoInternetDialog;
import retrofit2.Call;
import retrofit2.Callback;

public class My_Addresses extends Fragment {
    private static final String TAG = "My_Addresses";
    public static My_Cart my_cart;
    View rootView;
    String customerID;
    String defaultAddressID;

    RecyclerView addresses_recycler;
    FloatingActionButton add_address_fab;
    LinearLayout emptyRecordLayout;
    AppCompatButton addAddressButton;

    DialogLoader dialogLoader;
    static AddressListAdapter addressListAdapter;
    Toolbar toolbar;
    Boolean enable_back=true;

    List<AddressDetails> addressesList = new ArrayList<>();

    private int defaultAddressPosition = -1;
    private DbHandlerSingleton dbHandler;


    public  My_Addresses(Boolean enable_back){ this.enable_back = enable_back; }
    public My_Addresses(My_Cart my_cart) {
        this.my_cart = my_cart;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.buy_inputs_my__addresses, container, false);
        setHasOptionsMenu(true);
        toolbar = rootView.findViewById(R.id.toolbar_addresses);
        // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
        //MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.actionAddresses));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(enable_back);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(enable_back);


        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
        // noInternetDialog.show();

        // Get the CustomerID and DefaultAddressID from SharedPreferences
        customerID = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
        defaultAddressID = this.getContext().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userDefaultAddressID", "");

        // Binding Layout Views
        addresses_recycler = rootView.findViewById(R.id.addresses_recycler);
        add_address_fab = rootView.findViewById(R.id.add_address_fab);
        emptyRecordLayout = rootView.findViewById(R.id.empty_record);
        addAddressButton = rootView.findViewById(R.id.continue_shopping_btn);

        dialogLoader = new DialogLoader(getContext());

        // Request for User's Addresses
        RequestAllAddresses(rootView);

        addAddressButton.setOnClickListener(v -> {
            // Navigate to Add_Address Fragment with arguments
            Fragment fragment = new Shipping_Address(my_cart, My_Addresses.this);
            Bundle args = new Bundle();
            args.putBoolean("isUpdate", false);
            fragment.setArguments(args);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            if (((WalletBuySellActivity) getActivity()).currentFragment != null)
                fragmentManager.beginTransaction()
                        .hide(((WalletBuySellActivity) getActivity()).currentFragment)
                        .add(R.id.nav_host_fragment2, fragment)
                        .addToBackStack(null).commit();
            else
                fragmentManager.beginTransaction()
                        .add(R.id.nav_host_fragment2, fragment)
                        .addToBackStack(null).commit();
            WalletBuySellActivity.currentFragment=fragment;
        });

        // Handle Click event of add_address_fab FAB
        add_address_fab.setOnClickListener(v -> {
            // Navigate to Add_Address Fragment with arguments
            Fragment fragment = new Shipping_Address(my_cart, My_Addresses.this);
            Bundle args = new Bundle();
            args.putBoolean("isUpdate", false);
            fragment.setArguments(args);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//            if (((WalletBuySellActivity) getActivity()).currentFragment != null)
//                fragmentManager.beginTransaction()
//                        .hide(((WalletBuySellActivity) getActivity()).currentFragment)
//                        .add(R.id.nav_host_fragment2, fragment)
//                        .addToBackStack(null).commit();
//            else
                fragmentManager.beginTransaction()
                        .add(R.id.nav_host_fragment2, fragment)
                        .addToBackStack(null).commit();
        });

        return rootView;
    }

    //*********** Adds Addresses returned from the Server to the AddressesList ********//

    private void addAddresses(AddressData addressData) {

        // Add Addresses to addressesList from the List of AddressData
        addressesList = addressData.getData();

        if (addressesList.isEmpty()) {
            emptyRecordLayout.setVisibility(View.VISIBLE);
        } else {
            emptyRecordLayout.setVisibility(View.GONE);
        }

        for (int i = 0; i < addressesList.size(); i++) {
            if (addressesList.get(i).getAddressId() == addressesList.get(i).getDefaultAddress()) {
                defaultAddressPosition = i;

                //save a copy in the local database
                dbHandler =  DbHandlerSingleton.getHandlerInstance(getContext());
                dbHandler.insertDefaultAddress( customerID,
                        addressesList.get(i).getFirstname(),
                        addressesList.get(i).getLastname(),
                        //input_contact.getText().toString().trim(),
                        addressesList.get(i).getStreet(),
                        addressesList.get(i).getPostcode(),
                        addressesList.get(i).getCity(),
                        addressesList.get(i).getCountryName(),
                        addressesList.get(i).getLatitude()+"",
                        addressesList.get(i).getLongitude()+"",
                        addressesList.get(i).getContact(),
                        addressesList.get(i).getAddressId()+"");




            }
        }

        if (addressesList.size() == 1) {
            MakeAddressDefault(customerID, String.valueOf(addressesList.get(0).getAddressId()), getContext(), rootView);
        }

        // Initialize the AddressListAdapter for RecyclerView
        addressListAdapter = new AddressListAdapter(My_Addresses.this, getContext(), customerID, defaultAddressPosition, addressesList, My_Addresses.this);

        // Set the Adapter and LayoutManager to the RecyclerView
        addresses_recycler.setAdapter(addressListAdapter);
        addresses_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        addressListAdapter.notifyDataSetChanged();
    }

    //*********** Request User's all Addresses from the Server ********//

    public void RequestAllAddresses(View rootView) {

        dialogLoader.showProgressDialog();
        String access_token = TokenAuthFragment.WALLET_ACCESS_TOKEN;

        Call<AddressData> call = BuyInputsAPIClient.getInstance()
                .getAllAddress
                        (       access_token,
                                customerID
                        );

        call.enqueue(new Callback<AddressData>() {
            @Override
            public void onResponse(Call<AddressData> call, retrofit2.Response<AddressData> response) {

                dialogLoader.hideProgressDialog();

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        // Addresses have been returned. Add Addresses to the addressesList
                        addAddresses(response.body());

                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        // Addresses haven't been returned. Show the Message to the User
                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_SHORT).show();

                    } else {
                        // Unable to get Success status
                        Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AddressData> call, Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    //*********** Request to Delete the given User's Address ********//

    public void DeleteAddress(final String customerID, final String addressID, final Context context, final View view) {
        String access_token = TokenAuthFragment.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();

        Call<AddressData> call = BuyInputsAPIClient.getInstance()
                .deleteUserAddress
                        (access_token,
                                customerID,
                                addressID
                        );

        call.enqueue(new Callback<AddressData>() {
            @Override
            public void onResponse(Call<AddressData> call, retrofit2.Response<AddressData> response) {

                if (response.isSuccessful()) {

                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        // Address has been Deleted. Show the Message to the User
                        Snackbar.make(view, response.body().getMessage(), Snackbar.LENGTH_SHORT).show();
                        RequestAllAddresses(view);
                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        Snackbar.make(view, response.body().getMessage(), Snackbar.LENGTH_LONG).show();

                    } else {
                        // Unable to get Success status
                        Snackbar.make(view, context.getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AddressData> call, Throwable t) {
                Toast.makeText(context, "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    //*********** Request for Changing the Address to User's Default Address ********//

    public void MakeAddressDefault(final String customerID, final String addressID, final Context context, final View view) {

        final DialogLoader dialogLoader = new DialogLoader(context);
        dialogLoader.showProgressDialog();
        String access_token = TokenAuthFragment.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();

        Call<AddressData> call = BuyInputsAPIClient.getInstance()
                .updateDefaultAddress
                        (       access_token,
                                customerID,
                                addressID
                        );

        call.enqueue(new Callback<AddressData>() {
            @Override
            public void onResponse(Call<AddressData> call, retrofit2.Response<AddressData> response) {

                dialogLoader.hideProgressDialog();

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        // Notify that items from specified position have changed.

                        // Change the value of userDefaultAddressID in the SharedPreferences
                        SharedPreferences.Editor editor = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE).edit();
                        editor.putString("userDefaultAddressID", addressID);
                        editor.apply();

                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        Snackbar.make(view, response.body().getMessage(), Snackbar.LENGTH_LONG).show();

                    } else {
                        // Unable to get Success status
                        Snackbar.make(view, context.getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AddressData> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(context, "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Hide Cart Icon in the Toolbar

//        MenuItem searchItem = menu.findItem(R.id.toolbar_ic_search);
        MenuItem cartItem = menu.findItem(R.id.ic_cart_item);

//        searchItem.setVisible(false);
        cartItem.setVisible(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Request for User's Addresses
        RequestAllAddresses(rootView);
    }
}

