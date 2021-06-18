package com.cabral.emaishapay.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import com.cabral.emaishapay.AppExecutors;
import com.cabral.emaishapay.DailogFragments.AgentCustomerBalanceInquiry;
import com.cabral.emaishapay.DailogFragments.AgentCustomerDeposits;
import com.cabral.emaishapay.DailogFragments.AgentCustomerFundsTransfer;
import com.cabral.emaishapay.DailogFragments.AgentCustomerWithdraw;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.app.MyAppPrefsManager;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.fragments.wallet_fragments.TokenAuthFragment;
import com.cabral.emaishapay.fragments.wallet_fragments.WalletHomeFragment;
import com.cabral.emaishapay.DailogFragments.DepositMoneyMobile;
import com.cabral.emaishapay.DailogFragments.DepositMoneyVisa;
import com.cabral.emaishapay.DailogFragments.DepositMoneyVoucher;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.models.CardSpinnerItem;
import com.cabral.emaishapay.models.banner_model.BannerDetails;
import com.cabral.emaishapay.network.Connectivity;
import com.cabral.emaishapay.network.DataRepository;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient;
import com.cabral.emaishapay.network.db.entities.EcProduct;
import com.cabral.emaishapay.services.SyncService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.VIBRATE;
import static android.Manifest.permission_group.CAMERA;

public class WalletHomeActivity extends AppCompatActivity{

    public static String WALLET_ACCESS_TOKEN =null;
    private static final String TAG = "WalletHomeActivity";
    public static List<BannerDetails> Banners= new ArrayList<>();
    private static Context context;
    public static FragmentManager fm;
    public  int currentFragment;
    public static ActionBar actionBar;
    DialogLoader dialogLoader;

    public static final String PREFERENCES_WALLET_USER_ID = "walletuserId";
    public static final String PREFERENCES_USER_PIN = "";
    public static final String PREFERENCES_PREPIN_ENCRYPTION = "12";
    public static final String PREFERENCES_USER_BALANCE = "0";
    public static final String PREFERENCES_WALLET_BUSINESS_ID = "business_id";

    public static final String PREFERENCES_FILE_NAME = MyAppPrefsManager.PREF_NAME;
    public static final String PREFERENCES_FIRST_NAME = "firstname";
    public static final String PREFERENCES_LAST_NAME = "lastname";
    public static final String PREFERENCES_USER_EMAIL = "email";
    public static final String PREFERENCES_PHONE_NUMBER = "phoneNumber";
    public static final String PREFERENCES_WALLET_ACCOUNT_ROLE = "accountRole";

    public static final String PREFERENCES_FIREBASE_TOKEN_SUBMITTED = "tokenSubmitted";
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
    public static final String PREFERENCE_ACCOUNT_CONTACT ="contact";

    public static final String PREFERENCE_ACCOUNT_BUSINESS_NAME ="business_name";
    public static final String PREFERENCE_ACCOUNT_BUSINESS_LOCATION ="business_location";
    public static final String PREFERENCE_ACCOUNT_REG_NO ="regNo";
    public static final String PREFERENCE_ACCOUNT_LICENSE_NUMBER ="license_number";
    public static final String PREFERENCE_ACCOUNT_TRADE_LICENSE ="trade_license";
    public static final String PREFERENCE_ACCOUNT_REG_CERTIFICATE ="reg_certificate";
    public static final String PREFERENCE_SHOP_ID ="shop_id";


    public static final double PREFERENCE_WALLET_BALANCE = 0;

    public static NavController navController;
    public static BottomNavigationView bottomNavigationView,bottom_navigation_shop;
    public  static CoordinatorLayout scanCoordinatorLayout;
    public static FloatingActionButton scanFAB;
    public static FrameLayout frameLayout;
    public static FragmentContainerView fragmentContainerView;
    private static final int PERMISSION_CODE = 1;


    private final boolean doubleBackToExitPressedOnce = false;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_home);

        bottom_navigation_shop = findViewById(R.id.bottom_navigation_shop);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        scanCoordinatorLayout = findViewById(R.id.coordinator_layout_for_scanner);
        scanFAB = findViewById(R.id.fab);
        frameLayout = findViewById(R.id.frameLayout);
        fragmentContainerView = findViewById(R.id.wallet_home_container);


        scanFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //request permissions
                //check runtime permission
                    if (checkPermission()) {
                        // if permission is already granted display a toast message
                        //Toast.makeText(context, "Permission Granted..", Toast.LENGTH_SHORT).show();
                         //Log.w("PermissionGrant", "Permission Granted..");
                        //permission granted

                    } else {
                        String[] permissions = {Manifest.permission.CAMERA, VIBRATE};
                        //show popup to request runtime permission
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(permissions, PERMISSION_CODE);
                        }

                    }

                currentFragment= navController.getCurrentDestination().getId();

                    if(currentFragment == R.id.walletHomeFragment2){
                    WalletHomeActivity.navController.navigate(R.id.action_walletHomeFragment2_to_scanAndPayDialog);
                }
                else if (currentFragment  == R.id.walletAccountFragment2) {
                   WalletHomeActivity.navController.navigate(R.id.action_walletAccountFragment2_to_scanAndPayDialog);
                } else if (currentFragment == R.id.walletRewardsFragment2)  {
                    WalletHomeActivity.navController.navigate(R.id.action_walletRewardsFragment2_to_scanAndPayDialog);
                }else if (currentFragment == R.id.acceptPaymentFragment)  {
                        WalletHomeActivity.navController.navigate(R.id.action_acceptPaymentFragment_to_scanAndPayDialog);
                    }







            }
        });



        context = getApplicationContext();

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.wallet_home_container);

        navController = navHostFragment.getNavController();

        fm=getSupportFragmentManager();
        setUpNavigation();

        toolbar = findViewById(R.id.main_Toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);


        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if(WalletHomeActivity.bottomNavigationView!=null && WalletHomeActivity.bottom_navigation_shop!=null){

                    if(destination.getId()==R.id.walletHomeFragment2 ||  destination.getId()==R.id.walletAccountFragment2
                            ||  destination.getId()==R.id.cardListFragment ||  destination.getId()==R.id.acceptPaymentFragment ){

                        String role = getPreferences(PREFERENCES_WALLET_ACCOUNT_ROLE,context);
                        if( role.equalsIgnoreCase("DEFAULT")){
                            WalletHomeActivity.bottomNavigationView.setVisibility(View.VISIBLE);
                            WalletHomeActivity.scanCoordinatorLayout.setVisibility(View.VISIBLE);
                            WalletHomeActivity.bottom_navigation_shop.setVisibility(View.GONE);
                        }
                        else {
                            WalletHomeActivity.setUpMasterAgentNav();
                        }

                    }

                }

                if(Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.tokenAuthFragment) {
                    fragmentContainerView.setPadding(0,0,0,0);
                }else if(bottom_navigation_shop.getVisibility()==View.VISIBLE || bottomNavigationView.getVisibility()==View.VISIBLE
                        || scanCoordinatorLayout.getVisibility()==View.VISIBLE){
                    fragmentContainerView.setPadding(0,0,0,0);
                }
//                else{
//                    fragmentContainerView.setPadding(0,0,0,200);
//                }


        }
        });

        if (!getPreferences(PREFERENCES_FIREBASE_TOKEN_SUBMITTED, WalletHomeActivity.this).equals("yes")) {
            getAppToken();
        }

//        if(bottom_navigation_shop.getVisibility()==View.VISIBLE || bottomNavigationView.getVisibility()==View.VISIBLE
//                || scanCoordinatorLayout.getVisibility()==View.VISIBLE){
//            fragmentContainerView.setPadding(0,0,0,232);
//        } else{
//            fragmentContainerView.setPadding(0,0,0,0);
//        }


        //start product sync service
        startService(new Intent(this, SyncService.class));


    }


    public void setUpNavigation() {
        bottomNavigationView.setItemIconTintList(null);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {//walletAccountFragment
                int currentDestination;

                switch (item.getItemId()){

                    case R.id.walletRewardsFragment:
                        currentDestination = navController.getCurrentDestination().getId();

                        if (currentDestination  == R.id.walletHomeFragment2) {
                            navController.navigate(R.id.action_walletHomeFragment2_to_walletRewardsFragment2);
                        } else if (currentDestination == R.id.walletAccountFragment2 )  {
                             navController.navigate(R.id.action_walletAccountFragment2_to_walletRewardsFragment2);
                        } else if (currentDestination == R.id.walletRewardsFragment2 )  {

                        }

                        return true;

                    case R.id.walletAccountFragment :

                        currentDestination = navController.getCurrentDestination().getId();

                        if (currentDestination  == R.id.walletHomeFragment2) {
                            navController.navigate(R.id.action_walletHomeFragment2_to_walletAccountFragment2);
                        } else if (currentDestination == R.id.walletAccountFragment2 )  {
                            // navController.navigate(R.id.action_walletAccountFragment2_to_cardListFragment);
                        } else if (currentDestination == R.id.walletRewardsFragment2 )  {
                             navController.navigate(R.id.action_walletRewardsFragment2_to_navigation);
                        }

                        return true;

                    case R.id.walletHomeFragment :
                        currentDestination = navController.getCurrentDestination().getId();

                        if (currentDestination  == R.id.walletHomeFragment2) {

                        } else if (currentDestination == R.id.walletAccountFragment2 )  {
                             navController.navigate(R.id.action_walletAccountFragment2_to_walletHomeFragment2);
                        } else if (currentDestination == R.id.walletRewardsFragment2 )  {
                            navController.navigate(R.id.action_walletRewardsFragment2_to_walletHomeFragment2);
                        }
                        return true;


                    case R.id.WalletBuyFragment :
                        bottomNavigationView.postDelayed(() -> {
                            startActivity(new Intent(WalletHomeActivity.this, WalletBuySellActivity.class));
                        }, 300);

                        return true;
                    default:
                        return false;


                }

            }
        });

    }

    public static void setUpMasterAgentNav() {
        bottomNavigationView.setVisibility(View.GONE);
        scanCoordinatorLayout.setVisibility(View.VISIBLE);
        bottom_navigation_shop.setVisibility(View.VISIBLE);
        bottom_navigation_shop.setItemIconTintList(null);
        bottom_navigation_shop.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {//walletAccountFragment
                int currentDestination;

                switch (item.getItemId()){

//                    case R.id.walletSettlementFragment:
//                         Bundle args=new Bundle();
//                         args.putString("KEY_TITLE", context.getString(R.string.settlements) );
//
//
//                        currentDestination = navController.getCurrentDestination().getId();
//
//                        if (currentDestination  == R.id.walletHomeFragment2) {
//                            navController.navigate(R.id.action_walletHomeFragment2_to_walletTransactionsListFragment2,args);
//                        } else if (currentDestination == R.id.walletAccountFragment2 )  {
//                            navController.navigate(R.id.action_walletAccountFragment2_to_walletTransactionsListFragment2,args);
//                        }else if (currentDestination == R.id.walletTransactionsListFragment )  {
//
//                        }else if (currentDestination == R.id.acceptPaymentFragment )  {
//                            navController.navigate(R.id.action_acceptPaymentFragment_to_walletTransactionsListFragment2,args);
//                        }
//                        return true;

                    case R.id.walletAccountFragment_agent :
                        currentDestination = navController.getCurrentDestination().getId();

                        if (currentDestination  == R.id.walletHomeFragment2) {
                            navController.navigate(R.id.action_walletHomeFragment2_to_walletAccountFragment2);
                        } else if (currentDestination == R.id.walletAccountFragment2 )  {

                        }  else if (currentDestination == R.id.acceptPaymentFragment )  {
                            navController.navigate(R.id.action_acceptPaymentFragment_to_walletAccountFragment2);
                        } else if (currentDestination == R.id.walletTransactionsListFragment )  {
                            navController.navigate(R.id.action_walletTransactionsListFragment_to_walletAccountFragment2);
                        }

                        return true;

                    case R.id.walletHomeFragment :
                        currentDestination = navController.getCurrentDestination().getId();

                        if (currentDestination  == R.id.walletHomeFragment2) {

                        } else if (currentDestination == R.id.walletAccountFragment2 )  {
                            navController.navigate(R.id.action_walletAccountFragment2_to_walletHomeFragment2);
                        }  else if (currentDestination == R.id.acceptPaymentFragment )  {
                            navController.navigate(R.id.action_acceptPaymentFragment_to_walletHomeFragment2);
                        } else if (currentDestination == R.id.walletTransactionsListFragment )  {
                            navController.navigate(R.id.action_walletTransactionsListFragment_to_walletHomeFragment2);
                        }
                        return true;

                    case R.id.walletShopFragment:

                        Intent shop = new Intent(context, MerchantShopActivity.class);
                        shop.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(shop);

                        return true;
                    case R.id.walletAcceptPaymentFragment :
                        currentDestination = navController.getCurrentDestination().getId();

                        if (currentDestination  == R.id.walletHomeFragment2) {
                            navController.navigate(R.id.action_walletHomeFragment2_to_acceptPaymentFragment);
                        } else if (currentDestination == R.id.walletAccountFragment2 )  {
                            navController.navigate(R.id.action_walletAccountFragment2_to_acceptPaymentFragment);
                        } else if (currentDestination == R.id.acceptPaymentFragment )  {

                        }else if (currentDestination == R.id.walletTransactionsListFragment )  {
                            navController.navigate(R.id.action_walletTransactionsListFragment_to_acceptPaymentFragment);
                        }
                        return true;
                    default:
                        return false;


                }

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
        dialogLoader=new DialogLoader(this);
        getCards();
    }
    private  void navigateToAddMoneyVisa(ArrayList<CardSpinnerItem> cardItems){
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment depositDialog = new DepositMoneyVisa(cardItems, WalletHomeFragment.balance);
        depositDialog.show(ft, "dialog");
    }

    private void getCards(){
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,this);

        dialogLoader.showProgressDialog();

        Call<CardResponse> call = APIClient.getWalletInstance(this)
                .getCards(access_token, request_id,category,"getCards");

        call.enqueue(new Callback<CardResponse>() {
            @Override
            public void onResponse(Call<CardResponse> call, Response<CardResponse> response) {

                dialogLoader.hideProgressDialog();
                if(response.isSuccessful()){
                    ArrayList<CardSpinnerItem> cardItems = new ArrayList<>();
                    try {

                        List<CardResponse.Cards> cardlists = response.body().getCardsList();
                        cardItems=populateCardItemsList(cardlists);
                        navigateToAddMoneyVisa(cardItems);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else if (response.code() == 401) {



                    TokenAuthFragment.startAuth( true);
                    if (response.errorBody() != null) {
                        Log.e("info", String.valueOf(response.errorBody()));
                    } else {
                        Log.e("info", "Something got very very wrong");
                    }
                }

            }

            @Override
            public void onFailure(Call<CardResponse> call, Throwable t) {
                dialogLoader.hideProgressDialog();
            }
        });

    }

    private ArrayList<CardSpinnerItem> populateCardItemsList(List<CardResponse.Cards> cardlists) {
        ArrayList<CardSpinnerItem> cardItems = new ArrayList<>();

        cardItems.add(new CardSpinnerItem() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public String getCardNumber() {
                return null;
            }

            @Override
            public String getExpiryDate() {
                return null;
            }

            @Override
            public String getCvv() {
                return null;
            }

            @Override
            public String toString() {
                return "Select Card";
            }
        });

        for(int i =0; i<cardlists.size();i++){

            if( cardlists.get(i).getCard_number().length()>4){
                final  String card_number = cardlists.get(i).getCard_number();
                String first_four_digits = (card_number.substring(0,  4));
                String last_four_digits = (card_number.substring(card_number.length() - 4));
                final String masked_card_number = first_four_digits + "*******"+last_four_digits;
                int finalI = i;

                cardItems.add(new CardSpinnerItem() {
                    @Override
                    public String getId() {
                        return cardlists.get(finalI).getId();
                    }

                    @Override
                    public String getCardNumber() {
                        return cardlists.get(finalI).getCard_number();
                    }

                    @Override
                    public String getExpiryDate() {
                        return cardlists.get(finalI).getExpiry();
                    }

                    @Override
                    public String getCvv() {
                        return cardlists.get(finalI).getCvv();
                    }

                    @NonNull
                    @Override
                    public String toString() {
                        return masked_card_number;
                    }
                });
            }
        }


        cardItems.add(new CardSpinnerItem() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public String getCardNumber() {
                return null;
            }

            @Override
            public String getExpiryDate() {
                return null;
            }

            @Override
            public String getCvv() {
                return null;
            }

            @Override
            public String toString() {
                return "Add New";
            }
        });

        return cardItems;
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
        DialogFragment fundsTransferDialog = new AgentCustomerFundsTransfer(getString(R.string.customerFundTransfer));
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
        // To PersonalDetailsFragment
     navController.navigate(R.id.action_walletHomeFragment2_to_personalDetailsFragment);

    }

    public void openAgentCustomerLoanApplication(View view) {
     //   navController.navigate(R.id.action_walletHomeFragment2_to_loanUserDetailsFragment);

    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(navController.getCurrentDestination()!=null){
            currentFragment= navController.getCurrentDestination().getId();
            if (currentFragment == R.id.walletHomeFragment2  || currentFragment == R.id.tokenAuthFragment)
                new AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finishAffinity();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            else
                navController.popBackStack();
        }
        else /*if (doubleBackToExitPressedOnce)*/ {
            super.onBackPressed();
            finish();
        }

    }


    public void getAppToken() {
        FirebaseApp.initializeApp(WalletHomeActivity.this);

    }

    public static void disableNavigation() {
      bottomNavigationView.setVisibility(View.GONE);
      bottom_navigation_shop.setVisibility(View.GONE);
      scanCoordinatorLayout.setVisibility(View.GONE);
    }



    //generate unique request id
    public static String generateRequestId(){
        String request_id = "";
        Random rand = new Random();

        // Generate random integers in range 0 to 9999
        int rand_int = rand.nextInt(10000);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String timestamp_ = timestamp.toString();
        String result = timestamp_.replaceAll("\\p{Punct}|\\s", "");

        String formatted_rand_int = String.format("%021d", new BigInteger(result+rand_int) );
        request_id ="E"+ formatted_rand_int ;

        return request_id;
    }

    //select spinner by value
    public static void selectSpinnerItemByValue(Spinner spnr, String value) {

        if (value == null) return;

        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spnr.getAdapter();
        for (int position = 1; position < adapter.getCount(); position++) {

            String item = spnr.getAdapter().getItem(position) + "";
            if (item.toLowerCase().equals(value.toLowerCase())) {
                spnr.setSelection(position);
                return;
            }

        }
    }


    private boolean checkPermission() {
        // here we are checking two permission that is vibrate
        // and camera which is granted by user and not.
        // if permission is granted then we are returning
        // true otherwise false.
        int camera_permission = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int vibrate_permission = ContextCompat.checkSelfPermission(getApplicationContext(), VIBRATE);
        return camera_permission == PackageManager.PERMISSION_GRANTED && vibrate_permission == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermission() {
        // this method is to request
        // the runtime permission.
        int PERMISSION_REQUEST_CODE = 200;
        ActivityCompat.requestPermissions(this, new String[]{CAMERA, VIBRATE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // this method is called when user
        // allows the permission to use camera.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (grantResults.length > 0) {
                boolean cameraaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean vibrateaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (cameraaccepted && vibrateaccepted) {
//                    Toast.makeText(this, "Permission granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied \n You cannot use app without providing permission", Toast.LENGTH_LONG).show();
                }
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    public static  void SyncProductData() {
        if(context==null){
            return;
        }else{

            String category=WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE, context);
            if(category.equalsIgnoreCase("Default") || TextUtils.isEmpty(category)){
                return;
            }
        }
        if (Connectivity.isConnected(context)) {
            String sync_status = "0";
            List<EcProduct> productsList = DataRepository.getOurInstance(context).getUnsyncedProducts(sync_status);

            //Log.w("unsyncedProducts",productsList.size()+" products");
            for (int i = 0; i < productsList.size(); i++) {
                Log.e("WAlletIDError",productsList.get(i).getId()+"");

                String wallet_id=WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context);


                saveProductList(
                        productsList.get(i).getProduct_id(),
                        productsList.get(i).getId(),
                        wallet_id,
                        productsList.get(i).getManufacturer(),
                        productsList.get(i).getProduct_name(),
                        productsList.get(i).getProduct_code(),
                        productsList.get(i).getProduct_category(),
                        productsList.get(i).getProduct_buy_price(),
                        productsList.get(i).getProduct_sell_price(),
                        productsList.get(i).getProduct_supplier(),
                        productsList.get(i).getProduct_image(),
                        productsList.get(i).getProduct_stock(),
                        productsList.get(i).getProduct_weight()+" "+productsList.get(i).getProduct_weight_unit(),
                        productsList.get(i).getSync_status()

                );
            }

        }

    }


    public static void saveProductList(String product_id, String unique_product_id, String user_id, String product_manufacturer,
                                       String product_name, String product_code, String product_category, String product_buy_price, String product_sell_price,
                                       String product_supplier, String product_image, String product_stock, String product_unit, String sync_status) {
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();

        Call<ResponseBody> call = BuyInputsAPIClient
                .getInstance()
                .postProduct(access_token, unique_product_id, user_id, product_id, product_buy_price, product_sell_price,
                        product_supplier, Integer.parseInt(product_stock), product_manufacturer, product_category, product_name
                );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    //update product status
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            //update Sync Status
                            long updateStatus = DataRepository.getOurInstance(context).updateProductSyncStatus(product_id, "1");
                            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    if (updateStatus > 0) {
                                        Log.d("SyncStatus", "Product Synced");
                                    } else {

                                        Log.d("SyncStatus", "Sync Failed");

                                    }
                                }
                            });


                        }
                    });


                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: Sync Failed" + t.getMessage());

            }
        });
    }

}
