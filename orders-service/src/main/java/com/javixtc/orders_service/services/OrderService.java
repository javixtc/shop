package com.javixtc.orders_service.services;

import java.util.List;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.javixtc.orders_service.events.OrderEvent;
import com.javixtc.orders_service.model.dtos.BaseResponse;
import com.javixtc.orders_service.model.dtos.OrderItemRequest;
import com.javixtc.orders_service.model.dtos.OrderItemsResponse;
import com.javixtc.orders_service.model.dtos.OrderRequest;
import com.javixtc.orders_service.model.dtos.OrderResponse;
import com.javixtc.orders_service.model.entities.Order;
import com.javixtc.orders_service.model.entities.OrderItems;
import com.javixtc.orders_service.model.enums.OrderStatus;
import com.javixtc.orders_service.repositories.OrderRepository;
import com.javixtc.orders_service.utils.JsonUtils;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObservationRegistry observationRegistry;

    public OrderResponse placeOrder(OrderRequest orderRequest) {

        /**
         * Starts an observation for the "inventory-service" operation, using the provided ObservationRegistry.
         * This observation can be used to track and monitor the performance and behavior of the inventory service
         * interaction within the `placeOrder` method.
         */
        Observation inventoryObservation = Observation.createNotStarted("inventory-service", observationRegistry);
        //Check for inventory
        return inventoryObservation.observe(() -> {
            BaseResponse result = this.webClientBuilder.build()
                .post()
                .uri("lb://inventory-service/api/inventory/in-stock")
                .bodyValue(orderRequest.getOrderItems())
                .retrieve()
                .bodyToMono(BaseResponse.class)
                .block();

            if (result != null && !result.hasErrors()){
                Order order = new Order();
                order.setOrderNumber(UUID.randomUUID().toString());
                order.setOrderItems(orderRequest.getOrderItems().stream().map(orderRequestItem -> mapOrderItemToOrderItem(orderRequestItem, order))
                        .toList());
                var saveOrder = this.orderRepository.save(order);
                
                // Send order event to notification service
                this.kafkaTemplate.send("orders-topic", JsonUtils.toJson(
                    new OrderEvent(saveOrder.getOrderNumber(), saveOrder.getOrderItems().size(), OrderStatus.PLACED)));

                return mapToOrderResponse(saveOrder);
            } else {
                throw new IllegalArgumentException("Product is not in stock, please try again later");
            }
        });
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

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = this.orderRepository.findAll();

        return orders.stream().map(this::mapToOrderResponse).toList();

    }

    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(order.getId(), order.getOrderNumber()
                , order.getOrderItems().stream().map(this::mapToOrderItemRequest).toList());
    }

    private OrderItemsResponse mapToOrderItemRequest(OrderItems orderItems) {
        return new OrderItemsResponse(orderItems.getId(), orderItems.getSku(), orderItems.getPrice(), orderItems.getQuantity());
    }

}
