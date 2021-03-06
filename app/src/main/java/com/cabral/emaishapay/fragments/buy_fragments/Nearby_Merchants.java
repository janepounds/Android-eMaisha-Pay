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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.buyInputsAdapters.MerchantsListAdapter;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.database.User_Cart_BuyInputsDB;
import com.cabral.emaishapay.databinding.BuyInputsNearbymerchantsBinding;
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

    Boolean enable_back=true;
    DialogLoader dialogLoader;
    User_Cart_BuyInputsDB user_cart_BuyInputs_db;
    List<CartProduct> checkoutItemsList;

    AddressDetails shippingAddress;

    MerchantsListAdapter merchantsListAdapter;
    List<MerchantDetails> merchantList = new ArrayList<>();
    BuyInputsNearbymerchantsBinding binding;
    // To keep track of Checked Radio Button
    private RadioButton lastChecked_RB = null;
    My_Cart my_cart;
    MerchantData merchantData;
    public Nearby_Merchants(Boolean enable_back){ this.enable_back = enable_back; }

    public Nearby_Merchants() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.buy_inputs_nearbymerchants, container, false);


        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
        setHasOptionsMenu(true);

        dialogLoader = new DialogLoader(getContext());
        user_cart_BuyInputs_db = new User_Cart_BuyInputsDB();
        // Get checkoutItems from Local Databases User_Cart_DB
        checkoutItemsList = user_cart_BuyInputs_db.getCartItems();

        shippingAddress=((EmaishaPayApp) getContext().getApplicationContext()).getShippingAddress();
        if(getArguments()!=null){
            my_cart = (My_Cart) getArguments().getSerializable("my_cart");
        }

        if(shippingAddress!=null && this.merchantData==null )
            RequestMyNearbyMerchants(shippingAddress,checkoutItemsList);
        else
            addMerchantsToList(merchantData);


        // Set the Title of Toolbar
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarNearbyMerchants);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.nearby_merchants));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(enable_back);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(enable_back);

        return binding.getRoot();
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

                 //Log.w("Response",str+"");
                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        // merchants have been returned. Add merchants to the merchantsList
                        if(response.body().getData().size()>0){
                            addMerchantsToList(response.body());
                        }
                        else {

                            binding.noNearbyMerchant.setVisibility(View.VISIBLE);
                            Snackbar.make(getView(), "Couldn't find Nearby Merchants!", Snackbar.LENGTH_LONG).show();
                        }

                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        //emptyRecord.setVisibility(View.VISIBLE);
                        binding.noNearbyMerchant.setVisibility(View.VISIBLE);
                        Snackbar.make(getView(), response.body().getMessage(), Snackbar.LENGTH_LONG).show();

                    }
                    else {
                        // Unable to get Success status
                        //emptyRecord.setVisibility(View.VISIBLE);
                        binding.noNearbyMerchant.setVisibility(View.VISIBLE);
                        Snackbar.make(getView(), getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
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

        if(this.merchantList.size()>0){
            binding.noNearbyMerchant.setVisibility(View.GONE);
        }

        // Initialize the merchantsListAdapter for RecyclerView
        this.merchantsListAdapter = new MerchantsListAdapter(getContext(), merchantList,this.my_cart, getActivity().getSupportFragmentManager());
        // Set the Adapter and LayoutManager to the RecyclerView
        this.binding.merchantsList.setAdapter(merchantsListAdapter);

        this.binding.merchantsList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

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


