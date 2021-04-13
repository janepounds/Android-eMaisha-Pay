package com.cabral.emaishapay.modelviews;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.network.DataRepository;
import com.cabral.emaishapay.network.db.entities.EcProduct;
import com.cabral.emaishapay.utils.Resource;

import java.util.List;

public class ShopPOSModelView extends AndroidViewModel {
    private static final String TAG = "ShopPOSModelView";
    private static final String QUERY_KEY = "QUERY";
    private final DataRepository mRepository;
    private final SavedStateHandle mSavedStateHandler;
    private final LiveData<Resource<List<EcProduct>>> products;
    private String wallet_id;


    public ShopPOSModelView(@NonNull Application application,@NonNull SavedStateHandle savedStateHandle) {
        super(application);
        this.mSavedStateHandler=savedStateHandle;
        this.wallet_id= WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, application.getApplicationContext());
        mRepository=DataRepository.getOurInstance(application.getApplicationContext());

        products= Transformations.switchMap(
                mSavedStateHandler.getLiveData(QUERY_KEY),
                (Function<CharSequence, LiveData<Resource<List<EcProduct>>>>) query -> {
                    return mRepository.getProducts( wallet_id,query );

                });

    }


    public LiveData<Resource<List<EcProduct>>> getProducts() {


      return products;
    }


    public void setQuery(CharSequence query) {
        // Save the user's query into the SavedStateHandle.
        // This ensures that we retain the value across process death
        // and is used as the input into the Transformations.switchMap above
        mSavedStateHandler.set(QUERY_KEY, query);
    }



}
