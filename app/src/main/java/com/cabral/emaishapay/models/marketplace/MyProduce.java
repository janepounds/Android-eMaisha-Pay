package com.cabral.emaishapay.models.marketplace;

public class MyProduce {

    public int id;

    public String name;

    public String variety;

    public String quantity;

    public String price;

    public String image;

    public String date;

    public String units;

    public MyProduce() {
    }

    public MyProduce(String name, String variety, String quantity, String price, String image, String date, String units) {
        this.name = name;
        this.variety = variety;
        this.quantity = quantity;
        this.price = price;
        this.image = image;
        this.date = date;
        this.units=units;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }
}
