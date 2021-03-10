package com.cabral.emaishapay.adapters.buyInputsAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.cabral.emaishapay.R;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.database.User_Cart_BuyInputsDB;
import com.cabral.emaishapay.fragments.buy_fragments.CheckoutFinal;
import com.cabral.emaishapay.fragments.buy_fragments.My_Cart;
import com.cabral.emaishapay.models.address_model.AddressDetails;
import com.cabral.emaishapay.models.cart_model.CartProduct;
import com.cabral.emaishapay.models.merchants_model.MerchantDetails;
import com.cabral.emaishapay.models.product_model.ProductDetails;
import com.cabral.emaishapay.models.shipping_model.ShippingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * merchantsListAdapter is the adapter class of RecyclerView holding List of Orders in My_Orders
 **/

public class MerchantsListAdapter extends RecyclerView.Adapter<MerchantsListAdapter.MyViewHolder> {
    private static final String TAG = "MerchantsListAdapter";
    Context context;
    List<MerchantDetails> merchantsList;
    My_Cart my_cart;
    FragmentManager fragmentManager;
    String deliverycharge="0";

    //declare interface
    private OnItemClicked onClick;

    //make interface like this
    public interface OnItemClicked {
        void onItemClick(int position);
    }


    public MerchantsListAdapter(Context context, List<MerchantDetails> merchantsList, My_Cart my_cart, FragmentManager fragmentManager) {
        this.context = context;
        this.merchantsList = merchantsList;
        this.my_cart = my_cart;
        this.fragmentManager = fragmentManager;
    }


    //********** Called to Inflate a Layout from XML and then return the Holder *********//

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // Inflate the custom layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.buy_inputs_card_merchants/*buy_inputs_card_merchants*/, parent, false);

        return new MyViewHolder(itemView);
    }


    //********** Called by RecyclerView to display the Data at the specified Position *********//

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        // Get the data model based on Position
        final MerchantDetails merchantsDetails = merchantsList.get(position);

        holder.merchants_businessName.setText(merchantsDetails.getBusinessName());
        holder.merchant_location.setText(merchantsDetails.getAddress());

        int order_price = merchantsDetails.getTotalOrderPrice();
        if(order_price > 0){
            holder.order_price.setText(context.getString(R.string.defaultcurrency) +" " + order_price);
        }
        else{
            holder.order_price.setText("N/A");
        }

        //MerchantProductListAdapter
        // Initialize the merchantsListAdapter for RecyclerView
        RecyclerView productsRecyclerView = holder.order_products_list;
        final Map<String, String[]> productList = merchantsDetails.getProductPrices();
        ArrayList<String> productNameList = new ArrayList<String>();

        for (Map.Entry<String, String[]> entry : productList.entrySet()) {
            productNameList.add(entry.getKey());
        }
        Log.d(TAG, "onBindViewHolderprductlist: "+productList);

        MerchantProductListAdapter productsListAdapter = new MerchantProductListAdapter(this.context, productNameList, productList);
        // Set the Adapter and LayoutManager to the RecyclerView
        productsRecyclerView.setAdapter(productsListAdapter);

        productsRecyclerView.setHasFixedSize(true);

        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this.context, RecyclerView.VERTICAL, false));
        if (productNameList.size() > 7)
            productsRecyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150));

        productsListAdapter.notifyDataSetChanged();


        final AddressDetails shippingAddress = ((EmaishaPayApp) context.getApplicationContext()).getShippingAddress();

        holder.select_btn.setOnClickListener(v -> {
            if (!holder.order_price.getText().toString().equalsIgnoreCase("N/A")) {

                User_Cart_BuyInputsDB user_cart_BuyInputs_db = new User_Cart_BuyInputsDB();
                List<CartProduct> checkoutItemsList = user_cart_BuyInputs_db.getCartItems();//get checkout items

                for (int i = 0; i < checkoutItemsList.size(); i++) {
                    //update Cart product Prices
                    ProductDetails product_details = checkoutItemsList.get(i).getCustomersBasketProduct();
                    CartProduct cart_product = checkoutItemsList.get(i);
                    Log.d(TAG, "onBindViewHoldername: "+product_details.getProductsName());
                    String productKey=product_details.getProductsName()+" " + product_details.getSelectedProductsWeight();
                    String[] productpricedetails=productList.get(productKey);

                    //NB:CONVENTION product price in the producttList is also null if product is out of stoke
                    if ( productpricedetails!=null ){//Merchant sells the product
                        product_details.setProductsPrice(productpricedetails[0]);
                        product_details.setTotalPrice((product_details.getCustomersBasketQuantity() * Integer.parseInt(productpricedetails[0]) + ""));
                        product_details.setProductsFinalPrice(product_details.getTotalPrice());

                        checkoutItemsList.get(i).setCustomersBasketProduct(product_details);
                        user_cart_BuyInputs_db.updateCart(cart_product);
                        Log.d(TAG, "onBindViewHolder: "+checkoutItemsList);
                    } else {

                        user_cart_BuyInputs_db.deleteCartItem(cart_product.getCustomersBasketId());
                        Log.d(TAG, "onBindViewHolderr: "+checkoutItemsList);
                    }

                }
                //end Cart product update

                final String tax = "0";
                final ShippingService shippingService = new ShippingService();
                shippingService.setName(shippingAddress.getFirstname() + shippingAddress.getLastname() != null ? " " + shippingAddress.getLastname() : "");
                shippingService.setCurrencyCode(context.getString(R.string.defaultcurrency));
                if (merchantsDetails.getDistance() != null)
                    deliverycharge="" + 2000 * (Math.round(Float.parseFloat(merchantsDetails.getDistance()) * 10) / 10);

                shippingService.setRate(deliverycharge);
                // Save the AddressDetails
                ((EmaishaPayApp) context.getApplicationContext()).setTax(tax);
                ((EmaishaPayApp) context.getApplicationContext()).setShippingService(shippingService);

                // Navigate to CheckoutFinal Fragment
                Fragment fragment = new CheckoutFinal(my_cart, user_cart_BuyInputs_db, merchantsDetails.getMerchantId() + "");
                fragmentManager.beginTransaction().add(R.id.nav_host_fragment2, fragment, context.getString(R.string.checkout))
                        .addToBackStack(null).commit();
            }
        });

        holder.ignore_btn.setOnClickListener(v -> removeItem(position));
    }

    //********** Returns the total number of items in the data set *********//

    @Override
    public int getItemCount() {
        return merchantsList.size();
    }

    public void removeItem(int position) {
        // Remove specified position
        merchantsList.remove(position);
        // Notify adapter to remove the position
        notifyItemRemoved(position);
        // Notify adapter about data changed
        notifyItemChanged(position);
        // Notify adapter about item range changed
        notifyItemRangeChanged(position, merchantsList.size());
    }

    /********** Custom ViewHolder provides a direct reference to each of the Views within a Data_Item *********/

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView merchants_businessName, merchant_location, order_price;
        private RecyclerView order_products_list;
        private Button select_btn, ignore_btn;


        public MyViewHolder(final View itemView) {
            super(itemView);
            merchants_businessName = itemView.findViewById(R.id.text_businessName);
            merchant_location = itemView.findViewById(R.id.text_location_address);
            order_price = itemView.findViewById(R.id.order_price);
            order_products_list = itemView.findViewById(R.id.product_order_list);
            select_btn = itemView.findViewById(R.id.select_btn);
            ignore_btn = itemView.findViewById(R.id.ignore_btn);
        }
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }
}

