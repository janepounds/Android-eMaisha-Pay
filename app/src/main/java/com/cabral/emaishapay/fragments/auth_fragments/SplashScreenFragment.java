package com.cabral.emaishapay.fragments.auth_fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.AuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.app.MyAppPrefsManager;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.databinding.SplashBinding;
import com.cabral.emaishapay.models.device_model.AppSettingsDetails;
import com.cabral.emaishapay.network.StartAppRequests;
import com.cabral.emaishapay.utils.Utilities;

public class SplashScreenFragment extends Fragment implements Animation.AnimationListener{

    private static final String TAG = "SplashScreen";

    ProgressBar progressBar;
    SplashScreenFragment.MyTask myTask;
    StartAppRequests startAppRequests;
    MyAppPrefsManager myAppPrefsManager;
    Animation animFade;
    Context context;
    SplashBinding binding;

    @Override
    public void onAttach(@NonNull Context context) {
        this.context=context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.splash,container,false);

        getActivity().overridePendingTransition(R.anim.slide_down, R.anim.bt_slide_in_up);
        MyAppPrefsManager prefsManager = new MyAppPrefsManager(context);
        Log.d(TAG, "onCreate: Login Status = " + prefsManager.isUserLoggedIn());

        Log.i("VC_Shop", "emaisha_Version = " + ConstantValues.CODE_VERSION);

        animFade = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fadein);
        animFade.setAnimationListener(this);

        binding.logo.startAnimation(animFade);
        binding.layoutAppEmaishaPay.startAnimation(animFade);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
              // Initializing StartAppRequests and PreferencesManager
        startAppRequests = new StartAppRequests(context);
        myAppPrefsManager = new MyAppPrefsManager(context);

        ConstantValues.LANGUAGE_ID = myAppPrefsManager.getUserLanguageId();
        ConstantValues.LANGUAGE_CODE = myAppPrefsManager.getUserLanguageCode();
        ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();
        ConstantValues.IS_PUSH_NOTIFICATIONS_ENABLED = myAppPrefsManager.isPushNotificationsEnabled();
        ConstantValues.IS_LOCAL_NOTIFICATIONS_ENABLED = myAppPrefsManager.isLocalNotificationsEnabled();

        // Start MyTask after 3 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            myTask = new MyTask();
            myTask.execute();
        }, 3000);
    }


    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


    //*********** Sets App configuration ********//

    private void setAppConfig() {
        AppSettingsDetails appSettingsDetails = ((EmaishaPayApp) getActivity().getApplicationContext()).getAppSettingsDetails();

        if (appSettingsDetails != null) {

            //Log.e("Settings Error: ",appSettingsDetails.getMaintenance_text()+" ");
            ConstantValues.DEFAULT_HOME_STYLE = getString(R.string.actionHome) + " " + appSettingsDetails.getHomeStyle();
            ConstantValues.DEFAULT_CATEGORY_STYLE = getString(R.string.actionCategory) + " " + appSettingsDetails.getCategoryStyle();
            ConstantValues.DEFAULT_PRODUCT_CARD_STYLE = (appSettingsDetails.getCardStyle() == null || appSettingsDetails.getCardStyle().isEmpty() ? 0 : Integer.parseInt(appSettingsDetails.getCardStyle()));
            ConstantValues.DEFAULT_BANNER_STYLE = (appSettingsDetails.getBannerStyle() == null || appSettingsDetails.getBannerStyle().isEmpty() ? 0 : Integer.parseInt(appSettingsDetails.getBannerStyle()));
            ConstantValues.MAINTENANCE_MODE = appSettingsDetails.getApp_web_environment();

            ConstantValues.CURRENCY_CODE = myAppPrefsManager.getCurrencyCode();
            ConstantValues.CURRENCY_SYMBOL = Utilities.getCurrencySymbol(ConstantValues.CURRENCY_CODE).replace("US", "");

            //if (appSettingsDetails.getAppName() != null && !TextUtils.isEmpty(appSettingsDetails.getAppName())) \
            ConstantValues.APP_HEADER = getString(R.string.app_name);

            if (appSettingsDetails.getMaintenance_text() != null && !TextUtils.isEmpty(appSettingsDetails.getMaintenance_text())) {

                ConstantValues.MAINTENANCE_TEXT = appSettingsDetails.getMaintenance_text();
            }

            /*
            if (appSettingsDetails.getCurrencySymbol() != null  &&  !TextUtils.isEmpty(appSettingsDetails.getCurrencySymbol())) {
                ConstantValues.CURRENCY_SYMBOL = Utilities.getCurrencySymbol(appSettingsDetails.getCurrencySymbol());
            } else {
                ConstantValues.CURRENCY_SYMBOL = "₹";
            }
            */

            if (appSettingsDetails.getPacking_charge_tax() != null && !TextUtils.isEmpty(appSettingsDetails.getCurrencySymbol())) {
                ConstantValues.PACKING_CHARGE = appSettingsDetails.getPacking_charge_tax();
            } else {
                ConstantValues.PACKING_CHARGE = "0.0";
            }

            if (appSettingsDetails.getNewProductDuration() != null && !TextUtils.isEmpty(appSettingsDetails.getNewProductDuration())) {
                ConstantValues.NEW_PRODUCT_DURATION = Long.parseLong(appSettingsDetails.getNewProductDuration());
            } else {
                ConstantValues.NEW_PRODUCT_DURATION = 30;
            }


            ConstantValues.IS_GOOGLE_LOGIN_ENABLED = (appSettingsDetails.getGoogleLogin().equalsIgnoreCase("1"));
            ConstantValues.IS_FACEBOOK_LOGIN_ENABLED = (appSettingsDetails.getFacebookLogin().equalsIgnoreCase("1"));
            ConstantValues.IS_ADD_TO_CART_BUTTON_ENABLED = (appSettingsDetails.getCartButton().equalsIgnoreCase("1"));

            ConstantValues.IS_ADMOBE_ENABLED = (appSettingsDetails.getAdmob().equalsIgnoreCase("1"));
            ConstantValues.ADMOBE_ID = appSettingsDetails.getAdmobId();
            ConstantValues.AD_UNIT_ID_BANNER = appSettingsDetails.getAdUnitIdBanner();
            ConstantValues.AD_UNIT_ID_INTERSTITIAL = appSettingsDetails.getAdUnitIdInterstitial();


            myAppPrefsManager.setLocalNotificationsTitle(appSettingsDetails.getNotificationTitle());
            myAppPrefsManager.setLocalNotificationsDuration(appSettingsDetails.getNotificationDuration());
            myAppPrefsManager.setLocalNotificationsDescription(appSettingsDetails.getNotificationText());

        } else {
//            ConstantValues.APP_HEADER = getString(R.string.app_name);
            ConstantValues.APP_HEADER = getResources().getString(R.string.app_name);

            ConstantValues.CURRENCY_SYMBOL = "UGX";
            ConstantValues.NEW_PRODUCT_DURATION = 30;
            ConstantValues.IS_ADMOBE_ENABLED = false;

            ConstantValues.IS_GOOGLE_LOGIN_ENABLED = true;
            ConstantValues.IS_FACEBOOK_LOGIN_ENABLED = true;
            ConstantValues.IS_ADD_TO_CART_BUTTON_ENABLED = true;

            ConstantValues.DEFAULT_HOME_STYLE = getString(R.string.actionHome) + " " + 1;
            ConstantValues.DEFAULT_CATEGORY_STYLE = getString(R.string.actionCategory) + " " + 1;
            ConstantValues.DEFAULT_PRODUCT_CARD_STYLE = 18;
            ConstantValues.DEFAULT_BANNER_STYLE = 1;
        }
    }

    /************* MyTask is Inner Class, that handles StartAppRequests on Background Thread *************/

    public class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            // Check for Internet Connection from the static method of Helper class
            if (Utilities.hasActiveInternetConnection(context)) {
                // Call the method of StartAppRequests class to process App Startup Requests
                startAppRequests.StartRequests();
                return "1";
            } else {
                return "0";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            setAppConfig();

            MyAppPrefsManager prefsManager = new MyAppPrefsManager(context);
            Log.d("SplashScreen", "onCreate: Login Status = " + prefsManager.isUserLoggedIn());

            if (!prefsManager.isUserLoggedIn()) {
                if(prefsManager.isFirstTimeLaunch()){
                    AuthActivity.navController.navigate(R.id.action_splashScreenFragment_to_onBoardingFragment);
                }else{

                    AuthActivity.navController.navigate(R.id.action_splashScreenFragment_to_loginFragment);
                }
            } else {
                //AuthActivity.navController.navigate(R.id.action_splashScreenFragment_to_wallet_home_navigation);
                startActivity(new Intent(getActivity().getBaseContext(), WalletHomeActivity.class));
                getActivity().finish();
            }


        }
    }


}
