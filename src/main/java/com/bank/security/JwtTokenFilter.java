package com.bank.security;

import io.jsonwebtoken.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class JwtTokenFilter  extends GenericFilterBean
{
    final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);
    final String TAG= "JwtTokenFilter --> ";

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider)
    {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException, java.io.IOException
    {
        final String MTAG= TAG + " doFilter ";
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) req);
        logger.info(MTAG + " --> token --> " + token);

        if (token != null && jwtTokenProvider.validateToken(token) )
        {
            Authentication auth = token != null ? jwtTokenProvider.getAuthentication(token) : null;
            logger.info(MTAG + " --> AUTH --> " + auth.toString());

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(req, res);
    }

}
