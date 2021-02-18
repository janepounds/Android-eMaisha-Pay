package com.cabral.emaishapay.fragments.shop_fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import es.dmoral.toasty.Toasty;

import android.text.Editable;
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
import com.cabral.emaishapay.adapters.Shop.ProductAdapter;
import com.cabral.emaishapay.database.DbHandlerSingleton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;

public class ShopProductsFragment extends Fragment {

    Toolbar toolbar;
    ProductAdapter productAdapter;
    ImageView imgNoProduct;
    EditText etxtSearch;
    FloatingActionButton fabAdd;
    ProgressDialog loading;
    private RecyclerView recyclerView;
    FragmentManager fm;
    Activity shop;
    private Context context;
    private DbHandlerSingleton dbHandler;
    public ShopProductsFragment(ShopActivity shopActivity, FragmentManager supportFragmentManager) {
        this.fm = supportFragmentManager;
        this.shop = shopActivity;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop_products, container, false);
        toolbar = view.findViewById(R.id.toolbar_shop_products);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Products");

        dbHandler = DbHandlerSingleton.getHandlerInstance(getContext());
        fabAdd = view.findViewById(R.id.fab_add);
        etxtSearch = view.findViewById(R.id.etxt_search);
        recyclerView = view.findViewById(R.id.product_recyclerview);
        imgNoProduct = view.findViewById(R.id.image_no_product);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        recyclerView.setHasFixedSize(true);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(ProductActivity.this, AddProductActivity.class);
//                startActivity(intent);

                FragmentTransaction ft = fm.beginTransaction();
                Fragment prev = fm.findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                DialogFragment addProductDialog = new AddProductFragment();
                addProductDialog.show(ft, "dialog");
            }
        });


        //get data from local database
        List<HashMap<String, String>> productData;
        productData = dbHandler.getProducts();

        Log.d("data", "" + productData.size());

        if (productData.size() <= 0) {
            Toasty.info(context, R.string.no_product_found, Toast.LENGTH_SHORT).show();
            imgNoProduct.setImageResource(R.drawable.no_product);
        } else {
            imgNoProduct.setVisibility(View.GONE);
            productAdapter = new ProductAdapter(context, productData);

            recyclerView.setAdapter(productAdapter);
        }


        etxtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                List<HashMap<String, String>> searchProductList;
                searchProductList = dbHandler.getSearchProducts(s.toString());


                if (searchProductList.size() <= 0) {
                    //  Toasty.info(ProductActivity.this, "No Product Found!", Toast.LENGTH_SHORT).show();

                    recyclerView.setVisibility(View.GONE);
                    imgNoProduct.setVisibility(View.VISIBLE);
                    imgNoProduct.setImageResource(R.drawable.no_product);
                    //  txtNoProducts.setVisibility(View.VISIBLE);


                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    imgNoProduct.setVisibility(View.GONE);


                    productAdapter = new ProductAdapter(getContext(), searchProductList);

                    recyclerView.setAdapter(productAdapter);


                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });

        return view;
    }
}