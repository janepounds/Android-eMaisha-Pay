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
    public static final String QUERY_EXHAUSTED = "No more results.";
    private static final String QUERY_KEY = "QUERY";
    private final DataRepository mRepository;
    private final SavedStateHandle mSavedStateHandler;
    private final MediatorLiveData<Resource<List<EcProduct>>> products=new MediatorLiveData<>();
    private String wallet_id;
    private boolean cancelRequest;
    private long requestStartTime;
    private boolean isQueryExhausted;
    private boolean isPerformingQuery;


    public ShopPOSModelView(@NonNull Application application, DataRepository mRepository, SavedStateHandle mSavedStateHandler) {
        super(application);
        this.mRepository = mRepository;
        this.mSavedStateHandler = mSavedStateHandler;
        this.wallet_id= WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, application.getApplicationContext());

        getProductData();

    }

    private void getProductData(){
        requestStartTime = System.currentTimeMillis();
        cancelRequest = false;
        isPerformingQuery = true;


        final LiveData<Resource<List<EcProduct>>> repositorySource=mRepository.getProducts(wallet_id);

        products.addSource(repositorySource, new Observer<Resource<List<EcProduct>>>() {
            @Override
            public void onChanged(Resource<List<EcProduct>> listResource) {

                if(!cancelRequest){
                    if(listResource!=null){

                        if(listResource.status == Resource.Status.SUCCESS){
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            //Log.d(TAG, "onChanged: page number: " + pageNumber);
                            Log.d(TAG, "onChanged: " + listResource.data);
                            isPerformingQuery=false;
                            if(listResource.data!=null){
                                products.setValue( new Resource<>(
                                        Resource.Status.SUCCESS,
                                        listResource.data,
                                        QUERY_EXHAUSTED
                                ));
                            }
                            products.removeSource(repositorySource);
                        }
                        else if(listResource.status== Resource.Status.ERROR) {
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            if (listResource.message.equals(QUERY_EXHAUSTED)) {
                                isQueryExhausted = true;
                            }
                            products.removeSource(repositorySource);
                        }
                        products.setValue(listResource);
                    }
                    else {
                        products.removeSource(repositorySource);
                    }
                }else{
                    products.removeSource(repositorySource);
                }

            }
        });
    }

    public LiveData<Resource<List<EcProduct>>> getProducts() {


        return Transformations.switchMap(
                mSavedStateHandler.getLiveData(QUERY_KEY),
                (Function<CharSequence, LiveData<Resource<List<EcProduct>>>>) query -> {
                    if (TextUtils.isEmpty(query)) {
                        return products;
                    }
                    return mRepository.getProducts( query+"" );
                });
    }

    public LiveData<Resource<List<EcProduct>>> getSearchedProducts() {

        return products;

//                Transformations.switchMap(
//                mSavedStateHandler.getLiveData(QUERY_KEY),
//                (Function<CharSequence, LiveData<Resource<List<EcProduct>>>>) query -> {
//                    if (TextUtils.isEmpty(query)) {
//                        return products;
//                    }
////                    return mRepository.getSearchProducts( query+"" );
//                });
    }


}
