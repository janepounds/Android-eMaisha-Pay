package com.cabral.emaishapay.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import androidx.lifecycle.ViewModelProvider;

import com.cabral.emaishapay.AppExecutors;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.database.BuyInputsDB_Handler;
import com.cabral.emaishapay.database.BuyInputsDB_Manager;
import com.cabral.emaishapay.models.banner_model.BannerData;
import com.cabral.emaishapay.models.category_model.CategoryData;
import com.cabral.emaishapay.models.pages_model.PagesData;
import com.cabral.emaishapay.models.pages_model.PagesDetails;
import com.cabral.emaishapay.modelviews.ShopProductsModelView;
import com.cabral.emaishapay.modelviews.SignUpModelView;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient;
import com.cabral.emaishapay.network.api_helpers.ExternalAPIClient;
import com.cabral.emaishapay.network.db.entities.EcProduct;
import com.cabral.emaishapay.network.db.entities.RegionDetails;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.models.address_model.Regions;
import com.cabral.emaishapay.models.device_model.DeviceInfo;
import com.cabral.emaishapay.models.user_model.UserData;
import com.cabral.emaishapay.utils.Utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * StartAppRequests contains some Methods and API Requests, that are Executed on Application Startup
 **/

public class StartAppRequests {
    private static final String TAG = "StartAppRequests";
    private static BuyInputsDB_Handler db_handler;
    private static Context context;

    private EmaishaPayApp emaishaPayApp = new EmaishaPayApp();




    public StartAppRequests(Context context) {
        emaishaPayApp = ((EmaishaPayApp) EmaishaPayApp.getContext());
        db_handler = new BuyInputsDB_Handler();
        BuyInputsDB_Manager.initializeInstance(db_handler);
        this.context = context;
    }



    //*********** Contains all methods to Execute on Startup ********//

    public void StartRequests(){
        RequestBanners();
        RequestAllRegions();
        RequestStaticPagesData();
        SyncProductData();
        
    }

    private void RequestBanners() {
        String request_id = WalletHomeActivity.generateRequestId();

        Call<BannerData> call = APIClient.getWalletInstance(context)
                .getWalletBannerAd("Cabral",request_id,"getProductListing");
        try {
            Response<BannerData> response = call.execute();

            if (response.isSuccessful() && response.body().getSuccess().equalsIgnoreCase("1")) {


                WalletHomeActivity.Banners = response.body().getData();

            }
            else {
                Log.e(TAG, "RequestBanners: Response is not successful");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void RequestAllRegions() {

        int regionId = DataRepository.getOurInstance(context).getMaxRegionId();
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
                 DataRepository.getOurInstance(context).insertRegionDetails(regionDetails);

                Log.d(TAG, "RequestAllRegions: " + regionDetails);
            }
            else {
                Log.e(TAG, "RequestAllRegions: Response is not successful");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static  void SyncProductData() {
        if(context==null){
            return;
        }else{

            String category=WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE, context);
            if(category.equalsIgnoreCase("Default") || TextUtils.isEmpty(category)){
                return;
            }
        }
        if (Connectivity.isConnected(context)) {
            String sync_status = "0";
            List<EcProduct> productsList = DataRepository.getOurInstance(context).getUnsyncedProducts(sync_status);

             //Log.w("unsyncedProducts",productsList.size()+" products");
            for (int i = 0; i < productsList.size(); i++) {
                Log.e("WAlletIDError",productsList.get(i).getId()+"");

                String wallet_id=WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context);


                saveProductList(
                        productsList.get(i).getProduct_id(),
                        productsList.get(i).getId(),
                        wallet_id,
                        productsList.get(i).getManufacturer(),
                        productsList.get(i).getProduct_name(),
                        productsList.get(i).getProduct_code(),
                        productsList.get(i).getProduct_category(),
                        productsList.get(i).getProduct_buy_price(),
                        productsList.get(i).getProduct_sell_price(),
                        productsList.get(i).getProduct_supplier(),
                        productsList.get(i).getProduct_image(),
                        productsList.get(i).getProduct_stock(),
                        productsList.get(i).getProduct_weight()+" "+productsList.get(i).getProduct_weight_unit(),
                        productsList.get(i).getSync_status()

                );
            }

        }

    }

    public static void saveProductList(String product_id, String unique_product_id, String user_id, String product_manufacturer,
                                       String product_name, String product_code, String product_category, String product_buy_price, String product_sell_price,
                                       String product_supplier, String product_image, String product_stock, String product_unit, String sync_status) {
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();

        Call<ResponseBody> call = BuyInputsAPIClient
                .getInstance()
                .postProduct(access_token,unique_product_id,user_id,product_id,product_buy_price,product_sell_price,
                        product_supplier,Integer.parseInt(product_stock),product_manufacturer,product_category,product_name
                );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    //update product status
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            //update Sync Status
                            long updateStatus =  DataRepository.getOurInstance(context).updateProductSyncStatus(product_id,"1");
                            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    if (updateStatus>0) {
                                        Log.d("SyncStatus", "Product Synced");
                                    } else {

                                        Log.d("SyncStatus", "Sync Failed");

                                    }
                                }
                            });


                        }
                    });



                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: Sync Failed"+ t.getMessage());

            }
        });
    }





    public void RequestAllCategories() {
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        ConstantValues.LANGUAGE_ID=1;
        Call<CategoryData> call = BuyInputsAPIClient.getInstance()
                .getAllCategories
                        (access_token,
                                ConstantValues.LANGUAGE_ID
                        );

        try {
            Response<CategoryData> response = call.execute();

            CategoryData categoryData = new CategoryData();

            if (response.isSuccessful()) {

                //String json= new Gson().toJson(response.body());
                categoryData = response.body();

                if (!TextUtils.isEmpty(categoryData.getSuccess()))
                    emaishaPayApp.setCategoriesList(categoryData.getData());

            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Error: ", e.getMessage() );
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


    //*********** Register Device to Admin Panel with the Device's Info ********//

    public static void RegisterDeviceForFCM(final Context context) {



        final DeviceInfo device = Utilities.getDeviceInfo(context);
        final String user_id = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context);



        //deviceID = FirebaseInstanceId.getInstance().getToken();
        if(user_id!=null)
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String deviceID =instanceIdResult.getToken();
                    String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
                    String request_id = WalletHomeActivity.generateRequestId();
                    String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);
                    Call<UserData> call = APIClient.getWalletInstance(context)
                            .registerDeviceToFCM
                                    (       access_token,
                                            deviceID,
                                            device.getDeviceType(),
                                            user_id,
                                            device.getDeviceRAM(),
                                            device.getDeviceProcessors(),
                                            device.getDeviceAndroidOS(),
                                            device.getDeviceLocation(),
                                            device.getDeviceModel(),
                                            device.getDeviceManufacturer(),
                                            device.getDeviceSystemOS(),
                                            request_id,
                                            category,
                                            "registerdevices"

                                    );

                    call.enqueue(new Callback<UserData>() {
                        @Override
                        public void onResponse(Call<UserData> call, Response<UserData> response) {

                            if (response.isSuccessful()) {
                                if (response.body().getStatus().equalsIgnoreCase("1")) {

                                    Log.i("notification", response.body().getMessage());

                                }
                                else {

                                    Log.i("notification", response.body().getMessage());

                                }
                            }
                            else {
                                Log.i("notification", response.errorBody().toString());
                            }
                        }

                        @Override
                        public void onFailure(Call<UserData> call, Throwable t) {
//                Toast.makeText(context, "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
    }


}
