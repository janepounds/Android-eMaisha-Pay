package com.cabral.emaishapay.activities

import android.os.Bundle
import com.cabral.emaishapay.R
import com.cabral.emaishapay.fragments.buy_fragments.Shipping_Address
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import androidx.navigation.NavController
import android.content.Intent
import android.annotation.SuppressLint
import com.cabral.emaishapay.fragments.buy_fragments.My_Cart
import android.view.animation.Animation
import android.graphics.drawable.LayerDrawable
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawableWrapper
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.WrappedDrawable
import androidx.fragment.app.Fragment
import com.cabral.emaishapay.customs.NotificationBadger
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.cabral.emaishapay.models.order_model.PostOrder
import kotlinx.android.synthetic.main.activity_wallet_buy_sell.*


class WalletBuySellActivity : AppCompatActivity() {
    var toolbar: Toolbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.activity_wallet_buy_sell)
        setSupportActionBar(mainToolbar)
        Companion.actionBar = supportActionBar
        Companion.actionBar!!.setDisplayShowTitleEnabled(false)
        Companion.actionBar!!.setHomeButtonEnabled(false)
        Companion.actionBar!!.setDisplayHomeAsUpEnabled(false)
        bottomNavigationView = navView
        // Handle ToolbarNavigationClickListener with OnBackStackChangedListener
        supportFragmentManager.addOnBackStackChangedListener {


            // Check BackStackEntryCount of FragmentManager
            if (supportFragmentManager.backStackEntryCount <= 0) {
                // Set DrawerToggle Indicator and default ToolbarNavigationClickListener
                Companion.actionBar!!.setDisplayShowTitleEnabled(false)
                if (currentFragment is Shipping_Address) bottomNavigationView!!.visibility = View.GONE else bottomNavigationView!!.visibility = View.VISIBLE
            }
            Companion.actionBar!!.setHomeButtonEnabled(true)
            Companion.actionBar!!.setDisplayHomeAsUpEnabled(true)
        }


//        defaultHomeFragment = new WalletBuyFragment(WalletBuySellActivity.this, getSupportFragmentManager());
        navView.itemIconTintList = null
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment2) as NavHostFragment?
        navController = navHostFragment!!.navController
        NavigationUI.setupWithNavController(navView, navController!!)
        //        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        KeyboardVisibilityEvent.setEventListener(
                this
        ) { isOpen ->
            Log.d("SHOP ACTIVITY", "onVisibilityChanged: Keyboard visibility changed")
            if (navView.visibility == View.VISIBLE) {
                if (isOpen) {
                    Log.d("SHOP ACTIVITY", "onVisibilityChanged: Keyboard is open")
                   navView.visibility = View.INVISIBLE
                    Log.d("SHOP ACTIVITY", "onVisibilityChanged: NavBar got Invisible")
                } else {
                    Log.d("SHOP ACTIVITY", "onVisibilityChanged: Keyboard is closed")
                    navView.visibility = View.VISIBLE
                    Log.d("SHOP ACTIVITY", "onVisibilityChanged: NavBar got Visible")
                }
            }
        }
        //        setupDefaultHomePage();
        navController!!.addOnDestinationChangedListener { controller, destination, arguments ->
            if (bottomNavigationView != null) {
                if (destination.id == R.id.walletSellFragment || destination.id == R.id.walletBuyFragment || destination.id == R.id.walletOrdersFragment || destination.id == R.id.walletAddressesFragment) {
                    bottomNavigationView!!.visibility = View.VISIBLE
                } else {
                    bottomNavigationView!!.visibility = View.GONE
                }
            }
        }
    }

    //    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = item -> {
    //        Fragment selectedFragment = null;
    //        switch (item.getItemId()) {
    //            case R.id.walletBuyFragment:
    //                selectedFragment =defaultHomeFragment;
    //                break;
    //            case R.id.walletSellFragment:
    //                selectedFragment = new SellFragment(WalletBuySellActivity.this, getSupportFragmentManager());
    //                break;
    //            case R.id.walletOrdersFragment:
    //                selectedFragment = new My_Orders(false);
    //                break;
    //            case R.id.walletAddressesFragment:
    //                WalletBuySellActivity.bottomNavigationView.setVisibility(View.GONE);
    //                selectedFragment = new My_Addresses(false);
    //                break;
    //        }
    //        WalletBuySellActivity.bottomNavigationView.setVisibility(View.VISIBLE);
    //        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment2, selectedFragment).commit();
    //        currentFragment=selectedFragment;
    //        return true;
    //    };
    //
    //    private void setupDefaultHomePage() {
    //        getSupportFragmentManager().beginTransaction().add(R.id.nav_host_fragment2, defaultHomeFragment).commit();
    //        currentFragment = defaultHomeFragment;
    //    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {

        // Check if BackStack has some Fragments
        if (navController!!.currentDestination!!.id != R.id.walletBuyFragment) {
            // Pop previous Fragment
            navController!!.popBackStack()
        } else {
            val intent = Intent(this, WalletHomeActivity::class.java)
            startActivity(intent)
        }
        if (navController!!.currentDestination!!.id == R.id.walletOrdersFragment) {
            val intent = Intent(this, WalletHomeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate toolbar_menu Menu
        menuInflater.inflate(R.menu.cart_menu, menu)

        // Bind Menu Items
        val cartItem = menu.findItem(R.id.ic_cart_item)
        currentFragment = this.supportFragmentManager.primaryNavigationFragment
        cartItem.setActionView(R.layout.buy_inputs_animated_ic_cart)
        cartItem.actionView.setOnClickListener { v: View? ->
            // Navigate to My_Cart Fragment
            if (navController!!.currentDestination!!.id == R.id.walletBuyFragment) {
                navController!!.navigate(R.id.action_walletBuyFragment_to_myCart)
            } else if (navController!!.currentDestination!!.id == R.id.productDescription) {
                navController!!.navigate(R.id.action_productDescription_to_myCart)
            }
            bottomNavigationView!!.visibility = View.GONE
        }

        // Tint Menu Icons with the help of static method of Utilities class
//        Utilities.tintMenuIcon(DashboardActivity.this, languageItem, R.color.white);
        //Utilities.tintMenuIcon(WalletBuySellActivity.this, cartItem, R.color.white);
        return true
    }

    @SuppressLint("RestrictedApi")
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val cartItem = menu.findItem(R.id.ic_cart_item)
        // Get No. of Cart Items with the static method of My_Cart Fragment
        val cartSize = My_Cart.getCartSize()


        // if Cart has some Items
        if (cartSize > 0) {

            // Animation for cart_menuItem
            val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.shake_icon)
            animation.repeatMode = Animation.REVERSE
            animation.repeatCount = 1
            cartItem.actionView.startAnimation(animation)
            cartItem.actionView.animation = null
            var icon: LayerDrawable? = null
            var drawable = cartItem.icon
            if (drawable is DrawableWrapper) {
                drawable = drawable.wrappedDrawable
            } else if (drawable is WrappedDrawable) {
                drawable = (drawable as WrappedDrawable).wrappedDrawable
            }
            if (drawable is LayerDrawable) {
                icon = drawable
            } else if (drawable is DrawableWrapper) {
                val wrapper = drawable
                if (wrapper.wrappedDrawable is LayerDrawable) {
                    icon = wrapper.wrappedDrawable as LayerDrawable
                }
            }

//                icon = (LayerDrawable) drawable;


            // Set BadgeCount on Cart_Icon with the static method of NotificationBadger class
            if (icon != null) NotificationBadger.setBadgeCount(this, icon, cartSize.toString())
        } else {
            // Set the Icon for Empty Cart
            cartItem.setIcon(R.drawable.ic_cart_empty)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    companion object {
        var actionBar: ActionBar? = null
        @JvmField
        var bottomNavigationView: BottomNavigationView? = null
        var currentFragment: Fragment? = null
        @JvmField
        var postOrder = PostOrder()
        @JvmField
        var navController: NavController? = null
    }
}