package com.cabral.emaishapay.network;

import android.content.ContentValues;
import android.content.Context;

import com.cabral.emaishapay.network.db.daos.DefaultAddressDao;
import com.cabral.emaishapay.network.db.EmaishapayDb;
import com.cabral.emaishapay.network.db.daos.EcManufacturerDao;
import com.cabral.emaishapay.network.db.daos.EcOrderDetailsDao;
import com.cabral.emaishapay.network.db.daos.EcOrderListDao;
import com.cabral.emaishapay.network.db.daos.EcProductCartDao;
import com.cabral.emaishapay.network.db.daos.EcProductCategoryDao;
import com.cabral.emaishapay.network.db.daos.EcProductWeightDao;
import com.cabral.emaishapay.network.db.daos.EcProductsDao;
import com.cabral.emaishapay.network.db.daos.EcUserCartAttributesDao;
import com.cabral.emaishapay.network.db.daos.EcUserCartDao;
import com.cabral.emaishapay.network.db.daos.RegionDetailsDao;
import com.cabral.emaishapay.network.db.entities.DefaultAddress;

import java.util.List;

public class DataRepository {
    private static final String TAG = "DataRepository";

    private static DataRepository ourInstance;
    private EmaishapayDb dbInstance;
    private  final DefaultAddressDao mDefaultAddressDao;
    private  final EcManufacturerDao mEcManufacturerDao;
    private  final EcOrderDetailsDao mEcOrderDetailsDao;
    private  final EcOrderListDao mEcOrderListDao;
    private  final EcProductCartDao mEcProductCartDao;
    private  final EcProductCategoryDao mEcProductCategoryDao;
    private  final EcProductsDao mEcProductsDao;
    private  final EcProductWeightDao mEcProductWeightDao;
    private  final EcUserCartAttributesDao mEcUserCartAttributesDao;
    private  final EcUserCartDao mEcUserCartDao;
    private  final RegionDetailsDao mRegionDetailsDao;

    private DataRepository(Context context) {
        this.dbInstance=EmaishapayDb.getDatabase(context);
        mDefaultAddressDao=dbInstance.defaultAddressDao();
        mEcManufacturerDao=dbInstance.ecManufacturerDao();
        mEcOrderDetailsDao=dbInstance.ecOrderDetailsDao();
        mEcOrderListDao=dbInstance.ecOrderListDao();
        mEcProductCartDao=dbInstance.ecProductCartDao();
        mEcProductCategoryDao=dbInstance.ecProductCategoryDao();
        mEcProductsDao=dbInstance.ecProductsDao();
        mEcProductWeightDao=dbInstance.ecProductWeightDao();
        mEcUserCartAttributesDao=dbInstance.ecUserCartAttributesDao();
        mEcUserCartDao=dbInstance.ecUserCartDao();
        mRegionDetailsDao = dbInstance.regionDetailsDao();
    }

    public DataRepository getOurInstance(Context context){
        if(ourInstance==null){
            ourInstance=new DataRepository(context);
        }
        return  ourInstance;
    }

    //*********** Insert default address Item ********//
    public void insertDefaultAddress(String customer_id,String entry_first_name,String entry_last_name,String entry_street_address,String entry_postal_code,String entry_city,String entry_country_id,String entry_contact,String latitude,String longitude,String is_default) {
        // get and open SQLiteDatabase Instance from static method of DB_Manager class

        mDefaultAddressDao.insertDefaultAddress(new DefaultAddress(
                customer_id, entry_first_name,
                entry_last_name,entry_street_address,
                entry_postal_code, entry_city,
                entry_country_id,entry_contact,
                latitude, longitude,is_default
        ));
    }


    //***********Default Address ********//
    public DefaultAddress getDefaultAddress(String customer_id){

        List<DefaultAddress> addressList=mDefaultAddressDao.getDefaultAddress(customer_id);
        if(addressList.size()>0)
        return addressList.get(0);

        return null;
    }


}
