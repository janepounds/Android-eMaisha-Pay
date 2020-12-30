package com.cabral.emaishapay.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.fragments.PersonalDetailsFragment;

public class AccountOpeningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_opening);

//        getActionBar().setHomeButtonEnabled(true); //for back button
//        getActionBar().setDisplayHomeAsUpEnabled(true);//for back button
//        getActionBar().setTitle("Account Opening");
        openFragment(new PersonalDetailsFragment());


    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.open_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //for back button
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//
//            case android.R.id.home:
//                // app icon in action bar clicked; goto parent activity.
//                this.finish();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (getFragmentManager().getBackStackEntryCount() == 1) {
//            moveTaskToBack(false);
//        } else {
//            super.onBackPressed();
//        }
//    }
}