package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import static org.junit.Assert.*;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private List<Item> items;
    private Item item1;
    private Item item2;
    private final ItemRepository itemRepo = mock(ItemRepository.class);

    @Test
    public void failGetItemsById(){
        when(itemRepo.findById(item1.getId())).thenReturn(Optional.of(item1));
        ResponseEntity<Item> responseEntity = itemController.getItemById(3L);
        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void failGetItemByName(){
        when(itemRepo.findByName(item2.getName())).thenReturn(items);
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("BlackBoard");
        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void successGetItemById(){
        when(itemRepo.findById(item1.getId())).thenReturn(Optional.of(item1));
        ResponseEntity<Item> responseEntity = itemController.getItemById(1L);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(item1, responseEntity.getBody());
    }

    @Test
    public void successGetItemByName(){
        when(itemRepo.findByName(item2.getName())).thenReturn(items);
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("eraser");
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(items.size(), Objects.requireNonNull(responseEntity.getBody()).size());
        assertEquals(item2, responseEntity.getBody().get(1));
    }

    @Test
    public void successGetItems(){
        when(itemRepo.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> responseEntity = itemController.getItems();
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(items.size(), Objects.requireNonNull(responseEntity.getBody()).size());
        assertEquals(item1, responseEntity.getBody().get(0));
    }


    @Before
    public void setUp(){
        items = new ArrayList<>();
        item1 = new Item();
        item1.setId(1L);
        item1.setName("pencil");
        item1.setPrice(BigDecimal.valueOf(2));
        items.add(item1);
        item2 = new Item();
        item2.setId(2L);
        item2.setName("eraser");
        item2.setPrice(BigDecimal.valueOf(1));
        items.add(item2);

        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);
    }
}
