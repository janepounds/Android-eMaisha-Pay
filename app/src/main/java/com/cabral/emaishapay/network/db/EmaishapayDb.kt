package com.cabral.emaishapay.network.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cabral.emaishapay.network.db.daos.*
import com.cabral.emaishapay.network.db.entities.*
import com.cabral.emaishapay.network.db.relations.CartItem
import com.cabral.emaishapay.network.db.relations.ShopOrderWithProducts


@Database(entities = [DefaultAddress::class, EcManufacturer::class, ShopOrderProducts::class, MerchantOrder::class, MerchantOrderFts::class, EcProductCategory::class, EcProductWeight::class, EcProduct::class, EcProductFts::class, EcSupplier::class, UserCart::class, UserCartAttributes::class, RegionDetails::class, RemoteKeys::class], version = 4,
        exportSchema = false)
abstract class EmaishapayDb : RoomDatabase() {
    abstract fun defaultAddressDao(): DefaultAddressDao?
    abstract fun ecManufacturerDao(): EcManufacturerDao?
    abstract fun shopOrderProductsDao(): ShopOrderProductsDao
    abstract fun shopOrderDao(): ShopOrderDao
    abstract fun ecProductCategoryDao(): EcProductCategoryDao?
    abstract fun ecProductsDao(): EcProductsDao?
    abstract fun ecProductWeightDao(): EcProductWeightDao?
    abstract fun regionDetailsDao(): RegionDetailsDao?
    abstract fun userCartAttributesDao(): UserCartAttributesDao
    abstract fun userCartDao(): UserCartDao
    abstract fun supplierDao(): EcSupplierDao?
    abstract fun merchantOrderDao(): MerchantOrderDao?
    abstract fun remoteKeysDao(): RemoteKeysDao?
    abstract fun merchantProductDao(): MerchantProductDao?

    companion object {
        var INSTANCE: EmaishapayDb? = null

        @JvmStatic fun getDatabase(context: Context): EmaishapayDb =
                INSTANCE ?: synchronized(this) {
                    INSTANCE
                            ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(
                        context.applicationContext,
                        EmaishapayDb::class.java, "emaishapayDb"
                ).addMigrations(MIGRATION_1_2).addMigrations(MIGRATION_2_3).addMigrations(MIGRATION_3_4)
                 .build()

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `RemoteKeys` (`id` TEXT, "
                        + "`prevKey` INTEGER,`nextKey` INTEGER, PRIMARY KEY(`id`))")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE ShopOrderFts")
                database.execSQL("DROP TABLE EcProductFts")

                database.execSQL("CREATE VIRTUAL TABLE `ShopOrderFts` USING fts4( "
                        + "`order_date`,`order_type`,`order_payment_method`, `order_status`," +
                        " `customer_name`, `customer_address` , `customer_cell` , `customer_email`, content=`ShopOrder` )")

                database.execSQL("CREATE VIRTUAL TABLE `EcProductFts` USING fts4("
                        + "`product_name`,`product_category`,`product_description`, `product_code`," +
                        " `product_supplier`, `product_weight_unit`, `product_weight`, `manufacturer`, content=`EcProduct`  )")


            }
        }
        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `remote_keys` ADD COLUMN `type` TEXT NOT NULL DEFAULT 'order' ")
            }
        }

        @JvmStatic fun insertShopOrderWithProducts(database: EmaishapayDb,
                                        orderWithProducts: ShopOrderWithProducts) {
            database.runInTransaction {
                database.shopOrderDao().addOrder(orderWithProducts.shopOrder)

                //Log.d("OrderProducts","==="+orderWithProducts.getOrderProducts().size());
                if (orderWithProducts.orderProducts != null) {
                    for (orderProduct in orderWithProducts.orderProducts) {
                        orderProduct.order_id = orderWithProducts.shopOrder.order_id
                        database.shopOrderProductsDao().insertShopProduct(orderProduct)
                    }
                }
            }
        }

    }
}
