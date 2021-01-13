package com.myfarmnow.myfarmcrop.models;

public interface CropInventory {
    final String CONST_FERTILIZER_INVENTORY = "Fertilizer";
    final String CONST_SEEDS_INVENTORY = "Seed";
    final String CONST_SPRAY_INVENTORY = "Spray";

    String getBatchNumber();
    String getInventoryType();
    String getName();
    float getInitialQuantity();
    float getAmountConsumed();
    float calculateAmountLeft();
    String getUsageUnits();
    String getId();


}
