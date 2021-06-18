package com.cabral.emaishapay.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.cabral.emaishapay.R
import com.cabral.emaishapay.databinding.ActivityShopBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

class MerchantShopActivity : AppCompatActivity() {

    var actionBar: ActionBar? = null
    private lateinit var binding: ActivityShopBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MerchantShopActivity, R.layout.activity_shop)
        //setContentView(R.layout.activity_shop);
        bottomNavigationView = binding.navView
        setSupportActionBar(binding.toolbarMain)
        actionBar = supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(true)
        actionBar!!.setHomeButtonEnabled(false)
        actionBar!!.setDisplayHomeAsUpEnabled(false)
        binding.navView.itemIconTintList = null
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.shop_navigation_container) as NavHostFragment?
        navController = navHostFragment!!.navController
        NavigationUI.setupWithNavController(binding.navView, navController)
        KeyboardVisibilityEvent.setEventListener(
                this
        ) { isOpen ->
            Log.d("SHOP ACTIVITY", "onVisibilityChanged: Keyboard visibility changed")
            if (isOpen) {
                Log.d("SHOP ACTIVITY", "onVisibilityChanged: Keyboard is open")
                binding.navView.visibility = View.INVISIBLE
                Log.d("SHOP ACTIVITY", "onVisibilityChanged: NavBar got Invisible")
            } else {
                Log.d("SHOP ACTIVITY", "onVisibilityChanged: Keyboard is closed")
                binding.navView.visibility = View.VISIBLE
                Log.d("SHOP ACTIVITY", "onVisibilityChanged: NavBar got Visible")
            }
        }
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (ShopActivity.bottomNavigationView != null) {
                if (destination.id == R.id.shopPOSFragment || destination.id == R.id.shopProductsFragment || destination.id == R.id.shopOrdersFragment || destination.id == R.id.shopSalesFragment) {
                    bottomNavigationView.visibility = View.VISIBLE
                } else {
                    bottomNavigationView.visibility = View.GONE
                }
            }
        }

//        AppExecutors.getInstance().NetworkIO().execute(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        StartAppRequests.SyncProductData();
//                    }
//                }
//        );
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {

        // Check if BackStack has some Fragments
        if (navController.currentDestination!!.id != R.id.shopProductsFragment) {
            // Pop previous Fragment
            navController.popBackStack()
        } else {
            val intent = Intent(this, WalletHomeActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        @JvmStatic
        lateinit var navController: NavController
        @JvmStatic
        lateinit var bottomNavigationView: BottomNavigationView
    }
}