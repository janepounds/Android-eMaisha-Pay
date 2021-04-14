package com.cabral.emaishapay.modelviews;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import com.cabral.emaishapay.network.DataRepository;
import com.cabral.emaishapay.network.db.relations.ShopOrderWithProducts;

import java.util.List;

public class ShopSalesModelView extends AndroidViewModel {
    private static final String TAG = "ShopSaLesModelView";
    private static final String SALESQUERY_KEY = "SALES_QUERY";
    private final DataRepository mRepository;
    private final SavedStateHandle mSavedStateHandler;
    private final LiveData<List<ShopOrderWithProducts>> sales;
    private String order_id;

    public ShopSalesModelView(@NonNull Application application, SavedStateHandle mSavedStateHandler) {
        super(application);
        this.mRepository = DataRepository.getOurInstance(application.getApplicationContext());
        this.mSavedStateHandler = mSavedStateHandler;


        sales = Transformations.switchMap(
                mSavedStateHandler.getLiveData(SALESQUERY_KEY,null),
                (Function<CharSequence, LiveData<List<ShopOrderWithProducts>>>) query -> {
                    if(TextUtils.isEmpty(query) ){
                        return  mRepository.getOrderSales();
                    }
                    return mRepository.SearchOrderSales(query);
                });
    }



    public void setSalesQuery(CharSequence query) {
        mSavedStateHandler.set(SALESQUERY_KEY, query);
    }

    //Contructor initialises the API data request to save on local DB, no need for a Network Data bound Resource here
    public LiveData<List<ShopOrderWithProducts>> getOrderSales() {
        return sales;
    }


}
