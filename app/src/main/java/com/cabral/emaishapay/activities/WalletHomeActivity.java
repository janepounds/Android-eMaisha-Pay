package com.cabral.emaishapay.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cabral.emaishapay.DailogFragments.AgentCustomerBalanceInquiry;
import com.cabral.emaishapay.DailogFragments.AgentCustomerDeposits;
import com.cabral.emaishapay.DailogFragments.AgentCustomerFundsTransfer;
import com.cabral.emaishapay.DailogFragments.AgentCustomerWithdraw;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.fragments.WalletAccountFragment;
import com.cabral.emaishapay.fragments.WalletHomeFragment;
import com.cabral.emaishapay.DailogFragments.DepositMoneyMobile;
import com.cabral.emaishapay.DailogFragments.DepositMoneyVisa;
import com.cabral.emaishapay.DailogFragments.DepositMoneyVoucher;
import com.cabral.emaishapay.fragments.buy_fragments.WalletBuyFragment;
import com.cabral.emaishapay.models.order_model.PostOrder;
import com.cabral.emaishapay.network.StartAppRequests;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class WalletHomeActivity extends AppCompatActivity{
    private static final String TAG = "WalletHomeActivity";
    private Context context;
    public static FragmentManager fm;
    public Fragment currentFragment;
    public static ActionBar actionBar;
    Fragment selectedFragment = null;

    public static final String PREFERENCES_WALLET_USER_ID = "walletuserId";
    public static final String PREFERENCES_USER_PIN = "";
    public static final String PREFERENCES_PREPIN_ENCRYPTION = "12";

    public static final String PREFERENCES_FILE_NAME = "UserInfo";
    public static final String FARM_NAME_PREFERENCES_ID = "farmname";
    public static final String STREET_PREFERENCES_ID = "addressStreet";
    public static final String CITY_PREFERENCES_ID = "addressCityOrTown";
    public static final String USER_DEFAULT_ADDRESS_PREFERENCES_ID = "userDefaultAddressID";
    public static final String COUNTRY_PREFERENCES_ID = "addressCountry";
    public static final String PREFERENCES_FIRST_NAME = "firstname";
    public static final String PREFERENCES_LAST_NAME = "lastname";
    public static String RETRIEVED_USER_ID = "";
    public static final String PREFERENCES_USER_EMAIL = "email";
    public static final String PREFERENCES_PHONE_NUMBER = "phoneNumber";

    public static final String PREFERENCES_FIREBASE_TOKEN_SUBMITTED = "tokenSubmitted";
    public static final String PREFERENCES_USER_BACKED_UP = "userBackedUp";
    public static final String PREFERENCES_USER_PASSWORD = "password";

    public static final String PREFERENCE_ACCOUNT_PERSONAL_DOB ="dob";
    public static final String PREFERENCE_ACCOUNT_PERSONAL_GENDER = "gender";
    public static final String PREFERENCE_ACCOUNT_PERSONAL_NOK ="next_of_kin";
    public static final String PREFERENCE_ACCOUNT_PERSONAL_NOK_CONTACT ="next_of_kin_contact";
    public static final String PREFERENCE_ACCOUNT_PERSONAL_PIC ="pic";

    public static final String PREFERENCE_ACCOUNT_ID_TYPE ="idtype";
    public static final String PREFERENCE_ACCOUNT_ID_NUMBER ="idNumber";
    public static final String PREFERENCE_ACCOUNT_ID_EXPIRY_DATE ="expiryDate";
    public static final String PREFERENCE_ACCOUNT_ID_FRONT ="front";
    public static final String PREFERENCE_ACCOUNT_ID_BACK ="back";

    public static final String PREFERENCE_ACCOUNT_EMPLOYER ="employer";
    public static final String PREFERENCE_ACCOUNT_DESIGNATION ="designation";
    public static final String PREFERENCE_ACCOUNT_LOCATION ="location";
    public static final String PREFERENCE_ACCOUNT_EMPLOYEE_ID ="employeeId";

    public static final String PREFERENCE_ACCOUNT_BUSINESS_NAME ="business_name";
    public static final String PREFERENCE_ACCOUNT_BUSINESS_LOCATION ="business_location";
    public static final String PREFERENCE_ACCOUNT_REG_NO ="regNo";
    public static final String PREFERENCE_ACCOUNT_LICENSE_NUMBER ="license_number";
    public static final String PREFERENCE_ACCOUNT_TRADE_LICENSE ="trade_license";
    public static final String PREFERENCE_ACCOUNT_REG_CERTIFICATE ="reg_certificate";


    public static final double PREFERENCE_WALLET_BALANCE = 0;


    public static PostOrder postOrder = new PostOrder();
    public static BottomNavigationView bottomNavigationView;

    Fragment defaultHomeFragment;
    WalletBuyFragment buysellFragment;
    WalletAccountFragment walletAccountFragment;
    private boolean doubleBackToExitPressedOnce = false;
    private Toast backToast;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_home);
        //setupDefaultHomePage();
        context = getApplicationContext();
        fm = getSupportFragmentManager();

        defaultHomeFragment= new WalletHomeFragment();
        setUpNavigation();

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
            }

            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
           // setupTitle();
        });
        if (!getPreferences(PREFERENCES_FIREBASE_TOKEN_SUBMITTED, WalletHomeActivity.this).equals("yes")) {
            getAppToken();
        }


    }

    public void setUpNavigation() {
         bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemIconTintList(null);

        FragmentManager fragmentManager = getSupportFragmentManager();
        currentFragment=fragmentManager.getPrimaryNavigationFragment();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {//walletAccountFragment
                switch (item.getItemId()){
                    case R.id.WalletBuyFragment :
                        bottomNavigationView.postDelayed(() -> {
                            startActivity(new Intent(WalletHomeActivity.this, WalletBuySellActivity.class));
                        }, 300);

                        return true;

                    case R.id.walletAccountFragment :
                        if(walletAccountFragment == null) {
                            walletAccountFragment = new WalletAccountFragment();
                            if (currentFragment == null)
                                fragmentManager.beginTransaction()
                                        .add(R.id.nav_host_fragment, walletAccountFragment)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                        .commit();
                            else
                                fragmentManager.beginTransaction()
                                        .hide(currentFragment)
                                        .add(R.id.nav_host_fragment, walletAccountFragment)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                        .commit();
                        }else {
                            fragmentManager.beginTransaction().hide(currentFragment).show(walletAccountFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                        }
                        currentFragment = walletAccountFragment;
                        return true;

                    case R.id.walletHomeFragment :
                        if(defaultHomeFragment == null) {
                            defaultHomeFragment = new WalletHomeFragment();
                            if (currentFragment == null)
                                fragmentManager.beginTransaction()
                                        .add(R.id.nav_host_fragment, defaultHomeFragment)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                        .commit();
                            else
                                fragmentManager.beginTransaction()
                                        .hide(currentFragment)
                                        .add(R.id.nav_host_fragment, defaultHomeFragment)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                        .commit();
                        }else {
                            fragmentManager.beginTransaction().hide(currentFragment).show(defaultHomeFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                        }
                        currentFragment = defaultHomeFragment;
                        return true;

                        default:
                            fragmentManager.beginTransaction()
                                    .add(R.id.nav_host_fragment, defaultHomeFragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .commit();
                            return true;


                }
//                return false;
            }
        });



    }

    public static void startHome(Context context) {
        try {
            Intent home = new Intent(context, WalletHomeActivity.class);
            context.startActivity(home);
        } catch (Exception e) {
            Log.e("Intent start Error: ", e.getMessage());
        }

    }

    public static String getPreferences(String key, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_NAME,
                MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static void savePreferences(String key, String value, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_NAME,
                0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void openAddMobileMoney(View view) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment depositDialog = new DepositMoneyMobile(this, WalletHomeFragment.balance);
        depositDialog.show(ft, "dialog");
    }

    public void openAddMoneyVisa(View view) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment depositDialog = new DepositMoneyVisa(this, WalletHomeFragment.balance, getSupportFragmentManager());
        depositDialog.show(ft, "dialog");
    }

    public void openAddMoneyVoucher(View view) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment depositDialog = new DepositMoneyVoucher(this, WalletHomeFragment.balance);
        depositDialog.show(ft, "dialog");
    }
    public void openAgentCustomerDeposit(View view) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment depositDialog = new AgentCustomerDeposits();
        depositDialog.show(ft, "dialog");
    }
    public void openAgentCustomerBalanceInquiry(View view) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment balanceInquiryDialog = new AgentCustomerBalanceInquiry();
        balanceInquiryDialog.show(ft, "dialog");
    }
    public void openAgentCustomerFundsTransfer(View view) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment fundsTransferDialog = new AgentCustomerFundsTransfer();
        fundsTransferDialog.show(ft, "dialog");
    }

    public void openAgentCustomerWithdraw(View view) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment customerWithdrawDialog = new AgentCustomerWithdraw();
        customerWithdrawDialog.show(ft, "dialog");
    }
    public void openAgentCustomerAccountOpening(View view) {
        Intent intent = new Intent(this, AccountOpeningActivity.class);
        startActivity(intent);
    }
    public void editMyProduce(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

        View addProduceDialog = View.inflate(context,R.layout.add_produce_dialog,null);
        Dialog dialog = builder.create();

        ImageView close = addProduceDialog.findViewById(R.id.produce_close);
        Spinner name = addProduceDialog.findViewById(R.id.produce_name);
        EditText variety = addProduceDialog.findViewById(R.id.produce_variety);
        Spinner quantityUnit = addProduceDialog.findViewById(R.id.produce_quantity_unit);
        EditText quantity = addProduceDialog.findViewById(R.id.produce_quantity);
        TextView quantityMeasure = addProduceDialog.findViewById(R.id.produce_quantity_measure);
        EditText price = addProduceDialog.findViewById(R.id.produce_price);
        CardView cardView = addProduceDialog.findViewById(R.id.image_view_holder);
        ImageView image = addProduceDialog.findViewById(R.id.produce_image);
        Button submit = addProduceDialog.findViewById(R.id.produce_submit_button);
        LinearLayout layoutSubmitBtn = addProduceDialog.findViewById(R.id.layout_submit_button);
        LinearLayout layoutEditBtns = addProduceDialog.findViewById(R.id.edit_buttons);

        layoutSubmitBtn.setVisibility(View.GONE);
        layoutEditBtns.setVisibility(View.VISIBLE);

        close.setOnClickListener(view1 -> dialog.dismiss());
        builder.setView(addProduceDialog);
        builder.setCancelable(false);


        dialog.show();

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
        else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            backToast.cancel();
            finishAffinity();
        }
        else {
            if (currentFragment == defaultHomeFragment)
                new AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                WalletHomeActivity.super.onBackPressed();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            else
                showHomePage();
        }
    }


    public void getAppToken() {
        FirebaseApp.initializeApp(WalletHomeActivity.this);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            // Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d(TAG, "onComplete-token: "+token);
                        StartAppRequests.RegisterDeviceForFCM(getApplicationContext());
//                        sendFirebaseToken(token, WalletHomeActivity.this);


                    }
                });

    }
    public void disableNavigation() {
      bottomNavigationView.setVisibility(View.GONE);


    }



    private void showHomePage() {
        getSupportFragmentManager().beginTransaction().hide(currentFragment).show(defaultHomeFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, defaultHomeFragment).commit();
        currentFragment = defaultHomeFragment;

       // actionBar.setTitle(getString(R.string.app_name));
    }

}
