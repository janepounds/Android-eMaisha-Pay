package com.cabral.emaishapay.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.cabral.emaishapay.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_shop.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

class MerchantShopActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.activity_shop)
        setSupportActionBar(toolBarMain)

        actionBar!!.setDisplayShowTitleEnabled(true)
        actionBar!!.setHomeButtonEnabled(false)
        actionBar!!.setDisplayHomeAsUpEnabled(false)
        bottomNavigationView =navView
        navView.itemIconTintList = null
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.shop_navigation_container) as NavHostFragment
        navController = navHostFragment.navController

        NavigationUI.setupWithNavController(navView, navController)
        KeyboardVisibilityEvent.setEventListener(
                this
        ) { isOpen ->
            Log.d("SHOP ACTIVITY", "onVisibilityChanged: Keyboard visibility changed")
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

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.shopPOSFragment || destination.id == R.id.shopProductsFragment || destination.id == R.id.shopOrdersFragment || destination.id == R.id.shopSalesFragment) {
                navView.visibility = View.VISIBLE
            } else {
                navView.visibility = View.GONE
            }
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (navController.currentDestination!!.id != R.id.shopProductsFragment) {
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