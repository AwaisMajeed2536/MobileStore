package misbah.naseer.mobilestore.model;

/**
 * Created by Awais Majeed on 08-Mar-18.
 */

public class PlaceOrderModel {
    private String itemName;
    private int quantiity;

    public PlaceOrderModel() {
    }

    public PlaceOrderModel(String itemName, int quantiity) {
        this.itemName = itemName;
        this.quantiity = quantiity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantiity() {
        return quantiity;
    }

    public void setQuantiity(int quantiity) {
        this.quantiity = quantiity;
    }
}
