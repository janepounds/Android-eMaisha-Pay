package com.cabral.emaishapay.modelviews;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import com.cabral.emaishapay.network.DataRepository;
import com.cabral.emaishapay.network.db.entities.ShopOrderProducts;

import java.util.List;

public class ShopSalesModelView extends AndroidViewModel {
    private static final String TAG = "ShopPOSModelView";
    private static final String QUERY_KEY = "QUERY";
    private final DataRepository mRepository;
    private final SavedStateHandle mSavedStateHandler;
    private final LiveData<List<ShopOrderProducts>> orderDetails;
    private String order_id;

    public ShopSalesModelView(@NonNull Application application, SavedStateHandle mSavedStateHandler,String order_id) {
        super(application);
        this.mRepository = DataRepository.getOurInstance(application.getApplicationContext());
        this.mSavedStateHandler = mSavedStateHandler;
        this.order_id = order_id;

        this.orderDetails = Transformations.switchMap(
                mSavedStateHandler.getLiveData(QUERY_KEY),
                (Function<CharSequence, LiveData<List<ShopOrderProducts>>>) query -> {
                    return mRepository.getOrderDetailsList( order_id);

                });
    }


    public LiveData<List<ShopOrderProducts>> getOrderDetailsList() {
        return orderDetails;
    }


    public void setQuery(CharSequence query) {
        // Save the user's query into the SavedStateHandle.
        // This ensures that we retain the value across process death
        // and is used as the input into the Transformations.switchMap above
        mSavedStateHandler.set(QUERY_KEY, query);
    }


}
