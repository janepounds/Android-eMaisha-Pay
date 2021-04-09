package com.cabral.emaishapay.modelviews;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.models.SecurityQnsResponse;
import com.cabral.emaishapay.network.DataRepository;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.db.entities.RegionDetails;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpModelView extends AndroidViewModel {

    private static final String TAG = "SignUpModelView";

    private final DataRepository mRepository;
    private final LiveData<List<RegionDetails>> districts;
    private final MediatorLiveData<List<ArrayList<String>>> securityQnsFormatted=new MediatorLiveData<>();

    public SignUpModelView(@NonNull Application application) {
        super(application);
        mRepository=DataRepository.getOurInstance(application.getApplicationContext());
        districts=mRepository.getRegionDetails("district");
        RequestSecurityQns(application.getApplicationContext());
    }

    public LiveData<List<RegionDetails>> getDistricts(){
        return this.districts;
    }

    public LiveData<List<RegionDetails>> getSubcountyDetails(String belongs_to){
        return mRepository.getSubcountyDetails(belongs_to,"subcounty");
    }


    public LiveData<List<RegionDetails>> getVillageDetails(String belongs_to){
        return mRepository.getVillageDetails(belongs_to,"village");
    }


    public LiveData<List<ArrayList<String>>> getSecurityQuestions(){
        return this.securityQnsFormatted;
    }


    public void RequestSecurityQns(Context context){


        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);

        Call<SecurityQnsResponse> call = APIClient.getWalletInstance(context).getSecurityQns(request_id,category,"getSecurityQns");
        call.enqueue(new Callback<SecurityQnsResponse>() {
            @Override
            public void onResponse(Call<SecurityQnsResponse> call, @NotNull Response<SecurityQnsResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    List<SecurityQnsResponse.SecurityQns> securityQnsList = response.body().getSecurity_qnsList();
                    ArrayList<String> securityQnsSubList1 = new ArrayList<>();
                    ArrayList<String> securityQnsSubList2 = new ArrayList<>();
                    ArrayList<String> securityQnsSubList3 = new ArrayList<>();

                    //set security qns adapter
                    for(int i=0;i<securityQnsList.size();i++){
                       if(i<3){
                           securityQnsSubList1.add(securityQnsList.get(i).getSecurity_qn_name());
                       }else if(i<6){

                           securityQnsSubList2.add(securityQnsList.get(i).getSecurity_qn_name());
                       }else if(i<9){

                           securityQnsSubList3.add(securityQnsList.get(i).getSecurity_qn_name());
                       }
                    }

                    securityQnsFormatted.setValue(Arrays.asList(securityQnsSubList1,securityQnsSubList2,securityQnsSubList3));

                }else {
                    Log.e(TAG, "Unexpected  Error");
                }

            }

            @Override
            public void onFailure(@NotNull Call<SecurityQnsResponse> call, @NotNull Throwable t){
                Log.e(TAG, "Something got very wrong");
            }
        });

    }


    public  LiveData<RegionDetails> getRegionDetail(String name) {
       return mRepository.getRegionDetail(name);
    }
}
