@file:JvmName("MerchantOrdersFragment")
package com.cabral.emaishapay.fragments.shop_fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cabral.emaishapay.activities.WalletHomeActivity
import com.cabral.emaishapay.adapters.Shop.MerchantOrdersAdapter
import com.cabral.emaishapay.customs.DialogLoader
import com.cabral.emaishapay.databinding.FragmentShopOrdersBinding
import com.cabral.emaishapay.modelviews.MerchantOrdersViewModel
import com.cabral.emaishapay.network.db.EmaishapayDb
import com.cabral.emaishapay.network.pagingdata.MerchantOrderRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

//MerchantOrdersFragment

class MerchantOrdersFragment : Fragment() {
    private lateinit var binding: FragmentShopOrdersBinding
    var dialogLoader: DialogLoader? = null
    private lateinit var viewModel: MerchantOrdersViewModel
    private val adapter = MerchantOrdersAdapter()

    private var searchJob: Job? = null

    private fun search(query: String) {
        // Make sure we cancel the previous job before creating a new one
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchOrders(query).collectLatest {
                //Log.w("OrderSizeWarning",it.javaClass.fields.size.toString())

                if(it.javaClass.fields.size>0){

                    adapter.submitData(it)
                }

            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentShopOrdersBinding.inflate(layoutInflater)

        val context : Context? =context
        if(context!=null){
            val walletId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context)
            // get the view model
            viewModel = ViewModelProvider(this, MerchantOrdersViewModel.ViewModelFactory( MerchantOrderRepository( Integer.parseInt(walletId), EmaishapayDb.getDatabase(context)) ))
                    .get(MerchantOrdersViewModel::class.java)

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.ordersRecycler.addItemDecoration(decoration)

        initAdapter()
        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        search(query)
        initSearch(query)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, binding.etxtSearchOrder.text.trim().toString())
    }


    private fun initAdapter() {
        binding.ordersRecycler.adapter = adapter
        val mLinearLayoutManager =  LinearLayoutManager(context)
        binding.ordersRecycler.layoutManager= mLinearLayoutManager
        adapter.addLoadStateListener { loadState ->

            // show empty list
            val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            showEmptyList(isListEmpty)
            Log.w("AdpterItemCount", adapter.itemCount.toString())

            // Only show the list if refresh succeeds.
            binding.ordersRecycler.isVisible = loadState.mediator?.refresh is LoadState.NotLoading
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

    private fun showEmptyList(show: Boolean) {
        if (show) {
            binding.imageNoProduct.visibility = View.VISIBLE
            binding.txtNoProducts.visibility = View.VISIBLE
            binding.ordersRecycler.visibility = View.GONE
        } else {
            binding.imageNoProduct.visibility = View.GONE
            binding.txtNoProducts.visibility = View.GONE
            binding.ordersRecycler.visibility = View.VISIBLE
        }
    }


    private fun initSearch(query: String) {
        binding.etxtSearchOrder.setText(query)

        binding.etxtSearchOrder.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }
        binding.etxtSearchOrder.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }

        // Scroll to top when the list is refreshed from network.
        lifecycleScope.launch {
            adapter.loadStateFlow
                    // Only emit when REFRESH LoadState for RemoteMediator changes.
                    .distinctUntilChangedBy { it.refresh }
                    // Only react to cases where Remote REFRESH completes i.e., NotLoading.
                    .filter { it.refresh is LoadState.NotLoading }
                    .collect { binding.ordersRecycler.scrollToPosition(0) }
        }
    }

    private fun updateRepoListFromInput() {
        binding.etxtSearchOrder.text.trim().let {
            if (it.isNotEmpty()) {
                search(it.toString())
            }
        }
    }

    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
        private const val DEFAULT_QUERY = ""
    }


}