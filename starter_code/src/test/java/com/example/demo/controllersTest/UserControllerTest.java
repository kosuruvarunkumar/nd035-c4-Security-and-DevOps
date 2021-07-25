package com.example.demo.controllersTest;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);

        User user = new User();
        Cart cart = new Cart();
        user.setPassword("Password@123");
        user.setUsername("user1");
        user.setCart(cart);
        user.setId(1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("user1")).thenReturn(user);

    }

    private static CreateUserRequest createUserRequest(String userName, String password,
                                                String confirmPassword) {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(userName);
        createUserRequest.setPassword(password);
        createUserRequest.setConfirmPassword(confirmPassword);

        return createUserRequest;
    }

    @Test
    public void createUser() {
        String password = "password@123";
        when(bCryptPasswordEncoder.encode(password)).thenReturn("Hashed@123");
        CreateUserRequest createUserRequest = createUserRequest("testUser",password, password);
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getId());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("testUser", response.getBody().getUsername());
        assertEquals("Hashed@123", response.getBody().getPassword());
    }

    @Test
    public void createUserWithPasswordLengthLessThan7() {
        String password = "pass";
        when(bCryptPasswordEncoder.encode(password)).thenReturn("Hashed@123");
        CreateUserRequest createUserRequest = createUserRequest("testUser",password, password);

        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void createUserWithPasswordNotEqualsConfirmPassword() {
        String password = "password@123";
        when(bCryptPasswordEncoder.encode(password)).thenReturn("Hashed@123");
        CreateUserRequest createUserRequest = createUserRequest("testUser",password, "password");

        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void getUserByID() {
        ResponseEntity<User> response = userController.findById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("user1", response.getBody().getUsername());
    }

    @Test
    public void getUserByUserName() {
        ResponseEntity<User> response = userController.findByUserName("user1");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("user1", response.getBody().getUsername());
    }


}
