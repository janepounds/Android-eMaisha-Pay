package com.cabral.emaishapay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.cabral.emaishapay.AppExecutors;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.databinding.ActivityShopBinding;

import com.cabral.emaishapay.network.StartAppRequests;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

public class ShopActivity extends AppCompatActivity {

    public static ActionBar actionBar;
    ActivityShopBinding binding;
    public  static NavController navController;
    public  static BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(ShopActivity.this,R.layout.activity_shop);
        //setContentView(R.layout.activity_shop);
        bottomNavigationView=binding.navView;


        setSupportActionBar(binding.toolbarMain);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);



        binding.navView.setItemIconTintList(null);
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.shop_navigation_container);

        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.navView, navController);

        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        Log.d("SHOP ACTIVITY", "onVisibilityChanged: Keyboard visibility changed");
                        if (isOpen) {
                            Log.d("SHOP ACTIVITY", "onVisibilityChanged: Keyboard is open");
                            binding.navView.setVisibility(View.INVISIBLE);
                            Log.d("SHOP ACTIVITY", "onVisibilityChanged: NavBar got Invisible");
                        } else {
                            Log.d("SHOP ACTIVITY", "onVisibilityChanged: Keyboard is closed");
                            binding.navView.setVisibility(View.VISIBLE);
                            Log.d("SHOP ACTIVITY", "onVisibilityChanged: NavBar got Visible");
                        }
                    }
                });


        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if(ShopActivity.bottomNavigationView!=null ){

                    if(destination.getId()==R.id.shopPOSFragment ||  destination.getId()==R.id.shopProductsFragment
                            ||  destination.getId()==R.id.shopOrdersFragment ||  destination.getId()==R.id.shopSalesFragment ){

                        bottomNavigationView.setVisibility(View.VISIBLE);
                    }else{
                        bottomNavigationView.setVisibility(View.GONE);
                    }

                }

            }
        });

        AppExecutors.getInstance().NetworkIO().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        StartAppRequests.SyncProductData();
                    }
                }
        );


    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {

        // Check if BackStack has some Fragments
        if (navController.getCurrentDestination().getId()!=R.id.shopProductsFragment) {
            // Pop previous Fragment
            navController.popBackStack();

        }
        else {
            Intent intent = new Intent(this, WalletHomeActivity.class);
            startActivity(intent);
        }
    }

}
