package misbah.naseer.mobilestore.model;

/**
 * Created by Devprovider on 31/07/2017.
 */

public class OrderModel {
    private String order;
    private String status;

    public OrderModel() {
    }

    public OrderModel(String order, String status) {
        this.order = order;
        this.status = status;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
