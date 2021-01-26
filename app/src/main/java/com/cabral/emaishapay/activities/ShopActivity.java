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


        toolbar = findViewById(R.id.main_Toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);

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
        getSupportFragmentManager().beginTransaction().add(R.id.nav_host_fragment2, defaultHomeFragment).commit();
        currentFragment = defaultHomeFragment;
    }

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


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem cartItem = menu.findItem(R.id.ic_cart_item);
        // Get No. of Cart Items with the static method of My_Cart Fragment
        int cartSize = My_Cart.getCartSize();


        // if Cart has some Items
        if (cartSize > 0) {

            // Animation for cart_menuItem
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_icon);
            animation.setRepeatMode(Animation.REVERSE);
            animation.setRepeatCount(1);

            cartItem.getActionView().startAnimation(animation);
            cartItem.getActionView().setAnimation(null);


            LayerDrawable icon = null;
            Drawable drawable = cartItem.getIcon();

            if (drawable instanceof DrawableWrapper) {
                drawable = ((DrawableWrapper) drawable).getWrappedDrawable();
            } else if (drawable instanceof WrappedDrawable) {
                drawable = ((WrappedDrawable) drawable).getWrappedDrawable();
            }


            if (drawable instanceof LayerDrawable) {
                icon = (LayerDrawable) drawable;
            } else if (drawable instanceof DrawableWrapper) {
                DrawableWrapper wrapper = (DrawableWrapper) drawable;
                if (wrapper.getWrappedDrawable() instanceof LayerDrawable) {
                    icon = (LayerDrawable) wrapper.getWrappedDrawable();
                }
            }

//                icon = (LayerDrawable) drawable;


            // Set BadgeCount on Cart_Icon with the static method of NotificationBadger class
            if (icon != null)
                NotificationBadger.setBadgeCount(this, icon, String.valueOf(cartSize));


        } else {
            // Set the Icon for Empty Cart
            cartItem.setIcon(R.drawable.ic_cart_empty);
        }


        return super.onPrepareOptionsMenu(menu);
    }
}
