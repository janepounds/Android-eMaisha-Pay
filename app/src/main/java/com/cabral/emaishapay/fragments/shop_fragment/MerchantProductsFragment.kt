@file:JvmName("MerchantProductsFragment")
package com.cabral.emaishapay.fragments.shop_fragment

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cabral.emaishapay.DailogFragments.shop.AddShopProductFragment
import com.cabral.emaishapay.R
import com.cabral.emaishapay.activities.WalletHomeActivity
import com.cabral.emaishapay.adapters.Shop.MerchantProductsAdapter
import com.cabral.emaishapay.customs.DialogLoader
import com.cabral.emaishapay.modelviews.MerchantProductViewModel
import com.cabral.emaishapay.network.db.EmaishapayDb
import com.cabral.emaishapay.network.db.entities.EcManufacturer
import com.cabral.emaishapay.network.pagingdata.MerchantRepository
import kotlinx.android.synthetic.main.fragment_shop_products.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import java.util.*

class MerchantProductsFragment : Fragment() {

    val dialogLoader: DialogLoader by lazy {  DialogLoader(context) }
    private lateinit var viewModel: MerchantProductViewModel
    private lateinit var adapter : MerchantProductsAdapter

    private var manufacturers: List<EcManufacturer> = ArrayList()
    private var searchJob: Job? = null

    private fun loadProducts(query: String) {
        // Make sure we cancel the previous job before creating a new one
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchProducts(query).collectLatest {
                //Log.w("OrderSizeWarning",it.javaClass.fields.size.toString())

                if(it.javaClass.fields.size>0){

                    adapter.submitData(it)
                }

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        context?.let {
            val walletId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, it)
            viewModel = ViewModelProvider(this, MerchantProductViewModel.ViewModelFactory( MerchantRepository( Integer.parseInt(walletId), EmaishapayDb.getDatabase(it)) ))
                    .get(MerchantProductViewModel::class.java)
            adapter = MerchantProductsAdapter(viewModel, requireActivity().supportFragmentManager, it)
        }



        fabAdd.setOnClickListener {
            val ft = requireActivity().supportFragmentManager.beginTransaction()
            val prev = requireActivity().supportFragmentManager.findFragmentByTag("dialog")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)

            // Create and show the dialog.
            val addProductDialog: DialogFragment = AddShopProductFragment(manufacturers, getString(R.string.add_new_product), viewModel)
            addProductDialog.show(ft, "dialog")
        }

        return inflater.inflate(R.layout.fragment_shop_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToManufacturer(viewModel.getOnlineManufacturers())
        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        shopProductRecyclerview.addItemDecoration(decoration)

        initAdapter()
        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        loadProducts(query)
        initSearch(query)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, etxtSearch.text.trim().toString())
    }
    
    private fun initAdapter() {
        shopProductRecyclerview.adapter = adapter
        shopProductRecyclerview.layoutManager= LinearLayoutManager(context)
        
        adapter.addLoadStateListener { loadState ->
            // show empty list
            val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            showEmptyList(isListEmpty)
            Log.w("AdpterItemCount", adapter.itemCount.toString())

            // Only show the list if refresh succeeds.
           // ordersRecycler.isVisible = loadState.mediator?.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh.
            progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
            // Show the retry state if initial load or refresh fails.
            retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error


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
            imageNoProduct.visibility = View.VISIBLE
            shopProductRecyclerview.visibility = View.GONE
        } else {
            imageNoProduct.visibility = View.GONE
            shopProductRecyclerview.visibility = View.VISIBLE
        }
    }
    
    private fun initSearch(query: String) {
        etxtSearch.setText(query)

        etxtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateListFromInput()
                true
            } else {
                false
            }
        }

        etxtSearch.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateListFromInput()
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
                    .collect { shopProductRecyclerview.scrollToPosition(0) }
        }
    }

    private fun updateListFromInput() {
        etxtSearch.text.trim().let {
            if (it.isNotEmpty()) {
                loadProducts(it.toString())
            }
        }
    }

    private fun subscribeToManufacturer(onlineManufacturers: LiveData<List<EcManufacturer>>) {
        onlineManufacturers.observe(viewLifecycleOwner, Observer { myManfacturers: List<EcManufacturer> ->
            dialogLoader!!.showProgressDialog()
            if (myManfacturers != null && myManfacturers.size != 0) {
                saveManufacturersList(myManfacturers)
                adapter?.notifyDataSetChanged()
                dialogLoader!!.hideProgressDialog()
            }
        })
    }

    fun saveManufacturersList(manufacturers: List<EcManufacturer>) {
        this.manufacturers = manufacturers
    }

    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
        private const val DEFAULT_QUERY = ""
    }

}