package com.bank.security;

import com.bank.model.Role;
import com.bank.model.ApiUser;
import com.bank.repository.RoleRepository;
import com.bank.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CustomUserDetailsService implements UserDetailsService
{
    final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    final String TAG= "CustomUserDetailsService --> ";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    public ApiUser find(String username)
    {
        return userRepository.findByUsername(username);
    }

    public void saveUser(ApiUser user)
    {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setEnabled(true);

        Role userRole = roleRepository.findByAuthority("ROLE_ADMIN");
        if( userRole != null )
            user.setRole( userRole );

        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        final String MTAG= TAG + " loadUserByUsername --> ";

        ApiUser user= userRepository.findByUsername(username);
        logger.info( MTAG + " RECUPERADO User --> " + user.toString());


        if(user != null) {

            List<GrantedAuthority> authorities = getUserAuthority(user.getRole());
            return buildUserForAuthentication(user, authorities);

        } else {
            throw new UsernameNotFoundException("username not found");
        }
    }

    private List<GrantedAuthority> getUserAuthority(Role role)
    {
//        final String MTAG= TAG + " getUserAuthority --> ";
        Set<GrantedAuthority> roles = new HashSet<>();
        roles.add(new SimpleGrantedAuthority(role.getAuthority()));

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(roles);
        //logger.info( MTAG + " ApiUser tiene " + grantedAuthorities.size() + " autoridades, la primera es:  " + grantedAuthorities.get(0).getAuthority());

        return grantedAuthorities;
    }
    private UserDetails buildUserForAuthentication(ApiUser user, List<GrantedAuthority> authorities) {
        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
