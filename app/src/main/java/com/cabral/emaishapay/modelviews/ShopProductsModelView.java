package com.cabral.emaishapay.modelviews;

import android.app.Application;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AbstractSavedStateViewModelFactory;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.savedstate.SavedStateRegistryOwner;

import com.cabral.emaishapay.activities.ShopActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.models.shop_model.ManufacturersResponse;
import com.cabral.emaishapay.network.DataRepository;
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient;
import com.cabral.emaishapay.network.db.entities.EcManufacturer;
import com.cabral.emaishapay.network.db.entities.EcProduct;
import com.cabral.emaishapay.utils.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopProductsModelView extends AndroidViewModel {
    private static final String TAG = "ShopProductsModelView";
    public static final String QUERY_EXHAUSTED = "No more results.";
    private static final String QUERY_KEY = "QUERY";

    private final DataRepository mRepository;
    private final SavedStateHandle mSavedStateHandler;
    private MutableLiveData<List<EcManufacturer>> manufacturers=new MutableLiveData<>();
    private LiveData<Resource<List<EcProduct>>> repositorySource;
    private final MediatorLiveData<Resource<List<EcProduct>>> merchantProducts=new MediatorLiveData<>();

    private String wallet_id;
    private boolean cancelRequest;
    private long requestStartTime;

    public ShopProductsModelView(@NonNull Application application,
                                 @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        this.mSavedStateHandler=savedStateHandle;
        this.wallet_id= WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, application.getApplicationContext());
        mRepository=DataRepository.getOurInstance(application.getApplicationContext());

         repositorySource=
        Transformations.switchMap(
                mSavedStateHandler.getLiveData(QUERY_KEY,null),
                (Function<CharSequence, LiveData<Resource<List<EcProduct>>>>) query -> {

                    return mRepository.getProducts(wallet_id,query);
                });

        //executeFetchMerchantProducts( repositorySource );

        requestOnlineManufacturers();

    }



    private  void requestOnlineManufacturers(){

        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        Call<ManufacturersResponse> call1 = BuyInputsAPIClient
                .getInstance()
                .getManufacturers(access_token);
        call1.enqueue(new Callback<ManufacturersResponse>() {
            @Override
            public void onResponse(Call<ManufacturersResponse> call, Response<ManufacturersResponse> response) {
                if (response.isSuccessful()) {

                    manufacturers.setValue(response.body().getManufacturers());
                    //Log.d("Categories", String.valueOf(categories));

                } else {
                    Log.d("Failed", "Manufacturers Fetch failed");

                }
            }

            @Override
            public void onFailure(Call<ManufacturersResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("Failed", "Manufacturers Fetch failed");
            }
        });

    }

    public LiveData<List<EcManufacturer>> getOnlineManufacturers(){
        return this.manufacturers;
    }


    public LiveData<Resource<List<EcProduct>>> getMerchantProducts() {

        return repositorySource;
    }



    public void setQuery(CharSequence query) {
        // Save the user's query into the SavedStateHandle.
        // This ensures that we retain the value across process death
        // and is used as the input into the Transformations.switchMap above
        mSavedStateHandler.set(QUERY_KEY, query);
    }


    public void deleteProduct(EcProduct product) {
        Call<ResponseBody> call1 = BuyInputsAPIClient
                .getInstance()
                .deleteMerchantProduct(product.getProduct_id(),wallet_id);
        call1.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    mRepository.deleteProductStock(product);
                    //Log.d("Categories", String.valueOf(categories));

                } else {
                    Log.d("Failed", "Manufacturers Fetch failed");

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Log.d("Failed", "Manufacturers Fetch failed");
            }
        });


    }
}
