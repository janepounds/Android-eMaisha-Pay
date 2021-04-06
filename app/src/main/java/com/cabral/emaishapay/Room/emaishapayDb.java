package com.cabral.emaishapay.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.cabral.emaishapay.Room.Dao.defaultAddressDao;
import com.cabral.emaishapay.Room.Dao.regionDetailsDao;
import com.cabral.emaishapay.Room.Entities.default_address;
import com.cabral.emaishapay.Room.Entities.regionDetails;

import java.util.List;

@Database(entities = {default_address.class, regionDetails.class},version = 1,exportSchema = false)
public abstract class emaishapayDb extends RoomDatabase {

    public abstract regionDetailsDao regionDetailsDao();
    public abstract defaultAddressDao defaultAddressDao();

    public static emaishapayDb INSTANCE;

    public static emaishapayDb getDatabase(final Context context){
        if(INSTANCE==null){
            synchronized (RoomDatabase.class){
                if(INSTANCE==null){
                    INSTANCE= Room.databaseBuilder(context.getApplicationContext(),emaishapayDb.class,"emaishapayDb10")
                            //.addMigrations(MIGRATION_3_4)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static void insertUserData(final emaishapayDb database,
                                      final default_address default_address,
                                      final List<regionDetails> regionDetails) {
        database.runInTransaction(() -> {
            database.regionDetailsDao().insertRegionDetails(regionDetails);
            database.defaultAddressDao().insertDefaultAddress(default_address);
        });

    }


}
