package com.onlineshop.orderservice.service;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.onlineshop.orderservice.dto.InventoryResponse;
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

        order.setOrderLineItemsList(orderLineItemsMap.values().stream().toList());

        //Call inventory service and place order if product is in stock
        InventoryResponse[] inventoryResponseArray = webClient.get()
                .uri("http://localhost:8083/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", orderLineItemsMap.keySet().stream().toList()).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        if(inventoryResponseArray == null || inventoryResponseArray.length == 0) {
            throw new IllegalArgumentException("No Products found in Inventory!");
        }

        boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                .allMatch(inventoryResponse -> inventoryResponse.getQuantity() > 0 &&
                    inventoryResponse.getQuantity() > orderLineItemsMap.get(inventoryResponse.getSkuCode()).getQuantity());

        if(allProductsInStock) {
            //when placing an order, the inventory must be removed from database

            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
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
