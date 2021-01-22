package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import net.bytebuddy.utility.RandomString;
import org.junit.Before;
import org.junit.Test;

import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/*
    Tests that verify the different methods of the OrderController class
*/
public class OrderControllerTest {


    private OrderController orderController;
    private User user1;
    private List<UserOrder> list;
    UserOrder userOrder;


    private final UserRepository userRepo = mock(UserRepository.class);
    private final OrderRepository orderRepo = mock(OrderRepository.class);

    @Test
    public void failureInSubmit(){
        when(userRepo.findByUsername("John")).thenReturn(user1);
        ResponseEntity<UserOrder> response = orderController.submit("Peter");

        //the user wasn't found. Error 404
        assertEquals(404, response.getStatusCodeValue());
        assertNotNull(response);
    }

    @Test
    public void failureInGetOrdersForUser(){
        String userName = "John";
        when(userRepo.findByUsername("Raul")).thenReturn(user1);
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(userName);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void successInSubmit(){
        String userName = "John";
        when(userRepo.findByUsername(userName)).thenReturn(user1);
        ResponseEntity<UserOrder> response = orderController.submit(userName);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user1, Objects.requireNonNull(response.getBody()).getUser());
    }

    @Test
    public void successInGetOrdersForUser(){
        String userName = "John";
        when(userRepo.findByUsername(userName)).thenReturn(user1);
        when(orderRepo.findByUser(user1)).thenReturn(list);
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(userName);
        assertNotNull(response);
        assertFalse(Objects.requireNonNull(response.getBody()).isEmpty());
        assertTrue(response.getBody().size()>0);
    }


    @Before
    public void setUp(){
        list = new ArrayList<>();
        Item item1 = new Item();
        user1 = new User();
        item1.setId(1L);
        item1.setName("pencil");
        item1.setPrice(BigDecimal.valueOf(2));
        Cart cart = new Cart();
        cart.setId(1L);
        cart.addItem(item1);
        cart.setTotal(BigDecimal.valueOf(2));
        user1 = new User();
        user1.setUsername("John");
        user1.setPassword(RandomString.make(6));
        user1.setId(1L);
        user1.setCart(cart);
        cart.setUser(user1);

        userOrder = UserOrder.createFromCart(cart);
        list.add(userOrder);

        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
    }

}
