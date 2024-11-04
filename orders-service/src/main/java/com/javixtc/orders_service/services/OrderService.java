package com.javixtc.orders_service.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.javixtc.orders_service.model.dtos.OrderItemRequest;
import com.javixtc.orders_service.model.dtos.OrderRequest;
import com.javixtc.orders_service.model.entities.Order;
import com.javixtc.orders_service.model.entities.OrderItems;
import com.javixtc.orders_service.repositories.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest) {

        // Check for inventory
        

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderItems(orderRequest.getOrderItems().stream().map(orderRequestItem -> mapOrderItemToOrderItem(orderRequestItem, order))
                .toList());
        this.orderRepository.save(order);
    }

    private OrderItems mapOrderItemToOrderItem(OrderItemRequest orderItemRequest, Order order) {
        return OrderItems.builder()
            .id(orderItemRequest.getId())
            .sku(orderItemRequest.getSku())
            .price(orderItemRequest.getPrice())
            .quantity(orderItemRequest.getQuantity())
            .order(order)
            .build();
    }
}
