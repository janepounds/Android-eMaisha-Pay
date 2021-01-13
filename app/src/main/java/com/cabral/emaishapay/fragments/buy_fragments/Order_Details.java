package com.cabral.emaishapay.fragments.buyandsell;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.cabral.emaishapay.R;
import com.cabral.emaishapay.adapters.buyInputsAdapters.CouponsAdapter;
import com.cabral.emaishapay.adapters.buyInputsAdapters.OrderedProductsListAdapter;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.models.coupons_model.CouponsInfo;
import com.cabral.emaishapay.models.order_model.OrderDetails;
import com.cabral.emaishapay.models.order_model.OrderProducts;
import com.kofigyan.stateprogressbar.StateProgressBar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import am.appwise.components.ni.NoInternetDialog;

public class Order_Details extends Fragment {

    View rootView;
    
    OrderDetails orderDetails;

    CardView buyer_comments_card, seller_comments_card;
    RecyclerView checkout_items_recycler, checkout_coupons_recycler;
    TextView checkout_subtotal, checkout_tax, checkout_shipping, checkout_discount, checkout_total;
    TextView billing_name, billing_street, billing_address, shipping_name, shipping_street, shipping_address;
    TextView order_price, order_products_count, order_date, shipping_method, payment_method, buyer_comments, seller_comments, order_status;

    List<CouponsInfo> couponsList;
    List<OrderProducts> orderProductsList;

    CouponsAdapter couponsAdapter;
    OrderedProductsListAdapter orderedProductsAdapter;

    List<String> items;
    public StateProgressBar stateProgressBar;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.buy_inputs_order_details, container, false);

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
        //noInternetDialog.show();

        // Get orderDetails from bundle arguments
        orderDetails = getArguments().getParcelable("orderDetails");
        
        
        // Set the Title of Toolbar
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.order_details));
        

        // Binding Layout Views
        order_price = rootView.findViewById(R.id.order_price);
        order_products_count = rootView.findViewById(R.id.order_products_count);
        shipping_method = rootView.findViewById(R.id.shipping_method);
        payment_method = rootView.findViewById(R.id.payment_method);
        order_status = rootView.findViewById(R.id.order_status);
        order_date = rootView.findViewById(R.id.order_date);
        checkout_subtotal = rootView.findViewById(R.id.checkout_subtotal);
        checkout_tax = rootView.findViewById(R.id.checkout_tax);
        checkout_shipping = rootView.findViewById(R.id.checkout_shipping);
        checkout_discount = rootView.findViewById(R.id.checkout_discount);
        checkout_total = rootView.findViewById(R.id.checkout_total);
        billing_name = rootView.findViewById(R.id.billing_name);
        billing_address = rootView.findViewById(R.id.billing_address);
        billing_street = rootView.findViewById(R.id.billing_street);
        shipping_name = rootView.findViewById(R.id.shipping_name);
        shipping_address = rootView.findViewById(R.id.shipping_address);
        shipping_street = rootView.findViewById(R.id.shipping_street);
        buyer_comments = rootView.findViewById(R.id.buyer_comments);
        seller_comments = rootView.findViewById(R.id.seller_comments);
        buyer_comments_card = rootView.findViewById(R.id.buyer_comments_card);
        seller_comments_card = rootView.findViewById(R.id.seller_comments_card);
        checkout_items_recycler = rootView.findViewById(R.id.checkout_items_recycler);
        checkout_coupons_recycler = rootView.findViewById(R.id.checkout_coupons_recycler);

        checkout_items_recycler.setNestedScrollingEnabled(false);
        checkout_coupons_recycler.setNestedScrollingEnabled(false);


        // Set Order Details

        couponsList = orderDetails.getCoupons();
        orderProductsList = orderDetails.getProducts();

        double subTotal = 0;
        int noOfProducts = 0;
        for (int i=0;  i<orderProductsList.size();  i++) {
            subTotal += Double.parseDouble(orderProductsList.get(i).getFinalPrice());
            noOfProducts += orderProductsList.get(i).getProductsQuantity();
        }
        
        
        String orderPrice = ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(Double.parseDouble(orderDetails.getOrderPrice()));
        String Tax = ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(Double.parseDouble(orderDetails.getTotalTax()));
        String Shipping = ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(Double.parseDouble(orderDetails.getShippingCost()));
        String Discount = ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(Double.parseDouble(orderDetails.getCouponAmount()+""));
        String Subtotal = ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(subTotal);
        String Total = ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(Double.parseDouble(orderDetails.getOrderPrice()));
    
    
        order_price.setText(orderPrice);
        order_products_count.setText(String.valueOf(noOfProducts));
        shipping_method.setText(orderDetails.getShippingMethod());
        payment_method.setText(orderDetails.getPaymentMethod());

        // calling the status progressbar
        orderStatusProgressBar();

        order_status.setText(orderDetails.getOrdersStatus());
        order_date.setText(orderDetails.getDatePurchased());

        checkout_tax.setText(Tax);
        checkout_shipping.setText(Shipping);
        checkout_discount.setText(Discount);
        checkout_subtotal.setText(Subtotal);
        checkout_total.setText(Total);

        billing_name.setText(orderDetails.getBillingName());
        billing_address.setText(orderDetails.getBillingCity());
        billing_street.setText(orderDetails.getBillingStreetAddress());
        shipping_name.setText(orderDetails.getDeliveryName());
        shipping_address.setText(orderDetails.getDeliveryCity());
        shipping_street.setText(orderDetails.getDeliveryStreetAddress());

        
        if (!TextUtils.isEmpty(orderDetails.getCustomerComments())) {
            buyer_comments_card.setVisibility(View.VISIBLE);
            buyer_comments.setText(orderDetails.getCustomerComments());
        } else {
            buyer_comments_card.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(orderDetails.getAdminComments())) {
            seller_comments_card.setVisibility(View.VISIBLE);
            seller_comments.setText(orderDetails.getAdminComments());
        } else {
            seller_comments_card.setVisibility(View.GONE);
        }


        couponsAdapter = new CouponsAdapter(getContext(), couponsList, false, null);

        checkout_coupons_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        checkout_coupons_recycler.setAdapter(couponsAdapter);

        
        orderedProductsAdapter = new OrderedProductsListAdapter(getContext(), orderProductsList);

        checkout_items_recycler.setAdapter(orderedProductsAdapter);
        checkout_items_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        checkout_items_recycler.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));


        return rootView;

    }


    private void orderStatusProgressBar(){

        // this Items will all to Order status progress bar
        items = new ArrayList<>();
        items.add("Pending");
        items.add("Confirm");
        items.add("Out For Delivery");
        items.add("Delivered");

        String[] arraystr = new String[items.size()];
        stateProgressBar = rootView.findViewById(R.id.stateProgress);
        stateProgressBar.setStateDescriptionData(items.toArray(arraystr));


        // State Progress bar.
        int statusId = Integer.parseInt(orderDetails.getOrdersStatusId()+"");

        switch (statusId){
            case 1:   // Pending.
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                break;
            case 8:   // Confirmed.
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                break;
            case 5:   // Out for delivery.
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                break;
            case 2:   // completed.
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);
                break;
            case 3:   // Canceled.
                stateProgressBar.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

}



