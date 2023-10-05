package com.onlineshop.orderservice.service;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.onlineshop.orderservice.dto.OrderLineItemsDto;
import com.onlineshop.orderservice.dto.OrderRequest;
import com.onlineshop.orderservice.model.Order;
import com.onlineshop.orderservice.model.OrderLineItems;
import com.onlineshop.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final String UNABLE_TO_PROCESS_ORDER = "Unable to process order, at least one item with insufficient stock";
    private final String ORDER_SUCCESSFUL = "Order placed successfully";
    private final OrderRepository orderRepository;
    private final WebClient webClient;

    public void placeOrder(OrderRequest orderRequest){
        if(orderRequest==null || orderRequest.getOrderLineItemsDtoList() == null || orderRequest.getOrderLineItemsDtoList().isEmpty()){
            throw new IllegalArgumentException("Can't place an order without a Product List!");
        }

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        //Make sure no duplicate entries for the same skuCode in the OrderListItemsList
        final Map<String, OrderLineItems> orderLineItemsMap = orderRequest.getOrderLineItemsDtoList().stream()
            .map(this::mapFromDto)
            .collect(Collectors.toMap(OrderLineItems::getSkuCode, Function.identity(),
                (orderLineItem1, orderLineItem2) -> {
                    orderLineItem1.setQuantity(orderLineItem1.getQuantity()+orderLineItem2.getQuantity());
                    return orderLineItem1;
                }));

        if(orderLineItemsMap.values().stream().anyMatch(e-> e.getQuantity() <= 0)){
            throw new IllegalArgumentException("Can not order an item with a negative or null quantity.");
        }

        order.setOrderLineItemsList(orderLineItemsMap.values().stream().toList());

        //Call inventory service and place order if product is in stock
        String orderStatus = webClient.put()
                .uri("http://localhost:8083/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("orderItem", orderLineItemsMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getQuantity()))).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if(UNABLE_TO_PROCESS_ORDER.equals(orderStatus)) {
            throw new IllegalArgumentException("Not enough inventory in stock.");
        }

        if(ORDER_SUCCESSFUL.equals(orderStatus)) {
            orderRepository.save(order);
        }
    }

    private OrderLineItems mapFromDto(final OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }

}
