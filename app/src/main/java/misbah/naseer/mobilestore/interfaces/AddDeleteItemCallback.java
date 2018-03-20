package misbah.naseer.mobilestore.interfaces;

/**
 * Created by Awais Majeed on 18-Mar-18.
 */

public interface AddDeleteItemCallback {
    void onItemDeleteClicked(String itemId);
    void onItemAdded(String itemName);
}
