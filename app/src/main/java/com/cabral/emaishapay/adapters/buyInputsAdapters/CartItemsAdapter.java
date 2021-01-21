package com.cabral.emaishapay.adapters.buyInputsAdapters;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletBuySellActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.database.User_Cart_BuyInputsDB;
import com.cabral.emaishapay.database.User_Recents_BuyInputsDB;
import com.cabral.emaishapay.fragments.buy_fragments.My_Cart;
import com.cabral.emaishapay.fragments.buy_fragments.Product_Description;
import com.cabral.emaishapay.fragments.buy_fragments.Products;
import com.cabral.emaishapay.models.cart_model.CartProduct;
import com.cabral.emaishapay.models.cart_model.CartProductAttributes;
import com.cabral.emaishapay.models.product_model.Value;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * CartItemsAdapter is the adapter class of RecyclerView holding List of Cart Items in My_Cart
 **/

public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.MyViewHolder> {

    private Context context;
    private My_Cart cartFragment;
    private List<CartProduct> cartItemsList = new ArrayList<>();

    private User_Cart_BuyInputsDB user_cart_BuyInputs_db;
    private User_Recents_BuyInputsDB recents_db;
    
    private ProductAttributeValuesAdapter attributesAdapter;


    public CartItemsAdapter(Context context, List<CartProduct> cartItemsList, My_Cart cartFragment) {
        this.context = context;
        this.cartItemsList = cartItemsList;
        this.cartFragment = cartFragment;

        user_cart_BuyInputs_db = new User_Cart_BuyInputsDB();
        recents_db = new User_Recents_BuyInputsDB();
        notifyDataSetChanged();
    }



    //********** Called to Inflate a Layout from XML and then return the Holder *********//

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
    
        View itemView;
    
        if(viewType == R.layout.buy_inputs_button){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.buy_inputs_button, parent, false);
        }
        else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.buy_inputs_card_cart_items, parent, false);
        }
        
        return new MyViewHolder(itemView);
    }



    //********** Called by RecyclerView to display the Data at the specified Position *********//

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
    
        if (position == cartItemsList.size()) {
            holder.custom_button.setText(context.getString(R.string.explore));
            
            holder.custom_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isSubFragment", false);
    
                    // Navigate to Products Fragment
                    Fragment fragment = new Products();
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = ((WalletHomeActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(R.id.main_fragment_container, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .detach(fragment)
                            .attach(fragment)
                            .addToBackStack(context.getString(R.string.actionCart)).commit();
                }
            });
            
        }
        else {
            // Get the data model based on Position
            final CartProduct cartProduct = cartItemsList.get(position);
    
            // Set Product Image on ImageView with Glide Library
            Glide.with(context).load(ConstantValues.ECOMMERCE_URL+ cartProduct.getCustomersBasketProduct().getProductsImage()).into(holder.cart_item_cover);

            String cart_title = cartProduct.getCustomersBasketProduct().getProductsName();
            holder.cart_item_title.setText(cart_title.substring(0, 1).toUpperCase() + cart_title.substring(1).toLowerCase());
            //holder.cart_item_category.setText(cartProduct.getCustomersBasketProduct().getCategoryNames());
            holder.cart_item_quantity.setText("" + cartProduct.getCustomersBasketProduct().getCustomersBasketQuantity());
            holder.cart_item_base_price.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(Double.parseDouble(cartProduct.getCustomersBasketProduct().getProductsPrice())));
//            holder.cart_item_sub_price.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(Double.parseDouble(cartProduct.getCustomersBasketProduct().getTotalPrice())));

            holder.cart_item_measure.setText(cartProduct.getCustomersBasketProduct().getSelectedProductsWeight());
    
            List<Value> selectedAttributeValues= new ArrayList<>();
            List<CartProductAttributes> productAttributes= new ArrayList<>();
    
            productAttributes = cartProduct.getCustomersBasketProductAttributes();
    
            for (int i=0;  i<productAttributes.size();  i++) {
                selectedAttributeValues.add(productAttributes.get(i).getValues().get(0));
            }
    
            // Initialize the ProductAttributeValuesAdapter for RecyclerView
            attributesAdapter = new ProductAttributeValuesAdapter(context, selectedAttributeValues);
    
            holder.attributes_recycler.setAdapter(attributesAdapter);
            holder.attributes_recycler.setLayoutManager(new LinearLayoutManager(context));
    
            attributesAdapter.notifyDataSetChanged();
            
            
    
            // Holds Product Quantity
            final int[] number = {1};
            number[0] = cartProduct.getCustomersBasketProduct().getCustomersBasketQuantity();
    
    
            // Decrease Product Quantity
            holder.cart_item_quantity_minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Check if the Quantity is greater than the minimum Quantity
                    if (number[0] > 1)
                    {
                        // Decrease Quantity by 1
                        number[0] = number[0] - 1;
                        holder.cart_item_quantity.setText(""+ number[0]);
                
                        // Calculate Product Price with selected Quantity
                        double price = Double.parseDouble(cartProduct.getCustomersBasketProduct().getProductsFinalPrice()) * number[0];
                
                        // Set Final Price and Quantity
                        cartProduct.getCustomersBasketProduct().setTotalPrice(""+ price);
                        cartProduct.getCustomersBasketProduct().setCustomersBasketQuantity(number[0]);
                        
                
                        // Update CartItem in Local Database using static method of My_Cart
                        My_Cart.UpdateCartItem
                                (
                                        cartProduct
                                );
                
                        // Calculate Cart's Total Price Again
                        setCartTotal();
                
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                }
            });
    
    
            // Increase Product Quantity
            holder.cart_item_quantity_plusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Check if the Quantity is less than the maximum or stock Quantity
                    if (number[0] < cartProduct.getCustomersBasketProduct().getProductsQuantity()) {
                        // Increase Quantity by 1
                        number[0] = number[0] + 1;
                        holder.cart_item_quantity.setText(""+ number[0]);
                
                        // Calculate Product Price with selected Quantity
                        double price = Double.parseDouble(cartProduct.getCustomersBasketProduct().getProductsFinalPrice()) * number[0];
                
                        // Set Final Price and Quantity
                        cartProduct.getCustomersBasketProduct().setTotalPrice(""+ price);
                        cartProduct.getCustomersBasketProduct().setCustomersBasketQuantity(number[0]);
                        
                
                        // Update CartItem in Local Database using static method of My_Cart
                        My_Cart.UpdateCartItem
                                (
                                        cartProduct
                                );
                
                        // Calculate Cart's Total Price Again
                        setCartTotal();
                
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                }
            });
    
    
            // View Product Details
            holder.cart_item_viewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get Product Info
                    Bundle itemInfo = new Bundle();
                    itemInfo.putInt("itemID", cartProduct.getCustomersBasketProduct().getProductsId());

//                    holder.cart_item_measure.getText().toString()
                    // Navigate to Product_Description of selected Product
                    Fragment fragment = new Product_Description();
                    fragment.setArguments(itemInfo);
                    FragmentManager fragmentManager = ((WalletHomeActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(R.id.nav_host_fragment2, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(null)
                            .commit();
            
            
                    // Add the Product to User's Recently Viewed Products
                    if (!recents_db.getUserRecents().contains(cartProduct.getCustomersBasketProduct().getProductsId())) {
                        recents_db.insertRecentItem(cartProduct.getCustomersBasketProduct().getProductsId());
                    }
                }
            });
    
    
    
            // Delete relevant Product from Cart
            holder.cart_item_removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
            
                    holder.cart_item_removeBtn.setClickable(false);
            
                    // Delete CartItem from Local Database using static method of My_Cart
                    My_Cart.DeleteCartItem
                            (
                                    cartProduct.getCustomersBasketId()
                            );
            

                    // Calculate Cart's Total Price Again
                    setCartTotal();
            
                    // Remove CartItem from Cart List
                    cartItemsList.remove(holder.getAdapterPosition());
            
                    // Notify that item at position has been removed
                    notifyItemRemoved(holder.getAdapterPosition());
            
                    // Update Cart View from Local Database using static method of My_Cart
                    cartFragment.updateCartView(getItemCount());
            
                }
            });
            
        }
        

    }



    //********** Returns the total number of items in the data set *********//

    @Override
    public int getItemCount() {
        return (cartItemsList.size() == 0) ? cartItemsList.size() : cartItemsList.size() + 1;
    }
    
    
    //********** Return the view type of the item at position for the purposes of view recycling *********//
    
    @Override
    public int getItemViewType(int position) {
        return (position == cartItemsList.size()) ? R.layout.buy_inputs_button : R.layout.buy_inputs_card_cart_items;
    }



    //*********** Calculate and Set the Cart's Total Price ********//

    public void setCartTotal() {

        double finalPrice = 0;
        List<CartProduct> cartItemsList = user_cart_BuyInputs_db.getCartItems();

        for (int i=0;  i<cartItemsList.size();  i++) {
            // Update the Cart's Total Price
            finalPrice += Double.parseDouble(cartItemsList.get(i).getCustomersBasketProduct().getTotalPrice());
        }

        cartFragment.cart_item_total_price.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(finalPrice));
//        cartFragment.cart_item_discount_price.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(0.0));
//        cartFragment.cart_item_subtotal_price.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(finalPrice));
    }



    /********** Custom ViewHolder provides a direct reference to each of the Views within a Data_Item *********/

    public class MyViewHolder extends RecyclerView.ViewHolder {
    
        private Button custom_button;
        private LinearLayout cart_item_viewBtn;
        private ImageView cart_item_cover;
        private RecyclerView attributes_recycler;
        private ImageButton cart_item_quantity_minusBtn, cart_item_quantity_plusBtn;
        private ImageView cart_item_removeBtn;
        private TextView cart_item_title, cart_item_category, cart_item_base_price, cart_item_sub_price, cart_item_quantity,cart_item_measure;


        public MyViewHolder(final View itemView) {
            super(itemView);
    
            custom_button = itemView.findViewById(R.id.custom_button);
            
            cart_item_title = itemView.findViewById(R.id.cart_item_title);
            cart_item_base_price = itemView.findViewById(R.id.cart_item_base_price);
            //cart_item_sub_price = itemView.findViewById(R.id.cart_item_sub_price);
            cart_item_quantity = itemView.findViewById(R.id.cart_item_quantity);
            cart_item_cover = itemView.findViewById(R.id.cart_item_cover);
            cart_item_viewBtn = itemView.findViewById(R.id.cart_item_viewBtn);
            cart_item_removeBtn = itemView.findViewById(R.id.cart_item_removeBtn);
            cart_item_quantity_plusBtn = itemView.findViewById(R.id.cart_item_quantity_plusBtn);
            cart_item_quantity_minusBtn = itemView.findViewById(R.id.cart_item_quantity_minusBtn);
            cart_item_measure = itemView.findViewById(R.id.cart_item_measure);
            
            attributes_recycler = itemView.findViewById(R.id.cart_item_attributes_recycler);
        }
        
    }

}

