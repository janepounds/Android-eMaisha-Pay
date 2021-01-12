package com.cabral.emaishapay.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.customs.NotificationBadger;
import com.cabral.emaishapay.fragments.buyandsell.My_Cart;
import com.cabral.emaishapay.utils.Utilities;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.graphics.drawable.DrawableWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.WrappedDrawable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class WalletBuySellActivity extends AppCompatActivity {

    Toolbar toolbar;
    public static ActionBar actionBar;
    public Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_buy_sell);


        toolbar = findViewById(R.id.main_Toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

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
            }

            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            // setupTitle();
        });



        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.walletBuyFragment, R.id.walletSellFragment, R.id.walletOrdersFragment, R.id.walletAddressesFragment)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment2);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
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
        // Inflate toolbar_menu Menu
        getMenuInflater().inflate(R.menu.cart_menu, menu);

        // Bind Menu Items
        MenuItem cartItem = menu.findItem(R.id.ic_cart_item);
        currentFragment = this.getSupportFragmentManager().getPrimaryNavigationFragment();

        cartItem.setActionView(R.layout.buy_inputs_animated_ic_cart);

        cartItem.getActionView().setOnClickListener(v -> {
            // Navigate to My_Cart Fragment
            Fragment fragment = new My_Cart();
            FragmentManager fragmentManager = getSupportFragmentManager();
//            if (currentFragment == null)
//                fragmentManager.beginTransaction()
//                        .add(R.id.nav_host_fragment, fragment)
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                        .addToBackStack(getString(R.string.actionHome)).commit();
//            else
            fragmentManager.beginTransaction()
//                        .hide(currentFragment)
                    .replace(R.id.nav_host_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(null).commit();
        });

        // Tint Menu Icons with the help of static method of Utilities class
//        Utilities.tintMenuIcon(DashboardActivity.this, languageItem, R.color.white);
        Utilities.tintMenuIcon(WalletBuySellActivity.this, cartItem, R.color.white);

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