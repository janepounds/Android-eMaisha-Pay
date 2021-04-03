package com.cabral.emaishapay.fragments.shop_fragment;

import android.app.Activity;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.Shop.ProductAdapter;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.database.DatabaseAccess;
import com.cabral.emaishapay.database.DbHandlerSingleton;
import com.cabral.emaishapay.fragments.TokenAuthFragment;
import com.cabral.emaishapay.models.shop_model.Manufacturer;
import com.cabral.emaishapay.models.shop_model.ManufacturersResponse;
import com.cabral.emaishapay.network.BuyInputsAPIClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopProductsFragment extends Fragment {

    Toolbar toolbar;
    ProductAdapter productAdapter;
    ImageView imgNoProduct;
    EditText etxtSearch;
    FloatingActionButton fabAdd;
    private RecyclerView recyclerView;
    FragmentManager fm;
    Activity shop; String wallet_id;
    private Context context;
    private List<Manufacturer> manufacturers;
    ArrayList<HashMap<String, String>> products;
    public ShopProductsFragment() {

    }

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

        getOnlineManufacturers();
        wallet_id = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop_products, container, false);
        toolbar = view.findViewById(R.id.toolbar_shop_products);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Products");
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
                FragmentTransaction ft = fm.beginTransaction();
                Fragment prev = fm.findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                DialogFragment addProductDialog = new AddProductFragment(manufacturers);
                addProductDialog.show(ft, "dialog");
            }
        });

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity());
        databaseAccess.open();
        //get data from local database
        List<HashMap<String, String>> productData= new ArrayList();
        productData = databaseAccess.getProducts();

        Log.d("data", "" + productData.size());

        if (productData.size() <= 0) {
            Toasty.info(context, R.string.no_product_found, Toast.LENGTH_SHORT).show();
            imgNoProduct.setImageResource(R.drawable.no_product);
        } else {
            imgNoProduct.setVisibility(View.GONE);

            productAdapter = new ProductAdapter(context, productData, getActivity().getSupportFragmentManager(),manufacturers);

            recyclerView.setAdapter(productAdapter);
        }


        etxtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                List<HashMap<String, String>> searchProductList;
                searchProductList = databaseAccess.getSearchProducts(s.toString());


                if (searchProductList.size() <= 0) {
                    //  Toasty.info(ProductActivity.this, "No Product Found!", Toast.LENGTH_SHORT).show();

                    recyclerView.setVisibility(View.GONE);
                    imgNoProduct.setVisibility(View.VISIBLE);
                    imgNoProduct.setImageResource(R.drawable.no_product);
                    //  txtNoProducts.setVisibility(View.VISIBLE);


                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    imgNoProduct.setVisibility(View.GONE);


                    productAdapter = new ProductAdapter(getContext(), searchProductList, getActivity().getSupportFragmentManager(), manufacturers);

                    recyclerView.setAdapter(productAdapter);


                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });
        getOnlineOrders();


        return view;
    }

    private  void getOnlineManufacturers(){
        DialogLoader dialogLoader = new DialogLoader(getContext());
        dialogLoader.showProgressDialog();

        String access_token = TokenAuthFragment.WALLET_ACCESS_TOKEN;
        Call<ManufacturersResponse> call1 = BuyInputsAPIClient
                .getInstance()
                .getManufacturers(access_token);
        call1.enqueue(new Callback<ManufacturersResponse>() {
            @Override
            public void onResponse(Call<ManufacturersResponse> call, Response<ManufacturersResponse> response) {
                if (response.isSuccessful()) {

                    saveManufacturersList(response.body().getManufacturers());
                    //Log.d("Categories", String.valueOf(categories));

                } else {
                    Log.d("Failed", "Manufacturers Fetch failed");

                }
                dialogLoader.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<ManufacturersResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("Failed", "Manufacturers Fetch failed");
                dialogLoader.hideProgressDialog();
            }
        });

    }

    private  void getOnlineOrders(){
        //NetworkStateChecker.RegisterDeviceForFCM(HomeActivity.this);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity());
        databaseAccess.open();

        Call<ResponseBody> call1 = BuyInputsAPIClient
                .getInstance()
                .getOrders(
                        wallet_id
                );

        call1.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String s = null;
                    try {
                        s = response.body().string();
                        if (s != null) {
                            JSONObject jsonObject = new JSONObject(s);
                            Log.d("Order Fetch", String.valueOf(jsonObject.getJSONArray("orders")));
                            JSONArray order_array = jsonObject.getJSONArray("orders");
                            for (int i = 0; i < order_array.length(); i++) {
                                Log.d("Order", String.valueOf(order_array.getJSONObject(i)));
                                boolean check = databaseAccess.addOrder(order_array.getJSONObject(i));
                                if (check) {
                                    Log.d("Update Status", "New Order inserted or updated Successfully");
                                } else {
                                    Log.d("Update Failure", "New Order insertion failed");
                                }

                            }
                        } else {

                            Log.d("Order Fetch", "Response is Empty");
                        }

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }finally {
//                        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity());
//                        databaseAccess.open();
//                        products = databaseAccess.getProducts();
                    }

                } else {
                    Log.d("Order Fetch", "Response is an Error");

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();

            }
        });
    }

    public void saveManufacturersList(List<Manufacturer> manufacturers) {
        this.manufacturers = manufacturers;
    }
}