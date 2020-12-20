package com.cabral.emaishapay.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cabral.emaishapay.database.BuyInputsDB_Handler;
import com.cabral.emaishapay.database.BuyInputsDB_Manager;
import com.cabral.emaishapay.models.pages_model.PagesData;
import com.cabral.emaishapay.models.pages_model.PagesDetails;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.database.DbHandlerSingleton;
import com.cabral.emaishapay.models.address_model.RegionDetails;
import com.cabral.emaishapay.models.address_model.Regions;
import com.cabral.emaishapay.models.device_model.AppSettingsData;
import com.cabral.emaishapay.models.device_model.DeviceInfo;
import com.cabral.emaishapay.models.user_model.UserData;
import com.cabral.emaishapay.utils.Utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


/**
 * StartAppRequests contains some Methods and API Requests, that are Executed on Application Startup
 **/

public class StartAppRequests {
    private static final String TAG = "StartAppRequests";
    private List<Regions> dataList = new ArrayList<>();
    private DbHandlerSingleton dbHandler;
    private static BuyInputsDB_Handler db_handler;
    private Context context;

    private EmaishaPayApp emaishaPayApp = new EmaishaPayApp();


    public StartAppRequests(Context context) {
        emaishaPayApp = ((EmaishaPayApp) context.getApplicationContext());
        db_handler = new BuyInputsDB_Handler();
        dbHandler =DbHandlerSingleton.getHandlerInstance(context);
        BuyInputsDB_Manager.initializeInstance(db_handler);
        this.context = context;
    }



    //*********** Contains all methods to Execute on Startup ********//

    public void StartRequests(){

        //RequestBanners();
        RequestAllRegions();
        RequestStaticPagesData();
        
    }

    public void RequestAllRegions() {
        dbHandler = DbHandlerSingleton.getHandlerInstance(context);
        int regionId =dbHandler.getMaxRegionId();
        Log.d(TAG, "RequestAllRegions: "+ regionId);
        Call<Regions> call = ExternalAPIClient.getInstance()
                .getAllRegions(regionId);
        try {
            Response<Regions> response = call.execute();

            if (response.isSuccessful()) {


                Regions regionsData = null;
                regionsData = response.body();
                //Log.e("DataCheck0: ",appSettingsData.getAppDetails().getMaintenance_text());
                String strJson = new Gson().toJson(regionsData);
                List<RegionDetails> regionDetails = regionsData.getData();

                 dbHandler.insertRegionDetails(regionDetails);
//                if (!TextUtils.isEmpty(regionsData.getSuccess()))
//                    emaishaPayApp.setAppSettingsDetails(regionsData.getData());
                Log.d(TAG, "RequestAllRegions: " + regionDetails);
            }
            else {
                Log.e(TAG, "RequestAllRegions: Response is not successful");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //*********** Request Static Pages Data from the Server ********//
    
    private void RequestStaticPagesData() {
    
        ConstantValues.ABOUT_US = emaishaPayApp.getString(R.string.lorem_ipsum);
        ConstantValues.TERMS_SERVICES = emaishaPayApp.getString(R.string.lorem_ipsum);
        ConstantValues.PRIVACY_POLICY = emaishaPayApp.getString(R.string.lorem_ipsum);
        ConstantValues.REFUND_POLICY = emaishaPayApp.getString(R.string.lorem_ipsum);
        ConstantValues.A_Z = emaishaPayApp.getString(R.string.lorem_ipsum);

        Call<PagesData> call = ExternalAPIClient.getInstance()
                .getStaticPages
                        (
                                ConstantValues.LANGUAGE_ID
                        );
    
        try {
            Response<PagesData> response = call.execute();
    
            PagesData pages = new PagesData();
            
            if (response.isSuccessful()) {
                
                pages = response.body();
    
                if (pages.getSuccess().equalsIgnoreCase("1")) {

                    emaishaPayApp.setStaticPagesDetails(pages.getPagesData());
        
                    for (int i=0;  i<pages.getPagesData().size();  i++) {
                        PagesDetails page = pages.getPagesData().get(i);
            
                        if (page.getSlug().equalsIgnoreCase("about-us")) {
                            ConstantValues.ABOUT_US = page.getDescription();
                        }
                        else if (page.getSlug().equalsIgnoreCase("term-services")) {
                            ConstantValues.TERMS_SERVICES = page.getDescription();
                        }
                        else if (page.getSlug().equalsIgnoreCase("privacy-policy")) {
                            ConstantValues.PRIVACY_POLICY = page.getDescription();
                        }
                        else if (page.getSlug().equalsIgnoreCase("refund-policy")) {
                            ConstantValues.REFUND_POLICY = page.getDescription();
                        }
                        else if(page.getSlug().equalsIgnoreCase("A-Z")){
                            ConstantValues.A_Z = page.getDescription();
                        }
                    }
                }
            }
        
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
}
