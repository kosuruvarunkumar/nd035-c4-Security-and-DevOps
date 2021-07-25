package com.example.demo.controllersTest;

import com.example.demo.TestUtils;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    private Item createItem(Long id, String itemName, BigDecimal itemPrice, String itemDescription) {
        Item item = new Item();
        item.setId(id);
        item.setName(itemName);
        item.setPrice(itemPrice);
        item.setDescription(itemDescription);

        return item;
    }

    @Before
    public void setup() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
        List<Item> allItems = new ArrayList<>();
        List<Item> nords = new ArrayList<>();
        Item item1 = createItem(1L, "1+ Nord", BigDecimal.valueOf(24999), "OnePlus Nord CE 5G " +
                "(Charcoal Ink, 8GB RAM, 128GB Storage)");
        Item item2 = createItem(2L, "Samsung Galaxy M42", BigDecimal.valueOf(23999), "Prism Dot " +
                "Black, 8GB RAM, 128GB Storage");
        Item item3 = createItem(3L, "1+ Nord", BigDecimal.valueOf(27999), "OnePlus Nord CE 5G " +
                "(Silver Ray, 12GB RAM, 256GB Storage)");
        allItems.add(item1);
        allItems.add(item2);
        allItems.add(item3);
        nords.add(item1);
        nords.add(item3);
        when(itemRepository.findAll()).thenReturn(allItems);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRepository.findByName("1+ Nord")).thenReturn(nords);
    }

    @Test
    public void getAllItems() {
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(3, response.getBody().size());
    }

    @Test
    public void getItemById() {
        ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("1+ Nord", response.getBody().getName());
    }

    @Test
    public void getItemWhenIdIsNotPresent() {
        ResponseEntity<Item> response = itemController.getItemById(4L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getItemByName() {
        ResponseEntity<List<Item>> response = itemController.getItemsByName("1+ Nord");
        long id1 = response.getBody().get(0).getId();
        String description1 = "OnePlus Nord CE 5G (Charcoal Ink, 8GB RAM, 128GB Storage)";
        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals(1, id1);
        assertEquals(description1, response.getBody().get(0).getDescription());
        assertEquals(BigDecimal.valueOf(24999), response.getBody().get(0).getPrice());
        assertEquals(BigDecimal.valueOf(27999), response.getBody().get(1).getPrice());
    }

    @Test
    public void getItemByInvalidName() {
        ResponseEntity<List<Item>> response = itemController.getItemsByName("invalid name");
        assertNull(response.getBody());
        assertEquals(404, response.getStatusCodeValue());
    }
}
