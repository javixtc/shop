package com.javixtc.inventory_service.services;

import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.javixtc.inventory_service.model.dtos.BaseResponse;
import com.javixtc.inventory_service.model.dtos.OrderItemRequest;
import com.javixtc.inventory_service.model.entities.Inventory;
import com.javixtc.inventory_service.repositories.InventoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public boolean isInStock(String sku) {
        var inventory = inventoryRepository.findBySku(sku);

        return inventory.filter(value -> value.getQuantity() > 0).isPresent();
    }

    public BaseResponse areInStock(List<OrderItemRequest> orderItems) {
        var errorList = new ArrayList<String>();
        
        List<String> skus = orderItems.stream().map(OrderItemRequest::getSku).toList();

        List<Inventory> inventoryList = inventoryRepository.findBySkuIn(skus);

        orderItems.forEach(orderItem -> {
            var inventory = inventoryList.stream()
                .filter(value -> value.getSku().equals(orderItem.getSku()))
                .findFirst();               
            if (inventory.isEmpty()) {
                errorList.add("Product with sku " + orderItem.getSku() + " does not exists");
            } else if (inventory.get().getQuantity() < orderItem.getQuantity()) {
                errorList.add("Product with sku " + orderItem.getSku() + " has inssuficient quantity");
            }
        });

        return !errorList.isEmpty() ? new BaseResponse(errorList.toArray(new String[0])) : new BaseResponse(null);

    }

}
