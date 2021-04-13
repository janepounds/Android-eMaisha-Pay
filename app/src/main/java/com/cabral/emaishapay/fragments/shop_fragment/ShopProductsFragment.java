package com.cabral.emaishapay.fragments.shop_fragment;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.cabral.emaishapay.DailogFragments.shop.AddProductFragment;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.ShopActivity;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.Shop.ProductAdapter;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.database.DatabaseAccess;
import com.cabral.emaishapay.databinding.FragmentShopProductsBinding;
import com.cabral.emaishapay.models.shop_model.ManufacturersResponse;
import com.cabral.emaishapay.modelviews.ShopProductsModelView;
import com.cabral.emaishapay.modelviews.SignUpModelView;
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient;
import com.cabral.emaishapay.network.db.entities.EcManufacturer;
import com.cabral.emaishapay.network.db.entities.EcProduct;
import com.cabral.emaishapay.utils.Resource;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopProductsFragment extends Fragment {

    ProductAdapter productAdapter;
     String wallet_id;
    private Context context;
    private List<EcManufacturer> manufacturers=new ArrayList<>();

    DialogLoader dialogLoader;
    FragmentShopProductsBinding binding;
    private ShopProductsModelView viewModel;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_shop_products,container,false);

        dialogLoader = new DialogLoader(getContext());


        viewModel = new ViewModelProvider(this).get(ShopProductsModelView.class);
        wallet_id = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());

        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbarShopProducts);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Products");





        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                DialogFragment addProductDialog = new AddProductFragment(manufacturers);
                addProductDialog.show(ft, "dialog");
            }
        });

//        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity());
//        databaseAccess.open();
//        getOnlineOrders();



        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {



        productAdapter = new ProductAdapter(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.shopProductRecyclerview.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        binding.shopProductRecyclerview.setHasFixedSize(true);
        binding.shopProductRecyclerview.setAdapter(productAdapter);

        subscribeToMerchantProducts(viewModel.getMerchantProducts());

        subscribeToManufacturer(viewModel.getOnlineManufacturers());

        binding.etxtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setQuery(s);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });


    }

    private void subscribeToMerchantProducts(LiveData<Resource<List<EcProduct>>> merchantProducts) {
        binding.shopProductRecyclerview.setVisibility(View.GONE);
        binding.imageNoProduct.setVisibility(View.VISIBLE);
        binding.imageNoProduct.setImageResource(R.drawable.no_product);

        merchantProducts.observe(this.getViewLifecycleOwner(), myProducts->{
           // dialogLoader.showProgressDialog();

            if(myProducts.data!=null && myProducts.data.size()!=0){
                binding.imageNoProduct.setVisibility(View.GONE);
                binding.shopProductRecyclerview.setVisibility(View.VISIBLE);
                Log.d("debug","------->>>>"+myProducts.data.size());
                productAdapter.setProductList( myProducts.data);


                //dialogLoader.hideProgressDialog();
            }else {
                //Toasty.info(context, R.string.no_product_found, Toast.LENGTH_SHORT).show();
                binding.shopProductRecyclerview.setVisibility(View.GONE);
                binding.imageNoProduct.setVisibility(View.VISIBLE);
                binding.imageNoProduct.setImageResource(R.drawable.no_product);
            }
            binding.executePendingBindings();
        });
    }

    private void subscribeToManufacturer(LiveData<List<EcManufacturer>> onlineManufacturers) {
        onlineManufacturers.observe(getViewLifecycleOwner(), myManfacturers->{
            dialogLoader.showProgressDialog();
            if(myManfacturers!=null && myManfacturers.size()!=0){

                saveManufacturersList(myManfacturers);
                if(productAdapter!=null){
                    productAdapter.notifyDataSetChanged();
                }
                dialogLoader.hideProgressDialog();

            }
        });
    }


    public void saveManufacturersList(List<EcManufacturer> manufacturers) {
        this.manufacturers = manufacturers;
    }

}