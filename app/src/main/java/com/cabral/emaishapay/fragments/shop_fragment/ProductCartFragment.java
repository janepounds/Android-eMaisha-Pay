package com.cabral.emaishapay.fragments.shop_fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.adapters.Shop.CartAdapter;
import com.cabral.emaishapay.database.DbHandlerSingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductCartFragment extends Fragment {

    CartAdapter productCartAdapter;
    ImageView imgNoProduct;
    TextView txt_no_product, txt_total_price, btnSubmitOrder;
    LinearLayout linearLayout;
    List<String> customerNames, orderTypeNames, paymentMethodNames;
    ArrayAdapter<String> customerAdapter, orderTypeAdapter, paymentMethodAdapter;
    RecyclerView recyclerView;
    private DbHandlerSingleton dbHandler;
    private Context context;
    AlertDialog alertDialog;
    String order_type, order_payment_method, customer_name;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_product_cart, container, false);

        ((AppCompatActivity)requireActivity()).getSupportActionBar().setHomeButtonEnabled(true); //for back button
        ((AppCompatActivity)requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        ((AppCompatActivity)requireActivity()).getSupportActionBar().setTitle(R.string.product_cart);

        recyclerView = view.findViewById(R.id.cart_recyclerview);
        imgNoProduct = view.findViewById(R.id.image_no_product);
        btnSubmitOrder = view.findViewById(R.id.btn_submit_order);
        txt_no_product = view.findViewById(R.id.txt_no_product);
        linearLayout = view.findViewById(R.id.linear_layout);
        txt_total_price = view.findViewById(R.id.txt_total_price);

        txt_no_product.setVisibility(View.GONE);

        dbHandler = DbHandlerSingleton.getHandlerInstance(context);
        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView


        recyclerView.setHasFixedSize(true);





        //get data from local database
        List<HashMap<String, String>> cartProductList;
        cartProductList = dbHandler.getCartProduct();

        Log.d("CartSize", "" + cartProductList.size());

        if (cartProductList.size() <= 0) {
            // Toast.makeText(this, "No Product Found!", Toast.LENGTH_SHORT).show();
            imgNoProduct.setImageResource(R.drawable.empty_cart_image);
            imgNoProduct.setVisibility(View.VISIBLE);
            txt_no_product.setVisibility(View.VISIBLE);
            btnSubmitOrder.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            txt_total_price.setVisibility(View.GONE);
        } else {


            imgNoProduct.setVisibility(View.GONE);
            productCartAdapter = new CartAdapter(context, cartProductList, txt_total_price, btnSubmitOrder, imgNoProduct, txt_no_product);

            recyclerView.setAdapter(productCartAdapter);


        }


        btnSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog();

                //makePayment();
            }
        });
       return view;
    }

    //dialog for taking otp code
    public void dialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_payment, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        final Button dialog_btn_submit = dialogView.findViewById(R.id.btn_submit);
        final ImageButton dialog_btn_close = dialogView.findViewById(R.id.btn_close);
        final TextView dialog_order_payment_method = dialogView.findViewById(R.id.dialog_order_status);
        final TextView dialog_order_type = dialogView.findViewById(R.id.dialog_order_type);
        final TextView dialog_customer = dialogView.findViewById(R.id.dialog_customer);

        final ImageButton dialog_img_customer = dialogView.findViewById(R.id.img_select_customer);
        final ImageButton dialog_img_order_payment_method = dialogView.findViewById(R.id.img_order_payment_method);
        final ImageButton dialog_img_order_type = dialogView.findViewById(R.id.img_order_type);


        customerNames = new ArrayList<>();


        //get data from local database
        final List<HashMap<String, String>> customer;
        customer = dbHandler.getCustomers();

        for (int i = 0; i < customer.size(); i++) {

            // Get the ID of selected Country
            customerNames.add(customer.get(i).get("customer_name"));

        }


        orderTypeNames = new ArrayList<>();


        //get data from local database
        final List<HashMap<String, String>> order_types;
        order_types = dbHandler.getOrderType();

        for (int i = 0; i < order_types.size(); i++) {

            // Get the ID of selected Country
            orderTypeNames.add(order_types.get(i).get("order_type_name"));

        }


        //payment methods
        paymentMethodNames = new ArrayList<>();


        //get data from local database
        final List<HashMap<String, String>> payment_method;
        payment_method = dbHandler.getPaymentMethod();

        for (int i = 0; i < payment_method.size(); i++) {

            // Get the ID of selected Country
            paymentMethodNames.add(payment_method.get(i).get("payment_method_name"));

        }


        dialog_img_order_payment_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                paymentMethodAdapter = new ArrayAdapter<String>(context, R.layout.list_row);
                paymentMethodAdapter.addAll(paymentMethodNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);

//                dialog_title.setText(getString(R.string.zone));
                dialog_title.setText(R.string.select_payment_method);
                dialog_list.setVerticalScrollBarEnabled(true);
                dialog_list.setAdapter(paymentMethodAdapter);

                dialog_input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        paymentMethodAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialog_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        String selectedItem = paymentMethodAdapter.getItem(position);


                        dialog_order_payment_method.setText(selectedItem);


                    }
                });
            }


        });


        dialog_img_order_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                orderTypeAdapter = new ArrayAdapter<String>(context, R.layout.list_row);
                orderTypeAdapter.addAll(orderTypeNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);

//                dialog_title.setText(getString(R.string.zone));
                dialog_title.setText(R.string.select_order_type);
                dialog_list.setVerticalScrollBarEnabled(true);
                dialog_list.setAdapter(orderTypeAdapter);

                dialog_input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        orderTypeAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialog_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        String selectedItem = orderTypeAdapter.getItem(position);


                        dialog_order_type.setText(selectedItem);


                    }
                });
            }


        });


        dialog_img_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerAdapter = new ArrayAdapter<String>(context, R.layout.list_row);
                customerAdapter.addAll(customerNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);

//                dialog_title.setText(getString(R.string.zone));
                dialog_title.setText(R.string.select_customer);
                dialog_list.setVerticalScrollBarEnabled(true);
                dialog_list.setAdapter(customerAdapter);

                dialog_input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        customerAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialog_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        String selectedItem = customerAdapter.getItem(position);


                        dialog_customer.setText(selectedItem);


                    }
                });
            }
        });


        alertDialog = dialog.create();
        alertDialog.show();


        dialog_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                order_type = dialog_order_type.getText().toString().trim();
                order_payment_method = dialog_order_payment_method.getText().toString().trim();
                customer_name = dialog_customer.getText().toString().trim();

                Log.d("order type", order_type);
                Log.d("order payment method", order_payment_method);
                Log.d("customer name", customer_name);

                alertDialog.dismiss();
                String order = generateOrderNumber();

//
//                paymentThread = new Pos_Thread(order, PAYMENT_SERVICE);
//                paymentThread.start();
//
//                try {
//                    paymentThread.join(60000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                printThread.start();
//
//
//                try {
//                    paymentThread.join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//
            }


        });


        dialog_btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });


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

    private String generateOrderNumber() {
        //get current timestamp
        Long tsLong = System.currentTimeMillis() / 1000;
        return tsLong.toString();
    }
}