package com.cabral.emaishapay.app;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;
import android.widget.Toast;

import androidx.multidex.MultiDexApplication;

import com.cabral.emaishapay.models.pages_model.PagesDetails;
import com.google.gson.Gson;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.models.device_model.AppSettingsDetails;
import com.cabral.emaishapay.models.TokenResponse;
import com.cabral.emaishapay.network.APIClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.google.common.io.BaseEncoding;


/**
 * App extending Application, is used to save some Lists and Objects with Application Context.
 **/

public class EmaishaPayApp extends MultiDexApplication {
    private static final String TAG = "EmaishaPayApp";
    // Application Context
    private static Context context;

    private AppSettingsDetails appSettingsDetails = null;
    private List<PagesDetails> staticPagesDetails = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        // set App Context
        context = this.getApplicationContext();

        // initialize DB_Handler and DB_Manager
//        db_handler = new BuyInputsDB_Handler();
//        dbHandler = MyFarmDbHandlerSingleton.getHandlerInstance(context);
//        BuyInputsDB_Manager.initializeInstance(db_handler);
//        String pkg_name = context.getPackageName();
//        ConstantValues.PKG_NAME = pkg_name;




    }

    //*********** Returns Application Context ********//

    public static Context getContext() {
        return context;
    }

    public AppSettingsDetails getAppSettingsDetails() {
        return appSettingsDetails;
    }

    public void setAppSettingsDetails(AppSettingsDetails appSettingsDetails) {
        this.appSettingsDetails = appSettingsDetails;
    }


    public List<PagesDetails> getStaticPagesDetails() {
        return staticPagesDetails;
    }

    public void setStaticPagesDetails(List<PagesDetails> staticPagesDetails) {
        this.staticPagesDetails = staticPagesDetails;
    }



    private String getSHA1(String packageName) {
        try {
            Signature[] signatures = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures;
            for (Signature signature : signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA-1");
                md.update(signature.toByteArray());
                //return BaseEncoding.base16().encode(md.digest());
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static  void checkWalletAccount(String email, String phonenumber) {

        if(ConstantValues.IS_USER_LOGGED_IN){
            Call<TokenResponse> call = APIClient.getWalletInstance()
                    .checkWalletAccount
                            (
                                    email,
                                    phonenumber
                            );

            call.enqueue(new Callback<TokenResponse>() {
                @Override
                public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {


                    if (response.isSuccessful()) {

                        if (response.body().getMessage().equalsIgnoreCase("Wallet Account found") ) {
                            ConstantValues.CUSTOMER_HAS_WALLET=true;
                        }
                        else{
                            // Get the Error Message from Response
                            ConstantValues.CUSTOMER_HAS_WALLET=false;
                            String message = response.body().getMessage();
                            Log.e("CheckWalletAccountError",message);
                        }

                    } else {
                        // Show the Error Message
                        ConstantValues.CUSTOMER_HAS_WALLET=false;
                       // Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<TokenResponse> call, Throwable t) {

                    Toast.makeText(context, "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
                }


            });
        }

    }
}


