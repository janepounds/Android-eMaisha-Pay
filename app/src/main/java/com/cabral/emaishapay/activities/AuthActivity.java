package com.cabral.emaishapay.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.databinding.ActivityAuthBinding;

public class AuthActivity extends AppCompatActivity {
    public static NavController navController;
    public static NavHostFragment navHostFragment;
    ActivityAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binding= DataBindingUtil.setContentView(AuthActivity.this, R.layout.activity_auth);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.wallet_auth_container);

        navController = navHostFragment.getNavController();

        Boolean goToFlash=getIntent().getBooleanExtra("flash",true);
        if(!goToFlash){  // Hostfragment
            NavInflater inflater = navController.getNavInflater();
            NavGraph graph = inflater.inflate(R.navigation.auth_navigation);
            graph.setStartDestination(R.id.loginFragment);

            navHostFragment.getNavController().setGraph(graph);

        }

    }

    public static NavController getNavController() {
        if(navController==null){
            navController = navHostFragment.getNavController();
        }

        return navController;
    }
}