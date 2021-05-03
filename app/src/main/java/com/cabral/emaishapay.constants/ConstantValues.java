package com.cabral.emaishapay.constants;


import android.text.format.Formatter;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class ConstantValues {

    private static String TAG = "Consumer_IP";
    public static final String WALLET_DOMAIN = "http://emaishapro.emaisha.com";
    public static final String WALLET_API_DOMAIN = "http://emaishapro.emaisha.com/api/";
    public static final String ECOMMERCE_URL = "http://emaishashopro.emaisha.com/";

    public static final String WALLET_DOMAIN_STAGING = "http://emaishapayapi.emaisha.com";
    public static final String ECOMMERCE_URL_STAGING = "http://emaishashop.api.emaisha.com/";

    public static final String ECOMMERCE_WEB = "https://emaisha.com/";

    public static final String ECOMMERCE_CONSUMER_KEY = "dadb7a7c1557917902724bbbf5";
    public static final String ECOMMERCE_CONSUMER_SECRET = "3ba77f821557917902b1d57373";

    public static final String CODE_VERSION = "4.0.12";

    public static final boolean IS_CLIENT_ACTIVE = true;                               // "false" if compiling the project for Demo, "true" otherwise

    public static final String DEFAULT_NOTIFICATION = "fcm";                      // "fcm" for FCM_Notifications, "onesignal" for OneSignal

    public static final String NAVIGATION_STYLE = "side";                             // "bottom" for bottom navigation. "side" for side navigation.

    public static Boolean CUSTOMER_HAS_WALLET = true;//false

    public static String APP_HEADER;

    public static String MAINTENANCE_MODE;
    public static String MAINTENANCE_TEXT;

    public static String DEFAULT_HOME_STYLE;
    public static String DEFAULT_CATEGORY_STYLE;
    public static int DEFAULT_PRODUCT_CARD_STYLE;
    public static int DEFAULT_BANNER_STYLE;

    public static int LANGUAGE_ID=1;
    public static String LANGUAGE_CODE= "en";;
    public static String CURRENCY_SYMBOL="UGX";
    public static String CURRENCY_CODE="UGX";
    public static String PACKING_CHARGE;
    public static long NEW_PRODUCT_DURATION;

    public static boolean IS_GOOGLE_LOGIN_ENABLED;
    public static boolean IS_FACEBOOK_LOGIN_ENABLED;
    public static boolean IS_ADD_TO_CART_BUTTON_ENABLED;
    public static boolean IS_PRODUCT_CHECKED;

    public static boolean IS_ADMOBE_ENABLED;
    public static String ADMOBE_ID;
    public static String AD_UNIT_ID_BANNER;
    public static String AD_UNIT_ID_INTERSTITIAL;

    public static boolean IS_RESTART = false;

    public static String ABOUT_US;
    public static String TERMS_SERVICES;
    public static String PRIVACY_POLICY;
    public static String REFUND_POLICY;
    public static String A_Z;

    public static boolean IS_USER_LOGGED_IN;
    public static boolean IS_PUSH_NOTIFICATIONS_ENABLED;
    public static boolean IS_LOCAL_NOTIFICATIONS_ENABLED;

    public static String PKG_NAME;
    public static String SHA1;

    public static final String PHONE_PATTERN = "^[987]\\d{9}$";




    public static final String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        Log.i(TAG, "***** IP = " + ip);
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return null;
    }


}
