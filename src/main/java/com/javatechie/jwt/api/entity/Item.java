package com.javatechie.jwt.api.entity;

/*
  @Author kalhara@bowsin
  @Created 10/1/2020 5:05 PM  
*/


import com.javatechie.jwt.api.util.UID;

public class Item {

    @UID(encrypt = true, decrypt = true)
    private String itemUID;
    private String itemName;

    public Item() {
    }

    public Item(String itemUID, String itemName) {
        this.itemUID = itemUID;
        this.itemName = itemName;
    }


    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemUID() {
        return itemUID;
    }

    public void setItemUID(String itemUID) {
        this.itemUID = itemUID;
    }
}
