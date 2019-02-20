package de.tforneberg.patchdb.security;

import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import de.tforneberg.patchdb.security.jwt.JWTAuthenticationFilter;
import de.tforneberg.patchdb.security.jwt.JWTLoginFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true, securedEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsFromDBService userDetailsService;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		//Allow CORS from the front-end domain
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true); //needed for JWT authentication to work
		config.setAllowedOrigins(Arrays.asList("http://localhost:8081")); //allowed origins: the front-end URL
		config.addAllowedHeader("*");  //any header
		config.addAllowedMethod("*");  //any method
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//configureJWT(http);
		configureSession(http);
	} 
	
    private void configureSession(HttpSecurity http) throws Exception {
        http
        	.httpBasic()
        .and()
	        .authorizeRequests()
	        	.antMatchers("/", "/login", "/api/logout", "/api/users/register").permitAll()
	        	.antMatchers("/api/patches").permitAll()
	        	.anyRequest().authenticated()
		.and()
			//Configure CSRF prevention/security, tell Spring to send XSRF-Token in Cookie
			//(Clients can obtain the XSRF-Token by calling GET "/")
			.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		.and()
			//Add the CORS filter applied in corsConfigurationSource()
			.cors()
		.and()
			.logout() //Configure logout behavior
			.logoutUrl("/api/logout") //specify logout URL
			.logoutSuccessHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK)); //tell Spring not to do auto redirect after logout
		
	}

	private void configureJWT(HttpSecurity http) throws Exception {
		http
			//Prevent Spring from sending JSESSIONID cookie
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
			//Add the CORS filter applied in corsConfigurationSource()
			.cors()
		.and()
			//Configure CSRF prevention/security, tell Spring to send XSRF-Token in Cookie
			//Clients can obtain the XSRF-Token by calling GET "/"
			.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		.and() 
			.authorizeRequests() //Tells Spring to authorize the requests using the following matchers
				.antMatchers(HttpMethod.GET, "/").permitAll() //everyone can call GET "/"
				.antMatchers(HttpMethod.POST, "/api/login").permitAll() //everyone can attempt to login
				.antMatchers(HttpMethod.POST, "/api/logout").permitAll() //everyone can logout
				//fill in role restricted stuff here ... or declare in repositories using annotations
				.anyRequest().authenticated() //any URL that has not been matched "only" requires the user to be authenticated (role doesn't matter)
		.and()
			.logout() //Configure logout behavior
			.logoutUrl("/api/logout") //specify logout URL
			.deleteCookies("COOKIE-BEARER") //delete JWT cookie
			.logoutSuccessHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK)) //tell Spring not to do auto redirect after logout
		.and()
			//add custom JWT login filter at the very beginning of the filter stack
			.addFilterBefore(new JWTLoginFilter("/api/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
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