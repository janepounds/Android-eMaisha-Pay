package com.cabral.emaishapay.fragments.wallet_fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.cabral.emaishapay.R
import com.cabral.emaishapay.activities.WalletHomeActivity
import com.cabral.emaishapay.adapters.WalletTransactionsAdapter
import com.cabral.emaishapay.databinding.TransactionListBinding
import com.cabral.emaishapay.modelviews.TranscationsViewModel
import com.cabral.emaishapay.network.db.EmaishapayDb
import com.cabral.emaishapay.network.pagingdata.MerchantRepository
import kotlinx.android.synthetic.main.transaction_list.*
import kotlinx.coroutines.Job

class WalletTransactionsFragment:Fragment() {
    private lateinit var binding:TransactionListBinding
    private lateinit var viewmodel:TranscationsViewModel
    private  val adapter = WalletTransactionsAdapter()
    private var searchJob: Job? = null
    private lateinit var appTitle:String
    var keyTitle = "KEY_TITLE"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = TransactionListBinding.inflate(layoutInflater)
        WalletHomeActivity.bottom_navigation_shop.visibility = View.GONE
        WalletHomeActivity.bottomNavigationView.visibility = View.GONE
        WalletHomeActivity.scanCoordinatorLayout.visibility = View.GONE
        val context:Context? = context
        if(context!=null){
            val walletId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context)
            viewmodel = ViewModelProvider(this, TranscationsViewModel.ViewModelFactory(MerchantRepository(Integer.parseInt(walletId), EmaishapayDb.getDatabase(context))))
                    .get(TranscationsViewModel::class.java)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) appTitle = requireArguments().getString(keyTitle).toString()
        if (appTitle.equals("settlements", ignoreCase = true)) {
            (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbarWalletTransactionsList)
            binding.toolbarWalletTransactionsList.title = appTitle
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayShowHomeEnabled(true)
            binding.arrowCashIn.setImageResource(R.drawable.bank)
            binding.arrowCashOut.setImageResource(R.drawable.ic_account_opening)
            binding.txtCashIn.text = "Mobile Money"
            binding.textCashOut.text = "Bank"
            binding.walletCashIn.setTextColor(ContextCompat.getColor(requireContext(), R.color.textRed))
            binding.walletCashOut.setTextColor(ContextCompat.getColor(requireContext(), R.color.textRed))
            binding.btnAddSettlement.visibility = View.VISIBLE
            initAdapter()
            getSettlements()
        } else {
            (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbarWalletTransactionsList)
            binding.toolbarWalletTransactionsList.setTitle(appTitle)
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayShowHomeEnabled(true)
            binding.arrowCashIn.setImageResource(R.drawable.ic_diagonal_arrow)
            binding.arrowCashOut.setImageResource(R.drawable.ic_cashin)
            binding.txtCashIn.text = "Cash In"
            binding.textCashOut.text = "Cash Out"
            binding.walletCashIn.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            binding.walletCashOut.setTextColor(ContextCompat.getColor(requireContext(), R.color.textRed))
            binding.btnAddSettlement.visibility = View.GONE
            initAdapter()
            actualStatementData()
        }
        binding.btnAddSettlement.setOnClickListener(View.OnClickListener {

            //To TransferMoney
            val args = Bundle()
            args.putString("KEY_ACTION", getString(R.string.settlements))
            WalletHomeActivity.navController.navigate(R.id.action_walletTransactionsListFragment_to_transferMoney, args)
        })
    }

    private fun initAdapter() {

            binding.statementRecyclerView.adapter = adapter
            binding.statementRecyclerView.layoutManager= LinearLayoutManager(context)

            adapter.addLoadStateListener { loadState ->
                // show empty list
//                val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
//                showEmptyList(isListEmpty)
//                Log.w("AdpterItemCount", adapter.itemCount.toString())

                // Only show the list if refresh succeeds.
                // binding.ordersRecycler.isVisible = loadState.mediator?.refresh is LoadState.NotLoading
                // Show loading spinner during initial load or refresh.
                binding.progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
                // Show the retry state if initial load or refresh fails.
                binding.retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error


                // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
                val errorState = loadState.source.append as? LoadState.Error
                        ?: loadState.source.prepend as? LoadState.Error
                        ?: loadState.append as? LoadState.Error
                        ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                            context,
                            "\uD83D\uDE28 Wooops ${it.error}",
                            Toast.LENGTH_LONG
                    ).show()
                }
            }

    }

    private fun actualStatementData() {
        TODO("Not yet implemented")
    }

    private fun getSettlements() {
        TODO("Not yet implemented")
    }


}