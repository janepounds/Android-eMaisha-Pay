package com.cabral.emaishapay.fragments.shop_fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import com.cabral.emaishapay.activities.WalletHomeActivity
import com.cabral.emaishapay.adapters.Shop.MerchantOrdersAdapter
import com.cabral.emaishapay.customs.DialogLoader
import com.cabral.emaishapay.databinding.FragmentShopOrdersBinding
import com.cabral.emaishapay.modelviews.MerchantOrdersViewModel
import com.cabral.emaishapay.network.pagingdata.MerchantOrderRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

//MerchantOrdersFragment

class MerchantOrdersFragment: Fragment() {
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
                adapter.submitData(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentShopOrdersBinding.inflate(layoutInflater)
        val wallet_id = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context)
        // get the view model
        viewModel = ViewModelProvider(this, MerchantOrdersViewModel.ViewModelFactory( MerchantOrderRepository( Integer.parseInt(wallet_id)) ))
                .get(MerchantOrdersViewModel::class.java)

        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.ordersRecycler.addItemDecoration(decoration)

        initAdapter()
        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        search(query)
        initSearch(query)

        return binding.root
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, binding.etxtSearchOrder.text.trim().toString())
    }

    private fun initAdapter() {
        binding.ordersRecycler.adapter = adapter
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