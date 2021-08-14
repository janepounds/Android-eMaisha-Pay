package com.cabral.emaishapay.activities

import com.cabral.emaishapay.customs.DialogLoader
import kotlinx.android.synthetic.main.wallet_home.*
import android.os.Bundle
import com.cabral.emaishapay.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.coordinatorlayout.widget.CoordinatorLayout
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.cabral.emaishapay.network.Connectivity
import com.cabral.emaishapay.network.db.entities.EcProduct
import com.cabral.emaishapay.network.DataRepository
import okhttp3.ResponseBody
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient
import com.cabral.emaishapay.AppExecutors
import kotlinx.android.synthetic.main.layout_scan_and_pay_process_step_1.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.lang.IndexOutOfBoundsException
import java.math.BigInteger
import java.sql.Timestamp
import java.util.*

class WalletHomeActivity : AppCompatActivity() {

    var currentFragment = 0
    val dialogLoader: DialogLoader? by lazy { DialogLoader(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wallet_home)

        fab.setOnClickListener(View.OnClickListener {
            if (!checkPermission()) {
                val permissions = arrayOf(permission.CAMERA, permission.VIBRATE)
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

        val navHostFragment = walletHomeContainer as NavHostFragment

        context = applicationContext
        Companion.fm = supportFragmentManager
        Companion.bottomNavigationShop = bottomNavigationShop
        Companion.bottomNavigationView = bottomNavigation
        Companion.scanCoordinatorLayout = coordinatorLayoutForScanner
        Companion.navController=navHostFragment.navController
        Companion.context= context
        setUpNavigation()
        setSupportActionBar(main_Toolbar)

        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setHomeButtonEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->

            if ((destination.id == R.id.walletHomeFragment2) || (destination.id == R.id.walletAccountFragment2  ) || (destination.id == R.id.cardListFragment) || (destination.id == R.id.acceptPaymentFragment)) {
                val role = getPreferences(PREFERENCES_WALLET_ACCOUNT_ROLE, this)
                if (role.equals("DEFAULT", ignoreCase = true)) {
                    bottomNavigation.visibility = View.VISIBLE
                    coordinatorLayoutForScanner.visibility = View.VISIBLE
                    bottomNavigationShop.visibility = View.GONE
                } else {
                    setUpMasterAgentNav()
                }
            }


        }
        if (getPreferences(PREFERENCES_FIREBASE_TOKEN_SUBMITTED, this@WalletHomeActivity) != "yes") {
            appToken
        }
        scheduleJob()
    }

    private fun scheduleJob() {
        val componentName = ComponentName(context, SyncJobService::class.java)
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
        bottomNavigation.itemIconTintList = null
        bottomNavigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
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
                    when (currentDestination) {
                        R.id.walletHomeFragment2 -> {
                            navController!!.navigate(R.id.action_walletHomeFragment2_to_walletAccountFragment2)
                        }
                        R.id.walletRewardsFragment2 -> {
                            navController!!.navigate(R.id.action_walletRewardsFragment2_to_navigation)
                        }
                    }
                    true
                }
                R.id.walletHomeFragment -> {
                    currentDestination = navController!!.currentDestination!!.id
                    when (currentDestination) {
                        R.id.walletAccountFragment2 -> {
                            navController!!.navigate(R.id.action_walletAccountFragment2_to_walletHomeFragment2)
                        }
                        R.id.walletRewardsFragment2 -> {
                            navController!!.navigate(R.id.action_walletRewardsFragment2_to_walletHomeFragment2)
                        }
                    }
                    true
                }
                R.id.WalletBuyFragment -> {
                    bottomNavigation.postDelayed(
                            Runnable { startActivity(Intent(this@WalletHomeActivity, WalletBuySellActivity::class.java)) },
                            300
                    )
                    true
                }
                else -> false
            }
        })
    }

    fun openAddMobileMoney() {
        val ft = fm!!.beginTransaction()
        val prev = fm!!.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        val depositDialog: DialogFragment = DepositMoneyMobile(this, WalletHomeFragment.balance)
        depositDialog.show(ft, "dialog")
    }

    fun openAddMoneyVisa() {
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
        get() {
            val category = getPreferences(PREFERENCES_WALLET_ACCOUNT_ROLE, this)
            dialogLoader!!.showProgressDialog()
            val call = APIClient.getWalletInstance(this)
                    .getCards(WALLET_ACCESS_TOKEN, generateRequestId(), category, "getCards")
            call.enqueue(object : Callback<CardResponse> {
                override fun onResponse(call: Call<CardResponse>, response: Response<CardResponse>) {
                    dialogLoader!!.hideProgressDialog()
                    if (response.isSuccessful) {
                        response.body()?.let {
                            val cardItems = populateCardItemsList(it.cardsList)
                            navigateToAddMoneyVisa(cardItems)
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
                    dialogLoader!!.hideProgressDialog()
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
                val cardNumber = cardlists[i].card_number
                val firstFourDigits = (cardNumber.substring(0, 4))
                val lastFourDigits = (cardNumber.substring(cardNumber.length - 4))
                val maskedCardNumber = "$firstFourDigits*******$lastFourDigits"

                cardItems.add(object : CardSpinnerItem {
                    override fun getId(): String {
                        return cardlists[i].getId()
                    }

                    override fun getCardNumber(): String {
                        return cardlists[i].card_number
                    }

                    override fun getExpiryDate(): String {
                        return cardlists[i].expiry
                    }

                    override fun getCvv(): String {
                        return cardlists[i].cvv
                    }

                    override fun toString(): String {
                        return maskedCardNumber
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

    fun openAddMoneyVoucher() {
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

    fun openAgentCustomerDeposit() {
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

    fun openAgentCustomerBalanceInquiry() {
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

    fun openAgentCustomerFundsTransfer() {
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

    fun openAgentCustomerWithdraw() {
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

    fun openAgentCustomerAccountOpening() {
        // To PersonalDetailsFragment
        navController!!.navigate(R.id.action_walletHomeFragment2_to_personalDetailsFragment)
    }

    fun openAgentCustomerLoanApplication() {
        //   navController.navigate(R.id.action_walletHomeFragment2_to_loanUserDetailsFragment);
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        navController?.let {
            if (it.currentDestination != null) {
                currentFragment = it.currentDestination!!.id
                if (currentFragment == R.id.walletHomeFragment2 || currentFragment == R.id.tokenAuthFragment) AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { _, _ -> finishAffinity() }
                        .setNegativeButton("No", null)
                        .show() else it.popBackStack()
            } else  {
                super.onBackPressed()
                finish()
            }
        }
    }

     private val appToken: Unit
        get() {
            FirebaseApp.initializeApp(this@WalletHomeActivity)
        }

    private fun checkPermission(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(applicationContext, permission_group.CAMERA)
        val vibratePermission = ContextCompat.checkSelfPermission(applicationContext, permission.VIBRATE)
        return cameraPermission == PackageManager.PERMISSION_GRANTED && vibratePermission == PackageManager.PERMISSION_GRANTED
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // this method is called when user allows the permission to use camera.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            if (grantResults.isNotEmpty()) {
                val cameraaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val vibrateaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (!cameraaccepted || !vibrateaccepted) {
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
        private lateinit var context:Context

        @JvmField
        var Banners: List<BannerDetails> = ArrayList()
        var fm: FragmentManager? = null
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
        var scanCoordinatorLayout: CoordinatorLayout? = null
        private const val PERMISSION_CODE = 1
        @JvmField
        var bottomNavigationShop: BottomNavigationView? = null
        @JvmField
        var bottomNavigationView: BottomNavigationView? = null
        fun setUpMasterAgentNav() {
            bottomNavigationShop!!.visibility = View.GONE
            scanCoordinatorLayout!!.visibility = View.VISIBLE
            bottomNavigationShop!!.visibility = View.VISIBLE
            bottomNavigationShop!!.itemIconTintList = null

            bottomNavigationShop!!.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener {
                override fun onNavigationItemSelected(item: MenuItem): Boolean { //walletAccountFragment
                    val currentDestination: Int
                    when (item.itemId) {
                        R.id.walletAccountFragment_agent -> {
                            currentDestination = navController!!.currentDestination!!.id
                            when (currentDestination) {
                                R.id.walletHomeFragment2 -> {
                                    navController!!.navigate(R.id.action_walletHomeFragment2_to_walletAccountFragment2)
                                }
                                R.id.walletAccountFragment2 -> {
                                }
                                R.id.acceptPaymentFragment -> {
                                    navController!!.navigate(R.id.action_acceptPaymentFragment_to_walletAccountFragment2)
                                }
                                R.id.walletTransactionsListFragment -> {
                                    navController!!.navigate(R.id.action_walletTransactionsListFragment_to_walletAccountFragment2)
                                }
                            }
                            return true
                        }
                        R.id.walletHomeFragment -> {
                            currentDestination = navController!!.currentDestination!!.id
                            when (currentDestination) {
                                R.id.walletAccountFragment2 -> {
                                    navController!!.navigate(R.id.action_walletAccountFragment2_to_walletHomeFragment2)
                                }
                                R.id.acceptPaymentFragment -> {
                                    navController!!.navigate(R.id.action_acceptPaymentFragment_to_walletHomeFragment2)
                                }
                                R.id.walletTransactionsListFragment -> {
                                    navController!!.navigate(R.id.action_walletTransactionsListFragment_to_walletHomeFragment2)
                                }
                            }
                            return true
                        }
                        R.id.walletShopFragment -> {
                            val shop = Intent(context, MerchantShopActivity::class.java)
                            shop.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(shop)
                            return true
                        }
                        R.id.walletAcceptPaymentFragment -> {
                            currentDestination = navController!!.currentDestination!!.id
                            when (currentDestination) {
                                R.id.walletHomeFragment2 -> {
                                    navController!!.navigate(R.id.action_walletHomeFragment2_to_acceptPaymentFragment)
                                }
                                R.id.walletAccountFragment2 -> {
                                    navController!!.navigate(R.id.action_walletAccountFragment2_to_acceptPaymentFragment)
                                }
                                R.id.walletTransactionsListFragment -> {
                                    navController!!.navigate(R.id.action_walletTransactionsListFragment_to_acceptPaymentFragment)
                                }
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
            editor.apply()
        }

        @JvmStatic
        fun disableNavigation() {
            bottomNavigationView!!.visibility = View.GONE
            bottomNavigationShop!!.visibility = View.GONE
            scanCoordinatorLayout!!.visibility = View.GONE
        }

        //generate unique request id
        @JvmStatic
        fun generateRequestId(): String {
            val randInt = (Random()).nextInt(10000)// Generate random integers in range 0 to 9999
            val timestamp = Timestamp(System.currentTimeMillis()).toString()
            val result = timestamp.replace("\\p{Punct}|\\s".toRegex(), "")
            val formattedRandInt = String.format("%021d", BigInteger(result + randInt))
            return "E$formattedRandInt"
        }

        //select spinner by value
        @JvmStatic
        fun selectSpinnerItemByValue(spnr: Spinner, value: String?) {
            if (value == null) return
            val adapter = spnr.adapter as ArrayAdapter<*>
            for (position in 1 until adapter.count) {
                val item = spnr.adapter.getItem(position).toString()
                if ((item.equals(value, ignoreCase = true))) {
                    spnr.setSelection(position)
                    return
                }
            }
        }

        @JvmStatic
        fun syncProductData() {
            context?.let {
                val category = getPreferences(PREFERENCES_WALLET_ACCOUNT_ROLE, it)

                if (!category.equals("Default", ignoreCase = true) && !TextUtils.isEmpty(category) && Connectivity.isConnected(it)) {
                    val syncStatus = "0"
                    val productsList = DataRepository.getOurInstance(it).getUnsyncedProducts(syncStatus)
                    Log.w("unsyncedProducts", productsList.size.toString() + " products")
                    for (i in productsList.indices) {
                        Log.e("WAlletIDError", productsList[i].sync_status + "")
                        saveProductList(it, productsList[i])
                    }
                }

            }
        }

        private fun saveProductList(context: Context?, product: EcProduct) {
            val userId = getPreferences(PREFERENCES_WALLET_USER_ID, context)

            val call = BuyInputsAPIClient
                    .getInstance()
                    .postProduct(WALLET_ACCESS_TOKEN, product.id, userId, product.product_id, product.product_buy_price, product.product_sell_price,
                            product.product_supplier, product.product_stock!!.toInt(), product.manufacturer, product.product_category, product.product_name
                    )
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                    if (response.isSuccessful) {
                        AppExecutors.getInstance().diskIO().execute {
                            val updateStatus = DataRepository.getOurInstance(Companion.context).updateProductSyncStatus(product.id, "1")
                            AppExecutors.getInstance().mainThread().execute {
                                if (updateStatus > 0) {
                                    Log.d("SyncStatus", "Product Synced")
                                } else {
                                    Log.d("SyncStatus", "Sync Failed")
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    Log.d(TAG, "onFailure: Sync Failed" + t.message)
                }
            })
        }
    }
}