package com.example.warehouse.service;

import com.example.warehouse.entity.Item;
import java.util.List;

public interface ItemService {
    Item createItem(Item item);
    Item updateItem(Item item);
    void deleteItem(Long itemId);
    Item getItemById(Long itemId);
    List<Item> getAllItems();
    Item getItemByName(String name);
    boolean existsByName(String name);
    void updateQuantity(Long itemId, Integer quantityChange);
}