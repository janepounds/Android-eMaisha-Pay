package com.cabral.emaishapay.network.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.cabral.emaishapay.network.db.daos.DefaultAddressDao;
import com.cabral.emaishapay.network.db.daos.EcManufacturerDao;
import com.cabral.emaishapay.network.db.daos.ShopOrderProductsDao;
import com.cabral.emaishapay.network.db.daos.ShopOrderDao;
import com.cabral.emaishapay.network.db.daos.EcProductCartDao;
import com.cabral.emaishapay.network.db.daos.EcProductCategoryDao;
import com.cabral.emaishapay.network.db.daos.EcProductWeightDao;
import com.cabral.emaishapay.network.db.daos.EcProductsDao;
import com.cabral.emaishapay.network.db.daos.EcUserCartAttributesDao;
import com.cabral.emaishapay.network.db.daos.EcUserCartDao;
import com.cabral.emaishapay.network.db.daos.RegionDetailsDao;
import com.cabral.emaishapay.network.db.entities.DefaultAddress;
import com.cabral.emaishapay.network.db.entities.EcManufacturer;
import com.cabral.emaishapay.network.db.entities.EcProductFts;
import com.cabral.emaishapay.network.db.entities.RegionDetails;
import com.cabral.emaishapay.network.db.entities.ShopOrder;
import com.cabral.emaishapay.network.db.entities.ShopOrderFts;
import com.cabral.emaishapay.network.db.entities.ShopOrderProducts;
import com.cabral.emaishapay.network.db.entities.EcProductCart;
import com.cabral.emaishapay.network.db.entities.EcProductCategory;
import com.cabral.emaishapay.network.db.entities.EcProductWeight;
import com.cabral.emaishapay.network.db.entities.EcProduct;
import com.cabral.emaishapay.network.db.entities.EcSupplier;
import com.cabral.emaishapay.network.db.entities.EcUserCart;
import com.cabral.emaishapay.network.db.entities.EcUserCartAttributes;
import com.cabral.emaishapay.network.db.relations.ShopOrderWithProducts;

@Database(entities = {DefaultAddress.class, EcManufacturer.class, ShopOrderProducts.class, ShopOrder.class, ShopOrderFts.class,
        EcProductCart.class, EcProductCategory.class, EcProductWeight.class, EcProduct.class, EcProductFts.class,
        EcSupplier.class, EcUserCart.class, EcUserCartAttributes.class, RegionDetails.class},
        version = 1,
        exportSchema = false
        )
public abstract class EmaishapayDb extends RoomDatabase {

    public abstract DefaultAddressDao defaultAddressDao();
    public abstract EcManufacturerDao ecManufacturerDao();
    public abstract ShopOrderProductsDao shopOrderProductsDao();
    public abstract ShopOrderDao shopOrderDao();
    public abstract EcProductCartDao ecProductCartDao();
    public abstract EcProductCategoryDao ecProductCategoryDao();
    public abstract EcProductsDao ecProductsDao();
    public abstract EcProductWeightDao ecProductWeightDao();
    public abstract EcUserCartAttributesDao ecUserCartAttributesDao();
    public abstract EcUserCartDao ecUserCartDao();
    public abstract RegionDetailsDao regionDetailsDao();



    public static EmaishapayDb INSTANCE;

    public static EmaishapayDb getDatabase(final Context context){
        if(INSTANCE==null){
            synchronized (RoomDatabase.class){
                if(INSTANCE==null){
                    INSTANCE= Room.databaseBuilder(context.getApplicationContext(), EmaishapayDb.class,"emaishapayDb16")
                            //.addMigrations(MIGRATION_3_4)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static void insertShopOrderWithProducts(final EmaishapayDb database,
                                      final ShopOrderWithProducts orderWithProducts) {
        database.runInTransaction(() -> {
            database.shopOrderDao().addOrder(orderWithProducts.shopOrder);

            for (ShopOrderProducts orderProduct:orderWithProducts.orderProducts ) {
                orderProduct.setOrder_id(orderWithProducts.shopOrder.getOrder_id());
                database.shopOrderProductsDao().insertShopProduct(orderProduct);
            }


        });

    }


}
