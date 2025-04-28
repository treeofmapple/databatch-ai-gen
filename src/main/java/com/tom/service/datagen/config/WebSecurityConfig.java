package com.tom.service.datagen.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.tom.service.datagen.common.WhitelistLoader;
import com.tom.service.datagen.exception.AuthEntryPointJwt;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

	private final WhitelistLoader whitelist;
	private final AuthEntryPointJwt unauthorizedHandler;
	
	@Value("${application.security.user}")
	private String user;
	
	@Value("${application.security.password}")
	private String password;
	
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    String[] whiteListUrls = whitelist.loadWhitelist();
    	
    	http
    		.exceptionHandling(exception -> 
    			exception.authenticationEntryPoint(unauthorizedHandler))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(whiteListUrls).permitAll()
                .anyRequest().denyAll()
            )
            .httpBasic(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable());
        
		return http.build();
    }

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    	UserDetails genUser = User.builder()
                .username(user)
                .password(passwordEncoder.encode(password)) 
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(genUser);
    }
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(16);
    }
}
