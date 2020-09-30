package de.tforneberg.patchdb.security;

import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true, securedEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsFromDBService userDetailsService;
	
    @Autowired
    private Environment environment;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		//Allow CORS from the front-end domain
		CorsConfiguration config = new CorsConfiguration();

		String allowedOrigins = environment.getProperty("cors.allowedUrls");
		if (allowedOrigins != null) {
			config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(","))); //allowed front-end URLs
		}
		config.addAllowedHeader("*");  //any header
		config.addAllowedMethod("*");  //any method

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		configureSession(http);
	} 
	
    private void configureSession(HttpSecurity http) throws Exception {
        http
        	.httpBasic()
    	.and()
			.rememberMe().rememberMeParameter("remember").tokenValiditySeconds(2629746) //one month 
        .and()
	        .authorizeRequests()
				//.antMatchers("/", "/js/**", "/css/**", "/img/**", "/favicon.ico", "/index.html").permitAll() //everyone should be able to retrieve frontend
	        	.antMatchers("/api/users/register").permitAll() //everyone should be able to register
	        	.antMatchers("/api/patches").permitAll() //everyone should be able to get patches list
				.antMatchers("/api/**").authenticated() //everything else from API requires authentication
		.and()
			//Configure CSRF prevention/security, tell Spring to send XSRF-Token in Cookie
			//(Clients can obtain the XSRF-Token by calling e.g. GET "/")
			.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		.and()
			//Add the CORS filter applied in corsConfigurationSource()
			.cors()
		.and()
			.logout() //Configure logout behavior
				.logoutUrl("/api/logout") //specify logout URL
				.logoutSuccessHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK)); //do not auto redirect after logout
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
        	//Tell auth to use the custom userDetailService that gets the user data from DB
        	.userDetailsService(userDetailsService)
        	//Tell auth to use BCryptPasswordEncoder 
        	.passwordEncoder(passwordEncoder());
	}
} 
