package com.cabral.emaishapay.constants

import android.text.format.Formatter
import android.util.Log
import java.net.NetworkInterface
import java.net.SocketException

object ConstantValues {
    private const val TAG = "Consumer_IP"
    const val WALLET_DOMAIN_LIVE = "https://emaishapro.emaisha.com"
    const val WALLET_API_DOMAIN_LIVE = "https://emaishapro.emaisha.com/api/"
    const val WALLET_DOMAIN = "https://emaishapro.emaisha.com"
    const val WALLET_API_DOMAIN = "https://emaishapro.emaisha.com/api/"
    const val ECOMMERCE_URL = "https://emaishashop.api.emaisha.com/"
    const val WALLET_DOMAIN_STAGING = "https://emaishapayapi.emaisha.com"
    const val ECOMMERCE_URL_STAGING = "https://emaishashop.api.emaisha.com/"
    const val ECOMMERCE_WEB = "https://emaisha.com/"
    const val ECOMMERCE_CONSUMER_KEY = "dadb7a7c1557917902724bbbf5"
    const val ECOMMERCE_CONSUMER_SECRET = "3ba77f821557917902b1d57373"
    const val IS_CLIENT_ACTIVE = true // "false" if compiling the project for Demo, "true" otherwise
    const val DEFAULT_NOTIFICATION = "fcm" // "fcm" for FCM_Notifications, "onesignal" for OneSignal
    const val NAVIGATION_STYLE = "side" // "bottom" for bottom navigation. "side" for side navigation.
    var CUSTOMER_HAS_WALLET = true //false
    @JvmField
    var APP_HEADER: String? = null
    @JvmField
    var MAINTENANCE_MODE: String? = null
    @JvmField
    var MAINTENANCE_TEXT: String? = null
    @JvmField
    var DEFAULT_HOME_STYLE: String? = null
    @JvmField
    var DEFAULT_CATEGORY_STYLE: String? = null
    @JvmField
    var DEFAULT_PRODUCT_CARD_STYLE = 0
    @JvmField
    var DEFAULT_BANNER_STYLE = 0
    @JvmField
    var LANGUAGE_ID = 1
    @JvmField
    var LANGUAGE_CODE = "en"
    @JvmField
    var CURRENCY_SYMBOL = "UGX"
    @JvmField
    var CURRENCY_CODE = "UGX"
    @JvmField
    var PACKING_CHARGE: String? = null
    @JvmField
    var NEW_PRODUCT_DURATION: Long = 0
    @JvmField
    var IS_GOOGLE_LOGIN_ENABLED = false
    @JvmField
    var IS_FACEBOOK_LOGIN_ENABLED = false
    @JvmField
    var IS_ADD_TO_CART_BUTTON_ENABLED = false
    @JvmField
    var IS_PRODUCT_CHECKED = false
    @JvmField
    var IS_ADMOBE_ENABLED = false
    @JvmField
    var ADMOBE_ID: String? = null
    @JvmField
    var AD_UNIT_ID_BANNER: String? = null
    @JvmField
    var AD_UNIT_ID_INTERSTITIAL: String? = null
    var IS_RESTART = false
    @JvmField
    var ABOUT_US: String? = null
    @JvmField
    var TERMS_SERVICES: String? = null
    @JvmField
    var PRIVACY_POLICY: String? = null
    @JvmField
    var REFUND_POLICY: String? = null
    @JvmField
    var A_Z: String? = null
    @JvmField
    var IS_USER_LOGGED_IN = false
    @JvmField
    var IS_PUSH_NOTIFICATIONS_ENABLED = false
    @JvmField
    var IS_LOCAL_NOTIFICATIONS_ENABLED = false

    @JvmStatic
    val localIpAddress: String?
        get() {
            try {
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr = intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress) {

                            @Suppress("DEPRECATION") val ip =   Formatter.  formatIpAddress(inetAddress.hashCode())
                            Log.i(TAG, "***** IP = $ip")
                            return ip
                        }
                    }
                }
            } catch (ex: SocketException) {
                Log.e(TAG, ex.toString())
            }
            return null
        }
}