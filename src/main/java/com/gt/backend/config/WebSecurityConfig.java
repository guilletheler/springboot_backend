package com.gt.backend.config;

import com.gt.backend.components.JwtAuthenticationEntryPoint;
import com.gt.backend.components.JwtRequestFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String[] AUTH_WHITELIST = { "/users/authenticate", "/v3/api-docs", "/v3/api-docs/**",
			"/swagger-resources", "/swagger-resources/**", "/configuration/ui", "/configuration/security",
			"/swagger-ui.html", "/swagger-ui/**", "/webjars/**" };

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Autowired
	private UserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	private PasswordEncoder passwordEncoder;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// configure AuthenticationManager so that it knows from where to load
		// user for matching credentials
		// Use BCryptPasswordEncoder
		auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
	}

	public PasswordEncoder passwordEncoder() {
		if(passwordEncoder == null) {
			passwordEncoder = new BCryptPasswordEncoder();
		}
		return passwordEncoder;
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {

		// We don't need CSRF for this example
		// .csrf().disable()
		httpSecurity.cors().and().csrf().disable();

		// httpSecurity.authorizeRequests().requestMatchers(CorsUtils::isPreFlightRequest).permitAll();

		httpSecurity.authorizeRequests().antMatchers(AUTH_WHITELIST).permitAll()
				// all other requests need to be authenticated
				.anyRequest().authenticated().and().
				// make sure we use stateless session; session won't be used to
				// store user's state.
				exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		// Add a filter to validate the tokens with every request
		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
	}
	
	// // To enable CORS
	// @Bean
	// public CorsConfigurationSource corsConfigurationSource() {
	// final CorsConfiguration configuration = new CorsConfiguration();

	// configuration.setAllowedOrigins(
	// Arrays.asList(new String[] { "http://localhost", "http://localhost:4200",
	// "http://localhost:8080" })); // www
	// // -
	// // obligatory
	// // configuration.setAllowedOrigins(ImmutableList.of("*")); //set access from
	// all
	// // domains
	// configuration.setAllowedMethods(
	// Arrays.asList(new String[] { "GET", "POST", "PUT", "DELETE", "OPTIONS",
	// "HEADERS" }));
	// configuration.setAllowCredentials(true);
	// configuration
	// .setAllowedHeaders(Arrays.asList(new String[] { "Authorization",
	// "Cache-Control", "Content-Type" }));

	// final UrlBasedCorsConfigurationSource source = new
	// UrlBasedCorsConfigurationSource();
	// source.registerCorsConfiguration("/**", configuration);

	// return source;
	// }

}
