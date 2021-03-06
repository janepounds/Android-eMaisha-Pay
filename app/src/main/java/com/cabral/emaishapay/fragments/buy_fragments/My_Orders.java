package com.cabral.emaishapay.fragments.buy_fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.buyInputsAdapters.OrdersListAdapter;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.databinding.BuyInputsOrdersBinding;
import com.cabral.emaishapay.models.order_model.OrderData;
import com.cabral.emaishapay.models.order_model.OrderDetails;
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.List;

import am.appwise.components.ni.NoInternetDialog;
import retrofit2.Call;
import retrofit2.Callback;

public class My_Orders extends Fragment {

    String customerID;
    DialogLoader dialogLoader;
    OrdersListAdapter ordersListAdapter;
    private Context context;
    Boolean enable_back=true;
    BuyInputsOrdersBinding binding;
    List<OrderDetails> ordersList = new ArrayList<>();

    public  My_Orders(Boolean enable_back){
        this.enable_back=enable_back;
    }
    public  My_Orders(){
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            RequestMyOrders();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.buy_inputs_orders, container, false);
        setHasOptionsMenu(true);
        //MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.toolbarOrders);
        binding.toolbarOrders.setTitle(getString(R.string.actionOrders));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
        //noInternetDialog.show();

        // Get the CustomerID from SharedPreferences
        customerID = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
        
        Log.d("customerID",customerID+" "+ ConstantValues.LANGUAGE_ID+" "+ConstantValues.CURRENCY_CODE);


        dialogLoader = new DialogLoader(getContext());

        // Hide some of the Views
        binding.emptyRecord.setVisibility(View.GONE);



        // Request for User's Orders
        RequestMyOrders();

        binding.continueShoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isSubFragment", false);

                // Navigate to Products Fragment
                Fragment fragment = new WalletBuyFragment(getContext(),getParentFragmentManager());
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment2, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionCart)).commit();
            }
        });

        return binding.getRoot();
    }



    //*********** Adds Orders returned from the Server to the OrdersList ********//

    private void addOrders(OrderData orderData) {

        // Add Orders to ordersList from the List of OrderData
        ordersList = orderData.getData();


        // Initialize the OrdersListAdapter for RecyclerView
        ordersListAdapter = new OrdersListAdapter(getContext(), customerID, ordersList,this);
        // Set the Adapter and LayoutManager to the RecyclerView
        binding.ordersRecycler.setAdapter(ordersListAdapter);
        binding.ordersRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        
        ordersListAdapter.notifyDataSetChanged();
    }


    //*********** Request User's Orders from the Server ********//

    public void RequestMyOrders() {

        dialogLoader.showProgressDialog();
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;

        Call<OrderData> call = BuyInputsAPIClient.getInstance()
                .getOrders
                        (access_token,
                                customerID,
                                ConstantValues.LANGUAGE_ID,
                                ConstantValues.CURRENCY_CODE
                        );

        call.enqueue(new Callback<OrderData>() {
            @Override
            public void onResponse(Call<OrderData> call, retrofit2.Response<OrderData> response) {

                String str = new Gson().toJson(response.body());

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        
                        // Orders have been returned. Add Orders to the ordersList
                        addOrders(response.body());
                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        binding.emptyRecord.setVisibility(View.VISIBLE);
                        Snackbar.make(getView(), response.body().getMessage(), Snackbar.LENGTH_LONG).show();
    
                    }
                    else {
                        // Unable to get Success status
                        binding.emptyRecord.setVisibility(View.VISIBLE);
                        Snackbar.make(getView(), getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
                dialogLoader.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<OrderData> call, Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
                dialogLoader.hideProgressDialog();
            }
        });
    }
    
    
    //*********** Request User's cancel order ********//
    
    public void RequestMyOrdersCancel(int orderID) {
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        dialogLoader.showProgressDialog();
        
        Call<OrderData> call = BuyInputsAPIClient.getInstance()
                .updatestatus
                        (access_token,
                                customerID,
                                orderID+""
                        );
        
        call.enqueue(new Callback<OrderData>() {
            @Override
            public void onResponse(Call<OrderData> call, retrofit2.Response<OrderData> response) {
                
                dialogLoader.hideProgressDialog();
                
                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        
                        // Orders status has been changed. Add Orders again to the ordersList
                        RequestMyOrders();
                        
                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        binding.emptyRecord.setVisibility(View.VISIBLE);
                        Snackbar.make(getView(), response.body().getMessage(), Snackbar.LENGTH_LONG).show();
                        
                    }
                    else {
                        // Unable to get Success status
                        binding.emptyRecord.setVisibility(View.VISIBLE);
                        Snackbar.make(getView(), getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<OrderData> call, Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Hide Cart Icon in the Toolbar
        MenuItem cartItem = menu.findItem(R.id.ic_cart_item);

        cartItem.setVisible(false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    
}

