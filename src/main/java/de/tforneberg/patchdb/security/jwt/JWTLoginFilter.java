package de.tforneberg.patchdb.security.jwt;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.tforneberg.patchdb.model.requests.LoginRequestData;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

	public JWTLoginFilter(String url, AuthenticationManager authManager) {
		super(new AntPathRequestMatcher(url));
		setAuthenticationManager(authManager);
	}

	@Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException, IOException {

		//get LoginRequestData (name and password) from HTTP request
        LoginRequestData loginReq = new ObjectMapper().readValue(req.getInputStream(), LoginRequestData.class);
        
        //try to authenticate with the given credentials
        return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginReq.getName(),
                        loginReq.getPassword(),
                        Collections.emptyList()
                )
        );
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) {
        //if authentication was successful, create a JWT token
    	TokenAuthenticationHelper.addAuthentication(req, res, auth.getName());
    }
}
