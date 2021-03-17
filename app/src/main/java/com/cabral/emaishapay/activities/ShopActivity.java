package com.cabral.emaishapay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.database.DatabaseAccess;
import com.cabral.emaishapay.fragments.shop_fragment.ShopOrdersFragment;
import com.cabral.emaishapay.fragments.shop_fragment.ShopPOSFragment;
import com.cabral.emaishapay.fragments.shop_fragment.ShopProductsFragment;
import com.cabral.emaishapay.fragments.shop_fragment.ShopSalesFragment;
import com.cabral.emaishapay.network.BuyInputsAPIClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopActivity extends AppCompatActivity {
    Toolbar toolbar;
    public static ActionBar actionBar;
    public static BottomNavigationView bottomNavigationView;
    public Fragment currentFragment, defaultHomeFragment;
    String wallet_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);


        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Handle ToolbarNavigationClickListener with OnBackStackChangedListener
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {

            // Check BackStackEntryCount of FragmentManager
            if (getSupportFragmentManager().getBackStackEntryCount() <= 0) {
                // Set DrawerToggle Indicator and default ToolbarNavigationClickListener
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setHomeButtonEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(false);
                ShopActivity.bottomNavigationView.setVisibility(View.VISIBLE);
            }

            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            // setupTitle();
        });

        wallet_id = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, ShopActivity.this);
        getProductsBackUp();
        defaultHomeFragment =new ShopProductsFragment(ShopActivity.this, getSupportFragmentManager());

        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        Log.d("SHOP ACTIVITY","onVisibilityChanged: Keyboard visibility changed");
                        if(isOpen){
                            Log.d("SHOP ACTIVITY", "onVisibilityChanged: Keyboard is open");
                            bottomNavigationView.setVisibility(View.INVISIBLE);
                            Log.d("SHOP ACTIVITY", "onVisibilityChanged: NavBar got Invisible");
                        }else{
                            Log.d("SHOP ACTIVITY", "onVisibilityChanged: Keyboard is closed");
                            bottomNavigationView.setVisibility(View.VISIBLE);
                            Log.d("SHOP ACTIVITY", "onVisibilityChanged: NavBar got Visible");
                        }
                    }
                });
        setupDefaultHomePage();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = item -> {
        Fragment selectedFragment = null;
        switch (item.getItemId()) {
            case R.id.walletProductsFragment:
                selectedFragment =defaultHomeFragment;
                break;
            case R.id.walletOrdersFragment:
                selectedFragment = new ShopOrdersFragment(ShopActivity.this, getSupportFragmentManager());
                break;
            case R.id.walletPOSFragment:
                selectedFragment = new ShopPOSFragment(false);
                break;
            case R.id.walletSalesFragment:

                selectedFragment = new ShopSalesFragment(false);
                break;
        }
        ShopActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment3, selectedFragment).commit();
        currentFragment=selectedFragment;
        return true;
    };





    private void setupDefaultHomePage() {
        getSupportFragmentManager().beginTransaction().add(R.id.nav_host_fragment3, defaultHomeFragment).commit();
        currentFragment = defaultHomeFragment;
    }

//    public static void setUpTitle(){
//        if(currentFragment instanceof ShopProductsFragment){
//            actionBar.setDisplayShowTitleEnabled(true);
//
//            actionBar.setHomeButtonEnabled(true);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setTitle("Products");
//        }
//    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        // Get FragmentManager
        FragmentManager fm = getSupportFragmentManager();

        // Check if BackStack has some Fragments
        if (fm.getBackStackEntryCount() > 0) {
            // Pop previous Fragment
            fm.popBackStack();

        } // Check if doubleBackToExitPressed is true
//        else if (doubleBackToExitPressedOnce) {
//            super.onBackPressed();
//            backToast.cancel();
//            finishAffinity();
//        }
        else {
            Intent intent = new Intent(this, WalletHomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    private  void getProductsBackUp(){
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ShopActivity.this);
        databaseAccess.open();

        Call<ResponseBody> call = BuyInputsAPIClient
                .getInstance()
                .getBackup(
                        wallet_id
                );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String s = null;
                    try {
                        s = response.body().string();
                        if (s != null) {
                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray my_products = null;

                            my_products = jsonObject.getJSONArray("products");
                            if (my_products.length() > 0) {
                                Log.w("ProductSize", my_products.length() + "---------------------");
                                for (int i = 0; i < my_products.length(); i++) {

                                    boolean check = databaseAccess.addProduct(
                                            my_products.getJSONObject(i).getString("product_id"),
                                            my_products.getJSONObject(i).getString("product_name"),
                                            my_products.getJSONObject(i).getString("product_code"),
                                            my_products.getJSONObject(i).getString("product_category"),
                                            my_products.getJSONObject(i).getString("product_description"),
                                            my_products.getJSONObject(i).getString("product_buy_price"),
                                            my_products.getJSONObject(i).getString("product_sell_price"),
                                            my_products.getJSONObject(i).getString("product_stock"),
                                            my_products.getJSONObject(i).getString("product_supplier"),
                                            my_products.getJSONObject(i).getString("product_image"),
                                            my_products.getJSONObject(i).getString("product_weight_unit"),
                                            my_products.getJSONObject(i).getString("product_weight"));

                                    if (check) {
                                        Log.w("Products Insert", "Product Inserted Successfully");
                                    } else {
                                        Log.e("Product Insert", "product Insertion failed");

                                    }

                                }
                            } else {
                                Log.d("No Products", "Shop Has No Products Backed Up");

                            }


                        }

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.d("Error Occurred", "Error Occurred");
                    Log.d("Error response", String.valueOf(response));
                    Log.d("Error Code", String.valueOf(response.code()));

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Log.d("Error Occurred", "Error Occurred");

            }
        });
    }

}
