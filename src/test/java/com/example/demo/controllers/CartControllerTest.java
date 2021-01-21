package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import net.bytebuddy.utility.RandomString;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import static org.junit.Assert.*;

import java.math.BigDecimal;

import java.util.Objects;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;
    private ModifyCartRequest request;
    private User user;
    private Item item1;

    private final UserRepository userRepo = mock(UserRepository.class);
    private final CartRepository cartRepo = mock(CartRepository.class);
    private final ItemRepository itemRepo = mock(ItemRepository.class);

    @Test
    public void failureAddToCart(){
        when(userRepo.findByUsername(request.getUsername())).thenReturn(user);
        when(itemRepo.findById(2L)).thenReturn(Optional.of(item1));
        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    public void successAddToCart(){
        when(userRepo.findByUsername(request.getUsername())).thenReturn(user);
        when(itemRepo.findById(request.getItemId())).thenReturn(Optional.of(item1));
        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user.getCart(), response.getBody());
    }

    @Test
    public void successRemoveFromCart(){
        when(userRepo.findByUsername(request.getUsername())).thenReturn(user);
        when(itemRepo.findById(request.getItemId())).thenReturn(Optional.of(item1));
        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(Objects.requireNonNull(response.getBody()).getItems().isEmpty());
    }


    @Before
    public void setUp(){
        request = new ModifyCartRequest();
        request.setUsername("John");
        request.setItemId(1L);
        request.setQuantity(1);

        item1 = new Item();
        user = new User();
        item1.setId(1L);
        item1.setName("pencil");
        item1.setPrice(BigDecimal.valueOf(2));
        Cart cart = new Cart();
        cart.setId(1L);
        cart.addItem(item1);
        cart.setTotal(BigDecimal.valueOf(2));
        user = new User();
        user.setUsername("John");
        user.setPassword(RandomString.make(6));
        user.setId(1L);
        user.setCart(cart);
        cart.setUser(user);

        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);
    }

}
