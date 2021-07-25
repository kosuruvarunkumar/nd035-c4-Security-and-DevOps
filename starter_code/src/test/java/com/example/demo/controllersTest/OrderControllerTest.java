package com.example.demo.controllersTest;

import com.example.demo.TestUtils;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

public class OrderControllerTest {
    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    private List<Item> getItems() {
        List<Item> allItems= new ArrayList<>();
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("1+ Nord");
        item1.setPrice(BigDecimal.valueOf(24999));
        item1.setDescription("OnePlus Nord CE 5G (Charcoal Ink, 8GB RAM, 128GB Storage)");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("1+ Nord");
        item2.setPrice(BigDecimal.valueOf(27999));
        item2.setDescription("OnePlus Nord CE 5G (Silver Ray, 12GB RAM, 256GB Storage)");

        allItems.add(item1);
        allItems.add(item2);

        return allItems;
    }

    private User createUser() {
        User user = new User();
        Cart cart = new Cart();
        user.setId(1L);
        user.setUsername("user1");
        user.setPassword("password@123");
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(getItems());
        cart.setTotal(BigDecimal.valueOf(24999 + 27999));
        user.setCart(cart);

        return user;
    }

    private UserOrder createOrder() {
        UserOrder userOrder = new UserOrder();
        userOrder.setId(1L);
        userOrder.setUser(createUser());
        userOrder.setItems(getItems());
        userOrder.setTotal(BigDecimal.valueOf(24999+27999));

        return userOrder;
    }

    @Before
    public void setup() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        User user = createUser();
        UserOrder userOrder = createOrder();
        List<UserOrder> orders = new ArrayList<>();
        orders.add(userOrder);
        when(userRepository.findByUsername("user1")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);
    }

    @Test
    public void submitOrderWithValidUser() {
        ResponseEntity<UserOrder> response = orderController.submit("user1");
        UserOrder userOrder = response.getBody();
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(userOrder);
        assertEquals(getItems(), userOrder.getItems());
        assertEquals("user1", userOrder.getUser().getUsername());
        assertEquals(BigDecimal.valueOf(24999 + 27999), userOrder.getTotal());
    }

    @Test
    public void submitOrderWithInvalidUser() {
        ResponseEntity<UserOrder> response = orderController.submit("user2");
        assertEquals(404, response.getStatusCodeValue());

    }

    @Test
    public void getOrderHistoryForAnExistingUser() {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("user1");
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("user1", response.getBody().get(0).getUser().getUsername());
        assertEquals(BigDecimal.valueOf(24999+27999),response.getBody().get(0).getTotal());
        assertEquals(getItems(), response.getBody().get(0).getItems());
    }
}
