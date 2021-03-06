package com.cabral.emaishapay.modelviews;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.network.DataRepository;
import com.cabral.emaishapay.network.db.entities.MerchantOrder;
import com.cabral.emaishapay.utils.Resource;

import java.util.List;

public class ShopOrdersModelView extends AndroidViewModel {
    private static final String QUERY_KEY = "QUERY";
    private static final String SALESQUERY_KEY = "SALESQUERY";
    private final DataRepository mRepository;
    private final SavedStateHandle mSavedStateHandler;
    private final LiveData<Resource<List<MerchantOrder>>> orderList;
    private final String wallet_id;

    public ShopOrdersModelView(@NonNull Application application,@NonNull SavedStateHandle savedStateHandle) {
        super(application);
        this.mSavedStateHandler=savedStateHandle;
        this.wallet_id= WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, application.getApplicationContext());
        mRepository=DataRepository.getOurInstance(application.getApplicationContext());


        orderList= Transformations.switchMap(
                mSavedStateHandler.getLiveData(QUERY_KEY,null),
                (Function<CharSequence, LiveData<Resource<List<MerchantOrder>>>>) query -> {

                    return mRepository.getOrderList(wallet_id,query);
                });


    }



    public LiveData<Resource<List<MerchantOrder>>> getOrderList() {
        return this.orderList;
    }

    public void setQuery(CharSequence query) {
        // Save the user's query into the SavedStateHandle.
        // This ensures that we retain the value across process death
        // and is used as the input into the Transformations.switchMap above
        mSavedStateHandler.set(QUERY_KEY, query);
    }

    public long updateOrder(String order_id,String status){
        return mRepository.updateOrder(order_id,status);
    }


}
