package com.cabral.emaishapay.modelviews;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import com.cabral.emaishapay.network.DataRepository;
import com.cabral.emaishapay.network.db.entities.EcProduct;
import com.cabral.emaishapay.network.db.entities.ShopOrderList;
import com.cabral.emaishapay.utils.Resource;

import java.util.List;

public class ShopOrdersModelView extends AndroidViewModel {
    public static final String QUERY_EXHAUSTED = "No more results.";
    private static final String QUERY_KEY = "QUERY";
    private final DataRepository mRepository;
    private final SavedStateHandle mSavedStateHandler;
    private final MediatorLiveData<Resource<List<ShopOrderList>>> orderList=new MediatorLiveData<>();
    private String wallet_id;
    private boolean cancelRequest;
    private long requestStartTime;
    private boolean isQueryExhausted;
    private boolean isPerformingQuery;

    public ShopOrdersModelView(@NonNull Application application, DataRepository mRepository, SavedStateHandle mSavedStateHandler, String wallet_id) {
        super(application);
        this.mRepository = mRepository;
        this.mSavedStateHandler = mSavedStateHandler;
        this.wallet_id = wallet_id;

        getOrders();
    }

    public void getOrders(){

    }

    public LiveData<Resource<List<ShopOrderList>>> getOrderList() {


        return Transformations.switchMap(
                mSavedStateHandler.getLiveData(QUERY_KEY),
                (Function<CharSequence, LiveData<Resource<List<ShopOrderList>>>>) query -> {
                    if (TextUtils.isEmpty(query)) {
                        return orderList;
                    }
                    return mRepository.getOrderList();
                });
    }

    public LiveData<Resource<List<ShopOrderList>>> searchOrderList() {
        return orderList;

//        return Transformations.switchMap(
//                mSavedStateHandler.getLiveData(QUERY_KEY),
//                (Function<CharSequence, LiveData<Resource<List<ShopOrderList>>>>) query -> {
//                    if (TextUtils.isEmpty(query)) {
//                        return orderList;
//                    }
//                    return mRepository.searchOrderList( query+"" );
//                });
    }
}
