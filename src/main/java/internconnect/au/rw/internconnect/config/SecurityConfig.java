package internconnect.au.rw.internconnect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import internconnect.au.rw.internconnect.security.JwtAuthFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
			http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
					.requestMatchers("/api/auth/**").permitAll()
					.anyRequest().authenticated()
				)
				.httpBasic(b -> b.disable())
				.formLogin(f -> f.disable())
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}


