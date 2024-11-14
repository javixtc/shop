package com.javixtc.notification_service.events;

import com.javixtc.notification_service.model.enums.OrderStatus;

public record OrderEvent(String orderNumber, int itemsCount, OrderStatus orderStatus) {
}


