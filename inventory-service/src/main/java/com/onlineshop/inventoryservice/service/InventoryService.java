package com.onlineshop.inventoryservice.service;

import com.onlineshop.inventoryservice.dto.InventoryResponse;
import com.onlineshop.inventoryservice.model.Inventory;
import com.onlineshop.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final String UNABLE_TO_PROCESS_ORDER = "Unable to process order, at least one item not in stock";
    private final String ORDER_SUCCESSFUL = "Order placed successfully";

    private final InventoryRepository inventoryRepository;
    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventoryIn(List<String> skuCodes) {
        return inventoryRepository.findBySkuCodeIn(skuCodes).stream()
                .map(inventory ->
                        InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .quantity(inventory.getQuantity())
                                .build()
                ).toList();
    }

    @Transactional
    public String retrieveInventory(Map<String, Integer> itemsToRetrieve){
        List<Inventory> requestedInventoryList = inventoryRepository.findBySkuCodeIn(itemsToRetrieve.keySet().stream().toList());

        boolean allProductsInStock = requestedInventoryList.stream()
                .allMatch(item -> item.getQuantity() > 0 && item.getQuantity() > itemsToRetrieve.get(item.getSkuCode()));

        if(!allProductsInStock) {
            return UNABLE_TO_PROCESS_ORDER;
        }

        List<Inventory> remainingInventory = requestedInventoryList.stream()
                .map(item -> decreaseInventoryItemQuantity(item,itemsToRetrieve.get(item.getSkuCode())))
                .toList();

        inventoryRepository.saveAll(remainingInventory);

        return ORDER_SUCCESSFUL;

    }


    private Inventory decreaseInventoryItemQuantity(Inventory item, int quantityToDecrease) {
        item.setQuantity(item.getQuantity()-quantityToDecrease);
        return item;
    }
}
