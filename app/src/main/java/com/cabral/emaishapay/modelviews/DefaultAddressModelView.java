package com.cabral.emaishapay.modelviews;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.network.DataRepository;
import com.cabral.emaishapay.network.db.entities.DefaultAddress;
import com.cabral.emaishapay.network.db.entities.EcManufacturer;
import com.cabral.emaishapay.network.db.entities.EcProduct;
import com.cabral.emaishapay.network.db.entities.ShopOrder;
import com.cabral.emaishapay.utils.Resource;

import java.util.List;

public class DefaultAddressModelView extends AndroidViewModel {
    private final DataRepository mRepository;
    private final SavedStateHandle mSavedStateHandler;
    private LiveData<List<DefaultAddress>> repositorySource;
    String customer_id;

    public DefaultAddressModelView(@NonNull Application application, SavedStateHandle savedStateHandle) {
        super(application);
        this.mSavedStateHandler = savedStateHandle;
        this.customer_id = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, application.getApplicationContext());
        mRepository = DataRepository.getOurInstance(application.getApplicationContext());
        repositorySource = mRepository.getDefaultAddress(customer_id);
    }

    public LiveData<List<DefaultAddress>> getDefaultAddress() {
        return this.repositorySource;
    }


    public void insertDefaultAddress(String customer_id,String first_name,String last_name,String street_address,String postal_code,String city,String country,String contact,String latitude,String longitude,String is_default){
        mRepository.insertDefaultAddress(customer_id,first_name,last_name,street_address,postal_code,city,country,contact,latitude,longitude,is_default);
    }
}
