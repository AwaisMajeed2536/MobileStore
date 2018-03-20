package misbah.naseer.mobilestore.model;

import java.io.Serializable;

/**
 * Created by Awais Majeed on 18-Mar-18.
 */

public class AvailableItemsModel implements Serializable{

    private String itemName;
    private String itemId;

    public AvailableItemsModel() {
    }

    public AvailableItemsModel(String itemName, String itemId) {
        this.itemName = itemName;
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
