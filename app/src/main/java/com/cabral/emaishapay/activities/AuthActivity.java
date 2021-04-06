package com.cabral.emaishapay.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import com.cabral.emaishapay.R;

public class AuthActivity extends AppCompatActivity {
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.wallet_auth_container);
        navController = navHostFragment.getNavController();

        Boolean is_firstTimeLoggin=getIntent().getBooleanExtra("isFirstTimeLaunch",false);

        if(is_firstTimeLoggin){
            navController.navigate(R.id.action_loginFragment_to_onBoardingFragment2);
        }

    }


}