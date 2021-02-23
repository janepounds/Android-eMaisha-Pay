package com.cabral.emaishapay.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.customs.NotificationBadger;
import com.cabral.emaishapay.fragments.WalletHomeFragment;
import com.cabral.emaishapay.fragments.buy_fragments.My_Addresses;
import com.cabral.emaishapay.fragments.buy_fragments.My_Cart;
import com.cabral.emaishapay.fragments.buy_fragments.My_Orders;
import com.cabral.emaishapay.fragments.buy_fragments.WalletBuyFragment;
import com.cabral.emaishapay.fragments.sell_fragment.SellFragment;
import com.cabral.emaishapay.utils.Utilities;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.graphics.drawable.DrawableWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.WrappedDrawable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

public class WalletBuySellActivity extends AppCompatActivity {

    Toolbar toolbar;
    public static ActionBar actionBar;
    public static BottomNavigationView bottomNavigationView;
    public Fragment currentFragment, defaultHomeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_buy_sell);


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


        defaultHomeFragment =new WalletBuyFragment(WalletBuySellActivity.this, getSupportFragmentManager());

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
            case R.id.walletBuyFragment:
                selectedFragment =defaultHomeFragment;
                break;
            case R.id.walletSellFragment:
                selectedFragment = new SellFragment(WalletBuySellActivity.this, getSupportFragmentManager());
                break;
            case R.id.walletOrdersFragment:
                selectedFragment = new My_Orders(false);
                break;
            case R.id.walletAddressesFragment:
                WalletBuySellActivity.bottomNavigationView.setVisibility(View.GONE);
                selectedFragment = new My_Addresses(false);
                break;
        }
        WalletBuySellActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment2, selectedFragment).commit();
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
//                        .add(R.id.nav_host_fragment2, fragment)
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                        .addToBackStack(getString(R.string.actionHome)).commit();
//            else
            fragmentManager.beginTransaction()
//                        .hide(currentFragment)
                    .replace(R.id.nav_host_fragment2, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(null).commit();

            WalletBuySellActivity.bottomNavigationView.setVisibility(View.GONE);
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