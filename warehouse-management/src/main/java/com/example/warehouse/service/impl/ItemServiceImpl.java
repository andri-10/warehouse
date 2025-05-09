package com.example.warehouse.service.impl;

import com.example.warehouse.entity.Item;
import com.example.warehouse.repository.ItemRepository;
import com.example.warehouse.service.ItemService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public Item createItem(Item item) {
        if(itemRepository.existsByName(item.getName())) {
            throw new IllegalArgumentException("Item name already exists");
        }
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item updateItem(Item item) {

        Item existingItem = itemRepository.findById(item.getId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        if(!existingItem.getName().equals(item.getName()) &&
           itemRepository.existsByName(item.getName())){
            throw new IllegalArgumentException("Item with this name already exists");
        }
        return itemRepository.save(item);
    }

    @Override
    public void deleteItem(Long itemId) {
        if(!itemRepository.existsById(itemId)) {
            throw new IllegalArgumentException("Item not found");
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public Item getItemByName(String name) {
        return itemRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
    }

    @Override
    public boolean existsByName(String name) {
        return itemRepository.existsByName(name);
    }

    @Override
    public void updateQuantity(Long itemId, Integer quantityChange) {
        Item item = getItemById(itemId);
        int newQuantity = item.getQuantity() + quantityChange;

        if (newQuantity < 0) {
            throw new IllegalArgumentException("Insufficient inventory for item: " + item.getName());
        }
        item.setQuantity(newQuantity);
        itemRepository.save(item);
    }
}
