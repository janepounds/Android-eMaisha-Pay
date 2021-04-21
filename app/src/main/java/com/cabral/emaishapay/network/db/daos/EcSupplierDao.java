package com.cabral.emaishapay.network.db.daos;

import androidx.room.Dao;
import androidx.room.Query;

import com.cabral.emaishapay.network.db.entities.EcProduct;
import com.cabral.emaishapay.network.db.entities.EcSupplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Dao
public interface EcSupplierDao {

    //get product supplier
    @Query("SELECT * FROM EcSupplier")
    List<EcSupplier> getProductSupplier();


    //get supplier Name
    @Query("SELECT * FROM EcSupplier WHERE suppliers_id=:supplier_id")
    List<EcSupplier> getSupplierName(String supplier_id);
}
