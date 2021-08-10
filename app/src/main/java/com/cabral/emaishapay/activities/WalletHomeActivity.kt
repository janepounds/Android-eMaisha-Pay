package com.cabral.emaishapay.activities

import com.cabral.emaishapay.network.db.entities.*
import com.cabral.emaishapay.customs.DialogLoader
import kotlinx.android.synthetic.main.wallet_home.*
import android.os.Bundle
import com.cabral.emaishapay.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.FrameLayout
import android.Manifest.permission
import android.os.Build
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.NavController
import com.cabral.emaishapay.services.SyncJobService
import android.app.job.JobInfo
import android.app.job.JobScheduler
import com.cabral.emaishapay.DailogFragments.DepositMoneyMobile
import com.cabral.emaishapay.fragments.wallet_fragments.WalletHomeFragment
import com.cabral.emaishapay.models.CardSpinnerItem
import com.cabral.emaishapay.DailogFragments.DepositMoneyVisa
import com.cabral.emaishapay.models.CardResponse
import com.cabral.emaishapay.network.api_helpers.APIClient
import com.cabral.emaishapay.models.CardResponse.Cards
import com.cabral.emaishapay.fragments.wallet_fragments.TokenAuthFragment
import com.cabral.emaishapay.DailogFragments.DepositMoneyVoucher
import com.cabral.emaishapay.DailogFragments.AgentCustomerDeposits
import com.cabral.emaishapay.DailogFragments.AgentCustomerBalanceInquiry
import com.cabral.emaishapay.DailogFragments.AgentCustomerFundsTransfer
import com.cabral.emaishapay.DailogFragments.AgentCustomerWithdraw
import com.google.firebase.FirebaseApp
import android.Manifest.permission_group
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.widget.Toast
import com.cabral.emaishapay.models.banner_model.BannerDetails
import com.cabral.emaishapay.app.MyAppPrefsManager
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.cabral.emaishapay.network.Connectivity
import com.cabral.emaishapay.network.db.entities.EcProduct
import com.cabral.emaishapay.network.DataRepository
import okhttp3.ResponseBody
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient
import com.cabral.emaishapay.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.lang.IndexOutOfBoundsException
import java.math.BigInteger
import java.sql.Timestamp
import java.util.*

class WalletHomeActivity() : AppCompatActivity() {

    var currentFragment = 0
    lateinit  var dialogLoader: DialogLoader
    private val doubleBackToExitPressedOnce = false
    var toolbar: Toolbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wallet_home)

        /*******scan button is clicked***********************/
        fab.setOnClickListener(View.OnClickListener { //request permissions
            //check runtime permission
            if (checkPermission()) {

            } else {
                val permissions = arrayOf(permission.CAMERA, permission.VIBRATE)
                //show popup to request runtime permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(permissions, PERMISSION_CODE)
                }
            }
            currentFragment = navController!!.currentDestination!!.id
            when (currentFragment) {
                R.id.walletHomeFragment2 -> {
                    navController!!.navigate(R.id.action_walletHomeFragment2_to_scanAndPayDialog)
                }
                R.id.walletAccountFragment2 -> {
                    navController!!.navigate(R.id.action_walletAccountFragment2_to_scanAndPayDialog)
                }
                R.id.walletRewardsFragment2 -> {
                    navController!!.navigate(R.id.action_walletRewardsFragment2_to_scanAndPayDialog)
                }
                R.id.acceptPaymentFragment -> {
                    navController!!.navigate(R.id.action_acceptPaymentFragment_to_scanAndPayDialog)
                }
            }
        })
        context = applicationContext
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.wallet_home_container) as NavHostFragment?
        navController = navHostFragment!!.navController
        fm = supportFragmentManager
        setUpNavigation()
        setSupportActionBar(main_Toolbar)
        Companion.actionBar = supportActionBar
        Companion.actionBar!!.setDisplayShowTitleEnabled(false)
        Companion.actionBar!!.setHomeButtonEnabled(false)
        Companion.actionBar!!.setDisplayHomeAsUpEnabled(false)
        navController!!.addOnDestinationChangedListener { controller, destination, arguments ->
            if (bottom_navigation != null && bottom_navigation_shop != null) {
                if ((destination.id == R.id.walletHomeFragment2) || (destination.id == R.id.walletAccountFragment2
                                ) || (destination.id == R.id.cardListFragment) || (destination.id == R.id.acceptPaymentFragment)) {
                    val role = getPreferences(PREFERENCES_WALLET_ACCOUNT_ROLE, context)
                    if (role.equals("DEFAULT", ignoreCase = true)) {
                        bottom_navigation!!.visibility = View.VISIBLE
                        scanCoordinatorLayout?.visibility = View.VISIBLE
                        bottom_navigation_shop!!.visibility = View.GONE
                    } else {
                        setUpMasterAgentNav()
                    }
                }
            }
            if (Objects.requireNonNull(navController!!.currentDestination)!!.id  == R.id.tokenAuthFragment) {
                fragmentContainerView?.paddingLeft ?:  0
                fragmentContainerView?.paddingTop?:0
                fragmentContainerView?.paddingRight?:0
                fragmentContainerView?.paddingBottom?:0
            } else if ((bottom_navigation_shop.visibility == View.VISIBLE) || (bottom_navigation.visibility == View.VISIBLE
                            ) || (scanCoordinatorLayout?.visibility) == View.VISIBLE) {
                fragmentContainerView?.paddingLeft ?:  0
                fragmentContainerView?.paddingTop?:0
                fragmentContainerView?.paddingRight?:0
                fragmentContainerView?.paddingBottom?:0

            }

        }
        if (getPreferences(PREFERENCES_FIREBASE_TOKEN_SUBMITTED, this@WalletHomeActivity) != "yes") {
            appToken
        }
        scheduleJob()
    }

    private fun scheduleJob() {
        val componentName = ComponentName((context)!!, SyncJobService::class.java)
        val info = JobInfo.Builder(123, componentName)
                .setRequiresCharging(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .setPeriodic((15 * 60 * 1000).toLong())
                .build()
        val scheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        val resultCode = scheduler.schedule(info)
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.w(TAG, "Job scheduled")
        } else {
            Log.w(TAG, "Job scheduling failed")
        }
    }

    private fun setUpNavigation() {
        bottom_navigation!!.itemIconTintList = null
        bottom_navigation!!.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            //walletAccountFragment
            val currentDestination: Int
            when (item.itemId) {
                R.id.walletRewardsFragment -> {
                    currentDestination = navController!!.currentDestination!!.id
                    when (currentDestination) {
                        R.id.walletHomeFragment2 -> {
                            navController!!.navigate(R.id.action_walletHomeFragment2_to_walletRewardsFragment2)
                        }
                        R.id.walletAccountFragment2 -> {
                            navController!!.navigate(R.id.action_walletAccountFragment2_to_walletRewardsFragment2)
                        }
                        R.id.walletRewardsFragment2 -> {
                        }
                    }
                    true
                }
                R.id.walletAccountFragment -> {
                    currentDestination = navController!!.currentDestination!!.id
                    if (currentDestination == R.id.walletHomeFragment2) {
                        navController!!.navigate(R.id.action_walletHomeFragment2_to_walletAccountFragment2)
                    } else if (currentDestination == R.id.walletAccountFragment2) {
                        // navController.navigate(R.id.action_walletAccountFragment2_to_cardListFragment);
                    } else if (currentDestination == R.id.walletRewardsFragment2) {
                        navController!!.navigate(R.id.action_walletRewardsFragment2_to_navigation)
                    }
                    true
                }
                R.id.walletHomeFragment -> {
                    currentDestination = navController!!.currentDestination!!.id
                    if (currentDestination == R.id.walletHomeFragment2) {
                    } else if (currentDestination == R.id.walletAccountFragment2) {
                        navController!!.navigate(R.id.action_walletAccountFragment2_to_walletHomeFragment2)
                    } else if (currentDestination == R.id.walletRewardsFragment2) {
                        navController!!.navigate(R.id.action_walletRewardsFragment2_to_walletHomeFragment2)
                    }
                    true
                }
                R.id.WalletBuyFragment -> {
                    bottom_navigation!!.postDelayed({ startActivity(Intent(this@WalletHomeActivity, WalletBuySellActivity::class.java)) }, 300)
                    true
                }
                else -> false
            }
        })
    }

    fun openAddMobileMoney(view: View?) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        val ft = fm!!.beginTransaction()
        val prev = fm!!.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        // Create and show the dialog.
        val depositDialog: DialogFragment = DepositMoneyMobile(this, WalletHomeFragment.balance)
        depositDialog.show(ft, "dialog")
    }

    fun openAddMoneyVisa(view: View?) {
        dialogLoader = DialogLoader(this)
        cards
    }

    private fun navigateToAddMoneyVisa(cardItems: ArrayList<CardSpinnerItem?>) {
        val ft = fm!!.beginTransaction()
        val prev = fm!!.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        // Create and show the dialog.
        val depositDialog: DialogFragment = DepositMoneyVisa(cardItems, WalletHomeFragment.balance)
        depositDialog.show(ft, "dialog")
    }

    private val cards: Unit
        private get() {
            val access_token = WALLET_ACCESS_TOKEN
            val request_id = generateRequestId()
            val category = getPreferences(PREFERENCES_WALLET_ACCOUNT_ROLE, this)
            dialogLoader.showProgressDialog()
            val call = APIClient.getWalletInstance(this)
                    .getCards(access_token, request_id, category, "getCards")
            call.enqueue(object : Callback<CardResponse> {
                override fun onResponse(call: Call<CardResponse>, response: Response<CardResponse>) {
                    dialogLoader.hideProgressDialog()
                    if (response.isSuccessful) {
                        var cardItems = ArrayList<CardSpinnerItem?>()
                        try {
                            /********safe call with let**************/
                            val cardlists = response.body()!!.cardsList
                            cardItems = populateCardItemsList(cardlists)
                            navigateToAddMoneyVisa(cardItems)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (response.code() == 401) {
                        TokenAuthFragment.startAuth(true)
                        if (response.errorBody() != null) {
                            Log.e("info", response.errorBody().toString())
                        } else {
                            Log.e("info", "Something got very very wrong")
                        }
                    }
                }

                override fun onFailure(call: Call<CardResponse>, t: Throwable) {
                    dialogLoader.hideProgressDialog()
                }
            })
        }

    private fun populateCardItemsList(cardlists: List<Cards>): ArrayList<CardSpinnerItem?> {
        val cardItems = ArrayList<CardSpinnerItem?>()
        cardItems.add(object : CardSpinnerItem {
            override fun getId(): String? {
                return null
            }

            override fun getCardNumber(): String? {
                return null
            }

            override fun getExpiryDate(): String? {
                return null
            }

            override fun getCvv(): String? {
                return null
            }

            override fun toString(): String {
                return "Select Card"
            }
        })
        for (i in cardlists.indices) {
            if (cardlists[i].card_number.length > 4) {
                val card_number = cardlists[i].card_number
                val first_four_digits = (card_number.substring(0, 4))
                val last_four_digits = (card_number.substring(card_number.length - 4))
                val masked_card_number = "$first_four_digits*******$last_four_digits"
                val finalI = i
                cardItems.add(object : CardSpinnerItem {
                    override fun getId(): String {
                        return cardlists[finalI].getId()
                    }

                    override fun getCardNumber(): String {
                        return cardlists[finalI].card_number
                    }

                    override fun getExpiryDate(): String {
                        return cardlists[finalI].expiry
                    }

                    override fun getCvv(): String {
                        return cardlists[finalI].cvv
                    }

                    override fun toString(): String {
                        return masked_card_number
                    }
                })
            }
        }
        cardItems.add(object : CardSpinnerItem {
            override fun getId(): String? {
                return null
            }

            override fun getCardNumber(): String? {
                return null
            }

            override fun getExpiryDate(): String? {
                return null
            }

            override fun getCvv(): String? {
                return null
            }

            override fun toString(): String {
                return "Add New"
            }
        })
        return cardItems
    }

    fun openAddMoneyVoucher(view: View?) {
        val ft = fm!!.beginTransaction()
        val prev = fm!!.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        // Create and show the dialog.
        val depositDialog: DialogFragment = DepositMoneyVoucher(this, WalletHomeFragment.balance)
        depositDialog.show(ft, "dialog")
    }

    fun openAgentCustomerDeposit(view: View?) {
        val ft = fm!!.beginTransaction()
        val prev = fm!!.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        // Create and show the dialog.
        val depositDialog: DialogFragment = AgentCustomerDeposits()
        depositDialog.show(ft, "dialog")
    }

    fun openAgentCustomerBalanceInquiry(view: View?) {
        val ft = fm!!.beginTransaction()
        val prev = fm!!.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        // Create and show the dialog.
        val balanceInquiryDialog: DialogFragment = AgentCustomerBalanceInquiry()
        balanceInquiryDialog.show(ft, "dialog")
    }

    fun openAgentCustomerFundsTransfer(view: View?) {
        val ft = fm!!.beginTransaction()
        val prev = fm!!.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        // Create and show the dialog.
        val fundsTransferDialog: DialogFragment = AgentCustomerFundsTransfer(getString(R.string.customerFundTransfer))
        fundsTransferDialog.show(ft, "dialog")
    }

    fun openAgentCustomerWithdraw(view: View?) {
        val ft = fm!!.beginTransaction()
        val prev = fm!!.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        // Create and show the dialog.
        val customerWithdrawDialog: DialogFragment = AgentCustomerWithdraw()
        customerWithdrawDialog.show(ft, "dialog")
    }

    fun openAgentCustomerAccountOpening(view: View?) {
        // To PersonalDetailsFragment
        navController!!.navigate(R.id.action_walletHomeFragment2_to_personalDetailsFragment)
    }

    fun openAgentCustomerLoanApplication(view: View?) {
        //   navController.navigate(R.id.action_walletHomeFragment2_to_loanUserDetailsFragment);
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (navController!!.currentDestination != null) {
            currentFragment = navController!!.currentDestination!!.id
            if (currentFragment == R.id.walletHomeFragment2 || currentFragment == R.id.tokenAuthFragment) AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, id: Int) {
                            finishAffinity()
                        }
                    })
                    .setNegativeButton("No", null)
                    .show() else navController!!.popBackStack()
        } else  /*if (doubleBackToExitPressedOnce)*/ {
            super.onBackPressed()
            finish()
        }
    }

    val appToken: Unit
        get() {
            FirebaseApp.initializeApp(this@WalletHomeActivity)
        }

    private fun checkPermission(): Boolean {
        // here we are checking two permission that is vibrate
        // and camera which is granted by user and not.
        // if permission is granted then we are returning
        // true otherwise false.
        val camera_permission = ContextCompat.checkSelfPermission(applicationContext, permission_group.CAMERA)
        val vibrate_permission = ContextCompat.checkSelfPermission(applicationContext, permission.VIBRATE)
        return camera_permission == PackageManager.PERMISSION_GRANTED && vibrate_permission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        // this method is to request
        // the runtime permission.
        val PERMISSION_REQUEST_CODE = 200
        ActivityCompat.requestPermissions(this, arrayOf(permission_group.CAMERA, permission.VIBRATE), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // this method is called when user
        // allows the permission to use camera.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            if (grantResults.size > 0) {
                val cameraaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val vibrateaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (cameraaccepted && vibrateaccepted) {
//                    Toast.makeText(this, "Permission granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied \n You cannot use app without providing permission", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    companion object {
        @JvmField
        var WALLET_ACCESS_TOKEN: String? = null
        private val TAG = WalletHomeActivity::class.java.simpleName
        @JvmField
        var Banners: List<BannerDetails> = ArrayList()
        private var context: Context? = null
        var fm: FragmentManager? = null
        var actionBar: ActionBar? = null
        const val PREFERENCES_WALLET_USER_ID = "walletuserId"
        const val PREFERENCES_PREPIN_ENCRYPTION = "12"
        const val PREFERENCES_USER_BALANCE = "0"
        const val PREFERENCES_WALLET_BUSINESS_ID = "business_id"
        private const val PREFERENCES_FILE_NAME = MyAppPrefsManager.PREF_NAME
        const val PREFERENCES_FIRST_NAME = "firstname"
        const val PREFERENCES_LAST_NAME = "lastname"
        const val PREFERENCES_USER_EMAIL = "email"
        const val PREFERENCES_PHONE_NUMBER = "phoneNumber"
        const val PREFERENCES_WALLET_ACCOUNT_ROLE = "accountRole"
        const val PREFERENCES_FIREBASE_TOKEN_SUBMITTED = "tokenSubmitted"
        const val PREFERENCES_USER_PASSWORD = "password"
        const val PREFERENCE_ACCOUNT_PERSONAL_DOB = "dob"
        const val PREFERENCE_ACCOUNT_PERSONAL_GENDER = "gender"
        const val PREFERENCE_ACCOUNT_PERSONAL_NOK = "next_of_kin"
        const val PREFERENCE_ACCOUNT_PERSONAL_NOK_CONTACT = "next_of_kin_contact"
        const val PREFERENCE_ACCOUNT_PERSONAL_PIC = "pic"
        const val PREFERENCE_ACCOUNT_ID_TYPE = "idtype"
        const val PREFERENCE_ACCOUNT_ID_NUMBER = "idNumber"
        const val PREFERENCE_ACCOUNT_ID_EXPIRY_DATE = "expiryDate"
        const val PREFERENCE_ACCOUNT_ID_FRONT = "front"
        const val PREFERENCE_ACCOUNT_ID_BACK = "back"
        const val PREFERENCE_ACCOUNT_EMPLOYER = "employer"
        const val PREFERENCE_ACCOUNT_DESIGNATION = "designation"
        const val PREFERENCE_ACCOUNT_LOCATION = "location"
        const val PREFERENCE_ACCOUNT_EMPLOYEE_ID = "employeeId"
        const val PREFERENCE_ACCOUNT_CONTACT = "contact"
        const val PREFERENCE_ACCOUNT_BUSINESS_NAME = "business_name"
        const val PREFERENCE_ACCOUNT_BUSINESS_LOCATION = "business_location"
        const val PREFERENCE_ACCOUNT_REG_NO = "regNo"
        const val PREFERENCE_ACCOUNT_LICENSE_NUMBER = "license_number"
        const val PREFERENCE_ACCOUNT_TRADE_LICENSE = "trade_license"
        const val PREFERENCE_ACCOUNT_REG_CERTIFICATE = "reg_certificate"
        const val PREFERENCE_WALLET_BALANCE = 0.0
        @SuppressLint("StaticFieldLeak")
        @JvmField
        var navController: NavController? = null
        @JvmField
        var bottomNavigationView: BottomNavigationView? = null
        @JvmField
        var bottom_navigation_shop: BottomNavigationView? = null
        @JvmField
        var scanCoordinatorLayout: CoordinatorLayout? = null
        var scanFAB: FloatingActionButton? = null
        var frameLayout: FrameLayout? = null
        var fragmentContainerView: FragmentContainerView? = null
        private const val PERMISSION_CODE = 1
        fun setUpMasterAgentNav() {
            bottomNavigationView!!.visibility = View.GONE
            scanCoordinatorLayout!!.visibility = View.VISIBLE
            bottom_navigation_shop!!.visibility = View.VISIBLE
            bottom_navigation_shop!!.itemIconTintList = null
            bottom_navigation_shop!!.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener {
                override fun onNavigationItemSelected(item: MenuItem): Boolean { //walletAccountFragment
                    val currentDestination: Int
                    when (item.itemId) {
                        R.id.walletAccountFragment_agent -> {
                            currentDestination = navController!!.currentDestination!!.id
                            if (currentDestination == R.id.walletHomeFragment2) {
                                navController!!.navigate(R.id.action_walletHomeFragment2_to_walletAccountFragment2)
                            } else if (currentDestination == R.id.walletAccountFragment2) {
                            } else if (currentDestination == R.id.acceptPaymentFragment) {
                                navController!!.navigate(R.id.action_acceptPaymentFragment_to_walletAccountFragment2)
                            } else if (currentDestination == R.id.walletTransactionsListFragment) {
                                navController!!.navigate(R.id.action_walletTransactionsListFragment_to_walletAccountFragment2)
                            }
                            return true
                        }
                        R.id.walletHomeFragment -> {
                            currentDestination = navController!!.currentDestination!!.id
                            if (currentDestination == R.id.walletHomeFragment2) {
                            } else if (currentDestination == R.id.walletAccountFragment2) {
                                navController!!.navigate(R.id.action_walletAccountFragment2_to_walletHomeFragment2)
                            } else if (currentDestination == R.id.acceptPaymentFragment) {
                                navController!!.navigate(R.id.action_acceptPaymentFragment_to_walletHomeFragment2)
                            } else if (currentDestination == R.id.walletTransactionsListFragment) {
                                navController!!.navigate(R.id.action_walletTransactionsListFragment_to_walletHomeFragment2)
                            }
                            return true
                        }
                        R.id.walletShopFragment -> {
                            val shop = Intent(context, MerchantShopActivity::class.java)
                            shop.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context!!.startActivity(shop)
                            return true
                        }
                        R.id.walletAcceptPaymentFragment -> {
                            currentDestination = navController!!.currentDestination!!.id
                            if (currentDestination == R.id.walletHomeFragment2) {
                                navController!!.navigate(R.id.action_walletHomeFragment2_to_acceptPaymentFragment)
                            } else if (currentDestination == R.id.walletAccountFragment2) {
                                navController!!.navigate(R.id.action_walletAccountFragment2_to_acceptPaymentFragment)
                            } else if (currentDestination == R.id.acceptPaymentFragment) {
                            } else if (currentDestination == R.id.walletTransactionsListFragment) {
                                navController!!.navigate(R.id.action_walletTransactionsListFragment_to_acceptPaymentFragment)
                            }
                            return true
                        }
                        else -> return false
                    }
                }
            })
        }

        @JvmStatic
        fun startHome(context: Context) {
            try {
                val home = Intent(context, WalletHomeActivity::class.java)
                context.startActivity(home)
            } catch (e: Exception) {
                Log.e("Intent start Error: ", (e.message)!!)
            }
        }

        @JvmStatic
        fun getPreferences(key: String?, context: Context?): String? {
            val sharedPreferences = context!!.getSharedPreferences(PREFERENCES_FILE_NAME,
                    MODE_PRIVATE)
            return sharedPreferences.getString(key, "")
        }

        @JvmStatic
        fun savePreferences(key: String?, value: String?, context: Context) {
            val sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_NAME,
                    0)
            val editor = sharedPreferences.edit()
            editor.putString(key, value)
            editor.commit()
        }

        @JvmStatic
        fun disableNavigation() {
            bottomNavigationView!!.visibility = View.GONE
            bottom_navigation_shop!!.visibility = View.GONE
            scanCoordinatorLayout!!.visibility = View.GONE
        }

        //generate unique request id
        @JvmStatic
        fun generateRequestId(): String {
            var request_id: String = ""
            val rand = Random()

            // Generate random integers in range 0 to 9999
            val rand_int = rand.nextInt(10000)
            val timestamp = Timestamp(System.currentTimeMillis())
            val timestamp_ = timestamp.toString()
            val result = timestamp_.replace("\\p{Punct}|\\s".toRegex(), "")
            val formatted_rand_int = String.format("%021d", BigInteger(result + rand_int))
            request_id = "E$formatted_rand_int"
            return request_id
        }

        //select spinner by value
        @JvmStatic
        fun selectSpinnerItemByValue(spnr: Spinner, value: String?) {
            if (value == null) return
            val adapter = spnr.adapter as ArrayAdapter<String>
            for (position in 1 until adapter.count) {
                val item = spnr.adapter.getItem(position).toString() + ""
                if ((item.equals(value, ignoreCase = true))) {
                    spnr.setSelection(position)
                    return
                }
            }
        }

        @JvmStatic
        fun syncProductData() {
            if (context == null) {
                return
            } else {
                val category = getPreferences(PREFERENCES_WALLET_ACCOUNT_ROLE, context)
                if (category.equals("Default", ignoreCase = true) || TextUtils.isEmpty(category)) {
                    return
                }
            }
            if (Connectivity.isConnected(context)) {
                val sync_status = "0"
                val productsList = DataRepository.getOurInstance(context).getUnsyncedProducts(sync_status)
                Log.w("unsyncedProducts", productsList.size.toString() + " products")
                for (i in productsList.indices) {
                    Log.e("WAlletIDError", productsList[i].sync_status + "")
                    saveProductList(context, productsList[i])
                }
            }
        }

        private fun saveProductList(context: Context?, product: EcProduct) {
            val access_token = WALLET_ACCESS_TOKEN
            val user_id = getPreferences(PREFERENCES_WALLET_USER_ID, context)
            val request_id = generateRequestId()
            val call = BuyInputsAPIClient
                    .getInstance()
                    .postProduct(access_token, product.id, user_id, product.product_id, product.product_buy_price, product.product_sell_price,
                            product.product_supplier, product.product_stock!!.toInt(), product.manufacturer, product.product_category, product.product_name
                    )
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                    if (response.isSuccessful) {
                        //update product status
                        AppExecutors.getInstance().diskIO().execute(object : Runnable {
                            override fun run() {
                                //update Sync Status
                                val updateStatus = DataRepository.getOurInstance(Companion.context).updateProductSyncStatus(product.id, "1")
                                AppExecutors.getInstance().mainThread().execute(object : Runnable {
                                    override fun run() {
                                        if (updateStatus > 0) {
                                            Log.d("SyncStatus", "Product Synced")
                                        } else {
                                            Log.d("SyncStatus", "Sync Failed")
                                        }
                                    }
                                })
                            }
                        })
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    Log.d(TAG, "onFailure: Sync Failed" + t.message)
                }
            })
        }
    }
}