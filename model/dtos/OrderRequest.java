package com.javixtc.products_service.model.dtos;

import java.util.List;

public class OrderRequest {
    private String customerName;
    private String customerEmail;
    private List<OrderItem> items;

    // Default constructor
    public OrderRequest() {}

    // Parameterized constructor
    public OrderRequest(String customerName, String customerEmail, List<OrderItem> items) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.items = items;
    }

    // Getters and Setters
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}

class OrderItem {
    private String productId;
    private int quantity;

    // Default constructor
    public OrderItem() {}

    // Parameterized constructor
    public OrderItem(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
