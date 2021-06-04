package com.cabral.emaishapay.fragments.buy_fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.cabral.emaishapay.AppExecutors;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletBuySellActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.buyInputsAdapters.CartItemsAdapter;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;

import com.cabral.emaishapay.database.User_Cart_BuyInputsDB;
import com.cabral.emaishapay.databinding.BuyInputsMyCartBinding;
import com.cabral.emaishapay.models.cart_model.CartProduct;
import com.cabral.emaishapay.models.cart_model.CartProductAttributes;
import com.cabral.emaishapay.models.product_model.GetAllProducts;
import com.cabral.emaishapay.models.product_model.GetStock;
import com.cabral.emaishapay.models.product_model.ProductData;
import com.cabral.emaishapay.models.product_model.ProductDetails;
import com.cabral.emaishapay.models.product_model.ProductStock;
import com.cabral.emaishapay.modelviews.DefaultAddressModelView;
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient;
import com.cabral.emaishapay.network.db.entities.DefaultAddress;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import am.appwise.components.ni.NoInternetDialog;
import retrofit2.Call;
import retrofit2.Response;

public class My_Cart extends Fragment implements Serializable {
    private static final String TAG = "My_Cart";
    String customerID;
    NestedScrollView mainRvLayout;


    CartItemsAdapter cartItemsAdapter;
    User_Cart_BuyInputsDB user_cart_BuyInputs_db = new User_Cart_BuyInputsDB();
    BuyInputsMyCartBinding binding;
    List<CartProduct> cartItemsList = new ArrayList<>();
    List<CartProduct> finalCartItemsList = new ArrayList<>();
    List<ProductDetails> cartProducts = new ArrayList<>();
    List<String> stocks = new ArrayList<>();
    DialogLoader dialogLoader;
    Toolbar toolbar;
    private Context context;
    public TextView cart_item_total_price;
    DefaultAddressModelView viewModel;

    public  My_Cart(){  }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("My Cart");
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
        binding = DataBindingUtil.inflate(inflater,R.layout.buy_inputs_my_cart, container, false);

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
        //noInternetDialog.show();
        cart_item_total_price = binding.cartItemTotalPrice;
        setHasOptionsMenu(true);
        dialogLoader = new DialogLoader(getContext());
        viewModel = new ViewModelProvider(requireActivity()).get(DefaultAddressModelView.class);
        // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
        //MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarProductHome);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("My Cart");
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        customerID = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
        // Get the List of Cart Items from the Local Databases User_Cart_DB
        finalCartItemsList = cartItemsList = user_cart_BuyInputs_db.getCartItems();



//        cart_item_discount_price = rootView.findViewById(R.id.cart_item_discount_price);

        // Change the Visibility of cart_view and cart_view_empty LinearLayout based on CartItemsList's Size
        if (cartItemsList.size() != 0) {
            binding.cartView.setVisibility(View.VISIBLE);

            binding.cartViewEmpty.setVisibility(View.GONE);
        } else {
            binding.cartView.setVisibility(View.GONE);
            binding.cartViewEmpty.setVisibility(View.VISIBLE);
        }

        // Initialize the AddressListAdapter for RecyclerView
        cartItemsAdapter = new CartItemsAdapter(getContext(), finalCartItemsList, My_Cart.this);

        // Set the Adapter and LayoutManager to the RecyclerView
        binding.cartItemsRecycler.setAdapter(cartItemsAdapter);
        binding.cartItemsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        // Show the Cart's Total Price with the help of static method of CartItemsAdapter
        cartItemsAdapter.setCartTotal();

        cartItemsAdapter.notifyDataSetChanged();

        Log.d(TAG, "onCreateView: "+finalCartItemsList);
        subscribeToDefaultAddress(viewModel.getDefaultAddress());
//        getDefaultAddress();

        binding.btnClearCart.setOnClickListener(view -> {
            // Delete CartItem from Local Database using static method of My_Cart
            ClearCart();


            // Calculate Cart's Total Price Again
            cartItemsAdapter.setCartTotal();

            // Remove CartItem from Cart List
            cartItemsList.clear();

            // Notify that item at position has been removed
            cartItemsAdapter.notifyDataSetChanged();

            // Update Cart View from Local Database using static method of My_Cart
            updateCartView(cartItemsList.size());

        });

        // Handle Click event of continue_shopping_btn Button
        binding.continueShoppingBtn.setOnClickListener(view -> {
            // Go back to previous fragment(wallet home)
            requireActivity().onBackPressed();
        });
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        // Handle Click event of cart_checkout_btn Button
        binding.cartCheckoutBtn.setOnClickListener(view -> {
                            Log.e("VC_Shop", "checkout executes  ");
                            checkStockTask();

        });


        return binding.getRoot();
    }


    public void subscribeToDefaultAddress(LiveData<List<DefaultAddress>> defaultAddress) {


        defaultAddress.observe(this.getViewLifecycleOwner(), myAddresses -> {
            // dialogLoader.showProgressDialog();

            if (myAddresses != null && myAddresses.size() != 0) {
                for (int i = 0; i < myAddresses.size(); i++) {
                    String street = myAddresses.get(i).getEntry_street_address();
                    String city = myAddresses.get(i).getEntry_city();
                    String country = myAddresses.get(i).getEntry_country_id();


                    binding.defaultDeliveryLocation2Tv.setText(street + " " + city + " " + country);
                }

                //dialogLoader.hideProgressDialog();
            } else {
                binding.locationLayout.setVisibility(View.GONE);
            }
        });
    }


    //*********** Change the Layout View of My_Cart Fragment based on Cart Items ********//

    public void updateCartView(int cartListSize) {
        // Check if Cart has some Items
        if (cartListSize != 0) {
            binding.cartView.setVisibility(View.VISIBLE);
            binding.cartViewEmpty.setVisibility(View.GONE);
        } else {
            binding.cartView.setVisibility(View.GONE);
            binding.cartViewEmpty.setVisibility(View.VISIBLE);
        }
    }

    //*********** Used to Share the App with Others ********//

    private void showDialog(String str) {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.buy_inputs_dialog_maintenance, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        final TextView dialog_message = dialogView.findViewById(R.id.maintenanceText);
        final Button dialog_button_positive = dialogView.findViewById(R.id.dialog_button);

        dialog_message.setText(str);

        final android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        dialog_button_positive.setOnClickListener(v -> alertDialog.dismiss());
    }

    //*********** Static method to Add the given Item to User's Cart ********//

    public static void AddCartItem(CartProduct cartProduct) {
        User_Cart_BuyInputsDB user_cart_BuyInputs_db = new User_Cart_BuyInputsDB();
        user_cart_BuyInputs_db.addCartItem
                (
                        cartProduct
                );
    }

    //*********** Static method to Get the Cart Product based on product_id ********//

    public static CartProduct GetCartProduct(int product_id) {
        User_Cart_BuyInputsDB user_cart_BuyInputs_db = new User_Cart_BuyInputsDB();
        CartProduct cartProduct = user_cart_BuyInputs_db.getCartProduct
                (
                        product_id
                );
        return cartProduct;
    }

    public void refreshCartProducts() {
        cartItemsList = user_cart_BuyInputs_db.getCartItems();
        cartItemsAdapter.notifyDataSetChanged();
    }

    //*********** Static method to Update the given Item in User's Cart ********//

    public static void UpdateCartItem(CartProduct cartProduct) {
        User_Cart_BuyInputsDB user_cart_BuyInputs_db = new User_Cart_BuyInputsDB();
        user_cart_BuyInputs_db.updateCartItem
                (
                        cartProduct
                );
    }

    //*********** Static method to Delete the given Item from User's Cart ********//

    public static void DeleteCartItem(int cart_item_id) {
        User_Cart_BuyInputsDB user_cart_BuyInputs_db = new User_Cart_BuyInputsDB();
        user_cart_BuyInputs_db.deleteCartItem
                (
                        cart_item_id
                );
    }

    //*********** Static method to Clear User's Cart ********//

    public static void ClearCart() {
        User_Cart_BuyInputsDB user_cart_BuyInputs_db = new User_Cart_BuyInputsDB();
        user_cart_BuyInputs_db.clearCart();
    }

    //*********** Static method to get total number of Items in User's Cart ********//

    public static int getCartSize() {
        int cartSize = 0;

        User_Cart_BuyInputsDB user_cart_BuyInputs_db = new User_Cart_BuyInputsDB();
        List<CartProduct> cartItems = user_cart_BuyInputs_db.getCartItems();

        for (int i = 0; i < cartItems.size(); i++) {
            cartSize += cartItems.get(i).getCustomersBasketProduct().getCustomersBasketQuantity();
        }

        return cartSize;
    }

    //*********** Static method to check if the given Product is already in User's Cart ********//

    public static boolean checkCartHasProduct(int cart_item_id) {
        User_Cart_BuyInputsDB user_cart_BuyInputs_db = new User_Cart_BuyInputsDB();
        return user_cart_BuyInputs_db.getCartItemsIDs().contains(cart_item_id);
    }

    public static boolean checkCartHasProductAndMeasure(int cart_item_id) {
        boolean result = false;

        ArrayList<CartProduct> user_cart_BuyInputs_db = new User_Cart_BuyInputsDB().checkCartHasProductAndMeasure(cart_item_id);
        for (CartProduct cartProduct : user_cart_BuyInputs_db) {
            result = cartProduct.getCustomersBasketProduct().getProductsId() == cart_item_id;
        }

        return result;
    }



    public void RequestProductDetails(final int position, final int products_id) throws IOException {

        GetAllProducts getAllProducts = new GetAllProducts();
        getAllProducts.setPageNumber(0);
        getAllProducts.setLanguageId(ConstantValues.LANGUAGE_ID);
        getAllProducts.setCustomersId(customerID);
        getAllProducts.setProductsId(String.valueOf(products_id));
        getAllProducts.setCurrencyCode(ConstantValues.CURRENCY_CODE);

        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        Call<ProductData> call = BuyInputsAPIClient.getInstance()
                .getAllProducts
                        (access_token,
                                getAllProducts
                        );
        Response<ProductData> response = call.execute();

        if (response.isSuccessful()) {

            if (response.body().getSuccess().equalsIgnoreCase("1")) {
                cartProducts.add(response.body().getProductData().get(0));
            } else {
                Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
            }
            dialogLoader.hideProgressDialog();

        } else {
            dialogLoader.hideProgressDialog();
            Toast.makeText(getContext(), "Coud not get response.", Toast.LENGTH_SHORT).show();
        }

    }

    private void requestProductStock2(int productID, List<String> attributes, int position) {
        GetStock getStockParams = new GetStock();
        getStockParams.setProductsId(productID + "");
        getStockParams.setAttributes(attributes);
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();

        Call<ProductStock> call = BuyInputsAPIClient.getInstance().getProductStock(access_token,getStockParams);
        try {
            Response<ProductStock> response = call.execute();
            if (response.isSuccessful()) {
                stocks.add(position, response.body().getStock());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getSelectedAttributesIds(List<CartProductAttributes> selectedAttributes) {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < selectedAttributes.size(); i++) {
            ids.add(String.valueOf(selectedAttributes.get(i).getValues().get(0).getProducts_attributes_id()));
        }
        return ids;
    }

    private boolean isAllStockValid(List<String> stocks) {
        for (int i = 0; i < stocks.size(); i++) {
            if (Integer.parseInt(stocks.get(i)) <= 0)
                return false;
        }
        return true;
    }


    private void checkStockTask() {
        dialogLoader.showProgressDialog();
        stocks.clear();
        AppExecutors.getInstance().diskIO().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < cartItemsList.size(); i++) {
                            requestProductStock2(cartItemsList.get(i).getCustomersBasketProduct().getProductsId(), getSelectedAttributesIds(cartItemsList.get(i).getCustomersBasketProductAttributes()), i);
                        }
                        AppExecutors.getInstance().mainThread().execute(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        dialogLoader.hideProgressDialog();
                                        if (isAllStockValid(stocks)) {
                                            //got to my address
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("my_cart",My_Cart.this);
                                            WalletBuySellActivity.navController.navigate(R.id.action_myCart_to_walletAddressesFragment,bundle);

                                        } else {
                                            Toast.makeText(getContext(), "Your Product in the cart is out of stock.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                        );
                    }
                }
        );
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Hide Cart Icon in the Toolbar
        MenuItem cartItem = menu.findItem(R.id.ic_cart_item);
        cartItem.setVisible(false);

        //set badge value
        User_Cart_BuyInputsDB user_cart_BuyInputs_db = new User_Cart_BuyInputsDB();
        List<CartProduct> cartItemsList;
        cartItemsList = user_cart_BuyInputs_db.getCartItems();
        TextView badge = (TextView) cartItem.getActionView().findViewById(R.id.cart_badge);
        badge.setText(String.valueOf(cartItemsList.size()));
    }


}

