package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import net.bytebuddy.utility.RandomString;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class UserControllerTest {
    private UserController userController;
    private User user;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);


    @Before
    public void setUp(){
        userController = new UserController();
        user = new User();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);


    }

    @Test
    public void successFindById() throws Exception{
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        ResponseEntity<User> response = userController.findById(user.getId());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user.getPassword(), Objects.requireNonNull(response.getBody()).getPassword());
    }

    //
    @Test
    public void failFindById() throws  Exception{

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        ResponseEntity<User> response = userController.findById(1L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void successFindUserById() throws Exception{
        String userName = RandomString.make(5);
        user.setUsername(userName);
        when(userRepo.findByUsername(userName)).thenReturn(user);
        ResponseEntity<User> response = userController.findByUserName(userName);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userName, Objects.requireNonNull(response.getBody()).getUsername());
    }

    @Test
    public void create_user_happy_path() throws Exception{
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(RandomString.make(5));
        request.setPassword(request.getUsername() + request.getUsername());
        request.setConfirmPassword(request.getUsername() + request.getUsername());

        when(encoder.encode(request.getPassword())).thenReturn("thisIsHashed");

        ResponseEntity<User> response = userController.createUser(request);
        user = response.getBody();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals(request.getUsername(), user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());

    }

}
