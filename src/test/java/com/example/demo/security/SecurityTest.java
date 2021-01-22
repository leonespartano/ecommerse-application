package com.example.demo.security;


import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.example.demo.model.persistence.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import net.bytebuddy.utility.RandomString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.json.JSONObject;



import com.example.demo.model.requests.CreateUserRequest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class SecurityTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<CreateUserRequest> json;

    /*
        Test that verifies the correct operation of the end point "/create"
     */
    @Test
    public void createUsers() throws Exception {
        CreateUserRequest newUser = newUser();

        mvc.perform(post(SecurityConstants.SIGNUP_URL)
                .content(json.write(newUser).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(newUser.getUsername())
        );
    }
    /*
        Test that verifies the correct login in of a user
     */

    @Test
    public void loginUser() throws Exception {

        CreateUserRequest newUser = newUser();

        mvc.perform(post(SecurityConstants.SIGNUP_URL)
                .content(json.write(newUser).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        JSONObject obj = new JSONObject();
        obj.put("username", newUser.getUsername());
        obj.put("password", newUser.getPassword());
        MvcResult mvcResult;

            mvcResult = mvc.perform(post("/login")
                    .content(String.valueOf(obj))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andReturn();
            assertNotNull(mvcResult.getResponse().getHeaderValue("Authorization"));
    }

    /*
        Test that verifies that resources cannot be accessed, without prior authentication
     */
    @Test
    public void userProfileUnauthenticated() throws Exception {
        mvc.perform(get("api/user/test"))
                .andExpect(status().isUnauthorized());
    }

    /*
        Test that verifies the correct access of resources through Jason Web Token
     */
    @Test
    public void userProfileAuthenticated() throws Exception{
        CreateUserRequest newUser = newUser();

        mvc.perform(post(SecurityConstants.SIGNUP_URL)
                .content(json.write(newUser).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        JSONObject obj = new JSONObject();
        obj.put("username", newUser.getUsername());
        obj.put("password", newUser.getPassword());

        MvcResult mvcResult;
        mvcResult = mvc.perform(post("/login")
                .content(String.valueOf(obj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
        String jwtToken = (String) mvcResult.getResponse().getHeaderValue("Authorization");
        User user = new User();
        user.setUsername(newUser.getUsername());
        user.setPassword(newUser.getPassword());
        mvc.perform(get("/api/user/" + user.getUsername())
                .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.getUsername()));

    }



    private CreateUserRequest newUser(){
        CreateUserRequest newUser = new CreateUserRequest();
        newUser.setUsername(RandomString.make(5));
        newUser.setPassword("testpassword" + newUser.getUsername());
        newUser.setConfirmPassword("testpassword" + newUser.getUsername());
        return newUser;
    }
}
