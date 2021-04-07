package com.cabral.emaishapay.network;

import android.content.Context;

import com.cabral.emaishapay.network.db.daos.DefaultAddressDao;
import com.cabral.emaishapay.network.db.EmaishapayDb;
import com.cabral.emaishapay.network.db.daos.EcCategoryDao;
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

public class DataRepository {
    private static final String TAG = "DataRepository";

    private static DataRepository ourInstance;
    private EmaishapayDb dbInstance;
    private  final DefaultAddressDao mDefaultAddressDao;
    private  final EcCategoryDao mEcCategoryDao;
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
        mEcCategoryDao=dbInstance.ecCategoryDao();
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


}
