package com.bank.api;

import com.bank.model.ApiUser;
import com.bank.model.Role;
import com.bank.repository.UserRepository;
import com.bank.security.AuthBody;
import com.bank.security.CustomUserDetailsService;
import com.bank.security.JwtTokenProvider;
import com.bank.util.Constants;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.KeyPair;
import java.util.*;

import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin(maxAge= Constants.CORS_AGE)
@RestController
@RequestMapping("/api/auth")
public class AuthController
{
    final Logger logger = LoggerFactory.getLogger(AuthController.class);
    final String TAG= "AuthController --> ";

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserRepository users;

    @Autowired
    private CustomUserDetailsService userService;


    /**
     * curl --insecure --cert-type P12 --cert uoc_ident.p12:123456 -X POST -H "Content-Type: application/json" -d '{"username":"pepe","password":".."}' https://localhost:8443/api/auth/login
     * curl -X POST -H "Content-Type: application/json" -d '{"username":"pepe","password":".."}' https://localhost:8443/api/auth/login
     * curl http://localhost:8080/api/mov/list -H "Accept: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwZXBlIiwicm9sZXMiOlt7ImlkIjoxODAsImF1dGhvcml0eSI6IlJPTEVfQURNSU4ifV0sImV4cCI6MTYyOTQ0NDc5NiwiaWF0IjoxNjI5NDQ0Njc2fQ.UemJKoxl3av3w-3V4ZvR0YCdKXIa7c4cQ7A7eJPLh0A"
     * @param data
     * @param session
     * @return
     */
    @SuppressWarnings("rawtypes")
    @CrossOrigin(origins= Constants.CORS_ORIGIN)
    @PostMapping(value="/login",consumes = MediaType.APPLICATION_JSON_VALUE,produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity login(@RequestBody AuthBody data, HttpSession session)
    {
        final String MTAG= TAG + " -> /login";
        logger.info( MTAG + "  --> data -> " + data.toString() );

        try {
            String username = data.getUsername();

            UsernamePasswordAuthenticationToken tkn= new UsernamePasswordAuthenticationToken(username, data.getPassword());
            logger.info( MTAG + "  --> UsernamePasswordAuthenticationToken -> " + tkn.toString() );

            authenticationManager.authenticate( tkn );

            logger.info( MTAG + "  --> Usuario Autenticado" );

            ApiUser usr= (ApiUser) this.users.findByUsername(username);
            logger.info( MTAG + "  --> Usuario Autenticado --> " + usr.toString() );

            Role role= (Role) usr.getRole();
            logger.info( MTAG + "  --> Usuario Autenticado Role --> " + role.toString() );

            Set<Role> roles= new HashSet<>();
            roles.add( role );

            String token= null;
            try
            {
                token= jwtTokenProvider.createToken(username,roles);
                logger.info( MTAG + "  --> Token -->  " + token );

            }
            catch (Exception e)
            {
                logger.info( MTAG + "  --> ERROR: Generando el token " + e.getMessage() );
            }

            /** lets test the token validity from the begining
             *
             */
            boolean isAValidTkn= false;
//            try
//            {
//                isAValidTkn= jwtTokenProvider.validateToken(token);
//            }
//            catch (Exception e)
//            {
//                logger.info( MTAG + "  --> ERROR: Validando el token " + e.getMessage() );
//            }

            Map<Object, Object> model = new HashMap<>();
            model.put("username", username);
            model.put("token", token);
            //model.put("isValid", isAValidTkn);

            return ok(model);
        }
        //catch ( AuthenticationException e) {
        catch ( Exception e) {
            throw new BadCredentialsException("Invalid email/password supplied");
        }
    }

    @SuppressWarnings("rawtypes")
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody ApiUser user) {
        ApiUser userExists = userService.find(user.getUsername());
        if (userExists != null) {
            throw new BadCredentialsException("User with username: " + user.getUsername()+ " already exists");
        }
        userService.saveUser(user);
        Map<Object, Object> model = new HashMap<>();
        model.put("message", "User registered successfully");
        return ok(model);
    }

    /**
     * Se genera keyPair para almacenarlos en la Session, para usar en las validaciones
     * @return
     */
    private KeyPair getKeyPair()
    {
        final String MTAG= TAG + "getKeyPair() ";
        KeyPair keyPair = null;

        try {
            keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
        } catch (Exception e) {
            logger.info(MTAG + "ERROR --> Creando keyPair  --> " + e.getMessage());
            return null;
        }

        logger.info(MTAG + " KeyPair public  --> " + keyPair.getPublic().toString());
        return keyPair;
    }
}