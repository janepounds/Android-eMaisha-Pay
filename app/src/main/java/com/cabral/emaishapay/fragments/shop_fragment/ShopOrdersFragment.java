package com.cabral.emaishapay.fragments.shop_fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.ShopActivity;
import com.cabral.emaishapay.adapters.Shop.OnlineOrdersAdapter;
import com.cabral.emaishapay.adapters.Shop.OrderAdapter;
import com.cabral.emaishapay.database.DatabaseAccess;
import com.cabral.emaishapay.database.DbHandlerSingleton;

import java.util.HashMap;
import java.util.List;


public class ShopOrdersFragment extends Fragment {

    ImageView imgNoProduct;
    TextView txtNoProducts;
    EditText etxtSearch;
    private RecyclerView recyclerView;
    private OnlineOrdersAdapter orderAdapter;
    private DbHandlerSingleton dbHandler;
    private Context context;
    Toolbar toolbar;String wallet_id;

    public ShopOrdersFragment(ShopActivity shopActivity, FragmentManager supportFragmentManager) {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop_orders, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        imgNoProduct = view.findViewById(R.id.image_no_product);

        txtNoProducts = view.findViewById(R.id.txt_no_products);
        etxtSearch = view.findViewById(R.id.etxt_search_order);

        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);
        toolbar = view.findViewById(R.id.toolbar_orders);
        dbHandler = DbHandlerSingleton.getHandlerInstance(context);


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

        recyclerView.setHasFixedSize(true);




        //get data from local database
        List<HashMap<String, String>> orderList;
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity());
        databaseAccess.open();
        orderList = databaseAccess.getOrderList();

        if (orderList.size() <= 0) {
            //if no data in local db, then load data from server
//            Toasty.info(context, R.string.no_order_found, Toast.LENGTH_SHORT).show();
            recyclerView.setVisibility(View.GONE);
            imgNoProduct.setVisibility(View.VISIBLE);
            imgNoProduct.setImageResource(R.drawable.ic_delivery_cuate);
            txtNoProducts.setVisibility(View.VISIBLE);
        } else {
            orderAdapter = new OnlineOrdersAdapter(getContext(), orderList);

            recyclerView.setAdapter(orderAdapter);
        }


        etxtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                //  searchData(s.toString());


                //get data from local database
                List<HashMap<String, String>> searchOrder;

                searchOrder = dbHandler.searchOrderList(s.toString());


                if (searchOrder.size() <= 0) {
                    recyclerView.setVisibility(View.GONE);
                    imgNoProduct.setVisibility(View.VISIBLE);
                    imgNoProduct.setImageResource(R.drawable.no_data);


                } else {


                    recyclerView.setVisibility(View.VISIBLE);
                    imgNoProduct.setVisibility(View.GONE);


                    OrderAdapter supplierAdapter = new OrderAdapter(context, searchOrder);

                    recyclerView.setAdapter(supplierAdapter);


                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });



        return  view;
    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                requireActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    }
