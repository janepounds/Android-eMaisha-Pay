package com.cabral.emaishapay.modelviews;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.cabral.emaishapay.network.DataRepository;
import com.cabral.emaishapay.network.db.entities.RegionDetails;

import java.util.List;

public class SignUpModelView extends AndroidViewModel {

    private static final String TAG = "SignUpModelView";

    private final DataRepository mRepository;
    private MediatorLiveData<List<RegionDetails>> districts=new MediatorLiveData();

    public SignUpModelView(@NonNull Application application) {
        super(application);
        mRepository=DataRepository.getOurInstance(application.getApplicationContext());
        districts=mRepository.getRegionDetails("district");
    }

    public LiveData<List<RegionDetails>> getRegions(){
        return this.districts;
    }

    public LiveData<List<RegionDetails>> getSubcountyDetails(String belongs_to){
        return mRepository.getSubcountyDetails(belongs_to,"subcounty");
    }


    public LiveData<List<RegionDetails>> getVillageDetails(String belongs_to){
        return mRepository.getVillageDetails(belongs_to,"village");
    }
}
