package com.bank.security;

import com.bank.model.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.*;

@Component
public class JwtTokenProvider
{
    final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    final String TAG= "JwtTokenProvider --> ";

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length}")
    private long validityInMilliseconds; // 2'

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostConstruct
    protected void init()
    {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String username, Set<Role> set)
    {
        final String MTAG= TAG + " createToken --> ";

        Claims claims= null;
        try {
            claims = Jwts.claims().setSubject(username);
            claims.put("roles", set);

            logger.info(MTAG + "Token Claims  --> " + claims.toString());
        }
        catch(Exception e)
        {
            logger.info(MTAG + "ERROR --> Token Claims  --> " + e.getMessage());
        }

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        JwtBuilder builder = Jwts.builder();
        logger.info( MTAG + " JwtBuilder creado !");

        Key key= null;
        try
        {
            key = Keys.hmacShaKeyFor(secretKey.getBytes());
            logger.info(MTAG + "Creada KEY   --> " + key.toString());
        }
        catch(Exception e)
        {
            logger.info(MTAG + "ERROR --> Creando KEY   --> " + e.getMessage());
        }


        String token= null;
        try {
            token = builder
                    .addClaims(claims)
                    .setExpiration(validity)
                    .setIssuedAt(now)
                    .setSubject(username)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            logger.info(MTAG + "Creado Token   --> " + token);
        }
        catch(Exception e)
        {
            logger.info(MTAG + "ERROR --> Creando Token   --> " + e.getMessage());
        }

        return  token;

    }


    public Authentication getAuthentication(String token)
    {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token)
    {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String resolveToken(HttpServletRequest req)
    {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }


    public boolean validateToken(String token)
    {
        final String MTAG= TAG + "validateToken ";

        logger.info(MTAG + " token --> " + token);

        Jws<Claims> claims= null;

        try
        {
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
            claims = Jwts.parserBuilder().setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            logger.info(MTAG + " We Have Claims --> " + claims.toString());
        }
        catch (JwtException e)
        {
            logger.info(MTAG + " ERROR JwtException 1 -->  " + e.getMessage());
            throw new JwtException(e.getMessage());//"Expired or invalid JWT token");
        }
        catch (IllegalArgumentException e) {
            logger.info(MTAG + " ERROR IllegalArgumentException 1 --> " + e.getMessage());
            throw new JwtException(e.getMessage());//"Expired or invalid JWT token");
        }


        try
        {
            if (claims.getBody().getExpiration().before(new Date()))
            {
                logger.info(MTAG + " PROBLEM HERE --> Claims Have expired --> " );
//                throw new JwtException("Token validity has Expired");
                return false;
                //return false;
            }
            return true;
        }
        catch (JwtException e)
        {
            logger.info(MTAG + " ERROR JwtException 2 -->  " + e.getMessage());
            throw new JwtException(e.getMessage());//"Expired or invalid JWT token");
        }
        catch (IllegalArgumentException e) {
            logger.info(MTAG + " ERROR IllegalArgumentException 2 --> " + e.getMessage());
            throw new JwtException(e.getMessage());//"Expired or invalid JWT token");
        }
    }

}
