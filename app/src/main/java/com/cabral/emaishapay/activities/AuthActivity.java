package com.cabral.emaishapay.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import com.cabral.emaishapay.R;

public class AuthActivity extends AppCompatActivity {
    public static NavController navController;
    public static  Boolean is_firstTimeLoggin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.wallet_auth_container);
        navController = navHostFragment.getNavController();

        is_firstTimeLoggin=getIntent().getBooleanExtra("isFirstTimeLaunch",false);



    }


}