package com.axisbank.transit.authentication.config;

import com.axisbank.transit.authentication.filters.AuthenticationFilter;
import com.axisbank.transit.authentication.filters.CustomJwtAuthorizationFilter;
import com.axisbank.transit.core.shared.constants.ApiConstants;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static com.axisbank.transit.core.shared.constants.ApiConstants.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter{
	private static final String[] AUTH_WHITELIST = {

			// -- swagger uificat
			"/swagger-resources/**",
			"/swagger-ui.html",
			"/v2/api-docs",
			"/webjars/**",
			// Actuator APIs
			"/actuator/**",
			// H2 console
			"/h2-console/**",
			// Auth URLs
			ApiConstants.BASE_URI+"/register",
			ApiConstants.BASE_URI+"/refresh-token",
			ApiConstants.BASE_URI+"/refresh-token/mpin",
			ApiConstants.BASE_URI+"/get-otp",
			ApiConstants.BASE_URI+ ApiConstants.PAYMENT_URI + ApiConstants.PAYMENT_RESPONSE_HANDLER,
			ApiConstants.BASE_URI+"/delete/**",
			BASE_URI+FILE+"/**",
			ApiConstants.BASE_URI+"/otk",
			BASE_URI+APP_CONFIG
	};

	@Value("${app.cors.origins}")
	private String origins;

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception
	{
		return super.authenticationManagerBean();
	}
	@Bean
	public AuthenticationEntryPoint authEntryPoint(){
		return new AuthEntryPoint();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedMethods(Arrays.asList("GET","POST"));
		configuration.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type"));
		configuration.addAllowedHeader("*");
		configuration.setAllowCredentials(true);
		List<String> originList = Arrays.asList(origins.split(","));
		configuration.setAllowedOrigins(originList);
		configuration.setExposedHeaders(Arrays.asList("Authorization", "content-type"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
		.headers().frameOptions().sameOrigin()
		.and().cors().and()
		.authorizeRequests()
		.antMatchers(AUTH_WHITELIST).permitAll()
		.anyRequest().authenticated()
		.and()
		.addFilter(getAuthenticationFilter())
		.addFilter(new CustomJwtAuthorizationFilter(authenticationManager()))
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.exceptionHandling().authenticationEntryPoint(authEntryPoint());
	}


	public AuthenticationFilter getAuthenticationFilter() throws Exception {
		final AuthenticationFilter filter = new AuthenticationFilter();
		filter.setFilterProcessesUrl(BASE_URI+"/authenticate");
		return filter;
	}
}
