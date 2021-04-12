package com.cabral.emaishapay.network;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient;
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
import com.cabral.emaishapay.network.db.entities.EcManufacturer;
import com.cabral.emaishapay.network.db.entities.EcProduct;
import com.cabral.emaishapay.network.db.entities.EcProductCart;
import com.cabral.emaishapay.network.db.entities.EcProductCategory;
import com.cabral.emaishapay.network.db.entities.RegionDetails;
import com.cabral.emaishapay.network.db.entities.ShopOrderDetails;
import com.cabral.emaishapay.utils.NetworkBoundResource;
import com.cabral.emaishapay.utils.Resource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;

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

    public static  DataRepository getOurInstance(Context context){
        if (ourInstance == null) {
            synchronized (DataRepository.class) {
                if (ourInstance == null) {
                    ourInstance = new DataRepository(context);
                }
            }
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


    //get unsynced products
    public List<EcProduct> getUnsyncedProducts(String sync_status) {

        return mEcProductsDao.getUnsyncedProducts(sync_status);
    }

    public void updateProductSyncStatus(String product_id,String sync_status) {
        mEcProductsDao.updateProductSyncStatus(product_id,sync_status);

    }

    //get offline product names
    public ArrayList<HashMap<String, String>> getOfflineProductNames() {
        ArrayList<HashMap<String, String>> productnames = new ArrayList<>();

        for (EcProduct product:mEcProductsDao.getOfflineProductNames()) {
            HashMap<String, String> map = new HashMap();
            map.put("product_name", product.getProduct_name());
            productnames.add(map);
        }
        return productnames;
    }

    //get offline product categories
    public ArrayList<HashMap<String, String>> getOfflineProductCategories() {
        ArrayList<HashMap<String, String>> categories = new ArrayList<>();

        for (EcProductCategory category : mEcProductCategoryDao.getOfflineProductCategories()) {
            HashMap<String, String> map = new HashMap();
            map.put("category_name", category.getCategory_name());
            categories.add(map);
        }

        return categories;
    }

    public void insertRegionDetails(List<RegionDetails> regionDetails) {
        mRegionDetailsDao.insertRegionDetails( regionDetails);
    }

    //*********GET LATEST ID FROM REGIONS DETAILS*************//
    public int getMaxRegionId() {
        return mRegionDetailsDao.getMaxRegionId();
    }

    //******GET DISTRICTS*****//

    public LiveData<List<RegionDetails>> getRegionDetails(String type) {
        return  mRegionDetailsDao.getRegionDetails(type);
    }

    //******GET SUB COUNTIES**********//
    public LiveData<List<RegionDetails>> getSubcountyDetails(String belongs_to, String subcounty){
        return  mRegionDetailsDao.getSubcountyDetails(belongs_to,subcounty);
    }

    //*********GET VILLAGES*************//
    public LiveData<List<RegionDetails>> getVillageDetails(String belongs_to, String subcounty) {
        return mRegionDetailsDao.getVillageDetails(belongs_to,subcounty);
    }

    public LiveData<RegionDetails> getRegionDetail( String name) {
        return mRegionDetailsDao.getRegionDetail(name);
    }
    //**********GET OFFLINE MANUFACTURERS************//
    public  ArrayList<HashMap<String, String>> getOfflineManufacturers() {
        ArrayList<HashMap<String, String>> manufacturers = new ArrayList<>();
        for (EcManufacturer manufacturer : mEcManufacturerDao.getOfflineManufacturers()) {
            HashMap<String, String> map = new HashMap();
            map.put("manufacturer", manufacturer.getManufacturer_name());
            manufacturers.add(map);
        }

        return manufacturers;
    }

    //**********ADD PRODUCT NAME *******************//
    public void addProductName(EcProduct product) {
      mEcProductsDao.addProductName(product);
    }

    //**********ADD PRODUCT CATEGORY *******************//
    public void addProductCategory(EcProductCategory productCategory) {
        mEcProductCategoryDao.addProductCategory(productCategory);
    }

    //**********ADD MANUFACTURERS *******************//
    public void addManufacturers(EcManufacturer manufacturer) {
        mEcManufacturerDao.addManufacturers(manufacturer);
    }


    //*********UPDATE PRODUCT QTY*****************//
    public void updateProductQty(String id, String qty) {

      mEcProductCartDao.updateProductQty(id,qty);

    }

    //******GET TOTAL PRODUCT PRICE ********//
    public double getTotalOrderPrice(String type) {

       List<ShopOrderDetails> order_details = new ArrayList<>();
        double total_price = 0;


        if (type.equals("monthly")) {

            String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());

            order_details =    mEcOrderDetailsDao.getMonthlyTotalPrice(currentMonth);

        } else if (type.equals("yearly")) {

            String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
            order_details= mEcOrderDetailsDao.getYearlyTotalPrice(currentYear);

        } else if (type.equals("daily")) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());

            order_details=  mEcOrderDetailsDao.getDailyTotalPrice(currentDate);

        } else {
            order_details=  mEcOrderDetailsDao.getTotalPrice();

        }


        if (order_details.size()>0) {
            for(int i=0;i<order_details.size();i++){
                double price = Double.parseDouble(order_details.get(i).getProduct_price());
                int qty = Integer.parseInt(order_details.get(i).getProduct_qty());
                double sub_total = price * qty;
                total_price = total_price + sub_total;
            }


        } else {
            total_price = 0;
        }

        return total_price;
    }


    //************GET CART ITEM COUNT ************//

    public int getCartItemCount() {
        return mEcProductCartDao.getCartItemCount();
    }

    //**************DELETE PRODUCT FROM CART********************************//
    public void deleteProductFromCart(String id) {

        mEcProductCartDao.deleteProductFromCart(id);

    }


    //*****************GET TOTAL PRICE**************************************************//
    public double getTotalPrice() {

        List<EcProductCart> products;
        products = mEcProductCartDao.getTotalPrice();
        double total_price = 0;

        if (products!=null) {
            for(int i=0;i<products.size();i++){
                double price = Double.parseDouble(products.get(i).getProduct_price());
                int qty = products.get(i).getProduct_qty();
                double sub_total = price * qty;
                total_price = total_price + sub_total;


            }


        } else {
            total_price = 0;
        }

        return total_price;
    }

    //**************GET PRODUCT NAME****************************************//

    public List<EcProduct> getProductName(String product_id) {

        return mEcProductsDao.getProductName(product_id);
    }

    //***********GET PRODUCT IMAGE***************************************************//
    public ArrayList<HashMap<String, String>> getProductImage(String product_id) {
        ArrayList<HashMap<String, String>> productnames = new ArrayList<>();

        for (EcProduct product:mEcProductsDao.getProductImage(product_id)) {
            HashMap<String, String> map = new HashMap();
            map.put("product_image", product.getProduct_image());
            productnames.add(map);
        }

       return productnames;

    }

    //******************GET ORDER HISTORY DATA****************************************************//

    public void addToCart(String product_id) {


        mEcProductCartDao.addToCart(product_id);

    }



    public LiveData<Resource<List<EcProduct>>> getProducts(String wallet_id, String key) {


        return new NetworkBoundResource<List<EcProduct>, List<EcProduct>>() {
            @Override
            protected void saveCallResult(@NonNull List<EcProduct> productList) {
                mEcProductsDao.addProduct( productList);
            }

            @NonNull
            @Override
            protected LiveData<List<EcProduct>> loadFromDb() {
                if (TextUtils.isEmpty(key)) {
                    return mEcProductsDao.getProducts();
                }
                return mEcProductsDao.getSearchProducts( key );
            }

            @Override
            protected boolean shouldFetch(@Nullable List<EcProduct> data) {
                if (TextUtils.isEmpty(key)) {
                    return true;
                }

                return false;
            }

            @NonNull
            @Override
            protected Call<List<EcProduct>> createCall() {

                Call<List<EcProduct>> call = BuyInputsAPIClient.getInstance().getMerchantProducts(wallet_id);
                return call;
            }
        }.getAsLiveData();

    }

}
