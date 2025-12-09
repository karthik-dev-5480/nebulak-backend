package com.nebulak.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
public class AppConfig {
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http
		  .sessionManagement(sessionMgmt -> sessionMgmt
		  .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		  .authorizeHttpRequests(authz -> authz
	      .requestMatchers("/api/**").authenticated()  
	      .anyRequest().permitAll())
		  .addFilterBefore(new JwtValidator() , BasicAuthenticationFilter.class )
		  .csrf(csrf -> csrf.disable())
		  .cors(cors->cors.configurationSource(corsConfigurationSource()))
		  .httpBasic(Customizer.withDefaults())
		  .formLogin(Customizer.withDefaults());          
		return http.build();
	}
	@Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "https://nebulak-frontend-git-main-karthik-dev-5480s-projects.vercel.app" // ADD THIS LINE
            )); // allow all origins
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*")); 
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
