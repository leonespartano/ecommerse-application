package com.example.demo.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;


@Component
public class AuthenticationVerificationFilter extends BasicAuthenticationFilter {
    public static final Logger log = LoggerFactory.getLogger(AuthenticationVerificationFilter.class);


    public AuthenticationVerificationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String header = req.getHeader(SecurityConstants.HEADER_STRING);

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {

        String token = req.getHeader(SecurityConstants.HEADER_STRING);
        if (token != null) {
            try {


                Algorithm algorithm = Algorithm.RSA256(SecurityConstants.RSA_PUBLIC, SecurityConstants.RSA_PRIVATE);
                JWTVerifier verifier = JWT.require(algorithm).build();

                DecodedJWT jwt = verifier.verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""));
                String user = jwt.getSubject();

                if (user != null) {
                    return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                }
            }catch (SignatureVerificationException ex){
                log.error("The token was invalid");
                throw new RuntimeException(ex);
            }
            return null;

        }


        return null;
    }

}
