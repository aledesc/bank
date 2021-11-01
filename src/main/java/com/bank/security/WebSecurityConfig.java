package com.bank.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletResponse;


@Configuration
@ComponentScan(basePackages = "com.bank.identity")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);
    final String TAG= "WebSecurityConfig --> ";

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
//        final String MTAG= TAG + "configure(AuthenticationManagerBuilder auth)";

        UserDetailsService userDetailsService = getUserDetails();
//        logger.info( MTAG + " UserDetails -> " + userDetailsService.toString() );

        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()

                .authorizeRequests()

                .antMatchers("/**").permitAll()
                .antMatchers("/api/auth/login").permitAll()
                .antMatchers("/api/auth/register").permitAll()

//                .antMatchers("/account/create-account").hasAnyAuthority("ROLE_ADMIN")
//                .antMatchers("/account/deposit").hasAnyAuthority("ROLE_ADMIN")
//                .antMatchers("/account/withdraw").hasAnyAuthority("ROLE_ADMIN")
//                .antMatchers("/account/movs").hasAnyAuthority("ROLE_ADMIN")
                
                .antMatchers("/account/create-account").permitAll()
                .antMatchers("/account/deposit").permitAll()
                .antMatchers("/account/withdraw").permitAll()
                .antMatchers("/account/movs").permitAll()

                .anyRequest().authenticated();
    }


    @Bean
    public PasswordEncoder bCryptPasswordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception
    {
        return super.authenticationManagerBean();
    }

    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint()
    {
        return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized");
    }

    @Bean
    public UserDetailsService getUserDetails()
    {
        return new CustomUserDetailsService();
    }

}
