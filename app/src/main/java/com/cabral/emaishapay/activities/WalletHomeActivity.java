package com.cabral.emaishapay.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.graphics.drawable.DrawableWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.WrappedDrawable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.cabral.emaishapay.DailogFragments.AgentCustomerBalanceInquiry;
import com.cabral.emaishapay.DailogFragments.AgentCustomerDeposits;
import com.cabral.emaishapay.DailogFragments.AgentCustomerFundsTransfer;
import com.cabral.emaishapay.DailogFragments.AgentCustomerWithdraw;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.NotificationBadger;
import com.cabral.emaishapay.fragments.WalletHomeFragment;
import com.cabral.emaishapay.DailogFragments.DepositMoneyMobile;
import com.cabral.emaishapay.DailogFragments.DepositMoneyVisa;
import com.cabral.emaishapay.DailogFragments.DepositMoneyVoucher;
import com.cabral.emaishapay.fragments.buyandsell.Category_Products;
import com.cabral.emaishapay.fragments.buyandsell.CheckoutFinal;
import com.cabral.emaishapay.fragments.buyandsell.My_Addresses;
import com.cabral.emaishapay.fragments.buyandsell.My_Cart;
import com.cabral.emaishapay.fragments.buyandsell.My_Orders;
import com.cabral.emaishapay.fragments.buyandsell.Nearby_Merchants;
import com.cabral.emaishapay.fragments.buyandsell.PaymentMethodsFragment;
import com.cabral.emaishapay.fragments.buyandsell.Product_Description;
import com.cabral.emaishapay.fragments.buyandsell.Shipping_Address;
import com.cabral.emaishapay.fragments.buyandsell.Thank_You;
import com.cabral.emaishapay.fragments.buyandsell.WalletBuySellFragment;
import com.cabral.emaishapay.models.order_model.PostOrder;
import com.cabral.emaishapay.network.StartAppRequests;
import com.cabral.emaishapay.utils.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
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
    private boolean doubleBackToExitPressedOnce = false;
    private Toast backToast;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_home);
//        setupDefaultHomePage();
        context = getApplicationContext();
        fm = getSupportFragmentManager();

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

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;

        NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.getNavController());
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
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            backToast.cancel();
            finishAffinity();
        } else {
            this.doubleBackToExitPressedOnce = true;
            backToast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();

            new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }


    public void getAppToken() {
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

//    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = item -> {
//        Fragment selectedFragment = null;
//
//        switch (item.getItemId()) {
//            case R.id.walletBuySellFragment:
//                selectedFragment = new WalletBuySellFragment(WalletHomeActivity.this, getSupportFragmentManager());
//                break;
////            case R.id.page_2:
////                selectedFragment = new OffersFragment();
////                break;
////            case R.id.page_3:
////                selectedFragment = new AccountFragment();
////                break;
//        }
//
//        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, selectedFragment).commit();
//        return true;
//    };
    public void setupTitle() {
        Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (currentFrag instanceof My_Cart) {
            actionBar.setTitle(getString(R.string.actionCart));
            WalletHomeActivity.bottomNavigationView.setVisibility(View.GONE);
        } else if (currentFrag instanceof Shipping_Address) {
            actionBar.setTitle(getString(R.string.shipping_address));
            WalletHomeActivity.bottomNavigationView.setVisibility(View.GONE);
        } else if (currentFrag instanceof Nearby_Merchants) {
            actionBar.setTitle(getString(R.string.nearby_merchants));
            WalletHomeActivity.bottomNavigationView.setVisibility(View.GONE);
        } else if (currentFrag instanceof My_Orders) {
            actionBar.setTitle(getString(R.string.actionOrders));
            WalletHomeActivity.bottomNavigationView.setVisibility(View.GONE);
        } else if (currentFrag instanceof My_Addresses) {
            actionBar.setTitle(getString(R.string.actionAddresses));
            WalletHomeActivity.bottomNavigationView.setVisibility(View.GONE);
        } else if (currentFrag instanceof WalletBuySellFragment) {
            actionBar.setDisplayShowTitleEnabled(false);
            WalletHomeActivity.bottomNavigationView.setVisibility(View.GONE);
        } else if (currentFrag instanceof Category_Products) {
            WalletHomeActivity.bottomNavigationView.setVisibility(View.GONE);
        } else if (currentFrag instanceof PaymentMethodsFragment) {
            actionBar.setTitle(getString(R.string.payment_methods));
            WalletHomeActivity.bottomNavigationView.setVisibility(View.GONE);
        } else if (currentFrag instanceof Thank_You) {
            actionBar.setTitle(getString(R.string.order_confirmed));
            WalletHomeActivity.bottomNavigationView.setVisibility(View.GONE);
        } else if (currentFrag instanceof Product_Description) {
            actionBar.setTitle(getString(R.string.product_description));
            WalletHomeActivity.bottomNavigationView.setVisibility(View.GONE);
        } else if (currentFrag instanceof CheckoutFinal) {
            actionBar.setTitle(getString(R.string.checkout));
            WalletHomeActivity.bottomNavigationView.setVisibility(View.GONE);
        }

    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        Fragment fragment;
//        FragmentManager fragmentManager = getSupportFragmentManager();
//
//        switch (item.getItemId()) {
//
//            case android.R.id.home:
//
//                if (currentFragment == defaultHomeFragment)
//
//                    new AlertDialog.Builder(this)
//                            .setMessage("Are you sure you want to exit?")
//                            .setCancelable(false)
//                            .setPositiveButton("Yes", (dialog, id) -> finishAffinity())
//                            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
//                            .show();
//
//                else if (fragmentManager.getBackStackEntryCount() > 0) {
//                    // Pop previous Fragment
//                    fragmentManager.popBackStack();
//                } else
//                    showHomePage();
//
//                break;
//            case R.id.ic_cart_item:
//
//                // Navigate to My_Cart Fragment
//                fragment = new My_Cart();
//                fragmentManager.beginTransaction()
//                        .hide(currentFragment)
//                        .add(R.id.nav_host_fragment, fragment)
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                        .addToBackStack(getString(R.string.actionHome)).commit();
//                break;
//
//
//
//
//            default:
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    @Override
//    public void onBackPressed() {
//        // Get FragmentManager
//        FragmentManager fm = getSupportFragmentManager();
//
//        if (fm.getBackStackEntryCount() > 0) {
//            // Pop previous Fragment
//            fm.popBackStack();
//        } else {
//
//            if (currentFragment == defaultHomeFragment)
//
//                new AlertDialog.Builder(this)
//                        .setMessage("Are you sure you want to exit?")
//                        .setCancelable(false)
//                        .setPositiveButton("Yes", (dialog, id) -> finishAffinity())
//                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
//                        .show();
//            else
//                showHomePage();
//        }
//    }

    private void showHomePage() {
//        getSupportFragmentManager().beginTransaction().hide(currentFragment).show(defaultHomeFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, defaultHomeFragment).commit();
        currentFragment = defaultHomeFragment;

       // actionBar.setTitle(getString(R.string.app_name));
    }

       public static void sendFirebaseToken(String token, final Context context) {
           ///**************** RETROFIT IMPLEMENTATION******************************//////////





       }
//        final AsyncHttpClient client = new AsyncHttpClient();
//        final RequestParams params = new RequestParams();
//        // client.addHeader("Authorization","Bearer "+CropWalletAuthActivity.WALLET_ACCESS_TOKEN);
//        params.put("email", DashboardActivity.getPreferences(DashboardActivity.PREFERENCES_USER_EMAIL, context));
//        params.put("firebaseToken", token);
//
//        Handler mainHandler = new Handler(Looper.getMainLooper());
//        Runnable myRunnable = new Runnable() {
//            @Override
//            public void run() {
//                client.post(ApiPaths.CROP_SEND_FIREBASE_TOKEN, params, new AsyncHttpResponseHandler() {
//
//                    @Override
//                    public void onStart() {
//
//                    }
//
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                        savePreferences(PREFERENCES_FIREBASE_TOKEN_SUBMITTED, "yes", context);
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                        if (responseBody != null) {
//                            Log.e("info", new String(String.valueOf(responseBody)));
//                        } else {
//                            Log.e("info", "Something got very very wrong");
//                        }
//                    }
//
//
//                });
//            }
//        };
//        mainHandler.post(myRunnable);
//
//    }

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
//                        .add(R.id.nav_host_fragment, fragment)
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                        .addToBackStack(getString(R.string.actionHome)).commit();
//            else
                fragmentManager.beginTransaction()
//                        .hide(currentFragment)
                        .replace(R.id.nav_host_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null).commit();
        });

        // Tint Menu Icons with the help of static method of Utilities class
//        Utilities.tintMenuIcon(DashboardActivity.this, languageItem, R.color.white);
        Utilities.tintMenuIcon(WalletHomeActivity.this, cartItem, R.color.white);

        return true;
    }

    //*********** Prepares the OptionsMenu of Toolbar ********//

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
