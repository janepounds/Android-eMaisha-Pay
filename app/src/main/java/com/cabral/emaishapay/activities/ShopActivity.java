package com.cabral.emaishapay.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.customs.NotificationBadger;
import com.cabral.emaishapay.fragments.buy_fragments.My_Cart;
import com.cabral.emaishapay.fragments.shop_fragment.ShopOrdersFragment;
import com.cabral.emaishapay.fragments.shop_fragment.ShopPOSFragment;
import com.cabral.emaishapay.fragments.shop_fragment.ShopProductsFragment;
import com.cabral.emaishapay.fragments.shop_fragment.ShopSalesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.graphics.drawable.DrawableWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.WrappedDrawable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class ShopActivity extends AppCompatActivity {
    Toolbar toolbar;
    public static ActionBar actionBar;
    public static BottomNavigationView bottomNavigationView;
    public Fragment currentFragment, defaultHomeFragment;

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
        actionBar.setTitle("Products");

        // Handle ToolbarNavigationClickListener with OnBackStackChangedListener
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {

            // Check BackStackEntryCount of FragmentManager
            if (getSupportFragmentManager().getBackStackEntryCount() <= 0) {
                // Set DrawerToggle Indicator and default ToolbarNavigationClickListener
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setHomeButtonEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(false);
                WalletBuySellActivity.bottomNavigationView.setVisibility(View.VISIBLE);
            }

            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            // setupTitle();
        });


        defaultHomeFragment =new ShopProductsFragment(ShopActivity.this, getSupportFragmentManager());

        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
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
                WalletBuySellActivity.bottomNavigationView.setVisibility(View.GONE);
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


}