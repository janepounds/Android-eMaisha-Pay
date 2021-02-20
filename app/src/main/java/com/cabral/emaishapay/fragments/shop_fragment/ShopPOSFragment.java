package com.cabral.emaishapay.fragments.shop_fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.ShopActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.Shop.PosProductAdapter;
import com.cabral.emaishapay.database.DbHandlerSingleton;

import java.util.HashMap;
import java.util.List;

public class ShopPOSFragment extends Fragment {

    FragmentManager fm;
    Activity shop;
    private Context context;
    public static EditText etxtSearch, etxtCharge;
    PosProductAdapter productAdapter;
    TextView txtNoProducts, txtEnter, txtItems;
    View enterView, itemsView;
    ConstraintLayout layoutCart;
    ImageView imgNoProduct, imgScanner;
    private RecyclerView recyclerView;
    Toolbar toolbar; String userId;
    private DbHandlerSingleton dbHandler;


    public ShopPOSFragment() {
    }

    public ShopPOSFragment(ShopActivity shopActivity, FragmentManager supportFragmentManager) {
        this.fm = supportFragmentManager;
        this.shop = shopActivity;
    }

    public ShopPOSFragment(boolean b) {
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
        View view = inflater.inflate(R.layout.fragment_shop_pos, container, false);
        toolbar = view.findViewById(R.id.toolbar_shop_pos);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(R.string.all_product);

        etxtSearch = view.findViewById(R.id.etxt_search);
        recyclerView = view.findViewById(R.id.recycler);
        imgNoProduct = view.findViewById(R.id.image_no_product);
        txtNoProducts = view.findViewById(R.id.txt_no_products);
        //imgScanner = view.findViewById(R.id.img_scanner);

        etxtCharge = view.findViewById(R.id.pos_charge);
        txtEnter = view.findViewById(R.id.txt_enter);
        txtItems = view.findViewById(R.id.txt_items);
        enterView = view.findViewById(R.id.enter_selected);
        itemsView = view.findViewById(R.id.items_selected);
        layoutCart = view.findViewById(R.id.layout_cart);

        //for interstitial ads show
//        Utils utils=new Utils();
//        utils.interstitialAdsShow(PosActivity.this);
        dbHandler = DbHandlerSingleton.getHandlerInstance(getContext());

        txtEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterView.setVisibility(View.VISIBLE);
                itemsView.setVisibility(View.INVISIBLE);
                etxtSearch.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                layoutCart.setVisibility(View.GONE);
                etxtCharge.setVisibility(View.VISIBLE);
                etxtCharge.requestFocus();

            }
        });
        txtItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterView.setVisibility(View.INVISIBLE);
                itemsView.setVisibility(View.VISIBLE);
                etxtSearch.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                layoutCart.setVisibility(View.VISIBLE);
                etxtCharge.setVisibility(View.GONE);
                etxtCharge.clearFocus();


                //get data from local database
                List<HashMap<String, String>> productList;
                userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
                productList = dbHandler.getProducts(userId);

                if (productList.size() <= 0) {

                    recyclerView.setVisibility(View.GONE);
                    imgNoProduct.setVisibility(View.VISIBLE);
                    imgNoProduct.setImageResource(R.drawable.not_found);
                    txtNoProducts.setVisibility(View.VISIBLE);


                } else {


                    recyclerView.setVisibility(View.VISIBLE);
                    imgNoProduct.setVisibility(View.GONE);
                    txtNoProducts.setVisibility(View.GONE);

                    productAdapter = new PosProductAdapter(getContext(), productList);

                    recyclerView.setAdapter(productAdapter);


                }

            }
        });


        layoutCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ProductCartFragment nextFrag= new ProductCartFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment3, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();


            }
        });



        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);

        // set a LinearLayoutManager with default vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView


        recyclerView.setHasFixedSize(true);


        etxtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

               // databaseAccess.open();
                //get data from local database
                List<HashMap<String, String>> searchProductList;

                searchProductList = dbHandler.getSearchProducts(s.toString(), userId);


                if (searchProductList.size() <= 0) {

                    recyclerView.setVisibility(View.GONE);
                    imgNoProduct.setVisibility(View.VISIBLE);
                    imgNoProduct.setImageResource(R.drawable.not_found);
                    txtNoProducts.setVisibility(View.VISIBLE);


                } else {


                    recyclerView.setVisibility(View.VISIBLE);
                    imgNoProduct.setVisibility(View.GONE);
                    txtNoProducts.setVisibility(View.GONE);

                    productAdapter = new PosProductAdapter(getContext(), searchProductList);

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
