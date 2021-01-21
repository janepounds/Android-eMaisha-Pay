package com.cabral.emaishapay.adapters.buyInputsAdapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletBuySellActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.fragments.buy_fragments.My_Addresses;
import com.cabral.emaishapay.fragments.buy_fragments.Nearby_Merchants;
import com.cabral.emaishapay.fragments.buy_fragments.Shipping_Address;
import com.cabral.emaishapay.models.address_model.AddressDetails;
import com.cabral.emaishapay.utils.SharedPreferenceHelper;



import java.util.List;

/**
 * AddressListAdapter is the adapter class of RecyclerView holding List of Addresses in My_Addresses
 **/

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.MyViewHolder> {
    private static final String TAG = "AddressListAdapter";

    Context context;
    String customerID;
    List<AddressDetails> addressList;
    My_Addresses my_addresses;

    private int selectedPosition;

    // To keep track of Checked Radio Button
    private RadioButton lastChecked_RB;
    My_Addresses parentFrag;

    public AddressListAdapter(My_Addresses my_addresses, Context context, String customerID, int defaultAddressPosition, List<AddressDetails> addressList, My_Addresses parentFrag) {
        this.my_addresses = my_addresses;
        this.context = context;
        this.customerID = customerID;
        this.addressList = addressList;
        this.selectedPosition = defaultAddressPosition;
        this.parentFrag = parentFrag;
    }

    //********** Called to Inflate a Layout from XML and then return the Holder *********//

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // Inflate the custom layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.buy_inputs_card_addresses, parent, false);

        return new MyViewHolder(itemView);
    }

    //********** Called by RecyclerView to display the Data at the specified Position *********//

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // Get the data model based on Position
        final AddressDetails addressDetails = addressList.get(position);

        final String addressID = String.valueOf(addressDetails.getAddressId());

        holder.address_title.setText(addressDetails.getLastname() != null ? addressDetails.getFirstname() + " " + addressDetails.getLastname() : addressDetails.getFirstname());
        holder.address_details.setText(addressDetails.getStreet() + ", " + addressDetails.getCity() + ", " + addressDetails.getCountryName());

        SharedPreferenceHelper preferenceHelper = new SharedPreferenceHelper(context);
        if ((preferenceHelper.getDefaultLatitude() != null) && (preferenceHelper.getDefaultLongitude() != null)) {
            if ((preferenceHelper.getDefaultLatitude().equals(String.valueOf(addressDetails.getLatitude()))) && (preferenceHelper.getDefaultLongitude().equals(String.valueOf(addressDetails.getLongitude())))) {
                holder.makeDefault_rb.setChecked(true);
            }
        }

        Log.d(TAG, "onBindViewHolder: Latitude = " + addressDetails.getLatitude());
        Log.d(TAG, "onBindViewHolder: Longitude = " + addressDetails.getLongitude());

        // Check the Clicked RadioButton
        View.OnClickListener onSelectListener = view -> {
            preferenceHelper.setDefaultAddress(String.valueOf(addressDetails.getLongitude()), String.valueOf(addressDetails.getLatitude()));
            holder.makeDefault_rb.setChecked(true);
            Log.d(TAG, "onBindViewHolder: DefaultLatitude = " + preferenceHelper.getDefaultLatitude());
            Log.d(TAG, "onBindViewHolder: DefaultLongitude = " + preferenceHelper.getDefaultLongitude());

            if (parentFrag.my_cart != null) {
                ((EmaishaPayApp) context.getApplicationContext()).setShippingAddress(addressDetails);
                Fragment fragment = new Nearby_Merchants(parentFrag.my_cart);
                FragmentManager fragmentManager = parentFrag.getFragmentManager();
                fragmentManager.beginTransaction().add(R.id.nav_host_fragment2, fragment)
                        .addToBackStack(context.getString(R.string.select_merchants_fragment)).commit();
            } else {
                // Request the Server to Change Default Address
                //buy_inputs_my__addresses.MakeAddressDefault(customerID, addressID, context, view);
            }
        };

        holder.cardview_perant.setOnClickListener(onSelectListener);
        holder.makeDefault_rb.setOnClickListener(onSelectListener);

        holder.delete_address.setOnClickListener(v -> my_addresses.DeleteAddress(customerID, addressID, context, holder.delete_address));

        // Edit relevant Address
        holder.edit_address.setOnClickListener(view -> {
            // Set the current Address Info to Bundle
            Bundle addressInfo = new Bundle();
            addressInfo.putBoolean("isUpdate", true);
            addressInfo.putString("addressID", addressID);
            addressInfo.putString("addressFirstname", addressDetails.getFirstname());
            addressInfo.putString("addressLastname", addressDetails.getLastname());
            addressInfo.putString("addressCountryName", addressDetails.getCountryName());
            addressInfo.putString("addressCountryID", "" + addressDetails.getCountriesId());
            addressInfo.putString("addressZoneName", addressDetails.getZoneName());
            addressInfo.putString("addressZoneID", "" + addressDetails.getZoneId());
            addressInfo.putString("addressState", addressDetails.getState());
            addressInfo.putString("addressCity", addressDetails.getCity());
            addressInfo.putString("addressStreet", addressDetails.getStreet());
            addressInfo.putString("addressPostCode", addressDetails.getPostcode());
            addressInfo.putString("addressPhone", addressDetails.getPhone());
            addressInfo.putString("addressLocation", addressDetails.getLatitude() + ", " + addressDetails.getLongitude());
            // Navigate to Add_Address Fragment with arguments to Edit Address

            ((EmaishaPayApp) context.getApplicationContext()).setShippingAddress(addressDetails);
            Fragment fragment = new Shipping_Address(null, parentFrag);
            fragment.setArguments(addressInfo);
            FragmentManager fragmentManager = ((WalletBuySellActivity)context).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.nav_host_fragment2, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(null).commit();
        });
    }

    //********** Returns the total number of items in the data set *********//

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    /********** Custom ViewHolder provides a direct reference to each of the Views within a Data_Item *********/

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        Button edit_address, delete_address;
        RadioButton makeDefault_rb;
        TextView address_title, address_details;
        CardView cardview_perant;

        public MyViewHolder(final View itemView) {
            super(itemView);

            cardview_perant = itemView.findViewById(R.id.cardview_perant);
            address_title = itemView.findViewById(R.id.address_title);
            address_details = itemView.findViewById(R.id.address_details);
            edit_address = itemView.findViewById(R.id.edit_address);
            delete_address = itemView.findViewById(R.id.delete_address);
            makeDefault_rb = itemView.findViewById(R.id.default_address_rb);
        }
    }
}

