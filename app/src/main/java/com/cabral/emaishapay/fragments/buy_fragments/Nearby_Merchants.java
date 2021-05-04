package com.cabral.emaishapay.fragments.buy_fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.buyInputsAdapters.MerchantsListAdapter;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.database.User_Cart_BuyInputsDB;
import com.cabral.emaishapay.models.address_model.AddressDetails;
import com.cabral.emaishapay.models.cart_model.CartProduct;
import com.cabral.emaishapay.models.merchants_model.MerchantData;
import com.cabral.emaishapay.models.merchants_model.MerchantDetails;
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.List;

import am.appwise.components.ni.NoInternetDialog;
import retrofit2.Call;
import retrofit2.Callback;


public class Nearby_Merchants extends Fragment {

    Toolbar toolbar;
    Boolean enable_back=true;
    View rootView;

    DialogLoader dialogLoader;
    User_Cart_BuyInputsDB user_cart_BuyInputs_db;
    RecyclerView merchants_recycler;

    List<CartProduct> checkoutItemsList;

    AddressDetails shippingAddress;

    MerchantsListAdapter merchantsListAdapter;

    List<MerchantDetails> merchantList = new ArrayList<>();
    
    // To keep track of Checked Radio Button
    private RadioButton lastChecked_RB = null;
    My_Cart my_cart;
    MerchantData merchantData;
    public Nearby_Merchants(Boolean enable_back){ this.enable_back = enable_back; }
    public Nearby_Merchants(My_Cart my_cart) {
        this.my_cart = my_cart;
    }

    public Nearby_Merchants() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.buy_inputs_nearbymerchants, container, false);

        toolbar = rootView.findViewById(R.id.toolbar_nearby_merchants);
        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
        setHasOptionsMenu(true);
        merchants_recycler=rootView.findViewById(R.id.merchants_list);

        dialogLoader = new DialogLoader(getContext());
        user_cart_BuyInputs_db = new User_Cart_BuyInputsDB();
        // Get checkoutItems from Local Databases User_Cart_DB
        checkoutItemsList = user_cart_BuyInputs_db.getCartItems();

        shippingAddress=((EmaishaPayApp) getContext().getApplicationContext()).getShippingAddress();


        if(shippingAddress!=null && this.merchantData==null )
            RequestMyNearbyMerchants(shippingAddress,checkoutItemsList);
        else
            addMerchantsToList(merchantData);


        // Set the Title of Toolbar
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.nearby_merchants));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(enable_back);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(enable_back);

        return rootView;
    }


    @Override
    public void onResume() {
        Log.e("DEBUG", "onResume of Fragment");
        if( this.merchantList==null){
            addMerchantsToList(merchantData);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e("DEBUG", "OnPause of Fragment");
        this.merchantList=null;
        super.onPause();
    }
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        ((DashboardActivity)getActivity()).clearBackStackInclusive( getString(R.string.select_merchants_fragment)); // tag (addToBackStack tag) should be the same which was used while transacting the F2 fragment
//    }


    //*********** Request User's Nearby Merchants from the Server ********//

    public void RequestMyNearbyMerchants(AddressDetails shippingAddress,List<CartProduct> checkoutItemsList) {
        ArrayList <String> product_names = new ArrayList <String>();
        for (CartProduct product:checkoutItemsList) {
            product_names.add(product.getCustomersBasketProduct().getProductsId()+"::"+product.getCustomersBasketProduct().getProductsName()+"//"+product.getCustomersBasketProduct().getSelectedProductsWeight()+"//"+product.getCustomersBasketProduct().getSelectedProductsWeightUnit()+"::"+product.getCustomersBasketProduct().getCustomersBasketQuantity());
        }
        dialogLoader.showProgressDialog();
         //Log.w( "Coordinates",shippingAddress.getLatitude()+" "+shippingAddress.getLongitude());
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String product_names_str = new Gson().toJson(product_names);
         //Log.w( "CartData",product_names_str);
        Call<MerchantData> call = BuyInputsAPIClient.getInstance()
                .getNearbyMerchants
                        (       access_token,
                                String.valueOf(shippingAddress.getLatitude()),String.valueOf( shippingAddress.getLongitude()),
                                product_names_str
                        );

        call.enqueue(new Callback<MerchantData>() {
            @Override
            public void onResponse(Call<MerchantData> call, retrofit2.Response<MerchantData> response) {

                String str = new Gson().toJson(response.body());

                 //Log.w("Response",str+"");
                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        // merchants have been returned. Add merchants to the merchantsList
                        if(response.body().getData().size()>0)
                            addMerchantsToList(response.body());
                        else
                            Snackbar.make(rootView, "Couldn't find Nearby Merchants!", Snackbar.LENGTH_LONG).show();

                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        //emptyRecord.setVisibility(View.VISIBLE);
                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_LONG).show();

                    }
                    else {
                        // Unable to get Success status
                        //emptyRecord.setVisibility(View.VISIBLE);
                        Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                     //Log.w("Response",response.message()+"");
                }
                dialogLoader.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<MerchantData> call, Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
                 //Log.w("Response",t.toString()+"");
                dialogLoader.hideProgressDialog();
            }
        });
    }

    //*********** Adds merchants returned from the Server to the merchantsList ********//

    public void addMerchantsToList(MerchantData merchantData) {
        // Add merchants to merchantsList from the List of OrderData
        if(merchantData==null)
            return;

        this.merchantList = merchantData.getData();

        for (int position=0; position< this.merchantList.size(); position++) {
            if(this.merchantList.get(position).getTotalOrderPrice()<=0){
                this.merchantList.remove(position);
            }
        }

        // Initialize the merchantsListAdapter for RecyclerView
        this.merchantsListAdapter = new MerchantsListAdapter(getContext(), merchantList,this.my_cart, getFragmentManager());
        // Set the Adapter and LayoutManager to the RecyclerView
        this.merchants_recycler.setAdapter(merchantsListAdapter);

        this.merchants_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        this.merchantsListAdapter.notifyDataSetChanged();
    }
    
    public RadioButton getLastChecked_RB() {
        return lastChecked_RB;
    }
    
    public void setLastChecked_RB(RadioButton lastChecked_RB) {
        this.lastChecked_RB = lastChecked_RB;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Hide Cart Icon in the Toolbar

//        MenuItem searchItem = menu.findItem(R.id.toolbar_ic_search);
        MenuItem cartItem = menu.findItem(R.id.ic_cart_item);

//        searchItem.setVisible(false);
        cartItem.setVisible(false);
    }


}


