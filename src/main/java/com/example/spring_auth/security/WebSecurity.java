package com.example.spring_auth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
//public class WebSecurity extends WebSecurityConfigurer {
public class WebSecurity {

    @Autowired
    JwtRequestFilter jwtRequestFilter;

    // Bean to encode user password
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configure authentication manager
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Configure DAOAuthentication provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        try {
            authProvider.setUserDetailsService(userDetailsService());
            authProvider.setPasswordEncoder(passwordEncoder());
        } catch (Exception ex) {
            System.out.println("Exception caught from DAO");
        }
        return authProvider;
    }

    @Bean
    UserDetailsService userDetailsService() throws UsernameNotFoundException {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                System.out.println("User details incoming username = " + username);
                Map<String, String> map = new HashMap<>();
                map.put("martin", passwordEncoder().encode("123"));
                if (map.containsKey(username)) {
                    System.out.println("user found!");
                    // Return User
                    User user = new User(username, map.get(username), new ArrayList<>());
                    System.out.println(user);
                    return user;
                }
                // user not found? throw exception
                System.out.println("User not found!");
                throw new UsernameNotFoundException(username);
            }
        };
    }


    // Configure spring security filter chain
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.ignoringRequestMatchers("/login")).authorizeHttpRequests(auth -> auth
                // Specify public endpoints here
                .requestMatchers("/hello").permitAll().requestMatchers("/login").permitAll()

                // authenticate all endpoints
                .anyRequest().authenticated()).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
