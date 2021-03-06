package com.cabral.emaishapay.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.database.BuyInputsDB_Handler;
import com.cabral.emaishapay.database.BuyInputsDB_Manager;
import com.cabral.emaishapay.models.banner_model.BannerData;
import com.cabral.emaishapay.models.category_model.CategoryData;
import com.cabral.emaishapay.models.pages_model.PagesData;
import com.cabral.emaishapay.models.pages_model.PagesDetails;
import com.cabral.emaishapay.models.product_model.GetAllProducts;
import com.cabral.emaishapay.models.product_model.ProductData;
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
import com.cabral.emaishapay.models.device_model.DeviceInfo;
import com.cabral.emaishapay.models.user_model.UserData;
import com.cabral.emaishapay.utils.Utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.Header;


/**
 * StartAppRequests contains some Methods and API Requests, that are Executed on Application Startup
 **/

public class StartAppRequests {
    private static final String TAG = "StartAppRequests";
    private List<Regions> dataList = new ArrayList<>();
    private DbHandlerSingleton dbHandler;
    private static BuyInputsDB_Handler db_handler;
    private Context context;
    private List<HashMap<String, String>> productList;

    private EmaishaPayApp emaishaPayApp = new EmaishaPayApp();




    public StartAppRequests(Context context) {
        emaishaPayApp = ((EmaishaPayApp) EmaishaPayApp.getContext());
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
        SyncProductData();
        
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

    public void SyncProductData() {
        if (Connectivity.isConnected(context)) {
            String sync_status = "0";
            productList = dbHandler.getUnsyncedProducts(sync_status);
            for (int i = 0; i < productList.size(); i++) {
                Log.e("WAlletIDError",productList.get(i).get("user_id")+"");
                saveProductList(
                        productList.get(i).get("product_id"),
                        productList.get(i).get("unique_id"),
                        productList.get(i).get("measure_id"),
                        productList.get(i).get("user_id"),
                        productList.get(i).get("selected_product_id"),
                        productList.get(i).get("product_manufacturer"),
                        productList.get(i).get("product_name"),
                        productList.get(i).get("product_code"),
                        productList.get(i).get("selected_category_id"),
                        productList.get(i).get("product_category"),
                        productList.get(i).get("product_buy_price"),
                        productList.get(i).get("product_sell_price"),
                        productList.get(i).get("product_supplier"),
                        productList.get(i).get("product_image"),
                        productList.get(i).get("product_stock"),
                        productList.get(i).get("product_unit"),
                        productList.get(i).get("sync_status")

                );


            }
        }

    }

    public void saveProductList(String product_id,String unique_product_id,String measure_id,String user_id,String selected_product_id,String product_manufacturer,
                                String product_name,String product_code,String selected_category_id,String  product_category,String product_buy_price, String product_sell_price,
                                String product_supplier,String product_image,String product_stock,String product_unit,String sync_status) {
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        Call<ResponseBody> call = BuyInputsAPIClient
                .getInstance()
                .postProduct(access_token,unique_product_id,measure_id,user_id,selected_product_id,product_buy_price,product_sell_price,
                        product_supplier,Integer.parseInt(product_stock),product_manufacturer,product_category,product_name
                );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    //update product status
                    boolean check = dbHandler.updateProductSyncStatus(product_id,"1");
                    if(check){
                        Log.d("Sync Status", "Product Synced");

                    }else{

                        Log.d("Sync Status", "Sync Failed");
                    }



                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: Sync Failed"+ t.getMessage());

            }
        });
    }
    //*********** API Request Method to Fetch App Banners ********//



    public void RequestAllCategories() {
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
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
                    String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
                    Call<UserData> call = APIClient.getWalletInstance()
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
                                            device.getDeviceSystemOS()
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
