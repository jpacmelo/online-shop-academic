package com.onlineshop.inventoryservice.controller;

import com.onlineshop.inventoryservice.dto.InventoryResponse;
import com.onlineshop.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // http://localhost:8082/api/inventory?skuCode=iphone_13&skuCode=iphone_13_red
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> getListedInventory(@RequestParam List<String> skuCodes){
        return inventoryService.getInventoryIn(skuCodes);
    }

}
