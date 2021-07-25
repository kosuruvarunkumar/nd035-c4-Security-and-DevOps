package com.example.demo.controllersTest;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    private User createUser(Long id, String userName, String password) {
        User user = new User();
        user.setId(id);
        user.setUsername(userName);
        user.setPassword(password);
        user.setCart(new Cart());

        return user;
    }

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
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);

        User user1 = createUser(1L, "user1", "Password@123");
        when(userRepository.findByUsername("user1")).thenReturn(user1);

        Item item1 = createItem(1L, "Laptop", BigDecimal.valueOf(50000), "Dell laptop with i5 8 " +
                "gen intel processor");
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
    }

    @Test
    public void addToCart() {
        ModifyCartRequest modifyCartRequest = getModifyCartRequest("user1", 1L, 4);
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(BigDecimal.valueOf(200000), response.getBody().getTotal());
    }

    @Test
    public void addToCartWhenUserIsNotPresent() {
        ModifyCartRequest modifyCartRequest = getModifyCartRequest("user2", 1L, 5);
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void addToCartWhenItemIsNotPresent() {
        ModifyCartRequest modifyCartRequest = getModifyCartRequest("user1", 2L, 5);
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void removeFromCart() {
        ModifyCartRequest modifyCartRequest = getModifyCartRequest("user1", 1L, 4);
        cartController.addTocart(modifyCartRequest);
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(BigDecimal.valueOf(0), response.getBody().getTotal());
    }

    @Test
    public void removeFromCartWhenUserIsNotPresent() {
        ModifyCartRequest modifyCartRequest = getModifyCartRequest("user2", 1L, 5);
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void removeFromCartWhenItemIsNotPresent() {
        ModifyCartRequest modifyCartRequest = getModifyCartRequest("user1", 2L, 5);
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertEquals(404, response.getStatusCodeValue());
    }

    private ModifyCartRequest getModifyCartRequest(String user, Long itemId, Integer itemQuantity) {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(user);
        modifyCartRequest.setItemId(itemId);
        modifyCartRequest.setQuantity(itemQuantity);
        return modifyCartRequest;
    }
}
