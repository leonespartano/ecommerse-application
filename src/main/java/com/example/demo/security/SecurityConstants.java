package com.example.demo.security;

import com.example.demo.utils.PemUtils;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;


public class SecurityConstants {


    public static  RSAPrivateKey RSA_PRIVATE = null;
    public static  RSAPublicKey RSA_PUBLIC = null;

    static {
        try {
            RSA_PRIVATE = (RSAPrivateKey) PemUtils.readPrivateKeyFromFile("src/main/resources/rsa-private.pem", "RSA");
            RSA_PUBLIC =  (RSAPublicKey) PemUtils.readPublicKeyFromFile("src/main/resources/rsa-public.pem", "RSA");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGNUP_URL = "/api/user/create";




}
