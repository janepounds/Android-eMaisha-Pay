package com.cabral.emaishapay.modelviews;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import com.cabral.emaishapay.AppExecutors;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.models.shop_model.ManufacturersResponse;
import com.cabral.emaishapay.network.DataRepository;
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient;
import com.cabral.emaishapay.network.db.entities.EcManufacturer;
import com.cabral.emaishapay.network.db.entities.EcProduct;
import com.cabral.emaishapay.network.db.entities.EcProductCategory;
import com.cabral.emaishapay.network.db.entities.UserCart;
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
    private final MutableLiveData<List<EcManufacturer>> manufacturers=new MutableLiveData<>();
    private final LiveData<Resource<List<EcProduct>>> repositorySource;
    private final LiveData<Integer>cartReipositorySource;
    private UserCart userCartProduct;

    private final String wallet_id;
    private String product_id;
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

        cartReipositorySource = mRepository.addToCart(product_id, userCartProduct);


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
                    Log.d(TAG, "onResponse: "+response.body().getManufacturers());

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

    public LiveData<Integer> addToCart() {


        return cartReipositorySource;
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
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mRepository.deleteProductStock(product);
                        }
                    });
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

    public long updateProductStock( String product_id, String product_buy_price, String product_sell_price, String product_supplier, int product_stock, String new_manufacturer_name, String new_category_name, String new_product_name,String product_code,String product_image,String product_weight_unit,String product_weight) {
        return mRepository.updateProductStock(product_id,product_code,new_category_name,"",product_buy_price,product_sell_price,product_supplier,product_image,product_stock+"",product_weight_unit,product_weight,new_manufacturer_name);
    }

    public long addManufacturer(EcManufacturer manufacturer) {
        return mRepository.addManufacturers(manufacturer);
    }


    public ArrayList<HashMap<String, String>> getOfflineManufacturers() {
        return mRepository.getOfflineManufacturers();
    }
    //get offline product categories
    public ArrayList<HashMap<String, String>> getOfflineProductCategories() {

        return mRepository.getOfflineProductCategories();
    }

    public ArrayList<HashMap<String, String>> getOfflineProductNames() {
        return mRepository.getOfflineProductNames();
    }

    public long addProduct(EcProduct product) {
        product.setSync_status("0");
        return mRepository.addProduct(product);
    }

    //get offline product categories
    public ArrayList<HashMap<String, String>> getProductSupplier() {

        return mRepository.getProductSupplier();
    }

    public ArrayList<HashMap<String, String>> getWeightUnit() {
        return mRepository.getWeightUnit();
    }
    public long addProductCategory(EcProductCategory category) {
        return mRepository.addProductCategory(category);
    }

    public long updateProductSyncStatus( String product_id, String status) {
        return mRepository.updateProductSyncStatus(product_id,status);
    }

    public long restockProductStock(String product_id, int product_stock ) {
        return mRepository.restockProductStock(product_id,product_stock);
    }
}
