package com.cabral.emaishapay.network.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.cabral.emaishapay.models.product_model.Attribute;
import com.cabral.emaishapay.models.product_model.Image;
import com.cabral.emaishapay.models.product_model.ProductCategories;
import com.cabral.emaishapay.models.product_model.ProductMeasure;
import com.cabral.emaishapay.models.ratings.RatingDataList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ShopProductDetails {

    @ColumnInfo(name ="server_time")
    private String serverTime;
    @ColumnInfo(name ="products_id")
    private int productsId;
    @ColumnInfo(name ="products_quantity")
    private int productsQuantity;
    @ColumnInfo(name ="products_model")
    private String productsModel;
    @ColumnInfo(name ="products_image")
    private String productsImage;
    @ColumnInfo(name ="discount_price")
    private String discountPrice;
    @ColumnInfo(name ="products_date_added")
    private String productsDateAdded;
    @ColumnInfo(name ="products_last_modified")
    private String productsLastModified;
    @ColumnInfo(name ="products_date_available")
    private String productsDateAvailable;
    @ColumnInfo(name ="productsMeasure")
    private List<ProductMeasure> productsMeasure;
    @ColumnInfo(name ="products_status")
    private int productsStatus;
    @ColumnInfo(name ="products_ordered")
    private int productsOrdered;
    @ColumnInfo(name ="products_liked")
    private int productsLiked;
    @ColumnInfo(name ="language_id")
    private int languageId;
    @ColumnInfo(name ="products_name")
    private String productsName;
    @ColumnInfo(name ="products_description")
    private String productsDescription;
    @ColumnInfo(name ="products_url")
    private String productsUrl;
    @ColumnInfo(name ="defaultStock")
    private int productsDefaultStock;
    @ColumnInfo(name ="products_viewed")
    private int productsViewed;
    @ColumnInfo(name ="products_type")
    private int productsType;
    @ColumnInfo(name ="products_tax_class_id")
    private int productsTaxClassId;
    @ColumnInfo(name ="tax_rates_id")
    private int taxRatesId;
    @ColumnInfo(name ="tax_zone_id")
    private int taxZoneId;
    @ColumnInfo(name ="tax_class_id")
    private int taxClassId;
    @ColumnInfo(name ="tax_priority")
    private int taxPriority;
    @ColumnInfo(name ="tax_rate")
    private String taxRate;
    @ColumnInfo(name ="tax_description")
    private String taxDescription;
    @ColumnInfo(name ="tax_class_title")
    private String taxClassTitle;
    @ColumnInfo(name ="tax_class_description")
    private String taxClassDescription;
    @ColumnInfo(name ="sort_order")
    private int sortOrder;
    @ColumnInfo(name ="isLiked")
    private String isLiked;
    @ColumnInfo(name ="manufacturers_id")
    private int manufacturersId;
    @ColumnInfo(name ="manufacturers_name")
    private String manufacturersName;
    @ColumnInfo(name ="manufacturers_image")
    private String manufacturersImage;
    @ColumnInfo(name ="manufacturers_url")
    private String manufacturersUrl;
    @ColumnInfo(name ="flash_start_date")
    private String flashStartDate;
    @ColumnInfo(name ="flash_expires_date")
    private String flashExpireDate;
    @ColumnInfo(name ="flash_price")
    private String flashPrice;
    @ColumnInfo(name ="date_added")
    private String dateAdded;
    @ColumnInfo(name ="last_modified")
    private String lastModified;
    @ColumnInfo(name ="isSale_product")
    private String isSaleProduct;
    @ColumnInfo(name ="attributes_price")
    private String attributesPrice;
    @ColumnInfo(name ="final_price")
    private String productsFinalPrice;
    @ColumnInfo(name ="total_price")
    private String totalPrice;
    @ColumnInfo(name ="customers_basket_quantity")
    private int customersBasketQuantity;
    @ColumnInfo(name ="category_ids")
    private String categoryIDs;
    @ColumnInfo(name ="category_names")
    private String categoryNames;
    @ColumnInfo(name ="images")
    private List<Image> images = new ArrayList<Image>();
    @ColumnInfo(name ="categories")
    private List<ProductCategories> categories = new ArrayList<ProductCategories>();
    @ColumnInfo(name ="attributes")
    private List<Attribute> attributes = new ArrayList<Attribute>();
    @ColumnInfo(name ="reviewed_customers")
    private List<RatingDataList> ratingDataLists = new ArrayList<>();
    @ColumnInfo(name ="rating")
    private float rating;
    @ColumnInfo(name ="total_user_rated")
    private int total_user_rated;
    @ColumnInfo(name ="five_ratio")
    private int five_ratio;
    @ColumnInfo(name ="four_ratio")
    private int four_ratio;
    @ColumnInfo(name ="three_ratio")
    private int three_ratio;
    @ColumnInfo(name ="two_ratio")
    private int two_ratio;
    @ColumnInfo(name ="one_ratio")
    private int one_ratio;
    @ColumnInfo(name ="vendors_id")
    private int vendors_id;
    private String selectedproductsPrice;

    private  String seletedProductsWeightUnits,seletedProductsWeight;

    public ShopProductDetails(String serverTime, int productsId, int productsQuantity, String productsModel, String productsImage, String discountPrice, String productsDateAdded, String productsLastModified, String productsDateAvailable, List<ProductMeasure> productsMeasure, int productsStatus, int productsOrdered, int productsLiked, int languageId, String productsName, String productsDescription, String productsUrl, int productsDefaultStock, int productsViewed, int productsType, int productsTaxClassId, int taxRatesId, int taxZoneId, int taxClassId, int taxPriority, String taxRate, String taxDescription, String taxClassTitle, String taxClassDescription, int sortOrder, String isLiked, int manufacturersId, String manufacturersName, String manufacturersImage, String manufacturersUrl, String flashStartDate, String flashExpireDate, String flashPrice, String dateAdded, String lastModified, String isSaleProduct, String attributesPrice, String productsFinalPrice, String totalPrice, int customersBasketQuantity, String categoryIDs, String categoryNames, List<Image> images, List<ProductCategories> categories, List<Attribute> attributes, List<RatingDataList> ratingDataLists, float rating, int total_user_rated, int five_ratio, int four_ratio, int three_ratio, int two_ratio, int one_ratio, int vendors_id, String selectedproductsPrice, String seletedProductsWeightUnits, String seletedProductsWeight) {
        this.serverTime = serverTime;
        this.productsId = productsId;
        this.productsQuantity = productsQuantity;
        this.productsModel = productsModel;
        this.productsImage = productsImage;
        this.discountPrice = discountPrice;
        this.productsDateAdded = productsDateAdded;
        this.productsLastModified = productsLastModified;
        this.productsDateAvailable = productsDateAvailable;
        this.productsMeasure = productsMeasure;
        this.productsStatus = productsStatus;
        this.productsOrdered = productsOrdered;
        this.productsLiked = productsLiked;
        this.languageId = languageId;
        this.productsName = productsName;
        this.productsDescription = productsDescription;
        this.productsUrl = productsUrl;
        this.productsDefaultStock = productsDefaultStock;
        this.productsViewed = productsViewed;
        this.productsType = productsType;
        this.productsTaxClassId = productsTaxClassId;
        this.taxRatesId = taxRatesId;
        this.taxZoneId = taxZoneId;
        this.taxClassId = taxClassId;
        this.taxPriority = taxPriority;
        this.taxRate = taxRate;
        this.taxDescription = taxDescription;
        this.taxClassTitle = taxClassTitle;
        this.taxClassDescription = taxClassDescription;
        this.sortOrder = sortOrder;
        this.isLiked = isLiked;
        this.manufacturersId = manufacturersId;
        this.manufacturersName = manufacturersName;
        this.manufacturersImage = manufacturersImage;
        this.manufacturersUrl = manufacturersUrl;
        this.flashStartDate = flashStartDate;
        this.flashExpireDate = flashExpireDate;
        this.flashPrice = flashPrice;
        this.dateAdded = dateAdded;
        this.lastModified = lastModified;
        this.isSaleProduct = isSaleProduct;
        this.attributesPrice = attributesPrice;
        this.productsFinalPrice = productsFinalPrice;
        this.totalPrice = totalPrice;
        this.customersBasketQuantity = customersBasketQuantity;
        this.categoryIDs = categoryIDs;
        this.categoryNames = categoryNames;
        this.images = images;
        this.categories = categories;
        this.attributes = attributes;
        this.ratingDataLists = ratingDataLists;
        this.rating = rating;
        this.total_user_rated = total_user_rated;
        this.five_ratio = five_ratio;
        this.four_ratio = four_ratio;
        this.three_ratio = three_ratio;
        this.two_ratio = two_ratio;
        this.one_ratio = one_ratio;
        this.vendors_id = vendors_id;
        this.selectedproductsPrice = selectedproductsPrice;
        this.seletedProductsWeightUnits = seletedProductsWeightUnits;
        this.seletedProductsWeight = seletedProductsWeight;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public int getProductsId() {
        return productsId;
    }

    public void setProductsId(int productsId) {
        this.productsId = productsId;
    }

    public int getProductsQuantity() {
        return productsQuantity;
    }

    public void setProductsQuantity(int productsQuantity) {
        this.productsQuantity = productsQuantity;
    }

    public String getProductsModel() {
        return productsModel;
    }

    public void setProductsModel(String productsModel) {
        this.productsModel = productsModel;
    }

    public String getProductsImage() {
        return productsImage;
    }

    public void setProductsImage(String productsImage) {
        this.productsImage = productsImage;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getProductsDateAdded() {
        return productsDateAdded;
    }

    public void setProductsDateAdded(String productsDateAdded) {
        this.productsDateAdded = productsDateAdded;
    }

    public String getProductsLastModified() {
        return productsLastModified;
    }

    public void setProductsLastModified(String productsLastModified) {
        this.productsLastModified = productsLastModified;
    }

    public String getProductsDateAvailable() {
        return productsDateAvailable;
    }

    public void setProductsDateAvailable(String productsDateAvailable) {
        this.productsDateAvailable = productsDateAvailable;
    }

    public List<ProductMeasure> getProductsMeasure() {
        return productsMeasure;
    }

    public void setProductsMeasure(List<ProductMeasure> productsMeasure) {
        this.productsMeasure = productsMeasure;
    }

    public int getProductsStatus() {
        return productsStatus;
    }

    public void setProductsStatus(int productsStatus) {
        this.productsStatus = productsStatus;
    }

    public int getProductsOrdered() {
        return productsOrdered;
    }

    public void setProductsOrdered(int productsOrdered) {
        this.productsOrdered = productsOrdered;
    }

    public int getProductsLiked() {
        return productsLiked;
    }

    public void setProductsLiked(int productsLiked) {
        this.productsLiked = productsLiked;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public String getProductsName() {
        return productsName;
    }

    public void setProductsName(String productsName) {
        this.productsName = productsName;
    }

    public String getProductsDescription() {
        return productsDescription;
    }

    public void setProductsDescription(String productsDescription) {
        this.productsDescription = productsDescription;
    }

    public String getProductsUrl() {
        return productsUrl;
    }

    public void setProductsUrl(String productsUrl) {
        this.productsUrl = productsUrl;
    }

    public int getProductsDefaultStock() {
        return productsDefaultStock;
    }

    public void setProductsDefaultStock(int productsDefaultStock) {
        this.productsDefaultStock = productsDefaultStock;
    }

    public int getProductsViewed() {
        return productsViewed;
    }

    public void setProductsViewed(int productsViewed) {
        this.productsViewed = productsViewed;
    }

    public int getProductsType() {
        return productsType;
    }

    public void setProductsType(int productsType) {
        this.productsType = productsType;
    }

    public int getProductsTaxClassId() {
        return productsTaxClassId;
    }

    public void setProductsTaxClassId(int productsTaxClassId) {
        this.productsTaxClassId = productsTaxClassId;
    }

    public int getTaxRatesId() {
        return taxRatesId;
    }

    public void setTaxRatesId(int taxRatesId) {
        this.taxRatesId = taxRatesId;
    }

    public int getTaxZoneId() {
        return taxZoneId;
    }

    public void setTaxZoneId(int taxZoneId) {
        this.taxZoneId = taxZoneId;
    }

    public int getTaxClassId() {
        return taxClassId;
    }

    public void setTaxClassId(int taxClassId) {
        this.taxClassId = taxClassId;
    }

    public int getTaxPriority() {
        return taxPriority;
    }

    public void setTaxPriority(int taxPriority) {
        this.taxPriority = taxPriority;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public String getTaxDescription() {
        return taxDescription;
    }

    public void setTaxDescription(String taxDescription) {
        this.taxDescription = taxDescription;
    }

    public String getTaxClassTitle() {
        return taxClassTitle;
    }

    public void setTaxClassTitle(String taxClassTitle) {
        this.taxClassTitle = taxClassTitle;
    }

    public String getTaxClassDescription() {
        return taxClassDescription;
    }

    public void setTaxClassDescription(String taxClassDescription) {
        this.taxClassDescription = taxClassDescription;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(String isLiked) {
        this.isLiked = isLiked;
    }

    public int getManufacturersId() {
        return manufacturersId;
    }

    public void setManufacturersId(int manufacturersId) {
        this.manufacturersId = manufacturersId;
    }

    public String getManufacturersName() {
        return manufacturersName;
    }

    public void setManufacturersName(String manufacturersName) {
        this.manufacturersName = manufacturersName;
    }

    public String getManufacturersImage() {
        return manufacturersImage;
    }

    public void setManufacturersImage(String manufacturersImage) {
        this.manufacturersImage = manufacturersImage;
    }

    public String getManufacturersUrl() {
        return manufacturersUrl;
    }

    public void setManufacturersUrl(String manufacturersUrl) {
        this.manufacturersUrl = manufacturersUrl;
    }

    public String getFlashStartDate() {
        return flashStartDate;
    }

    public void setFlashStartDate(String flashStartDate) {
        this.flashStartDate = flashStartDate;
    }

    public String getFlashExpireDate() {
        return flashExpireDate;
    }

    public void setFlashExpireDate(String flashExpireDate) {
        this.flashExpireDate = flashExpireDate;
    }

    public String getFlashPrice() {
        return flashPrice;
    }

    public void setFlashPrice(String flashPrice) {
        this.flashPrice = flashPrice;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getIsSaleProduct() {
        return isSaleProduct;
    }

    public void setIsSaleProduct(String isSaleProduct) {
        this.isSaleProduct = isSaleProduct;
    }

    public String getAttributesPrice() {
        return attributesPrice;
    }

    public void setAttributesPrice(String attributesPrice) {
        this.attributesPrice = attributesPrice;
    }

    public String getProductsFinalPrice() {
        return productsFinalPrice;
    }

    public void setProductsFinalPrice(String productsFinalPrice) {
        this.productsFinalPrice = productsFinalPrice;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getCustomersBasketQuantity() {
        return customersBasketQuantity;
    }

    public void setCustomersBasketQuantity(int customersBasketQuantity) {
        this.customersBasketQuantity = customersBasketQuantity;
    }

    public String getCategoryIDs() {
        return categoryIDs;
    }

    public void setCategoryIDs(String categoryIDs) {
        this.categoryIDs = categoryIDs;
    }

    public String getCategoryNames() {
        return categoryNames;
    }

    public void setCategoryNames(String categoryNames) {
        this.categoryNames = categoryNames;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<ProductCategories> getCategories() {
        return categories;
    }

    public void setCategories(List<ProductCategories> categories) {
        this.categories = categories;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public List<RatingDataList> getRatingDataLists() {
        return ratingDataLists;
    }

    public void setRatingDataLists(List<RatingDataList> ratingDataLists) {
        this.ratingDataLists = ratingDataLists;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getTotal_user_rated() {
        return total_user_rated;
    }

    public void setTotal_user_rated(int total_user_rated) {
        this.total_user_rated = total_user_rated;
    }

    public int getFive_ratio() {
        return five_ratio;
    }

    public void setFive_ratio(int five_ratio) {
        this.five_ratio = five_ratio;
    }

    public int getFour_ratio() {
        return four_ratio;
    }

    public void setFour_ratio(int four_ratio) {
        this.four_ratio = four_ratio;
    }

    public int getThree_ratio() {
        return three_ratio;
    }

    public void setThree_ratio(int three_ratio) {
        this.three_ratio = three_ratio;
    }

    public int getTwo_ratio() {
        return two_ratio;
    }

    public void setTwo_ratio(int two_ratio) {
        this.two_ratio = two_ratio;
    }

    public int getOne_ratio() {
        return one_ratio;
    }

    public void setOne_ratio(int one_ratio) {
        this.one_ratio = one_ratio;
    }

    public int getVendors_id() {
        return vendors_id;
    }

    public void setVendors_id(int vendors_id) {
        this.vendors_id = vendors_id;
    }

    public String getSelectedproductsPrice() {
        return selectedproductsPrice;
    }

    public void setSelectedproductsPrice(String selectedproductsPrice) {
        this.selectedproductsPrice = selectedproductsPrice;
    }

    public String getSeletedProductsWeightUnits() {
        return seletedProductsWeightUnits;
    }

    public void setSeletedProductsWeightUnits(String seletedProductsWeightUnits) {
        this.seletedProductsWeightUnits = seletedProductsWeightUnits;
    }

    public String getSeletedProductsWeight() {
        return seletedProductsWeight;
    }

    public void setSeletedProductsWeight(String seletedProductsWeight) {
        this.seletedProductsWeight = seletedProductsWeight;
    }
}
