package com.security.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

@EnableWebSecurity
public class DefaultSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().and().authorizeRequests().antMatchers("/**/articles/**").authenticated()
                        .antMatchers("/**").permitAll()
                        .and().formLogin().loginPage("/login").successHandler(new SavedRequestAwareAuthenticationSuccessHandler()).and().oauth2ResourceServer().jwt();
        return http.build();
    }

    @Bean
    UserDetailsService users() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("password")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.mvcMatcher("/articles/**")
                .authorizeRequests()
                .mvcMatchers("/articles/**")
                .access("hasAuthority('SCOPE_articles.read')")
                .and()
                .oauth2ResourceServer()
                .jwt();
        return http.build();
    }

}