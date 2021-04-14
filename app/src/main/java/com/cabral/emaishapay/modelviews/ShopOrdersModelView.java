package com.cabral.emaishapay.modelviews;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.network.DataRepository;
import com.cabral.emaishapay.network.db.entities.ShopOrder;
import com.cabral.emaishapay.network.db.relations.ShopOrderWithProducts;
import com.cabral.emaishapay.utils.Resource;

import java.util.List;

public class ShopOrdersModelView extends AndroidViewModel {
    private static final String QUERY_KEY = "QUERY";
    private static final String SALESQUERY_KEY = "SALESQUERY";
    private final DataRepository mRepository;
    private final SavedStateHandle mSavedStateHandler;
    private final LiveData<Resource<List<ShopOrder>>> orderList;
    private final LiveData<List<ShopOrderWithProducts>> sales;
    private String wallet_id;

    public ShopOrdersModelView(@NonNull Application application,@NonNull SavedStateHandle savedStateHandle) {
        super(application);
        this.mSavedStateHandler=savedStateHandle;
        this.wallet_id= WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, application.getApplicationContext());
        mRepository=DataRepository.getOurInstance(application.getApplicationContext());

        sales = Transformations.switchMap(
                mSavedStateHandler.getLiveData(SALESQUERY_KEY,null),
                (Function<CharSequence, LiveData<List<ShopOrderWithProducts>>>) query -> {
                    if(TextUtils.isEmpty(query) ){
                        return  mRepository.getOrderSales();
                    }
                    return mRepository.SearchOrderSales(query);
                });

        orderList= Transformations.switchMap(
                mSavedStateHandler.getLiveData(QUERY_KEY,null),
                (Function<CharSequence, LiveData<Resource<List<ShopOrder>>>>) query -> {

                    return mRepository.getOrderList(wallet_id,query);
                });


    }



    public LiveData<Resource<List<ShopOrder>>> getOrderList() {
        return this.orderList;
    }

    public void setQuery(CharSequence query) {
        // Save the user's query into the SavedStateHandle.
        // This ensures that we retain the value across process death
        // and is used as the input into the Transformations.switchMap above
        mSavedStateHandler.set(QUERY_KEY, query);
    }

    public void setSalesQuery(CharSequence query) {
        mSavedStateHandler.set(SALESQUERY_KEY, query);
    }

    //Contructor initialises the API data request to save on local DB, no need for a Network Data bound Resource here
    public LiveData<List<ShopOrderWithProducts>> getOrderSales() {
        return sales;
    }
}
