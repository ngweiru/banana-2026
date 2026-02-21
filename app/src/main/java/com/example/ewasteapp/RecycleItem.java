package com.example.ewasteapp;

public class RecycleItem {
    private String itemCode;
    private String category;
    private String weight;

    public RecycleItem(String itemCode, String category, String weight) {
        this.itemCode = itemCode;
        this.category = category;
        this.weight = weight;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
