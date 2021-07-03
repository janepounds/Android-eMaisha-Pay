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


@Database(entities = [DefaultAddress::class, EcManufacturer::class, ShopOrderProducts::class, MerchantOrder::class, ShopOrderFts::class, EcProductCategory::class, EcProductWeight::class, EcProduct::class, EcProductFts::class, EcSupplier::class, UserCart::class, UserCartAttributes::class, RegionDetails::class, RemoteKeys::class], version = 2,
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
                ).addMigrations(MIGRATION_1_2)
                 .build()

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `RemoteKeys` (`id` INTEGER, "
                        + "`prevKey` INTEGER,`nextKey` INTEGER, PRIMARY KEY(`id`))")
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

        fun insertCartItem(database: EmaishapayDb,
                           cartItem: CartItem) {
            database.runInTransaction {
                database.userCartDao().addCartItem(cartItem.userCart)

                //Log.d("OrderProducts","==="+orderWithProducts.getOrderProducts().size());
                if (cartItem.userCartAttributesList != null) {
                    for (cartProductAttributes in cartItem.userCartAttributesList) {
                        database.userCartAttributesDao().addCartAttributes(cartProductAttributes)
                    }
                }
            }
        }

        fun updateCartItem(database: EmaishapayDb, cartItem: CartItem) {
            database.runInTransaction {
                database.userCartDao().updateCartItem(cartItem.userCart)

                //Log.d("OrderProducts","==="+orderWithProducts.getOrderProducts().size());
                if (cartItem.userCartAttributesList != null) {
                    for (cartProductAttributes in cartItem.userCartAttributesList) {
                        database.userCartAttributesDao().updateCartAttributes(cartProductAttributes)
                    }
                }
            }
        }
    }
}
